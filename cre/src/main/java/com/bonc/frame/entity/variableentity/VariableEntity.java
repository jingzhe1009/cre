package com.bonc.frame.entity.variableentity;

import java.util.Date;

public class VariableEntity {
    private String entityId;

    private String entityPid;

    private String createPerson;

    private Date createDate;

    private String updatePerson;

    private Date updateDate;

    private String entityRawtypeDesc;

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

    public String getEntityRawtypeDesc() {
        return entityRawtypeDesc;
    }

    public void setEntityRawtypeDesc(String entityRawtypeDesc) {
        this.entityRawtypeDesc = entityRawtypeDesc;
    }
}