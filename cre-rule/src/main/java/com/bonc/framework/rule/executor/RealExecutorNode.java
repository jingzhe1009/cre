package com.bonc.framework.rule.executor;

import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.exception.ExecuteException;
import com.bonc.framework.rule.exception.ExecuteModelException;
import com.bonc.framework.rule.executor.builder.AbstractExecutorBuilder;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.executor.context.impl.ExecutorResponse;
import com.bonc.framework.rule.executor.context.impl.ModelExecutorType;
import com.bonc.framework.rule.executor.resolver.IResolver;
import com.bonc.framework.rule.log.IRuleLog;
import com.bonc.framework.rule.log.LogState;
import com.bonc.framework.rule.log.entity.RuleLogDetail;
import com.bonc.framework.rule.log.entity.TraceModelLogDetail;
import com.bonc.framework.rule.resources.flow.FlowNode;
import com.bonc.framework.rule.resources.flow.FlowNodeStateEnum;
import com.bonc.framework.rule.resources.flow.basicflow.AbstractFlowNode;
import com.bonc.framework.rule.resources.flow.basicflow.CompoundFlowNode;
import com.bonc.framework.util.ExceptionUtil;
import com.bonc.framework.util.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/4/19 16:53
 */
public class RealExecutorNode {

    private Log log = LogFactory.getLog(getClass());
    private Logger logger = LogManager.getLogger(getClass());

    public static final RealExecutorNode INSTANCE = new RealExecutorNode();

    private RealExecutorNode() {

    }

    @Deprecated
    public int executor(FlowNode node, Map<String, Object> param,
                        boolean isLog, AbstractExecutorBuilder builder,
                        String logId, IRuleLog ruleLog,
                        boolean isTraceDetail, TraceModelLogDetail traceModelLogDetail,
                        ConsumerInfo... consumerInfo) throws Exception {
        throw new UnsupportedOperationException("当前节点执行方法已废弃");
    }

    private int executorCompound(CompoundFlowNode compoundFlowNode, AbstractExecutorBuilder builder,
                                 ExecutorRequest executorRequest, ExecutorResponse executorResponse) throws Exception {
        if (compoundFlowNode == null ||
                compoundFlowNode.isEmpty()) {
            throw new IllegalArgumentException("该节点为空，无法处理");
        }
        FlowNode head = compoundFlowNode.getHead();
        FlowNode tail = compoundFlowNode.getTail();
        int hitRuleNum = 0;
        FlowNode current = head;
        for (; current != tail; current = current.getChildFlowNodes().get(0)) {
            hitRuleNum += executorSingle(current, builder, executorRequest, executorResponse);
            // 判断是否继续执行
            if (!FlowNodeStateEnum.TRUE.equals(current.getNodeState())) {
                // 更新复合节点状态
                compoundFlowNode.setNodeState(current.getNodeState());
                return hitRuleNum;
            }

        }

        hitRuleNum += executorSingle(current, builder, executorRequest, executorResponse);
        // 更新复合节点状态
        compoundFlowNode.setNodeState(current.getNodeState());
        return hitRuleNum;
    }

    public int executor(FlowNode node, AbstractExecutorBuilder builder,
                        ExecutorRequest executorRequest, ExecutorResponse executorResponse) throws Exception {
        if (node instanceof CompoundFlowNode) {
            return executorCompound((CompoundFlowNode) node, builder, executorRequest, executorResponse);
        }

        return executorSingle(node, builder, executorRequest, executorResponse);
    }


    private int executorSingle(FlowNode node, AbstractExecutorBuilder builder,
                               ExecutorRequest executorRequest, ExecutorResponse executorResponse) throws Exception {
        int hitRuleNum = 0;
        if (executorRequest == null) {
            throw new IllegalArgumentException("传入的参数[executorRequest]为null");
        }
        if (executorResponse == null) {
            throw new IllegalArgumentException("传入的参数[executorResponse]为null");
        }
        boolean isLog = executorRequest.isLog();
        boolean isTraceDetail = executorRequest.isTraceDetail();
        ConsumerInfo[] consumerInfo = executorRequest.getConsumerInfos();

        String logId = executorResponse.getExecutorLogId();
        TraceModelLogDetail traceModelLogDetail = executorResponse.getExecutorDetail();

        //记录节点执行日志
        RuleLogDetail ruleLogDetail = new RuleLogDetail();
        ruleLogDetail.setId(logId + "_" + node.getNodeId());
        ruleLogDetail.setLogId(logId);
        ruleLogDetail.setNodeId(node.getNodeId());
        ruleLogDetail.setNodeName(((AbstractFlowNode) node).getNodeName());
        ruleLogDetail.setNodeType(((AbstractFlowNode) node).getNodeType());
        ruleLogDetail.setStartTime(new Date());
        // 增加渠道号
        if (consumerInfo != null && consumerInfo.length == 1 && consumerInfo[0] != null) {
            ruleLogDetail.setConsumerId(consumerInfo[0].getConsumerId());
            ruleLogDetail.setServerId(consumerInfo[0].getServerId());
            ruleLogDetail.setConsumerSeqNo(consumerInfo[0].getConsumerSeqNo());
        }

        IResolver resolver = builder.getResolverByFlowNode(node.getClass());//获取执行器
        if (resolver == null) {
            throw new ExecuteModelException("The IResolver is null.");
        }

        Object result = null;
        try {
            result = resolver.resolver(node, executorRequest, ruleLogDetail);
            if (result != null && result instanceof List && (!((List) result).isEmpty())) {
                int size = ((List) result).size();
                hitRuleNum += size;
            }
            ruleLogDetail.setState(LogState.STATE_END);
            ruleLogDetail.setResult(JsonUtils.toJSONString(result));
        } catch (Exception e) {
            ruleLogDetail.setState(LogState.STATE_EXCEPTION);
            String exception = ExceptionUtil.getStackTrace(e);
            if (exception != null && exception.length() > 3000) {
                exception = exception.substring(0, 3000);
            }
            ruleLogDetail.setResult(exception);
            throw e;
        } finally {
            ruleLogDetail.setEndTime(new Date());
            if (executorRequest.getModelExecutionType() == ModelExecutorType.ABTEST) {
                Level verbose = Level.getLevel("VERBOSE");
                if (logger.isEnabled(verbose)) {
                    logger.log(verbose, ruleLogDetail);
                }
            } else {
                builder.getRuleLog().recordRuleDetailLog(ruleLogDetail, isLog);
                if (this.log.isTraceEnabled()) {
                    this.log.trace(ruleLogDetail);
                }
                if (isTraceDetail && traceModelLogDetail != null) {
                    traceModelLogDetail.addRuleLogDetail(ruleLogDetail);
                }
            }
        }
        return hitRuleNum;
    }
}
