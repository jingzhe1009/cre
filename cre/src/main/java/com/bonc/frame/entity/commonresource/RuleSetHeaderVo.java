package com.bonc.frame.entity.commonresource;

public class RuleSetHeaderVo extends RuleSetHeader {

    //版本
    protected String  version;

    protected String  ruleSetGroupName;

    public String getRuleSetGroupName() {
        return ruleSetGroupName;
    }

    public void setRuleSetGroupName(String ruleSetGroupName) {
        this.ruleSetGroupName = ruleSetGroupName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public RuleSetHeaderVo() {
    }

    public RuleSetHeaderVo(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "RuleSetHeaderVo{" +
                "version='" + version + '\'' +
                ", ruleSetGroupName='" + ruleSetGroupName + '\'' +
                '}';
    }
}
