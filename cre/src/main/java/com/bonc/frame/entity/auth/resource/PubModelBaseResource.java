package com.bonc.frame.entity.auth.resource;

import java.util.Date;

public class PubModelBaseResource extends DataResource implements Cloneable {
    private String ruleName;
    private String moduleName;
    private String folderId;
    private String ruleDesc;
    private String ruleType;
    private String isPublic;
    private String isHeader;
    private String modelGroupId;
    private Date createDate;
    private String createPerson;
    private String modelGroupName;

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

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
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

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getIsHeader() {
        return isHeader;
    }

    public void setIsHeader(String isHeader) {
        this.isHeader = isHeader;
    }

    public String getModelGroupId() {
        return modelGroupId;
    }

    public void setModelGroupId(String modelGroupId) {
        this.modelGroupId = modelGroupId;
        super.resourceId = modelGroupId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(String createPerson) {
        this.createPerson = createPerson;
    }

    public String getModelGroupName() {
        return modelGroupName;
    }

    public void setModelGroupName(String modelGroupName) {
        this.modelGroupName = modelGroupName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PubModelBaseResource{");
        sb.append("ruleName='").append(ruleName).append('\'');
        sb.append(", moduleName='").append(moduleName).append('\'');
        sb.append(", folderId='").append(folderId).append('\'');
        sb.append(", ruleDesc='").append(ruleDesc).append('\'');
        sb.append(", ruleType='").append(ruleType).append('\'');
        sb.append(", isPublic='").append(isPublic).append('\'');
        sb.append(", isHeader='").append(isHeader).append('\'');
        sb.append(", modelGroupId='").append(modelGroupId).append('\'');
        sb.append(", createDate=").append(createDate);
        sb.append(", createPerson='").append(createPerson).append('\'');
        sb.append(", modelGroupName='").append(modelGroupName).append('\'');
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
