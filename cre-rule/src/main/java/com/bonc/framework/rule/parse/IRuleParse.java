package com.bonc.framework.rule.parse;

import com.bonc.framework.rule.RuleType;
import com.bonc.framework.rule.exception.RuleParseException;
import com.bonc.framework.rule.resources.RuleResource;

/**
 * 规则转换解析接口
 *
 * @author qxl
 * @version 1.0
 * @date 2018年3月1日 上午10:53:06
 */
public interface IRuleParse {

    /**
     * 解析规则规则前执行，即实例化RuleResource对象
     *
     * @param ruleId
     * @param ruleType
     * @param ruleContent
     * @return
     */
    void beforeParse() throws RuleParseException;

    /**
     * 解析规则，不同的规则类型使用不同的规则解析器
     *
     * @param ruleId
     * @param ruleType
     * @param ruleContent
     * @return
     */
    RuleResource parse(String folderId, String ruleId, RuleType ruleType, String ruleName,
                       String ruleContent) throws RuleParseException;

    /**
     * 解析规则，不同的规则类型使用不同的规则解析器
     *
     * @param ruleId
     * @param ruleType
     * @param ruleContent
     * @return
     */
    RuleResource parse(String folderId, String ruleId, RuleType ruleType, String ruleName,
                       String ruleContent, boolean isLog) throws RuleParseException;

    /**
     * 解析规则完成后执行
     *
     * @param ruleId
     * @param ruleType
     * @param ruleContent
     * @return
     */
    void afterParse(RuleResource ruleResource) throws RuleParseException;

}
