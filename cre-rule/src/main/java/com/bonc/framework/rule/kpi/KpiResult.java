package com.bonc.framework.rule.kpi;

import com.bonc.framework.rule.executor.entity.kpi.KpiDefinition;
import com.bonc.framework.rule.log.LogState;

import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/9/8 16:20
 */
public class KpiResult extends KpiDefinition {

    private static final long serialVersionUID = -1913842079567669553L;

    private Map<String, Object> inputData;
    private Map<String, Object> outputData;
    private String state;
    private String sourceLogId;
    private KpiLog kpiLog;

    public KpiResult() {
    }

    public void setSuccessLog(String ruleLogId, String sourceLogId, Map<String, Object> inputData, Map<String, Object> outputData) {
        this.kpiLog = new KpiLog(kpiId, ruleLogId, sourceLogId, inputData, outputData, LogState.STATE_END, null);
        this.inputData = inputData;
        this.outputData = outputData;
        this.sourceLogId = sourceLogId;
        this.state = LogState.STATE_END;
    }

    public void setExceptionLog(String ruleLogId, String sourceLogId, Map<String, Object> inputData, String logContent) {
        this.kpiLog = new KpiLog(kpiId, ruleLogId, sourceLogId, inputData, null, LogState.STATE_EXCEPTION, logContent);
        this.inputData = inputData;
        this.state = LogState.STATE_EXCEPTION;
    }

    public KpiLog getKpiLog() {
        return kpiLog;
    }

    public void setKpiLog(KpiLog kpiLog) {
        this.kpiLog = kpiLog;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSourceLogId() {
        return sourceLogId;
    }

    public void setSourceLogId(String sourceLogId) {
        this.sourceLogId = sourceLogId;
    }
}
