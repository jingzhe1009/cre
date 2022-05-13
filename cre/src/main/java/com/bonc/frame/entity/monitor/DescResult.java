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
	private String productId;
	//模型名称
	private String modelName;
	//产品名称
	private String productName;
	//调用方式
	private String type;
	//交易流水号
	private String consumerSeqNo;
	//模型版本号
	private String modelVersion;
	//执行时间
	private String actionTime;
	//执行状态
	private String actionStatus;
	//响应时间
	private String  useTime;
	/**
	 * 外部数据库执行情况执行情况
	 */
	private String logId;
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

	/**
	 * 新增内容
	 *
	 * */
	//响应码
	private String returnCode;
	//模型类型
	private String modelType;
    //规则模型结果
	private String returnResult;
	//评分模型评分
	private String returnScore;
	//产品标示
	private String projectCode;
	//平均调用时间
	private String averageUseTime;


	public String getLogId() {
		return logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

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

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getActionTime() {
		return actionTime;
	}

	public void setActionTime(String actionTime) {
		this.actionTime = actionTime;
	}

	public String getActionStatus() {
		return actionStatus;
	}

	public void setActionStatus(String actionStatus) {
		this.actionStatus = actionStatus;
	}

	public String getUseTime() {
		return useTime;
	}

	public void setUseTime(String useTime) {
		this.useTime = useTime;
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

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
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

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public String getAverageUseTime() {
		return averageUseTime;
	}

	public void setAverageUseTime(String averageUseTime) {
		this.averageUseTime = averageUseTime;
	}

	public DescResult(String logId, String deptId, String channelId, String deptName, String channelName, String modelId,
					  String productId, String modelName, String productName, String type, String consumerSeqNo, String modelVersion,
					  String actionTime, String actionStatus, String useTime, String oracleIn, String hbaseIn,
					  String oracleOut, String hbaseOut, String apiName, String apiIn, String apiOut, String returnCode, String modelType,
					  String returnResult, String returnScore, String projectCode, String averageUseTime) {
		super();
		this.logId = logId;
		this.deptId = deptId;
		this.channelId = channelId;
		this.deptName = deptName;
		this.channelName = channelName;
		this.modelId = modelId;
		this.productId = productId;
		this.modelName = modelName;
		this.productName = productName;
		this.type = type;
		this.consumerSeqNo = consumerSeqNo;
		this.modelVersion = modelVersion;
		this.actionTime = actionTime;
		this.actionStatus = actionStatus;
		this.useTime = useTime;
		this.oracleIn = oracleIn;
		this.hbaseIn = hbaseIn;
		this.oracleOut = oracleOut;
		this.hbaseOut = hbaseOut;
		this.apiName = apiName;
		this.apiIn = apiIn;
		this.apiOut = apiOut;
		this.returnCode = returnCode;
		this.returnResult = returnResult;
		this.returnScore = returnScore;
		this.projectCode = projectCode;
		this.modelType = modelType;
		this.averageUseTime = averageUseTime;
	}

	public DescResult() {
	}
}
