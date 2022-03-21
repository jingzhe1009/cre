package com.bonc.frame.service.modelImportAndExport.import_v1.imp;

import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.kpi.KpiFetchLimiters;
import com.bonc.frame.entity.metadata.MetaDataColumn;
import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelImport.ImportParam;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportIdOrNameCacheObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.importContext.ImportContext;
import com.bonc.frame.entity.variable.Variable;
import com.bonc.frame.service.modelImportAndExport.import_v1.ImportHelper;
import com.bonc.frame.service.modelImportAndExport.import_v1.ModelImportType;
import com.bonc.frame.util.CollectionUtil;
import com.bonc.frame.util.ExportUtil;
import com.bonc.frame.util.IdUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/25 13:26
 */
@Service
public class KpiImportAdjustServiceImp extends AbstractImportAdjustServiceImp {
    @Override
    public ModelImportType getSupport() {
        return ModelImportType.KPI;
    }

    public String updateReference(String type, ImportAdjustObject o, ImportParam importParam, ImportContext context) throws Exception {
        String success = "0";
        String updateKpiFolderOrGroupSuccess = updateObjectFolderOrGroup(type, o, ExportConstant.KPI_GROUP, importParam, context);
        if ("-1".equals(updateKpiFolderOrGroupSuccess)) {
            success = "-1";
        }
        String updateKpiRefObjectIdSuccess = updateKpiRefObjectId(type, o, importParam, context);
        if ("-1".equals(updateKpiRefObjectIdSuccess)) {
            success = "-1";
        }
        return success;

    }

    @Override
    public String adjustById(String type, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        return null;
    }

    @Override
    public String adjustKey(String type, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        String suffix = null;
        String success = null;
        do {
            success = checkAdjustObjectProperty(type, ExportConstant.KPI_CODE, true, true, importAdjustObject, suffix, toFolderId, importParam, context);
            suffix = getNextSuffix(suffix);
        } while (success == null || "-1".equals(success));
        updatetKpiSql(importAdjustObject, importParam, context); // 修改SQL
        return success;
    }

    @Override
    public String adjustOtherProperty(String type, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        String suffix = null;
        String success = null;
        do {
            success = checkAdjustObjectProperty(type, ExportConstant.KPI_NAME, false, true, importAdjustObject, suffix, toFolderId, importParam, context);
            suffix = getNextSuffix(suffix);
        } while (success == null || "-1".equals(success));

        // id
        suffix = null;
        success = null;
        do {
            success = checkAdjustObjectProperty(type, ExportConstant.KPI_ID, false, false, importAdjustObject, suffix, toFolderId, importParam, context);
            suffix = getNextSuffix(suffix);
        } while (success == null || "-1".equals(success));
        // 修改引用的KpiId
        updateKpiIdOfKpiRef(importAdjustObject, importParam, context);
        return importAdjustObject.getSuccess();
    }

    @Override
    public List<Object> updatePropertyAndgetDbObjectList(String type, ImportIdOrNameCacheObject importIdOrNameObject, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        String idOrNameType = importIdOrNameObject.getIdOrNameType();
        String idOrNameValue = importIdOrNameObject.getIdOrName();
        String idOrNameKey = importIdOrNameObject.getIdOrNameKey();
        String suffix = importIdOrNameObject.getSuffix();
        List<Object> result = new ArrayList<>();

        List<KpiDefinition> dbVariable = null;
        ImportIdOrNameCacheObject cacheneedSaveIdAndName = null;
        if (ExportConstant.KPI_CODE.equals(idOrNameType)) {
            if (!StringUtils.isBlank(idOrNameValue)) {

                if (StringUtils.isNotBlank(suffix)) {
                    if (idOrNameValue.length() > 28) {
//                        idOrNameValue = idOrNameValue.substring(0, idOrNameValue.length() - 2);
                        idOrNameValue = idOrNameValue.substring(0, 28);
                    }

                    idOrNameValue = idOrNameValue + suffix;
                }
                idOrNameKey = idOrNameValue;
                importIdOrNameObject.setIdOrName(idOrNameValue);
                importIdOrNameObject.setIdOrNameKey(idOrNameKey);

                dbVariable = kpiService.getKpiDetailWithLimiter(null, idOrNameValue, null);
            }
        } else if (ExportConstant.KPI_NAME.equals(idOrNameType)) {
            if (!StringUtils.isBlank(idOrNameValue)) {

                if (StringUtils.isNotBlank(suffix)) {
                    idOrNameValue = idOrNameValue + suffix;
                }
                idOrNameKey = idOrNameValue;
                importIdOrNameObject.setIdOrName(idOrNameValue);
                importIdOrNameObject.setIdOrNameKey(idOrNameKey);

                dbVariable = kpiService.getKpiDetailWithLimiter(null, null, idOrNameValue);

            }
        } else if (ExportConstant.KPI_ID.equals(idOrNameType)) {
            if (!StringUtils.isBlank(idOrNameValue)) {

                if (StringUtils.isNotBlank(suffix)) {
                    idOrNameValue = IdUtil.createId();
                }
                idOrNameKey = idOrNameValue;
                importIdOrNameObject.setIdOrName(idOrNameValue);
                importIdOrNameObject.setIdOrNameKey(idOrNameKey);

                dbVariable = kpiService.getKpiDetailWithLimiter(idOrNameValue, null, null);
            }
        } else {
            throw new Exception("指标调整指标时,传入的类型错误,类型:[" + type + "],属性类型:[" + idOrNameType + "],调整结果对象:[" + importAdjustObject + "]");
        }


        if (dbVariable != null && !dbVariable.isEmpty()) {
            result.addAll(dbVariable);
        }
        return result;
    }

