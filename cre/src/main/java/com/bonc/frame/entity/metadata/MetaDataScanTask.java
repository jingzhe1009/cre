package com.bonc.frame.entity.metadata;

import java.util.Date;

public class MetaDataScanTask {
    private String scanId;

    private String packageId;

    private String dbId;

    private String scanStatus;

    // ","分隔的多张表表名
    private String scanKey;

    private String scanItem;
    
    private String tableKind;

    private String scanPerson;

    private Date startDate;

    private Date endDate;

    public String getScanId() {
        return scanId;
    }

    public void setScanId(String scanId) {
        this.scanId = scanId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    public String getScanStatus() {
        return scanStatus;
    }

    public void setScanStatus(String scanStatus) {
        this.scanStatus = scanStatus;
    }

    public String getScanKey() {
        return scanKey;
    }

    public void setScanKey(String scanKey) {
        this.scanKey = scanKey;
    }

    public String getScanItem() {
        return scanItem;
    }

    public void setScanItem(String scanItem) {
        this.scanItem = scanItem;
    }

    public String getTableKind() {
		return tableKind;
	}

	public void setTableKind(String tableKind) {
		this.tableKind = tableKind;
	}

	public String getScanPerson() {
        return scanPerson;
    }

    public void setScanPerson(String scanPerson) {
        this.scanPerson = scanPerson;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}