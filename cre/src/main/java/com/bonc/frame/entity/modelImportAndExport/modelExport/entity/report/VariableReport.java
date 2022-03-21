package com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/2/11 20:02
 */
public class VariableReport extends BaseReport {
    private String variableId;
    private String variableAlias;
    private String variableCode;

    @Override
    public String reportId() {
        return variableId;
    }

    @Override
    public String reportName() {
        return variableCode;
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public String getVariableAlias() {
        return variableAlias;
    }

    public void setVariableAlias(String variableAlias) {
        this.variableAlias = variableAlias;
    }

    public String getVariableCode() {
        return variableCode;
    }

    public void setVariableCode(String variableCode) {
        this.variableCode = variableCode;
    }
}
