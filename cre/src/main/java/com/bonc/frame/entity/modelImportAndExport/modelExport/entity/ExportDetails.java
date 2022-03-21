package com.bonc.frame.entity.modelImportAndExport.modelExport.entity;

import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.commonresource.ApiGroup;
import com.bonc.frame.entity.commonresource.ModelGroup;
import com.bonc.frame.entity.commonresource.VariableGroup;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.kpi.KpiGroup;
import com.bonc.frame.entity.metadata.MetaDataColumn;
import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.rulefolder.RuleFolder;
import com.bonc.frame.entity.variable.Variable;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report.BaseReport;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report.*;
import com.bonc.frame.util.ExportUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/11 1:03
 */
@Deprecated
public class ExportDetails {
    Log log = LogFactory.getLog(ExportDetails.class);

    private Map<String, Map<String, BaseReport>> dataIds;// {type:{id:{id:id,name:name,from:{type:{id:object}}}}


    public void addDataIdWithFrom(String type, Object o, String fromObjType, Object fromObj) throws Exception {
        if (StringUtils.isBlank(type)) {
            log.warn("数据类型为null,Object:" + o);
            return;
        }
        if (o instanceof String) {
            o = paseObjectIdToObject(type, (String) o);
        }
        switch (type) {
            case ExportConstant.FOLDER:
                if (o instanceof RuleFolder) {
                    addFolderWithFrom(type, (RuleFolder) o, fromObjType, fromObj);
                }
                break;
            case ExportConstant.MODEL_GROUP:
                if (o instanceof ModelGroup) {
                    addModelGroupWithFrom(type, (ModelGroup) o, fromObjType, fromObj);
                }
                break;
            case ExportConstant.MODEL_VERSION:
                if (o instanceof RuleDetailWithBLOBs) {
                    addModelVersionWithFrom(type, (RuleDetailWithBLOBs) o, fromObjType, fromObj);
                }
                break;
            case ExportConstant.MODEL_HEADER:
                if (o instanceof RuleDetailHeader) {
                    addModelHeaderWithFrom(type, (RuleDetailHeader) o, fromObjType, fromObj);
                }
                break;
            case ExportConstant.VARIABLE_GROUP:
                if (o instanceof VariableGroup) {
                    addVariableGroupWithFrom(type, (VariableGroup) o, fromObjType, fromObj);
                }
                break;
            case ExportConstant.VARIABLE:
                if (o instanceof Variable) {
                    addVariableWithFrom(type, (Variable) o, fromObjType, fromObj);
                }
                break;
            case ExportConstant.KPI_GROUP:
                if (o instanceof KpiGroup) {
                    addKpiGroupWithFrom(type, (KpiGroup) o, fromObjType, fromObj);
                }
                break;
            case ExportConstant.KPI:
                if (o instanceof KpiDefinition) {
                    addKpiWithFrom(type, (KpiDefinition) o, fromObjType, fromObj);
                }
                break;
            case ExportConstant.API_GROUP:
                if (o instanceof ApiGroup) {
                    addApiGroupWithFrom(type, (ApiGroup) o, fromObjType, fromObj);
                }
                break;
            case ExportConstant.API:
                if (o instanceof ApiConf) {
                    addApiWithFrom(type, (ApiConf) o, fromObjType, fromObj);
                }
                break;
            case ExportConstant.DB:
                if (o instanceof DataSource) {
                    addDBWithFrom(type, (DataSource) o, fromObjType, fromObj);
                }
                break;
            case ExportConstant.PUB_DB_TABLE:
            case ExportConstant.PRI_DB_TABLE:
                if (o instanceof MetaDataTable) {
                    addDBTableWithFrom(type, (MetaDataTable) o, fromObjType, fromObj);
                }
                break;
            case ExportConstant.DB_COLUNM:
                if (o instanceof MetaDataColumn) {
                    addDBColumnWithFrom(type, (MetaDataColumn) o, fromObjType, fromObj);
                }
                break;
            default:
        }

    }


    private void addFolderWithFrom(String type, RuleFolder folder, String fromObjType, Object fromObj) {
        if (folder != null) {
            if (dataIds == null) {
                dataIds = new HashMap<>();
            }
            Map<String, BaseReport> map = dataIds.get(type);
            if (map == null) {
                map = new HashMap<>();
                dataIds.put(type, map);
            }
            String folderId = folder.getFolderId();
            if (!StringUtils.isBlank(folderId)) {
                if (!map.containsKey(folderId)) {
                    FolderReport folderReport = new FolderReport();
                    folderReport.setFolderId(folderId);
                    folderReport.setFolderName(folder.getFolderName());
                    folderReport.setFolderDesc(folder.getFolderDesc());
                    folderReport.addObjectFrom(fromObjType, fromObj);

                    map.put(folderId, folderReport);
                } else {
                    BaseReport baseReport = map.get(folderId);
                    baseReport.addObjectFrom(fromObjType, fromObj);
                }
            }
        }
    }

