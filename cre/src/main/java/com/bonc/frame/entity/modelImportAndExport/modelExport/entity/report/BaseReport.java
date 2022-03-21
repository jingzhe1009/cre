package com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report;

import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.commonresource.ApiGroup;
import com.bonc.frame.entity.commonresource.ModelGroup;
import com.bonc.frame.entity.commonresource.VariableGroup;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.kpi.KpiGroup;
import com.bonc.frame.entity.metadata.MetaDataColumn;
import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.info.ExportStatistics;
import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.rulefolder.RuleFolder;
import com.bonc.frame.entity.variable.Variable;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.util.ExportUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/6 2:24
 */
public class BaseReport {
    private Map<String, Map<String, BaseReport>> from; // type:{id:object}
    private ExportStatistics lackStatistics = new ExportStatistics();

    private boolean isSuccess = true;
    private Exception exception;
    private String exceptionMessage;

    public String reportId() {
        return null;
    }

    public String reportName() {
        return null;
    }

    public void putAllObjectFrom(Map<String, Map<String, BaseReport>> lack) {
        if (lack == null || lack.isEmpty()) {
            return;
        }
        if (from == null || from.isEmpty()) {
            from = lack;
        }
        for (String fromObjType : lack.keySet()) {
            Map<String, BaseReport> map = from.get(fromObjType);
            Map<String, BaseReport> addMap = lack.get(fromObjType);
            if (map == null || map.isEmpty()) {
                map = addMap;
                from.put(fromObjType, map);
            } else {
                map.putAll(addMap);
            }
        }
    }


    public void addObjectFrom(String type, Object o) {
        if (StringUtils.isBlank(type) || o == null) {
            return;
        }
        if (from == null) {
            from = new HashMap<>();
        }
        Map<String, BaseReport> map = from.get(type);
        if (map == null) {
            map = new HashMap<>();
            from.put(type, map);
        }
        if (!ExportConstant.MODEL_VERSION.equals(type)) {
            String objectId = ExportUtil.getObjectId(type, o);
            if (StringUtils.isBlank(objectId)) {
                return;
            }
            if (map.containsKey(objectId)) {
                return;
            }
            lackStatistics.put(type, o);
        }
        switch (type) {
            case ExportConstant.FOLDER:
                if (o instanceof RuleFolder) {
                    addFolderWithFrom(type, (RuleFolder) o);
                }
                break;
            case ExportConstant.MODEL_GROUP:
                if (o instanceof ModelGroup) {
                    addModelGroupWithFrom(type, (ModelGroup) o);
                }
                break;
            case ExportConstant.MODEL_VERSION:
                if (o instanceof RuleDetailWithBLOBs) {
                    addModelVersionWithFrom(type, (RuleDetailWithBLOBs) o);
                }
                break;
            case ExportConstant.MODEL_HEADER:
                if (o instanceof RuleDetailHeader) {
                    addModelHeaderWithFrom(type, (RuleDetailHeader) o);
                }
                break;
            case ExportConstant.VARIABLE_GROUP:
                if (o instanceof VariableGroup) {
                    addVariableGroupWithFrom(type, (VariableGroup) o);
                }
                break;
            case ExportConstant.VARIABLE:
                if (o instanceof Variable) {
                    addVariableWithFrom(type, (Variable) o);
                }
                break;
            case ExportConstant.KPI_GROUP:
                if (o instanceof KpiGroup) {
                    addKpiGroupWithFrom(type, (KpiGroup) o);
                }
                break;
            case ExportConstant.KPI:
                if (o instanceof KpiDefinition) {
                    addKpiWithFrom(type, (KpiDefinition) o);
                }
                break;
            case ExportConstant.API_GROUP:
                if (o instanceof ApiGroup) {
                    addApiGroupWithFrom(type, (ApiGroup) o);
                }
                break;
            case ExportConstant.API:
                if (o instanceof ApiConf) {
                    addApiWithFrom(type, (ApiConf) o);
                }
                break;
            case ExportConstant.DB:
                if (o instanceof DataSource) {
                    addDBWithFrom(type, (DataSource) o);
                }
                break;
            case ExportConstant.PUB_DB_TABLE:
            case ExportConstant.PRI_DB_TABLE:
                if (o instanceof MetaDataTable) {
                    addDBTableWithFrom(type, (MetaDataTable) o);
                }
                break;
            case ExportConstant.DB_COLUNM:
                if (o instanceof MetaDataColumn) {
                    addDBColumnWithFrom(type, (MetaDataColumn) o);
                }
                break;
            default:
        }

    }

