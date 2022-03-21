package com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/26 12:52
 */
public class ImportIdOrNameCacheObject {
    private String sourceIdOrName; // 原始属性值
    private String suffix; // 添加的后缀


    private String idOrNameType;
    private String idOrNameKey; // 有的是 场景Id-属性值   有的就是属性值
    private String idOrName; // 调整后的属性值,也就是将要入库的属性值
    private String fromObjectType;
    private String fromObjectId;

    public String getSourceIdOrName() {
        return sourceIdOrName;
    }

    public void setSourceIdOrName(String sourceIdOrName) {
        this.sourceIdOrName = sourceIdOrName;
    }

    public String getSuffix() {
        if (StringUtils.isNotBlank(suffix)) {
            return "_" + suffix;
        } else {
            return null;
        }
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getIdOrNameType() {
        return idOrNameType;
    }

    public void setIdOrNameType(String idOrNameType) {
        this.idOrNameType = idOrNameType;
    }

    public String getIdOrNameKey() {
        return idOrNameKey;
    }

    public void setIdOrNameKey(String idOrNameKey) {
        this.idOrNameKey = idOrNameKey;
    }

    public String getIdOrName() {
        return idOrName;
    }

    public void setIdOrName(String idOrName) {
        this.idOrName = idOrName;
    }

    public String getFromObjectType() {
        return fromObjectType;
    }

    public void setFromObjectType(String fromObjectType) {
        this.fromObjectType = fromObjectType;
    }

    public String getFromObjectId() {
        return fromObjectId;
    }

    public void setFromObjectId(String fromObjectId) {
        this.fromObjectId = fromObjectId;
    }

    @Override
    public String toString() {
        return "ImportIdOrNameCacheObject{" +
                "sourceIdOrName='" + sourceIdOrName + '\'' +
                ", suffix='" + suffix + '\'' +
                ", idOrNameType='" + idOrNameType + '\'' +
                ", idOrNameKey='" + idOrNameKey + '\'' +
                ", idOrName='" + idOrName + '\'' +
                ", fromObjectType='" + fromObjectType + '\'' +
                ", fromObjectId='" + fromObjectId + '\'' +
                '}';
    }
}
