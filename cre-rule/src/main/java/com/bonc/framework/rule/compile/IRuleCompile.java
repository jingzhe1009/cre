package com.bonc.framework.rule.compile;

import com.bonc.framework.rule.RuleType;
import com.bonc.framework.rule.resources.RuleResource;

/**
 * 规则编译接口
 *
 * @author qxl
 * @version 1.0
 * @date 2018年3月1日 上午10:51:31
 */
public interface IRuleCompile {

    /**
     * 编译规则
     *
     * @param ruleId      规则ID
     * @param ruleType    规则类型
     * @param ruleContent 规则原始内容
     * @return
     * @throws Exception
     */
    RuleResource compileRule(String folderId, String ruleId, RuleType ruleType, String ruleName,
                             String ruleContent) throws Exception;

    /**
     * 编译规则
     *
     * @param ruleId      规则ID
     * @param ruleType    规则类型
     * @param ruleContent 规则原始内容
     * @param isLog       是否记录日志
     * @return
     * @throws Exception
     */
    RuleResource compileRule(String folderId, String ruleId, RuleType ruleType, String ruleName,
                             String ruleContent, boolean isLog) throws Exception;


}