    private void updateKpiIdOfKpiRef(ImportAdjustObject o, ImportParam importParam, ImportContext context) {
        KpiDefinition fromData = (KpiDefinition) o.getFromData();
        KpiDefinition toData = (KpiDefinition) o.getToData();
        if (!fromData.getKpiId().equals(toData.getKpiId())) {
            List<KpiFetchLimiters> kpiFetchLimitersList = toData.getKpiFetchLimitersList();
            for (KpiFetchLimiters kpiFetchLimiters : kpiFetchLimitersList) {
                kpiFetchLimiters.setKpiId(toData.getKpiId());
            }
        }
    }

    //FIXME: 应该改为从fromdata中获取id和kpiFetchLimitersList 给todata赋值
    private String updateKpiRefObjectId(String type, ImportAdjustObject o, ImportParam importParam, ImportContext context) throws Exception {
        String success = "0";
        KpiDefinition kpiDefinition = (KpiDefinition) o.getToData();
        if (kpiDefinition != null) {
            DataSource dataSource = null;
            MetaDataTable metaDataTable = null;
            String apiIdFromKpi = ExportUtil.getApiIdFromKpi(null, kpiDefinition);
            String dbIdsFromKpi = ExportUtil.getDbIdsFromKpi(null, kpiDefinition);
            String pubTableIdsFromKpi = ExportUtil.getPubTableIdsFromKpi(null, kpiDefinition);
            String kpiValueSource = kpiDefinition.getKpiValueSource(); // 如果是接口,表示接口返回的参数Id; 如果是数据源,表示数据源列的Id
            List<KpiFetchLimiters> kpiFetchLimitersList = kpiDefinition.getKpiFetchLimitersList();
            if (StringUtils.isNotBlank(apiIdFromKpi)) {
                ImportAdjustObject successDataKpi = getSuccessDataFromContext(ExportConstant.API, apiIdFromKpi, ExportConstant.KPI, kpiDefinition, importParam, context);
                if (successDataKpi == null) {
                    success = "-1";
                } else {
                    ApiConf toData = (ApiConf) successDataKpi.getToData();
                    String toDataApiId = toData.getApiId();
                    String toDataApiName = toData.getApiName();
                    if (!apiIdFromKpi.equals(toDataApiId)) {
                        kpiDefinition.setApiId(toDataApiId);
                    }
                    kpiDefinition.setApiName(toDataApiName);
                }
                if (StringUtils.isNotBlank(kpiValueSource)) {
                    ImportAdjustObject successDataColumn = getSuccessDataFromContext(ExportConstant.VARIABLE, kpiValueSource, ExportConstant.KPI, kpiDefinition, importParam, context);
                    if (successDataColumn == null) {
                        success = "-1";
                    } else {
                        Variable toData = (Variable) successDataColumn.getToData();
                        String toDataId = toData.getVariableId();
                        String variableCode = toData.getVariableCode();
                        String variableAlias = toData.getVariableAlias();
                        if (!kpiValueSource.equals(toDataId)) {
                            kpiDefinition.setKpiValueSource(toDataId);
                        }
                        kpiDefinition.setKpiValueSourceCode(variableCode);
                        kpiDefinition.setKpiValueSourceName(variableAlias);
                    }
                } else {
                    log.error("指标缺失接口取值字段", new Exception("指标缺失接口取值字段"));
                }
            }
            if (StringUtils.isNotBlank(dbIdsFromKpi)) {
                ImportAdjustObject successDataSource = getSuccessDataFromContext(ExportConstant.DB, dbIdsFromKpi, ExportConstant.KPI, kpiDefinition, importParam, context);
                if (successDataSource == null) {
                    success = "-1";
                } else {
                    DataSource toData = (DataSource) successDataSource.getToData();
                    String toDataId = toData.getDbId();
                    String dbAlias = toData.getDbAlias();
                    String dbType = toData.getDbType();
                    if (!dbIdsFromKpi.equals(toDataId)) {
                        kpiDefinition.setDbId(toDataId);
                    }
                    kpiDefinition.setDbAlias(dbAlias);
                    kpiDefinition.setDbType(dbType);
                    dataSource = toData;
                }
                if (StringUtils.isNotBlank(pubTableIdsFromKpi)) {
                    ImportAdjustObject successDataTable = getSuccessDataFromContext(ExportConstant.PUB_DB_TABLE, pubTableIdsFromKpi, ExportConstant.KPI, kpiDefinition, importParam, context);
                    if (successDataTable == null) {
                        success = "-1";
                    } else {
                        MetaDataTable toData = (MetaDataTable) successDataTable.getToData();
                        String toDataId = toData.getTableId();
                        String toTableCode = toData.getTableCode();
                        if (!pubTableIdsFromKpi.equals(toDataId)) {
                            kpiDefinition.setTableId(toDataId);
                        }
                        kpiDefinition.setTableCode(toTableCode);
                        metaDataTable = toData;
                    }
                } else {
                    log.error("指标缺失数据源表", new Exception("指标缺失数据源表"));
                }
                if (StringUtils.isNotBlank(kpiValueSource)) {
                    ImportAdjustObject successDataColumn = getSuccessDataFromContext(ExportConstant.DB_COLUNM, kpiValueSource, ExportConstant.KPI, kpiDefinition, importParam, context);
                    if (successDataColumn == null) {
                        success = "-1";
                    } else {
                        MetaDataColumn toData = (MetaDataColumn) successDataColumn.getToData();
                        String toDataId = toData.getColumnId();
                        String columnCode = toData.getColumnCode();
                        String columnName = toData.getColumnName();
                        if (!kpiValueSource.equals(toDataId)) {
                            kpiDefinition.setKpiValueSource(toDataId);
                        }
                        kpiDefinition.setKpiValueSourceCode(columnCode);
                        kpiDefinition.setKpiValueSourceName(columnName);
                    }
                } else {
                    log.error("指标缺失数据源取值字段", new Exception("指标缺失数据源取值字段"));
                }
            }

            if (kpiFetchLimitersList != null && !kpiFetchLimitersList.isEmpty()) {
                for (KpiFetchLimiters kpiFetchLimiters : kpiFetchLimitersList) {
                    String updateKpiFetchLimitersSuccess = updateKpiFetchLimitersRefId(kpiDefinition, kpiFetchLimiters, importParam, context);
                    if ("-1".equals(updateKpiFetchLimitersSuccess)) {
                        success = "-1";
                    }
                }
            }
            if ("0".equals(kpiDefinition.getFetchType())) {
                kpiService.convertKpiSql(kpiDefinition, metaDataTable, dataSource);
            }
        }
        return success;
    }


