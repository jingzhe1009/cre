package com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report;

import java.util.List;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/2/11 20:02
 */
public class ApiReport extends BaseReport {
    private String apiId;
    private String apiName;
    private String apiDesc;

    @Override
    public String reportId() {
        return apiId;
    }

    @Override
    public String reportName() {
        return apiName;
    }

    private List<VariableReport> apiVariableRef;

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiDesc() {
        return apiDesc;
    }

    public void setApiDesc(String apiDesc) {
        this.apiDesc = apiDesc;
    }

    public List<VariableReport> getApiVariableRef() {
        return apiVariableRef;
    }

    public void setApiVariableRef(List<VariableReport> apiVariableRef) {
        this.apiVariableRef = apiVariableRef;
    }
}
