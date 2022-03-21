package com.bonc.frame.ws.service.rule;

import com.bonc.frame.util.ResponseResult;
import com.bonc.framework.api.log.entity.ConsumerInfo;

/**
 * 
 * @author qxl
 * @date 2018年5月9日 下午3:59:48
 * @version 1.0
 */
public interface WsRuleService {

	/**
	 * 执行规则
	 * @param folderId
	 * @param ruleId
	 * @param paramStr
	 * @return
	 */
    ResponseResult executeRule(String folderId, String ruleId, String paramStr,
                               boolean isSkipValidation,String loaderKpiStrategyType, ConsumerInfo... consumerInfo);


	/**
	 * 启用规则
	 * @param folderId
	 * @param ruleId
	 * @return
	 */
	ResponseResult startRule(String folderId, String ruleId);

	/**
	 * 停用规则
	 * @param folderId
	 * @param ruleId
	 * @return
	 */
	ResponseResult stopRule(String folderId, String ruleId);

}
