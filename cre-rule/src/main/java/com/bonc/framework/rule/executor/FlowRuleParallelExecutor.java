package com.bonc.framework.rule.executor;

import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.constant.Config;
import com.bonc.framework.rule.exception.ExecuteModelException;
import com.bonc.framework.rule.executor.builder.AbstractExecutorBuilder;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.executor.context.impl.ExecutorResponse;
import com.bonc.framework.rule.executor.context.impl.ModelExecutorType;
import com.bonc.framework.rule.executor.worker.ITraversal;
import com.bonc.framework.rule.kpi.IKpiService;
import com.bonc.framework.rule.log.IRuleLog;
import com.bonc.framework.rule.log.LogState;
import com.bonc.framework.rule.log.entity.RuleLog;
import com.bonc.framework.rule.log.entity.TraceModelLogDetail;
import com.bonc.framework.rule.parse.IRuleParse;
import com.bonc.framework.rule.parse.RuleParseFactory;
import com.bonc.framework.rule.resources.RuleResource;
import com.bonc.framework.rule.resources.flow.FlowNode;
import com.bonc.framework.rule.resources.flow.FlowNodeStateEnum;
import com.bonc.framework.util.ExceptionUtil;
import com.bonc.framework.util.IdUtil;
import com.bonc.framework.util.JsonUtils;
import com.google.common.base.Splitter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author qxl
 * @version 1.0
 * @date 2018年1月8日 下午3:32:20
 */
public class FlowRuleParallelExecutor extends AbstractExecutor {
    private Log log = LogFactory.getLog(getClass());
    Logger logger = LogManager.getLogger(FlowRuleParallelExecutor.class);

    private IRuleLog ruleLog;

    private IKpiService kpiService;


    public FlowRuleParallelExecutor(AbstractExecutorBuilder builder) {
        super(builder);
        ruleLog = builder.getRuleLog();
        kpiService = builder.getKpiSetvice();
    }


    @Override
    public void executor(RuleResource ruleResource, Map<String, Object> param, ConsumerInfo... consumerInfo) throws ExecuteModelException {
        if (ruleResource == null) {
            throw new ExecuteModelException("编译后的模型为空，模型执行失败");
        }
        this.executor(ruleResource, param, ruleResource.isLog(), consumerInfo);
    }

