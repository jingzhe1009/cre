package com.bonc.framework.rule.executor.entity;

import java.io.Serializable;
import java.util.Date;

public class Variable implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -5499027490332662477L;

    protected String variableId;

    protected String entityId;
    protected String folderId;

    //变量别名
    protected String variableAlias;

    //变量编码
    protected String variableCode;

    //变量类型
    protected String typeId;

    //变量种类
    protected String kindId;

    //数据来源
    protected String variableFrom;

    //是否数据字典
    protected String isDict;

    //变量默认值
    protected String defaultValue;

    protected String defaultValueType;

    //变量描述
    protected String variableRemarks;

    protected String createPerson;

    protected Date createDate;

    protected String updatePerson;

    protected Date updateDate;


    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
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

    public String getKindId() {
        return kindId;
    }

    public void setKindId(String kindId) {
        this.kindId = kindId;
    }

    public String getVariableFrom() {
        return variableFrom;
    }

    public void setVariableFrom(String variableFrom) {
        this.variableFrom = variableFrom;
    }

    public String getIsDict() {
        return isDict;
    }

    public void setIsDict(String isDict) {
        this.isDict = isDict;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValueType() {
        return defaultValueType;
    }

    public void setDefaultValueType(String defaultValueType) {
        this.defaultValueType = defaultValueType;
    }

    public String getVariableRemarks() {
        return variableRemarks;
    }

    public void setVariableRemarks(String variableRemarks) {
        this.variableRemarks = variableRemarks;
    }

    public String getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(String createPerson) {
        this.createPerson = createPerson;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdatePerson() {
        return updatePerson;
    }

    public void setUpdatePerson(String updatePerson) {
        this.updatePerson = updatePerson;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "Variable [variableId=" + variableId + ", entityId=" + entityId + ", folderId=" + folderId
                + ", variableAlias=" + variableAlias + ", variableCode=" + variableCode + ", typeId=" + typeId
                + ", kindId=" + kindId + ", variableFrom=" + variableFrom + ", isDict=" + isDict + ", defaultValue="
                + defaultValue + ", defaultValueType=" + defaultValueType + ", variableRemarks=" + variableRemarks
                + ", createPerson=" + createPerson + ", createDate=" + createDate + ", updatePerson=" + updatePerson
                + ", updateDate=" + updateDate + "]";
    }


}
