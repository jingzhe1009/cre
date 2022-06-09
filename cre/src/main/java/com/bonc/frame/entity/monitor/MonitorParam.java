package com.bonc.frame.entity.monitor;


public class MonitorParam {

	//产品id
	private String productId;

	//渠道id
	private String channelId;

	//模型id
	private String modelId;

	//调用方式
	private String type;

	//年1月2日3
	private String cycleId;

	// 状态码
	private String statusCode;

	// 开始时间
	private String startDate;

	// 结束时间
	private String endDate;
	// 当前页
	private String start;
	// 每页数量
	private String length;

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getLength() {
		return length;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getLangth() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCycleId() {
		return cycleId;
	}

	public void setCycleId(String cycleId) {
		this.cycleId = cycleId;
	}

	public MonitorParam(String cycleId) {
		this.cycleId = cycleId;
	}

	public MonitorParam() {
	}
}
