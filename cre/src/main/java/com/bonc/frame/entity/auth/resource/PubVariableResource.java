package com.bonc.frame.entity.auth.resource;

import java.util.Date;

/**
 * @author yedunyao
 * @date 2019/8/23 11:18
 */
public class PubVariableResource extends DataResource implements Cloneable {
    //变量Id
    private String variableId;
    private String entityId;
    //变量别名
    private String variableAlias;
    //变量编码
    private String variableCode;
    //类型Id
    private String typeId;
    //变量类型
    private String typeValue;
    //类型描述
    private String typeDesc;
    //变量种类
    private String kindId;
    //描述
    private String variableRemarks;
    private Date createDate;
    private Date updateDate;
    //参数组Id
    private String varaibleGroupId;
    //参数组名
    private String variableGroupName;

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
        super.resourceId = variableId;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
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

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public String getKindId() {
        return kindId;
    }

    public void setKindId(String kindId) {
        this.kindId = kindId;
    }

    public String getVariableRemarks() {
        return variableRemarks;
    }

    public void setVariableRemarks(String variableRemarks) {
        this.variableRemarks = variableRemarks;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getVaraibleGroupId() {
        return varaibleGroupId;
    }

    public void setVaraibleGroupId(String varaibleGroupId) {
        this.varaibleGroupId = varaibleGroupId;
    }

    public String getVariableGroupName() {
        return variableGroupName;
    }

    public void setVariableGroupName(String variableGroupName) {
        this.variableGroupName = variableGroupName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PubVariableResource{");
        sb.append("variableId='").append(variableId).append('\'');
        sb.append(", entityId='").append(entityId).append('\'');
        sb.append(", variableAlias='").append(variableAlias).append('\'');
        sb.append(", variableCode='").append(variableCode).append('\'');
        sb.append(", typeId='").append(typeId).append('\'');
        sb.append(", typeValue='").append(typeValue).append('\'');
        sb.append(", typeDesc='").append(typeDesc).append('\'');
        sb.append(", kindId='").append(kindId).append('\'');
        sb.append(", variableRemarks='").append(variableRemarks).append('\'');
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", varaibleGroupId='").append(varaibleGroupId).append('\'');
        sb.append(", variableGroupName='").append(variableGroupName).append('\'');
        sb.append(", resourceId='").append(resourceId).append('\'');
        sb.append(", resources=").append(resources);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
