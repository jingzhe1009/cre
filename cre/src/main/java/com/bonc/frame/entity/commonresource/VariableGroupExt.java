package com.bonc.frame.entity.commonresource;

import com.bonc.frame.entity.variable.Variable;

/**
 * @author yedunyao
 * @date 2019/7/23 10:18
 */
public class VariableGroupExt extends Variable {

    private String typeValue;

    private String kindValue;

    private String variableGroupName;

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }

    public String getVariableGroupName() {
        return variableGroupName;
    }

    public void setVariableGroupName(String variableGroupName) {
        this.variableGroupName = variableGroupName;
    }

    public String getKindValue() {
        return kindValue;
    }

    public void setKindValue(String kindValue) {
        this.kindValue = kindValue;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VariableGroupExt{");
        sb.append("variableId='").append(getVariableId()).append('\'');
        sb.append(", variableAlias='").append(getVariableAlias()).append('\'');
        sb.append(", variableCode='").append(getVariableCode()).append('\'');
        sb.append(", typeId='").append(getTypeId()).append('\'');
        sb.append(", typeValue='").append(typeValue).append('\'');
        sb.append(", kindId='").append(getKindId()).append('\'');
        sb.append(", kindValue='").append(kindValue).append('\'');
        sb.append(", isPublic='").append(getIsPublic()).append('\'');
        sb.append(", variableGroupId='").append(getVariableGroupId()).append('\'');
        sb.append(", variableGroupName='").append(variableGroupName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
