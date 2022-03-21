package com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.report;

import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.importContext.ImportContext;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/4/5 22:22
 */
public class ModelImportReport extends ObjectImportReport {

    private Map<String, ObjectImportReport> version;

    public String add(String type, ImportAdjustObject object, ImportContext context) throws Exception {
        if (StringUtils.isBlank(type) || object == null) {
            throw new Exception("添加报告失败,类型:" + type + ",添加对象:" + object);
        }
        String success = ImportReport.addSuccess;
        if (version == null) {
            version = new HashMap<>();
        }
        ObjectImportReport objectImportReport = version.get(object.getId());
        if (objectImportReport == null) {
            objectImportReport = object.paseObjectReport();
            if (objectImportReport == null) {
                success = ImportReport.addFailException;
                throw new Exception("添加对象失败,获取改对象的场景为Null,类型:" + type + ",添加对象:" + object);
            }
            version.put(object.getId(), objectImportReport);
        } else {
            success = ImportReport.addFailExist;
        }
        return success;
    }

    public Map<String, ObjectImportReport> getVersion() {
        return version;
    }

    public void setVersion(Map<String, ObjectImportReport> version) {
        this.version = version;
    }
}
