package com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.importContext;

import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.commonresource.ApiGroup;
import com.bonc.frame.entity.commonresource.ModelGroup;
import com.bonc.frame.entity.commonresource.VariableGroup;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.kpi.KpiGroup;
import com.bonc.frame.entity.metadata.MetaDataColumn;
import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.entity.modelImportAndExport.ImportAndExportOperateLog;
import com.bonc.frame.entity.modelImportAndExport.modelExport.ExportParam;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportResult;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.data.ResultData;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report.BaseReport;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report.ModelReport;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report.ModelVersionReport;
import com.bonc.frame.entity.modelImportAndExport.modelImport.ImportParam;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportIdOrNameCacheObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportResult;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.report.ObjectImportReport;
import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.rulefolder.RuleFolder;
import com.bonc.frame.entity.variable.VariableTreeNode;
import com.bonc.frame.service.modelImportAndExport.import_v1.ImportHelper;
import com.bonc.frame.util.CollectionUtil;
import com.bonc.frame.util.ExportUtil;
import com.bonc.frame.util.IdUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/24 1:53
 */
public class ImportContext {
    Log log = LogFactory.getLog(ImportContext.class);

    private List<ImportParam> importParams; //参数
    private Map<String, String> importStrategy;//{type : 策略}

    private String importFileString;
    private ExportResult importFileObject;
    private Map<String, String> fileVariableColdIdMap; //(fileCode:fileId)

    /**
     * fileIdAndNameCache {type:{id/name/code:object}
     * type= ObjectType + "_id/_name/_code"
     */
    private Map<String, Map<String, Object>> fileIdAndNameCache;

    /**
     * needSaveIdAndNameCache {type:{id/name/code:fileId}
     * type= ObjectType + "_id/_name/_code"
     */
    private Map<String, Map<String, ImportIdOrNameCacheObject>> needSaveIdAndNameCache;
    private Map<String, Map<String, ImportIdOrNameCacheObject>> tmpNeedSaveIdAndNameCache;
    private Map<String, Map<String, ObjectImportReport>> failImportData;

    private Map<String, Map<String, Object>> needSaveData;

    private Map<String, Map<String, ImportAdjustObject>> importSuccessDataMap;
    private Map<String, Map<String, ImportAdjustObject>> tmpImportSuccessDataMap;
//    private Map<String, Map<String, Map<String, ImportAdjustObject>>> importSuccessDataMap;
    //    private Map<String, Map<String, Object>> useImportData;// {type:{fileId:{fileObject:null}}}
    //    private Map<String, Map<String, Object>> useUpdateData; // {type:{fileId:{fileObject:updateObject}}}
    //    private Map<String, Map<String, Object>> useSystemData;// {type:{fileId:{fileObject:useSysObject}}}

    private Map<String, Map<String, BaseReport>> importFailDataMap;

    private Map<String, Map<String, BaseReport>> lack;
    //    private ExportDetails dataIdsWithFrom;
    private Map<String, Map<String, BaseReport>> dataIdsWithFrom;

    private ImportResult importResult;

    // ----------------------------构造器--------------------------------------

    public ImportContext() {

    }

    public ImportContext(List<ImportParam> importParams, String importFileString, ExportResult importFileObject) throws Exception {
        if (CollectionUtil.isEmpty(importParams)) {
            throw new Exception("导入参数不能为null");
        }
        this.importParams = importParams;
        // ---- 导入文件的内容 ----
        if (StringUtils.isBlank(importFileString)) {
            throw new Exception("文件内容为null:[" + importFileString + "]");
        }
        this.importFileString = importFileString;
        if (importFileObject == null) {
            try {
                importFileObject = JSONObject.toJavaObject(JSONObject.parseObject(importFileString), ExportResult.class);
            } catch (Exception e) {
                throw new Exception("文件内容转java对象失败,文件内容有误", e);
            }
        }
        //ADD
        /*List<ExportParam> newTmp = new ArrayList<ExportParam>();
        List<ExportParam> tmp=importFileObject.getExportParams();
        for(ExportParam p:tmp) {
        	if(p.getModelGroupId()==null||"".equals(p.getModelGroupId())) {
        		p.setModelGroupId(p.getFolderId());
        	}
        	newTmp.add(p);
        }
        importFileObject.setExportParams(newTmp);*/
        
        //END
        
        ResultData data = importFileObject.getData();
        if (data == null || data.isEmpty()) {
            throw new Exception("文件中的数据为空");
        }
        ResultData tmpData = importFileObject.getData();
        tmpData.getModelHeader();
        this.importFileObject = importFileObject;
        // ---- {variableCode:variableId} -----
        Map<String, VariableTreeNode> variableMap = data.getVariable();
        if (variableMap != null && !variableMap.isEmpty()) {
            for (String variableId : variableMap.keySet()) {
                if (this.fileVariableColdIdMap == null) {
                    this.fileVariableColdIdMap = new HashMap<>();
                }
                VariableTreeNode variable = variableMap.get(variableId);
                if (variable != null && StringUtils.isNotBlank(variable.getVariableCode()) && StringUtils.isNotBlank(variable.getVariableId())) {
                    fileVariableColdIdMap.put(variable.getVariableCode(), variable.getVariableId());
                }
            }
        }
        // ------ fileIdAndNameCache ------
        putResultDataToFileIdAndNameCache(data);

    }

    public void putStrategy(String type, String strategy) {
        if (importStrategy == null) {
            importStrategy = new HashMap<>();
        }
        importStrategy.put(type, strategy);
    }

