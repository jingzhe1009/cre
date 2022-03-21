package com.bonc.frame.engine.kpi;

import com.bonc.frame.service.kpi.KpiService;
import com.bonc.frame.util.CollectionUtil;
import com.bonc.frame.util.SpringUtils;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.executor.entity.kpi.KpiDefinition;
import com.bonc.framework.rule.kpi.IKpiService;
import com.bonc.framework.rule.kpi.KpiLog;
import com.bonc.framework.rule.kpi.KpiResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/1/6 17:41
 */
public class IKpiServiceImp implements IKpiService {

    private KpiService kpiService;

    public IKpiServiceImp() {
        this.kpiService = (KpiService) SpringUtils.getBean("kpiService");
    }

    @Override
    public Object getKpiValue(KpiDefinition kpiDefinition, Map<String, Object> params, ExecutorRequest executorRequest) {
        return kpiService.getKpiValue(copyProperties(kpiDefinition), params, executorRequest);
    }

//    @Override
//    public List<KpiResult> getKpiValueBatchByFetchType(List<KpiDefinition> kpiDefinitionList, String fetchType, Map<String, Object> params, ExecutorRequest executorRequest) {
//        return kpiService.getKpiValueBatchByFetchType(copyPropertiesAll(kpiDefinitionList), fetchType, params, executorRequest);
//    }

    @Override
    public List<KpiResult> getKpiValueBatch(List<KpiDefinition> kpiDefinitionList, Map<String, Object> params, ExecutorRequest executorRequest) {
        return kpiService.getKpiValueBatch(copyPropertiesAll(kpiDefinitionList), params, executorRequest);
    }

    @Override
    public List<KpiLog> loadKpiVarByKpiDefinitionList(List<KpiDefinition> kpiDefinitionList, Map<String, Object> param, ExecutorRequest executorRequest) {
        if (CollectionUtil.isEmpty(kpiDefinitionList)) {
            return Collections.emptyList();
        }
        // FIXME: hardcode
        List<KpiLog> resultLog = new ArrayList<>(kpiDefinitionList.size());
        try {
//            ResponseResult kpiBaseInfoByRuleId = kpiService.getKpiBaseInfoByRuleId(ruleId);
//            List<com.bonc.frame.entity.kpi.KpiDefinition> kpiDefinitionList = (List<com.bonc.frame.entity.kpi.KpiDefinition>) kpiBaseInfoByRuleId.getData();

            List<KpiResult> kpiValueBatch = getKpiValueBatch(kpiDefinitionList, param, executorRequest);
            if (kpiValueBatch != null && !kpiValueBatch.isEmpty()) {
                for (KpiResult kpiResult : kpiValueBatch) {
                    if (kpiResult != null) {
                        String kpiCode = kpiResult.getKpiCode();
                        Map<String, Object> outputData = kpiResult.getOutputData();
                        if (outputData == null || outputData.isEmpty()) {
                            throw new RuntimeException();
                        }
                        param.putAll(outputData);
                        resultLog.add(kpiResult.getKpiLog());
                    }
                }
//                param.putAll(kpiValueBatch);
            }
        } catch (Exception e) {
            throw new RuntimeException("加载指标失败:" + e.getMessage(), e);
        }
        return resultLog;
    }


//    @Override
//    public void loadKpiVar(String folderId, String ruleId, Map<String, Object> param, ExecutorRequest executorRequest) {
//        try {
//            List<com.bonc.frame.entity.kpi.KpiDefinition> kpiDefinitionList = null;
//            if (!StringUtils.isBlank(ruleId)) {
//                ResponseResult kpiBaseInfoByRuleId = kpiService.getKpiBaseInfoByRuleIdNoAuth(ruleId);
//                kpiDefinitionList = (List<com.bonc.frame.entity.kpi.KpiDefinition>) kpiBaseInfoByRuleId.getData();
//            }
//            //kpiService.getKpiValueBatch(copyPropertiesAll(kpiDefinitionList), params, executorRequest)
//            List<KpiResult> kpiValueBatch = kpiService.getKpiValueBatch(kpiDefinitionList, param, executorRequest);
//            if (kpiValueBatch != null && !kpiValueBatch.isEmpty()) {
//                for (KpiResult kpiResult : kpiValueBatch) {
//                    if (kpiResult != null) {
//                        String kpiCode = kpiResult.getKpiCode();
//                        Map<String, Object> outputData = kpiResult.getOutputData();
//                        if (outputData == null || outputData.isEmpty()) {
//                            throw new RuntimeException("指标返回结果异常:返回结果中的outPutMap为null");// 这个异常一般不会发生
//                        }
//                        param.put(kpiCode, outputData.values().toArray()[0]);
//                    }
//                }
////                param.putAll(kpiValueBatch);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("加载指标失败。", e);
//        }
//    }

    public com.bonc.frame.entity.kpi.KpiDefinition copyProperties(KpiDefinition kpiDefinition) {
        if (kpiDefinition != null) {
            com.bonc.frame.entity.kpi.KpiDefinition result = new com.bonc.frame.entity.kpi.KpiDefinition();
            org.springframework.beans.BeanUtils.copyProperties(kpiDefinition, result);
            return result;
        }
        return null;
    }

    public List<com.bonc.frame.entity.kpi.KpiDefinition> copyPropertiesAll(List<KpiDefinition> kpiDefinitionList) {
        if (kpiDefinitionList != null && !kpiDefinitionList.isEmpty()) {
            List<com.bonc.frame.entity.kpi.KpiDefinition> result = new ArrayList<>(kpiDefinitionList.size());
            for (KpiDefinition kpiDefinition : kpiDefinitionList) {
                result.add(copyProperties(kpiDefinition));
            }
            return result;
        }
        return Collections.EMPTY_LIST;
    }
}
