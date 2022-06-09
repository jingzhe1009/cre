package com.bonc.frame.entity.monitor;

import java.util.Date;

/**
 * 模型执行日志表实例
 */
public class RuleLog {
    // 日志唯一标识
    private String logId;
    // 模型唯一标识
    private String ruleId;
    // 场景id-产品id
    private String folderId;
    // 执行状态-2正常结束，1异常
    private String state;
    // 命中的规则个数
    private String hitRuleNumber;
    // 执行开始时间
    private Date startTime;
    // 执行结束时间
    private Date endTime;
    // 输入数据
    private String inputData;
    // 输出数据
    private String outputData;
    // 异常信息
    private String exception;
    // 渠道唯一标识
    private String consumerId;
    // 流水号
    private String consumerSeqNo;
    // 响应时间（毫秒值）
    private int useTime;
    // 响应码
    private String returnCode;
    // 模型版本号
    private String modelVersion;
    // 模型类型 1规则模型，2评分模型
    private String modelType;
    // 调用方式 1:测试  2:试算  3:A/B测试  4:离线任务  5:外部服务调用(ws接口)
    private String methodType;
    // 规则模型结果-0通过，1拒绝
    private String returnResult;
    // 评分模型结果
    private String returnScore;
    // 产品标识
    private String projectNo;

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

    public String getHitRuleNumber() {
        return hitRuleNumber;
    }

    public void setHitRuleNumber(String hitRuleNumber) {
        this.hitRuleNumber = hitRuleNumber;
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

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getConsumerSeqNo() {
        return consumerSeqNo;
    }

    public void setConsumerSeqNo(String consumerSeqNo) {
        this.consumerSeqNo = consumerSeqNo;
    }

    public int getUseTime() {
        return useTime;
    }

    public void setUseTime(int useTime) {
        this.useTime = useTime;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public String getReturnResult() {
        return returnResult;
    }

    public void setReturnResult(String returnResult) {
        this.returnResult = returnResult;
    }

    public String getReturnScore() {
        return returnScore;
    }

    public void setReturnScore(String returnScore) {
        this.returnScore = returnScore;
    }

    public String getProjectNo() {
        return projectNo;
    }

    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }

    public RuleLog(String logId, String ruleId, String folderId, String state, String hitRuleNumber, Date startTime, Date endTime, String inputData, String outputData, String exception, String consumerId, String consumerSeqNo, int useTime, String returnCode, String modelVersion, String modelType, String methodType, String returnResult, String returnScore, String projectNo) {
        this.logId = logId;
        this.ruleId = ruleId;
        this.folderId = folderId;
        this.state = state;
        this.hitRuleNumber = hitRuleNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.inputData = inputData;
        this.outputData = outputData;
        this.exception = exception;
        this.consumerId = consumerId;
        this.consumerSeqNo = consumerSeqNo;
        this.useTime = useTime;
        this.returnCode = returnCode;
        this.modelVersion = modelVersion;
        this.modelType = modelType;
        this.methodType = methodType;
        this.returnResult = returnResult;
        this.returnScore = returnScore;
        this.projectNo = projectNo;
    }

    public RuleLog() {

    }
}
