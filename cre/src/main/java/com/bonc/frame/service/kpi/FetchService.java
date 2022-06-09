package com.bonc.frame.service.kpi;

import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.framework.rule.exception.ExecuteException;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.kpi.KpiResult;

import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/10/28 11:55
 */
public interface FetchService {

    boolean isSupport(KpiDefinition kpiDefinition);

    FetchType getSupport();

    /**
     * 根据指标定义和输入参数获取指标值
     *
     * @param kpiDefinition 指标定义
     * @param params        输入参数
     * @param <T>
     * @return 指标值
     */
    KpiResult getKpiValue(KpiDefinition kpiDefinition, Map<String, Object> params, ExecutorRequest executorRequest) throws ExecuteException;

    /**
     * 批量获取指标值
     *
     * @param kpiDefinitions
     * @param params
     * @return
     */
    List<KpiResult> getKpiValueBatch(List<KpiDefinition> kpiDefinitions, Map<String, Object> params, ExecutorRequest executorRequest);


}
