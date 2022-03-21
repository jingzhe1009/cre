package com.bonc.frame.entity.modelImportAndExport.modelExport.entity.info;

import java.util.Date;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/11 0:58
 */
@Deprecated
public class ExportInfo {

    private String id;     // id
    private String operateType; // 操作类型  export/import
    private String success;    //  操作是否成功 1:成功   0:部分成功   -1:失败
    private String systemVersion;   // 系统版本 --  导出为导出的当前系统版本 ,导入为导入文件的版本
    private String fileName;        // 文件名称 -- 导出文件名称  导入的文件名称
    private String operateContent;  // 操作内容
    private Date operateDate;   //操作时间
    private String operatePerson; //操作人

    private Exception exception;
    private String exceptionMessage;

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getOperateContent() {
        return operateContent;
    }

    public void setOperateContent(String operateContent) {
        this.operateContent = operateContent;
    }

    public Date getOperateDate() {
        return operateDate;
    }

    public void setOperateDate(Date operateDate) {
        this.operateDate = operateDate;
    }

    public String getOperatePerson() {
        return operatePerson;
    }

    public void setOperatePerson(String operatePerson) {
        this.operatePerson = operatePerson;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
        if (exception != null) {
            this.exceptionMessage = exception.getMessage();
        }
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}
