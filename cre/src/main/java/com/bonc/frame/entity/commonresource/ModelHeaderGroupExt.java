package com.bonc.frame.entity.commonresource;

/**
 * @author yedunyao
 * @since 2019/9/23 17:09
 */
public class ModelHeaderGroupExt extends ModelHeader {

    private String modelGroupName;

    public String getModelGroupName() {
        return modelGroupName;
    }

    public void setModelGroupName(String modelGroupName) {
        this.modelGroupName = modelGroupName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ModelHeaderGroupExt{");
        sb.append("modelGroupName='").append(modelGroupName).append('\'');
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
