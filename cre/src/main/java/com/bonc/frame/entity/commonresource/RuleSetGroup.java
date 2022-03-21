package com.bonc.frame.entity.commonresource;

import com.bonc.frame.entity.BaseEntity;

/**
 * 规则库规则类别实体类
 *
 * @author yedunyao
 * @date 2019/9/2 10:54
 */
public class RuleSetGroup extends BaseEntity {

    private String ruleSetGroupId;

    private String ruleSetGroupName;

    public String getRuleSetGroupId() {
        return ruleSetGroupId;
    }

    public void setRuleSetGroupId(String ruleSetGroupId) {
        this.ruleSetGroupId = ruleSetGroupId;
    }

    public String getRuleSetGroupName() {
        return ruleSetGroupName;
    }

    public void setRuleSetGroupName(String ruleSetGroupName) {
        this.ruleSetGroupName = ruleSetGroupName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RuleSetGroup{");
        sb.append("ruleSetGroupId='").append(ruleSetGroupId).append('\'');
        sb.append(", ruleSetGroupName='").append(ruleSetGroupName).append('\'');
        sb.append(", createDate=").append(createDate);
        sb.append(", createPerson='").append(createPerson).append('\'');
        sb.append(", updatePerson='").append(updatePerson).append('\'');
        sb.append(", updateDate=").append(updateDate);
        sb.append('}');
        return sb.toString();
    }
}
