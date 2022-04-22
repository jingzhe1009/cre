package com.bonc.frame.entity.auth.resource;

public class PubRuleSetGroupResource extends DataResource implements Cloneable{

    private String ruleSetName;

    private String ruleSetGroupName;

    private String ruleSetGroupId;

    private String startDate;

    private String endDate;

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

    public String getRuleSetGroupId() {
        return ruleSetGroupId;
    }

    public void setRuleSetGroupId(String ruleSetGroupId) {
        this.ruleSetGroupId = ruleSetGroupId;
        super.resourceId =ruleSetGroupId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "PubRuleSetGroupResource{" +
                "ruleSetName='" + ruleSetName + '\'' +
                ", ruleSetGroupName='" + ruleSetGroupName + '\'' +
                ", ruleSetGroupId='" + ruleSetGroupId + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
