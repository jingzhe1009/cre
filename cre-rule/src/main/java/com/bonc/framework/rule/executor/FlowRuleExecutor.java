package com.bonc.framework.rule.executor;

import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.RuleEngineFactory;
import com.bonc.framework.rule.exception.ExecuteModelException;
import com.bonc.framework.rule.executor.builder.AbstractExecutorBuilder;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.executor.context.impl.ExecutorResponse;
import com.bonc.framework.rule.executor.entity.kpi.KpiDefinition;
import com.bonc.framework.rule.executor.resolver.IResolver;
import com.bonc.framework.rule.executor.worker.ITraversal;
import com.bonc.framework.rule.executor.worker.ITraversalStrategy;
import com.bonc.framework.rule.executor.worker.TraversalStrategyFactory;
import com.bonc.framework.rule.kpi.IKpiService;
import com.bonc.framework.rule.kpi.KpiConstant;
import com.bonc.framework.rule.log.IRuleLog;
import com.bonc.framework.rule.log.LogState;
import com.bonc.framework.rule.log.entity.RuleLog;
import com.bonc.framework.rule.log.entity.RuleLogDetail;
import com.bonc.framework.rule.log.entity.TraceModelLogDetail;
import com.bonc.framework.rule.resources.RuleResource;
import com.bonc.framework.rule.resources.flow.FlowNode;
import com.bonc.framework.rule.resources.flow.basicflow.AbstractFlowNode;
import com.bonc.framework.util.ExceptionUtil;
import com.bonc.framework.util.IdUtil;
import com.bonc.framework.util.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qxl
 * @version 1.0
 * @date 2018年1月8日 下午3:32:20
 */
public class FlowRuleExecutor extends AbstractExecutor {
    private Log log = LogFactory.getLog(getClass());

    private IRuleLog ruleLog;

    private IKpiService kpiService;

    public FlowRuleExecutor(AbstractExecutorBuilder builder) {
        super(builder);
        ruleLog = builder.getRuleLog();
        kpiService = builder.getKpiSetvice();
    }

    @Override
    @Deprecated
    public void executor(RuleResource ruleResource, Map<String, Object> param, ConsumerInfo... consumerInfo) throws ExecuteModelException {
        if (ruleResource == null) {
            throw new ExecuteModelException("The ruleResource is null.");
        }
//		if(!(ruleResource instanceof FlowRuleResource)){
//			throw new ExecuteModelException("The ruleResource type is not equals the executor type.");
//		}
        this.executor(ruleResource, param, ruleResource.isLog(), consumerInfo);
    }

