package com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.report;

import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.importContext.ImportContext;

import java.util.List;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/4/6 14:20
 */
public class ObjectImportReport {
    private String type;
    private ImportObjectBaseInfo fromData;
    private ImportObjectBaseInfo toData;
    private String importSuccessType;

    private Exception exception;
    private List<String> exceptionMessageList;

    public String add(String type, ImportAdjustObject object, ImportContext context) throws Exception {
        return ImportReport.addSuccess;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ImportObjectBaseInfo getFromData() {
        return fromData;
    }

    public void setFromData(ImportObjectBaseInfo fromData) {
        this.fromData = fromData;
    }

    public ImportObjectBaseInfo getToData() {
        return toData;
    }

    public void setToData(ImportObjectBaseInfo toData) {
        this.toData = toData;
    }

    public String getImportSuccessType() {
        return importSuccessType;
    }

    public void setImportSuccessType(String importSuccessType) {
        this.importSuccessType = importSuccessType;
    }

    @Override
    public String toString() {
        return "ObjectImportReport{" +
                "type='" + type + '\'' +
                ", fromData=" + fromData +
                ", toData=" + toData +
                ", importSuccessType='" + importSuccessType + '\'' +
                '}';
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public List<String> getExceptionMessageList() {
        return exceptionMessageList;
    }

    public void setExceptionMessageList(List<String> exceptionMessageList) {
        this.exceptionMessageList = exceptionMessageList;
    }
}
