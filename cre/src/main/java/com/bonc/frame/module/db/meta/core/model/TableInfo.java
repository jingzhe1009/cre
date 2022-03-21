package com.bonc.frame.module.db.meta.core.model;

import java.util.Date;
import java.util.List;

public class TableInfo {
    private String tableId;

    private String tableCode;

    private String resId;

    private String tableName;

    private String tableType;
    
    private String tableKind;

    private Short resStatus;
    
    private String packageId;

    private Date createDate;

    private String createPerson;

    private Date updateDate;

    private String updatePerson;

    private String tableSql;
    
    private String owner;
    
    private List<StructInfo> structs;

	private String scanId;

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public String getTableKind() {
		return tableKind;
	}

	public void setTableKind(String tableKind) {
		this.tableKind = tableKind;
	}

	public Short getResStatus() {
        return resStatus;
    }

    public void setResStatus(Short resStatus) {
        this.resStatus = resStatus;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(String createPerson) {
        this.createPerson = createPerson;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdatePerson() {
        return updatePerson;
    }

    public void setUpdatePerson(String updatePerson) {
        this.updatePerson = updatePerson;
    }

    public String getTableSql() {
        return tableSql;
    }

    public void setTableSql(String tableSql) {
        this.tableSql = tableSql;
    }

	public List<StructInfo> getStructs() {
		return structs;
	}

	public void setStructs(List<StructInfo> structs) {
		this.structs = structs;
	}
	
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public String getScanId() {
		return scanId;
	}

	public void setScanId(String scanId) {
		this.scanId = scanId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((resId == null) ? 0 : resId.hashCode());
		result = prime * result
				+ ((tableCode == null) ? 0 : tableCode.hashCode());
		result = prime * result + ((tableId == null) ? 0 : tableId.hashCode());
		result = prime * result
				+ ((tableName == null) ? 0 : tableName.hashCode());
		result = prime * result
				+ ((tableSql == null) ? 0 : tableSql.hashCode());
		result = prime * result
				+ ((tableType == null) ? 0 : tableType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TableInfo other = (TableInfo) obj;
		if (resId == null) {
			if (other.resId != null)
				return false;
		} else if (!resId.equals(other.resId))
			return false;
		if (tableCode == null) {
			if (other.tableCode != null)
				return false;
		} else if (!tableCode.equals(other.tableCode))
			return false;
//		if (tableId == null) {
//			if (other.tableId != null)
//				return false;
//		} else if (!tableId.equals(other.tableId))
//			return false;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		if (tableSql == null) {
			if (other.tableSql != null)
				return false;
		} else if (!tableSql.equals(other.tableSql))
			return false;
		if (tableType == null) {
			if (other.tableType != null)
				return false;
		} else if (!tableType.equals(other.tableType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TableInfo [tableId=" + tableId + ", tableCode=" + tableCode + ", resId=" + resId + ", tableName="
				+ tableName + ", tableType=" + tableType + ", resStatus=" + resStatus + ", packageId=" + packageId
				+ ", createDate=" + createDate + ", createPerson=" + createPerson + ", updateDate=" + updateDate
				+ ", updatePerson=" + updatePerson + ", tableSql=" + tableSql + ", owner=" + owner + ", structs="
				+ structs + "]";
	}

	
}