    //遍历执行FlowNode流程
    @SuppressWarnings("rawtypes")
    @Deprecated
    private void executor(RuleResource ruleResource, Map<String, Object> param, boolean isLog, ConsumerInfo... consumerInfo) throws ExecuteModelException {
        FlowNode flowNode = null;
        try {
            IRuleParse parse = RuleParseFactory.getRuleParseByRuleType(ruleResource.getRuleType());
            flowNode = parse.parse(ruleResource.getFolderId(), ruleResource.getRuleId(), ruleResource.getRuleType(),
                    ruleResource.getRuleName(), ruleResource.getRuleContent(), ruleResource.isLog())
                    .getFlowNode();
        } catch (Exception e) {
            throw new ExecuteModelException(e);
        }

        if (flowNode == null) {
            throw new NullPointerException("The flowNode is null.");
        }

        long start = System.currentTimeMillis();

        //规则执行日志
        RuleLog ruleLogEntity = new RuleLog();
        String logId = IdUtil.getUUID();
        ruleLogEntity.setLogId(logId);
        ruleLogEntity.setRuleId(ruleResource.getRuleId());
//        ruleLogEntity.setRuleName(ruleResource.getRuleName());
        ruleLogEntity.setFolderId(ruleResource.getFolderId());
        ruleLogEntity.setStartTime(new Date());
        ruleLogEntity.setInputData(JsonUtils.collectToString(param));
        ruleLogEntity.setHitRuleNum("0");
        // 增加渠道号
        if (consumerInfo != null && consumerInfo.length == 1 && consumerInfo[0] != null) {
            ruleLogEntity.setConsumerId(consumerInfo[0].getConsumerId());
            ruleLogEntity.setServerId(consumerInfo[0].getServerId());
            ruleLogEntity.setConsumerSeqNo(consumerInfo[0].getConsumerSeqNo());
        }
        int hitRuleNum = 0;
        //
        try {
            // loaderKpi
//            kpiService.loadKpiVar(ruleResource.getFolderId(), ruleResource.getRuleId(), param, consumerInfo);

            ITraversal iTraversal = this.builder.getTraversal(ruleResource);//获取遍历器
            iTraversal.setHeadNode(flowNode);
            FlowNode curNode = flowNode;
//            int num = 0;
            while (true) {
                // 执行
                ExecutorNode executorNode = null;
                if (curNode.getNodeState().equals(FlowNodeStateEnum.TRUE)) {
                    if (iTraversal.canParallel()) {
                        final List<FlowNode> flowNodes = iTraversal.pollNodes();
                        executorNode = new ParallelExecutorNode(flowNodes);
                        curNode = flowNodes.get(0);
                    } else {
                        curNode = iTraversal.nextNode();
                        executorNode = new SerialExecutorNode(curNode);
                    }
                } else if (curNode.getNodeState().equals(FlowNodeStateEnum.FALSE)) {
                    if (iTraversal.canParallel()) {
                        final List<FlowNode> flowNodes = iTraversal.pollNodes();
                        executorNode = new ParallelExecutorNode(flowNodes);
                    } else {
                        curNode = iTraversal.nextBroNode();
                        executorNode = new SerialExecutorNode(curNode);
                    }
                } else if (curNode == null || curNode.getNodeState().equals(FlowNodeStateEnum.END)) {
                    iTraversal.end();
                    break;
                }
                // 执行
                int curHitRuleNum = executorNode.executor(param, true, builder, logId, ruleLog, consumerInfo);
                hitRuleNum += curHitRuleNum;
            }
            ruleLogEntity.setState(LogState.STATE_END);
            ruleLogEntity.setHitRuleNum(String.valueOf(hitRuleNum));
            ruleLogEntity.setOutputData(JsonUtils.collectToString(param));
            if (log.isDebugEnabled()) {
                log.debug(String.format("输出变量：[%s]", param));
            }
        } catch (Exception e) {
            ruleLogEntity.setState(LogState.STATE_EXCEPTION);
            String exception = ExceptionUtil.getStackTrace(e);
            ruleLogEntity.setException(exception);
            throw new ExecuteModelException("执行模型失败", e);
        } finally {
            ruleLogEntity.setEndTime(new Date());
            ruleLog.recordRuleLog(ruleLogEntity, isLog);
            if (log.isTraceEnabled()) {
                log.trace(ruleLogEntity);
            }
            // TODO: 记录AB测试 B模型日志
            Level verbose = Level.getLevel("VERBOSE");
            if (logger.isEnabled(verbose)) {
                logger.log(verbose, ruleLogEntity);
            }
            log.info("End to execute the rule, rule: " + ruleResource.info() +
                    " time used: " + (System.currentTimeMillis() - start) + "ms.");
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public ExecutorResponse executor(ExecutorRequest executorRequest) throws ExecuteModelException {
        RuleResource ruleResource = executorRequest.getExecutable();
        ConsumerInfo[] consumerInfo = executorRequest.getConsumerInfos();
        Map<String, Object> param = executorRequest.getParam();

        boolean isLog = executorRequest.isLog();
        List<String> list = Config.CRE_LOG_RULE_NONPERSIST_LIST;
        String code = executorRequest.getModelExecutionType().getCode();
        if (!list.isEmpty() && list.contains(code)) {
            isLog = false;
            executorRequest.setLog(isLog);
        }

        boolean isTraceDetail = executorRequest.isTraceDetail();

        if (ruleResource == null) {
            log.error("ruleResource为null，编译后的模型为null，模型执行失败", new ExecuteModelException("编译后的模型为空，模型执行失败"));
            return ExecutorResponse.newFailedExecutorResponse("模型未编译。");
        }

        FlowNode flowNode = ruleResource.getFlowNode();
        if (flowNode == null) {
            log.error("模型执行失败,模型图为空", new NullPointerException("模型执行失败,模型图为空"));
            return ExecutorResponse.newFailedExecutorResponse("模型执行失败,模型图为空");
        }

        long start = System.currentTimeMillis();

        //规则执行日志
        RuleLog ruleLogEntity = new RuleLog();
        String logId = IdUtil.getUUID();
        ruleLogEntity.setLogId(logId);
        executorRequest.setRuleLogId(logId);

        ruleLogEntity.setRuleId(ruleResource.getRuleId());
        ruleLogEntity.setFolderId(ruleResource.getFolderId());
        ruleLogEntity.setSourceModelId(executorRequest.getSourceModelId());
        ruleLogEntity.setExecutionType(code);
        if (executorRequest.getModelExecutionType() == ModelExecutorType.ABTEST) {
            ruleLogEntity.setSourceLogId(executorRequest.getSourceLogId());
            ruleLogEntity.setExecutionInfo(executorRequest.getAbTestId());
        }
        ruleLogEntity.setStartTime(new Date());
        ruleLogEntity.setInputData(JsonUtils.collectToString(param));
        ruleLogEntity.setHitRuleNum("0");
        // 增加渠道号
        if (consumerInfo != null && consumerInfo.length == 1 && consumerInfo[0] != null) {
            ruleLogEntity.setConsumerId(consumerInfo[0].getConsumerId());
            ruleLogEntity.setServerId(consumerInfo[0].getServerId());
            ruleLogEntity.setConsumerSeqNo(consumerInfo[0].getConsumerSeqNo());
        }

        int hitRuleNum = 0;

        try {

            ExecutorResponse executorResponse = new ExecutorResponse();
            executorResponse.setExecutorLogId(logId);
            TraceModelLogDetail traceModelLogDetail = null;
            if (isTraceDetail) {
                traceModelLogDetail = new TraceModelLogDetail(false);

                traceModelLogDetail.setRuleLog(ruleLogEntity);
                executorResponse.setExecutorDetail(traceModelLogDetail);
            }

            //设置开始节点的节点状态为true
            flowNode.setNodeState(FlowNodeStateEnum.TRUE);

            ITraversal iTraversal = this.builder.getTraversal(ruleResource);//获取遍历器
            iTraversal.setHeadNode(flowNode);
            FlowNode curNode = flowNode;

            while (true) {
                // 执行
                ExecutorNode executorNode = null;
                if (curNode.getNodeState().equals(FlowNodeStateEnum.TRUE)) {
                    if (iTraversal.canParallel()) {
                        final List<FlowNode> flowNodes = iTraversal.pollNodes();
                        executorNode = new ParallelExecutorNode(flowNodes);
                        curNode = flowNodes.get(0);
                    } else {
                        curNode = iTraversal.nextNode();
                        executorNode = new SerialExecutorNode(curNode);
                    }
                } else if (curNode.getNodeState().equals(FlowNodeStateEnum.FALSE)) {
                    if (iTraversal.canParallel()) {
                        final List<FlowNode> flowNodes = iTraversal.pollNodes();
                        executorNode = new ParallelExecutorNode(flowNodes);
                    } else {
                        curNode = iTraversal.nextBroNode();
                        executorNode = new SerialExecutorNode(curNode);
                    }
                } else if (curNode == null || curNode.getNodeState().equals(FlowNodeStateEnum.END)) {
                    iTraversal.end();
                    break;
                }
                // 执行
                if (executorNode != null) {
                    int curHitRuleNum = executorNode.executor(builder,
                            executorRequest, executorResponse);
                    hitRuleNum += curHitRuleNum;
                }
            }
            ruleLogEntity.setState(LogState.STATE_END);
            ruleLogEntity.setHitRuleNum(String.valueOf(hitRuleNum));
            ruleLogEntity.setOutputData(JsonUtils.collectToString(param));
            if (log.isDebugEnabled()) {
                log.debug(String.format("输出变量：[%s]", param));
            }

            //  处理结果,返回
            long timeUsed = System.currentTimeMillis() - start;
            log.info("End to execute the rule, rule: " + ruleResource.info() +
                    " time used: " + timeUsed + "ms.");

            executorResponse.setResult(param);
            executorResponse.setSuccessed(true);
            executorResponse.setUsedTime(timeUsed);
            return executorResponse;
        } catch (Exception e) {
            log.error("模型执行异常:" + e.getMessage(), e);
            ruleLogEntity.setState(LogState.STATE_EXCEPTION);
            String exception = ExceptionUtil.getStackTrace(e);
            ruleLogEntity.setException(exception);
            return ExecutorResponse.newFailedExecutorResponse("执行模型失败。模型执行异常" + e.getMessage());
        } finally {
            //  日志入库
            ruleLogEntity.setEndTime(new Date());
            // 记录AB测试 B模型日志
            if (executorRequest.getModelExecutionType() == ModelExecutorType.ABTEST) {
                Level verbose = Level.getLevel("VERBOSE");
                if (logger.isEnabled(verbose)) {
                    logger.log(verbose, ruleLogEntity);
                }
            } else {
                ruleLog.recordRuleLog(ruleLogEntity, isLog);
                if (log.isTraceEnabled()) {
                    log.trace(ruleLogEntity);
                }
            }
            // 释放ThreadLocal
//            flowNode.removeAllThreadLocal();
        }
    }

}
