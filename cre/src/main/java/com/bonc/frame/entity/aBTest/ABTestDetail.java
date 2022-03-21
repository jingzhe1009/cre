package com.bonc.frame.entity.aBTest;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author yedunyao
 * @since 2020/9/1 14:54
 */
public class ABTestDetail extends ABTest implements Serializable {

    private static final long serialVersionUID = -2858411474768860602L;

    private String aModelName;
    private String aModelVerson;
    private String bModelName;
    private String bModelVerson;
    private String ruleName;

    public String getaModelName() {
        return aModelName;
    }

    public void setaModelName(String aModelName) {
        this.aModelName = aModelName;
    }

    public String getaModelVerson() {
        return aModelVerson;
    }

    public void setaModelVerson(String aModelVerson) {
        this.aModelVerson = aModelVerson;
    }

    public String getbModelName() {
        return bModelName;
    }

    public void setbModelName(String bModelName) {
        this.bModelName = bModelName;
    }

    public String getbModelVerson() {
        return bModelVerson;
    }

    public void setbModelVerson(String bModelVerson) {
        this.bModelVerson = bModelVerson;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o instanceof ABTest) {
            ABTest abTest = (ABTest) o;
            return Objects.equals(aBTestId, abTest.aBTestId);
        }
        return false;
    }

    @Override
    public int hashCode() {

        return Objects.hash(aBTestId);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ABTestDetail{");
        sb.append("aModelName='").append(aModelName).append('\'');
        sb.append(", aModelVerson='").append(aModelVerson).append('\'');
        sb.append(", bModelName='").append(bModelName).append('\'');
        sb.append(", bModelVerson='").append(bModelVerson).append('\'');
        sb.append(", ruleName='").append(ruleName).append('\'');
        sb.append(", aBTestId='").append(aBTestId).append('\'');
        sb.append(", aBTestName='").append(aBTestName).append('\'');
        sb.append(", aBTestDesc='").append(aBTestDesc).append('\'');
        sb.append(", folderId='").append(folderId).append('\'');
        sb.append(", folderName='").append(folderName).append('\'');
        sb.append(", aRuleId='").append(aRuleId).append('\'');
        sb.append(", bRuleId='").append(bRuleId).append('\'');
        sb.append(", productName='").append(productName).append('\'');
        sb.append(", productCode='").append(productCode).append('\'');
        sb.append(", createUserJobNumber='").append(createUserJobNumber).append('\'');
        sb.append(", updateUserJobNumber='").append(updateUserJobNumber).append('\'');
        sb.append(", aFetchCount=").append(aFetchCount);
        sb.append(", aSuccessCount=").append(aSuccessCount);
        sb.append(", aFailedCount=").append(aFailedCount);
        sb.append(", bExecuteCount=").append(bExecuteCount);
        sb.append(", bSuccessCount=").append(bSuccessCount);
        sb.append(", bFailedCount=").append(bFailedCount);
        sb.append(", status='").append(status).append('\'');
        sb.append(", exception='").append(exception).append('\'');
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", createDate=").append(createDate);
        sb.append(", createPerson='").append(createPerson).append('\'');
        sb.append(", updatePerson='").append(updatePerson).append('\'');
        sb.append(", updateDate=").append(updateDate);
        sb.append(", aFetchStartTime=").append(aFetchStartTime);
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args) {
        ABTest abTest = new ABTest();
        abTest.setaBTestId("1");
        ABTest abTest1 = new ABTest();
        abTest1.setaBTestId("1");
        ABTest abTest2 = new ABTest();
        abTest2.setaBTestId("2");

        ABTestDetail abTestDetail = new ABTestDetail();
        abTestDetail.setaBTestId("1");
        ABTestDetail abTestDetail1 = new ABTestDetail();
        abTestDetail1.setaBTestId("1");

        System.out.println(abTest.equals(abTest1));
        System.out.println(abTest.equals(abTest2));
        System.out.println(abTest.equals(abTestDetail));
        System.out.println(abTest2.equals(abTestDetail));
        System.out.println(abTestDetail.equals(abTestDetail1));
    }

}
