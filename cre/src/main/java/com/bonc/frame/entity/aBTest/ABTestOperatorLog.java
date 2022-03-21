package com.bonc.frame.entity.aBTest;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yedunyao
 * @since 2020/9/1 14:55
 */
public class ABTestOperatorLog implements Serializable {

    private static final long serialVersionUID = -8036466324561852863L;

    private String logId;

    private String aBTestId;

    private String aBTestName;
    private String newABTestName;


    /**
     * 操作类型：0，新建；1，修改；2，删除；3，开始测试；4，停止；
     */
    private String operateType;

    private String operateContent;

    private String operatePerson;

    private Date operateTime;

    private String ip;

    private String operateUserJobNumber;

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getaBTestId() {
        return aBTestId;
    }

    public void setaBTestId(String aBTestId) {
        this.aBTestId = aBTestId;
    }

    public String getaBTestName() {
        return aBTestName;
    }

    public void setaBTestName(String aBTestName) {
        this.aBTestName = aBTestName;
    }

    public String getNewABTestName() {
        return newABTestName;
    }

    public void setNewABTestName(String newABTestName) {
        this.newABTestName = newABTestName;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getOperateContent() {
        return operateContent;
    }

    public void setOperateContent(String operateContent) {
        this.operateContent = operateContent;
    }

    public String getOperatePerson() {
        return operatePerson;
    }

    public void setOperatePerson(String operatePerson) {
        this.operatePerson = operatePerson;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getOperateUserJobNumber() {
        return operateUserJobNumber;
    }

    public void setOperateUserJobNumber(String operateUserJobNumber) {
        this.operateUserJobNumber = operateUserJobNumber;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ABTestOperatorLog{");
        sb.append("logId='").append(logId).append('\'');
        sb.append(", aBTestId='").append(aBTestId).append('\'');
        sb.append(", aBTestName='").append(aBTestName).append('\'');
        sb.append(", newABTestName='").append(newABTestName).append('\'');
        sb.append(", operateType='").append(operateType).append('\'');
        sb.append(", operateContent='").append(operateContent).append('\'');
        sb.append(", operatePerson='").append(operatePerson).append('\'');
        sb.append(", operateTime=").append(operateTime);
        sb.append(", ip='").append(ip).append('\'');
        sb.append(", operateUserJobNumber='").append(operateUserJobNumber).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
