package com.bonc.frame.entity.apilog;

import java.util.Date;

/**
 * @author yedunyao
 * @date 2019/5/8 16:31
 */
public class ApiLog {

    private String logId;

    private String apiId;

    private String logContent;

    private Date logOccurTime;

    private String callResult;

    private String logParam;

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getLogContent() {
        return logContent;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }

    public Date getLogOccurTime() {
        return logOccurTime;
    }

    public void setLogOccurTime(Date logOccurTime) {
        this.logOccurTime = logOccurTime;
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
}
