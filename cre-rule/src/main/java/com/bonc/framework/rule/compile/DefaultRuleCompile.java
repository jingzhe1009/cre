package com.bonc.framework.rule.compile;

import com.bonc.framework.rule.resources.RuleResource;

/**
 * 规则编译接口默认实现
 *
 * @author qxl
 * @version 1.0
 * @date 2018年3月2日 下午5:44:11
 */
public class DefaultRuleCompile extends AbstractRuleCompile {

    @Override
    public RuleResource compile(RuleResource ruleResource) throws Exception {
        return ruleResource;
    }

}
