package com.bonc.framework.rule.executor.resolver;

import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.exception.ExecuteModelException;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.log.entity.RuleLogDetail;
import com.bonc.framework.rule.resources.flow.FlowNode;
import com.bonc.framework.rule.resources.flow.FlowNodeStateEnum;

import java.util.Map;

public class DefaultResolver implements IResolver {

    @Override
    public Object resolver(FlowNode node, Map<String, Object> param, ConsumerInfo... consumerInfo) throws ExecuteModelException {
        Object r = true;
        node.setNodeState(FlowNodeStateEnum.TRUE);
        return r;
    }

    @Override
    public Object resolver(FlowNode node, ExecutorRequest executorRequest, RuleLogDetail ruleLogDetail) throws ExecuteModelException {
        Object r = true;
        node.setNodeState(FlowNodeStateEnum.TRUE);
        return r;
    }

}
