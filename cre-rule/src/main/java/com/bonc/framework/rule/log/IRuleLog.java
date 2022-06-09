package com.bonc.framework.rule.log;

import com.bonc.framework.rule.log.entity.RuleLog;
import com.bonc.framework.rule.log.entity.RuleLogDetail;

import java.util.Map;

/**
 * 规则执行日志接口
 *
 * @author qxl
 * @version 1.0
 * @date 2018年4月24日 下午3:05:25
 */
public interface IRuleLog {

    /**
     * 记录规则执行信息
     *
     * @param ruleLog
     */
    void recordRuleLog(RuleLog ruleLog, boolean isLog);

    /**
     * 记录规则执行详情，即每个节点执行情况
     *
     * @param ruleLogDetail
     */
    void recordRuleDetailLog(RuleLogDetail ruleLogDetail, boolean isLog);

    void saveModelLog(Map<String, String> map,RuleLog ruleLog);

}
