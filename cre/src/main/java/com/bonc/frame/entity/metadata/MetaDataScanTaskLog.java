package com.bonc.frame.entity.metadata;

public class MetaDataScanTaskLog {
    private String logId;

    private String scanId;

    private String scanContext;

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getScanId() {
        return scanId;
    }

    public void setScanId(String scanId) {
        this.scanId = scanId;
    }

    public String getScanContext() {
        return scanContext;
    }

    public void setScanContext(String scanContext) {
        this.scanContext = scanContext;
    }
}