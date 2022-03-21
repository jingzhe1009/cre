package com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.report;

import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.info.ExportStatistics;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.importContext.ImportContext;
import com.bonc.frame.util.ExportUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/4/5 19:46
 */
public class ImportReport {
    public static final String addFailException = "-2";
    public static final String addFailExist = "-1";
    public static final String addSuccess = "1";

    private ExportStatistics statistics = new ExportStatistics();
    private ExportStatistics useImportDataStatistics = new ExportStatistics();
    private ExportStatistics useUpdateDataStatistics = new ExportStatistics();
    private ExportStatistics useSystemDataStatistics = new ExportStatistics();

    private PubOrPriImportReport pri;
    private PubOrPriImportReport pub;

    // -------------------------- add -----------------------------
    public void add(String type, ImportAdjustObject object, ImportContext context) throws Exception {
        if (StringUtils.isBlank(type) || object == null) {
            throw new Exception("添加报告失败,类型:" + type + ",添加对象:" + object);
        }
        String success = ImportReport.addSuccess;
        String importSuccessType = object.getImportSuccessType();
        Object toData = object.getToData();
        if (StringUtils.isBlank(importSuccessType) || toData == null) {
            throw new Exception("添加报告失败,对象成功类型为null或对象结果为null,类型:" + type + ",添加对象:" + object);
        }
        String isPublic = ExportUtil.getObjectIsPublic(type, toData);
        try {
            if ("1".equals(isPublic)) {
                if (pub == null) {
                    pub = new PubOrPriImportReport();
                }
                success = pub.add(type, isPublic, object, context);
            } else if ("0".equals(isPublic)) {
                if (pri == null) {
                    pri = new PubOrPriImportReport();
                }
                success = pri.add(type, isPublic, object, context);
            } else {
                throw new Exception("添加报告失败,获取对象是否是公共的失败,isPublic:[" + isPublic + ",类型:" + type + ",添加对象:" + object);
            }
        } catch (Exception e) {
            e.printStackTrace();
            success = ImportReport.addFailException;
        }
        if (ImportReport.addSuccess.equals(success)) {
            addStatistics(type, toData, importSuccessType);
        }
    }

    // --------------- statistics -----------------------
    private void addStatistics(String type, Object toData, String importSuccessType) throws Exception {
        if (statistics == null) {
            statistics = new ExportStatistics();
        }
        statistics.put(type, toData);
        if (ExportConstant.importSuccessType_useImportData.equals(importSuccessType)) {
            if (useImportDataStatistics == null) {
                useImportDataStatistics = new ExportStatistics();
            }
            useImportDataStatistics.put(type, toData);
        } else if (ExportConstant.importSuccessType_useUpdateData.equals(importSuccessType)) {
            if (useUpdateDataStatistics == null) {
                useUpdateDataStatistics = new ExportStatistics();
            }
            useUpdateDataStatistics.put(type, toData);
        } else if (ExportConstant.importSuccessType_useSystemData.equals(importSuccessType)) {
            if (useSystemDataStatistics == null) {
                useSystemDataStatistics = new ExportStatistics();
            }
            useSystemDataStatistics.put(type, toData);
        }
    }

    // -------------------------- get set --------------------------
    public ExportStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(ExportStatistics statistics) {
        this.statistics = statistics;
    }

    public ExportStatistics getUseImportDataStatistics() {
        return useImportDataStatistics;
    }

    public void setUseImportDataStatistics(ExportStatistics useImportDataStatistics) {
        this.useImportDataStatistics = useImportDataStatistics;
    }

    public ExportStatistics getUseUpdateDataStatistics() {
        return useUpdateDataStatistics;
    }

    public void setUseUpdateDataStatistics(ExportStatistics useUpdateDataStatistics) {
        this.useUpdateDataStatistics = useUpdateDataStatistics;
    }

    public ExportStatistics getUseSystemDataStatistics() {
        return useSystemDataStatistics;
    }

    public void setUseSystemDataStatistics(ExportStatistics useSystemDataStatistics) {
        this.useSystemDataStatistics = useSystemDataStatistics;
    }

    public PubOrPriImportReport getPri() {
        return pri;
    }

    public void setPri(PubOrPriImportReport pri) {
        this.pri = pri;
    }

    public PubOrPriImportReport getPub() {
        return pub;
    }

    public void setPub(PubOrPriImportReport pub) {
        this.pub = pub;
    }
}
