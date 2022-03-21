package com.bonc.frame.entity.analysis;

import com.bonc.frame.util.ConstantUtil;

public class ModelStatisticsInfo {
    private String modelCount;
    private String modelVersionCount;

    private String ruleType;

    private String ruleStatus;

    public String getModelCount() {
        return modelCount;
    }

    public void setModelCount(String modelCount) {
        this.modelCount = modelCount;
    }

    public String getModelVersionCount() {
        return modelVersionCount;
    }

    public void setModelVersionCount(String modelVersionCount) {
        this.modelVersionCount = modelVersionCount;
    }

    public String getRuleType() {
        return ruleType;
    }

    public String getRuleStatus() {
        return ruleStatus;
    }

    public void setRuleStatus(String ruleStatus) {
        if(ConstantUtil.RULE_STATUS_RUNNING.equals(ruleStatus)){
            this.ruleStatus = "enable";
        } else if(ConstantUtil.RULE_STATUS_READY.equals(ruleStatus)|| ConstantUtil.RULE_STATUS_OVER.equals(ruleStatus)||ConstantUtil.RULE_STATUS_EXCEPTION.equals(ruleStatus)){
            this.ruleStatus = "disEnable";
        } else {
            this.ruleStatus = ruleStatus;
        }
    }

    public void setRuleType(String ruleType) {
        if("0".equals(ruleType)){
            this.ruleType = "gradeModel";
        }else if ("1".equals(ruleType)){
            this.ruleType = "ruleModel";
        } else {
            this.ruleType = ruleType;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ModelStatisticsInfo{");
        sb.append("modelCount='").append(modelCount).append('\'');
        sb.append(", modelVersionCount='").append(modelVersionCount).append('\'');
        sb.append(", ruleType='").append(ruleType).append('\'');
        sb.append(", ruleStatus='").append(ruleStatus).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
