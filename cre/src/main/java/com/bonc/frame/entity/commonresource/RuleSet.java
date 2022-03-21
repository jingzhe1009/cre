package com.bonc.frame.entity.commonresource;

/**
 * @author yedunyao
 * @date 2019/9/3 10:22
 */
public class RuleSet extends RuleSetHeader {

    public static final String ENABLE = "1";
    public static final String DISABLE = "0";

    protected String ruleSetId;

//    private String ruleSetName;

    protected String ruleSetContent;

    // 版本间描述
    protected String ruleSetDesc;

    // 版本号
    protected String version;

    // 规则集组
//    private String ruleSetGroupId;

//    protected String ruleSetHeaderId;

    protected String enable;

    // 引用的参数数组
    protected String params;

    public String getRuleSetId() {
        return ruleSetId;
    }

    public void setRuleSetId(String ruleSetId) {
        this.ruleSetId = ruleSetId;
    }

    public String getRuleSetContent() {
        return ruleSetContent;
    }

    public void setRuleSetContent(String ruleSetContent) {
        this.ruleSetContent = ruleSetContent;
    }

    public String getRuleSetDesc() {
        return ruleSetDesc;
    }

    public void setRuleSetDesc(String ruleSetDesc) {
        this.ruleSetDesc = ruleSetDesc;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRuleSetHeaderId() {
        return ruleSetHeaderId;
    }

    public void setRuleSetHeaderId(String ruleSetHeaderId) {
        this.ruleSetHeaderId = ruleSetHeaderId;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RuleSet{");
        sb.append("ruleSetId='").append(ruleSetId).append('\'');
        sb.append(", ruleSetContent='").append(ruleSetContent).append('\'');
        sb.append(", params='").append(params).append('\'');
        sb.append(", ruleSetDesc='").append(ruleSetDesc).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", enable='").append(enable).append('\'');
        sb.append(", ruleSetHeaderId='").append(ruleSetHeaderId).append('\'');
        sb.append(", ruleSetName='").append(ruleSetName).append('\'');
        sb.append(", ruleSetGroupId='").append(ruleSetGroupId).append('\'');
        sb.append(", createDate=").append(createDate);
        sb.append(", createPerson='").append(createPerson).append('\'');
        sb.append(", updatePerson='").append(updatePerson).append('\'');
        sb.append(", updateDate=").append(updateDate);
        sb.append('}');
        return sb.toString();
    }
}
