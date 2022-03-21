package com.bonc.frame.entity.commonresource;

/**
 * @author yedunyao
 * @date 2019/9/23 17:24
 */
public class ModelDetail extends Model {

    protected String ruleContent;

    protected String ruleIntercept;

    public String getRuleContent() {
        return ruleContent;
    }

    public void setRuleContent(String ruleContent) {
        this.ruleContent = ruleContent;
    }

    public String getRuleIntercept() {
        return ruleIntercept;
    }

    public void setRuleIntercept(String ruleIntercept) {
        this.ruleIntercept = ruleIntercept;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ModelDetail{");
        sb.append("ruleContent='").append(ruleContent).append('\'');
        sb.append(", ruleIntercept='").append(ruleIntercept).append('\'');
        sb.append(", ruleId='").append(ruleId).append('\'');
        sb.append(", ruleStatus='").append(ruleStatus).append('\'');
        sb.append(", ruleDesc='").append(ruleDesc).append('\'');
        sb.append(", isLog='").append(isLog).append('\'');
        sb.append(", modelGroupId='").append(modelGroupId).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", modelHeaderId='").append(modelHeaderId).append('\'');
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
