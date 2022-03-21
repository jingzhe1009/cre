package com.bonc.frame.entity.metadata;

import java.util.Date;
import java.util.Objects;

/**
 * 元数据
 */

public class MetaDataTable {
    private String tableId;

    private String packageId;

    private String dbId;

    private String tableCode;

    private String tableName;

    private String tableType;
    
    private String tableKind;
    
    private String isTemp;
    
    private String isPublic;
    
    private String cdtConfig;

    private Date createDate;

    private String createPerson;

    private Date updateDate;

    private String updatePerson;

    private String scanId;
    
    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
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

	public String getIsTemp() {
		return isTemp;
	}

	public void setIsTemp(String isTemp) {
		this.isTemp = isTemp;
	}

	public String getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(String isPublic) {
		this.isPublic = isPublic;
	}

	public String getCdtConfig() {
		return cdtConfig;
	}

	public void setCdtConfig(String cdtConfig) {
		this.cdtConfig = cdtConfig;
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

    public boolean isFromScan() {
        if (scanId == null || "".equals(scanId)) {
            return false;
        }
        return true;
    }

    public boolean isRelationTable() {
        if ("3".equals(tableKind)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "MetaDataTable{" +
                "tableId='" + tableId + '\'' +
                ", packageId='" + packageId + '\'' +
                ", dbId='" + dbId + '\'' +
                ", tableCode='" + tableCode + '\'' +
                ", tableName='" + tableName + '\'' +
                ", tableType='" + tableType + '\'' +
                ", tableKind='" + tableKind + '\'' +
                ", isTemp='" + isTemp + '\'' +
                ", isPublic='" + isPublic + '\'' +
                ", cdtConfig='" + cdtConfig + '\'' +
                ", createDate=" + createDate +
                ", createPerson='" + createPerson + '\'' +
                ", updateDate=" + updateDate +
                ", updatePerson='" + updatePerson + '\'' +
                ", scanId='" + scanId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaDataTable that = (MetaDataTable) o;
        return Objects.equals(packageId, that.packageId) &&
                Objects.equals(dbId, that.dbId) &&
                Objects.equals(tableCode, that.tableCode) &&
                Objects.equals(isPublic, that.isPublic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageId, dbId, tableCode, tableType, tableKind, isPublic);
    }
}