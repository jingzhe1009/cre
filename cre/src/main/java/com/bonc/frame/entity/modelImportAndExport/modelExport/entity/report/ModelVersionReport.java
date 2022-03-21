package com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/1/9 18:51
 */
public class ModelVersionReport extends BaseReport {
    private String versionId;
    private String version;
    @Override
    public String reportId() {
        return versionId;
    }

    @Override
    public String reportName() {
        return version;
    }
    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
