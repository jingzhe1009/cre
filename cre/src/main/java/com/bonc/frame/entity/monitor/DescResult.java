package com.bonc.frame.entity.monitor;

public class DescResult {
	
	/**
	 * 模型执行情况
	 */
	//归属行ID
	private String bankId;
	//调用渠道ID
	private String channelId;
	//归属行名称
	private String bankName;
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
	private String seriId;
	//模型版本号
	private String modelVersion;
	//执行时间
	private String actionTime;
	//执行状态
	private String actionStatus;
	//响应时间
	private String reponseTime;
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
	public String getBankId() {
		return bankId;
	}
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
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
	public String getSeriId() {
		return seriId;
	}
	public void setSeriId(String seriId) {
		this.seriId = seriId;
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
	public String getReponseTime() {
		return reponseTime;
	}
	public void setReponseTime(String reponseTime) {
		this.reponseTime = reponseTime;
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
	public DescResult(String bankId, String channelId, String bankName, String channelName, String modelId,
			String productId, String modelName, String productName, String type, String seriId, String modelVersion,
			String actionTime, String actionStatus, String reponseTime, String oracleIn, String hbaseIn,
			String oracleOut, String hbaseOut, String apiName, String apiIn, String apiOut) {
		super();
		this.bankId = bankId;
		this.channelId = channelId;
		this.bankName = bankName;
		this.channelName = channelName;
		this.modelId = modelId;
		this.productId = productId;
		this.modelName = modelName;
		this.productName = productName;
		this.type = type;
		this.seriId = seriId;
		this.modelVersion = modelVersion;
		this.actionTime = actionTime;
		this.actionStatus = actionStatus;
		this.reponseTime = reponseTime;
		this.oracleIn = oracleIn;
		this.hbaseIn = hbaseIn;
		this.oracleOut = oracleOut;
		this.hbaseOut = hbaseOut;
		this.apiName = apiName;
		this.apiIn = apiIn;
		this.apiOut = apiOut;
	}
	
	

}
