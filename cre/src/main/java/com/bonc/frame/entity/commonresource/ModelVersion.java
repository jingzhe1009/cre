package com.bonc.frame.entity.commonresource;

import io.swagger.annotations.ApiModelProperty;

/**
 * 模型查看关联规则集时的版本信息：id和版本号
 */
public class ModelVersion {
    @ApiModelProperty("模型id")
    protected String ruleId;
    @ApiModelProperty("版本号")
    protected String version;

    @Override
    public String toString() {
        return "ModelVersion{" +
                "ruleId='" + ruleId + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ModelVersion(String ruleId, String version) {

        this.ruleId = ruleId;
        this.version = version;
    }

    public ModelVersion() {

    }
}
