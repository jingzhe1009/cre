package com.bonc.framework.rule.compile;

import com.bonc.framework.rule.RuleType;
import com.bonc.framework.rule.parse.IRuleParse;
import com.bonc.framework.rule.parse.RuleParseFactory;
import com.bonc.framework.rule.resources.RuleResource;

/**
 * 规则编译接口默认实现
 *
 * @author qxl
 * @version 1.0
 * @date 2018年3月2日 下午4:52:49
 */
public abstract class AbstractRuleCompile implements IRuleCompile {

    @Override
    public RuleResource compileRule(String folderId, String ruleId, RuleType ruleType, String ruleName, String ruleContent) throws Exception {
        return this.compileRule(folderId, ruleId, ruleType, ruleName, ruleContent, false);
    }

    @Override
    public RuleResource compileRule(String folderId, String ruleId, RuleType ruleType, String ruleName,
                                    String ruleContent, boolean isLog) throws Exception {
        try {
            IRuleParse ruleParse = RuleParseFactory.getRuleParseByRuleType(ruleType);
            RuleResource ruleResource = ruleParse.parse(folderId, ruleId, ruleType, ruleName, ruleContent, isLog);
            RuleResource result = compile(ruleResource);
            return result;
        } catch (Exception e) {
            throw e;
        }

    }

    abstract RuleResource compile(RuleResource ruleResource) throws Exception;

}
