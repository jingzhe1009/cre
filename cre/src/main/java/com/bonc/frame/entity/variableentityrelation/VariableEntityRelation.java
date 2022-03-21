package com.bonc.frame.entity.variableentityrelation;

public class VariableEntityRelation {
	private String entityId;

    private String variableId;
    
    private String entityVariableAlias;
    
    public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getVariableId() {
		return variableId;
	}

	public void setVariableId(String variableId) {
		this.variableId = variableId;
	}

    public String getEntityVariableAlias() {
        return entityVariableAlias;
    }

    public void setEntityVariableAlias(String entityVariableAlias) {
        this.entityVariableAlias = entityVariableAlias;
    }
}