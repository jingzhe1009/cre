package com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report;

import com.bonc.frame.entity.variable.Variable;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/2/12 21:37
 */
public class VariableGroupReport extends BaseReport {
    private String variableGroupName;
    private String variableGroupId;
    private Map<String, VariableReport> variable;

    public void addVariable(Variable variable) {
        if (variable != null) {
            String variableId = variable.getVariableId();
            if (this.variable == null) {
                this.variable = new HashMap<>();
            }
            if (!this.variable.containsKey(variableId)) {
                VariableReport variableReport = new VariableReport();
                variableReport.setVariableId(variableId);
                variableReport.setVariableAlias(variable.getVariableAlias());
                variableReport.setVariableCode(variable.getVariableCode());
                this.variable.put(variableId, variableReport);
            }
        }
    }
    @Override
    public String reportId() {
        return variableGroupId;
    }

    @Override
    public String reportName() {
        return variableGroupName;
    }
    public String getVariableGroupName() {
        return variableGroupName;
    }

    public void setVariableGroupName(String variableGroupName) {
        this.variableGroupName = variableGroupName;
    }

    public String getVariableGroupId() {
        return variableGroupId;
    }

    public void setVariableGroupId(String variableGroupId) {
        this.variableGroupId = variableGroupId;
    }

    public Map<String, VariableReport> getVariable() {
        return variable;
    }

    public void setVariable(Map<String, VariableReport> variable) {
        this.variable = variable;
    }
}
