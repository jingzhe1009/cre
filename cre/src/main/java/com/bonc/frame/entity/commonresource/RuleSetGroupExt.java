package com.bonc.frame.entity.commonresource;

/**
 * @author yedunyao
 * @date 2019/9/3 14:07
 */
public class RuleSetGroupExt extends RuleSet {

    private String ruleSetGroupName;

    public String getRuleSetGroupName() {
        return ruleSetGroupName;
    }

    public void setRuleSetGroupName(String ruleSetGroupName) {
        this.ruleSetGroupName = ruleSetGroupName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RuleSetGroupExt{");
        sb.append("ruleSetGroupName='").append(ruleSetGroupName).append('\'');
        sb.append(", ruleSetId='").append(ruleSetId).append('\'');
        sb.append(", ruleSetContent='").append(ruleSetContent).append('\'');
        sb.append(", ruleSetDesc='").append(ruleSetDesc).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", ruleSetHeaderId='").append(ruleSetHeaderId).append('\'');
        sb.append(", ruleSetHeaderId='").append(ruleSetHeaderId).append('\'');
        sb.append(", ruleSetName='").append(ruleSetName).append('\'');
        sb.append(", ruleSetGroupId='").append(ruleSetGroupId).append('\'');
        sb.append(", createDate=").append(createDate);
        sb.append(", createPerson='").append(createPerson).append('\'');
        sb.append(", updatePerson='").append(updatePerson).append('\'');
        sb.append(", updateDate=").append(updateDate);
        sb.append('}');
        return sb.toString();
    }
}
