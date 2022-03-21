package com.bonc.frame.entity.variable.reference;

/**
 * 模型对参数的引用，包括规则集、分支对参数的引用
 *
 * @author yedunyao
 * @date 2019/7/31 10:19
 */
public class VariableRule {

    public static final String FORK_VARAIBLE_CODE = "0";    // 分支变量
    public static final String RULESET_VARAIBLE_CODE = "1"; // 规则集变量

    private String ruleId;

    private String variableId;

    private String ruleVariableType;
    private String variableRange;

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public String getRuleVariableType() {
        return ruleVariableType;
    }

    public void setRuleVariableType(String ruleVariableType) {
        this.ruleVariableType = ruleVariableType;
    }

    public String getVariableRange() {
        return variableRange;
    }

    public void setVariableRange(String variableRange) {
        this.variableRange = variableRange;
    }

    @Override
    public String toString() {
        return "VariableRule{" +
                "ruleId='" + ruleId + '\'' +
                ", variableId='" + variableId + '\'' +
                ", ruleVariableType='" + ruleVariableType + '\'' +
                ", variableRange='" + variableRange + '\'' +
                '}';
    }
}