    // ------------------------------------ tmpImportSuccessDataMap 和 tmpNeedSaveIdAndNameCache to end --------------------------------------------
    public void initTmpMap(boolean isSaveTmp) throws Exception {
        if (isSaveTmp) {
            if (tmpImportSuccessDataMap != null && !tmpImportSuccessDataMap.isEmpty()) {
                for (String type : tmpImportSuccessDataMap.keySet()) {
                    Map<String, ImportAdjustObject> stringImportAdjustObjectMap = tmpImportSuccessDataMap.get(type);
                    if (stringImportAdjustObjectMap != null && !stringImportAdjustObjectMap.isEmpty()) {
                        for (String id : stringImportAdjustObjectMap.keySet()) {
                            putAdjustSuccessData(type, id, stringImportAdjustObjectMap.get(id));
                        }
                    }
                }
            }
            if (tmpNeedSaveIdAndNameCache != null && !tmpNeedSaveIdAndNameCache.isEmpty()) {
                for (String type : tmpNeedSaveIdAndNameCache.keySet()) {
                    Map<String, ImportIdOrNameCacheObject> stringImportIdOrNameCacheObjectMap = tmpNeedSaveIdAndNameCache.get(type);
                    if (stringImportIdOrNameCacheObjectMap != null && !stringImportIdOrNameCacheObjectMap.isEmpty()) {
                        for (String id : stringImportIdOrNameCacheObjectMap.keySet()) {
                            putneedSaveIdAndName(type, id, stringImportIdOrNameCacheObjectMap.get(id));
                        }
                    }
                }
            }
        }
        tmpImportSuccessDataMap = null;
        tmpNeedSaveIdAndNameCache = null;
    }

    // ------------------------------------importFileObject ---------------------------------------
    public Object getFileObject(String fileObjType, String fileObjId) throws Exception {
        if (importFileObject == null) {
            throw new Exception("获取文件中的对象失败，文件内容为null。获取文件对象类型:[" + fileObjType + "],获取对象Id:[" + fileObjId + "],文件内容:【" + importFileObject + "】");
        }
        ResultData data = importFileObject.getData();
        if (data == null || data.isEmpty()) {
            throw new Exception("文件中的数据为空");
        }
        if (ExportConstant.MODEL_GROUP.equals(fileObjType)) {
            if (ExportConstant.MODEL_GROUP_TMP_ID.equals(fileObjId)) {
                ModelGroup modelGroup = new ModelGroup();
                modelGroup.setModelGroupId(ExportConstant.MODEL_GROUP_TMP_ID);
                return modelGroup;
            }
        }
        Map typeObjectMap = data.get(fileObjType);
        if (typeObjectMap == null || typeObjectMap.isEmpty() || !typeObjectMap.containsKey(fileObjId) || typeObjectMap.get(fileObjId) == null) {
            return null;
        }
        return typeObjectMap.get(fileObjId);
    }

    // --------------------------------- fileIdAndNameCache --------------------------------------

