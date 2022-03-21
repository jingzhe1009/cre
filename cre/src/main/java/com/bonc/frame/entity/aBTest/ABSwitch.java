package com.bonc.frame.entity.aBTest;

/**
 * AB模型切换结果映射表
 *
 * @author yedunyao
 * @since 2020/9/8 14:14
 */
public class ABSwitch {

    private String aRuleId;

    private String bRuleId;

    public ABSwitch() {
    }

    public ABSwitch(String aRuleId, String bRuleId) {
        this.aRuleId = aRuleId;
        this.bRuleId = bRuleId;
    }

    public String getaRuleId() {
        return aRuleId;
    }

    public void setaRuleId(String aRuleId) {
        this.aRuleId = aRuleId;
    }

    public String getbRuleId() {
        return bRuleId;
    }

    public void setbRuleId(String bRuleId) {
        this.bRuleId = bRuleId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ABSwitch{");
        sb.append(", aRuleId='").append(aRuleId).append('\'');
        sb.append(", bRuleId='").append(bRuleId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
