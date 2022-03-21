package com.bonc.framework.rule.parse;

import com.bonc.framework.rule.RuleType;
import com.bonc.framework.rule.exception.RuleParseException;
import com.bonc.framework.rule.resources.RuleResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author qxl
 * @version 1.0
 * @date 2018年3月5日 下午5:16:33
 */
public abstract class AbstractRuleParse implements IRuleParse {

    private Log log = LogFactory.getLog(getClass());

    protected RuleResource ruleResource;

    @Override
    public void beforeParse() throws RuleParseException {
        log.trace("开始编译规则模型");
        ruleResource = new RuleResource();
    }

    @Override
    public RuleResource parse(String folderId, String ruleId, RuleType ruleType, String ruleName,
                              String ruleContent, boolean isLog) throws RuleParseException {
        this.beforeParse();
//		RuleResource ruleResource = new RuleResource();
        ruleResource.setFolderId(folderId);
        ruleResource.setRuleId(ruleId);
        ruleResource.setRuleType(ruleType);
        ruleResource.setLog(isLog);
        ruleResource.setRuleName(ruleName);
        ruleResource.setRuleContent(ruleContent);
        this.parseRule(ruleContent);
        this.afterParse(ruleResource);
        return ruleResource;
    }

    @Override
    public RuleResource parse(String folderId, String ruleId, RuleType ruleType, String ruleName,
                              String ruleContent) throws RuleParseException {
        return this.parse(folderId, ruleId, ruleType, ruleName, ruleContent, false);
    }

    abstract RuleResource parseRule(String ruleContent) throws RuleParseException;

    @Override
    public void afterParse(RuleResource ruleResource) throws RuleParseException {
//		System.out.println("parse rule over.");
        log.trace("编译规则模型结束");
    }


}
