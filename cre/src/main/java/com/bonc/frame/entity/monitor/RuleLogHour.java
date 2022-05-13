package com.bonc.frame.entity.monitor;

public class RuleLogHour {
    // 小时统计日志唯一标识
    private String logHourId;
    // 渠道唯一标识
    private String consumerId;
    // 产品标识
    private String projectNo;
    // 模型FID
    private String modelFid;
    // 模型RID
    private String modelRid;
    // 调用方式
    private String methodType;
    // 模型类型
    private String modelType;
    // 规则模型成功次数
    private int modelSuccessCount;
    // 规则模型失败次数
    private int modelFailCount;
    // 平均评分模型评分
    private int modelAverageScore;
    // 响应码0000-执行成功-次数
    private int successCodeCount;
    // 响应码9998-hbase错误-次数
    private int hbaseCodeCount;
    // 响应码9999-规则引擎执行错误次数
    private int systemCodeCount;
    // 响应码9997-Oracle执行错误-次数
    private int oracleCodeCount;
    // 响应码-9996-红色接口执行错误-次数
    private int redInterCodeCount;
    // 平均响应时间
    private int averageUseTime;
    // 日期标识字段
    private String signTime;

    public String getLogHourId() {
        return logHourId;
    }

    public void setLogHourId(String logHourId) {
        this.logHourId = logHourId;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getProjectNo() {
        return projectNo;
    }

    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }

    public String getModelFid() {
        return modelFid;
    }

    public void setModelFid(String modelFid) {
        this.modelFid = modelFid;
    }

    public String getModelRid() {
        return modelRid;
    }

    public void setModelRid(String modelRid) {
        this.modelRid = modelRid;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public int getModelSuccessCount() {
        return modelSuccessCount;
    }

    public void setModelSuccessCount(int modelSuccessCount) {
        this.modelSuccessCount = modelSuccessCount;
    }

    public int getModelFailCount() {
        return modelFailCount;
    }

    public void setModelFailCount(int modelFailCount) {
        this.modelFailCount = modelFailCount;
    }

    public int getModelAverageScore() {
        return modelAverageScore;
    }

    public void setModelAverageScore(int modelAverageScore) {
        this.modelAverageScore = modelAverageScore;
    }

    public int getSuccessCodeCount() {
        return successCodeCount;
    }

    public void setSuccessCodeCount(int successCodeCount) {
        this.successCodeCount = successCodeCount;
    }

    public int getHbaseCodeCount() {
        return hbaseCodeCount;
    }

    public void setHbaseCodeCount(int hbaseCodeCount) {
        this.hbaseCodeCount = hbaseCodeCount;
    }

    public int getSystemCodeCount() {
        return systemCodeCount;
    }

    public void setSystemCodeCount(int systemCodeCount) {
        this.systemCodeCount = systemCodeCount;
    }

    public int getOracleCodeCount() {
        return oracleCodeCount;
    }

    public void setOracleCodeCount(int oracleCodeCount) {
        this.oracleCodeCount = oracleCodeCount;
    }

    public int getRedInterCodeCount() {
        return redInterCodeCount;
    }

    public void setRedInterCodeCount(int redInterCodeCount) {
        this.redInterCodeCount = redInterCodeCount;
    }

    public int getAverageUseTime() {
        return averageUseTime;
    }

    public void setAverageUseTime(int averageUseTime) {
        this.averageUseTime = averageUseTime;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }

    public RuleLogHour(String logHourId, String consumerId, String projectNo, String modelFid, String modelRid, String methodType, String modelType, int modelSuccessCount, int modelFailCount, int modelAverageScore, int successCodeCount, int hbaseCodeCount, int systemCodeCount, int oracleCodeCount, int redInterCodeCount, int averageUseTime, String signTime) {

        this.logHourId = logHourId;
        this.consumerId = consumerId;
        this.projectNo = projectNo;
        this.modelFid = modelFid;
        this.modelRid = modelRid;
        this.methodType = methodType;
        this.modelType = modelType;
        this.modelSuccessCount = modelSuccessCount;
        this.modelFailCount = modelFailCount;
        this.modelAverageScore = modelAverageScore;
        this.successCodeCount = successCodeCount;
        this.hbaseCodeCount = hbaseCodeCount;
        this.systemCodeCount = systemCodeCount;
        this.oracleCodeCount = oracleCodeCount;
        this.redInterCodeCount = redInterCodeCount;
        this.averageUseTime = averageUseTime;
        this.signTime = signTime;
    }

    public RuleLogHour() {

    }
}
