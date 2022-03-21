package com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.report;

import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.info.ExportStatistics;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.importContext.ImportContext;
import com.bonc.frame.util.ExportUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/4/5 22:11
 */
public class PubOrPriImportReport {
    private ExportStatistics statistics = new ExportStatistics();
    private ExportStatistics useImportDataStatistics = new ExportStatistics();
    private ExportStatistics useUpdateDataStatistics = new ExportStatistics();
    private ExportStatistics useSystemDataStatistics = new ExportStatistics();

    private Map<String, GroupOrFolderImportReport> folder;

    private Map<String, GroupOrFolderImportReport> modelGroup;

    private Map<String, GroupOrFolderImportReport> apiGroup;

    private Map<String, GroupOrFolderImportReport> kpiGroup;

    private Map<String, GroupOrFolderImportReport> variableGroup;

    private Map<String, GroupOrFolderImportReport> db;

    public String add(String type, String isPublic, ImportAdjustObject object, ImportContext context) throws Exception {
        String success = ImportReport.addSuccess;
        if (StringUtils.isBlank(type) || object == null) {
            throw new Exception("添加报告失败,类型:" + type + ",添加对象:" + object);
        }
        String importSuccessType = object.getImportSuccessType();
        Object toData = object.getToData();
        if (StringUtils.isBlank(importSuccessType) || toData == null) {
            throw new Exception("添加报告失败,对象成功类型为null或对象结果为null,类型:" + type + ",添加对象:" + object);
        }
        if ("1".equals(isPublic)) {
            success = addPubObject(type, isPublic, object, context);
        } else if ("0".equals(isPublic)) {
            success = addPriObject(type, isPublic, object, context);
        } else {
            throw new Exception("添加报告失败,获取对象是否是公共的失败,isPublic:[" + isPublic + ",类型:" + type + ",添加对象:" + object);
        }
        if (ImportReport.addSuccess.equals(success)) {
            addStatistics(type, toData, importSuccessType);
        }
        return success;
    }

    /**
     * 添加场景下的对象, 场景 或者场景下的参数,接口,模型
     */
    public String addPriObject(String type, String isPublic, ImportAdjustObject object, ImportContext context) throws Exception {
        String success = ImportReport.addSuccess; // -1:失败,异常    0:失败,已存在     1:成功
        if (StringUtils.isBlank(type) || object == null) {
            throw new Exception("添加报告失败,类型:" + type + ",添加对象:" + object);
        }
        if (folder == null) {
            folder = new HashMap<>();
        }
        GroupOrFolderImportReport groupOrFolderImportReport = null;
        if (ExportConstant.FOLDER.equals(type)) { // 添加场景
            success = addObject(folder, type, object, "0", context);
        } else { // 添加 场景下的参数,接口,模型
            success = addObjectChild(ExportConstant.FOLDER, folder, type, object, context);
        }
        return success;
    }

    public String addPubObject(String type, String isPublic, ImportAdjustObject object, ImportContext context) throws Exception {
        String success = ImportReport.addSuccess; // -1:失败,异常    0:失败,已存在     1:成功
        if (StringUtils.isBlank(type) || object == null) {
            success = ImportReport.addFailExist;
            throw new Exception("添加报告失败,类型:" + type + ",添加对象:" + object);
        }

        Object toData = object.getToData();
        if (toData == null) {
            success = ImportReport.addFailExist;
            throw new Exception("添加报告失败,调整后的对象中的结果对象为null;type:" + type + ",对象:" + object);
        }
        String objectGroupId = ExportUtil.getObjectGroupId(type, toData);
        Map<String, GroupOrFolderImportReport> pubGroupReportMap = getPubGroupReportMap(type);
        String dbId = null;
        GroupOrFolderImportReport groupOrFolderImportReport = null;
        switch (type) {
            case ExportConstant.FOLDER:
            case ExportConstant.MODEL_GROUP:
            case ExportConstant.KPI_GROUP:
            case ExportConstant.API_GROUP:
            case ExportConstant.VARIABLE_GROUP:
            case ExportConstant.DB:
                success = addObject(pubGroupReportMap, type, object, "0", context);
                break;
            case ExportConstant.MODEL_HEADER:
            case ExportConstant.MODEL_VERSION:
            case ExportConstant.KPI:
            case ExportConstant.API:
            case ExportConstant.VARIABLE:
            case ExportConstant.PUB_DB_TABLE:
            case ExportConstant.PRI_DB_TABLE:
                success = addObjectChild(ExportUtil.getGroupType(type), pubGroupReportMap, type, object, context);
                break;
//                if (toData instanceof MetaDataTable) {
//                    dbId = ((MetaDataTable) toData).getDbId();
//                    if (StringUtils.isBlank(dbId)) {
//                        throw new Exception("添加报告失败,通过对象获取到的组Id为null;type:" + type + ",对象:" + object);
//
//                    }
//                    groupOrFolderImportReport = pubGroupReportMap.get(dbId);
//                    if (groupOrFolderImportReport == null) {
//                        throw new Exception("添加对象失败,通过改对象的组为Null,类型:" + type + ",添加对象:" + object);
//                    }
//                    groupOrFolderImportReport.add(type, object, context);
//                }
//                break;
//            case ExportConstant.DB_COLUNM:
//                if (toData instanceof MetaDataColumn) {
//                    String tableId = ((MetaDataColumn) toData).getTableId();
//                    // TODO : 这里获取DB有问题
//                    dbId = getDbId(tableId);
//                    if (StringUtils.isBlank(dbId)) {
//                        throw new Exception("添加报告失败,通过对象获取到的组Id为null;type:" + type + ",对象:" + object);
//
//                    }
//                    groupOrFolderImportReport = pubGroupReportMap.get(dbId);
//                    if (groupOrFolderImportReport == null) {
//                        throw new Exception("添加对象失败,通过改对象的组为Null,类型:" + type + ",添加对象:" + object);
//                    }
//                    groupOrFolderImportReport.add(type, object, context);
//                }

        }
        return success;
    }

