package com.bonc.frame.module.db.meta.core.model;

import java.util.Date;

public class StructInfo {
    private String columnId;

    private String tableId;

    private String columnCode;

    private String columnName;

    private String columnType;
    
    private Short dataType;

    private Short isPk;

    private Short isNullable;

    private Integer columnSize;

    private String columnDefault;

    private String remarks;
    
    private Date createDate;

    private String createPerson;

    private Date updateDate;

    private String updatePerson;

	private String scanId;

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getColumnCode() {
        return columnCode;
    }

    public void setColumnCode(String columnCode) {
        this.columnCode = columnCode;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public Short getIsPk() {
        return isPk;
    }

    public void setIsPk(Short isPk) {
        this.isPk = isPk;
    }

    public Short getIsNullable() {
        return isNullable;
    }

    public void setIsNullable(Short isNullable) {
        this.isNullable = isNullable;
    }

    public Integer getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(Integer columnSize) {
        this.columnSize = columnSize;
    }

    public String getColumnDefault() {
        return columnDefault;
    }

    public void setColumnDefault(String columnDefault) {
        this.columnDefault = columnDefault;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
	public Short getDataType() {
		return dataType;
	}

	public void setDataType(Short dataType) {
		this.dataType = dataType;
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
		result = prime * result
				+ ((columnCode == null) ? 0 : columnCode.hashCode());
		result = prime * result
				+ ((columnDefault == null) ? 0 : columnDefault.hashCode());
		result = prime * result
				+ ((columnName == null) ? 0 : columnName.hashCode());
		result = prime * result
				+ ((columnSize == null) ? 0 : columnSize.hashCode());
		result = prime * result
				+ ((columnType == null) ? 0 : columnType.hashCode());
		result = prime * result
				+ ((isNullable == null) ? 0 : isNullable.hashCode());
		result = prime * result + ((isPk == null) ? 0 : isPk.hashCode());
		result = prime * result + ((remarks == null) ? 0 : remarks.hashCode());
		result = prime * result + ((dataType == null)? 0 : dataType.hashCode());
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
		StructInfo other = (StructInfo) obj;
		if (columnCode == null) {
			if (other.columnCode != null)
				return false;
		} else if (!columnCode.equals(other.columnCode))
			return false;
		if (columnDefault == null) {
			if (other.columnDefault != null)
				return false;
		} else if (!columnDefault.equals(other.columnDefault))
			return false;
		if (columnName == null) {
			if (other.columnName != null)
				return false;
		} else if (!columnName.equals(other.columnName))
			return false;
		if (columnSize == null) {
			if (other.columnSize != null)
				return false;
		} else if (!columnSize.equals(other.columnSize))
			return false;
		if (columnType == null) {
			if (other.columnType != null)
				return false;
		} else if (!columnType.equals(other.columnType))
			return false;
		if (isNullable == null) {
			if (other.isNullable != null)
				return false;
		} else if (!isNullable.equals(other.isNullable))
			return false;
		if (isPk == null) {
			if (other.isPk != null)
				return false;
		} else if (!isPk.equals(other.isPk))
			return false;
		if (remarks == null) {
			if (other.remarks != null)
				return false;
		} else if (!remarks.equals(other.remarks))
			return false;
		if(dataType == null){
			if(other.dataType != null)
				return false;
		}else if(!dataType.equals(other.dataType))
			return false;
		return true;
	}

	
    
    
}