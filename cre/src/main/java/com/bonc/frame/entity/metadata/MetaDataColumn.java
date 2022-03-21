package com.bonc.frame.entity.metadata;

import java.util.Date;
import java.util.Objects;

public class MetaDataColumn {
    private String columnId;

    private String tableId;

    private String columnCode;

    private String columnName;

    private String columnType;

    private Integer columnSize;

    private String isPk;

    private String isNull;

    private String defaultValue;
    
    private Date createDate;

    private String createPerson;

    private Date updateDate;

    private String updatePerson;

    private String scanId;

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

    public Integer getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(Integer columnSize) {
        this.columnSize = columnSize;
    }

    public String getIsPk() {
        return isPk;
    }

    public void setIsPk(String isPk) {
        this.isPk = isPk;
    }

    public String getIsNull() {
        return isNull;
    }

    public void setIsNull(String isNull) {
        this.isNull = isNull;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getScanId() {
        return scanId;
    }

    public void setScanId(String scanId) {
        this.scanId = scanId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaDataColumn column = (MetaDataColumn) o;
        return Objects.equals(tableId, column.tableId) &&
                Objects.equals(columnCode, column.columnCode);
    }

    @Override
    public int hashCode() {

        return Objects.hash(tableId, columnCode);
    }

    @Override
    public String toString() {
        return "MetaDataColumn{" +
                "columnId='" + columnId + '\'' +
                ", tableId='" + tableId + '\'' +
                ", columnCode='" + columnCode + '\'' +
                ", columnName='" + columnName + '\'' +
                ", columnType='" + columnType + '\'' +
                ", columnSize=" + columnSize +
                ", isPk='" + isPk + '\'' +
                ", isNull='" + isNull + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", createDate=" + createDate +
                ", createPerson='" + createPerson + '\'' +
                ", updateDate=" + updateDate +
                ", updatePerson='" + updatePerson + '\'' +
                ", scanId='" + scanId + '\'' +
                '}';
    }
}