package com.bonc.frame.service.impl.kpi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.exception.FetchException;
import com.bonc.frame.service.api.ApiService;
import com.bonc.frame.service.kpi.FetchType;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.framework.api.RuleApiFacade;
import com.bonc.framework.api.entity.CallApiResponse;
import com.bonc.framework.api.exception.CallApiException;
import com.bonc.framework.api.log.entity.ApiLog;
import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.entity.format.VariableFormat;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.executor.context.impl.ModelExecutorType;
import com.bonc.framework.rule.kpi.KpiResult;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/10/28 11:59
 */
@Service
public class ApiFetchServiceImpl extends AbstractFetchServiceImpl {

    @Autowired
    ApiService apiService;
    @Autowired
    VariableService variableService;

    @Override
    public boolean isSupport(KpiDefinition kpiDefinition) {
        Preconditions.checkArgument(kpiDefinition != null,
                "参数[kpiDefinition]不能为空");
        if (getSupport().getValue().equals(kpiDefinition.getFetchType())) {
            return true;
        }
        return false;
    }

    @Override
    public FetchType getSupport() {
        return FetchType.API;
    }

    @Override
    public KpiResult getKpiValue(KpiDefinition kpiDefinition, Map<String, Object> params, ExecutorRequest executorRequest) {
        if (ModelExecutorType.ABTEST == executorRequest.getModelExecutionType()) {
            KpiResult kpiResultFromSourceKpiLog = getKpiResultFromSourceKpiLog(kpiDefinition, executorRequest);
            if (kpiResultFromSourceKpiLog != null) {
                return kpiResultFromSourceKpiLog;
            }
        }
        log.info("指标[" + kpiDefinition.getKpiId() + "]以接口方式获取指标值");
        if (!isSupport(kpiDefinition)) {
            throw new IllegalArgumentException("不支持该类型");
        }
        String apiId = kpiDefinition.getApiId();
        String apiName = kpiDefinition.getApiName();

        ConsumerInfo[] consumerInfo = null;
        String ruleLogId = null;
        Map<String, ApiLog> sourceApiLogMap = null;
        String modelExecutionTypeCode = null;
        if (executorRequest != null) {
            consumerInfo = executorRequest.getConsumerInfos();
            ruleLogId = executorRequest.getRuleLogId();
            sourceApiLogMap = executorRequest.getSourceApiLogMap();
            modelExecutionTypeCode = executorRequest.getModelExecutionType().getCode();
        }

        Object resultData = null; // 返回数据
        String apiResult = null; // 请求接口返回的数据

        KpiResult kpiResult = new KpiResult();
        BeanUtils.copyProperties(kpiDefinition, kpiResult);
        try {
            if (StringUtils.isBlank(apiId)) {
                throw new Exception("指标[" + kpiDefinition.getKpiCode() + "]接口Id为空");
            }
            log.info("指标[" + kpiDefinition.getKpiCode() + "]开始请求接口 apiId : " + apiId + " , apiName : " + apiName
                    + "参数:[" + JSON.toJSONString(params) + "]");
            CallApiResponse callApiResponse = RuleApiFacade.getInstance().callRuleApi(apiId, JSON.toJSONString(params), ruleLogId,
                    executorRequest.isLog(),
                    modelExecutionTypeCode, sourceApiLogMap, consumerInfo);
            if (callApiResponse != null) {
                Map<String, Object> realParam = callApiResponse.getRealParam();
                kpiResult.setInputData(realParam);
                apiResult = callApiResponse.getResult();
            }
            log.info("指标[" + kpiDefinition.getKpiCode() + "]请求接口的返回值为: " + apiResult);
        } catch (Exception e) {
            log.error("指标[" + kpiDefinition.getKpiCode() + "]请求接口异常,apiid : " + apiId + " apiName : " + apiName, e);
            throw new FetchException("指标[" + kpiDefinition.getKpiCode() + "]请求接口异常,apiid : " + apiId +
                    " apiName : " + apiName + "参数:[" + JSON.toJSONString(params) + "]", e);
        }

        // 处理结果数据
        if (StringUtils.isBlank(apiResult)) {
            //返会默认值
            log.warn("指标[" + kpiDefinition.getKpiId() + "]从接口中获取的结果为空," +
                    "指标[" + kpiDefinition.getKpiCode() + "]值赋予默认值[" + kpiDefinition.getKpiDefaultValue() + "]");
            resultData = VariableFormat.convertValue(kpiDefinition.getKpiType(), kpiDefinition.getKpiCode(), kpiDefinition.getKpiDefaultValue());
        } else {
            JSONObject apiResultJson = JSON.parseObject(apiResult);
            if (apiResultJson == null || apiResultJson.isEmpty()) {
                //返回默认值
                log.warn("指标[" + kpiDefinition.getKpiId() + "]从接口中获取的结果为空," +
                        "指标[" + kpiDefinition.getKpiCode() + "]值赋予默认值[" + kpiDefinition.getKpiDefaultValue() + "]");
                resultData = VariableFormat.convertValue(kpiDefinition.getKpiType(), kpiDefinition.getKpiCode(), kpiDefinition.getKpiDefaultValue());
            } else {
                resultData = apiResultJson.get(kpiDefinition.getKpiValueSourceCode());
                log.info("指标[" + kpiDefinition.getKpiCode() + "]查询到的指标值 : " + resultData);
            }
        }
        Map<String, Object> outputData = new HashMap<>();
        outputData.put(kpiDefinition.getKpiCode(), resultData);
        kpiResult.setSuccessLog(ruleLogId, null, params, outputData);

        return kpiResult;
    }

