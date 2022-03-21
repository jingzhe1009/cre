package com.bonc.frame.entity.commonresource;

/**
 * @author yedunyao
 * @date 2019/9/23 17:11
 */
public class Model extends ModelHeader {

    protected String ruleId;

    protected String ruleStatus;

    protected String ruleDesc;

    protected String isLog;

    protected String modelGroupId;

    protected String version;

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleStatus() {
        return ruleStatus;
    }

    public void setRuleStatus(String ruleStatus) {
        this.ruleStatus = ruleStatus;
    }

    public String getRuleDesc() {
        return ruleDesc;
    }

    public void setRuleDesc(String ruleDesc) {
        this.ruleDesc = ruleDesc;
    }

    public String getIsLog() {
        return isLog;
    }

    public void setIsLog(String isLog) {
        this.isLog = isLog;
    }

    @Override
    public String getModelGroupId() {
        return modelGroupId;
    }

    @Override
    public void setModelGroupId(String modelGroupId) {
        this.modelGroupId = modelGroupId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Model{");
        sb.append("ruleId='").append(ruleId).append('\'');
        sb.append(", ruleStatus='").append(ruleStatus).append('\'');
        sb.append(", ruleDesc='").append(ruleDesc).append('\'');
        sb.append(", isLog='").append(isLog).append('\'');
        sb.append(", modelGroupId='").append(modelGroupId).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", modelHeaderId='").append(modelHeaderId).append('\'');
        sb.append(", ruleName='").append(ruleName).append('\'');
        sb.append(", ruleType='").append(ruleType).append('\'');
        sb.append(", ruleHeaderDesc='").append(ruleHeaderDesc).append('\'');
        sb.append(", modelGroupId='").append(modelGroupId).append('\'');
        sb.append(", createDate=").append(createDate);
        sb.append(", createPerson='").append(createPerson).append('\'');
        sb.append(", updatePerson='").append(updatePerson).append('\'');
        sb.append(", updateDate=").append(updateDate);
        sb.append('}');
        return sb.toString();
    }
}
