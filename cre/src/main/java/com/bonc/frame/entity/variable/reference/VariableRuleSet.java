package com.bonc.frame.entity.variable.reference;

/**
 * 规则库中规则对参数的引用
 *
 * @author yedunyao
 * @date 2019/9/10 11:13
 */
public class VariableRuleSet {

    private String ruleSetId;

    private String variableId;

    public String getRuleSetId() {
        return ruleSetId;
    }

    public void setRuleSetId(String ruleSetId) {
        this.ruleSetId = ruleSetId;
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VariableRuleSet{");
        sb.append("ruleSetId='").append(ruleSetId).append('\'');
        sb.append(", variableId='").append(variableId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