    private void putResultDataToFileIdAndNameCache(ResultData data) throws Exception {
        if (data == null) {
            return;
        }
        Map<String, VariableTreeNode> variableMap = data.getVariable();
        if (variableMap != null && !variableMap.isEmpty()) {
            for (VariableTreeNode variable : variableMap.values()) {
                String variableId = ExportUtil.getObjectPropertyKeyValue(ExportConstant.VARIABLE_ID, variable);
                putfileIdAndName(ExportConstant.VARIABLE_ID, variableId, variable);
                String variableCode = ExportUtil.getObjectPropertyKeyValue(ExportConstant.VARIABLE_CODE, variable);
                putfileIdAndName(ExportConstant.VARIABLE_CODE, variableCode, variable);
                String variableAlias = ExportUtil.getObjectPropertyKeyValue(ExportConstant.VARIABLE_ALIAS, variable);
                putfileIdAndName(ExportConstant.VARIABLE_ALIAS, variableAlias, variable);
            }
        }
        Map<String, VariableGroup> variableGroupMap = data.getVariableGroup();
        if (variableGroupMap != null && !variableGroupMap.isEmpty()) {
            for (VariableGroup variableGroup1 : variableGroupMap.values()) {
                String variableGroupId = ExportUtil.getObjectPropertyKeyValue(ExportConstant.VARIABLE_GROUP_ID, variableGroup1);
                putfileIdAndName(ExportConstant.VARIABLE_GROUP_ID, variableGroupId, variableGroup1);
                String variableGroupName = ExportUtil.getObjectPropertyKeyValue(ExportConstant.VARIABLE_GROUP_NAME, variableGroup1);
                putfileIdAndName(ExportConstant.VARIABLE_GROUP_NAME, variableGroupName, variableGroup1);
            }
        }


        Map<String, ApiConf> apiMap = data.getApi();
        if (apiMap != null && !apiMap.isEmpty()) {
            for (ApiConf apiConf : apiMap.values()) {
                String apiId = ExportUtil.getObjectPropertyKeyValue(ExportConstant.API_ID, apiConf);
                putfileIdAndName(ExportConstant.API_ID, apiId, apiConf);
                String apiName = ExportUtil.getObjectPropertyKeyValue(ExportConstant.API_NAME, apiConf);
                putfileIdAndName(ExportConstant.API_NAME, apiName, apiConf);
            }
        }
        Map<String, ApiGroup> apiGroupMap = data.getApiGroup();
        if (apiGroupMap != null && !apiGroupMap.isEmpty()) {
            for (ApiGroup apiGroup : apiGroupMap.values()) {
                String apiGroupId = ExportUtil.getObjectPropertyKeyValue(ExportConstant.API_GROUP_ID, apiGroup);
                putfileIdAndName(ExportConstant.API_GROUP_ID, apiGroupId, apiGroup);
                String apiGroupName = ExportUtil.getObjectPropertyKeyValue(ExportConstant.API_GROUP_NAME, apiGroup);
                putfileIdAndName(ExportConstant.API_GROUP_NAME, apiGroupName, apiGroup);
            }
        }

        Map<String, DataSource> dbMap = data.getDb();
        if (dbMap != null && !dbMap.isEmpty()) {
            for (DataSource db : dbMap.values()) {
                String dbId = ExportUtil.getObjectPropertyKeyValue(ExportConstant.DB_ID, db);
                putfileIdAndName(ExportConstant.DB_ID, dbId, db);
                String variableGroupName = ExportUtil.getObjectPropertyKeyValue(ExportConstant.DB_ALIAS, db);
                putfileIdAndName(ExportConstant.DB_ALIAS, variableGroupName, db);
            }
        }

        Map<String, MetaDataTable> dataTableMap = data.getPubDbTable();
        if (dataTableMap != null && !dataTableMap.isEmpty()) {
            for (MetaDataTable metaDataTable : dataTableMap.values()) {
                String tableId = ExportUtil.getObjectPropertyKeyValue(ExportConstant.PUB_DB_TABLE_ID, metaDataTable);
                putfileIdAndName(ExportConstant.PUB_DB_TABLE_ID, tableId, metaDataTable);

                String tableName = ExportUtil.getObjectPropertyKeyValue(ExportConstant.PUB_DB_TABLE_NAME, metaDataTable);
                putfileIdAndName(ExportConstant.PUB_DB_TABLE_NAME, tableName, metaDataTable);
            }
        }

        Map<String, MetaDataColumn> dataColumnMap = data.getDbColunm();
        if (dataColumnMap != null && !dataColumnMap.isEmpty()) {
            for (MetaDataColumn metaDataColumn : dataColumnMap.values()) {
                String columnId = ExportUtil.getObjectPropertyKeyValue(ExportConstant.DB_COLUNM_ID, metaDataColumn);
                putfileIdAndName(ExportConstant.DB_COLUNM_ID, columnId, metaDataColumn);

                String columnName = ExportUtil.getObjectPropertyKeyValue(ExportConstant.DB_COLUNM_NAME, metaDataColumn);
                putfileIdAndName(ExportConstant.DB_COLUNM_NAME, columnName, metaDataColumn);
            }
        }

        Map<String, KpiDefinition> kpiDefinitionMap = data.getKpi();
        if (kpiDefinitionMap != null && !kpiDefinitionMap.isEmpty()) {
            for (KpiDefinition kpiDefinition : kpiDefinitionMap.values()) {
                String kpiId = ExportUtil.getObjectPropertyKeyValue(ExportConstant.KPI_ID, kpiDefinition);
                putfileIdAndName(ExportConstant.KPI_ID, kpiId, kpiDefinition);
                String kpiCode = ExportUtil.getObjectPropertyKeyValue(ExportConstant.KPI_CODE, kpiDefinition);
                putfileIdAndName(ExportConstant.KPI_CODE, kpiCode, kpiDefinition);
                String kpiName = ExportUtil.getObjectPropertyKeyValue(ExportConstant.KPI_NAME, kpiDefinition);
                putfileIdAndName(ExportConstant.KPI_NAME, kpiName, kpiDefinition);
            }
        }

        Map<String, KpiGroup> kpiGroupMap = data.getKpiGroup();
        if (kpiGroupMap != null && !kpiGroupMap.isEmpty()) {
            for (KpiGroup kpiGroup : kpiGroupMap.values()) {
                String kpiGroupId = ExportUtil.getObjectPropertyKeyValue(ExportConstant.KPI_GROUP_ID, kpiGroup);
                putfileIdAndName(ExportConstant.KPI_GROUP_ID, kpiGroupId, kpiGroup);
                String kpiGroupName = ExportUtil.getObjectPropertyKeyValue(ExportConstant.KPI_GROUP_NAME, kpiGroup);
                putfileIdAndName(ExportConstant.KPI_GROUP_NAME, kpiGroupName, kpiGroup);
            }
        }

        Map<String, RuleFolder> folderMap = data.getFolder();
        if (folderMap != null && !folderMap.isEmpty()) {
            for (RuleFolder folder : folderMap.values()) {
                String folderId = ExportUtil.getObjectPropertyKeyValue(ExportConstant.FOLDER_ID, folder);
                putfileIdAndName(ExportConstant.FOLDER_ID, folderId, folder);
                String folderName = ExportUtil.getObjectPropertyKeyValue(ExportConstant.FOLDER_NAME, folder);
                putfileIdAndName(ExportConstant.FOLDER_NAME, folderName, folder);
            }
        }
        Map<String, ModelGroup> modelGroupMap = data.getModelGroup();
        if (modelGroupMap != null && !modelGroupMap.isEmpty()) {
            for (ModelGroup modelGroup : modelGroupMap.values()) {
                String modelGroupId = ExportUtil.getObjectPropertyKeyValue(ExportConstant.MODEL_GROUP_ID, modelGroup);
                putfileIdAndName(ExportConstant.MODEL_GROUP_ID, modelGroupId, modelGroup);
                String modelGroupName = ExportUtil.getObjectPropertyKeyValue(ExportConstant.MODEL_GROUP_NAME, modelGroup);
                putfileIdAndName(ExportConstant.MODEL_GROUP_NAME, modelGroupName, modelGroup);
            }
        }

        Map<String, RuleDetailHeader> detailHeaderMap = data.getModelHeader();
        if (detailHeaderMap != null && !detailHeaderMap.isEmpty()) {
            for (RuleDetailHeader ruleDetailHeader : detailHeaderMap.values()) {
                String modelGroupId = ExportUtil.getObjectPropertyKeyValue(ExportConstant.MODEL_RULE_NAME, ruleDetailHeader);
                putfileIdAndName(ExportConstant.MODEL_RULE_NAME, modelGroupId, ruleDetailHeader);
                String modelGroupName = ExportUtil.getObjectPropertyKeyValue(ExportConstant.MODEL_NAME, ruleDetailHeader);
                putfileIdAndName(ExportConstant.MODEL_NAME, modelGroupName, ruleDetailHeader);
            }
        }
        Map<String, RuleDetailWithBLOBs> modelVersionMap = data.getModelVersion();
        if (modelVersionMap != null && !modelVersionMap.isEmpty()) {
            for (RuleDetailWithBLOBs ruleDetailHeader : modelVersionMap.values()) {
                String modelGroupId = ruleDetailHeader.getRuleId();
                putfileIdAndName(ExportConstant.MODEL_VERSION_ID, modelGroupId, ruleDetailHeader);
//                String modelGroupName = ruleDetailHeader.getVersion();
//                putfileIdAndName(ExportConstant.MODEL_VERSION_NUMBER, modelGroupName, ruleDetailHeader);
            }
        }
    }

    public void putfileIdAndName(String idOrNameType, String idOrName, Object fromObject) {
        if (StringUtils.isBlank(idOrNameType) || StringUtils.isBlank(idOrName)) {
//            throw new Exception("放入缓存的Id或Code或Name失败,缓存类型:[" + idOrNameType + "],缓存值:[" + idOrName + "],文件对象:[" + fromObject + "]");
            return;
        }
        if (StringUtils.isNotBlank(idOrName)) {
            if (fileIdAndNameCache == null) {
                fileIdAndNameCache = new HashMap<>();
            }
            Map<String, Object> stringImportIdOrNameCacheMap = fileIdAndNameCache.get(idOrNameType);
            if (stringImportIdOrNameCacheMap == null) {
                stringImportIdOrNameCacheMap = new HashMap<>();
                fileIdAndNameCache.put(idOrNameType, stringImportIdOrNameCacheMap);
            }
            stringImportIdOrNameCacheMap.put(idOrName, fromObject);
        }
    }

