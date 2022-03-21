package com.bonc.frame.entity.commonresource;

import com.bonc.frame.entity.BaseEntity;

/**
 * @author yedunyao
 * @date 2019/9/23 17:03
 */
public class ModelHeader extends BaseEntity {

    protected String modelHeaderId;

    protected String ruleName;

    protected String ruleType;

    protected String ruleHeaderDesc;

    protected String modelGroupId;

    public String getModelHeaderId() {
        return modelHeaderId;
    }

    public void setModelHeaderId(String modelHeaderId) {
        this.modelHeaderId = modelHeaderId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getRuleHeaderDesc() {
        return ruleHeaderDesc;
    }

    public void setRuleHeaderDesc(String ruleHeaderDesc) {
        this.ruleHeaderDesc = ruleHeaderDesc;
    }

    public String getModelGroupId() {
        return modelGroupId;
    }

    public void setModelGroupId(String modelGroupId) {
        this.modelGroupId = modelGroupId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ModelHeader{");
        sb.append("modelHeaderId='").append(modelHeaderId).append('\'');
        sb.append(", ruleName='").append(ruleName).append('\'');
        sb.append(", ruleType='").append(ruleType).append('\'');
        sb.append(", ruleHeaderDesc='").append(ruleHeaderDesc).append('\'');
        sb.append(", modelGroupId='").append(modelGroupId).append('\'');
        sb.append(", createDate=").append(createDate);
        sb.append(", createPerson='").append(createPerson).append('\'');
        sb.append(", updatePerson='").append(updatePerson).append('\'');
        sb.append(", updateDate=").append(updateDate);
        sb.append('}');
        return sb.toString();
    }
}
