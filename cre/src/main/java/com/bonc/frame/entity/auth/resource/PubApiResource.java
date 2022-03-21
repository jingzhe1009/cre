package com.bonc.frame.entity.auth.resource;

import java.util.Date;

/**
 * @author yedunyao
 * @date 2019/8/23 15:20
 */
public class PubApiResource extends DataResource implements Cloneable {

    private String apiId;

    private String apiType;

    private String apiName;

    private String apiDesc;

    private Date createDate;

    private String apiGroupId;

    private String apiGroupName;

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
        super.resourceId = apiId;
    }

    public String getApiType() {
        return apiType;
    }

    public void setApiType(String apiType) {
        this.apiType = apiType;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiDesc() {
        return apiDesc;
    }

    public void setApiDesc(String apiDesc) {
        this.apiDesc = apiDesc;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PubApiResource{");
        sb.append("apiId='").append(apiId).append('\'');
        sb.append(", apiType='").append(apiType).append('\'');
        sb.append(", apiName='").append(apiName).append('\'');
        sb.append(", apiDesc='").append(apiDesc).append('\'');
        sb.append(", createDate=").append(createDate);
        sb.append(", apiGroupId='").append(apiGroupId).append('\'');
        sb.append(", apiGroupName='").append(apiGroupName).append('\'');
        sb.append(", resourceId='").append(resourceId).append('\'');
        sb.append(", resources=").append(resources);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
