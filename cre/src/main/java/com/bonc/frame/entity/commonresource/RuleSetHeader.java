package com.bonc.frame.entity.commonresource;

import com.bonc.frame.entity.BaseEntity;

/**
 * @author yedunyao
 * @date 2019/9/3 15:54
 */
public class RuleSetHeader extends BaseEntity {
    //规则集头Id
    protected String ruleSetHeaderId;
    //规则集名
    protected String ruleSetName;
    //规则集组Id
    protected String ruleSetGroupId;
    // 规则集描述
    protected String ruleSetHeaderDesc;

    public String getRuleSetHeaderId() {
        return ruleSetHeaderId;
    }

    public void setRuleSetHeaderId(String ruleSetHeaderId) {
        this.ruleSetHeaderId = ruleSetHeaderId;
    }

    public String getRuleSetName() {
        return ruleSetName;
    }

    public void setRuleSetName(String ruleSetName) {
        this.ruleSetName = ruleSetName;
    }

    public String getRuleSetGroupId() {
        return ruleSetGroupId;
    }

    public void setRuleSetGroupId(String ruleSetGroupId) {
        this.ruleSetGroupId = ruleSetGroupId;
    }

    public String getRuleSetHeaderDesc() {
        return ruleSetHeaderDesc;
    }

    public void setRuleSetHeaderDesc(String ruleSetHeaderDesc) {
        this.ruleSetHeaderDesc = ruleSetHeaderDesc;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RuleSetHeader{");
        sb.append("ruleSetHeaderId='").append(ruleSetHeaderId).append('\'');
        sb.append(", ruleSetName='").append(ruleSetName).append('\'');
        sb.append(", ruleSetGroupId='").append(ruleSetGroupId).append('\'');
        sb.append(", ruleSetHeaderDesc='").append(ruleSetHeaderDesc).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
