package com.bonc.frame.module.db.meta.core.model;

import java.util.Date;

public class DatabaseResource {
    private String resId;

    private String resName;

    private String resIp;

    private Integer resPort;

    private Short resType;

    private String resUsername;

    private String resPassword;

    private String resService;

    private Short resCharset;

    private Short resMax;

    private String remarks;

    private Date createDate;

    private String createPersion;

    private Date updateDate;

    private String updatePersion;

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getResIp() {
        return resIp;
    }

    public void setResIp(String resIp) {
        this.resIp = resIp;
    }

    public Integer getResPort() {
        return resPort;
    }

    public void setResPort(Integer resPort) {
        this.resPort = resPort;
    }

    public Short getResType() {
        return resType;
    }

    public void setResType(Short resType) {
        this.resType = resType;
    }

    public String getResUsername() {
        return resUsername;
    }

    public void setResUsername(String resUsername) {
        this.resUsername = resUsername;
    }

    public String getResPassword() {
        return resPassword;
    }

    public void setResPassword(String resPassword) {
        this.resPassword = resPassword;
    }

    public String getResService() {
        return resService;
    }

    public void setResService(String resService) {
        this.resService = resService;
    }

    public Short getResCharset() {
        return resCharset;
    }

    public void setResCharset(Short resCharset) {
        this.resCharset = resCharset;
    }

    public Short getResMax() {
        return resMax;
    }

    public void setResMax(Short resMax) {
        this.resMax = resMax;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatePersion() {
        return createPersion;
    }

    public void setCreatePersion(String createPersion) {
        this.createPersion = createPersion;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdatePersion() {
        return updatePersion;
    }

    public void setUpdatePersion(String updatePersion) {
        this.updatePersion = updatePersion;
    }
    
    
}