package com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report;


import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.metadata.MetaDataColumn;
import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.variable.Variable;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/1/9 18:47
 */
public class FolderReport extends BaseReport {
    private String folderId;
    private String folderName;
    private String folderDesc;
    private Map<String, ModelReport> modelHeader;

    private Map<String, VariableReport> variable;

    private Map<String, ApiReport> api;

    private Map<String, KpiReport> kpi;

    private Map<String, DBReport> db;
    private Map<String, DBTableReport> pubDbTable;
    private Map<String, DBTableReport> priDbTable;
    private Map<String, DBColumnReport> dbColunm;

    public void addModelHeader(RuleDetailHeader ruleDetailHeader) {
        if (ruleDetailHeader != null) {
            String variableId = ruleDetailHeader.getRuleName();
            if (modelHeader == null) {
                modelHeader = new HashMap<>();
            }
            if (!modelHeader.containsKey(variableId)) {
                ModelReport modelReport = new ModelReport();
                modelReport.setRuleName(ruleDetailHeader.getRuleName());
                modelReport.setModelName(ruleDetailHeader.getModuleName());
                modelHeader.put(variableId, modelReport);
            }
        }
    }

    public void addModelVersion(RuleDetailWithBLOBs ruleDetailWithBLOBs) {
        if (ruleDetailWithBLOBs != null) {
            String modelId = ruleDetailWithBLOBs.getRuleName();
            if (modelHeader == null) {
                modelHeader = new HashMap<>();
            }
            if (modelHeader.containsKey(modelId)) {
                ModelReport modelReport = modelHeader.get(modelId);
                if (modelReport != null) {
                    modelReport.addVersion(ruleDetailWithBLOBs);
                }
            } else {
                ModelReport modelReport = new ModelReport();
                modelReport.setModelName(ruleDetailWithBLOBs.getModuleName());
                modelReport.setRuleName(modelId);
                modelReport.addVersion(ruleDetailWithBLOBs);
                modelHeader.put(modelId, modelReport);
            }
        }
    }

    public void addVariable(Variable variable) {
        if (variable != null) {
            String variableId = variable.getVariableId();
            if (this.variable == null) {
                this.variable = new HashMap<>();
            }
            if (!this.variable.containsKey(variableId)) {
                VariableReport variableReport = new VariableReport();
                variableReport.setVariableId(variableId);
                variableReport.setVariableAlias(variable.getVariableAlias());
                variableReport.setVariableCode(variable.getVariableCode());
                this.variable.put(variableId, variableReport);
            }
        }
    }

    public void addApi(ApiConf apiConf) {
        if (apiConf != null) {
            String apiId = apiConf.getApiId();
            if (api == null) {
                api = new HashMap<>();
            }
            if (!api.containsKey(apiId)) {
                ApiReport apiReport = new ApiReport();
                apiReport.setApiId(apiId);
                apiReport.setApiDesc(apiConf.getApiDesc());
                apiReport.setApiName(apiConf.getApiName());
                api.put(apiId, apiReport);
            }
        }
    }

    public void addKpi(KpiDefinition kpiDefinition) {
        if (kpiDefinition != null) {
            String kpiId = kpiDefinition.getKpiId();
            if (kpi == null) {
                kpi = new HashMap<>();
            }
            if (!kpi.containsKey(kpiId)) {
                KpiReport kpiReport = new KpiReport();
                kpiReport.setKpiId(kpiId);
                kpiReport.setKpiDesc(kpiDefinition.getKpiDesc());
                kpiReport.setKpiName(kpiDefinition.getKpiName());
                kpi.put(kpiId, kpiReport);
            }
        }
    }

    public void addDB(DataSource dataSource) {
        if (dataSource != null) {
            String dbId = dataSource.getDbId();
            if (db == null) {
                db = new HashMap<>();
            }
            if (!db.containsKey(dbId)) {
                DBReport dbReport = new DBReport();
                dbReport.setDbId(dbId);
                dbReport.setDbAlias(dataSource.getDbAlias());
                db.put(dbId, dbReport);
            }
        }
    }

