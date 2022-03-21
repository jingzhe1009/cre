package com.bonc.framework.api.entity;

import com.alibaba.fastjson.JSON;
import com.bonc.framework.api.log.entity.ApiLog;
import com.bonc.framework.api.log.entity.ConsumerInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @since 2019/12/9 10:56
 */
public class CallApiRequest {

    public static final String MODEL_EXECUTION_TASK = "1";
    public static final String MODEL_EXECUTION_TRIAL = "2";
    public static final String MODEL_EXECUTION_TEST = "3";
    public static final String MODEL_EXECUTION_ABTEST = "4";
    public static final String MODEL_EXECUTION_WS_INTERFACE = "5";

    /**
     * 接口Id
     */
    private String apiId;

    /**
     * 模型执行日志的Id
     */
    private String ruleLogId;
    /**
     * 模型执行类型
     */
    private String modelExecutionType;
    /**
     * 传入的参数
     */
    private Map<String, Object> param;
    /**
     * 如果是A/B测试 , 会有这个参数
     */
    Map<String, ApiLog> sourceApiLogMap;

    /**
     * 调用的渠道信息
     */
    private ConsumerInfo[] consumerInfos;

    private boolean isLog;

    public CallApiRequest() {
    }

    public CallApiResponse newCallApiResponse() {
        CallApiResponse response = new CallApiResponse();
        response.setRuleLogId(ruleLogId);
        response.setParam(param);
        return response;
    }

    public void addSourceApiLog(ApiLog sourceApiLog) {
        if (sourceApiLog == null) {
            return;
        }
        if (sourceApiLogMap == null) {
            this.sourceApiLogMap = new HashMap<>();
        }
        String key = sourceApiLog.getApiId() + "_" + JSON.toJSONString(sourceApiLog.getLogParam());
        this.sourceApiLogMap.put(key, sourceApiLog);
    }

    public void addSourceApiLogList(List<ApiLog> sourceApiLogList) {
        if (sourceApiLogList == null || sourceApiLogList.isEmpty()) {
            return;
        }
        for (ApiLog kpiLog : sourceApiLogList) {
            addSourceApiLog(kpiLog);
        }
    }

    public ApiLog getSourceApiLog(String apiId, Map<String, Object> param) {
        if (sourceApiLogMap != null) {
            String key = apiId + "_" + JSON.toJSONString(param);
            return sourceApiLogMap.get(key);
        }
        return null;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getRuleLogId() {
        return ruleLogId;
    }

    public void setRuleLogId(String ruleLogId) {
        this.ruleLogId = ruleLogId;
    }

    public String getModelExecutionType() {
        return modelExecutionType;
    }

    public void setModelExecutionType(String modelExecutionType) {
        this.modelExecutionType = modelExecutionType;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public ConsumerInfo[] getConsumerInfos() {
        return consumerInfos;
    }

    public void setConsumerInfos(ConsumerInfo[] consumerInfos) {
        this.consumerInfos = consumerInfos;
    }

    public Map<String, ApiLog> getSourceApiLogMap() {
        return sourceApiLogMap;
    }

    public void setSourceApiLogMap(Map<String, ApiLog> sourceApiLogMap) {
        this.sourceApiLogMap = sourceApiLogMap;
    }

    public boolean isLog() {
        return isLog;
    }

    public void setLog(boolean log) {
        isLog = log;
    }
}