    public Object getCachefileIdAndName(String idOrNameType, String idOrName) throws Exception {
        if (StringUtils.isBlank(idOrNameType)) {
            throw new Exception("获取文件缓存的Id或Code或Name失败,缓存类型:[" + idOrNameType + "],缓存值:[" + idOrName + "]");
        }
        if (StringUtils.isBlank(idOrName)) {
            return null;
        }
        return getCachefileIdAndNameInMap(fileIdAndNameCache, idOrNameType, idOrName);
    }

    private Object getCachefileIdAndNameInMap(Map<String, Map<String, Object>> map, String idOrNameType, String idOrName) throws
            Exception {
        if (StringUtils.isBlank(idOrNameType) || StringUtils.isBlank(idOrName)) {
            throw new Exception("获取文件缓存的Id或Code或Name失败,缓存类型:[" + idOrNameType + "],缓存值:[" + idOrName + "]");
        }
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, Object> stringImportIdOrNameCacheMap = map.get(idOrNameType);
        if (stringImportIdOrNameCacheMap == null) {
            return null;
        }
        return stringImportIdOrNameCacheMap.get(idOrName);
    }


    // ------------------------------------needSaveIdAndNameCache ---------------------------------------
    public void putneedSaveTmpIdAndName(String idOrNameType, String idOrName, String fromObjectType, String fromObjectId) throws Exception {
        if (StringUtils.isBlank(idOrNameType) || StringUtils.isBlank(fromObjectType)) {
            throw new Exception("放入缓存的Id或Code或Name失败,缓存类型:[" + idOrNameType + "],缓存值:[" + idOrName + "],来源对象类型:[" + idOrNameType + "],来源文件对象Id:[" + idOrName + "]");
        }
        if (StringUtils.isNotBlank(idOrName)) {
            if (tmpNeedSaveIdAndNameCache == null) {
                tmpNeedSaveIdAndNameCache = new HashMap<>();
            }
            Map<String, ImportIdOrNameCacheObject> stringImportIdOrNameCacheMap = tmpNeedSaveIdAndNameCache.get(idOrNameType);
            if (stringImportIdOrNameCacheMap == null) {
                stringImportIdOrNameCacheMap = new HashMap<>();
                tmpNeedSaveIdAndNameCache.put(idOrNameType, stringImportIdOrNameCacheMap);
            }
            String idOrNameKey = idOrName;
            ImportIdOrNameCacheObject importIdOrNameCacheObject = new ImportIdOrNameCacheObject();
            importIdOrNameCacheObject.setIdOrNameType(idOrNameType);
            importIdOrNameCacheObject.setIdOrNameKey(idOrNameKey);
            importIdOrNameCacheObject.setIdOrName(idOrName);
            importIdOrNameCacheObject.setFromObjectType(fromObjectType);
            importIdOrNameCacheObject.setFromObjectId(fromObjectId);

            stringImportIdOrNameCacheMap.put(idOrNameKey, importIdOrNameCacheObject);
        }
    }

    public void putneedSaveTmpIdAndName(ImportIdOrNameCacheObject importIdOrNameCacheObject) throws Exception {
        String idOrNameType = importIdOrNameCacheObject.getIdOrNameType();
        String idOrNameKey = importIdOrNameCacheObject.getIdOrNameKey();
        String idOrName = importIdOrNameCacheObject.getIdOrName();
        if (StringUtils.isBlank(idOrNameType)) {
            throw new Exception("放入缓存的Id或Code或Name失败,缓存对象:[" + importIdOrNameCacheObject + "]");
        }
        if (StringUtils.isNotBlank(idOrName)) {
            if (tmpNeedSaveIdAndNameCache == null) {
                tmpNeedSaveIdAndNameCache = new HashMap<>();
            }
            Map<String, ImportIdOrNameCacheObject> stringImportIdOrNameCacheMap = tmpNeedSaveIdAndNameCache.get(idOrNameType);
            if (stringImportIdOrNameCacheMap == null) {
                stringImportIdOrNameCacheMap = new HashMap<>();
                tmpNeedSaveIdAndNameCache.put(idOrNameType, stringImportIdOrNameCacheMap);
            }
            if (StringUtils.isBlank(idOrNameKey)) {
                idOrNameKey = idOrName;
                importIdOrNameCacheObject.setIdOrNameKey(idOrNameKey);
            }
            stringImportIdOrNameCacheMap.put(idOrNameKey, importIdOrNameCacheObject);
        }
    }

    public void putneedSaveIdAndName(String idOrNameType, String idOrName, String fromObjectType, String fromObjectId) throws Exception {
        if (StringUtils.isBlank(idOrNameType) || StringUtils.isBlank(fromObjectType)) {
            throw new Exception("放入缓存的Id或Code或Name失败,缓存类型:[" + idOrNameType + "],缓存值:[" + idOrName + "],来源对象类型:[" + idOrNameType + "],来源文件对象Id:[" + idOrName + "]");
        }
        if (StringUtils.isNotBlank(idOrName)) {
            if (needSaveIdAndNameCache == null) {
                needSaveIdAndNameCache = new HashMap<>();
            }
            Map<String, ImportIdOrNameCacheObject> stringImportIdOrNameCacheMap = needSaveIdAndNameCache.get(idOrNameType);
            if (stringImportIdOrNameCacheMap == null) {
                stringImportIdOrNameCacheMap = new HashMap<>();
                needSaveIdAndNameCache.put(idOrNameType, stringImportIdOrNameCacheMap);
            }
            ImportIdOrNameCacheObject importIdOrNameCacheObject = new ImportIdOrNameCacheObject();
            importIdOrNameCacheObject.setIdOrNameType(idOrNameType);
            importIdOrNameCacheObject.setIdOrName(idOrName);
            importIdOrNameCacheObject.setFromObjectType(fromObjectType);
            importIdOrNameCacheObject.setFromObjectId(fromObjectId);

            stringImportIdOrNameCacheMap.put(idOrName, importIdOrNameCacheObject);
        }
    }

