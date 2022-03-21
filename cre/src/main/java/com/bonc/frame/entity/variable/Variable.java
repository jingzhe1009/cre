package com.bonc.frame.entity.variable;

import com.bonc.frame.entity.rule.RuleDetail;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Variable {

    protected String variableId;

    protected String entityId;

    protected String folderId;

    //变量别名
    protected String variableAlias;

    //变量编码
    protected String variableCode;

    //变量类型
    protected String typeId;

    /**
     * 参数的域, 该字段存在 VRE_VARIABLE_RULE表中,表示,当前模型中参数的域的范围
     */
    protected String variableRange;

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

    protected List<RuleDetail> ruleDetailList;
    protected Date updateDate;

    /**
     * 是否为公共资源，非1：私有；1：公共
     * 如果是公共资源，{@code folderId}、{@code kindId}必为null，
     * 否则，{@code folderId}、{@code kindId}必不为空
     */
    protected String isPublic;

    // 所属公共参数组
    protected String variableGroupId;


    public List<RuleDetail> getRuleDetailList() {
        return ruleDetailList;
    }

    public void setRuleDetailList(List<RuleDetail> ruleDetailList) {
        this.ruleDetailList = ruleDetailList;
    }

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

    public String getVariableRange() {
        return variableRange;
    }

    public void setVariableRange(String variableRange) {
        this.variableRange = variableRange;
    }


    @Override
    public String toString() {
        return "Variable{" +
                "variableId='" + variableId + '\'' +
                ", entityId='" + entityId + '\'' +
                ", folderId='" + folderId + '\'' +
                ", variableAlias='" + variableAlias + '\'' +
                ", variableCode='" + variableCode + '\'' +
                ", typeId='" + typeId + '\'' +
                ", variableRange='" + variableRange + '\'' +
                ", kindId='" + kindId + '\'' +
                ", variableFrom='" + variableFrom + '\'' +
                ", isDict='" + isDict + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", defaultValueType='" + defaultValueType + '\'' +
                ", variableRemarks='" + variableRemarks + '\'' +
                ", createPerson='" + createPerson + '\'' +
                ", createDate=" + createDate +
                ", updatePerson='" + updatePerson + '\'' +
                ", updateDate=" + updateDate +
                ", isPublic='" + isPublic + '\'' +
                ", variableGroupId='" + variableGroupId + '\'' +
                ", ruleDetailList=" + ruleDetailList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return Objects.equals(variableCode, variable.variableCode) &&
                Objects.equals(typeId, variable.typeId) &&
                Objects.equals(kindId, variable.kindId) &&
                Objects.equals(variableFrom, variable.variableFrom) &&
                Objects.equals(defaultValue, variable.defaultValue) &&
                Objects.equals(defaultValueType, variable.defaultValueType) &&
                Objects.equals(variableRemarks, variable.variableRemarks) &&
                Objects.equals(isPublic, variable.isPublic) &&
                Objects.equals(variableGroupId, variable.variableGroupId) &&
                Objects.equals(folderId, variable.folderId);
    }

}