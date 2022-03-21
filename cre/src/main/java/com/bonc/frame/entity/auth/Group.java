package com.bonc.frame.entity.auth;

import java.util.Date;

/**
 * @author yedunyao
 * @date 2019/5/9 19:48
 */
public class Group extends UserMiddle{

    private String groupId;

    private String groupName;

    private String groupDesc;

    private Date createDate;

    private String createPerson;

    private Date updateDate;

    private String updatePerson;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }

    @Override
    public String toString() {
        return "group{" +
                "groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupDesc=" + groupDesc + '\'' +
                ", createDate=" + createDate + '\'' +
                ", createPerson='" + createPerson + '\'' +
                ", updateDate=" + updateDate + '\'' +
                ", updatePerson='" + updatePerson + '\'' +
                '}';
    }
}
