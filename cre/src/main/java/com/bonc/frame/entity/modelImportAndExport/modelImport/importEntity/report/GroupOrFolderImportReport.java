package com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.report;

import com.bonc.frame.entity.metadata.MetaDataColumn;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.info.ExportStatistics;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.importContext.ImportContext;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/4/5 22:22
 */
public class GroupOrFolderImportReport extends ObjectImportReport {
    private ExportStatistics statistics = new ExportStatistics();
    private ExportStatistics useImportDataStatistics = new ExportStatistics();
    private ExportStatistics useUpdateDataStatistics = new ExportStatistics();
    private ExportStatistics useSystemDataStatistics = new ExportStatistics();

    private Map<String, ModelImportReport> modelHeader;
    private Map<String, ObjectImportReport> kpi;
    private Map<String, ObjectImportReport> api;
    private Map<String, ObjectImportReport> variable;
    private Map<String, TableImportReport> pubDbTable;
//    private Map<String, ObjectImportReport> dbColumn;

    public String add(String type, ImportAdjustObject object, ImportContext context) throws Exception {
        if (StringUtils.isBlank(type) || object == null) {
            throw new Exception("添加报告失败,类型:" + type + ",添加对象:" + object);
        }
        String success = ImportReport.addSuccess;
        Object toData = object.getToData();
        String importSuccessType = object.getImportSuccessType();
        switch (type) {
            case ExportConstant.MODEL_HEADER:
                success = addModelHeader(type, object, context);
                break;
            case ExportConstant.MODEL_VERSION:
                success = addModelVersion(type, object, context);
                break;
            case ExportConstant.KPI:
            case ExportConstant.API:
            case ExportConstant.VARIABLE:
                success = addApiKpiVariable(type, object, "0", context);
                break;
            case ExportConstant.PUB_DB_TABLE:
            case ExportConstant.PRI_DB_TABLE:
                success = addDbTable(type, object, "0", context);
                break;
            case ExportConstant.DB_COLUNM:
                if (toData instanceof MetaDataColumn) {
                    success = addDbColumn(type, object, context);
                }
                break;
        }

        if (ImportReport.addSuccess.equals(success)) {
            addStatistics(type, toData, importSuccessType);
        }
        return success;
    }

    private String addModelHeader(String type, ImportAdjustObject object, ImportContext context) throws Exception {
        String success = ImportReport.addSuccess;
        if (object == null) {
            success = ImportReport.addFailException;
            return success;
        }
        if (modelHeader == null) {
            modelHeader = new HashMap<>();
        }
        ModelImportReport modelImportReport = modelHeader.get(object.getId());
        if (modelImportReport == null) {
            modelImportReport = object.paseModelReport();
            if (modelImportReport == null) {
                success = ImportReport.addFailException;
                throw new Exception("添加对象失败,获取改对象的场景为Null,类型:" + type + ",添加对象:" + object);
            }
            modelHeader.put(object.getId(), modelImportReport);
        } else {
            success = ImportReport.addFailExist;
        }
        return success;
    }

    private String addModelVersion(String type, ImportAdjustObject object, ImportContext context) throws Exception {
        String success = ImportReport.addSuccess;
        ModelImportReport groupOrFolderImportReport = null;
        Object fromData = object.getFromData();
        String objectFileRuleName = ((RuleDetailWithBLOBs) fromData).getRuleName();
        ImportAdjustObject folderOrGroupObject = context.getSuccessData(ExportConstant.MODEL_HEADER, objectFileRuleName);
        if (folderOrGroupObject != null && modelHeader != null) {
            String objectFolderId = folderOrGroupObject.getId();
            groupOrFolderImportReport = modelHeader.get(objectFolderId);
        }
        if (groupOrFolderImportReport == null) { // 如果场景为null,就去缓存中再拿一次, 再次保存报告
            addModelHeader(ExportConstant.MODEL_HEADER, folderOrGroupObject, context);
            folderOrGroupObject = context.getSuccessData(ExportConstant.MODEL_HEADER, objectFileRuleName);
            if (folderOrGroupObject != null && modelHeader != null) {
                String objectFolderId = folderOrGroupObject.getId();
                groupOrFolderImportReport = modelHeader.get(objectFolderId);
            }
            if (groupOrFolderImportReport == null) {
                success = ImportReport.addFailException;
                throw new Exception("添加对象失败,获取改对象的模型头为Null,类型:" + type + ",添加对象:" + object);

            }
        }
        success = groupOrFolderImportReport.add(type, object, context);
        return success;
    }

    private String addApiKpiVariable(String type, ImportAdjustObject object, String isForcedAdd, ImportContext context) throws Exception {
        String success = ImportReport.addSuccess;
        String importSuccessType = object.getImportSuccessType();
        if ((StringUtils.isNotBlank(importSuccessType) && ExportConstant.importSuccessType_useUpdateData.equals(importSuccessType))
                || (StringUtils.isNotBlank(isForcedAdd) && "1".equals(isForcedAdd))) {
            Map<String, ObjectImportReport> pubGroupReportMap = getObjectReportMap(type);
            if (pubGroupReportMap == null) {
                pubGroupReportMap = new HashMap<>();
                setObjectReportMap(type, pubGroupReportMap);
            }
            ObjectImportReport objectImportReport = pubGroupReportMap.get(object.getId());
            if (objectImportReport == null) {
                objectImportReport = object.paseObjectReport();
                if (objectImportReport == null) {
                    success = ImportReport.addFailException;
                    throw new Exception("添加对象失败,获取改对象的场景为Null,类型:" + type + ",添加对象:" + object);
                }
                if (ExportConstant.MODEL_GROUP.equals(type) || ExportConstant.MODEL_HEADER.equals(type) || ExportConstant.MODEL_VERSION.equals(type)) {
                    pubGroupReportMap.put(object.getId(), objectImportReport);
                } else {
//                    if ((StringUtils.isNotBlank(importSuccessType) && ExportConstant.importSuccessType_useUpdateData.equals(importSuccessType))
//                            || (StringUtils.isNotBlank(isForcedAdd) && "1".equals(isForcedAdd))) {
                    pubGroupReportMap.put(object.getId(), objectImportReport);
//                    }
                }
//                    pubGroupReportMap.put(object.getId(), objectImportReport);
                success = ImportReport.addSuccess;
            } else {
                success = ImportReport.addFailExist;
            }
        }
        return success;
    }

