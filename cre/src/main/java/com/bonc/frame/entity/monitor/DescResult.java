package com.bonc.frame.entity.monitor;

public class DescResult {

	/**
	 * 模型执行情况
	 */
	//归属行ID
	private String deptId;
	//调用渠道ID
	private String channelId;
	//归属行名称
	private String deptName;
	//调用渠道名称
	private String channelName;
	//模型id
	private String modelId;
	//产品id
	private String groupId;
	//模型名称
	private String modelName;
	//产品名称
	private String groupName;
	//调用方式
	private String methodType;
	//交易流水号
	private String consumerSeqNo;
	//模型版本号
	private String modelVersion;
	//执行时间
	private String startTime;
	//执行状态
	private String returnCode;
	//响应时间
	private String endTime;
	/**
	 * 外部数据库执行情况执行情况
	 */
	//oracle入参
	private String oracleIn;
	//hbase入参
	private String hbaseIn;
	//oracle出参
	private String oracleOut;
	//hbase出参
	private String hbaseOut;
	/**
	 * 红色执行情况
	 */
	//红色接口名称
	private String apiName;
	//接口入参
	private String apiIn;
	//接口出参
	private String apiOut;

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getMethodType() {
		return methodType;
	}

	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}

	public String getConsumerSeqNo() {
		return consumerSeqNo;
	}

	public void setConsumerSeqNo(String consumerSeqNo) {
		this.consumerSeqNo = consumerSeqNo;
	}

	public String getModelVersion() {
		return modelVersion;
	}

	public void setModelVersion(String modelVersion) {
		this.modelVersion = modelVersion;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getOracleIn() {
		return oracleIn;
	}

	public void setOracleIn(String oracleIn) {
		this.oracleIn = oracleIn;
	}

	public String getHbaseIn() {
		return hbaseIn;
	}

	public void setHbaseIn(String hbaseIn) {
		this.hbaseIn = hbaseIn;
	}

	public String getOracleOut() {
		return oracleOut;
	}

	public void setOracleOut(String oracleOut) {
		this.oracleOut = oracleOut;
	}

	public String getHbaseOut() {
		return hbaseOut;
	}

	public void setHbaseOut(String hbaseOut) {
		this.hbaseOut = hbaseOut;
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public String getApiIn() {
		return apiIn;
	}

	public void setApiIn(String apiIn) {
		this.apiIn = apiIn;
	}

	public String getApiOut() {
		return apiOut;
	}

	public void setApiOut(String apiOut) {
		this.apiOut = apiOut;
	}

	public DescResult(String deptId, String channelId, String deptName, String channelName, String modelId, String groupId, String modelName, String groupName, String methodType, String consumerSeqNo, String modelVersion, String startTime, String returnCode, String endTime, String oracleIn, String hbaseIn, String oracleOut, String hbaseOut, String apiName, String apiIn, String apiOut) {

		this.deptId = deptId;
		this.channelId = channelId;
		this.deptName = deptName;
		this.channelName = channelName;
		this.modelId = modelId;
		this.groupId = groupId;
		this.modelName = modelName;
		this.groupName = groupName;
		this.methodType = methodType;
		this.consumerSeqNo = consumerSeqNo;
		this.modelVersion = modelVersion;
		this.startTime = startTime;
		this.returnCode = returnCode;
		this.endTime = endTime;
		this.oracleIn = oracleIn;
		this.hbaseIn = hbaseIn;
		this.oracleOut = oracleOut;
		this.hbaseOut = hbaseOut;
		this.apiName = apiName;
		this.apiIn = apiIn;
		this.apiOut = apiOut;
	}

	public DescResult() {

	}
}