    private String getDbId(String tableId) {
        if (db == null || db.isEmpty()) {
            return null;
        }
        for (String dbId : db.keySet()) {
            GroupOrFolderImportReport dbImportReport = db.get(dbId);
            if (dbImportReport == null) {
                break;
            }
            Map<String, TableImportReport> pubDbTableMap = dbImportReport.getPubDbTable();
            if (pubDbTableMap == null || pubDbTableMap.isEmpty()) {
                break;
            }
            if (pubDbTableMap.containsKey(tableId)) {
                return dbId;
            }
        }
        return null;
    }


    private String addObject(Map<String, GroupOrFolderImportReport> map, String type, ImportAdjustObject
            object, String isForcedAdd, ImportContext context) throws Exception {
        String success = ImportReport.addSuccess;
        if (object == null) {
            success = ImportReport.addFailException;
        }

        GroupOrFolderImportReport groupOrFolderImportReport = map.get(object.getId());
        if (groupOrFolderImportReport == null) {
            groupOrFolderImportReport = object.paseGroupOrFolderReport();
            if (groupOrFolderImportReport == null) {
                success = ImportReport.addFailException;
                throw new Exception("添加对象失败,获取改对象的场景为Null,类型:" + type + ",添加对象:" + object);
            }
            if (ExportConstant.FOLDER.equals(type) || ExportConstant.MODEL_GROUP.equals(type) || ExportConstant.MODEL_HEADER.equals(type) || ExportConstant.MODEL_VERSION.equals(type)) {
                map.put(object.getId(), groupOrFolderImportReport);
            } else {
                String importSuccessType = groupOrFolderImportReport.getImportSuccessType();
                if ((StringUtils.isNotBlank(importSuccessType) && ExportConstant.importSuccessType_useUpdateData.equals(importSuccessType))
                        || (StringUtils.isNotBlank(isForcedAdd) && "1".equals(isForcedAdd))) {
                    map.put(object.getId(), groupOrFolderImportReport);
                }
            }
//            map.put(object.getId(), groupOrFolderImportReport);
        } else {
            success = ImportReport.addFailExist;
        }
        return success;
    }

