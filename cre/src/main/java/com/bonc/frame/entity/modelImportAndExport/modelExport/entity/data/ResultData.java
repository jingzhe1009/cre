package com.bonc.frame.entity.modelImportAndExport.modelExport.entity.data;

import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.commonresource.ApiGroup;
import com.bonc.frame.entity.commonresource.ModelGroup;
import com.bonc.frame.entity.commonresource.VariableGroup;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.kpi.KpiGroup;
import com.bonc.frame.entity.metadata.MetaDataColumn;
import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.rulefolder.RuleFolder;
import com.bonc.frame.entity.variable.VariableTreeNode;
import com.bonc.frame.util.ExportUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/2/10 18:43
 */
public class ResultData {
    Log log = LogFactory.getLog(ResultData.class);

    //    Map<String, Map<String, Object>> data; // {type:{id:object}}
    private Map<String, RuleFolder> folder;
    private Map<String, ModelGroup> modelGroup;
    private Map<String, RuleDetailHeader> modelHeader;
    private Map<String, RuleDetailWithBLOBs> modelVersion;
    private Map<String, VariableGroup> variableGroup;
    private Map<String, VariableTreeNode> variable;
    private Map<String, KpiGroup> kpiGroup;
    private Map<String, KpiDefinition> kpi;
    private Map<String, ApiGroup> apiGroup;
    private Map<String, ApiConf> api;
    private Map<String, DataSource> db;
    private Map<String, MetaDataTable> pubDbTable;
    private Map<String, MetaDataTable> priDbTable;
    private Map<String, MetaDataColumn> dbColunm;

    public boolean containsObject(String type, Object o) {
        if (StringUtils.isBlank(type) || o == null) {
            log.warn("数据类型为null或对象为null,type:" + type + ",o:" + o);
            return false;
        }
        String objectId = ExportUtil.getObjectId(type, o);
        return containsKey(type, objectId);
    }

    public Map get(String type) {
        if (StringUtils.isBlank(type)) {
            log.warn("数据类型为null");
            return null;
        }
        switch (type) {
            case ExportConstant.FOLDER:
                return folder;
            case ExportConstant.MODEL_GROUP:
                return modelGroup;
            case ExportConstant.MODEL_HEADER:
                return modelHeader;
            case ExportConstant.MODEL_VERSION:
                return modelVersion;
            case ExportConstant.VARIABLE_GROUP:
                return variableGroup;
            case ExportConstant.VARIABLE:
                return variable;
            case ExportConstant.KPI_GROUP:
                return kpiGroup;
            case ExportConstant.KPI:
                return kpi;
            case ExportConstant.API_GROUP:
                return apiGroup;
            case ExportConstant.API:
                return api;
            case ExportConstant.DB:
                return db;
            case ExportConstant.PUB_DB_TABLE:
                return pubDbTable;
            case ExportConstant.PRI_DB_TABLE:
                return priDbTable;
            case ExportConstant.DB_COLUNM:
                return dbColunm;
            default:
                return null;
        }
    }


