package com.bonc.frame.entity.monitor;

public class IndexParam {
	//当日调用情况 -成功次数
	private String currentDaySucTimes;
	//当日调用情况 -失败次数
	private String currentDayFailTimes;
	//当日执行情况-响应时长
	private String currentDayResponseTime;
	//产品统计
	private String productCount;
	//渠道统计
	private String channelCount;
	//统计模型
	private String countModel;
	//评分模型
	private String scoreModel;
	//规则模型
	private String ruleModel;
	//规则集统计
	private String ruleSetCount;
	//指标统计
	private String kpiCount;
	public String getCurrentDaySucTimes() {
		return currentDaySucTimes;
	}
	public void setCurrentDaySucTimes(String currentDaySucTimes) {
		this.currentDaySucTimes = currentDaySucTimes;
	}
	public String getCurrentDayFailTimes() {
		return currentDayFailTimes;
	}
	public void setCurrentDayFailTimes(String currentDayFailTimes) {
		this.currentDayFailTimes = currentDayFailTimes;
	}
	public String getCurrentDayResponseTime() {
		return currentDayResponseTime;
	}
	public void setCurrentDayResponseTime(String currentDayResponseTime) {
		this.currentDayResponseTime = currentDayResponseTime;
	}
	public String getProductCount() {
		return productCount;
	}
	public void setProductCount(String productCount) {
		this.productCount = productCount;
	}
	public String getChannelCount() {
		return channelCount;
	}
	public void setChannelCount(String channelCount) {
		this.channelCount = channelCount;
	}
	public String getCountModel() {
		return countModel;
	}
	public void setCountModel(String countModel) {
		this.countModel = countModel;
	}
	public String getScoreModel() {
		return scoreModel;
	}
	public void setScoreModel(String scoreModel) {
		this.scoreModel = scoreModel;
	}
	public String getRuleModel() {
		return ruleModel;
	}
	public void setRuleModel(String ruleModel) {
		this.ruleModel = ruleModel;
	}
	public String getRuleSetCount() {
		return ruleSetCount;
	}
	public void setRuleSetCount(String ruleSetCount) {
		this.ruleSetCount = ruleSetCount;
	}
	public String getKpiCount() {
		return kpiCount;
	}
	public void setKpiCount(String kpiCount) {
		this.kpiCount = kpiCount;
	}
	
	

}
