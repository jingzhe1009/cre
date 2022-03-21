package com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/9 5:08
 */
public class DBColumnReport extends BaseReport {
    private String columnId;
    private String columnName;
    private String columnCode;

    @Override
    public String reportId() {
        return columnId;
    }

    @Override
    public String reportName() {
        return columnName;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnCode() {
        return columnCode;
    }

    public void setColumnCode(String columnCode) {
        this.columnCode = columnCode;
    }
}
