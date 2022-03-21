package com.bonc.framework.rule.executor;

import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.executor.builder.AbstractExecutorBuilder;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.executor.context.impl.ExecutorResponse;
import com.bonc.framework.rule.log.IRuleLog;
import com.bonc.framework.rule.log.entity.TraceModelLogDetail;
import com.bonc.framework.rule.resources.flow.FlowNode;

import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/4/19 17:18
 */
public class SerialExecutorNode implements ExecutorNode {

    private RealExecutorNode realExecutorNode;

    private FlowNode node;

    public SerialExecutorNode(FlowNode node) {
        this.node = node;
        realExecutorNode = RealExecutorNode.INSTANCE;
    }

    @Deprecated
    @Override
    public int executor(Map<String, Object> param, boolean isLog, AbstractExecutorBuilder builder,
                        String logId, IRuleLog ruleLog, ConsumerInfo... consumerInfo) throws Exception {
        return realExecutorNode.executor(node, param,
                isLog, builder,
                logId, ruleLog,
                false, null,
                consumerInfo);
    }

    @Override
    public int executor(Map<String, Object> param, boolean isLog,
                        AbstractExecutorBuilder builder,
                        String logId, IRuleLog ruleLog,
                        boolean isTraceDetail, TraceModelLogDetail traceModelLogDetail,
                        ConsumerInfo... consumerInfo) throws Exception {
        return realExecutorNode.executor(node, param,
                isLog, builder,
                logId, ruleLog,
                isTraceDetail, traceModelLogDetail,
                consumerInfo);
    }

    @Override
    public int executor(AbstractExecutorBuilder builder, ExecutorRequest executorRequest, ExecutorResponse executorResponse) throws Exception {
        return realExecutorNode.executor(node, builder, executorRequest, executorResponse);
    }
}