    private String updateKpiFetchLimitersRefId(KpiDefinition kpiDefinition, KpiFetchLimiters kpiFetchLimiters, ImportParam importParam, ImportContext context) throws Exception {
        String success = "0";
        if (kpiFetchLimiters != null) {
            String columnId = kpiFetchLimiters.getColumnId();
            if (StringUtils.isNotBlank(columnId)) {
                ImportAdjustObject successDataColumn = getSuccessDataFromContext(ExportConstant.DB_COLUNM, columnId, ExportConstant.KPI, kpiDefinition, importParam, context);
                if (successDataColumn == null) {
                    success = "-1";
                } else {
                    MetaDataColumn toData = (MetaDataColumn) successDataColumn.getToData();
                    String toDataId = toData.getColumnId();
                    String columnCode = toData.getColumnCode();
                    String columnName = toData.getColumnName();
                    if (!columnId.equals(toDataId)) {
                        kpiFetchLimiters.setColumnId(toDataId);
                    }
                    kpiFetchLimiters.setColumnCode(columnCode);
                    kpiFetchLimiters.setColumnName(columnName);
                    kpiFetchLimiters.setId(IdUtil.createId());
                }
            }

            String variableId = kpiFetchLimiters.getVariableId();
            if (StringUtils.isNotBlank(variableId)) {
                ImportAdjustObject successDataColumn = getSuccessDataFromContext(ExportConstant.VARIABLE, variableId, ExportConstant.KPI, kpiDefinition, importParam, context);
                if (successDataColumn == null) {
                    success = "-1";
                } else {
                    Variable toData = (Variable) successDataColumn.getToData();
                    String toDataId = toData.getVariableId();
                    String columnCode = toData.getVariableCode();
                    String columnName = toData.getVariableAlias();
                    if (!variableId.equals(toDataId)) {
                        kpiFetchLimiters.setVariableId(toDataId);
                    }
                    kpiFetchLimiters.setVariableCode(columnCode);
                    kpiFetchLimiters.setVariableName(columnName);
                    kpiFetchLimiters.setId(IdUtil.createId());
                }
            }
        }
        return success;
    }

