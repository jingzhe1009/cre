package com.bonc.frame.engine.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.util.JsonUtils;
import com.bonc.framework.rule.log.IRuleLog;
import com.bonc.framework.rule.log.entity.RuleLog;
import com.bonc.framework.rule.log.entity.RuleLogDetail;

import java.util.Map;

/** 
 * @author 作者: jxw 
 * @date 创建时间: 2018年9月27日 上午11:07:31 
 * @version 版本: 1.0 
*/
public class DefaultCreRuleLog implements IRuleLog{
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public DefaultCreRuleLog(DaoHelper daoHelper) {
		this(daoHelper, 0);
	}
	
	public DefaultCreRuleLog(DaoHelper daoHelper,int size) {
		
	}

	@Override
	public void recordRuleLog(RuleLog ruleLog, boolean isLog) {
		log.info(JsonUtils.toJSONNoFeatures(ruleLog));
	}

	@Override
	public void recordRuleDetailLog(RuleLogDetail ruleLogDetail, boolean isLog) {
		log.info(JsonUtils.toJSONNoFeatures(ruleLogDetail));
	}

	@Override
	public void saveModelLog(Map<String, String> map, RuleLog ruleLog) {

	}

}

