package com.bonc.frame.entity.modelImportAndExport.modelExport;

import java.util.List;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/13 5:01
 */
public class ExportModelParam {
    private String ruleName;
    private String type;
    private List<String> modelVersion;

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(List<String> modelVersion) {
        this.modelVersion = modelVersion;
    }
}