    public void putneedSaveIdAndName(String idOrNameType, String idOrName, ImportIdOrNameCacheObject importIdOrNameCacheObject) throws Exception {
        if (StringUtils.isBlank(idOrNameType)) {
            throw new Exception("放入缓存的Id或Code或Name失败,缓存类型:[" + idOrNameType + "],缓存值:[" + idOrName + "],来源对象类型:[" + idOrNameType + "],来源文件对象Id:[" + idOrName + "]");
        }
        if (StringUtils.isNotBlank(idOrName)) {
            if (needSaveIdAndNameCache == null) {
                needSaveIdAndNameCache = new HashMap<>();
            }
            Map<String, ImportIdOrNameCacheObject> stringImportIdOrNameCacheMap = needSaveIdAndNameCache.get(idOrNameType);
            if (stringImportIdOrNameCacheMap == null) {
                stringImportIdOrNameCacheMap = new HashMap<>();
                needSaveIdAndNameCache.put(idOrNameType, stringImportIdOrNameCacheMap);
            }
            stringImportIdOrNameCacheMap.put(idOrName, importIdOrNameCacheObject);
        }
    }

    public ImportIdOrNameCacheObject getCacheNeedSaveIdAndName(String idOrNameType, String idOrName) throws
            Exception {
        if (StringUtils.isBlank(idOrNameType)) {
            throw new Exception("获取缓存的Id或Code或Name失败,缓存类型:[" + idOrNameType + "],缓存值:[" + idOrName + "]");
        }
        if (StringUtils.isBlank(idOrName)) {
            return null;
        }
        ImportIdOrNameCacheObject result = getCacheNeedSaveIdAndNameInMap(needSaveIdAndNameCache, idOrNameType, idOrName);
        if (result == null) {
            result = getCacheNeedSaveIdAndNameInMap(tmpNeedSaveIdAndNameCache, idOrNameType, idOrName);
        }
        return result;
    }


    private ImportIdOrNameCacheObject getCacheNeedSaveIdAndNameInMap(Map<String, Map<String, ImportIdOrNameCacheObject>> map, String idOrNameType, String idOrName) throws
            Exception {
        if (StringUtils.isBlank(idOrNameType) || StringUtils.isBlank(idOrName)) {
            throw new Exception("获取缓存的Id或Code或Name失败,缓存类型:[" + idOrNameType + "],缓存值:[" + idOrName + "]");
        }
        if (map == null) {
            return null;
        }
        Map<String, ImportIdOrNameCacheObject> stringImportIdOrNameCacheMap = map.get(idOrNameType);
        if (stringImportIdOrNameCacheMap == null) {
            return null;
        }
        return stringImportIdOrNameCacheMap.get(idOrName);
    }

    // ------------------------------------- needSaveData -----------------------------------------------------

    /**
     * @return 返回的字符串有三种情况
     * "1" : 引用的是系统中的数据,或 已经保存到系统中了
     * "0" : 保存成功
     * "-1" : 保存失败,
     */
    public String putNeedSaveData(String type, ImportAdjustObject importAdjustObject, String objectId, Object object) throws Exception {
        String success = "0";
        if (StringUtils.isBlank(type)) {
            throw new Exception("保存数据失败,类型:[" + type + "],保存对象:[" + importAdjustObject + "]");
        }
        if (importAdjustObject != null) {
            String importSuccessType = importAdjustObject.getImportSuccessType();
            if (StringUtils.isBlank(importSuccessType) || !ExportConstant.importSuccessType_useSystemData.equals(importSuccessType)) {
                Object toData = importAdjustObject.getToData();
                String toDataObjectId = ExportUtil.getObjectId(type, toData);
                Object mapObject = getObjectInMap(needSaveData, type, toDataObjectId);
                if (mapObject == null) {
                    needSaveData = putObjectToMap(needSaveData, type, toDataObjectId, toData);
                } else {
                    success = "1";
                }
            }
        }
        if (StringUtils.isNotBlank(objectId) && object != null) {
            Object mapObject = getObjectInMap(needSaveData, type, objectId);
            if (mapObject == null) {
                needSaveData = putObjectToMap(needSaveData, type, objectId, object);
            } else {
                success = "1";
            }
        }
        return success;
    }

    public Object getNeedSaveData(String type, ImportAdjustObject importAdjustObject, String objectId) throws Exception {
        if (StringUtils.isBlank(type)) {
            throw new Exception("保存数据失败,类型:[" + type + "],保存对象:[" + importAdjustObject + "]");
        }
        if (importAdjustObject != null) {
            String importSuccessType = importAdjustObject.getImportSuccessType();
            if (StringUtils.isBlank(importSuccessType) || !ExportConstant.importSuccessType_useSystemData.equals(importSuccessType)) {
                Object toData = importAdjustObject.getToData();
                String toDataObjectId = ExportUtil.getObjectId(type, toData);
                return getObjectInMap(needSaveData, type, toDataObjectId);
            }
        }
        if (StringUtils.isNotBlank(objectId)) {
            return getObjectInMap(needSaveData, type, objectId);
        }
        return null;
    }


    //--------------------------------------importSuccessDataMap----------------------------------------------
    public ImportAdjustObject getSuccessData(String ObjectType, String fileObjectId) throws Exception {
        if (StringUtils.isBlank(ObjectType) || StringUtils.isBlank(fileObjectId)) {
        	throw new Exception("获取调整成功的数据失败,类型:[" + ObjectType + "],文件中对象的Id:[" + fileObjectId + "]");
        }
        ImportAdjustObject successData = null;
//        successData = getSuccessData(importSuccessDataMap, ObjectType, fileObjectId);
        successData = getObjectInMap(importSuccessDataMap, ObjectType, fileObjectId);
        if (successData == null) {
//            successData = getSuccessData(tmpImportSuccessDataMap, ObjectType, fileObjectId);
            successData = getObjectInMap(tmpImportSuccessDataMap, ObjectType, fileObjectId);
        }
        return successData;
    }

//    public ImportAdjustObject getSuccessData(Map<String, Map<String, ImportAdjustObject>> map, String ObjectType, String fileObjectId) throws Exception {
//        if (StringUtils.isBlank(ObjectType) || StringUtils.isBlank(fileObjectId)) {
//            throw new Exception("获取调整成功的数据失败,类型:[" + ObjectType + "],文件中对象的Id:[" + fileObjectId + "]");
//        }
//        if (map == null || map.isEmpty()) {
//            return null;
//        }
//        ImportAdjustObject successData = null;
////        for (String importSuccessType : importSuccessDataMap.keySet()) {
////            Map<String, Map<String, ImportAdjustObject>> stringMapMap = importSuccessDataMap.get(importSuccessType);
////            successData = getObjectInMap(stringMapMap, ObjectType, fileObjectId);
////        }
//
//        successData = getObjectInMap(map, ObjectType, fileObjectId);
//        return successData;
//    }

