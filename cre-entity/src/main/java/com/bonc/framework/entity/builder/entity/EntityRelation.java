package com.bonc.framework.entity.builder.entity;

public class EntityRelation {
	private String entityId;

    private String entityPid;

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getEntityPid() {
		return entityPid;
	}

	public void setEntityPid(String entityPid) {
		this.entityPid = entityPid;
	}

	@Override
	public String toString() {
		return "EntityRelation [entityId=" + entityId + ", entityPid=" + entityPid + "]";
	}
	
    
}