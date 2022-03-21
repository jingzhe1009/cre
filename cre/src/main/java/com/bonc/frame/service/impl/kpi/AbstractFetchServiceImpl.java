package com.bonc.frame.service.impl.kpi;

import com.alibaba.fastjson.JSON;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.kpi.KpiFetchLimiters;
import com.bonc.frame.service.kpi.FetchService;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.kpi.KpiLog;
import com.bonc.framework.rule.kpi.KpiResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/9/17 14:28
 */
public abstract class AbstractFetchServiceImpl implements FetchService {

    protected Log log = LogFactory.getLog(getClass());

    /**
     * 从历史日志中获取指标值
     */
    public KpiResult getKpiResultFromSourceKpiLog(KpiDefinition kpiDefinition, ExecutorRequest executorRequest) {
        if (executorRequest == null) {
            return null;
        }
        Map<String, Object> executorParam = executorRequest.getParam();
        List<KpiFetchLimiters> kpiFetchLimitersList = kpiDefinition.getKpiFetchLimitersList();
        Map<String, Object> param = new HashMap<>();
        if (kpiFetchLimitersList != null && !kpiFetchLimitersList.isEmpty()) {
            for (KpiFetchLimiters kpiFetchLimiters : kpiFetchLimitersList) {
                if (kpiFetchLimiters != null) {
                    String variableCode = kpiFetchLimiters.getVariableCode();
                    Object o = executorParam.get(variableCode);
                    param.put(variableCode, o);
                }
            }
        }
        KpiLog sourceKpiLog = executorRequest.getSourceKpi(kpiDefinition.getKpiId(), param);
        if (sourceKpiLog != null) {
            KpiResult kpiResult = new KpiResult();
            org.springframework.beans.BeanUtils.copyProperties(kpiDefinition, kpiResult);
            kpiResult.setSuccessLog(executorRequest.getRuleLogId(), sourceKpiLog.getLogId(), param, sourceKpiLog.getOutputData());
            log.debug("从历史日志中获取指标值,kpiId:[" + kpiDefinition.getKpiId() + "],kpiValue:[" + JSON.toJSONString(sourceKpiLog.getOutputData()) + "]");
            return kpiResult;
        }
        return null;
    }

    /**
     * 从历史日志中获取指标值
     */
    public List<KpiResult> getKpiResultFromSourceKpiLogBatch(List<KpiDefinition> kpiDefinitionList, ExecutorRequest executorRequest) {
        log.debug("批量获取,从缓存日志中获取指标值");
        List<KpiResult> results = new ArrayList<>();
        if (kpiDefinitionList == null || kpiDefinitionList.isEmpty()) {
            return results;
        }
        if (executorRequest == null) {
            return results;
        }
        List<Integer> removeKpiIndex = new ArrayList<>(); // 记录从日志中获取到的指标下标,
        Map<String, Object> getSourceKpiValue = new HashMap<>(); // 记录日志
        for (int i = 0; i < kpiDefinitionList.size(); i++) {
            KpiDefinition kpiDefinition = kpiDefinitionList.get(i);
            KpiResult kpiResultFromSourceKpiLog = getKpiResultFromSourceKpiLog(kpiDefinition, executorRequest);
            if (kpiResultFromSourceKpiLog != null) {
                results.add(kpiResultFromSourceKpiLog);
                removeKpiIndex.add(i);
                getSourceKpiValue.putAll(kpiResultFromSourceKpiLog.getOutputData());
            }
        }
        // 删除不需要执行的指标
        if (!removeKpiIndex.isEmpty()) {
            for (int index : removeKpiIndex) {
                kpiDefinitionList.remove(index);
            }
        }
        log.debug("批量获取,从缓存中获取到的指标值:[" + JSON.toJSONString(getSourceKpiValue) + "]");
        return results;
    }
}
