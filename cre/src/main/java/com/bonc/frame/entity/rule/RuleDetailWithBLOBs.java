package com.bonc.frame.entity.rule;

import java.io.Serializable;
import java.util.Objects;

public class RuleDetailWithBLOBs extends RuleDetail implements Serializable {

    private static final long serialVersionUID = 6600981111568646962L;

    private String ruleContent;

    private String ruleIntercept;

    public String getRuleContent() {
        return ruleContent;
    }

    public void setRuleContent(String ruleContent) {
        this.ruleContent = ruleContent;
    }

    public String getRuleIntercept() {
        return ruleIntercept;
    }

    public void setRuleIntercept(String ruleIntercept) {
        this.ruleIntercept = ruleIntercept;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RuleDetailWithBLOBs{");
        sb.append("ruleContent='").append(ruleContent).append('\'');
        sb.append(", ruleIntercept='").append(ruleIntercept).append('\'');
        sb.append(", ruleId='").append(ruleId).append('\'');
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
        sb.append(", isHeader='").append(isHeader).append('\'');
        sb.append(", deptId='").append(deptId).append('\'');
        sb.append(", deptName='").append(deptName).append('\'');
        sb.append(", partnerCode='").append(partnerCode).append('\'');
        sb.append(", partnerName='").append(partnerName).append('\'');
        sb.append(", productCode='").append(productCode).append('\'');
        sb.append(", productName='").append(productName).append('\'');
        sb.append(", systemCode='").append(systemCode).append('\'');
        sb.append(", systemName='").append(systemName).append('\'');
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
        boolean ruleContentEquals = Objects.equals(ruleContent, that.ruleContent);
        boolean equals = Objects.equals(folderId, that.folderId);
        boolean equals1 = Objects.equals(moduleName, that.moduleName);
        boolean equals3 = Objects.equals(ruleType, that.ruleType);
        boolean equals4 = Objects.equals(isPublic, that.isPublic);
        boolean equals5 = Objects.equals(modelGroupId, that.modelGroupId);
        boolean result = ruleContentEquals &&
                equals &&
                equals1 &&
                equals3 &&
                equals4 &&
                equals5;
        return result;
    }

}