    private void addFolderWithFrom(String type, RuleFolder folder) {
        if (folder != null) {
            if (from == null) {
                from = new HashMap<>();
            }
            Map<String, BaseReport> map = from.get(type);
            if (map == null) {
                map = new HashMap<>();
                from.put(type, map);
            }
            String folderId = folder.getFolderId();
            if (!StringUtils.isBlank(folderId)) {
                if (!map.containsKey(folderId)) {
                    FolderReport folderReport = new FolderReport();
                    folderReport.setFolderId(folderId);
                    folderReport.setFolderName(folder.getFolderName());
                    folderReport.setFolderDesc(folder.getFolderDesc());
                    map.put(folderId, folderReport);
                }
            }
        }
    }

    private void addModelGroupWithFrom(String type, ModelGroup modelGroup) {
        if (modelGroup != null) {
            if (from == null) {
                from = new HashMap<>();
            }
            Map<String, BaseReport> map = from.get(type);
            String modelGroupId = modelGroup.getModelGroupId();
            if (!StringUtils.isBlank(modelGroupId)) {
                if (map == null) {
                    map = new HashMap<>();
                    from.put(type, map);
                }
                if (!map.containsKey(modelGroupId)) {
                    ModelGroupReport modelGroupReport = new ModelGroupReport();
                    modelGroupReport.setModelGroupId(modelGroupId);
                    modelGroupReport.setModelGroupName(modelGroup.getModelGroupName());
                    map.put(modelGroupId, modelGroupReport);
                }
            }
        }
    }

    private void addModelVersionWithFrom(String type, RuleDetailWithBLOBs ruleDetailWithBLOBs) {
        if (ruleDetailWithBLOBs != null) {
            if (from == null) {
                from = new HashMap<>();
            }
            Map<String, BaseReport> map = from.get(ExportConstant.MODEL_HEADER);

            String modelId = ruleDetailWithBLOBs.getRuleName();
            if (map == null) {
                map = new HashMap<>();
                from.put(ExportConstant.MODEL_HEADER, map);
            }
            if (map.containsKey(modelId)) {
                ModelReport modelReport = (ModelReport) map.get(modelId);
                if (modelReport != null && !modelReport.containsVersion(ruleDetailWithBLOBs)) {
                    modelReport.addVersion(ruleDetailWithBLOBs);
                    lackStatistics.put(type, ruleDetailWithBLOBs);
                }
            } else {
                ModelReport modelReport = new ModelReport();
                modelReport.setRuleName(ruleDetailWithBLOBs.getRuleName());
                modelReport.setModelName(ruleDetailWithBLOBs.getModuleName());
                modelReport.addVersion(ruleDetailWithBLOBs);
                lackStatistics.put(type, ruleDetailWithBLOBs);

                map.put(modelId, modelReport);
                lackStatistics.put(ExportConstant.MODEL_HEADER, ruleDetailWithBLOBs);
            }
        }
    }

    private void addModelHeaderWithFrom(String type, RuleDetailHeader ruleDetailHeader) {
        if (ruleDetailHeader != null) {
            if (from == null) {
                from = new HashMap<>();
            }
            Map<String, BaseReport> map = from.get(type);

            String modelId = ruleDetailHeader.getRuleName();
            if (map == null) {
                map = new HashMap<>();
                from.put(type, map);
            }
            if (!map.containsKey(modelId)) {
                ModelReport modelReport = ExportUtil.paseModelHeaderReport(ruleDetailHeader);
                modelReport.setRuleName(ruleDetailHeader.getRuleName());
                modelReport.setModelName(modelId);
                map.put(modelId, modelReport);
            }
        }
    }

