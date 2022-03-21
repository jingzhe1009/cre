package com.bonc.frame.entity.commonresource;

import java.util.Date;
import java.util.Objects;

/**
 * @author yedunyao
 * @date 2019/7/22 16:01
 */
public class ApiGroup {

    private String apiGroupId;

    private String apiGroupName;

    private Date createDate;

    private String createPerson;

    private String updatePerson;

    private Date updateDate;

    public String getApiGroupId() {
        return apiGroupId;
    }

    public void setApiGroupId(String apiGroupId) {
        this.apiGroupId = apiGroupId;
    }

    public String getApiGroupName() {
        return apiGroupName;
    }

    public void setApiGroupName(String apiGroupName) {
        this.apiGroupName = apiGroupName;
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
        return "ApiGroup{" +
                "apiGroupId='" + apiGroupId + '\'' +
                ", apiGroupName='" + apiGroupName + '\'' +
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
        ApiGroup apiGroup = (ApiGroup) o;
        return Objects.equals(apiGroupName, apiGroup.apiGroupName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiGroupName);
    }
}
