package com.bonc.frame.entity.auth.resource;

import com.google.common.base.Objects;

/**
 * @author yedunyao
 * @date 2019/8/15 11:39
 */
public class RuleResource extends DataResource implements Cloneable {

    private String ruleId;

    private String folderId;

    private String ruleName;
    private String moduleName;

    private String ruleType;

    private String folderName;

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;

    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
        super.resourceId = ruleName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("ruleId", ruleId)
                .add("resourceId", resourceId)
                .add("folderId", folderId)
                .add("ruleName", ruleName)
                .add("ruleType", ruleType)
                .add("folderName", folderName)
                .add("resources", resources)
                .toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
