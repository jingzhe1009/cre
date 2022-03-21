package com.bonc.framework.entity.builder.entity;

import java.io.Serializable;
import java.util.Date;

public class Variable implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String variableId;

    private String entityId;
    
    private String folderId;

	//变量别名
    private String variableAlias;

    //变量编码
    private String variableCode;
    
    private String belongEntityId;//所属实体ID
    
    private String aliasCode;//编码别名

    //变量类型
    private String typeId;

    //变量种类
    private String kindId;

    //数据来源
    private String variableFrom;

    //是否数据字典
    private String isDict;

    //变量默认值
    private String defaultValue;

    private String defaultValueType;

    //变量描述
    private String variableRemarks;

    private String createPerson;

    private Date createDate;

    private String updatePerson;

    private Date updateDate;

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

    public String getBelongEntityId() {
		return belongEntityId;
	}

	public void setBelongEntityId(String belongEntityId) {
		this.belongEntityId = belongEntityId;
	}

	public String getAliasCode() {
		return aliasCode;
	}

	public void setAliasCode(String aliasCode) {
		this.aliasCode = aliasCode;
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
				+ ", variableAlias=" + variableAlias + ", variableCode=" + variableCode + ", belongEntityId="
				+ belongEntityId + ", aliasCode=" + aliasCode + ", typeId=" + typeId + ", kindId=" + kindId + "]";
	}
    
}