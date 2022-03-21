package com.bonc.framework.api.log;

import com.bonc.framework.api.log.entity.ApiLog;

import java.util.Map;

public interface IApiLog {

	/**
	 * 记录api执行信息
	 */
	void recordRuleLog(Map<String, Object> map,boolean isLog);

    void recordRuleLog(ApiLog apiLog, boolean isLog);
	
}
