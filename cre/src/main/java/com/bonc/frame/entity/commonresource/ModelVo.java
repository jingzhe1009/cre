package com.bonc.frame.entity.commonresource;

public class ModelVo extends Model {
    private  String ruleType;

    @Override
    public String getRuleType() {
        return ruleType;
    }

    @Override
    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    @Override
    public String toString() {
        return "ModelVo{" +
                "ruleType='" + ruleType + '\'' +
                '}';
    }
}
