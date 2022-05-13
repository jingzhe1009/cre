package com.bonc.frame.entity.monitor;

public class ExcuteResult {
	
	//执行结果
	private String excuteResult;
	//状态码
	private String state;
	//执行次数
	private String excuteTimes;


	public ExcuteResult(String excuteResult, String state, String excuteTimes) {
		this.excuteResult = excuteResult;
		this.state = state;
		this.excuteTimes = excuteTimes;
	}

	public String getExcuteResult() {
		return excuteResult;
	}
	public void setExcuteResult(String excuteResult) {
		this.excuteResult = excuteResult;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getExcuteTimes() {
		return excuteTimes;
	}
	public void setExcuteTimes(String excuteTimes) {
		this.excuteTimes = excuteTimes;
	}

}
