package com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report;

import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/1/9 18:49
 */
public class ModelReport extends BaseReport {
    private String ruleName;
    private String modelName;
    private Map<String, ModelVersionReport> version;

    public void addVersion(RuleDetailWithBLOBs ruleDetailWithBLOBs) {
        if (ruleDetailWithBLOBs != null) {
            String ruleId = ruleDetailWithBLOBs.getRuleId();
            ModelVersionReport modelVersionReport = new ModelVersionReport();
            modelVersionReport.setVersion(ruleDetailWithBLOBs.getVersion());
            modelVersionReport.setVersionId(ruleId);
            if (version == null) {
                version = new HashMap<>();
            }
            if (!version.containsKey(ruleId)) {
                version.put(ruleId, modelVersionReport);
            }
        }
    }

    public boolean containsVersion(RuleDetailWithBLOBs ruleDetailWithBLOBs) {
        if (ruleDetailWithBLOBs != null && version != null) {
            return version.containsKey(ruleDetailWithBLOBs.getRuleId());
        }
        return false;
    }

    public String toReportString() {
        StringBuilder result = new StringBuilder();

        result.append(modelName);
        if (version != null && !version.isEmpty()) {
            result.append("\t\t\t" + version.size());
            for (ModelVersionReport modelVersionReport : version.values()) {
                if (modelVersionReport != null) {
                    String version = modelVersionReport.getVersion();
                    result.append("\t\t\t" + version);
                }
                result.append("\n");
            }
        } else {
            result.append("\t\t\t" + "0\n");
        }
        return result.toString();
    }
    @Override
    public String reportId() {
        return ruleName;
    }

    @Override
    public String reportName() {
        return modelName;
    }
    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Map<String, ModelVersionReport> getVersion() {
        return version;
    }

    public void setVersion(Map<String, ModelVersionReport> version) {
        this.version = version;
    }
}
