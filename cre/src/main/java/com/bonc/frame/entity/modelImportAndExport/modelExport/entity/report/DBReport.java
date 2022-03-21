package com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/8 20:33
 */
public class DBReport extends BaseReport {
    private String dbId;
    private String dbAlias;

    @Override
    public String reportId() {
        return dbId;
    }

    @Override
    public String reportName() {
        return dbAlias;
    }

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    public String getDbAlias() {
        return dbAlias;
    }

    public void setDbAlias(String dbAlias) {
        this.dbAlias = dbAlias;
    }
}
