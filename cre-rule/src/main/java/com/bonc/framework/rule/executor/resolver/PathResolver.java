package com.bonc.framework.rule.executor.resolver;

import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.RuleEngineFactory;
import com.bonc.framework.rule.exception.ExecuteModelException;
import com.bonc.framework.rule.exception.ExecuteRuleException;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.executor.context.impl.QLExpressContext;
import com.bonc.framework.rule.executor.entity.kpi.KpiDefinition;
import com.bonc.framework.rule.executor.resolver.rule.RuleFactory;
import com.bonc.framework.rule.kpi.IKpiService;
import com.bonc.framework.rule.kpi.KpiConstant;
import com.bonc.framework.rule.kpi.KpiLog;
import com.bonc.framework.rule.log.entity.RuleLogDetail;
import com.bonc.framework.rule.resources.flow.FlowNode;
import com.bonc.framework.rule.resources.flow.FlowNodeStateEnum;
import com.bonc.framework.rule.resources.flow.basicflow.PathFlowNode;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PathResolver implements IResolver {

    private Log log = LogFactory.getLog(ActionResolver.class);

    @Override
    public Object resolver(FlowNode node, Map<String, Object> param, ConsumerInfo... consumerInfo) throws ExecuteModelException {
        if (!(node instanceof PathFlowNode)) {
            throw new ExecuteModelException("[PathResolver.resolver]The node is not PathFlowNode!");
        }
        Object result = true;
        PathFlowNode flowNode = (PathFlowNode) node;
        String condition = flowNode.getCondition();

        if (log.isDebugEnabled()) {
            String debugInfo = String.format("执行模型--执行路径节点[nodeName: %s, nodeId: %s, condition: %s, param: %s]",
                    flowNode.getNodeName(), flowNode.getNodeId(), condition, param);
            log.debug(debugInfo);
        }

        if (!StringUtils.isBlank(condition)) {
            //转换"&gt;"、"&lt;"、"&nbsp;"、"&amp;"等特殊字符
            condition = StringEscapeUtils.unescapeXml(condition);
            try {
                result = RuleFactory.getRule().execute(condition, new QLExpressContext(param));

                if (log.isDebugEnabled()) {
                    String debugInfo = String.format("执行模型--执行路径节点结果[nodeName: %s, nodeId: %s, condition: %s, result: %s]",
                            flowNode.getNodeName(), flowNode.getNodeId(), condition, result);
                    log.debug(debugInfo);
                }
//				System.out.println("[PathResolver.resolver]The result is ["+r+"],The condition["+condition+"].");
            } catch (Exception e) {
                log.error(e);
                throw new ExecuteModelException("condition--" + condition + "--" + e.toString());
            }
        }
        if (result != null && result instanceof Boolean && !((boolean) result)) {
            node.setNodeState(FlowNodeStateEnum.FALSE);
        } else {
            node.setNodeState(FlowNodeStateEnum.TRUE);
        }
        return result;
    }

    @Override
    public Object resolver(FlowNode node, ExecutorRequest executorRequest, RuleLogDetail ruleLogDetail) throws ExecuteRuleException {
        if (!(node instanceof PathFlowNode)) {
            throw new ExecuteRuleException("[PathResolver.resolver]The node is not PathFlowNode!");
        }
        if (executorRequest == null) {
            throw new IllegalArgumentException("传入的参数[executorRequest]为null");
        }
        Map<String, Object> param = executorRequest.getParam();
        boolean isLoadKpi = executorRequest.isLoadKpi();
        ConsumerInfo[] consumerInfo = executorRequest.getConsumerInfos();


        Object result = true;
        PathFlowNode flowNode = (PathFlowNode) node;
        String condition = flowNode.getCondition();

        if (log.isDebugEnabled()) {
            String debugInfo = String.format("执行模型--执行路径节点[nodeName: %s, nodeId: %s, condition: %s, param: %s]",
                    flowNode.getNodeName(), flowNode.getNodeId(), condition, param);
            log.debug(debugInfo);
        }
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
            throw new ExecuteRuleException("规则集加载指标失败,action:[" + flowNode + "]", e);
        }
        // 指标执行日志
        if (!CollectionUtils.isEmpty(kpiLogExecutionProcess)) {
            ruleLogDetail.addAllKpiLog(kpiLogExecutionProcess);
        }

        if (!StringUtils.isBlank(condition)) {
            //转换"&gt;"、"&lt;"、"&nbsp;"、"&amp;"等特殊字符
            condition = StringEscapeUtils.unescapeXml(condition);
            try {
                result = RuleFactory.getRule().execute(condition, new QLExpressContext(param));

                if (log.isDebugEnabled()) {
                    String debugInfo = String.format("执行模型--执行路径节点结果[nodeName: %s, nodeId: %s, condition: %s, result: %s]",
                            flowNode.getNodeName(), flowNode.getNodeId(), condition, result);
                    log.debug(debugInfo);
                }
//				System.out.println("[PathResolver.resolver]The result is ["+r+"],The condition["+condition+"].");
            } catch (Exception e) {
                log.error(e);
                throw new ExecuteRuleException("condition--" + condition + "--" + e.toString());
            }
        }
        if (result != null && result instanceof Boolean && !((boolean) result)) {
            node.setNodeState(FlowNodeStateEnum.FALSE);
        } else {
            node.setNodeState(FlowNodeStateEnum.TRUE);
        }
        return result;
    }

}