    private void updatetKpiSql(ImportAdjustObject o, ImportParam importParam, ImportContext context) {
        KpiDefinition fromData = (KpiDefinition) o.getFromData();
        KpiDefinition kpiDefinition = (KpiDefinition) o.getToData();
        String success = "-1";
        if ("0".equals(kpiDefinition.getFetchType())) {
            DataSource dataSource = null;
            MetaDataTable metaDataTable = null;
            String dbIdsFromKpi = ExportUtil.getDbIdsFromKpi(null, fromData);
            String pubTableIdsFromKpi = ExportUtil.getPubTableIdsFromKpi(null, fromData);
            if (StringUtils.isNotBlank(dbIdsFromKpi)) {
                ImportAdjustObject successDataSource = getSuccessDataFromContext(ExportConstant.DB, dbIdsFromKpi, ExportConstant.KPI, kpiDefinition, importParam, context);
                if (successDataSource == null) {
                    success = "-1";
                } else {
                    DataSource toData = (DataSource) successDataSource.getToData();
                    String toDataId = toData.getDbId();
                    String dbAlias = toData.getDbAlias();
                    String dbType = toData.getDbType();
                    if (!dbIdsFromKpi.equals(toDataId)) {
                        kpiDefinition.setDbId(toDataId);
                    }
                    kpiDefinition.setDbAlias(dbAlias);
                    kpiDefinition.setDbType(dbType);
                    dataSource = toData;
                }
                if (StringUtils.isNotBlank(pubTableIdsFromKpi)) {
                    ImportAdjustObject successDataTable = getSuccessDataFromContext(ExportConstant.PUB_DB_TABLE, pubTableIdsFromKpi, ExportConstant.KPI, kpiDefinition, importParam, context);
                    if (successDataTable == null) {
                        success = "-1";
                    } else {
                        MetaDataTable toData = (MetaDataTable) successDataTable.getToData();
                        String toDataId = toData.getTableId();
                        String toTableCode = toData.getTableCode();
                        if (!pubTableIdsFromKpi.equals(toDataId)) {
                            kpiDefinition.setTableId(toDataId);
                        }
                        kpiDefinition.setTableCode(toTableCode);
                        metaDataTable = toData;
                    }
                } else {
                    log.error("指标缺失数据源表", new Exception("指标缺失数据源表"));
                }
            }
            kpiService.convertKpiSql(kpiDefinition, metaDataTable, dataSource);
        }
    }

    @Override
    public void dataPersistenceObjectRef(String type, ImportAdjustObject o, ImportContext context) throws Exception {
        super.dataPersistenceObjectRef(type, o, context);

        KpiDefinition kpiDefinition = (KpiDefinition) o.getFromData();

        String apiIdFromKpi = ExportUtil.getApiIdFromKpi(null, kpiDefinition);
        String dbIdsFromKpi = ExportUtil.getDbIdsFromKpi(null, kpiDefinition);
        String pubTableIdsFromKpi = ExportUtil.getPubTableIdsFromKpi(null, kpiDefinition);
        Set<String> varIdFromKpi = ExportUtil.getVarIdFromKpi(null, kpiDefinition);
        Set<String> columnIdsFromKpi = ExportUtil.getColumnIdsFromKpi(null, kpiDefinition);
        if (!StringUtils.isEmpty(apiIdFromKpi)) {
            ImportHelper.getInstance().saveObject(ExportConstant.API, apiIdFromKpi, null, context);
        }
        if (!StringUtils.isEmpty(dbIdsFromKpi)) {
            ImportHelper.getInstance().saveObject(ExportConstant.DB, dbIdsFromKpi, null, context);
        }
        if (!StringUtils.isEmpty(pubTableIdsFromKpi)) {
            ImportHelper.getInstance().saveObject(ExportConstant.PUB_DB_TABLE, pubTableIdsFromKpi, null, context);
        }
        if (!CollectionUtil.isEmpty(varIdFromKpi)) {
            for (String variableId : varIdFromKpi) {
                ImportHelper.getInstance().saveObject(ExportConstant.VARIABLE, variableId, null, context);
            }
        }
        if (!CollectionUtil.isEmpty(columnIdsFromKpi)) {
            for (String columnId : columnIdsFromKpi) {
                ImportHelper.getInstance().saveObject(ExportConstant.DB_COLUNM, columnId, null, context);
            }
        }
    }

    @Override
    public void dataPersistenceObject(String type, Object o, ImportContext context) throws Exception {
        if ((getSupport().getValue()).equals(type)) {
            KpiDefinition kpiDefinition = (KpiDefinition) o;
            kpiService.insertKpiPersistence(kpiDefinition);
        }
    }

}


