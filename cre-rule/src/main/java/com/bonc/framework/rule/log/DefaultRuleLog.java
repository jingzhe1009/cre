package com.bonc.framework.rule.log;

import com.bonc.framework.rule.log.entity.RuleLog;
import com.bonc.framework.rule.log.entity.RuleLogDetail;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 规则日志记录接口默认实现
 *
 * @author qxl
 * @version 1.0
 * @date 2018年4月24日 下午3:18:44
 */
public class DefaultRuleLog implements IRuleLog {

    private Log log = LogFactory.getLog(getClass());

    @Override
    public void recordRuleLog(RuleLog ruleLog, boolean isLog) {
        if (isLog) {
            log.info(ruleLog);
        }

    }

    @Override
    public void recordRuleDetailLog(RuleLogDetail ruleLogDetail, boolean isLog) {
        if (isLog) {
            log.info(ruleLogDetail);
        }
    }


}
