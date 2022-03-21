package com.bonc.framework.api.log.entity;

import com.google.common.base.Objects;

import java.util.Date;

public class ApiLog extends ConsumerInfo {

    private String apiId;

    private String logId;

    private String ruleLogId;

    private String sourceLogId;

    private String logContent;

    private Date logOccurtime;

    private String callResult;

    private String logParam;


    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getLogContent() {
        return logContent;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }

    public Date getLogOccurtime() {
        return logOccurtime;
    }

    public void setLogOccurtime(Date logOccurtime) {
        this.logOccurtime = logOccurtime;
    }

    public String getCallResult() {
        return callResult;
    }

    public void setCallResult(String callResult) {
        this.callResult = callResult;
    }

    public String getLogParam() {
        return logParam;
    }

    public void setLogParam(String logParam) {
        this.logParam = logParam;
    }

    public String getRuleLogId() {
        return ruleLogId;
    }

    public void setRuleLogId(String ruleLogId) {
        this.ruleLogId = ruleLogId;
    }

    public String getSourceLogId() {
        return sourceLogId;
    }

    public void setSourceLogId(String sourceLogId) {
        this.sourceLogId = sourceLogId;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("apiId", apiId)
                .add("logId", logId)
                .add("ruleLogId", ruleLogId)
                .add("sourceLogId", sourceLogId)
                .add("logContent", logContent)
                .add("logOccurtime", logOccurtime)
                .add("callResult", callResult)
                .add("logParam", logParam)
                .add("consumerId", consumerId)
                .add("serverId", serverId)
                .add("consumerSeqNo", consumerSeqNo)
                .toString();
    }
}