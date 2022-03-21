package com.bonc.frame.entity.rule;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@ApiModel("模型详细信息类")
public class RuleDetail extends RuleDetailHeader implements Serializable {

    private static final long serialVersionUID = 6064428372046210178L;

    @ApiModelProperty("模型id，在版本管理中，代表具体的某一个版本的模型id")
    protected String ruleId;

//    private String folderId;

//    private String ruleName;

//    private String ruleDesc;

//    private String ruleType;

    @ApiModelProperty("是否删除：0，未删除；1，已删除")
    protected String isDel;

    @ApiModelProperty("是否记录执行日志：0，否；1，是")
    protected String isLog;

    protected String isDraft; // 是否是草稿

    @ApiModelProperty("版本号")
    protected String version;

    @ApiModelProperty("版本描述")
    protected String versionDesc;

    @ApiModelProperty("版本创建人")
    protected String versionCreatePerson;

    @ApiModelProperty("版本创建时间")
    protected Date versionCreateDate;

    @ApiModelProperty("版本修改人")
    protected String versionUpdatePerson;

    @ApiModelProperty("版本修改时间")
    protected Date versionUpdateDate;

    @ApiModelProperty("模型状态")
    protected String ruleStatus;

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

    public String getIsDel() {
        return isDel;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel;
    }

    public String getIsLog() {
        return isLog;
    }

    public void setIsLog(String isLog) {
        this.isLog = isLog;
    }

    public String getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(String createPerson) {
        this.createPerson = createPerson;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdatePerson() {
        return updatePerson;
    }

    public void setUpdatePerson(String updatePerson) {
        this.updatePerson = updatePerson;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getRuleStatus() {
        return ruleStatus;
    }

    public void setRuleStatus(String ruleStatus) {
        this.ruleStatus = ruleStatus;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionDesc() {
        return versionDesc;
    }

    public void setVersionDesc(String versionDesc) {
        this.versionDesc = versionDesc;
    }

    public String getVersionCreatePerson() {
        return versionCreatePerson;
    }

    public void setVersionCreatePerson(String versionCreatePerson) {
        this.versionCreatePerson = versionCreatePerson;
    }

    public Date getVersionCreateDate() {
        return versionCreateDate;
    }

    public void setVersionCreateDate(Date versionCreateDate) {
        this.versionCreateDate = versionCreateDate;
    }

    public String getVersionUpdatePerson() {
        return versionUpdatePerson;
    }

    public void setVersionUpdatePerson(String versionUpdatePerson) {
        this.versionUpdatePerson = versionUpdatePerson;
    }

    public Date getVersionUpdateDate() {
        return versionUpdateDate;
    }

    public void setVersionUpdateDate(Date versionUpdateDate) {
        this.versionUpdateDate = versionUpdateDate;
    }

    public String getIsDraft() {
        return isDraft;
    }

    public void setIsDraft(String isDraft) {
        this.isDraft = isDraft;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RuleDetail{");
        sb.append("ruleId='").append(ruleId).append('\'');
        sb.append(", isDel='").append(isDel).append('\'');
        sb.append(", isLog='").append(isLog).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", versionDesc='").append(versionDesc).append('\'');
        sb.append(", versionCreatePerson='").append(versionCreatePerson).append('\'');
        sb.append(", versionCreateDate=").append(versionCreateDate);
        sb.append(", versionUpdatePerson='").append(versionUpdatePerson).append('\'');
        sb.append(", versionUpdateDate=").append(versionUpdateDate);
        sb.append(", ruleStatus='").append(ruleStatus).append('\'');
        sb.append(", folderId='").append(folderId).append('\'');
        sb.append(", ruleName='").append(ruleName).append('\'');
        sb.append(", ruleDesc='").append(ruleDesc).append('\'');
        sb.append(", ruleType='").append(ruleType).append('\'');
        sb.append(", isPublic='").append(isPublic).append('\'');
        sb.append(", modelGroupId='").append(modelGroupId).append('\'');
        sb.append(", modelGroupName='").append(modelGroupName).append('\'');
        sb.append(", createDate=").append(createDate);
        sb.append(", createPerson='").append(createPerson).append('\'');
        sb.append(", updatePerson='").append(updatePerson).append('\'');
        sb.append(", updateDate=").append(updateDate);
        sb.append('}');
        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleDetailWithBLOBs that = (RuleDetailWithBLOBs) o;
        return Objects.equals(folderId, that.folderId) &&
                Objects.equals(moduleName, that.moduleName) &&
                Objects.equals(ruleType, that.ruleType) &&
                Objects.equals(isPublic, that.isPublic) &&
                Objects.equals(modelGroupId, that.modelGroupId);
    }


}