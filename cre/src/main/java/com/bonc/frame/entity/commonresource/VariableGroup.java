package com.bonc.frame.entity.commonresource;

import java.util.Date;
import java.util.Objects;

/**
 * @author yedunyao
 * @date 2019/7/22 16:01
 */
public class VariableGroup {

    // VARIABLE_GROUP_ID
    private String variableGroupId;

    private String variableGroupName;

    private Date createDate;

    private String createPerson;

    private String updatePerson;

    private Date updateDate;

    public String getVariableGroupId() {
        return variableGroupId;
    }

    public void setVariableGroupId(String variableGroupId) {
        this.variableGroupId = variableGroupId;
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

    public String getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(String createPerson) {
        this.createPerson = createPerson;
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
        return "VariableGroup{" +
                "variableGroupId='" + variableGroupId + '\'' +
                ", variableGroupName='" + variableGroupName + '\'' +
                ", createDate=" + createDate +
                ", createPerson='" + createPerson + '\'' +
                ", updatePerson='" + updatePerson + '\'' +
                ", updateDate=" + updateDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariableGroup that = (VariableGroup) o;
        return Objects.equals(variableGroupName, that.variableGroupName);
    }

}
