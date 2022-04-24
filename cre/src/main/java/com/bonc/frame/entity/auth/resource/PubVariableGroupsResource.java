package com.bonc.frame.entity.auth.resource;

import java.util.Date;

public class PubVariableGroupsResource extends DataResource implements Cloneable{
    private String variableGroupId;
    private String variableGroupName;
    private Date createDate;
    private Date updateDate;

    public String getVariableGroupId() {
        return variableGroupId;
    }

    public void setVariableGroupId(String variableGroupId) {
        this.variableGroupId = variableGroupId;
        super.resourceId = variableGroupId;
    }

    public String getVariableGroupName() {
        return variableGroupName;
    }

    public void setVariableGroupName(String variableGroupName) {
        this.variableGroupName = variableGroupName;
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

    @Override
    public String toString() {
        return "PubVariableGroupsResource{" +
                "variableGroupId='" + variableGroupId + '\'' +
                ", variableGroupName='" + variableGroupName + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                '}';
    }
}
