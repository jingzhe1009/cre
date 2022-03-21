package com.bonc.framework.rule.executor.entity;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class VariableExt extends Variable implements IVariable, Serializable {

    private static final long serialVersionUID = 527839967342749282L;

    private String typeValue;
    private String kindValue;
    private String entityVariableAlias;

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }

    public String getEntityVariableAlias() {
        return entityVariableAlias;
    }

    public void setEntityVariableAlias(String entityVariableAlias) {
        this.entityVariableAlias = entityVariableAlias;
    }

    public String getKindValue() {
        return kindValue;
    }

    public void setKindValue(String kindValue) {
        this.kindValue = kindValue;
    }


    @Override
    public String getId() {
        return super.variableId;
    }

    @Override
    public String getCode() {
        return super.variableCode;
    }

    @Override
    public String getName() {
        return super.variableAlias;
    }

    @Override
    public String getType() {
        return super.typeId;
    }

    @Override
    public String getDefaultValue() {
        return super.defaultValue;
    }

    @Override
    public String getVarCode() {
        String varCode = super.variableCode;
        String varAliasCode = this.entityVariableAlias;

        if (!StringUtils.isBlank(varAliasCode)) {
            varCode = varAliasCode;
        }
        return varCode;
    }
}