    private String addObjectChild(String groupType, Map<String, GroupOrFolderImportReport> groupMap, String
            type, ImportAdjustObject object, ImportContext context) throws Exception {
        String success = ImportReport.addSuccess;
        String importSuccessType = object.getImportSuccessType();
        if ((StringUtils.isNotBlank(importSuccessType) && ExportConstant.importSuccessType_useUpdateData.equals(importSuccessType))
                || ExportConstant.FOLDER.equals(type) || ExportConstant.MODEL_GROUP.equals(type) || ExportConstant.MODEL_HEADER.equals(type) || ExportConstant.MODEL_VERSION.equals(type)) {
            GroupOrFolderImportReport groupOrFolderImportReport = null;
            Object fromData = object.getFromData();
            String objectFileFolderOrGroupId = null;
            if (ExportConstant.FOLDER.equals(groupType)) {
                objectFileFolderOrGroupId = ExportUtil.getObjectFolderId(type, fromData);
            }
//        else if (ExportConstant.DB_COLUNM.equals(type)) {
//
//        }
            else{
            	objectFileFolderOrGroupId = ExportUtil.getObjectFolderId(ExportConstant.FOLDER, fromData);
            }
            ImportAdjustObject folderOrGroupObject = context.getSuccessData(groupType, objectFileFolderOrGroupId);
            if (folderOrGroupObject != null) {
                String objectFolderId = folderOrGroupObject.getId();
                groupOrFolderImportReport = groupMap.get(objectFolderId);
            }
            if (groupOrFolderImportReport == null) { // 如果场景或组为null,就去缓存中再拿一次, 再次保存报告
                Map<String, GroupOrFolderImportReport> pubGroupReportMap = getPubGroupReportMap(groupType);
                addObject(pubGroupReportMap, groupType, folderOrGroupObject, "1", context);
                folderOrGroupObject = context.getSuccessData(groupType, objectFileFolderOrGroupId);
                if (folderOrGroupObject != null) {
                    String objectFolderId = folderOrGroupObject.getId();
                    groupOrFolderImportReport = groupMap.get(objectFolderId);
                }
                if (groupOrFolderImportReport == null) { // 如果还是null 抛异常
                    success = ImportReport.addFailException;
                    throw new Exception("添加对象失败,获取改对象的场景为Null,类型:" + type + ",添加对象:" + object);

                }
            }
            success = groupOrFolderImportReport.add(type, object, context);
        }
        return success;
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

    // -------------------------------------- other -----------------------


    private Map<String, GroupOrFolderImportReport> getPubGroupReportMap(String type) throws Exception {
        if (StringUtils.isBlank(type)) {
            throw new Exception("添加报告失败,类型:" + type);
        }
        Map<String, GroupOrFolderImportReport> result = null;
        switch (type) {
            case ExportConstant.FOLDER:
                result = folder;
                break;
            case ExportConstant.MODEL_GROUP:
                result = modelGroup;
                break;
            case ExportConstant.MODEL_HEADER:
                result = modelGroup;
                break;
            case ExportConstant.MODEL_VERSION:
                result = modelGroup;
                break;
            case ExportConstant.VARIABLE_GROUP:
                result = variableGroup;
                break;
            case ExportConstant.VARIABLE:
                result = variableGroup;
                break;
            case ExportConstant.KPI_GROUP:
                result = kpiGroup;
                break;
            case ExportConstant.KPI:
                result = kpiGroup;
                break;
            case ExportConstant.API_GROUP:
                result = apiGroup;
                break;
            case ExportConstant.API:
                result = apiGroup;
                break;
            case ExportConstant.DB:
                result = db;
                break;
            case ExportConstant.PUB_DB_TABLE:
                result = db;
                break;
            case ExportConstant.PRI_DB_TABLE:
                result = db;
                break;
//            case ExportConstant.DB_COLUNM:
//                result = db;
//                break;
        }
        if (result == null) {
            result = new HashMap<>();
            setPubGroupReportMap(type, result);
        }
        return result;
    }

    private void setPubGroupReportMap(String type, Map<String, GroupOrFolderImportReport> groupReportMap) throws
            Exception {
        if (StringUtils.isBlank(type)) {
            throw new Exception("添加报告失败,类型:" + type);
        }
        switch (type) {
            case ExportConstant.FOLDER:
                folder = groupReportMap;
                break;
            case ExportConstant.MODEL_GROUP:
                modelGroup = groupReportMap;
                break;
            case ExportConstant.MODEL_HEADER:
                modelGroup = groupReportMap;
                break;
            case ExportConstant.MODEL_VERSION:
                modelGroup = groupReportMap;
                break;
            case ExportConstant.VARIABLE_GROUP:
                variableGroup = groupReportMap;
                break;
            case ExportConstant.VARIABLE:
                variableGroup = groupReportMap;
                break;
            case ExportConstant.KPI_GROUP:
                kpiGroup = groupReportMap;
                break;
            case ExportConstant.KPI:
                kpiGroup = groupReportMap;
                break;
            case ExportConstant.API_GROUP:
                apiGroup = groupReportMap;
                break;
            case ExportConstant.API:
                apiGroup = groupReportMap;
                break;
            case ExportConstant.DB:
                db = groupReportMap;
                break;
            case ExportConstant.PUB_DB_TABLE:
                db = groupReportMap;
                break;
            case ExportConstant.PRI_DB_TABLE:
                db = groupReportMap;
                break;
            case ExportConstant.DB_COLUNM:
                db = groupReportMap;
                break;
        }
    }

    // --------------- get set -----------------------
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

    public Map<String, GroupOrFolderImportReport> getFolder() {
        return folder;
    }

    public void setFolder(Map<String, GroupOrFolderImportReport> folder) {
        this.folder = folder;
    }

    public Map<String, GroupOrFolderImportReport> getModelGroup() {
        return modelGroup;
    }

    public void setModelGroup(Map<String, GroupOrFolderImportReport> modelGroup) {
        this.modelGroup = modelGroup;
    }

    public Map<String, GroupOrFolderImportReport> getApiGroup() {
        return apiGroup;
    }

    public void setApiGroup(Map<String, GroupOrFolderImportReport> apiGroup) {
        this.apiGroup = apiGroup;
    }

    public Map<String, GroupOrFolderImportReport> getKpiGroup() {
        return kpiGroup;
    }

    public void setKpiGroup(Map<String, GroupOrFolderImportReport> kpiGroup) {
        this.kpiGroup = kpiGroup;
    }

    public Map<String, GroupOrFolderImportReport> getVariableGroup() {
        return variableGroup;
    }

    public void setVariableGroup(Map<String, GroupOrFolderImportReport> variableGroup) {
        this.variableGroup = variableGroup;
    }

    public Map<String, GroupOrFolderImportReport> getDb() {
        return db;
    }

    public void setDb(Map<String, GroupOrFolderImportReport> db) {
        this.db = db;
    }
}
