package com.bonc.frame.entity.auth;

import com.google.common.base.Objects;

import java.util.Date;

/**
 * 权限表（主体-资源表）
 *
 * @author yedunyao
 * @date 2019/8/13 16:41
 */
public class Authority {

    private String id;

    private String resourceId;

    private String resourceTypeId;

    private String resourceExpression;

    private String subjectId;

    private Date createDate;

    private String createPerson;

    private Date updateDate;

    private String updatePerson;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceTypeId() {
        return resourceTypeId;
    }

    public void setResourceTypeId(String resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    public String getResourceExpression() {
        return resourceExpression;
    }

    public void setResourceExpression(String resourceExpression) {
        this.resourceExpression = resourceExpression;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
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

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdatePerson() {
        return updatePerson;
    }

    public void setUpdatePerson(String updatePerson) {
        this.updatePerson = updatePerson;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("resourceId", resourceId)
                .add("resourceTypeId", resourceTypeId)
                .add("resourceExpression", resourceExpression)
                .add("subjectId", subjectId)
                .add("createDate", createDate)
                .add("createPerson", createPerson)
                .add("updateDate", updateDate)
                .add("updatePerson", updatePerson)
                .toString();
    }
}