    private String addDbTable(String type, ImportAdjustObject object, String isForcedAdd, ImportContext context) throws Exception {
        String success = ImportReport.addSuccess;
        String importSuccessType = object.getImportSuccessType();
        if ((StringUtils.isNotBlank(importSuccessType) && ExportConstant.importSuccessType_useUpdateData.equals(importSuccessType))
                || (StringUtils.isNotBlank(isForcedAdd) && "1".equals(isForcedAdd))) {
            if (pubDbTable == null) {
                pubDbTable = new HashMap<>();
            }
            TableImportReport tableImportReport = pubDbTable.get(object.getId());
            if (tableImportReport == null) {
                tableImportReport = object.paseTableReport();
                if (tableImportReport == null) {
                    success = ImportReport.addFailException;
                    throw new Exception("添加对象失败,获取改对象的场景为Null,类型:" + type + ",添加对象:" + object);
                }
//                if ((StringUtils.isNotBlank(importSuccessType) && ExportConstant.importSuccessType_useUpdateData.equals(importSuccessType))
//                        || (StringUtils.isNotBlank(isForcedAdd) && "1".equals(isForcedAdd))) {
                pubDbTable.put(object.getId(), tableImportReport);
//                }
//                    pubDbTable.put(object.getId(), tableImportReport);
                success = ImportReport.addSuccess;
            } else {
                success = ImportReport.addFailExist;
            }
        }
        return success;
    }

    private String addDbColumn(String type, ImportAdjustObject object, ImportContext context) throws Exception {
        MetaDataColumn fromData = (MetaDataColumn) object.getFromData();
        String success = ImportReport.addSuccess;
        String fileTableId = fromData.getTableId();
        if (StringUtils.isBlank(fileTableId)) {
            throw new Exception("添加报告失败,通过对象获取到的组Id为null;type:" + type + ",对象:" + object);
        }
        TableImportReport tableImportReport = null;
        ImportAdjustObject tableSuccessObject = context.getSuccessData(ExportConstant.PUB_DB_TABLE, fileTableId);
        if (tableSuccessObject != null) {
            String objectFolderId = tableSuccessObject.getId();
            tableImportReport = pubDbTable.get(objectFolderId);
        }
        if (tableImportReport == null) { // 如果场景或组为null,就去缓存中再拿一次, 再次保存报告
            addDbTable(ExportConstant.PUB_DB_TABLE, tableSuccessObject, "1", context);
            tableSuccessObject = context.getSuccessData(ExportConstant.PUB_DB_TABLE, fileTableId);
            if (tableSuccessObject != null) {
                String objectFolderId = tableSuccessObject.getId();
                tableImportReport = pubDbTable.get(objectFolderId);
            }
            if (tableImportReport == null) { // 如果还是null 抛异常
                success = ImportReport.addFailException;
                throw new Exception("添加对象失败,获取改对象的场景为Null,类型:" + type + ",添加对象:" + object);

            }
        }
        success = tableImportReport.add(type, object, context);
        return success;
    }

    private Map<String, ObjectImportReport> getObjectReportMap(String type) throws Exception {
        if (StringUtils.isBlank(type)) {
            throw new Exception("添加报告失败,类型:" + type);
        }
        Map<String, ObjectImportReport> result = null;
        switch (type) {

            case ExportConstant.VARIABLE:
                result = variable;
                break;
            case ExportConstant.KPI:
                result = kpi;
                break;
            case ExportConstant.API:
                result = api;
                break;

        }
        return result;
    }

    private void setObjectReportMap(String type, Map<String, ObjectImportReport> map) throws Exception {
        if (StringUtils.isBlank(type)) {
            throw new Exception("添加报告失败,类型:" + type);
        }
        Map result = null;
        switch (type) {
            case ExportConstant.VARIABLE:
                variable = map;
                break;
            case ExportConstant.KPI:
                kpi = map;
                break;
            case ExportConstant.API:
                api = map;
                break;
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


    // -------------------- get set --------------------


    public Map<String, ModelImportReport> getModelHeader() {
        return modelHeader;
    }

    public void setModelHeader(Map<String, ModelImportReport> modelHeader) {
        this.modelHeader = modelHeader;
    }

    public Map<String, ObjectImportReport> getKpi() {
        return kpi;
    }

    public void setKpi(Map<String, ObjectImportReport> kpi) {
        this.kpi = kpi;
    }

    public Map<String, ObjectImportReport> getApi() {
        return api;
    }

    public void setApi(Map<String, ObjectImportReport> api) {
        this.api = api;
    }

    public Map<String, ObjectImportReport> getVariable() {
        return variable;
    }

    public void setVariable(Map<String, ObjectImportReport> variable) {
        this.variable = variable;
    }

    public Map<String, TableImportReport> getPubDbTable() {
        return pubDbTable;
    }

    public void setPubDbTable(Map<String, TableImportReport> pubDbTable) {
        this.pubDbTable = pubDbTable;
    }

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
}
