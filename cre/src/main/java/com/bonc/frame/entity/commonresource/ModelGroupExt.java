package com.bonc.frame.entity.commonresource;

/**
 * @author yedunyao
 * @date 2019/9/3 14:07
 */
public class ModelGroupExt extends Model {

    private String modelGroupName;

    public String getModelGroupName() {
        return modelGroupName;
    }

    public void setModelGroupName(String modelGroupName) {
        this.modelGroupName = modelGroupName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ModelGroupExt{");
        sb.append("modelGroupName='").append(modelGroupName).append('\'');
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