    /**
     * 批量获取指标值
     *
     * @return key=kpiCode, value=kpiValue
     */
    @Override
    //TODO :遍历判断是不是所有的指标都是从接口获取指标值
    public List<KpiResult> getKpiValueBatch(List<KpiDefinition> kpiDefinitionList, Map<String, Object> params, ExecutorRequest executorRequest) {
        List<KpiResult> results = new ArrayList<>(kpiDefinitionList.size());

        if (ModelExecutorType.ABTEST == executorRequest.getModelExecutionType()) {
            List<KpiResult> kpiResultFromSourceKpiLogBatch = getKpiResultFromSourceKpiLogBatch(kpiDefinitionList, executorRequest);
            if (kpiResultFromSourceKpiLogBatch != null && !kpiResultFromSourceKpiLogBatch.isEmpty()) {
                results.addAll(kpiResultFromSourceKpiLogBatch);
            }
            if (CollectionUtils.isEmpty(kpiDefinitionList)) {
                return results;
            }
        }
        //将指标通过 接口Id进行分类
        Map<String, List<KpiDefinition>> apiKpi = sortInterfaceForKpi(kpiDefinitionList);

        //遍历,相同的接口只请求一次
        for (String apiId : apiKpi.keySet()) {
            List<KpiDefinition> kpiDefinitionAPIS = apiKpi.get(apiId);
            // 批量获取api返回值
            results.addAll(getKpiValueBatchByApi(kpiDefinitionAPIS, apiId, params, executorRequest));
        }

        return results;
    }

