package com.bonc.framework.rule.executor.resolver;

import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.exception.ExecuteModelException;
import com.bonc.framework.rule.executor.context.IQLExpressContext;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.executor.context.impl.QLExpressContext;
import com.bonc.framework.rule.executor.resolver.rule.RuleFactory;
import com.bonc.framework.rule.log.entity.RuleLogDetail;
import com.bonc.framework.rule.resources.flow.FlowNode;
import com.bonc.framework.rule.resources.flow.FlowNodeStateEnum;
import com.bonc.framework.rule.resources.flow.basicflow.impl.FunctionFlowNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class FunctionResolver implements IResolver {

    Log log = LogFactory.getLog(this.getClass());

    @SuppressWarnings("rawtypes")
    @Override
    public Object resolver(FlowNode node, Map<String, Object> param, ConsumerInfo... consumerInfo) throws ExecuteModelException {
        if (!(node instanceof FunctionFlowNode)) {
            throw new ExecuteModelException("[FunctionResolver.resolver]The node is not FunctionFlowNode!");
        }
        Object r = null;
        FunctionFlowNode flowNode = (FunctionFlowNode) node;
        String function = flowNode.getFunction();
        IQLExpressContext context = new QLExpressContext(param);
        if (!StringUtils.isBlank(function)) {
            try {
                r = RuleFactory.getRule().execute(function, context);
            } catch (Exception e) {
                throw new ExecuteModelException("Execute function exception:[" + function + "].Exception:" + e);
            }
        }
        node.setNodeState(FlowNodeStateEnum.TRUE);
        return r;
    }

    @Override
    public Object resolver(FlowNode node, ExecutorRequest executorRequest, RuleLogDetail ruleLogDetail) throws ExecuteModelException {
        if (!(node instanceof FunctionFlowNode)) {
            throw new ExecuteModelException("[FunctionResolver.resolver]The node is not FunctionFlowNode!");
        }
        if (executorRequest == null) {
            throw new IllegalArgumentException();
        }
        Map<String, Object> param = executorRequest.getParam();
        boolean isLoadKpi = executorRequest.isLoadKpi();
        ConsumerInfo[] consumerInfo = executorRequest.getConsumerInfos();

        Object r = null;
        FunctionFlowNode flowNode = (FunctionFlowNode) node;
        String function = flowNode.getFunction();
        IQLExpressContext context = new QLExpressContext(param);
        if (!StringUtils.isBlank(function)) {
            try {
                r = RuleFactory.getRule().execute(function, context);
            } catch (Exception e) {
                throw new ExecuteModelException("Execute function exception:[" + function + "].Exception:" + e);
            }
        }
        node.setNodeState(FlowNodeStateEnum.TRUE);
        return r;
    }

}