    //遍历执行FlowNode流程
    @SuppressWarnings("rawtypes")
    @Deprecated
    private void executor(RuleResource ruleResource, Map<String, Object> param, boolean isLog, ConsumerInfo... consumerInfo) throws ExecuteModelException {
        FlowNode flowNode = ruleResource.getFlowNode();
        if (flowNode == null) {
            throw new NullPointerException("The flowNode is null.");
        }
        Map<String, Object> inputMap = new HashMap<String, Object>();
        inputMap.putAll(param);

        //规则执行日志
        RuleLog ruleLogEntity = new RuleLog();
        String logId = IdUtil.getUUID();
        ruleLogEntity.setLogId(logId);
        ruleLogEntity.setRuleId(ruleResource.getRuleId());
        ruleLogEntity.setFolderId(ruleResource.getFolderId());
        ruleLogEntity.setStartTime(new Date());
        ruleLogEntity.setInputData(JsonUtils.collectToString(inputMap));
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
            int num = 0;
            while (true) {
                ITraversalStrategy traversalStrategy = TraversalStrategyFactory.getTraversalStrategy(curNode.getNodeState());
                curNode = traversalStrategy.getNode(iTraversal);    // 根据节点状态进行遍历
                if (curNode == null) {
                    break;
                }
                log.info(curNode);

                //记录节点执行日志
                RuleLogDetail ruleLogDetail = new RuleLogDetail();
                ruleLogDetail.setId(logId + "_" + (++num) + ((AbstractFlowNode) curNode).getNodeId());
                ruleLogDetail.setLogId(logId);
                ruleLogDetail.setNodeId(curNode.getNodeId());
                ruleLogDetail.setNodeName(((AbstractFlowNode) curNode).getNodeName());
                ruleLogDetail.setNodeType(((AbstractFlowNode) curNode).getNodeType());
                ruleLogDetail.setStartTime(new Date());
                // 增加渠道号
                if (consumerInfo != null && consumerInfo.length == 1 && consumerInfo[0] != null) {
                    ruleLogDetail.setConsumerId(consumerInfo[0].getConsumerId());
                    ruleLogDetail.setServerId(consumerInfo[0].getServerId());
                    ruleLogDetail.setConsumerSeqNo(consumerInfo[0].getConsumerSeqNo());
                }

                IResolver resolver = builder.getResolverByFlowNode(curNode.getClass());//获取执行器
                if (resolver == null) {
                    throw new ExecuteModelException("The IResolver is null.");
                }

                Object result = null;
                try {
                    result = resolver.resolver(curNode, param, consumerInfo);
                    if (result != null && result instanceof List && !((List) result).isEmpty()) {
                        int size = ((List) result).size();
                        hitRuleNum += size;
                    }
                    ruleLogDetail.setState(LogState.STATE_END);
                    ruleLogDetail.setResult(JsonUtils.toJSONString(result));
                } catch (Exception e) {
                    ruleLogDetail.setState(LogState.STATE_EXCEPTION);
                    String exception = ExceptionUtil.getStackTrace(e);
                    /*if (exception != null && exception.length() > 3000) {
                        exception = exception.substring(0, 3000);
                    }*/
                    ruleLogDetail.setResult(exception);
                    throw e;
                } finally {
                    ruleLogDetail.setEndTime(new Date());
                    ruleLog.recordRuleDetailLog(ruleLogDetail, isLog);
                    if (log.isTraceEnabled()) {
                        log.trace(ruleLogDetail);
                    }
                }
            }
            ruleLogEntity.setState(LogState.STATE_END);
            ruleLogEntity.setHitRuleNum(String.valueOf(hitRuleNum));
            ruleLogEntity.setOutputData(JsonUtils.collectToString(param));
        } catch (Exception e) {
            log.error(e);
            ruleLogEntity.setState(LogState.STATE_EXCEPTION);
            String exception = ExceptionUtil.getStackTrace(e);
            /*if (exception != null && exception.length() > 3000) {
                exception = exception.substring(0, 3000);
            }*/
            ruleLogEntity.setException(exception);
            throw new ExecuteModelException(e);
        } finally {
            ruleLogEntity.setEndTime(new Date());
            ruleLog.recordRuleLog(ruleLogEntity, isLog);
            if (log.isTraceEnabled()) {
                log.trace(ruleLogEntity);
            }
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public ExecutorResponse executor(ExecutorRequest executorRequest) throws ExecuteModelException {
        RuleResource ruleResource = executorRequest.getExecutable();
        ConsumerInfo[] consumerInfo = executorRequest.getConsumerInfos();
        Map<String, Object> param = executorRequest.getParam();
        boolean isLog = executorRequest.isLog();
        boolean isTraceDetail = executorRequest.isTraceDetail();

        if (ruleResource == null) {
//            throw new ExecuteModelException("The ruleResource is null.");
            log.error("ruleResource为null，编译后的模型为null，模型执行失败", new ExecuteModelException("ruleResource为null，编译后的模型为null，模型执行失败"));
            return ExecutorResponse.newFailedExecutorResponse("模型未编译。");
        }

        FlowNode flowNode = ruleResource.getFlowNode();
        if (flowNode == null) {
//            throw new NullPointerException("The flowNode is null.");
            log.error("模型执行失败,模型图为空");
            return ExecutorResponse.newFailedExecutorResponse("模型执行失败,模型图为空");
        }
        Map<String, Object> inputMap = new HashMap<String, Object>();
        inputMap.putAll(param);

        long start = System.currentTimeMillis();

        //规则执行日志
        RuleLog ruleLogEntity = new RuleLog();
        String logId = IdUtil.getUUID();
        ruleLogEntity.setLogId(logId);
        ruleLogEntity.setRuleId(ruleResource.getRuleId());
        ruleLogEntity.setFolderId(ruleResource.getFolderId());
        ruleLogEntity.setStartTime(new Date());
        ruleLogEntity.setInputData(JsonUtils.collectToString(inputMap));
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

            ExecutorResponse executorResponse = new ExecutorResponse();
            executorResponse.setExecutorLogId(logId);
            TraceModelLogDetail traceModelLogDetail = null;
            if (isTraceDetail) {
                traceModelLogDetail = new TraceModelLogDetail(false);

                traceModelLogDetail.setRuleLog(ruleLogEntity);
                executorResponse.setExecutorDetail(traceModelLogDetail);
            }
            //loaderKpi
            try {
                if (executorRequest.isLoadKpi() && KpiConstant.LOADER_KPI_STRATEGY_TYPE_COMPILE.equals(executorRequest.getLoaderKpiStrategyType())) {
                    List<KpiDefinition> kpiDefinitions = RuleEngineFactory.getRuleEngine().getConContext().queryKpiDefinitions(executorRequest.getRuleId());
                    kpiService.loadKpiVarByKpiDefinitionList(kpiDefinitions, param, executorRequest);
//                    kpiService.loadKpiVar(executorRequest.getFolderId(), executorRequest.getRuleId(), param, executorRequest);
                }
            } catch (Exception e) {
                log.error("模型编译指标失败", new ExecuteModelException(e));
                return ExecutorResponse.newFailedExecutorResponse("模型编译指标失败" + e.getMessage());
            }

            ITraversal iTraversal = this.builder.getTraversal(ruleResource);//获取遍历器
            iTraversal.setHeadNode(flowNode);
            FlowNode curNode = flowNode;
            int num = 0;
            while (true) {
                ITraversalStrategy traversalStrategy = TraversalStrategyFactory.getTraversalStrategy(curNode.getNodeState());
                curNode = traversalStrategy.getNode(iTraversal);    // 根据节点状态进行遍历
                if (curNode == null) {
                    break;
                }
                log.info(curNode);

                //记录节点执行日志
                RuleLogDetail ruleLogDetail = new RuleLogDetail();
                ruleLogDetail.setId(logId + "_" + (++num) + ((AbstractFlowNode) curNode).getNodeId());
                ruleLogDetail.setLogId(logId);
                ruleLogDetail.setNodeId(curNode.getNodeId());
                ruleLogDetail.setNodeName(((AbstractFlowNode) curNode).getNodeName());
                ruleLogDetail.setNodeType(((AbstractFlowNode) curNode).getNodeType());
                ruleLogDetail.setStartTime(new Date());
                // 增加渠道号
                if (consumerInfo != null && consumerInfo.length == 1 && consumerInfo[0] != null) {
                    ruleLogDetail.setConsumerId(consumerInfo[0].getConsumerId());
                    ruleLogDetail.setServerId(consumerInfo[0].getServerId());
                    ruleLogDetail.setConsumerSeqNo(consumerInfo[0].getConsumerSeqNo());
                }

                IResolver resolver = builder.getResolverByFlowNode(curNode.getClass());//获取执行器
                if (resolver == null) {
                    throw new ExecuteModelException("The IResolver is null.");
                }

                Object result = null;
                try {
//                    result = resolver.resolver(curNode, param, consumerInfo);
                    result = resolver.resolver(curNode, executorRequest, ruleLogDetail);
                    if (result != null && result instanceof List && !((List) result).isEmpty()) {
                        int size = ((List) result).size();
                        hitRuleNum += size;
                    }
                    ruleLogDetail.setState(LogState.STATE_END);
                    ruleLogDetail.setResult(JsonUtils.toJSONString(result));
                } catch (Exception e) {
                    ruleLogDetail.setState(LogState.STATE_EXCEPTION);
                    String exception = ExceptionUtil.getStackTrace(e);
                    /*if (exception != null && exception.length() > 3000) {
                        exception = exception.substring(0, 3000);
                    }*/
                    ruleLogDetail.setResult(exception);
                    throw e;
                } finally {
                    ruleLogDetail.setEndTime(new Date());
                    ruleLog.recordRuleDetailLog(ruleLogDetail, isLog);
                    if (log.isTraceEnabled()) {
                        log.trace(ruleLogDetail);
                    }

                    if (isTraceDetail && traceModelLogDetail != null) {
                        traceModelLogDetail.addRuleLogDetail(ruleLogDetail);
                    }

                }
            }
            ruleLogEntity.setState(LogState.STATE_END);
            ruleLogEntity.setHitRuleNum(String.valueOf(hitRuleNum));
            ruleLogEntity.setOutputData(JsonUtils.collectToString(param));

            long timeUsed = System.currentTimeMillis() - start;
            log.info("End to execute the rule, rule: " + ruleResource.info() +
                    " time used: " + timeUsed + "ms.");
            executorResponse.setResult(param);
            executorResponse.setSuccessed(true);
            executorResponse.setUsedTime(timeUsed);

            return executorResponse;
        } catch (Exception e) {
            log.error("执行模型失败:" + e.getMessage(), e);
            ruleLogEntity.setState(LogState.STATE_EXCEPTION);
            String exception = ExceptionUtil.getStackTrace(e);
            /*if (exception != null && exception.length() > 3000) {
                exception = exception.substring(0, 3000);
            }*/
            ruleLogEntity.setException(exception);
            ruleLog.recordRuleLog(ruleLogEntity, isLog);
//            throw new ExecuteModelException(e);
            return ExecutorResponse.newFailedExecutorResponse("执行模型失败:" + e.getMessage());
        } finally {
            //  日志入库
            ruleLogEntity.setEndTime(new Date());
            ruleLog.recordRuleLog(ruleLogEntity, isLog);
            if (log.isTraceEnabled()) {
                log.trace(ruleLogEntity);
            }
        }
    }

}
