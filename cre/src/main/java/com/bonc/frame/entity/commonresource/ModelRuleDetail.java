package com.bonc.frame.entity.commonresource;

import io.swagger.annotations.ApiModelProperty;

public class ModelRuleDetail {
    private String ruleId;
    @ApiModelProperty("模型名称")
    private String ruleName;
    @ApiModelProperty("模型描述")
    private String ruleDesc;
    @ApiModelProperty("模型类型：0，评分；1，规则")
    private String ruleType;

    public ModelRuleDetail() {
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    @Override
    public String toString() {
        return "ModelRuleDetail{" +
                "ruleId='" + ruleId + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", ruleDesc='" + ruleDesc + '\'' +
                ", ruleType='" + ruleType + '\'' +
                '}';
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleDesc() {
        return ruleDesc;
    }

    public void setRuleDesc(String ruleDesc) {
        this.ruleDesc = ruleDesc;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public ModelRuleDetail(String ruleId, String ruleName, String ruleDesc, String ruleType) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.ruleDesc = ruleDesc;
        this.ruleType = ruleType;
    }
}
