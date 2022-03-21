package com.bonc.frame.entity.kpi;

import java.io.Serializable;

/**
 * 规则集-指标引用
 */
public class KpiRuleSet implements Serializable {
    private static final long serialVersionUID = 5551696938095846637L;
    private String kpiId;
    private String ruleSetId;

    public String getKpiId() {
        return kpiId;
    }

    public void setKpiId(String kpiId) {
        this.kpiId = kpiId;
    }

    public String getRuleSetId() {
        return ruleSetId;
    }

    public void setRuleSetId(String ruleSetId) {
        this.ruleSetId = ruleSetId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KpiRule{");
        sb.append("kpiId='").append(kpiId).append('\'');
        sb.append(", ruleSetId='").append(ruleSetId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
