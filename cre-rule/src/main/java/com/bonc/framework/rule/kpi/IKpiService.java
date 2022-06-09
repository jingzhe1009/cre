package com.bonc.framework.rule.kpi;

import com.bonc.framework.rule.exception.ExecuteException;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.executor.entity.kpi.KpiDefinition;

import java.util.List;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/1/6 17:31
 */
public interface IKpiService {

    Object getKpiValue(KpiDefinition kpiDefinition, Map<String, Object> params, ExecutorRequest executorRequest) throws ExecuteException;

//    /**
//     * 批量获取KPI的值
//     *
//     * @param fetchType 指标类型 数据源:FetchType.DB.getValue()="0"   指标:FetchType.API.getValue()="1"
//     */
//    List<KpiResult> getKpiValueBatchByFetchType(List<KpiDefinition> kpiDefinitionList, String fetchType, Map<String, Object> params, ExecutorRequest executorRequest);

    /**
     * 批量获取KPI的值
     * 会根据指标的类型进行分类,调用 getKpiValueBatchByFetchType
     */
    List<KpiResult> getKpiValueBatch(List<KpiDefinition> kpiDefinitionList, Map<String, Object> params, ExecutorRequest executorRequest);

//    void loadKpiVar(String folderId, String ruleId, Map<String, Object> param, ExecutorRequest executorRequest);

    List<KpiLog> loadKpiVarByKpiDefinitionList(List<KpiDefinition> kpiDefinitionList, Map<String, Object> param, ExecutorRequest executorRequest);
}
