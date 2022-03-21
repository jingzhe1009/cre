package com.bonc.frame.entity.aBTest;

import com.bonc.frame.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author yedunyao
 * @since 2020/9/1 14:54
 */
public class ABTest extends BaseEntity implements Serializable {

    public static final String INIT = "0";          // 未开始
    public static final String READY = "5";         // 就绪 可停止
    public static final String RUNNING = "1";       // 运行中 可停止
//    public static final String PAUSE = "3";         // 暂停
    public static final String STOPPING = "6";       // 停止中 不可操作 运行中的
    public static final String STOP = "2";          // 停止 可运行
//    public static final String DELETE = "6";          // 删除
    public static final String EXCEPTION = "3";    // 异常
//    public static final String ABNOMAL_STOP = "-2";    // 异常停止 可运行
    public static final String ONLINE = "4";    // 已上线

    private static final long serialVersionUID = -1792743808250199368L;

    protected String aBTestId;

    protected String aBTestName;

    protected String aBTestDesc;

    protected String folderId;
    protected String folderName;

    protected String aRuleId;

    protected String bRuleId;

    protected String productName;
    protected String productCode;

    protected String createUserJobNumber;
    protected String updateUserJobNumber;

    protected long aFetchCount;
    protected long aSuccessCount;
    protected long aFailedCount;

    protected long bExecuteCount;
    protected long bSuccessCount;
    protected long bFailedCount;

    protected String status;

    protected String exception;

    protected Date startTime;
    protected Date endTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    protected Date aFetchStartTime;

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

    public String getaBTestDesc() {
        return aBTestDesc;
    }

    public void setaBTestDesc(String aBTestDesc) {
        this.aBTestDesc = aBTestDesc;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getCreateUserJobNumber() {
        return createUserJobNumber;
    }

    public void setCreateUserJobNumber(String createUserJobNumber) {
        this.createUserJobNumber = createUserJobNumber;
    }

    public String getUpdateUserJobNumber() {
        return updateUserJobNumber;
    }

    public void setUpdateUserJobNumber(String updateUserJobNumber) {
        this.updateUserJobNumber = updateUserJobNumber;
    }

    public long getaFetchCount() {
        return aFetchCount;
    }

    public void setaFetchCount(long aFetchCount) {
        this.aFetchCount = aFetchCount;
    }

    public long getaSuccessCount() {
        return aSuccessCount;
    }

    public void setaSuccessCount(long aSuccessCount) {
        this.aSuccessCount = aSuccessCount;
    }

    public long getaFailedCount() {
        return aFailedCount;
    }

    public void setaFailedCount(long aFailedCount) {
        this.aFailedCount = aFailedCount;
    }

    public long getbExecuteCount() {
        return bExecuteCount;
    }

    public void setbExecuteCount(long bExecuteCount) {
        this.bExecuteCount = bExecuteCount;
    }

    public long getbSuccessCount() {
        return bSuccessCount;
    }

    public void setbSuccessCount(long bSuccessCount) {
        this.bSuccessCount = bSuccessCount;
    }

    public long getbFailedCount() {
        return bFailedCount;
    }

    public void setbFailedCount(long bFailedCount) {
        this.bFailedCount = bFailedCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getaFetchStartTime() {
        return aFetchStartTime;
    }

    public void setaFetchStartTime(Date aFetchStartTime) {
        this.aFetchStartTime = aFetchStartTime;
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
        final StringBuilder sb = new StringBuilder("ABTest{");
        sb.append("aBTestId='").append(aBTestId).append('\'');
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
}
