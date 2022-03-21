package com.bonc.frame.entity.commonresource;

import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/9/3 14:07
 */
public class RuleSetReferenceExt extends RuleSet {

    private List<Map<String, Object>> kpiInfo;

    public List<Map<String, Object>> getKpiInfo() {
        return kpiInfo;
    }

    public void setKpiInfo(List<Map<String, Object>> kpiInfo) {
        this.kpiInfo = kpiInfo;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RuleSetReferenceExt{");
        sb.append("kpiInfo=").append(kpiInfo);
        sb.append(", ruleSetId='").append(ruleSetId).append('\'');
        sb.append(", ruleSetContent='").append(ruleSetContent).append('\'');
        sb.append(", ruleSetDesc='").append(ruleSetDesc).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", enable='").append(enable).append('\'');
        sb.append(", params='").append(params).append('\'');
        sb.append(", ruleSetHeaderId='").append(ruleSetHeaderId).append('\'');
        sb.append(", ruleSetName='").append(ruleSetName).append('\'');
        sb.append(", ruleSetGroupId='").append(ruleSetGroupId).append('\'');
        sb.append(", ruleSetHeaderDesc='").append(ruleSetHeaderDesc).append('\'');
        sb.append(", createDate=").append(createDate);
        sb.append(", createPerson='").append(createPerson).append('\'');
        sb.append(", updatePerson='").append(updatePerson).append('\'');
        sb.append(", updateDate=").append(updateDate);
        sb.append('}');
        return sb.toString();
    }
}
