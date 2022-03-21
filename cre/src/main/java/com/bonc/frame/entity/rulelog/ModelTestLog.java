package com.bonc.frame.entity.rulelog;

import java.io.Serializable;
import java.util.Date;

/**
 * 试算历史
 *
 * @author yedunyao
 * @date 2019/11/10 16:56
 */
public class ModelTestLog implements Serializable {

    private static final long serialVersionUID = -1070861454367160780L;

    private String logId;

    /**
     * 模型执行日志的Id
     * EXECUTOR_RULE_LOG_ID
     */
    private String executorRuleLogId;

    private String modelId;

    private String modelName;

    private String modelVersion;

    /**
     * 操作类型
     * 1，试算
     * <p>
     * 当前系统只有试算
     */
    private String operateType;

    /**
     * 试算的请求参数
     */
    private String operateContent;

    /**
     * 试算是否成功：
     * 1，成功；
     * 0，失败
     */
    private String operateIsSuccess;

    /**
     * 试算结果
     * 即模型执行后的返回结果或异常信息
     */
    private String operateResult;

    private Date operateTime;

    private String operatePerson;


    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getOperateContent() {
        return operateContent;
    }

    public void setOperateContent(String operateContent) {
        this.operateContent = operateContent;
    }

    public String getOperateIsSuccess() {
        return operateIsSuccess;
    }

    public void setOperateIsSuccess(String operateIsSuccess) {
        this.operateIsSuccess = operateIsSuccess;
    }

    public String getOperateResult() {
        return operateResult;
    }

    public void setOperateResult(String operateResult) {
        this.operateResult = operateResult;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getOperatePerson() {
        return operatePerson;
    }

    public void setOperatePerson(String operatePerson) {
        this.operatePerson = operatePerson;
    }

    public String getExecutorRuleLogId() {
        return executorRuleLogId;
    }

    public void setExecutorRuleLogId(String executorRuleLogId) {
        this.executorRuleLogId = executorRuleLogId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ModelTestLog{");
        sb.append("logId='").append(logId).append('\'');
        sb.append(", executorRuleLogId='").append(executorRuleLogId).append('\'');
        sb.append(", modelId='").append(modelId).append('\'');
        sb.append(", modelName='").append(modelName).append('\'');
        sb.append(", modelVersion='").append(modelVersion).append('\'');
        sb.append(", operateType='").append(operateType).append('\'');
        sb.append(", operateContent='").append(operateContent).append('\'');
        sb.append(", operateIsSuccess='").append(operateIsSuccess).append('\'');
        sb.append(", operateResult='").append(operateResult).append('\'');
        sb.append(", operateTime=").append(operateTime);
        sb.append(", operatePerson='").append(operatePerson).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
