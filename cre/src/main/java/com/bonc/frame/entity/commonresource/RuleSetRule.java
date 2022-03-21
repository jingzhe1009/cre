package com.bonc.frame.entity.commonresource;

/**
 * 模型-规则库规则集引用
 *
 * @author yedunyao
 * @date 2019/9/10 15:17
 */
public class RuleSetRule {

    /**
     * 模型id
     */
    private String ruleId;

    /**
     * 规则集id
     */
    private String ruleSetId;

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleSetId() {
        return ruleSetId;
    }

    public void setRuleSetId(String ruleSetId) {
        this.ruleSetId = ruleSetId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RuleSetRule{");
        sb.append("ruleId='").append(ruleId).append('\'');
        sb.append(", ruleSetId='").append(ruleSetId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
