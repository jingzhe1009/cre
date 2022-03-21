package com.bonc.frame.entity.auth.resource;

import java.util.Date;

public class PubRuleSetResource extends DataResource implements Cloneable{

    private String ruleSetHeaderId;
    private String ruleSetName;
    private String ruleSetGroupId;
    private String ruleSetHeaderDesc;
    private Date createDate;
    private Date updateDate;
    private String createPerson;
    private String updatePerson;
    private String ruleSetGroupName;

    public String getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(String createPerson) {
        this.createPerson = createPerson;
    }

    public String getRuleSetGroupId() {
        return ruleSetGroupId;
    }

    public void setRuleSetGroupId(String ruleSetGroupId) {
        this.ruleSetGroupId = ruleSetGroupId;
    }

    public String getRuleSetGroupName() {
        return ruleSetGroupName;
    }

    public void setRuleSetGroupName(String ruleSetGroupName) {
        this.ruleSetGroupName = ruleSetGroupName;
    }

    public String getRuleSetHeaderDesc() {
        return ruleSetHeaderDesc;
    }

    public void setRuleSetHeaderDesc(String ruleSetHeaderDesc) {
        this.ruleSetHeaderDesc = ruleSetHeaderDesc;
    }

    public String getRuleSetHeaderId() {
        return ruleSetHeaderId;
    }

    public void setRuleSetHeaderId(String ruleSetHeaderId) {
        this.ruleSetHeaderId = ruleSetHeaderId;
        super.resourceId = ruleSetHeaderId;
    }

    public String getRuleSetName() {
        return ruleSetName;
    }

    public void setRuleSetName(String ruleSetName) {
        this.ruleSetName = ruleSetName;
    }

    public String getUpdatePerson() {
        return updatePerson;
    }

    public void setUpdatePerson(String updatePerson) {
        this.updatePerson = updatePerson;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PubRuleSetResource{");
        sb.append("ruleSetHeaderId='").append(ruleSetHeaderId).append('\'');
        sb.append(", ruleSetName='").append(ruleSetName).append('\'');
        sb.append(", ruleSetGroupId='").append(ruleSetGroupId).append('\'');
        sb.append(", ruleSetHeaderDesc='").append(ruleSetHeaderDesc).append('\'');
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", createPerson='").append(createPerson).append('\'');
        sb.append(", updatePerson='").append(updatePerson).append('\'');
        sb.append(", ruleSetGroupName='").append(ruleSetGroupName).append('\'');
        sb.append(", resourceId='").append(resourceId).append('\'');
        sb.append(", resources=").append(resources);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
