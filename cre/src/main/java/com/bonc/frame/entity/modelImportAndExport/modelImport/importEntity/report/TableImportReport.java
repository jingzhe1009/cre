package com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.report;

import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.importContext.ImportContext;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/4/6 19:29
 */
public class TableImportReport extends ObjectImportReport {
    private Map<String, ObjectImportReport> dbColumn;

    public String add(String type, ImportAdjustObject object, ImportContext context) throws Exception {
        if (StringUtils.isBlank(type) || object == null) {
            throw new Exception("添加报告失败,类型:" + type + ",添加对象:" + object);
        }
        String success = ImportReport.addSuccess;
        if (dbColumn == null) {
            dbColumn = new HashMap<>();
        }
        ObjectImportReport objectImportReport = dbColumn.get(object.getId());
        if (objectImportReport == null) {
            objectImportReport = object.paseObjectReport();
            if (objectImportReport == null) {
                success = ImportReport.addFailException;
                throw new Exception("添加对象失败,获取改对象的场景为Null,类型:" + type + ",添加对象:" + object);
            }
            dbColumn.put(object.getId(), objectImportReport);
        } else {
            success = ImportReport.addFailExist;
        }
        return success;
    }

    public Map<String, ObjectImportReport> getDbColumn() {
        return dbColumn;
    }

    public void setDbColumn(Map<String, ObjectImportReport> dbColumn) {
        this.dbColumn = dbColumn;
    }
}