    public void addDBPriTable(MetaDataTable dataTable) {
        if (dataTable != null) {
            String tableId = dataTable.getTableId();
            if (pubDbTable == null) {
                pubDbTable = new HashMap<>();
            }
            if (!pubDbTable.containsKey(tableId)) {
                DBTableReport dbTableReport = new DBTableReport();
                dbTableReport.setTableId(tableId);
                dbTableReport.setTableName(dataTable.getTableName());
                dbTableReport.setTableCode(dataTable.getTableCode());
                pubDbTable.put(tableId, dbTableReport);
            }
        }
    }

    public void addDBPubTable(MetaDataTable dataTable) {
        if (dataTable != null) {
            String tableId = dataTable.getTableId();
            if (priDbTable == null) {
                priDbTable = new HashMap<>();
            }
            if (!priDbTable.containsKey(tableId)) {
                DBTableReport dbTableReport = new DBTableReport();
                dbTableReport.setTableId(tableId);
                dbTableReport.setTableName(dataTable.getTableName());
                dbTableReport.setTableCode(dataTable.getTableCode());
                priDbTable.put(tableId, dbTableReport);
            }
        }
    }

    public void addDBColumn(MetaDataColumn metaDataColumn) {
        if (metaDataColumn != null) {
            String columnId = metaDataColumn.getColumnId();
            if (dbColunm == null) {
                dbColunm = new HashMap<>();
            }
            if (!dbColunm.containsKey(columnId)) {
                DBColumnReport dbColumnReport = new DBColumnReport();
                dbColumnReport.setColumnId(columnId);
                dbColumnReport.setColumnName(metaDataColumn.getColumnName());
                dbColumnReport.setColumnCode(metaDataColumn.getColumnCode());
                dbColunm.put(columnId, dbColumnReport);
            }
        }
    }

    public boolean containsModel(RuleDetailWithBLOBs ruleDetailWithBLOBs) {
        if (ruleDetailWithBLOBs != null && modelHeader != null) {
            return modelHeader.containsKey(ruleDetailWithBLOBs.getRuleName());
        }
        return false;
    }

    public boolean containModelVersion(RuleDetailWithBLOBs ruleDetailWithBLOBs) {
        if (ruleDetailWithBLOBs != null && modelHeader != null) {
            ModelReport modelReport = modelHeader.get(ruleDetailWithBLOBs.getRuleName());
            if (modelReport != null) {
                return modelReport.containsVersion(ruleDetailWithBLOBs);
            }
        }
        return false;
    }

    public boolean containVariable(Variable variable) {
        if (variable != null && this.variable != null) {
            return this.variable.containsKey(variable.getVariableId());
        }
        return false;
    }

    @Override
    public String reportId() {
        return folderId;
    }

    @Override
    public String reportName() {
        return folderName;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderDesc() {
        return folderDesc;
    }

    public void setFolderDesc(String folderDesc) {
        this.folderDesc = folderDesc;
    }

    public Map<String, ModelReport> getModelHeader() {
        return modelHeader;
    }

    public void setModelHeader(Map<String, ModelReport> modelHeader) {
        this.modelHeader = modelHeader;
    }

    public Map<String, VariableReport> getVariable() {
        return variable;
    }

    public void setVariable(Map<String, VariableReport> variable) {
        this.variable = variable;
    }

    public Map<String, ApiReport> getApi() {
        return api;
    }

    public void setApi(Map<String, ApiReport> api) {
        this.api = api;
    }

    public Map<String, KpiReport> getKpi() {
        return kpi;
    }

    public void setKpi(Map<String, KpiReport> kpi) {
        this.kpi = kpi;
    }

    public Map<String, DBReport> getDb() {
        return db;
    }

    public void setDb(Map<String, DBReport> db) {
        this.db = db;
    }

    public Map<String, DBTableReport> getPubDbTable() {
        return pubDbTable;
    }

    public void setPubDbTable(Map<String, DBTableReport> pubDbTable) {
        this.pubDbTable = pubDbTable;
    }

    public Map<String, DBTableReport> getPriDbTable() {
        return priDbTable;
    }

    public void setPriDbTable(Map<String, DBTableReport> priDbTable) {
        this.priDbTable = priDbTable;
    }

    public Map<String, DBColumnReport> getDbColunm() {
        return dbColunm;
    }

    public void setDbColunm(Map<String, DBColumnReport> dbColunm) {
        this.dbColunm = dbColunm;
    }
}
