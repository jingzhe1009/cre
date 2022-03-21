package com.bonc.frame.entity.modelImportAndExport.modelExport.entity;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/5 16:09
 */
public class ExportConstant {
    public static final String FOLDER_PUB_ID = "0000000000000000000000000000001";
    public static final String FOLDER = "folder";
    public static final String FOLDER_NAME = "folderName";
    public static final String FOLDER_ID = "folderId";

    public static final String MODEL_GROUP_TMP_ID = "folder%o%modelGroup%Tmp%Id";
    public static final String MODEL_GROUP = "modelGroup";
    public static final String MODEL_GROUP_NAME = "modelGroupName";
    public static final String MODEL_GROUP_ID = "modelGroupId";

    public static final String MODEL_HEADER = "modelHeader";
    public static final String MODEL_RULE_NAME = "ruleName";
    public static final String MODEL_NAME = "modelName";

    public static final String MODEL_VERSION = "modelVersion";
    public static final String MODEL_VERSION_ID = "versionId";
    public static final String MODEL_VERSION_NUMBER = "version";

    public static final String VARIABLE_GROUP = "variableGroup";
    public static final String VARIABLE_GROUP_NAME = "variableGroupName";
    public static final String VARIABLE_GROUP_ID = "variableGroupId";

    public static final String VARIABLE = "variable";
    public static final String VARIABLE_CODE = VARIABLE + "Code";
    public static final String VARIABLE_ALIAS = VARIABLE + "Alias";
    public static final String VARIABLE_ID = VARIABLE + "Id";
    public static final String VARIABLE_ENTITY_ID = VARIABLE + "EntityId";

    public static final String KPI_GROUP = "kpiGroup";
    public static final String KPI_GROUP_ID = "kpiGroupId";
    public static final String KPI_GROUP_NAME = "kpiGroupName";

    public static final String KPI = "kpi";
    public static final String KPI_ID = "kpiId";
    public static final String KPI_CODE = "kpiCode";
    public static final String KPI_NAME = "kpiName";

    public static final String API_GROUP = "apiGroup";
    public static final String API_GROUP_ID = "apiGroupId";
    public static final String API_GROUP_NAME = "apiGroupName";

    public static final String API = "api";
    public static final String API_ID = "apiId";
    public static final String API_NAME = "apiName";
    public static final String API_URL = "apiUrl";

    public static final String DB = "db";
    public static final String DB_ID = "dbId";
    public static final String DB_ALIAS = "dbAlias";
    public static final String DB_URL = "dbURL";

    public static final String PUB_DB_TABLE = "pubDbTable";
    public static final String PUB_DB_TABLE_CODE = "pubDbTableCode";
    public static final String PUB_DB_TABLE_NAME = "pubDbTableName";
    public static final String PUB_DB_TABLE_ID = "pubDbTableId";

    public static final String PRI_DB_TABLE = "priDbTable";

    public static final String DB_COLUNM = "dbColunm";
    public static final String DB_COLUNM_CODE = "dbColunmCode";
    public static final String DB_COLUNM_NAME = "dbColunmName";
    public static final String DB_COLUNM_ID = "dbColunmId";


    public static final String importSuccessType_useImportData = "useImportData"; // 直接导入
//    public static final String importSuccessType_useDuplicateData = "useDuplicateData"; // 文件重复数据
    public static final String importSuccessType_useDuplicateData = "useUpdateData"; // 文件重复数据
    public static final String importSuccessType_useUpdateData = "useUpdateData"; // 修改名称
    public static final String importSuccessType_useSystemData = "useSystemData"; // 使用系统

}
