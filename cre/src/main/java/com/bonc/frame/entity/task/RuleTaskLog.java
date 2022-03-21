package com.bonc.frame.entity.task;

import com.bonc.frame.module.task.ruleTask.RuleTaskJobInfo;

import java.util.Date;

/**
 * @author yedunyao
 * @date 2019/6/13 15:26
 */
public class RuleTaskLog {

    /**
     * 0，排队中；1，执行中；2，完成；-1，异常
     */
    public static final String IN_QUEUE = "0";
    public static final String RUNNING = "1";
    public static final String FINISH = "2";
    public static final String EXCEPTION = "-1";

    private String finalKey;

    private String taskId;

    private String ruleTaskId;

    private String status;

    private String message;

    private Date createDate;

    private Date updateDate;

    public RuleTaskLog() {
    }

    public RuleTaskLog(RuleTaskJobInfo jobInfo) {
        finalKey = jobInfo.getFinalKey();
        taskId = jobInfo.getTaskId();
        ruleTaskId = String.valueOf(jobInfo.getJobKey());
    }

    public String getFinalKey() {
        return finalKey;
    }

    public void setFinalKey(String finalKey) {
        this.finalKey = finalKey;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getRuleTaskId() {
        return ruleTaskId;
    }

    public void setRuleTaskId(String ruleTaskId) {
        this.ruleTaskId = ruleTaskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "RuleTaskLog{" +
                "finalKey='" + finalKey + '\'' +
                ", taskId='" + taskId + '\'' +
                ", ruleTaskId='" + ruleTaskId + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                '}';
    }
}
