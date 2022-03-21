package com.bonc.frame.entity.modelImportAndExport.modelExport.entity.lack;

import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.variable.Variable;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/6 2:45
 */
@Deprecated
public class ResultFrom {
    Log log = LogFactory.getLog(ResultFrom.class);

//    private Map<String, ModelReport> MODEL;

    private Map<String, VariableReport> VARIABLE;

    private Map<String, ApiReport> API;

    private Map<String, KpiReport> KPI;

    private Map<String, DBReport> DB;

    private Map<String, DBTableReport> PRI_TABLE;
    private Map<String, DBTableReport> PUB_TABLE;
    private Map<String, DBColumnReport> COLUMN;

    private String paseNullId(Map map) {
        int i = 1;
        if (map == null || map.isEmpty()) {
            return "NULL" + i;
        }
        while (map.containsKey("NULL" + i)) {
            i++;
        }
        return "NULL" + i;
    }


    public void putVariableLack(Variable variable, String fromObjType, String fromObjId, Object fromObj) {
        if (variable == null) {
            return;
        }
        if (VARIABLE == null) {
            VARIABLE = new HashMap<>();
        }
        String variableId = variable.getVariableId();
        if (StringUtils.isBlank(variableId)) {
            variableId = paseNullId(VARIABLE);
        }
        VariableReport variableReport = VARIABLE.get(variableId);
        if (variableReport == null) {
            variableReport = new VariableReport();
            variableReport.setVariableId(variableId);
            variableReport.setVariableAlias(variable.getVariableAlias());
            variableReport.setVariableCode(variable.getVariableCode());
            VARIABLE.put(variableId, variableReport);
        }
        variableReport.addObjectFrom(fromObjType, fromObj);
    }

    public void putApiLack(ApiConf apiConf, String fromObjType, String fromObjId, Object fromObj) {
        if (apiConf != null) {
            String apiId = apiConf.getApiId();
            if (API == null) {
                API = new HashMap<>();
            }
            ApiReport apiReport = API.get(apiId);
            if (apiReport == null) {
                apiReport = new ApiReport();
                apiReport.setApiId(apiId);
                apiReport.setApiDesc(apiConf.getApiDesc());
                apiReport.setApiName(apiConf.getApiName());
//                apiConf.get
//                apiReport.setAPI_VARIABLE();
                API.put(apiId, apiReport);
            }
            apiReport.addObjectFrom(fromObjType, fromObj);
        }
    }

    public void putKpiLack(KpiDefinition kpiDefinition, String fromObjType, String fromObjId, Object fromObj) {
        if (kpiDefinition != null) {
            String kpiId = kpiDefinition.getKpiId();
            if (KPI == null) {
                KPI = new HashMap<>();
            }
            KpiReport kpiReport = KPI.get(kpiId);
            if (kpiReport == null) {
                kpiReport = new KpiReport();
                kpiReport.setKpiId(kpiId);
                kpiReport.setKpiDesc(kpiDefinition.getKpiDesc());
                kpiReport.setKpiName(kpiDefinition.getKpiName());
                KPI.put(kpiId, kpiReport);
            }
            kpiReport.addObjectFrom(fromObjType, fromObj);
        }
    }

}
