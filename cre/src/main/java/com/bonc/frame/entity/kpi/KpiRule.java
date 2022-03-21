package com.bonc.frame.entity.kpi;


import java.io.Serializable;

/**
 * @author zhengbao.King
 */
public class KpiRule implements Serializable {
    private static final long serialVersionUID = -3442466132783327372L;
    private String kpiId;
    private String ruleId;
    private String kpiRange;


    public String getKpiId() {
        return kpiId;
    }

    public void setKpiId(String kpiId) {
        this.kpiId = kpiId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getKpiRange() {
        return kpiRange;
    }

    public void setKpiRange(String kpiRange) {
        this.kpiRange = kpiRange;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KpiRule{");
        sb.append("kpiId='").append(kpiId).append('\'');
        sb.append(", ruleId='").append(ruleId).append('\'');
        sb.append(", kpiRange='").append(kpiRange).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
