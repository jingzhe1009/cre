package com.bonc.frame.module.db.meta.core.model;

import java.util.Date;

public class ScanTask {
    private String taskId;

    private String resId;

    private Short taskStatus;
    
    private String packageId;

    private String taskKey;

    private String taskItems;
    
    private String tableKind;

    private String createUser;

    private Date startDate;

    private Date endDate;
    
    public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public Short getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Short taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public String getTaskItems() {
        return taskItems;
    }

    public void setTaskItems(String taskItems) {
        this.taskItems = taskItems;
    }

    public String getTableKind() {
		return tableKind;
	}

	public void setTableKind(String tableKind) {
		this.tableKind = tableKind;
	}

	public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
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