    public void put(String type, Object o) throws Exception {
        if (StringUtils.isBlank(type)) {
            log.warn("数据类型为null,Object:" + o);
            return;
        }
        switch (type) {
            case ExportConstant.FOLDER:
                if (o instanceof RuleFolder) {
                    putFOLDER((RuleFolder) o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.MODEL_GROUP:
                if (o instanceof ModelGroup) {
                    putMODEL_GROUP((ModelGroup) o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.MODEL_HEADER:
                if (o instanceof RuleDetailHeader) {
                    RuleDetailHeader ruleDetailHeader = new RuleDetailHeader();
                    org.springframework.beans.BeanUtils.copyProperties(o, ruleDetailHeader);
                    putMODEL_HEADER(ruleDetailHeader);
//                    putMODEL_HEADER((RuleDetailHeader) o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.MODEL_VERSION:
                if (o instanceof RuleDetailWithBLOBs) {
                    putMODEL_VERSION((RuleDetailWithBLOBs) o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.VARIABLE_GROUP:
                if (o instanceof VariableGroup) {
                    putVARIABLE_GROUP((VariableGroup) o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.VARIABLE:
                if (o instanceof VariableTreeNode) {
                    putVARIABLE((VariableTreeNode) o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.KPI_GROUP:
                if (o instanceof KpiGroup) {
                    putKPI_GROUP((KpiGroup) o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.KPI:
                if (o instanceof KpiDefinition) {
                    putKPI((KpiDefinition) o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.API_GROUP:
                if (o instanceof ApiGroup) {
                    putAPI_GROUP((ApiGroup) o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.API:
                if (o instanceof ApiConf) {
                    putAPI((ApiConf) o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.DB:
                if (o instanceof DataSource) {
                    putDB((DataSource) o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.PUB_DB_TABLE:
                if (o instanceof MetaDataTable) {
                    putPUB_TABLE((MetaDataTable) o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.PRI_DB_TABLE:
                if (o instanceof MetaDataTable) {
                    putPRI_TABLE((MetaDataTable) o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.DB_COLUNM:
                if (o instanceof MetaDataColumn) {
                    putCOLUNM((MetaDataColumn) o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            default:
                log.error("数据的类型不匹配,Object:" + o);
                throw new Exception("数据的类型不匹配,Object:" + o);
        }
    }


    public void putFOLDER(RuleFolder o) throws Exception {
        if (o == null || StringUtils.isBlank(o.getFolderId())) {
            throw new Exception("导入导出接口组:[" + o + "]异常");
        }
        this.folder = putObjToMap(this.folder, o.getFolderId(), o);
    }

    public void putMODEL_GROUP(ModelGroup o) throws Exception {
        if (o == null || StringUtils.isBlank(o.getModelGroupId())) {
            throw new Exception("导入导出接口组:[" + o + "]异常");
        }
        modelGroup = putObjToMap(modelGroup, o.getModelGroupId(), o);
    }

    public void putMODEL_VERSION(RuleDetailWithBLOBs model) throws Exception {
        if (model == null || StringUtils.isBlank(model.getRuleId())) {
            throw new Exception("导入导出接口组:[" + model + "]异常");
        }
        if (modelVersion == null || modelVersion.isEmpty()) {
            modelVersion = new HashMap<>();
        }
        modelVersion.put(model.getRuleId(), model);
    }

    public void putMODEL_HEADER(RuleDetailHeader model) throws Exception {
        if (model == null || StringUtils.isBlank(model.getRuleName())) {
            throw new Exception("导入导出接口组:[" + model + "]异常");
        }
        if (modelHeader == null || modelHeader.isEmpty()) {
            modelHeader = new HashMap<>();
        }
        modelHeader.put(model.getRuleName(), model);
    }

    public void putVARIABLE_GROUP(VariableGroup o) throws Exception {
        if (o == null || StringUtils.isBlank(o.getVariableGroupId())) {
            throw new Exception("导入导出接口组:[" + o + "]异常");
        }
        if (variableGroup == null || variableGroup.isEmpty()) {
            variableGroup = new HashMap<>();
        }
        variableGroup.put(o.getVariableGroupId(), o);
    }

    public void putVARIABLE(VariableTreeNode o) throws Exception {
        if (o == null || StringUtils.isBlank(o.getVariableId())) {
            throw new Exception("导入导出参数:[" + o + "]异常");
        }
        if (variable == null || variable.isEmpty()) {
            variable = new HashMap<>();
        }
        variable.put(o.getVariableId(), o);
    }

    public void putKPI_GROUP(KpiGroup o) throws Exception {
        if (o == null || StringUtils.isBlank(o.getKpiGroupId())) {
            throw new Exception("导入导出指标组:[" + o + "]异常");
        }
        if (kpiGroup == null || kpiGroup.isEmpty()) {
            kpiGroup = new HashMap<>();
        }
        kpiGroup.put(o.getKpiGroupId(), o);
    }

    public void putKPI(KpiDefinition o) throws Exception {
        if (o == null || StringUtils.isBlank(o.getKpiId())) {
            throw new Exception("导入导出指标:[" + o + "]异常");
        }
        if (kpi == null || kpi.isEmpty()) {
            kpi = new HashMap<>();
        }
        kpi.put(o.getKpiId(), o);
    }

    public void putAPI_GROUP(ApiGroup o) throws Exception {
        if (o == null || StringUtils.isBlank(o.getApiGroupId())) {
            throw new Exception("导入导出接口组组:[" + o + "]异常");
        }
        if (apiGroup == null || apiGroup.isEmpty()) {
            apiGroup = new HashMap<>();
        }
        apiGroup.put(o.getApiGroupId(), o);
    }

    public void putAPI(ApiConf o) throws Exception {
        if (o == null || StringUtils.isBlank(o.getApiId())) {
            throw new Exception("导入导出接口:[" + o + "]异常");
        }
        if (api == null || api.isEmpty()) {
            api = new HashMap<>();
        }
        api.put(o.getApiId(), o);
    }

    public void putDB(DataSource dataSource) throws Exception {
        if (dataSource == null || StringUtils.isBlank(dataSource.getDbId())) {
            throw new Exception("导入导出接口:[" + dataSource + "]异常");
        }
        if (db == null || db.isEmpty()) {
            db = new HashMap<>();
        }
        db.put(dataSource.getDbId(), dataSource);
    }

    public void putPUB_TABLE(MetaDataTable api) throws Exception {
        if (api == null || StringUtils.isBlank(api.getTableId())) {
            throw new Exception("导入导出接口:[" + api + "]异常");
        }
        if (pubDbTable == null || pubDbTable.isEmpty()) {
            pubDbTable = new HashMap<>();
        }
        pubDbTable.put(api.getTableId(), api);
    }

    public void putPRI_TABLE(MetaDataTable api) throws Exception {
        if (api == null || StringUtils.isBlank(api.getTableId())) {
            throw new Exception("导入导出接口:[" + api + "]异常");
        }
        if (priDbTable == null || priDbTable.isEmpty()) {
            priDbTable = new HashMap<>();
        }
        priDbTable.put(api.getTableId(), api);
    }

    public void putCOLUNM(MetaDataColumn api) throws Exception {
        if (api == null || StringUtils.isBlank(api.getColumnId())) {
            throw new Exception("导入导出接口:[" + api + "]异常");
        }
        if (dbColunm == null || dbColunm.isEmpty()) {
            dbColunm = new HashMap<>();
        }
        dbColunm.put(api.getColumnId(), api);
    }


    private Map putObjToMap(Map map, String key, Object value) {
        if (StringUtils.isBlank(key)) {
            return map;
        }
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(key, value);
        return map;
    }

    public boolean containsKey(String type, String id) {
        if (StringUtils.isBlank(type) || StringUtils.isBlank(id)) {
            log.warn("ResultData.containsKey有null,数据类型type:" + type + ",id:" + id);
            return false;
        }
        switch (type) {
            case ExportConstant.FOLDER:
                return mapContantKey(folder, id);
            case ExportConstant.MODEL_GROUP:
                return mapContantKey(modelGroup, id);
            case ExportConstant.MODEL_HEADER:
                return mapContantKey(modelHeader, id);
            case ExportConstant.MODEL_VERSION:
                return mapContantKey(modelVersion, id);
            case ExportConstant.VARIABLE_GROUP:
                return mapContantKey(variableGroup, id);
            case ExportConstant.VARIABLE:
                return mapContantKey(variable, id);
            case ExportConstant.KPI_GROUP:
                return mapContantKey(kpiGroup, id);
            case ExportConstant.KPI:
                return mapContantKey(kpi, id);
            case ExportConstant.API_GROUP:
                return mapContantKey(apiGroup, id);
            case ExportConstant.API:
                return mapContantKey(api, id);
            case ExportConstant.DB:
                return mapContantKey(db, id);
            case ExportConstant.PUB_DB_TABLE:
                return mapContantKey(pubDbTable, id);
            case ExportConstant.PRI_DB_TABLE:
                return mapContantKey(priDbTable, id);
            case ExportConstant.DB_COLUNM:
                return mapContantKey(dbColunm, id);
            default:
                log.error("数据的类型不匹配,id:" + id);
        }
        return false;
    }

    private boolean mapContantKey(Map map, String key) {
        if (map == null || map.isEmpty()) {
            return false;
        }
        return map.containsKey(key);
    }

    public boolean isEmpty() {
        boolean isEmpty = true;
        if (folder != null && !folder.isEmpty()) {
            isEmpty = false;
            return isEmpty;
        }
        if (modelGroup != null && !modelGroup.isEmpty()) {
            isEmpty = false;
            return isEmpty;
        }
        if (modelHeader != null && !modelHeader.isEmpty()) {
            isEmpty = false;
            return isEmpty;
        }
        if (modelVersion != null && !modelVersion.isEmpty()) {
            isEmpty = false;
            return isEmpty;
        }
        if (variableGroup != null && !variableGroup.isEmpty()) {
            isEmpty = false;
            return isEmpty;
        }
        if (variable != null && !variable.isEmpty()) {
            isEmpty = false;
            return isEmpty;
        }
        if (kpiGroup != null && !kpiGroup.isEmpty()) {
            isEmpty = false;
            return isEmpty;
        }
        if (kpi != null && !kpi.isEmpty()) {
            isEmpty = false;
            return isEmpty;
        }
        if (apiGroup != null && !apiGroup.isEmpty()) {
            isEmpty = false;
            return isEmpty;
        }
        if (api != null && !api.isEmpty()) {
            isEmpty = false;
            return isEmpty;
        }
        if (db != null && !db.isEmpty()) {
            isEmpty = false;
            return isEmpty;
        }
        if (pubDbTable != null && !pubDbTable.isEmpty()) {
            isEmpty = false;
            return isEmpty;
        }
        if (dbColunm != null && !dbColunm.isEmpty()) {
            isEmpty = false;
            return isEmpty;
        }
        return isEmpty;
    }

    //---------------------------get set---------------------------------------------

    public Map<String, RuleFolder> getFolder() {
        return folder;
    }

    public void setFolder(Map<String, RuleFolder> folder) {
        this.folder = folder;
    }

    public Map<String, ModelGroup> getModelGroup() {
        return modelGroup;
    }

    public void setModelGroup(Map<String, ModelGroup> modelGroup) {
        this.modelGroup = modelGroup;
    }

    public Map<String, RuleDetailHeader> getModelHeader() {
        return modelHeader;
    }

    public void setModelHeader(Map<String, RuleDetailHeader> modelHeader) {
        this.modelHeader = modelHeader;
    }

    public Map<String, RuleDetailWithBLOBs> getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(Map<String, RuleDetailWithBLOBs> modelVersion) {
        this.modelVersion = modelVersion;
    }

    public Map<String, VariableGroup> getVariableGroup() {
        return variableGroup;
    }

    public void setVariableGroup(Map<String, VariableGroup> variableGroup) {
        this.variableGroup = variableGroup;
    }

    public Map<String, VariableTreeNode> getVariable() {
        return variable;
    }

    public void setVariable(Map<String, VariableTreeNode> variable) {
        this.variable = variable;
    }

    public Map<String, KpiGroup> getKpiGroup() {
        return kpiGroup;
    }

    public void setKpiGroup(Map<String, KpiGroup> kpiGroup) {
        this.kpiGroup = kpiGroup;
    }

    public Map<String, KpiDefinition> getKpi() {
        return kpi;
    }

    public void setKpi(Map<String, KpiDefinition> kpi) {
        this.kpi = kpi;
    }

    public Map<String, ApiGroup> getApiGroup() {
        return apiGroup;
    }

    public void setApiGroup(Map<String, ApiGroup> apiGroup) {
        this.apiGroup = apiGroup;
    }

    public Map<String, ApiConf> getApi() {
        return api;
    }

    public void setApi(Map<String, ApiConf> api) {
        this.api = api;
    }

    public Map<String, DataSource> getDb() {
        return db;
    }

    public void setDb(Map<String, DataSource> db) {
        this.db = db;
    }

    public Map<String, MetaDataTable> getPubDbTable() {
        return pubDbTable;
    }

    public void setPubDbTable(Map<String, MetaDataTable> pubDbTable) {
        this.pubDbTable = pubDbTable;
    }

    public Map<String, MetaDataTable> getPriDbTable() {
        return priDbTable;
    }

    public void setPriDbTable(Map<String, MetaDataTable> priDbTable) {
        this.priDbTable = priDbTable;
    }

    public Map<String, MetaDataColumn> getDbColunm() {
        return dbColunm;
    }

    public void setDbColunm(Map<String, MetaDataColumn> dbColunm) {
        this.dbColunm = dbColunm;
    }
//public boolean containsFolder(String folderId) {
    //        if (FOLDER == null && FOLDER.isEmpty()) {
    //            return false;
    //        }
    //        return FOLDER.containsKey(folderId);
    //    }
    //
    //    public boolean containsModelVersion(String modelGroupId) {
    //        if (MODEL_VERSION == null || MODEL_VERSION.isEmpty()) {
    //            return false;
    //        }
    //        return MODEL_VERSION.containsKey(modelGroupId);
    //    }
    //
    //    public boolean containsModelHeader(String modelGroupId) {
    //        if (MODEL_HEADER == null || MODEL_HEADER.isEmpty()) {
    //            return false;
    //        }
    //        return MODEL_HEADER.containsKey(modelGroupId);
    //    }
    //
    //    public boolean containsModelGroup(String modelGroupId) {
    //        if (MODEL_GROUP == null || MODEL_GROUP.isEmpty()) {
    //            return false;
    //        }
    //        return MODEL_GROUP.containsKey(modelGroupId);
    //    }
    //
    //
    //    public boolean containsKpiGroup(String kpiGroupId) {
    //        if (KPI_GROUP == null || KPI_GROUP.isEmpty()) {
    //            return false;
    //        }
    //        return KPI_GROUP.containsKey(kpiGroupId);
    //    }
    //
    //    public boolean containsKpi(String kpiGroupId) {
    //        if (KPI == null || KPI.isEmpty()) {
    //            return false;
    //        }
    //        return KPI.containsKey(kpiGroupId);
    //    }
    //
    //    public boolean containsApiGroup(String apiGroupId) {
    //        if (API_GROUP == null || API_GROUP.isEmpty()) {
    //            return false;
    //        }
    //        return API_GROUP.containsKey(apiGroupId);
    //    }
    //
    //    public boolean containsApi(String kpiGroupId) {
    //        if (API == null || API.isEmpty()) {
    //            return false;
    //        }
    //        return API.containsKey(kpiGroupId);
    //    }
    //
    //    public boolean containsVariableGroup(String variableGroupId) {
    //        if (VARIABLE_GROUP == null || VARIABLE_GROUP.isEmpty()) {
    //            return false;
    //        }
    //        return VARIABLE_GROUP.containsKey(variableGroupId);
    //    }
    //
    //    public boolean containsVariable(String kpiGroupId) {
    //        if (VARIABLE == null || VARIABLE.isEmpty()) {
    //            return false;
    //        }
    //        return VARIABLE.containsKey(kpiGroupId);
    //    }
    //
    //    public boolean containsDB(String kpiGroupId) {
    //        if (DB == null || DB.isEmpty()) {
    //            return false;
    //        }
    //        return DB.containsKey(kpiGroupId);
    //    }
    //
    //    public boolean containsPubTable(String kpiGroupId) {
    //        if (PUB_DB_TABLE == null || PUB_DB_TABLE.isEmpty()) {
    //            return false;
    //        }
    //        return PUB_DB_TABLE.containsKey(kpiGroupId);
    //    }
    //
    //    public boolean containsPriTable(String kpiGroupId) {
    //        if (PRI_DB_TABLE == null || PRI_DB_TABLE.isEmpty()) {
    //            return false;
    //        }
    //        return PRI_DB_TABLE.containsKey(kpiGroupId);
    //    }
    //
    //    public boolean containsColumn(String kpiGroupId) {
    //        return mapContantKey(DB_COLUNM, kpiGroupId);
    //    }

}