    private void addModelGroupWithFrom(String type, ModelGroup modelGroup, String fromObjType, Object fromObj) {
        if (modelGroup != null) {
            if (dataIds == null) {
                dataIds = new HashMap<>();
            }
            Map<String, BaseReport> map = dataIds.get(type);
            String modelGroupId = modelGroup.getModelGroupId();
            if (!StringUtils.isBlank(modelGroupId)) {
                if (map == null) {
                    map = new HashMap<>();
                    dataIds.put(type, map);
                }
                if (!map.containsKey(modelGroupId)) {
                    ModelGroupReport modelGroupReport = new ModelGroupReport();
                    modelGroupReport.setModelGroupId(modelGroupId);
                    modelGroupReport.setModelGroupName(modelGroup.getModelGroupName());
                    modelGroupReport.addObjectFrom(fromObjType, fromObj);

                    map.put(modelGroupId, modelGroupReport);
                } else {
                    BaseReport baseReport = map.get(modelGroupId);
                    baseReport.addObjectFrom(fromObjType, fromObj);
                }
            }
        }
    }

    private void addModelVersionWithFrom(String type, RuleDetailWithBLOBs ruleDetailWithBLOBs, String fromObjType, Object fromObj) {
        if (ruleDetailWithBLOBs != null) {
            if (dataIds == null) {
                dataIds = new HashMap<>();
            }
            Map<String, BaseReport> map = dataIds.get(type);

            String modelId = ruleDetailWithBLOBs.getRuleName();
            if (map == null) {
                map = new HashMap<>();
                dataIds.put(type, map);
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
                modelReport.setModelName(ruleDetailWithBLOBs.getModuleName());
                modelReport.setRuleName(modelId);
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

    private void addModelHeaderWithFrom(String type, RuleDetailHeader ruleDetailHeader, String fromObjType, Object fromObj) {
        if (ruleDetailHeader != null) {
            if (dataIds == null) {
                dataIds = new HashMap<>();
            }
            Map<String, BaseReport> map = dataIds.get(type);

            String modelId = ruleDetailHeader.getRuleName();
            if (map == null) {
                map = new HashMap<>();
                dataIds.put(type, map);
            }
            if (map.containsKey(modelId)) {
                ModelReport modelReport = (ModelReport) map.get(modelId);
                if (modelReport != null) {
                    modelReport.addObjectFrom(fromObjType, fromObj);
                }
            } else {
                ModelReport modelReport = ExportUtil.paseModelHeaderReport(ruleDetailHeader);
                modelReport.setModelName(ruleDetailHeader.getModuleName());
                modelReport.setRuleName(modelId);
                modelReport.addObjectFrom(fromObjType, fromObj);
                map.put(modelId, modelReport);
            }
        }
    }

    private void addVariableGroupWithFrom(String type, VariableGroup variableGroup, String fromObjType, Object fromObj) {

        if (dataIds == null) {
            dataIds = new HashMap<>();
        }
        Map<String, BaseReport> map = dataIds.get(type);

        if (variableGroup == null) {
            return;
        }
        String variableGroupId = variableGroup.getVariableGroupId();
        if (!StringUtils.isBlank(variableGroupId)) {
            if (map == null) {
                map = new HashMap<>();
                dataIds.put(type, map);
            }
            if (!map.containsKey(variableGroupId)) {
                VariableGroupReport variableGroupReport = new VariableGroupReport();
                variableGroupReport.setVariableGroupId(variableGroupId);
                variableGroupReport.setVariableGroupName(variableGroup.getVariableGroupName());
                map.put(variableGroupId, variableGroupReport);
            } else {
                BaseReport baseReport = map.get(variableGroupId);
                baseReport.addObjectFrom(fromObjType, fromObj);
            }
        }
        return;
    }

    private void addApiGroupWithFrom(String type, ApiGroup apiGroup, String fromObjType, Object fromObj) {
        if (apiGroup != null) {
            if (dataIds == null) {
                dataIds = new HashMap<>();
            }
            Map<String, BaseReport> map = dataIds.get(type);

            String apiGroupId = apiGroup.getApiGroupId();
            if (!StringUtils.isBlank(apiGroupId)) {
                if (map == null) {
                    map = new HashMap<>();
                    dataIds.put(type, map);
                }
                if (!map.containsKey(apiGroupId)) {
                    ApiGroupReport modelGroupReport = new ApiGroupReport();
                    modelGroupReport.setApiGroupId(apiGroupId);
                    modelGroupReport.setApiGroupName(apiGroup.getApiGroupName());
                    map.put(apiGroupId, modelGroupReport);
                } else {
                    BaseReport baseReport = map.get(apiGroupId);
                    baseReport.addObjectFrom(fromObjType, fromObj);
                }
            }
        }
        return;
    }

    private void addKpiGroupWithFrom(String type, KpiGroup kpiGroup, String fromObjType, Object fromObj) {
        if (kpiGroup != null) {
            if (dataIds == null) {
                dataIds = new HashMap<>();
            }
            Map<String, BaseReport> map = dataIds.get(type);

            String kpiGroupId = kpiGroup.getKpiGroupId();
            if (!StringUtils.isBlank(kpiGroupId)) {
                if (map == null) {
                    map = new HashMap<>();
                    dataIds.put(type, map);
                }
                if (!map.containsKey(kpiGroupId)) {
                    KpiGroupReport kpiGroupReport = new KpiGroupReport();
                    kpiGroupReport.setKpiGroupId(kpiGroupId);
                    kpiGroupReport.setKpiGroupName(kpiGroup.getKpiGroupName());
                    kpiGroupReport.setKpiGroupDesc(kpiGroup.getKpiGroupDesc());
                    map.put(kpiGroupId, kpiGroupReport);
                } else {
                    BaseReport baseReport = map.get(kpiGroupId);
                    baseReport.addObjectFrom(fromObjType, fromObj);
                }
            }
        }
        return;
    }

    private void addVariableWithFrom(String type, Variable variable, String fromObjType, Object fromObj) {
        if (variable != null) {
            if (dataIds == null) {
                dataIds = new HashMap<>();
            }
            Map<String, BaseReport> map = dataIds.get(type);

            String variableId = variable.getVariableId();
            if (map == null) {
                map = new HashMap<>();
                dataIds.put(type, map);
            }
            if (!StringUtils.isBlank(variableId)) {
                if (!map.containsKey(variableId)) {
                    VariableReport variableReport = new VariableReport();
                    variableReport.setVariableId(variableId);
                    variableReport.setVariableAlias(variable.getVariableAlias());
                    variableReport.setVariableCode(variable.getVariableCode());
                    map.put(variableId, variableReport);
                } else {
                    BaseReport baseReport = map.get(variableId);
                    baseReport.addObjectFrom(fromObjType, fromObj);
                }
            }
        }
        return;
    }

    private void addApiWithFrom(String type, ApiConf apiConf, String fromObjType, Object fromObj) {
        if (apiConf != null) {
            if (dataIds == null) {
                dataIds = new HashMap<>();
            }
            Map<String, BaseReport> map = dataIds.get(type);

            String apiId = apiConf.getApiId();
            if (map == null) {
                map = new HashMap<>();
                dataIds.put(type, map);
            }
            if (!map.containsKey(apiId)) {
                ApiReport apiReport = new ApiReport();
                apiReport.setApiId(apiId);
                apiReport.setApiDesc(apiConf.getApiDesc());
                apiReport.setApiName(apiConf.getApiName());
                map.put(apiId, apiReport);
            } else {
                BaseReport baseReport = map.get(apiId);
                baseReport.addObjectFrom(fromObjType, fromObj);
            }
        }
        return;
    }

    private void addKpiWithFrom(String type, KpiDefinition kpiDefinition, String fromObjType, Object fromObj) {
        if (kpiDefinition != null) {
            if (dataIds == null) {
                dataIds = new HashMap<>();
            }
            Map<String, BaseReport> map = dataIds.get(type);

            String kpiId = kpiDefinition.getKpiId();
            if (map == null) {
                map = new HashMap<>();
                dataIds.put(type, map);
            }
            if (!map.containsKey(kpiId)) {
                KpiReport kpiReport = new KpiReport();
                kpiReport.setKpiId(kpiId);
                kpiReport.setKpiDesc(kpiDefinition.getKpiDesc());
                kpiReport.setKpiName(kpiDefinition.getKpiName());
                map.put(kpiId, kpiReport);
            } else {
                BaseReport baseReport = map.get(kpiId);
                baseReport.addObjectFrom(fromObjType, fromObj);
            }
        }
        return;
    }

    private void addDBWithFrom(String type, DataSource dataSource, String fromObjType, Object fromObj) {
        if (dataSource != null) {
            if (dataIds == null) {
                dataIds = new HashMap<>();
            }
            Map<String, BaseReport> map = dataIds.get(type);

            String dbId = dataSource.getDbId();
            if (map == null) {
                map = new HashMap<>();
                dataIds.put(type, map);
            }
            if (!map.containsKey(dbId)) {
                DBReport dbReport = new DBReport();
                dbReport.setDbId(dbId);
                dbReport.setDbAlias(dataSource.getDbAlias());
                map.put(dbId, dbReport);
            } else {
                BaseReport baseReport = map.get(dbId);
                baseReport.addObjectFrom(fromObjType, fromObj);
            }
        }
        return;
    }

    private void addDBTableWithFrom(String type, MetaDataTable dataTable, String fromObjType, Object fromObj) {
        if (dataTable != null) {
            if (dataIds == null) {
                dataIds = new HashMap<>();
            }
            Map<String, BaseReport> map = dataIds.get(type);

            String tableId = dataTable.getTableId();
            if (map == null) {
                map = new HashMap<>();
                dataIds.put(type, map);
            }
            if (!map.containsKey(tableId)) {
                DBTableReport dbTableReport = new DBTableReport();
                dbTableReport.setTableId(tableId);
                dbTableReport.setTableName(dataTable.getTableName());
                dbTableReport.setTableCode(dataTable.getTableCode());
                map.put(tableId, dbTableReport);
            } else {
                BaseReport baseReport = map.get(tableId);
                baseReport.addObjectFrom(fromObjType, fromObj);
            }
        }
        return;
    }


    private void addDBColumnWithFrom(String type, MetaDataColumn metaDataColumn, String fromObjType, Object fromObj) {
        if (metaDataColumn != null) {
            if (dataIds == null) {
                dataIds = new HashMap<>();
            }
            Map<String, BaseReport> map = dataIds.get(type);

            String columnId = metaDataColumn.getColumnId();
            if (map == null) {
                map = new HashMap<>();
                dataIds.put(type, map);
            }
            if (!map.containsKey(columnId)) {
                DBColumnReport dbColumnReport = new DBColumnReport();
                dbColumnReport.setColumnId(columnId);
                dbColumnReport.setColumnName(metaDataColumn.getColumnName());
                dbColumnReport.setColumnCode(metaDataColumn.getColumnCode());
                map.put(columnId, dbColumnReport);
            } else {
                BaseReport baseReport = map.get(columnId);
                baseReport.addObjectFrom(fromObjType, fromObj);
            }
        }
        return;
    }

    /**
     * 将id通过类型返回一个对象
     *
     * @param type
     * @param id
     * @return
     */
    private Object paseObjectIdToObject(String type, String id) {
        if (StringUtils.isBlank(type)) {
            log.warn("数据类型为null,id:" + id);
            return null;
        }

        switch (type) {
            case ExportConstant.VARIABLE_GROUP:
                VariableGroup variableGroup = new VariableGroup();
                variableGroup.setVariableGroupId(id);
                return variableGroup;
            case ExportConstant.VARIABLE:
                Variable variable = new Variable();
                variable.setVariableId(id);
                return variable;
            case ExportConstant.KPI_GROUP:
                KpiGroup kpiGroup = new KpiGroup();
                kpiGroup.setKpiGroupId(id);
                return kpiGroup;
            case ExportConstant.KPI:
                KpiDefinition kpiDefinition = new KpiDefinition();
                kpiDefinition.setKpiId(id);
                return kpiDefinition;
            case ExportConstant.API_GROUP:
                ApiGroup apiGroup = new ApiGroup();
                apiGroup.setApiGroupId(id);
                return apiGroup;
            case ExportConstant.API:
                ApiConf apiConf = new ApiConf();
                apiConf.setApiId(id);
                return apiConf;
            case ExportConstant.DB:
                DataSource dataSource = new DataSource();
                dataSource.setDbId(id);
                return dataSource;
            case ExportConstant.PUB_DB_TABLE:
            case ExportConstant.PRI_DB_TABLE:
                MetaDataTable metaDataTable = new MetaDataTable();
                metaDataTable.setTableId(id);
                return metaDataTable;
            case ExportConstant.DB_COLUNM:
                MetaDataColumn metaDataColumn = new MetaDataColumn();
                metaDataColumn.setColumnId(id);
                return metaDataColumn;
            default:
                log.error("数据的类型不匹配,");
        }
        return null;
    }

    public Map<String, Map<String, BaseReport>> getDataIds() {
        return dataIds;
    }

    public void setDataIds(Map<String, Map<String, BaseReport>> dataIds) {
        this.dataIds = dataIds;
    }

}
