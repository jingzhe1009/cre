package com.bonc.frame.entity.metadata;

/**
 * @author yedunyao
 * @date 2019/5/29 19:22
 */
public class RelationTable {

    private String dbResourceId;

    private String tableId;

    private String isMain;

    private String tableRelation;

    private String tableName;

    private String tableCode;

    private String cdtConfig;

    private String tableKind;

    public String getDbResourceId() {
        return dbResourceId;
    }

    public void setDbResourceId(String dbResourceId) {
        this.dbResourceId = dbResourceId;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getIsMain() {
        return isMain;
    }

    public void setIsMain(String isMain) {
        this.isMain = isMain;
    }

    public String getTableRelation() {
        return tableRelation;
    }

    public void setTableRelation(String tableRelation) {
        this.tableRelation = tableRelation;
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

    public String getCdtConfig() {
        return cdtConfig;
    }

    public void setCdtConfig(String cdtConfig) {
        this.cdtConfig = cdtConfig;
    }

    public String getTableKind() {
        return tableKind;
    }

    public void setTableKind(String tableKind) {
        this.tableKind = tableKind;
    }

    @Override
    public String toString() {
        return "RelationTable{" +
                "dbResourceId='" + dbResourceId + '\'' +
                ", tableId='" + tableId + '\'' +
                ", isMain='" + isMain + '\'' +
                ", tableRelation='" + tableRelation + '\'' +
                ", tableName='" + tableName + '\'' +
                ", tableCode='" + tableCode + '\'' +
                ", cdtConfig='" + cdtConfig + '\'' +
                ", tableKind='" + tableKind + '\'' +
                '}';
    }
}