    public void putAdjustTmpSuccessData(String objectType, String fileObjectId, ImportAdjustObject importAdjustObject) throws
            Exception {
        if (StringUtils.isBlank(objectType) || StringUtils.isBlank(fileObjectId) || importAdjustObject == null) {
            throw new Exception("放入调整成功的数据失败,类型:[" + objectType + "],文件中对象的Id:[" + fileObjectId + "],调整结果对象:[" + importAdjustObject + "]");
        }
        String importSuccessType = importAdjustObject.getImportSuccessType();
        if (StringUtils.isBlank(importSuccessType)) {
            throw new Exception("放入调整成功的数据失败,成功类型为null,成功类型:[" + importSuccessType + "]类型:[" + objectType + "],文件中对象的Id:[" + fileObjectId + "],调整结果对象:[" + importAdjustObject + "]");
        }
        tmpImportSuccessDataMap = putObjectToMap(tmpImportSuccessDataMap, objectType, fileObjectId, importAdjustObject);
//        putAdjustSuccessDataToIsSuccessData(importSuccessType, objectType, fileObjectId, importAdjustObject);
    }

    public void putAdjustSuccessData(String objectType, String fileObjectId, ImportAdjustObject importAdjustObject) throws
            Exception {
        if (StringUtils.isBlank(objectType) || StringUtils.isBlank(fileObjectId) || importAdjustObject == null) {
            throw new Exception("放入调整成功的数据失败,类型:[" + objectType + "],文件中对象的Id:[" + fileObjectId + "],调整结果对象:[" + importAdjustObject + "]");
        }
        String importSuccessType = importAdjustObject.getImportSuccessType();
        if (StringUtils.isBlank(importSuccessType)) {
            throw new Exception("放入调整成功的数据失败,成功类型为null,成功类型:[" + importSuccessType + "]类型:[" + objectType + "],文件中对象的Id:[" + fileObjectId + "],调整结果对象:[" + importAdjustObject + "]");
        }
        importSuccessDataMap = putObjectToMap(importSuccessDataMap, objectType, fileObjectId, importAdjustObject);
//        putAdjustSuccessDataToIsSuccessData(importSuccessType, objectType, fileObjectId, importAdjustObject);
    }


    public void putAdjustTmpSuccessData(String importSuccessType, String objectType, String fileObjectId, Object
            fileObject, Object toObject) throws Exception {
        if (StringUtils.isBlank(importSuccessType) || StringUtils.isBlank(objectType) || StringUtils.isBlank(fileObjectId)) {
            throw new Exception("放入调整成功的数据失败,成功类型:[" + importSuccessType + "],对象类型:[" + objectType + "],文件中对象的Id:[" + fileObjectId + "],文件中的对象:[" + fileObject + "],调整后的对象:[" + toObject + "]");
        }
        ImportAdjustObject importAdjustObject = new ImportAdjustObject();
        importAdjustObject.setFromData(fileObject);
        importAdjustObject.setToData(toObject);
        importAdjustObject.setImportSuccessType(importSuccessType);
        tmpImportSuccessDataMap = putObjectToMap(tmpImportSuccessDataMap, objectType, fileObjectId, importAdjustObject);
//        putAdjustSuccessDataToIsSuccessData(importSuccessType, objectType, fileObjectId, importAdjustObject);
    }

    public void putAdjustSuccessData(String importSuccessType, String objectType, String fileObjectId, Object
            fileObject, Object toObject) throws Exception {
        if (StringUtils.isBlank(importSuccessType) || StringUtils.isBlank(objectType) || StringUtils.isBlank(fileObjectId)) {
            throw new Exception("放入调整成功的数据失败,成功类型:[" + importSuccessType + "],对象类型:[" + objectType + "],文件中对象的Id:[" + fileObjectId + "],文件中的对象:[" + fileObject + "],调整后的对象:[" + toObject + "]");
        }
        ImportAdjustObject importAdjustObject = new ImportAdjustObject();
        importAdjustObject.setFromData(fileObject);
        importAdjustObject.setToData(toObject);
        importAdjustObject.setImportSuccessType(importSuccessType);
        importSuccessDataMap = putObjectToMap(importSuccessDataMap, objectType, fileObjectId, importAdjustObject);
//        putAdjustSuccessDataToIsSuccessData(importSuccessType, objectType, fileObjectId, importAdjustObject);
    }

//    private void putAdjustSuccessDataToIsSuccessData(String importSuccessType, String objectType, String fileObjectId, ImportAdjustObject importAdjustObject) throws Exception {
//        if (StringUtils.isBlank(importSuccessType) || StringUtils.isBlank(objectType) || StringUtils.isBlank(fileObjectId)) {
//            throw new Exception("放入调整成功的数据失败,成功类型:[" + importSuccessType + "],对象类型:[" + objectType + "],文件中对象的Id:[" + fileObjectId + "],调整结果:[" + importAdjustObject + "]");
//        }
//        if (importSuccessDataMap == null) {
//            importSuccessDataMap = new HashMap<>();
//        }
////        Map<String, Map<String, ImportAdjustObject>> useSystemData = importSuccessDataMap.get(importSuccessType);
////        if (useSystemData == null) {
////            useSystemData = new HashMap<>();
////            importSuccessDataMap.put(importSuccessType, useSystemData);
////        }
////        useSystemData = putObjectToMap(useSystemData, objectType, fileObjectId, importAdjustObject);
////        importSuccessDataMap.put(importSuccessType, useSystemData);
//
//        importSuccessDataMap = putObjectToMap(importSuccessDataMap, objectType, fileObjectId, importAdjustObject);
//    }

