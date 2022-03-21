package com.bonc.frame.module.db.meta.core.model;

public class ScanTaskLog {
	private String logId;
	
    private String taskId;

    private String taskDetail;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskDetail() {
        return taskDetail;
    }

    public void setTaskDetail(String taskDetail) {
        this.taskDetail = taskDetail;
    }
    
    public int getDetailLength(){
    	if(this.taskDetail==null) return 0;
    	return this.taskDetail.length();
    }

	public String getLogId() {
		return logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	@Override
	public String toString() {
		return "ScanTaskLog [logId=" + logId + ", taskId=" + taskId + ", taskDetail=" + taskDetail
				+ "]";
	}
	
	
}