package com.bonc.framework.rule.executor.resolver;

import com.bonc.framework.api.RuleApiFacade;
import com.bonc.framework.api.entity.CallApiResponse;
import com.bonc.framework.api.exception.CallApiException;
import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.exception.ExecuteModelException;
import com.bonc.framework.rule.exception.ExecuteRuleException;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.log.entity.RuleLogDetail;
import com.bonc.framework.rule.resources.flow.FlowNode;
import com.bonc.framework.rule.resources.flow.FlowNodeStateEnum;
import com.bonc.framework.rule.resources.flow.basicflow.impl.InterfaceFlowNode;
import com.bonc.framework.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class InterfaceResolver implements IResolver {

    Log log = LogFactory.getLog(this.getClass());

    @Override
    @Deprecated
    public Object resolver(FlowNode node, Map<String, Object> param, ConsumerInfo... consumerInfo) throws ExecuteModelException {
        if (!(node instanceof InterfaceFlowNode)) {
            throw new ExecuteRuleException("[InterfaceFlowNode.resolver]The node is not InterfaceFlowNode!");
        }

        Object r = null;
        InterfaceFlowNode flowNode = (InterfaceFlowNode) node;
        String apiId = flowNode.getApiId();

        if (log.isDebugEnabled()) {
            String debugInfo = String.format("执行模型--执行接口节点[nodeName: %s, nodeId: %s, apiId: %s, param: %s]",
                    flowNode.getNodeName(), flowNode.getNodeId(), apiId, param);
            log.debug(debugInfo);
        }

        if (!StringUtils.isBlank(apiId)) {
            try {
                // TODO: 可配置的连接超时时间
                r = RuleApiFacade.getInstance().callRuleApi(apiId, JsonUtils.collectToString(param), consumerInfo);
                Map<String, Object> map = JsonUtils.stringToCollect((String) r);

                if (log.isDebugEnabled()) {
                    String debugInfo = String.format("执行模型--执行接口节点结果[nodeName: %s, nodeId: %s, apiId: %s, map: %s]",
                            flowNode.getNodeName(), flowNode.getNodeId(), apiId, map);
                    log.debug(debugInfo);
                }

                param.putAll(map);
            } catch (Exception e) {
                throw new ExecuteRuleException("调用接口失败，节点名称[" + flowNode.getNodeName() + "]", e);
            }
        }
        node.setNodeState(FlowNodeStateEnum.TRUE);
        return param;
    }

    @Override
    public Object resolver(FlowNode node, ExecutorRequest executorRequest, RuleLogDetail ruleLogDetail) throws ExecuteRuleException {
        if (!(node instanceof InterfaceFlowNode)) {
            throw new ExecuteRuleException("[InterfaceFlowNode.resolver]The node is not InterfaceFlowNode!");
        }
        if (executorRequest == null) {
            throw new IllegalArgumentException("传入的参数为null");
        }
        Map<String, Object> param = executorRequest.getParam();
        ConsumerInfo[] consumerInfo = executorRequest.getConsumerInfos();

        Object r = null;
        InterfaceFlowNode flowNode = (InterfaceFlowNode) node;
        String apiId = flowNode.getApiId();

        if (log.isDebugEnabled()) {
            String debugInfo = String.format("执行模型--执行接口节点[nodeName: %s, nodeId: %s, apiId: %s, param: %s]",
                    flowNode.getNodeName(), flowNode.getNodeId(), apiId, param);
            log.debug(debugInfo);
        }

        if (!StringUtils.isBlank(apiId)) {
            try {
                // TODO: 可配置的连接超时时间
                CallApiResponse callApiResponse = RuleApiFacade.getInstance().callRuleApi(apiId,
                        JsonUtils.collectToString(param), executorRequest.getRuleLogId(),
                        executorRequest.isLog(),
                        executorRequest.getModelExecutionType().getCode(),
                        executorRequest.getSourceApiLogMap(), consumerInfo);
                if (callApiResponse == null) {
                    throw new CallApiException("请求接口失败,接口响应为空");
                }
                r = callApiResponse.getResult();
                Map<String, Object> map = JsonUtils.stringToCollect((String) r);

                if (log.isDebugEnabled()) {
                    String debugInfo = String.format("执行模型--执行接口节点结果[nodeName: %s, nodeId: %s, apiId: %s, map: %s]",
                            flowNode.getNodeName(), flowNode.getNodeId(), apiId, map);
                    log.debug(debugInfo);
                }

                param.putAll(map);
            } catch (Exception e) {
                throw new ExecuteRuleException("请求接口异常,节点名称[" + flowNode.getNodeName() + "]", e);
            }
        }
        node.setNodeState(FlowNodeStateEnum.TRUE);
        return param;
    }

}
