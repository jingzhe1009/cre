package com.bonc.framework.rule.log.entity;

import com.bonc.framework.api.log.entity.ConsumerInfo;

import java.util.Date;

public class RuleLog extends ConsumerInfo {

    private String logId;

    private String ruleId;

//    private String ruleName;

    private String folderId;


    private String sourceModelId;

    private String sourceLogId;

    private String executionType;

    private String executionInfo;

    private String state;

    private String hitRuleNum;

    private Date startTime;

    private Date endTime;

    private String inputData;

    private String outputData;

    private String exception;

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public String getOutputData() {
        return outputData;
    }

    public void setOutputData(String outputData) {
        this.outputData = outputData;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getHitRuleNum() {
        return hitRuleNum;
    }

    public void setHitRuleNum(String hitRuleNum) {
        this.hitRuleNum = hitRuleNum;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getSourceModelId() {
        return sourceModelId;
    }

    public void setSourceModelId(String sourceModelId) {
        this.sourceModelId = sourceModelId;
    }

    public String getSourceLogId() {
        return sourceLogId;
    }

    public void setSourceLogId(String sourceLogId) {
        this.sourceLogId = sourceLogId;
    }

    public String getExecutionType() {
        return executionType;
    }

    public void setExecutionType(String executionType) {
        this.executionType = executionType;
    }

    public String getExecutionInfo() {
        return executionInfo;
    }

    public void setExecutionInfo(String executionInfo) {
        this.executionInfo = executionInfo;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RuleLog{");
        sb.append("logId='").append(logId).append('\'');
        sb.append(", ruleId='").append(ruleId).append('\'');
        sb.append(", folderId='").append(folderId).append('\'');
        sb.append(", sourceModelId='").append(sourceModelId).append('\'');
        sb.append(", sourceLogId='").append(sourceLogId).append('\'');
        sb.append(", executionType='").append(executionType).append('\'');
        sb.append(", executionInfo='").append(executionInfo).append('\'');
        sb.append(", state='").append(state).append('\'');
        sb.append(", hitRuleNum='").append(hitRuleNum).append('\'');
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", inputData='").append(inputData).append('\'');
        sb.append(", outputData='").append(outputData).append('\'');
        sb.append(", exception='").append(exception).append('\'');
        sb.append(", consumerId='").append(consumerId).append('\'');
        sb.append(", serverId='").append(serverId).append('\'');
        sb.append(", consumerSeqNo='").append(consumerSeqNo).append('\'');
        sb.append('}');
        return sb.toString();
    }
}