    private <E> Map<String, Map<String, E>> putObjectToMap(Map<String, Map<String, E>> map, String key1, String key2, E o) {
        if (StringUtils.isBlank(key1) || o == null) {
            return map;
        }
        if (map == null) {
            map = new HashMap<>();
        }
        Map<String, E> map1 = map.get(key1);
        if (map1 == null) {
            map1 = new HashMap<>();
            map.put(key1, map1);
        }
        if (!StringUtils.isBlank(key2)) {
            map1.put(key2, o);
        }
        return map;
    }

    private <E> E getObjectInMap(Map<String, Map<String, E>> map, String type, String id) {
        if (StringUtils.isBlank(type) || StringUtils.isBlank(id)) {
            return null;
        }
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, E> map1 = map.get(type);
        if (map1 == null || map1.isEmpty()) {
            return null;
        }
        return map1.get(id);
    }

    // ---------------------------------------- failImportData --------------------------
    public void putFailImportData(ImportAdjustObject importAdjustObject) {
        if (importAdjustObject == null) {
            return;
        }
        String objectType = importAdjustObject.getType();
        String id = importAdjustObject.getId();
        if (StringUtils.isBlank(objectType) || StringUtils.isBlank(id)) {
            return;
        }
        ObjectImportReport objectImportReport = importAdjustObject.paseObjectReport();
        failImportData = putObjectToMap(failImportData, objectType, id, objectImportReport);
    }

    // ----------------------------------------lack ------------------------------------
    public void putlackData(String type, Object o, String fromType, Object fromObject) {

        addlackIdWithFrom(type, o, fromType, fromObject);
    }


    private void addlackIdWithFrom(String type, Object o, String fromObjType, Object fromObj) {
        if (StringUtils.isBlank(type)) {
            return;
        }
        log.warn("添加缺失数据,type:" + type + ",o:" + o + ",\nfromObjType:" + fromObjType + ",fromObj:" + fromObj);
        if (o instanceof String) {
            o = ExportUtil.paseObjectIdToObject(type, (String) o);
        }
        if (lack == null) {
            lack = new HashMap<>();
        }
        Map<String, BaseReport> map = lack.get(type);
        if (map == null) {
            map = new HashMap<>();
            lack.put(type, map);
        }
        if (ExportConstant.MODEL_VERSION.equals(type)) {
            if (o instanceof RuleDetailWithBLOBs) {
                addModelVersionLackWithFrom(type, (RuleDetailWithBLOBs) o, fromObjType, fromObj);
            }
            return;
        }
        String key = ExportUtil.getObjectId(type, o);
        if (StringUtils.isBlank(key)) {
            key = ExportUtil.getObjectName(type, o);
        }
        BaseReport report = map.get(key);
        if (report == null) {
            report = ExportUtil.paseReport(type, o);
        }
        if (report != null) {
            report.addObjectFrom(fromObjType, fromObj);
            map.put(key, report);
        }
    }

    private void addModelVersionLackWithFrom(String type, RuleDetailWithBLOBs ruleDetailWithBLOBs, String fromObjType, Object fromObj) {
        if (ruleDetailWithBLOBs != null) {
            log.warn("添加缺失数据,type:" + type + ",ruleDetailWithBLOBs:" + ruleDetailWithBLOBs + ",\nfromObjType:" + fromObjType + ",fromObj:" + fromObj);

            if (lack == null) {
                lack = new HashMap<>();
            }
            Map<String, BaseReport> map = lack.get(ExportConstant.MODEL_HEADER);

            String modelId = ruleDetailWithBLOBs.getRuleName();
            if (map == null) {
                map = new HashMap<>();
                lack.put(ExportConstant.MODEL_HEADER, map);
            }
            if (map.containsKey(modelId)) {
                ModelReport modelReport = (ModelReport) map.get(modelId);
                if (modelReport != null) {
                    modelReport.addVersion(ruleDetailWithBLOBs);

                    String versionId = ruleDetailWithBLOBs.getRuleId();
                    Map<String, ModelVersionReport> versionList = modelReport.getVersion();
                    if (versionList != null) {
                        ModelVersionReport modelVersionReport = versionList.get(versionId);
                        if (modelVersionReport != null)
                            modelVersionReport.addObjectFrom(fromObjType, fromObj);
                    }

                }
            } else {
                ModelReport modelReport = new ModelReport();
                modelReport.setRuleName(ruleDetailWithBLOBs.getRuleName());
                modelReport.setModelName(modelId);
                modelReport.addVersion(ruleDetailWithBLOBs);

                String versionId = ruleDetailWithBLOBs.getRuleId();
                Map<String, ModelVersionReport> versionList = modelReport.getVersion();
                if (versionList != null) {
                    ModelVersionReport modelVersionReport = versionList.get(versionId);
                    if (modelVersionReport != null)
                        modelVersionReport.addObjectFrom(fromObjType, fromObj);
                }
                map.put(modelId, modelReport);
            }
        }
    }

