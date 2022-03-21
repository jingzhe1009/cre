package com.bonc.frame.entity.rulelog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RuleLog {
	private String logId;

	private String ruleId;

	private String folderId;

	private String state;

	private String hitRuleNum;

	private Date startTime;

	private Date endTime;

//	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

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

	public String getHitRuleNum() {
		return hitRuleNum;
	}

	public void setHitRuleNum(String hitRuleNum) {
		this.hitRuleNum = hitRuleNum;
	}

	public Date getStartTime() {
		return startTime;
	}

	//获取毫秒字符串
	/*public String getStartTimeStr() {
		String startTimeStr = null;
		if (startTime != null) {
			startTimeStr = sdf.format(startTime);
		}
		return startTimeStr;
	}*/

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	//获取毫秒字符串
	/*public String getEndTimeStr() {
		String endTimeStr = null;
		if (endTime != null) {
			endTimeStr = sdf.format(endTime);
		}
		return endTimeStr;
	}*/

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}