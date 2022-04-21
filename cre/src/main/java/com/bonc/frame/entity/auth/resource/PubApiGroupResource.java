package com.bonc.frame.entity.auth.resource;

import java.util.Date;

public class PubApiGroupResource extends DataResource implements Cloneable{

    private Date startDate;

    private Date endDate;

    private String apiGroupId;

    private String apiGroupName;

    public String getApiGroupId() {
        return apiGroupId;
    }

    public void setApiGroupId(String apiGroupId) {
        this.apiGroupId = apiGroupId;
        super.resourceId = apiGroupId;
    }

    public String getApiGroupName() {
        return apiGroupName;
    }

    public void setApiGroupName(String apiGroupName) {
        this.apiGroupName = apiGroupName;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "PubApiGroupResource{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", apiGroupId='" + apiGroupId + '\'' +
                ", apiGroupName='" + apiGroupName + '\'' +
                '}';
    }
}

