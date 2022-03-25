package com.bonc.frame.entity.auth;

import java.util.Date;

public class Channel extends UserMiddle{
    //渠道编码
    private String channelId;
    //渠道名称
    private String channelName;
    //渠道描述
    private String channelDesc;
    // 渠道编码
    private String channelCode;
    //上级id
    private String parentId;
    // 机构id
    private String deptId;
    //创建日期
    private Date createDate;
    //创建人
    private String createPerson;
    //修改日期
    private Date updateDate;
    //修改人
    private String updatePerson;

    @Override
    public String getChannelId() {
        return channelId;
    }

    @Override
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelDesc() {
        return channelDesc;
    }

    public void setChannelDesc(String channelDesc) {
        this.channelDesc = channelDesc;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
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
        return "Channel{" +
                "channelId='" + channelId + '\'' +
                ", channelName='" + channelName + '\'' +
                ", channelDesc='" + channelDesc + '\'' +
                ", parentId='" + parentId + '\'' +
                ", deptId='" + deptId + '\'' +
                ", channelCode=" + channelCode +
                ", createDate=" + createDate +
                ", createPerson='" + createPerson + '\'' +
                ", updateDate=" + updateDate +
                ", updatePerson='" + updatePerson + '\'' +
                '}';
    }
}
