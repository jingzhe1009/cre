package com.bonc.frame.entity.rule;

import com.bonc.frame.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author yedunyao
 * @date 2019/9/25 18:07
 */
@ApiModel(value = "RuleDetailHeader", description = "模型头信息类",
        subTypes = {RuleDetail.class})
public class RuleDetailHeader extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -9142104750201428892L;

    @ApiModelProperty(name = "folderId", value = "场景id")
    protected String folderId;

    @ApiModelProperty("模型id")
    protected String ruleName;

    @ApiModelProperty("模型名称")
    protected String moduleName;

    @ApiModelProperty("模型描述")
    protected String ruleDesc;

    @ApiModelProperty("模型类型：0，评分；1，规则")
    protected String ruleType;

    /**
     * 是否为公共；0，私有；1，公共
     */
    @ApiModelProperty("是否为公共：0，私有；1，公共")
    protected String isPublic;

    @ApiModelProperty("模型组id")
    protected String modelGroupId;

    @ApiModelProperty("是否是头信息 : 1,是 ; 0,不是,是版本信息 ")
    protected String isHeader;

    @ApiModelProperty("部门编号")
    protected String deptId;

    @ApiModelProperty("部门名称")
    protected String deptName;

    @ApiModelProperty("合作方编号")
    protected String partnerCode;

    @ApiModelProperty("合作方名称")
    protected String partnerName;

    @ApiModelProperty("产品编号")
    protected String productCode;

    @ApiModelProperty("产品名称")
    protected String productName;

    @ApiModelProperty("系统编号")
    protected String systemCode;

    @ApiModelProperty("系统名称")
    protected String systemName;

    @ApiModelProperty("平台用户创建人")
    protected String platformCreateUserJobNumber;
    @ApiModelProperty("平台用户修改人")
    protected String platformUpdateUserJobNumber;

    // 用于关联查询后的显示
    protected String modelGroupName;

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
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

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getModelGroupId() {
        return modelGroupId;
    }

    public void setModelGroupId(String modelGroupId) {
        this.modelGroupId = modelGroupId;
    }

    public String getModelGroupName() {
        return modelGroupName;
    }

    public void setModelGroupName(String modelGroupName) {
        this.modelGroupName = modelGroupName;
    }

    public String getIsHeader() {
        return isHeader;
    }

    public void setIsHeader(String isHeader) {
        this.isHeader = isHeader;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getPlatformCreateUserJobNumber() {
        return platformCreateUserJobNumber;
    }

    public void setPlatformCreateUserJobNumber(String platformCreateUserJobNumber) {
        this.platformCreateUserJobNumber = platformCreateUserJobNumber;
    }

    public String getPlatformUpdateUserJobNumber() {
        return platformUpdateUserJobNumber;
    }

    public void setPlatformUpdateUserJobNumber(String platformUpdateUserJobNumber) {
        this.platformUpdateUserJobNumber = platformUpdateUserJobNumber;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public String toString() {
        return "RuleDetailHeader{" +
                "folderId='" + folderId + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", ruleDesc='" + ruleDesc + '\'' +
                ", ruleType='" + ruleType + '\'' +
                ", isPublic='" + isPublic + '\'' +
                ", modelGroupId='" + modelGroupId + '\'' +
                ", isHeader='" + isHeader + '\'' +
                ", deptId='" + deptId + '\'' +
                ", deptName='" + deptName + '\'' +
                ", partnerCode='" + partnerCode + '\'' +
                ", partnerName='" + partnerName + '\'' +
                ", productCode='" + productCode + '\'' +
                ", productName='" + productName + '\'' +
                ", systemCode='" + systemCode + '\'' +
                ", systemName='" + systemName + '\'' +
                ", platformCreateUserJobNumber='" + platformCreateUserJobNumber + '\'' +
                ", platformUpdateUserJobNumber='" + platformUpdateUserJobNumber + '\'' +
                ", modelGroupName='" + modelGroupName + '\'' +
                ", createDate=" + createDate +
                ", createPerson='" + createPerson + '\'' +
                ", updatePerson='" + updatePerson + '\'' +
                ", updateDate=" + updateDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleDetailHeader that = (RuleDetailHeader) o;
        return Objects.equals(folderId, that.folderId) &&
                Objects.equals(moduleName, that.moduleName) &&
                Objects.equals(ruleType, that.ruleType) &&
                Objects.equals(isPublic, that.isPublic) &&
                Objects.equals(modelGroupId, that.modelGroupId);
    }


}
