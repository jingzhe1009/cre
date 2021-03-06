package com.bonc.framework.rule.executor.resolver;

import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.RuleEngineFactory;
import com.bonc.framework.rule.exception.ExecuteModelException;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.executor.entity.kpi.KpiDefinition;
import com.bonc.framework.rule.kpi.IKpiService;
import com.bonc.framework.rule.kpi.KpiConstant;
import com.bonc.framework.rule.kpi.KpiLog;
import com.bonc.framework.rule.log.entity.RuleLogDetail;
import com.bonc.framework.rule.resources.flow.FlowNode;
import com.bonc.framework.rule.resources.flow.FlowNodeStateEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/9/14 16:24
 */
public class StartResolver implements IResolver {
    private Log log = LogFactory.getLog(StartResolver.class);

    @Override
    public Object resolver(FlowNode node, Map<String, Object> param, ConsumerInfo... consumerInfo) throws ExecuteModelException {
        Object r = true;
        node.setNodeState(FlowNodeStateEnum.TRUE);
        return r;
    }

    @Override
    public Object resolver(FlowNode node, ExecutorRequest executorRequest, RuleLogDetail ruleLogDetail) throws ExecuteModelException {
        // loaderKpi
        List<KpiLog> kpiLogExecutionProcess = null;
        try {
            if (executorRequest.isLoadKpi() && KpiConstant.LOADER_KPI_STRATEGY_TYPE_COMPILE.equals(executorRequest.getLoaderKpiStrategyType())) {
                List<KpiDefinition> kpiDefinitions = RuleEngineFactory.getRuleEngine().getConContext().queryKpiDefinitions(executorRequest.getRuleId());
                IKpiService kpiService = RuleEngineFactory.getRuleEngine().getKpiService();
                kpiLogExecutionProcess = kpiService.loadKpiVarByKpiDefinitionList(kpiDefinitions, executorRequest.getParam(), executorRequest);
//                    kpiService.loadKpiVar(executorRequest.getFolderId(), executorRequest.getRuleId(), param, executorRequest);
            }
        } catch (Exception e) {
            log.error("???????????????????????????????????????:" + e.getMessage(), e);
            throw new ExecuteModelException("???????????????????????????????????????", e);
//                return ExecutorResponse.newFailedExecutorResponse("????????????????????????" + e.getMessage());
        }
        if (!CollectionUtils.isEmpty(kpiLogExecutionProcess)) {
            ruleLogDetail.addAllKpiLog(kpiLogExecutionProcess);
        }
        Object r = true;
        node.setNodeState(FlowNodeStateEnum.TRUE);
        return r;
    }
}
