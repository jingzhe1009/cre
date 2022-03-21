package com.bonc.frame.engine.log;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.util.JsonUtils;
import com.bonc.framework.api.log.IApiLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/** 
 * @author 作者: jxw 
 * @date 创建时间: 2018年9月27日 上午11:04:26 
 * @version 版本: 1.0 
*/
public class DefaultApiLog implements IApiLog{
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public DefaultApiLog(DaoHelper daoHelper) {
		this(daoHelper, 0);
	}
	
	public DefaultApiLog(DaoHelper daoHelper,int size) {
		
	}

    @Deprecated
    @Override
    public void recordRuleLog(Map<String, Object> map, boolean isLog) {
        log.info(JsonUtils.toJSONNoFeatures(map));
    }

    @Override
    public void recordRuleLog(com.bonc.framework.api.log.entity.ApiLog apiLog, boolean isLog) {
        log.info(JsonUtils.toJSONNoFeatures(apiLog));
    }

}

