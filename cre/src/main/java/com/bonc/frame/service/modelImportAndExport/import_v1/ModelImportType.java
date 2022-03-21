package com.bonc.frame.service.modelImportAndExport.import_v1;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/29 2:49
 */
public enum ModelImportType {
    ABS("abs"),
    FOLDER("folder"),
    MODEL_GROUP("modelGroup"),
    MODEL_HEADER("modelHeader"),
    MODEL_VERSION("modelVersion"),
    VARIABLE_GROUP("variableGroup"),
    VARIABLE("variable"),
    KPI_GROUP("kpiGroup"),
    KPI("kpi"),
    API_GROUP("apiGroup"),
    API("api"),
    DB("db"),
    PUB_DB_TABLE("pubDbTable"),
    PRI_DB_TABLE("priDbTable"),
    DB_COLUNM("dbColunm");

    private String value;

    ModelImportType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
