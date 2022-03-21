package com.bonc.frame.entity.dbresource;

public class DbResourceMetaRef {
    private String dbResourceId;

    private String tableId;

    private String isMain;

    private String resourceTableRef;

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

    public String getResourceTableRef() {
        return resourceTableRef;
    }

    public void setResourceTableRef(String resourceTableRef) {
        this.resourceTableRef = resourceTableRef;
    }

	@Override
	public String toString() {
		return "DbResourceMetaRef [dbResourceId=" + dbResourceId + ", tableId=" + tableId + ", isMain=" + isMain
				+ ", resourceTableRef=" + resourceTableRef + "]";
	}
    
    
}