package com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report;

import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/2/12 18:02
 */
public class ModelGroupReport extends BaseReport {
    private String modelGroupName;
    private String modelGroupId;
    private Map<String, ModelReport> modelHeader;

    public void addModelHeader(RuleDetailHeader ruleDetailWithBLOBs) {
        if (ruleDetailWithBLOBs != null) {
            String modelId = ruleDetailWithBLOBs.getRuleName();
            if (modelHeader == null) {
                modelHeader = new HashMap<>();
            }
            if (!modelHeader.containsKey(modelId)) {
                ModelReport modelReport = new ModelReport();
                modelReport.setRuleName(ruleDetailWithBLOBs.getRuleName());
                modelReport.setModelName(ruleDetailWithBLOBs.getModuleName());
                modelHeader.put(modelId, modelReport);
            }
        }
    }

    public void addModelVersion(RuleDetailWithBLOBs ruleDetailWithBLOBs) {
        if (ruleDetailWithBLOBs != null) {
            String modelId = ruleDetailWithBLOBs.getRuleName();
            if (modelHeader == null) {
                modelHeader = new HashMap<>();
            }
            if (modelHeader.containsKey(modelId)) {
                ModelReport modelReport = modelHeader.get(modelId);
                if (modelReport != null) {
                    modelReport.addVersion(ruleDetailWithBLOBs);
                }
            } else {
                ModelReport modelReport = new ModelReport();
                modelReport.setRuleName(ruleDetailWithBLOBs.getRuleName());
                modelReport.setModelName(ruleDetailWithBLOBs.getModuleName());
                modelReport.addVersion(ruleDetailWithBLOBs);
                modelHeader.put(modelId, modelReport);
            }
        }
    }
    @Override
    public String reportId() {
        return modelGroupId;
    }

    @Override
    public String reportName() {
        return modelGroupName;
    }
    public String getModelGroupName() {
        return modelGroupName;
    }

    public void setModelGroupName(String modelGroupName) {
        this.modelGroupName = modelGroupName;
    }

    public String getModelGroupId() {
        return modelGroupId;
    }

    public void setModelGroupId(String modelGroupId) {
        this.modelGroupId = modelGroupId;
    }

    public Map<String, ModelReport> getModelHeader() {
        return modelHeader;
    }

    public void setModelHeader(Map<String, ModelReport> modelHeader) {
        this.modelHeader = modelHeader;
    }
}
