package com.bonc.frame.module.aBTest.metric;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/9/25 17:49
 */
public class ABTestMetricJobInfo implements Serializable{

    private static final long serialVersionUID = -282465682696213198L;
    private String abTestId;
    private String executeLogId;
    private String message;
    private boolean success;

    public ABTestMetricJobInfo() {
    }

    public ABTestMetricJobInfo(String abTestId, String executeLogId, boolean success) {
        this.abTestId = abTestId;
        this.executeLogId = executeLogId;
        this.success = success;
    }

    public String createKey() {
        return JSON.toJSONString(this);
    }

    public String getAbTestId() {
        return abTestId;
    }

    public void setAbTestId(String abTestId) {
        this.abTestId = abTestId;
    }

    public String getExecuteLogId() {
        return executeLogId;
    }

    public void setExecuteLogId(String executeLogId) {
        this.executeLogId = executeLogId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ABTestMetricJobInfo{");
        sb.append("abTestId='").append(abTestId).append('\'');
        sb.append(", executeLogId='").append(executeLogId).append('\'');
        sb.append(", message='").append(message).append('\'');
        sb.append(", success=").append(success);
        sb.append('}');
        return sb.toString();
    }
}
