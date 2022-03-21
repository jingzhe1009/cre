package com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/8 20:33
 */
public class DBTableReport extends BaseReport {
    private String tableId;
    private String tableName;
    private String tableCode;

    @Override
    public String reportId() {
        return tableId;
    }

    @Override
    public String reportName() {
        return tableName;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }


}
