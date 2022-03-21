package com.bonc.framework.api.log;

import com.bonc.framework.api.log.entity.ApiLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class DefaultApiLog implements IApiLog {

    Log log = LogFactory.getLog(getClass());

    @Deprecated
    @Override
    public void recordRuleLog(Map<String, Object> map, boolean isLog) {
        if (isLog) {
            log.info("api Map:" + map.toString());
        }

    }

    @Override
    public void recordRuleLog(ApiLog apiLog, boolean isLog) {
        if (isLog) {
            log.info("api log:" + apiLog.toString());
        }

    }

}