    // ------------------------------ buildResult 生成结果 -----------------------------------------------------
    public ImportAndExportOperateLog buildResult() {
        if (importResult == null) {
            importResult = new ImportResult();
        }
        ImportAndExportOperateLog fromInfo = null;
        if (importFileObject != null) {
            fromInfo = importFileObject.getInfo();
            importResult.setFromInfo(fromInfo);
        }
//        System.out.println("importSuccessDataMap : " + JSONObject.toJSONString(importSuccessDataMap));
        ImportAndExportOperateLog toInfo = new ImportAndExportOperateLog();
        toInfo.setLogId(IdUtil.createId());
        toInfo.setOperateType("import");
        toInfo.setSystemVersion("v2.0");
        if (importStrategy != null && !importStrategy.isEmpty()) {
            toInfo.setModelImportStrategy(importStrategy.get(ExportConstant.MODEL_HEADER));
        }
        toInfo.setOperateDate(new Date());
        if (lack != null && !lack.isEmpty()) {
            importResult.setLack(lack);
            toInfo.setSuccess(ImportAndExportOperateLog.FAIL);
            toInfo.setExceptionMessage("存在缺失数据");
            importResult.setFailImportData(failImportData);
        } else {
            try {
                saveAndBulidReport();
                toInfo.setSuccess(ImportAndExportOperateLog.ALL_SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
                toInfo.setSuccess(ImportAndExportOperateLog.FAIL);
                toInfo.setException(e);
                toInfo.setExceptionMessage("调整数据,入库失败");
            }
        }
        importResult.setToInfo(toInfo);
        importResult.setImportParams(importParams);

//        System.out.println("importResult : " + JSONObject.toJSONString(importResult));

        ImportAndExportOperateLog resultLog = new ImportAndExportOperateLog();
        org.springframework.beans.BeanUtils.copyProperties(toInfo, resultLog);
        if (fromInfo != null) {
            resultLog.setSystemVersion(fromInfo.getSystemVersion());
            resultLog.setFileName(fromInfo.getFileName());
        }
        resultLog.setOperateContent(JSONObject.toJSONString(importResult));
        return resultLog;

    }

    /**
     * 保存并且将入库的添加到报告中
     *
     * @throws Exception
     */
    @Transactional
    public void saveAndBulidReport() throws Exception {

        if (importSuccessDataMap != null && !importSuccessDataMap.isEmpty()) {
            ImportAdjustObject successData = null;
            for (String type : importSuccessDataMap.keySet()) {
                Map<String, ImportAdjustObject> stringImportAdjustObjectMap = importSuccessDataMap.get(type);
                if (stringImportAdjustObjectMap != null && !stringImportAdjustObjectMap.isEmpty()) {
                    for (String id : stringImportAdjustObjectMap.keySet()) {
                        ImportAdjustObject importAdjustObject = stringImportAdjustObjectMap.get(id);
                        if (importAdjustObject == null) {
                            continue;
                        }
                        String success = importAdjustObject.getSuccess();
                        if (ImportAdjustObject.FAIL_SUCCESS_TYPE.equals(success)) {
                            importResult.putFailImportData(importAdjustObject);
                        } else {
                            ImportHelper.getInstance().saveObject(type, null, importAdjustObject, this);
                            importResult.add(type, importAdjustObject, this);
                        }
                    }
                }
            }
        }
    }


    // --------------------------------------get(),set() ------------------------------------------------------


    public List<ImportParam> getImportParams() {
        return importParams;
    }

    public void setImportParams(List<ImportParam> importParams) {
        this.importParams = importParams;
    }

    public String getImportFileString() {
        return importFileString;
    }

    public void setImportFileString(String importFileString) {
        this.importFileString = importFileString;
    }

    public ExportResult getImportFileObject() {
        return importFileObject;
    }

    public void setImportFileObject(ExportResult importFileObject) {
        this.importFileObject = importFileObject;
    }

    public Map<String, Map<String, ImportIdOrNameCacheObject>> getNeedSaveIdAndNameCache() {
        return needSaveIdAndNameCache;
    }

    public void setNeedSaveIdAndNameCache
            (Map<String, Map<String, ImportIdOrNameCacheObject>> needSaveIdAndNameCache) {
        this.needSaveIdAndNameCache = needSaveIdAndNameCache;
    }

    public Map<String, Map<String, ImportIdOrNameCacheObject>> getTmpNeedSaveIdAndNameCache() {
        return tmpNeedSaveIdAndNameCache;
    }

    public void setTmpNeedSaveIdAndNameCache(Map<String, Map<String, ImportIdOrNameCacheObject>> tmpNeedSaveIdAndNameCache) {
        this.tmpNeedSaveIdAndNameCache = tmpNeedSaveIdAndNameCache;
    }

    public Map<String, Map<String, Object>> getNeedSaveData() {
        return needSaveData;
    }

    public void setNeedSaveData(Map<String, Map<String, Object>> needSaveData) {
        this.needSaveData = needSaveData;
    }
    //    public Map<String, Map<String, Map<String, ImportAdjustObject>>> getImportSuccessDataMap() {
//        return importSuccessDataMap;
//    }
//
//    public void setImportSuccessDataMap
//            (Map<String, Map<String, Map<String, ImportAdjustObject>>> importSuccessDataMap) {
//        this.importSuccessDataMap = importSuccessDataMap;
//    }


    public Map<String, Map<String, ImportAdjustObject>> getImportSuccessDataMap() {
        return importSuccessDataMap;
    }

    public void setImportSuccessDataMap(Map<String, Map<String, ImportAdjustObject>> importSuccessDataMap) {
        this.importSuccessDataMap = importSuccessDataMap;
    }

    public Map<String, Map<String, ImportAdjustObject>> getTmpImportSuccessDataMap() {
        return tmpImportSuccessDataMap;
    }

    public void setTmpImportSuccessDataMap(Map<String, Map<String, ImportAdjustObject>> tmpImportSuccessDataMap) {
        this.tmpImportSuccessDataMap = tmpImportSuccessDataMap;
    }

    public Map<String, Map<String, BaseReport>> getLack() {
        return lack;
    }

    public void setLack(Map<String, Map<String, BaseReport>> lack) {
        this.lack = lack;
    }

    public Map<String, Map<String, BaseReport>> getDataIdsWithFrom() {
        return dataIdsWithFrom;
    }

    public void setDataIdsWithFrom(Map<String, Map<String, BaseReport>> dataIdsWithFrom) {
        this.dataIdsWithFrom = dataIdsWithFrom;
    }

    public Map<String, Map<String, BaseReport>> getImportFailDataMap() {
        return importFailDataMap;
    }

    public void setImportFailDataMap(Map<String, Map<String, BaseReport>> importFailDataMap) {
        this.importFailDataMap = importFailDataMap;
    }

    public ImportResult getImportResult() {
        return importResult;
    }

    public void setImportResult(ImportResult importResult) {
        this.importResult = importResult;
    }


    public Map<String, String> getFileVariableColdIdMap() {
        return fileVariableColdIdMap;
    }

    public void setFileVariableColdIdMap(Map<String, String> fileVariableColdIdMap) {
        this.fileVariableColdIdMap = fileVariableColdIdMap;
    }

    public Map<String, String> getImportStrategy() {
        return importStrategy;
    }

    public void setImportStrategy(Map<String, String> importStrategy) {
        this.importStrategy = importStrategy;
    }

    public Map<String, Map<String, ObjectImportReport>> getFailImportData() {
        return failImportData;
    }

    public void setFailImportData(Map<String, Map<String, ObjectImportReport>> failImportData) {
        this.failImportData = failImportData;
    }
}
