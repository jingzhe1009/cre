package com.bonc.frame.entity.api;

/**
 * @author yedunyao
 * @date 2019/8/2 10:40
 */
public class ApiRule {

    private String apiId;

    private String ruleId;

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    @Override
    public String toString() {
        return "ApiRule{" +
                "apiId='" + apiId + '\'' +
                ", ruleId='" + ruleId + '\'' +
                '}';
    }
}