    private void addVariableGroupWithFrom(String type, VariableGroup variableGroup) {

        if (from == null) {
            from = new HashMap<>();
        }
        Map<String, BaseReport> map = from.get(type);

        if (variableGroup == null) {
            return;
        }
        String variableGroupId = variableGroup.getVariableGroupId();
        if (!StringUtils.isBlank(variableGroupId)) {
            if (map == null) {
                map = new HashMap<>();
                from.put(type, map);
            }
            if (!map.containsKey(variableGroupId)) {
                VariableGroupReport variableGroupReport = new VariableGroupReport();
                variableGroupReport.setVariableGroupId(variableGroupId);
                variableGroupReport.setVariableGroupName(variableGroup.getVariableGroupName());
                map.put(variableGroupId, variableGroupReport);
            }
        }
        return;
    }

    private void addApiGroupWithFrom(String type, ApiGroup apiGroup) {
        if (apiGroup != null) {
            if (from == null) {
                from = new HashMap<>();
            }
            Map<String, BaseReport> map = from.get(type);

            String apiGroupId = apiGroup.getApiGroupId();
            if (!StringUtils.isBlank(apiGroupId)) {
                if (map == null) {
                    map = new HashMap<>();
                    from.put(type, map);
                }
                if (!map.containsKey(apiGroupId)) {
                    ApiGroupReport modelGroupReport = new ApiGroupReport();
                    modelGroupReport.setApiGroupId(apiGroupId);
                    modelGroupReport.setApiGroupName(apiGroup.getApiGroupName());
                    map.put(apiGroupId, modelGroupReport);
                }
            }
        }
        return;
    }

    private void addKpiGroupWithFrom(String type, KpiGroup kpiGroup) {
        if (kpiGroup != null) {
            if (from == null) {
                from = new HashMap<>();
            }
            Map<String, BaseReport> map = from.get(type);

            String kpiGroupId = kpiGroup.getKpiGroupId();
            if (!StringUtils.isBlank(kpiGroupId)) {
                if (map == null) {
                    map = new HashMap<>();
                    from.put(type, map);
                }
                if (!map.containsKey(kpiGroupId)) {
                    KpiGroupReport kpiGroupReport = new KpiGroupReport();
                    kpiGroupReport.setKpiGroupId(kpiGroupId);
                    kpiGroupReport.setKpiGroupName(kpiGroup.getKpiGroupName());
                    kpiGroupReport.setKpiGroupDesc(kpiGroup.getKpiGroupDesc());
                    map.put(kpiGroupId, kpiGroupReport);
                }
            }
        }
        return;
    }

    private void addVariableWithFrom(String type, Variable variable) {
        if (variable != null) {
            if (from == null) {
                from = new HashMap<>();
            }
            Map<String, BaseReport> map = from.get(type);

            String variableId = variable.getVariableId();
            if (map == null) {
                map = new HashMap<>();
                from.put(type, map);
            }
            if (!StringUtils.isBlank(variableId)) {
                if (!map.containsKey(variableId)) {
                    VariableReport variableReport = new VariableReport();
                    variableReport.setVariableId(variableId);
                    variableReport.setVariableAlias(variable.getVariableAlias());
                    variableReport.setVariableCode(variable.getVariableCode());
                    map.put(variableId, variableReport);
                }
            }
        }
        return;
    }

    private void addApiWithFrom(String type, ApiConf apiConf) {
        if (apiConf != null) {
            if (from == null) {
                from = new HashMap<>();
            }
            Map<String, BaseReport> map = from.get(type);

            String apiId = apiConf.getApiId();
            if (map == null) {
                map = new HashMap<>();
                from.put(type, map);
            }
            if (!map.containsKey(apiId)) {
                ApiReport apiReport = new ApiReport();
                apiReport.setApiId(apiId);
                apiReport.setApiDesc(apiConf.getApiDesc());
                apiReport.setApiName(apiConf.getApiName());
                map.put(apiId, apiReport);
            }
        }
        return;
    }

