package com.bonc.frame.entity.aBTest;

import java.util.Date;

/**
 * AB模型切换路径表
 *
 * AB模型切换生成一个链表或是带环的链表
 * A -> B -> C -> D
 * A -> B -> C -> D -> A -> E
 *
 * @author yedunyao
 * @since 2020/9/8 14:29
 */
public class ABSwitchPath {

    private String logId;
    private String headRuleId;
    /** 前一个模型 */
    private String pRuleId;
    /** 下一个模型 */
    private String nRuleId;

    private String operatePerson;
    private Date operateTime;
    private String operateUserJobNumber;

    public ABSwitchPath() {
    }

    public ABSwitchPath(String logId, String headRuleId, String pRuleId, String nRuleId,
                        String operatePerson, Date operateTime, String operateUserJobNumber) {
        this.logId = logId;
        this.headRuleId = headRuleId;
        this.pRuleId = pRuleId;
        this.nRuleId = nRuleId;
        this.operatePerson = operatePerson;
        this.operateTime = operateTime;
        this.operateUserJobNumber = operateUserJobNumber;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getHeadRuleId() {
        return headRuleId;
    }

    public void setHeadRuleId(String headRuleId) {
        this.headRuleId = headRuleId;
    }

    public String getpRuleId() {
        return pRuleId;
    }

    public void setpRuleId(String pRuleId) {
        this.pRuleId = pRuleId;
    }

    public String getnRuleId() {
        return nRuleId;
    }

    public void setnRuleId(String nRuleId) {
        this.nRuleId = nRuleId;
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

    public String getOperateUserJobNumber() {
        return operateUserJobNumber;
    }

    public void setOperateUserJobNumber(String operateUserJobNumber) {
        this.operateUserJobNumber = operateUserJobNumber;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ABSwitchPath{");
        sb.append("logId='").append(logId).append('\'');
        sb.append(", headRuleId='").append(headRuleId).append('\'');
        sb.append(", pRuleId='").append(pRuleId).append('\'');
        sb.append(", nRuleId='").append(nRuleId).append('\'');
        sb.append(", operatePerson='").append(operatePerson).append('\'');
        sb.append(", operateTime=").append(operateTime);
        sb.append(", operateUserJobNumber='").append(operateUserJobNumber).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
