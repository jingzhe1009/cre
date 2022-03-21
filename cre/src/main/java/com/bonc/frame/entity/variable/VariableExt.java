package com.bonc.frame.entity.variable;

public class VariableExt extends Variable{
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
}
