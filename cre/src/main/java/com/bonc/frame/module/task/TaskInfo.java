package com.bonc.frame.module.task;

import java.io.Serializable;

/**
 * @author yedunyao
 * @date 2019/7/12 15:34
 */
public abstract class TaskInfo<T> implements Serializable {

    private static final long serialVersionUID = 2851813245839913166L;

    protected String taskId;

    // 每个子任务的唯一值
    protected Object jobKey;

    protected String finalKey;

    protected T data;

    protected String buildKey() {
        return taskId + "_" + String.valueOf(jobKey);
    }

    public String getFinalKey() {
        return finalKey;
    }

    public String getTaskId() {
        return taskId;
    }

    public Object getJobKey() {
        return jobKey;
    }

    public T getData() {
        return data;
    }

}