    private void addKpiWithFrom(String type, KpiDefinition kpiDefinition) {
        if (kpiDefinition != null) {
            if (from == null) {
                from = new HashMap<>();
            }
            Map<String, BaseReport> map = from.get(type);

            String kpiId = kpiDefinition.getKpiId();
            if (map == null) {
                map = new HashMap<>();
                from.put(type, map);
            }
            if (!map.containsKey(kpiId)) {
                KpiReport kpiReport = new KpiReport();
                kpiReport.setKpiId(kpiId);
                kpiReport.setKpiDesc(kpiDefinition.getKpiDesc());
                kpiReport.setKpiName(kpiDefinition.getKpiName());
                map.put(kpiId, kpiReport);
            }
        }
        return;
    }

    private void addDBWithFrom(String type, DataSource dataSource) {
        if (dataSource != null) {
            if (from == null) {
                from = new HashMap<>();
            }
            Map<String, BaseReport> map = from.get(type);

            String dbId = dataSource.getDbId();
            if (map == null) {
                map = new HashMap<>();
                from.put(type, map);
            }
            if (!map.containsKey(dbId)) {
                DBReport dbReport = new DBReport();
                dbReport.setDbId(dbId);
                dbReport.setDbAlias(dataSource.getDbAlias());
                map.put(dbId, dbReport);
            }
        }
        return;
    }

    private void addDBTableWithFrom(String type, MetaDataTable dataTable) {
        if (dataTable != null) {
            if (from == null) {
                from = new HashMap<>();
            }
            Map<String, BaseReport> map = from.get(type);

            String tableId = dataTable.getTableId();
            if (map == null) {
                map = new HashMap<>();
                from.put(type, map);
            }
            if (!map.containsKey(tableId)) {
                DBTableReport dbTableReport = new DBTableReport();
                dbTableReport.setTableId(tableId);
                dbTableReport.setTableName(dataTable.getTableName());
                dbTableReport.setTableCode(dataTable.getTableCode());
                map.put(tableId, dbTableReport);
            }
        }
        return;
    }


