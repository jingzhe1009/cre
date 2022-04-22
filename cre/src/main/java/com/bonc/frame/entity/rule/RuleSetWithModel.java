package com.bonc.frame.entity.rule;

public class RuleSetWithModel {
    private String ruleId;
    private String ruleName;
    private String modelGroupName;
    private String version;
    private String ruleType;

    @Override
    public String toString() {
        return "RuleSetWithModel{" +
                "ruleId='" + ruleId + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", modelGroupName='" + modelGroupName + '\'' +
                ", version='" + version + '\'' +
                ", ruleType='" + ruleType + '\'' +
                '}';
    }

    public RuleSetWithModel() {
    }

    public RuleSetWithModel(String ruleId, String ruleName, String modelGroupName, String version, String ruleType) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.modelGroupName = modelGroupName;
        this.version = version;
        this.ruleType = ruleType;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getModelGroupId() {
        return modelGroupName;
    }

    public void setModelGroupName(String modelGroupName) {
        this.modelGroupName = modelGroupName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }
}
