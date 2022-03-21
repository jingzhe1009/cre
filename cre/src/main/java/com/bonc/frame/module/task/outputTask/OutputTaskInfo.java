package com.bonc.frame.module.task.outputTask;

import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.module.task.TaskInfo;

import java.io.Serializable;

/**
 * @author yedunyao
 * @date 2019/7/12 14:21
 */
public class OutputTaskInfo<T> extends TaskInfo<T> implements Serializable {

    private static final long serialVersionUID = -4763804368533668840L;

    // 数据源信息
    private final DataSource dataSource;


    public OutputTaskInfo(String finalKey, String taskId, Object jobKey,
                          DataSource dataSource, T data) {
        this.finalKey = finalKey;
        this.taskId = taskId;
        this.jobKey = jobKey;
        this.dataSource = dataSource;
        this.data = data;
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

    public DataSource getDataSource() {
        return dataSource;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "OutputTaskInfo{" +
                "finalKey='" + finalKey + '\'' +
                ", taskId='" + taskId + '\'' +
                ", jobKey=" + jobKey +
                ", dataSource=" + dataSource +
                '}';
    }
}
