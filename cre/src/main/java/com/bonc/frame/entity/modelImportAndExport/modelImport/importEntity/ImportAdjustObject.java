package com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity;

import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.report.GroupOrFolderImportReport;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.report.ModelImportReport;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.report.ObjectImportReport;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.report.TableImportReport;
import com.bonc.frame.util.ExportUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/25 16:47
 */
public class ImportAdjustObject {
    public static final String FAIL_SUCCESS_TYPE = "-1";
    public static final String CONTINUE_SUCCESS_TYPE = "0";
    public static final String SUCCESS_SUCCESS_TYPE = "1";
    private String type;
    private String id;
    private Object fromData; // 文件中的数据
    private Object toData; // 将要导入到数据库或引用的数据库中的数据

    private String importSuccessType; // useImportData:使用原来   useUpdateData:(id,code,)进行了修改  useSystemData:使用系统

    private String success; // -1:失败  0:当前属性检验成功,其他属性未知    1:成功并且结束(引用了缓存或数据库中的数据)

    private Exception exception;
    private List<String> exceptionMessageList;

    public ImportAdjustObject() {
    }

    public ImportAdjustObject(String type, Object fromData, Object toData, String importSuccessType, String success) {
        this.type = type;
        this.fromData = fromData;
        this.toData = toData;
        this.importSuccessType = importSuccessType;
        this.success = success;
    }


    public String getId() {
        if (StringUtils.isBlank(id)) {
            buildId();
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private void buildId() {
        StringBuilder result = new StringBuilder();
        String fromDataId = null;
        if (fromData != null) {
            fromDataId = ExportUtil.getObjectId(type, fromData);
        }
        String toDataId = null;
        if (toData != null) {
            toDataId = ExportUtil.getObjectId(type, toData);
        }
        if (StringUtils.isNotBlank(fromDataId)) {
            result.append(fromDataId);
        }
        if (StringUtils.isNotBlank(toDataId)) {
            result.append(toDataId);
        }
        if (StringUtils.isNotBlank(result.toString())) {
            id = result.toString();
        }
    }

    @Override
    public String toString() {
        return "ImportAdjustObject{" +
                "type='" + type + '\'' +
                ", id=" + id +
                ", fromData=" + fromData +
                ", toData=" + toData +
                ", importSuccessType='" + importSuccessType + '\'' +
                ", success='" + success + '\'' +
                '}';
    }

    public GroupOrFolderImportReport paseGroupOrFolderReport() {
        GroupOrFolderImportReport result = new GroupOrFolderImportReport();
        result.setType(type);
        result.setImportSuccessType(importSuccessType);
        result.setFromData(ExportUtil.paseImportObjectBaseInfo(type, fromData));
        result.setToData(ExportUtil.paseImportObjectBaseInfo(type, toData));
        result.setException(exception);
        result.setExceptionMessageList(exceptionMessageList);
        return result;
    }

    public ObjectImportReport paseObjectReport() {
        ObjectImportReport result = new ObjectImportReport();
        result.setType(type);
        result.setImportSuccessType(importSuccessType);
        result.setFromData(ExportUtil.paseImportObjectBaseInfo(type, fromData));
        result.setToData(ExportUtil.paseImportObjectBaseInfo(type, toData));
        result.setException(exception);
        result.setExceptionMessageList(exceptionMessageList);
        return result;
    }

    public ModelImportReport paseModelReport()  {
        ModelImportReport result = new ModelImportReport();
        result.setType(type);
        result.setImportSuccessType(importSuccessType);
        result.setFromData(ExportUtil.paseImportObjectBaseInfo(type, fromData));
        result.setToData(ExportUtil.paseImportObjectBaseInfo(type, toData));
        result.setException(exception);
        result.setExceptionMessageList(exceptionMessageList);
        return result;
    }

    public TableImportReport paseTableReport()  {
        TableImportReport result = new TableImportReport();
        result.setType(type);
        result.setImportSuccessType(importSuccessType);
        result.setFromData(ExportUtil.paseImportObjectBaseInfo(type, fromData));
        result.setToData(ExportUtil.paseImportObjectBaseInfo(type, toData));
        result.setException(exception);
        result.setExceptionMessageList(exceptionMessageList);
        return result;
    }

    // ---------------------------------  失败异常 -----------------------------
    public void addExceptionMessage(String exceptionMessage) {
        if (exceptionMessageList == null) {
            exceptionMessageList = new ArrayList<>();
        }
        exceptionMessageList.add(exceptionMessage);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getFromData() {
        return fromData;
    }

    public void setFromData(Object fromData) {
        this.fromData = fromData;
    }

    public Object getToData() {
        return toData;
    }

    public void setToData(Object toData) {
        this.toData = toData;
    }

    public String getImportSuccessType() {
        return importSuccessType;
    }

    public void setImportSuccessType(String importSuccessType) {
        this.importSuccessType = importSuccessType;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
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
