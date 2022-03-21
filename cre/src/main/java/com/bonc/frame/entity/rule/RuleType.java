package com.bonc.frame.entity.rule;

import java.util.Date;

public class RuleType {
    private String ruleType;

    private String ruleTypeName;

    private String ruleTypeDesc;

    private String order;

    private String isOnly;

    private String createPerson;

    private Date createDate;

    private String updatePerson;

    private Date updateDate;

    private String ruleTypeHtml;

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getRuleTypeName() {
        return ruleTypeName;
    }

    public void setRuleTypeName(String ruleTypeName) {
        this.ruleTypeName = ruleTypeName;
    }

    public String getRuleTypeDesc() {
        return ruleTypeDesc;
    }

    public void setRuleTypeDesc(String ruleTypeDesc) {
        this.ruleTypeDesc = ruleTypeDesc;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getIsOnly() {
        return isOnly;
    }

    public void setIsOnly(String isOnly) {
        this.isOnly = isOnly;
    }

    public String getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(String createPerson) {
        this.createPerson = createPerson;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdatePerson() {
        return updatePerson;
    }

    public void setUpdatePerson(String updatePerson) {
        this.updatePerson = updatePerson;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getRuleTypeHtml() {
        return ruleTypeHtml;
    }

    public void setRuleTypeHtml(String ruleTypeHtml) {
        this.ruleTypeHtml = ruleTypeHtml;
    }
}