    /**
     * @param kpiDefinitions 所有的指标都通过请求同一个接口,获取返回值
     * @param apiId          接口Id
     */
    private List<KpiResult> getKpiValueBatchByApi(List<KpiDefinition> kpiDefinitions, String apiId, Map<String, Object> params, ExecutorRequest executorRequest) {
        List<KpiResult> restlts = new ArrayList<>(kpiDefinitions.size());

        // kpiCodes 用来写日志
        StringBuilder kpiCodes = new StringBuilder();
        for (KpiDefinition defaultV : kpiDefinitions) {
            kpiCodes.append(defaultV.getKpiCode()).append(",");
        }
        kpiCodes.deleteCharAt(kpiCodes.length() - 1);
        String kpiCodeStr = kpiCodes.toString();
        log.info("指标[" + kpiCodeStr + "]请求接口,apiid : " + apiId
                + ",参数:[" + JSON.toJSONString(params) + "]");

        //请求接口,获取返回值
        CallApiResponse callApiResponse = null;
        Map<String, Object> apiResult = null;
        Map<String, Object> realParam = null;
        try {
            callApiResponse = callApi(apiId, params, kpiCodeStr, executorRequest);
            if (callApiResponse != null) {
                realParam = callApiResponse.getRealParam();
                String result = callApiResponse.getResult();
                if (StringUtils.isBlank(result)) {
                    return null;
                }
                apiResult = JSONObject.parseObject(result);
            } else {
                throw new CallApiException("请求接口的响应为空");
            }
        } catch (Exception e) {
            log.error("指标[" + kpiCodeStr + "]请求接口异常,apiid : " + apiId, e);
            throw new FetchException("指标[" + kpiCodeStr + "]请求接口异常,apiid : " + apiId
                    + ",参数:[" + JSON.toJSONString(params) + "]", e);
        }
        String ruleLogId = null;
        if (executorRequest != null) {
            ruleLogId = executorRequest.getRuleLogId();
        }

        for (KpiDefinition apiKpiDef : kpiDefinitions) {
            String kpiId = apiKpiDef.getKpiId();
            String kpiCode = apiKpiDef.getKpiCode();
            String kpiDefaultValue = apiKpiDef.getKpiDefaultValue();
            KpiResult kpiResult = new KpiResult();
            BeanUtils.copyProperties(apiKpiDef, kpiResult);
            if (apiResult == null || apiResult.isEmpty()) {
                //返回默认值
                log.warn("指标[" + kpiId + "]从接口中获取的结果为空," +
                        "指标[" + kpiCode + "]值赋予默认值[" + kpiDefaultValue + "]");

                Map<String, Object> outputData = new HashMap<>();
                outputData.put(kpiCode, kpiDefaultValue == null ? null : VariableFormat.convertValue(kpiResult.getKpiType(), kpiCode, kpiDefaultValue));
                kpiResult.setSuccessLog(ruleLogId, null, realParam, outputData);

                restlts.add(kpiResult);
            } else {
                Object kpiValue = apiResult.get(apiKpiDef.getKpiValueSourceCode());
                log.info(" 指标[" + kpiId + "]获取到的值为 {code:" + kpiCode + ",value:" + kpiValue + "}");
                Map<String, Object> outputData = new HashMap<>();
                outputData.put(kpiCode, kpiValue == null ? null : VariableFormat.convertValue(kpiResult.getKpiType(), kpiCode, kpiValue));
                kpiResult.setSuccessLog(ruleLogId, null, realParam, outputData);

                restlts.add(kpiResult);
            }
        }
        return restlts;
    }


    /**
     * 将指标通过 接口Id进行分类
     *
     * @return key=apiId,value=List<KpiDefinition>
     */
    private Map<String, List<KpiDefinition>> sortInterfaceForKpi(List<KpiDefinition> kpiDefinitionList) {
        Map<String, List<KpiDefinition>> keyMap = new HashMap<>();//key存的是接口比较的字符串，value存的是指标ID
        for (KpiDefinition kpiDefinition : kpiDefinitionList) {
            String key = kpiDefinition.getApiId();
            if (!keyMap.containsKey(key)) {
                keyMap.put(key, new ArrayList());
            }
            keyMap.get(key).add(kpiDefinition);
        }
        return keyMap;
    }

    private CallApiResponse callApi(String apiId, Map<String, Object> params, String kpiCodeStr, ExecutorRequest executorRequest) throws Exception {
        //获取consumerInfo 这个是在loaderKpi的时候放进去的

        String apiResult = null; // 请求接口返回的数据

        if (StringUtils.isBlank(apiId)) {
            throw new Exception("指标[" + kpiCodeStr + "]接口Id为空");
        }
        log.info("指标[" + kpiCodeStr + "]开始请求接口 apiId : " + apiId);

        ConsumerInfo[] consumerInfo = null;
        String ruleLogId = null;
        Map<String, ApiLog> sourceApiLogMap = null;
        String modelExecutionTypeCode = null;
        if (executorRequest != null) {
            consumerInfo = executorRequest.getConsumerInfos();
            ruleLogId = executorRequest.getRuleLogId();
            sourceApiLogMap = executorRequest.getSourceApiLogMap();
            modelExecutionTypeCode = executorRequest.getModelExecutionType().getCode();
        }

        CallApiResponse callApiResponse = RuleApiFacade.getInstance().callRuleApi(apiId, JSON.toJSONString(params), ruleLogId,
                executorRequest.isLog(),
                modelExecutionTypeCode, sourceApiLogMap, consumerInfo);
        if (callApiResponse != null) {
            apiResult = callApiResponse.getResult();
        }

        log.info("指标[" + kpiCodeStr + "]请求接口的返回值为: " + apiResult);
//
//        if (StringUtils.isBlank(apiResult)) {
//            return null;
//        }
//        return JSONObject.parseObject(apiResult);
        return callApiResponse;
    }


//    public List<Object> getResultFromJson(String jsonString, String key) {
//        List<Object> result = new ArrayList<>();
//        String regex = "\"" + key + "\":(.*?)[,}]";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(jsonString);
//        while (matcher.find()) {
//            result.add(matcher.group(1).replaceAll("\"", ""));
//        }
//        return result;
//    }


}
