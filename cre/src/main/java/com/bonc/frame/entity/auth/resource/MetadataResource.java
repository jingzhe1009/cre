package com.bonc.frame.entity.auth.resource;

import com.google.common.base.Objects;

/**
 * @author yedunyao
 * @date 2019/8/16 10:38
 */
public class MetadataResource extends DataResource implements Cloneable {

    private String tableId;

    private String tableName;

    private String dbId;

    private String dbAlias;

    private String folderId;

    private String folderName;

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
        super.resourceId = tableId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
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

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("tableId", tableId)
                .add("tableName", tableName)
                .add("dbId", dbId)
                .add("dbAlias", dbAlias)
                .add("folderId", folderId)
                .add("folderName", folderName)
                .add("resourceId", resourceId)
                .add("resources", resources)
                .toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
