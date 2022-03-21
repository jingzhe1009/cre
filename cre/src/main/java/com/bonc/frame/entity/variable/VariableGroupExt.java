package com.bonc.frame.entity.variable;

/**
 * 已弃用 用 com.bonc.frame.entity.commonresource.VariableGroupExt
 * @author yedunyao
 * @date 2019/8/31 19:22
 */
@Deprecated
public class VariableGroupExt {

    private String variableId;
    private String variableAlias;
    private String variableCode;
    private String typeId;
    private String typeValue;
    private String kindId;
    private String kindValue;
    private String isPublic;
    private String variableGroupId;
    private String variableGroupName;

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public String getVariableAlias() {
        return variableAlias;
    }

    public void setVariableAlias(String variableAlias) {
        this.variableAlias = variableAlias;
    }

    public String getVariableCode() {
        return variableCode;
    }

    public void setVariableCode(String variableCode) {
        this.variableCode = variableCode;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }

    public String getKindId() {
        return kindId;
    }

    public void setKindId(String kindId) {
        this.kindId = kindId;
    }

    public String getKindValue() {
        return kindValue;
    }

    public void setKindValue(String kindValue) {
        this.kindValue = kindValue;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getVariableGroupId() {
        return variableGroupId;
    }

    public void setVariableGroupId(String variableGroupId) {
        this.variableGroupId = variableGroupId;
    }

    public String getVariableGroupName() {
        return variableGroupName;
    }

    public void setVariableGroupName(String variableGroupName) {
        this.variableGroupName = variableGroupName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VariableGroupExt{");
        sb.append("variableId='").append(variableId).append('\'');
        sb.append(", variableAlias='").append(variableAlias).append('\'');
        sb.append(", variableCode='").append(variableCode).append('\'');
        sb.append(", typeId='").append(typeId).append('\'');
        sb.append(", typeValue='").append(typeValue).append('\'');
        sb.append(", kindId='").append(kindId).append('\'');
        sb.append(", kindValue='").append(kindValue).append('\'');
        sb.append(", isPublic='").append(isPublic).append('\'');
        sb.append(", variableGroupId='").append(variableGroupId).append('\'');
        sb.append(", variableGroupName='").append(variableGroupName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
