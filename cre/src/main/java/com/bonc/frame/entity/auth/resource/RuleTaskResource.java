package com.bonc.frame.entity.auth.resource;

/**
 * @author yedunyao
 * @date 2019/8/16 10:38
 */
public class RuleTaskResource extends DataResource implements Cloneable {

    private String taskId;

    private String taskName;

    private String packageId;

    private String packageName;

    private String ruleId;

    private String ruleName;

    private String ruleVersion;

    /**
     * 任务描述
     */
    private String describe;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
        super.resourceId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleVersion() {
        return ruleVersion;
    }

    public void setRuleVersion(String ruleVersion) {
        this.ruleVersion = ruleVersion;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RuleTaskResource{");
        sb.append("taskId='").append(taskId).append('\'');
        sb.append(", taskName='").append(taskName).append('\'');
        sb.append(", packageId='").append(packageId).append('\'');
        sb.append(", packageName='").append(packageName).append('\'');
        sb.append(", ruleId='").append(ruleId).append('\'');
        sb.append(", ruleName='").append(ruleName).append('\'');
        sb.append(", ruleVersion='").append(ruleVersion).append('\'');
        sb.append(", describe='").append(describe).append('\'');
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
