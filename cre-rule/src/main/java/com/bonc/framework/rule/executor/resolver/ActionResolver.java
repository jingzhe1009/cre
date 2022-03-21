package com.bonc.framework.rule.executor.resolver;

import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.RuleEngineFactory;
import com.bonc.framework.rule.exception.ExecuteModelException;
import com.bonc.framework.rule.exception.ExecuteRuleException;
import com.bonc.framework.rule.executor.context.IQLExpressContext;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.executor.entity.kpi.KpiDefinition;
import com.bonc.framework.rule.executor.resolver.rule.RuleFactory;
import com.bonc.framework.rule.kpi.IKpiService;
import com.bonc.framework.rule.kpi.KpiConstant;
import com.bonc.framework.rule.kpi.KpiLog;
import com.bonc.framework.rule.log.entity.RuleLogDetail;
import com.bonc.framework.rule.resources.flow.FlowNode;
import com.bonc.framework.rule.resources.flow.FlowNodeStateEnum;
import com.bonc.framework.rule.resources.flow.basicflow.impl.ActionFlowNode;
import com.bonc.framework.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionResolver implements IResolver {

    private Log log = LogFactory.getLog(ActionResolver.class);

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    @Deprecated
    public Object resolver(FlowNode node, Map<String, Object> param, ConsumerInfo... consumerInfo) throws ExecuteModelException {
        if (!(node instanceof ActionFlowNode)) {
            throw new ExecuteRuleException("[ActionResolver.resolver]The node is not actionFlowNode!");
        }

        ActionFlowNode flowNode = (ActionFlowNode) node;
        String action = flowNode.getAction();
        IQLExpressContext context = RuleFactory.getRule().createContext(param);

        if (log.isDebugEnabled()) {
            String debugInfo = String.format("执行模型--执行规则集节点[nodeName: %s, nodeId: %s, action: %s, param: %s]",
                    flowNode.getNodeName(), flowNode.getNodeId(), flowNode.getAction(), param);
            log.debug(debugInfo);
        }

        List<Map> ruleList = null;
        try {
            ruleList = JsonUtils.toList(action, Map.class);
        } catch (Exception e) {
            throw new ExecuteRuleException("The action parse to List error.[" + action + "]", e);
        }

        if (ruleList == null || ruleList.isEmpty()) {
            node.setNodeState(FlowNodeStateEnum.TRUE);
            return null;
        }
        Object r = false;
        FlowNodeStateEnum state = FlowNodeStateEnum.TRUE;
        List<Map<String, Object>> hitRuleList = new ArrayList<Map<String, Object>>();
        for (Map map : ruleList) {
            String rule = (String) map.get("rule");
            Boolean isEndAction = (Boolean) map.get("isEndAction");
            Boolean isEndFlow = (Boolean) map.get("isEndFlow");
            if (!StringUtils.isBlank(rule)) {
                try {
                    r = RuleFactory.getRule().execute(rule, context);
//					System.out.println("[ActionResolver.resolver]The result is ["+r+"],The action["+action+"].");
                } catch (Exception e) {
                    log.error("执行规则失败,规则表达式不合法,规则表达式：" + rule, e);
                    throw new ExecuteRuleException("执行规则失败,规则表达式不合法,规则表达式：" + rule + "，原因：" + e.getMessage());
                }
            }
            try {
                Map resultMap = JsonUtils.stringToMap(r.toString());
                context.putAll(resultMap);

                if (log.isDebugEnabled()) {
                    String debugInfo = String.format("执行模型--执行规则集节点结果[nodeName: %s, nodeId: %s, action: %s, context: %s]",
                            flowNode.getNodeName(), flowNode.getNodeId(), flowNode.getAction(), context);
                    log.debug(debugInfo);
                }

            } catch (Exception e) {
            }

            if (r != null) {//命中规则
                Map<String, Object> hitRuleMap = new HashMap<String, Object>();
                hitRuleMap.putAll(map);
                hitRuleMap.put("executeResult", r);
                hitRuleList.add(hitRuleMap);
                if (isEndFlow != null && isEndFlow == true) {//结束流程
                    state = FlowNodeStateEnum.END;
                }
                if (isEndAction != null && isEndAction == true) {//结束规则集
                    node.setNodeState(FlowNodeStateEnum.TRUE);
                    break;
                }
            }

        }
        node.setNodeState(state);
        return hitRuleList;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Object resolver(FlowNode node, ExecutorRequest executorRequest, RuleLogDetail ruleLogDetail) throws ExecuteRuleException {
        if (!(node instanceof ActionFlowNode)) {
            throw new ExecuteRuleException("[ActionResolver.resolver]The node is not actionFlowNode!");
        }
        if (executorRequest == null) {
            throw new IllegalArgumentException();
        }
        Map<String, Object> param = executorRequest.getParam();
        boolean isLoadKpi = executorRequest.isLoadKpi();
        ConsumerInfo[] consumerInfo = executorRequest.getConsumerInfos();

        ActionFlowNode flowNode = (ActionFlowNode) node;
        String action = flowNode.getAction();
        IQLExpressContext context = RuleFactory.getRule().createContext(param);
        //加载指标
        List<KpiLog> kpiLogExecutionProcess = null;
        try {
            if (isLoadKpi && !KpiConstant.LOADER_KPI_STRATEGY_TYPE_COMPILE.equals(executorRequest.getLoaderKpiStrategyType())) {
                Map<String, KpiDefinition> needKpiIds = flowNode.getNeedKpis();
                List<KpiDefinition> neetLoaderkpiDefinitionList = null;
                if (needKpiIds != null && !needKpiIds.isEmpty()) {
                    if (KpiConstant.LOADER_KPI_STRATEGY_TYPE_RUNTIME_FIRST.equals(executorRequest.getLoaderKpiStrategyType())) {
                        // 如果运行时, 第一次遇见这个指标时加载 ; 判断param中有没有指标的code,如果有,就代表已经加载过了, 不再加载
                        neetLoaderkpiDefinitionList = new ArrayList<>(needKpiIds.size());
                        for (String id : needKpiIds.keySet()) {
                            KpiDefinition kpiDefinition = needKpiIds.get(id);
                            if (!param.containsKey(kpiDefinition.getKpiCode())) {
                                neetLoaderkpiDefinitionList.add(kpiDefinition);
                            }
                        }
                    } else if (KpiConstant.LOADER_KPI_STRATEGY_TYPE_RUNTIME_EVERY.equals(executorRequest.getLoaderKpiStrategyType())) {
                        // 如果每次都加载, 就直接获取所有的指标, 进行加载
                        neetLoaderkpiDefinitionList = new ArrayList<>(needKpiIds.values());
                    }
                    // 加载指标
                    IKpiService kpiService = RuleEngineFactory.getRuleEngine().getKpiService();
                    kpiLogExecutionProcess = kpiService.loadKpiVarByKpiDefinitionList(neetLoaderkpiDefinitionList, param, executorRequest);
                }
            }
        } catch (Exception e) {
            log.error("规则集获取指标值失败,节点:[" + flowNode + "],参数:" + param, e);
            throw new ExecuteRuleException("规则集获取指标值失败,节点名称:[" + flowNode.getNodeName() + "],失败原因:[" + e.getMessage() + "],参数:" + param, e);
        }
        // 指标执行日志
        if (!CollectionUtils.isEmpty(kpiLogExecutionProcess)) {
            ruleLogDetail.addAllKpiLog(kpiLogExecutionProcess);
        }
        if (log.isDebugEnabled()) {
            String debugInfo = String.format("执行模型--执行规则集节点[nodeName: %s, nodeId: %s, action: %s, param: %s]",
                    flowNode.getNodeName(), flowNode.getNodeId(), flowNode.getAction(), param);
            log.debug(debugInfo);
        }

        List<Map> ruleList = null;
        try {
            ruleList = JsonUtils.toList(action, Map.class);
        } catch (Exception e) {
            throw new ExecuteRuleException("规则集转规则集List失败,节点名称:[" + flowNode.getNodeName() + "],action[" + action + "]", e);
        }

        if (ruleList == null || ruleList.isEmpty()) {
            node.setNodeState(FlowNodeStateEnum.TRUE);
            return null;
        }
        Object r = false;
        FlowNodeStateEnum state = FlowNodeStateEnum.TRUE;
        List<Map<String, Object>> hitRuleList = new ArrayList<Map<String, Object>>();
        for (Map map : ruleList) {
            String rule = (String) map.get("rule");
            Boolean isEndAction = (Boolean) map.get("isEndAction");
            Boolean isEndFlow = (Boolean) map.get("isEndFlow");
            if (!StringUtils.isBlank(rule)) {
                try {
                    r = RuleFactory.getRule().execute(rule, context);
//					System.out.println("[ActionResolver.resolver]The result is ["+r+"],The action["+action+"].");
                } catch (Exception e) {
//                    log.error("执行规则失败，规则表达式：" + rule, e);
                    throw new ExecuteRuleException("执行规则失败，规则表达式：" + rule + "失败原因:[" + e.getMessage() + "],参数:" + param, e);
                }
            }
            try {
                Map resultMap = JsonUtils.stringToMap(r.toString());
                context.putAll(resultMap);

                if (log.isDebugEnabled()) {
                    String debugInfo = String.format("执行模型--执行规则集节点结果[nodeName: %s, nodeId: %s, action: %s, context: %s]",
                            flowNode.getNodeName(), flowNode.getNodeId(), flowNode.getAction(), context);
                    log.debug(debugInfo);
                }

            } catch (Exception e) {
//                log.warn("表达式结果解析异常", e);
            }

            if (r != null) {//命中规则
                Map<String, Object> hitRuleMap = new HashMap<String, Object>();
                hitRuleMap.putAll(map);
                hitRuleMap.put("executeResult", r);
                hitRuleList.add(hitRuleMap);
                if (isEndFlow != null && isEndFlow == true) {//结束流程
                    state = FlowNodeStateEnum.END;
                }
                if (isEndAction != null && isEndAction == true) {//结束规则集
                    node.setNodeState(FlowNodeStateEnum.TRUE);
                    break;
                }
            }

        }
        node.setNodeState(state);
        return hitRuleList;
    }

}
