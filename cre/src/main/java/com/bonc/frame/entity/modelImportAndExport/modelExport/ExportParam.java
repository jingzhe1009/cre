package com.bonc.frame.entity.modelImportAndExport.modelExport;

import java.util.List;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/13 5:00
 */
public class ExportParam {
    private String folderId;
    private String modelGroupId;
    private String type;
    private List<ExportModelParam> modelHeader;

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getModelGroupId() {
        return modelGroupId;
    }

    public void setModelGroupId(String modelGroupId) {
        this.modelGroupId = modelGroupId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ExportModelParam> getModelHeader() {
        return modelHeader;
    }

    public void setModelHeader(List<ExportModelParam> modelHeader) {
        this.modelHeader = modelHeader;
    }
}
