package com.bonc.framework.rule.kpi;

import com.alibaba.fastjson.JSON;
import com.bonc.framework.util.IdUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/9/9 10:48
 */
public class KpiLog implements Serializable {

    private static final long serialVersionUID = -4551787029187642477L;

    private String logId;
    private String sourceLogId;
    private String ruleLogId;
    private Map<String, Object> inputData;
    private Map<String, Object> outputData;
    private String kpiId;
    private String logOccurtime;
    private String state;
    private String logContent;

    public KpiLog() {
    }

    public KpiLog(String kpiId, String ruleLogId, String sourceLogId, Map<String, Object> inputData, Map<String, Object> outputData, String state, String logContent) {
        this.logId = IdUtil.createId();
        this.kpiId = kpiId;
        this.ruleLogId = ruleLogId;
        this.sourceLogId = sourceLogId;
        this.inputData = inputData;
        this.outputData = outputData;
        this.logOccurtime = new Date().toString();
        this.state = state;
        this.logContent = logContent;
    }

//    public static KpiLog newSuccessKpiLogLog(String kpiId, String ruleLogId, Map<String, Object> inputData, Map<String, Object> outputData) {
//        return new KpiLog(kpiId, ruleLogId, inputData, outputData, LogState.STATE_END, null);
//    }
//
//    public static KpiLog newExceptionKpiLogLog(String kpiId, String ruleLogId, Map<String, Object> inputData, String logContent) {
//        return new KpiLog(kpiId, ruleLogId, inputData, null, LogState.STATE_EXCEPTION, logContent);
//    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getSourceLogId() {
        return sourceLogId;
    }

    public void setSourceLogId(String sourceLogId) {
        this.sourceLogId = sourceLogId;
    }

    public String getRuleLogId() {
        return ruleLogId;
    }

    public void setRuleLogId(String ruleLogId) {
        this.ruleLogId = ruleLogId;
    }

    public Map<String, Object> getInputData() {
        return inputData;
    }

    public void setInputData(Map<String, Object> inputData) {
        this.inputData = inputData;
    }

    public Map<String, Object> getOutputData() {
        return outputData;
    }

    public void setOutputData(Map<String, Object> outputData) {
        this.outputData = outputData;
    }

    public String getKpiId() {
        return kpiId;
    }

    public void setKpiId(String kpiId) {
        this.kpiId = kpiId;
    }

    public String getLogOccurtime() {
        return logOccurtime;
    }

    public void setLogOccurtime(String logOccurtime) {
        this.logOccurtime = logOccurtime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLogContent() {
        return logContent;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
