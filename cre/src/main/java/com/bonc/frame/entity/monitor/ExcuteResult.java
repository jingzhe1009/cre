package com.bonc.frame.entity.monitor;

public class ExcuteResult {

	//执行结果
	private String excuteResult;
	//状态码
	private String statusCode;
	//执行次数
	private String excuteTimes;
	// 日志id
	private String logId;
	// 机构名称
	private String deptName;
	// 渠道名称
	private String channelName;
	// 产品名称
	private String groupName;
	// 模型名称
	private String modelName;
	// 调用方式
	private String methodType;
	// 流水号
	private String consumerSeqNo;
	// 执行状态
	private String returnCode;
	// 执行时间
	private String startTime;
	// 响应时间
	private String endTime;

	public String getLogId() {
		return logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
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

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
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

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public ExcuteResult(String excuteResult, String statusCode, String excuteTimes, String logId, String deptName, String channelName, String groupName, String modelName, String methodType, String consumerSeqNo, String returnCode, String startTime, String endTime) {
		this.excuteResult = excuteResult;
		this.statusCode = statusCode;
		this.excuteTimes = excuteTimes;
		this.logId = logId;
		this.deptName = deptName;
		this.channelName = channelName;
		this.groupName = groupName;
		this.modelName = modelName;
		this.methodType = methodType;
		this.consumerSeqNo = consumerSeqNo;
		this.returnCode = returnCode;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public ExcuteResult() {

	}

	public ExcuteResult(String excuteResult, String statusCode, String excuteTimes) {
		this.excuteResult = excuteResult;
		this.statusCode = statusCode;
		this.excuteTimes = excuteTimes;
	}

	public String getExcuteResult() {
		return excuteResult;
	}
	public void setExcuteResult(String excuteResult) {
		this.excuteResult = excuteResult;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getExcuteTimes() {
		return excuteTimes;
	}
	public void setExcuteTimes(String excuteTimes) {
		this.excuteTimes = excuteTimes;
	}

}
