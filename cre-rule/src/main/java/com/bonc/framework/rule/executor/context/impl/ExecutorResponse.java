package com.bonc.framework.rule.executor.context.impl;


import com.bonc.framework.rule.log.entity.TraceModelLogDetail;

import java.util.Map;

/**
 * @author yedunyao
 * @since 2019/12/9 10:56
 */
public class ExecutorResponse {

    /**
     * 是否执行成功
     */
    private boolean successed = true;

    /**
     * 结果描述，多为失败原因的描述
     */
    private String message;

    /**
     * 执行用时（ms）
     */
    private long usedTime;

    /**
     * 模型执行的输出结果
     */
    private Map<String, Object> result;

    /**
     * 模型执行的详细过程，每个节点的执行情况
     */
    private TraceModelLogDetail executorDetail;

    /**
     * 执行日志的id
     */
    private String executorLogId;

    public boolean isSuccessed() {
        return successed;
    }

    public void setSuccessed(boolean successed) {
        this.successed = successed;
    }

    public long getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(long usedTime) {
        this.usedTime = usedTime;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

    public TraceModelLogDetail getExecutorDetail() {
        return executorDetail;
    }

    public void setExecutorDetail(TraceModelLogDetail executorDetail) {
        this.executorDetail = executorDetail;
    }

    public String getExecutorLogId() {
        return executorLogId;
    }

    public void setExecutorLogId(String executorLogId) {
        this.executorLogId = executorLogId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ExecutorResponse newFailedExecutorResponse(String message) {
        ExecutorResponse executorResponse = new ExecutorResponse();
        executorResponse.setMessage(message);
        executorResponse.setSuccessed(false);
        return executorResponse;
    }

}