    private void addDBColumnWithFrom(String type, MetaDataColumn metaDataColumn) {
        if (metaDataColumn != null) {
            if (from == null) {
                from = new HashMap<>();
            }
            Map<String, BaseReport> map = from.get(type);

            String columnId = metaDataColumn.getColumnId();
            if (map == null) {
                map = new HashMap<>();
                from.put(type, map);
            }
            if (!map.containsKey(columnId)) {
                DBColumnReport dbColumnReport = new DBColumnReport();
                dbColumnReport.setColumnId(columnId);
                dbColumnReport.setColumnName(metaDataColumn.getColumnName());
                dbColumnReport.setColumnCode(metaDataColumn.getColumnCode());
                map.put(columnId, dbColumnReport);
            }
        }
        return;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    //    public void addObjectFrom(String fromObjType, Object fromObj) {
//        if (StringUtils.isBlank(fromObjType) || fromObj == null) {
//            return;
//        }
//        if (from == null) {
//            from = new HashMap<>();
//        }
//        Map<String, BaseReport> map = from.get(fromObjType);
//        if (map == null) {
//            map = new HashMap<>();
//        }
//
//
////        map.put(fromObjId, report);
//        map = putToMap(fromObjType, map, fromObj);
//        from.put(fromObjType, map);
//
//    }
//
//    private Map putToMap(String type, Map map, Object o) {
//        switch (type) {
//            case ExportConstant.FOLDER:
//                if (o instanceof RuleFolder) {
//                    map = addFolder(map, (RuleFolder) o);
//                }
//                break;
//            case ExportConstant.MODEL_GROUP:
//                if (o instanceof ModelGroup) {
//                    map = addModelGroup(map, (ModelGroup) o);
//                }
//                break;
//            case ExportConstant.MODEL_HEADER:
//                if (o instanceof RuleDetailWithBLOBs) {
//                    map = addModelVersion(map, (RuleDetailWithBLOBs) o);
//                }
//                break;
//            case ExportConstant.MODEL_VERSION:
//                if (o instanceof RuleDetailWithBLOBs) {
//                    map = addModelVersion(map, (RuleDetailWithBLOBs) o);
//                }
//                break;
//            case ExportConstant.VARIABLE_GROUP:
//                if (o instanceof VariableGroup) {
//                    map = addVariableGroup(map, (VariableGroup) o);
//                }
//                break;
//            case ExportConstant.VARIABLE:
//                if (o instanceof Variable) {
//                    map = addVariable(map, (Variable) o);
//                }
//                break;
//            case ExportConstant.KPI_GROUP:
//                if (o instanceof KpiGroup) {
//                    map = addKpiGroup(map, (KpiGroup) o);
//                }
//                break;
//            case ExportConstant.KPI:
//                if (o instanceof KpiDefinition) {
//                    map = addKpi(map, (KpiDefinition) o);
//                }
//                break;
//            case ExportConstant.API_GROUP:
//                if (o instanceof ApiGroup) {
//                    map = addApiGroup(map, (ApiGroup) o);
//                }
//                break;
//            case ExportConstant.API:
//                if (o instanceof ApiConf) {
//                    map = addApi(map, (ApiConf) o);
//                }
//                break;
//            case ExportConstant.DB:
//                if (o instanceof DataSource) {
//                    map = addDB(map, (DataSource) o);
//                }
//                break;
//            case ExportConstant.PUB_DB_TABLE:
//                if (o instanceof MetaDataTable) {
//                    map = addDBPubTable(map, (MetaDataTable) o);
//                }
//                break;
//            case ExportConstant.PRI_DB_TABLE:
//                if (o instanceof MetaDataTable) {
//                    map = addDBPriTable(map, (MetaDataTable) o);
//                }
//                break;
//            case ExportConstant.DB_COLUNM:
//                if (o instanceof MetaDataColumn) {
//                    map = addDBColumn(map, (MetaDataColumn) o);
//                }
//                break;
//            default:
//        }
//        return map;
//    }
//
//    private Map<String, FolderReport> addFolder(Map<String, FolderReport> map, RuleFolder folder) {
//        if (folder != null) {
//            String folderId = folder.getFolderId();
//            if (!StringUtils.isBlank(folderId)) {
//                if (!map.containsKey(folderId)) {
//                    FolderReport folderReport = new FolderReport();
//                    folderReport.setFOLDER_ID(folderId);
//                    folderReport.setFOLDER_NAME(folder.getFolderName());
//                    folderReport.setFOLDER_DESC(folder.getFolderDesc());
//                    if (map == null) {
//                        map = new HashMap();
//                    }
//                    map.put(folderId, folderReport);
//                }
//            }
//        }
//        return map;
//    }
//
//    public Map addModelGroup(Map map, ModelGroup modelGroup) {
//        if (modelGroup != null) {
//            String modelGroupId = modelGroup.getModelGroupId();
//            if (!StringUtils.isBlank(modelGroupId)) {
//                ModelGroupReport modelGroupReport = new ModelGroupReport();
//                modelGroupReport.setMODEL_GROUP_ID(modelGroupId);
//                modelGroupReport.setMODEL_GROUP_NAME(modelGroup.getModelGroupName());
//                if (map == null) {
//                    map = new HashMap();
//                }
//                map.put(modelGroupId, modelGroupReport);
//            }
//        }
//        return map;
//    }
//
//    public Map addModelHeader(Map map, RuleDetailWithBLOBs ruleDetailWithBLOBs) {
//        if (ruleDetailWithBLOBs != null) {
//            String modelId = ruleDetailWithBLOBs.getRuleName();
//            if (map == null) {
//                map = new HashMap();
//            }
//            if (map.containsKey(modelId)) {
//                ModelReport modelReport = (ModelReport) map.get(modelId);
//                if (modelReport != null) {
//                    modelReport.addVersion(ruleDetailWithBLOBs);
//                }
//            } else {
//                ModelReport modelReport = new ModelReport();
//                modelReport.setRuleName(ruleDetailWithBLOBs.getRuleName());
//                modelReport.setMODEL_NAME(modelId);
//                modelReport.addVersion(ruleDetailWithBLOBs);
//                map.put(modelId, modelReport);
//            }
//        }
//        return map;
//    }
//
//    public Map addModelVersion(Map map, RuleDetailWithBLOBs ruleDetailWithBLOBs) {
//        if (ruleDetailWithBLOBs != null) {
//            String modelId = ruleDetailWithBLOBs.getRuleName();
//            if (map == null) {
//                map = new HashMap();
//            }
//            if (map.containsKey(modelId)) {
//                ModelReport modelReport = (ModelReport) map.get(modelId);
//                if (modelReport != null) {
//                    modelReport.addVersion(ruleDetailWithBLOBs);
//                }
//            } else {
//                ModelReport modelReport = new ModelReport();
//                modelReport.setRuleName(ruleDetailWithBLOBs.getRuleName());
//                modelReport.setMODEL_NAME(modelId);
//                modelReport.addVersion(ruleDetailWithBLOBs);
//                map.put(modelId, modelReport);
//            }
//        }
//        return map;
//    }
//
//    public Map addVariableGroup(Map map, VariableGroup variableGroup) {
//        if (variableGroup == null) {
//            return map;
//        }
//        String variableGroupId = variableGroup.getVariableGroupId();
//        if (!StringUtils.isBlank(variableGroupId)) {
//            if (map == null) {
//                map = new HashMap();
//            }
//            if (!map.containsKey(variableGroupId)) {
//                VariableGroupReport variableGroupReport = new VariableGroupReport();
//                variableGroupReport.setVARIABLE_GROUP_ID(variableGroupId);
//                variableGroupReport.setVARIABLE_GROUP_NAME(variableGroup.getVariableGroupName());
//                map.put(variableGroupId, variableGroupReport);
//            }
//        }
//        return map;
//    }
//
//    public Map addApiGroup(Map map, ApiGroup apiGroup) {
//        if (apiGroup != null) {
//            String apiGroupId = apiGroup.getApiGroupId();
//            if (!StringUtils.isBlank(apiGroupId)) {
//                if (map == null) {
//                    map = new HashMap();
//                }
//                if (!map.containsKey(apiGroupId)) {
//                    ApiGroupReport modelGroupReport = new ApiGroupReport();
//                    modelGroupReport.setAPI_GROUP_ID(apiGroupId);
//                    modelGroupReport.setAPI_GROUP_NAME(apiGroup.getApiGroupName());
//                    map.put(apiGroupId, modelGroupReport);
//                }
//            }
//        }
//        return map;
//    }
//
//    public Map addKpiGroup(Map map, KpiGroup kpiGroup) {
//        if (kpiGroup != null) {
//            String kpiGroupId = kpiGroup.getKpiGroupId();
//            if (!StringUtils.isBlank(kpiGroupId)) {
//                if (map == null) {
//                    map = new HashMap();
//                }
//                if (!map.containsKey(kpiGroupId)) {
//                    KpiGroupReport kpiGroupReport = new KpiGroupReport();
//                    kpiGroupReport.setKPI_GROUP_ID(kpiGroupId);
//                    kpiGroupReport.setKPI_GROUP_NAME(kpiGroup.getKpiGroupName());
//                    kpiGroupReport.setKPI_GROUP_DESC(kpiGroup.getKpiGroupDesc());
//                    map.put(kpiGroupId, kpiGroupReport);
//                }
//            }
//        }
//        return map;
//    }
//
//    public Map addVariable(Map map, Variable variable) {
//        if (variable != null) {
//            String variableId = variable.getVariableId();
//            if (map == null) {
//                map = new HashMap<>();
//            }
//            if (!map.containsKey(variableId)) {
//                VariableReport variableReport = new VariableReport();
//                variableReport.setVARIABLE_ID(variableId);
//                variableReport.setVARIABLE_ALIAS(variable.getVariableAlias());
//                variableReport.setVARIABLE_CODE(variable.getVariableCode());
//                map.put(variableId, variableReport);
//            }
//        }
//        return map;
//    }
//
//    public Map addApi(Map map, ApiConf apiConf) {
//        if (apiConf != null) {
//            String apiId = apiConf.getApiId();
//            if (map == null) {
//                map = new HashMap<>();
//            }
//            if (!map.containsKey(apiId)) {
//                ApiReport apiReport = new ApiReport();
//                apiReport.setAPI_ID(apiId);
//                apiReport.setAPI_DESC(apiConf.getApiDesc());
//                apiReport.setAPI_NAME(apiConf.getApiName());
//                map.put(apiId, apiReport);
//            }
//        }
//        return map;
//    }
//
//    public Map addKpi(Map map, KpiDefinition kpiDefinition) {
//        if (kpiDefinition != null) {
//            String kpiId = kpiDefinition.getKpiId();
//            if (map == null) {
//                map = new HashMap<>();
//            }
//            if (!map.containsKey(kpiId)) {
//                KpiReport kpiReport = new KpiReport();
//                kpiReport.setKPI_ID(kpiId);
//                kpiReport.setKPI_DESC(kpiDefinition.getKpiDesc());
//                kpiReport.setKPI_NAME(kpiDefinition.getKpiName());
//                map.put(kpiId, kpiReport);
//            }
//        }
//        return map;
//    }
//
//    public Map addDB(Map map, DataSource dataSource) {
//        if (dataSource != null) {
//            String dbId = dataSource.getDbId();
//            if (map == null) {
//                map = new HashMap<>();
//            }
//            if (!map.containsKey(dbId)) {
//                DBReport dbReport = new DBReport();
//                dbReport.setDB_ID(dbId);
//                dbReport.setDB_ALIAS(dataSource.getDbAlias());
//                map.put(dbId, dbReport);
//            }
//        }
//        return map;
//    }
//
//    public Map addDBPriTable(Map map, MetaDataTable dataTable) {
//        if (dataTable != null) {
//            String tableId = dataTable.getTableId();
//            if (map == null) {
//                map = new HashMap<>();
//            }
//            if (!map.containsKey(tableId)) {
//                DBTableReport dbTableReport = new DBTableReport();
//                dbTableReport.setTableId(tableId);
//                dbTableReport.setTableName(dataTable.getTableName());
//                dbTableReport.setTableCode(dataTable.getTableCode());
//                map.put(tableId, dbTableReport);
//            }
//        }
//        return map;
//    }
//
//    public Map addDBPubTable(Map map, MetaDataTable dataTable) {
//        if (dataTable != null) {
//            String tableId = dataTable.getTableId();
//            if (map == null) {
//                map = new HashMap<>();
//            }
//            if (!map.containsKey(tableId)) {
//                DBTableReport dbTableReport = new DBTableReport();
//                dbTableReport.setTableId(tableId);
//                dbTableReport.setTableName(dataTable.getTableName());
//                dbTableReport.setTableCode(dataTable.getTableCode());
//                map.put(tableId, dbTableReport);
//            }
//        }
//        return map;
//    }
//
//    public Map addDBColumn(Map map, MetaDataColumn metaDataColumn) {
//        if (metaDataColumn != null) {
//            String columnId = metaDataColumn.getColumnId();
//            if (map == null) {
//                map = new HashMap<>();
//            }
//            if (!map.containsKey(columnId)) {
//                DBColumnReport dbColumnReport = new DBColumnReport();
//                dbColumnReport.setColumnId(columnId);
//                dbColumnReport.setColumnName(metaDataColumn.getColumnName());
//                dbColumnReport.setColumnCode(metaDataColumn.getColumnCode());
//                map.put(columnId, dbColumnReport);
//            }
//        }
//        return map;
//    }
//
//


    public Map<String, Map<String, BaseReport>> getFrom() {
        return from;
    }

    public void setFrom(Map<String, Map<String, BaseReport>> from) {
        this.from = from;
    }
}
