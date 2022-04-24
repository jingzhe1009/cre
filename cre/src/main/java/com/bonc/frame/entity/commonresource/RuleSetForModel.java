package com.bonc.frame.entity.commonresource;

/**
 * 模型关联规则集展示数据实体类
 */
public class RuleSetForModel {

    private String ruleSetGroupName;
    private String ruleSetName;
    private String ruleSetDesc;
    private String version;

    @Override
    public String toString() {
        return "RuleSetForModel{" +
                "ruleSetGroupName='" + ruleSetGroupName + '\'' +
                ", ruleSetName='" + ruleSetName + '\'' +
                ", ruleSetDesc='" + ruleSetDesc + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    public String getRuleSetGroupName() {
        return ruleSetGroupName;
    }

    public void setRuleSetGroupName(String ruleSetGroupName) {
        this.ruleSetGroupName = ruleSetGroupName;
    }

    public String getRuleSetName() {
        return ruleSetName;
    }

    public void setRuleSetName(String ruleSetName) {
        this.ruleSetName = ruleSetName;
    }

    public String getRuleSetDesc() {
        return ruleSetDesc;
    }

    public void setRuleSetDesc(String ruleSetDesc) {
        this.ruleSetDesc = ruleSetDesc;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public RuleSetForModel() {

    }

    public RuleSetForModel(String ruleSetGroupName, String ruleSetName, String ruleSetDesc, String version) {

        this.ruleSetGroupName = ruleSetGroupName;
        this.ruleSetName = ruleSetName;
        this.ruleSetDesc = ruleSetDesc;
        this.version = version;
    }
}
