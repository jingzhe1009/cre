package com.bonc.frame.entity.auth;

import java.util.Date;

public class Dept extends UserMiddle{
    private String deptId;

    private String deptName;

    private String deptDesc;

    private String parentId;

    private Integer userNum;

    private Date createDate;

    private String createPerson;

    private Date updateDate;

    private String updatePerson;

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptDesc() {
        return deptDesc;
    }

    public void setDeptDesc(String deptDesc) {
        this.deptDesc = deptDesc;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getUserNum() {
        return userNum;
    }

    public void setUserNum(Integer userNum) {
        this.userNum = userNum;
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
        return "dept{" +
                "deptId='" + deptId + '\'' +
                ", deptName='" + deptName + '\'' +
                ", deptDesc=" + deptDesc + '\'' +
                ", parentId=" + parentId + '\'' +
                ", userNum=" + userNum + '\'' +
                ", createDate=" + createDate + '\'' +
                ", createPerson='" + createPerson + '\'' +
                ", updateDate=" + updateDate +
                ", updatePerson='" + updatePerson + '\'' +
                '}';
    }
}
