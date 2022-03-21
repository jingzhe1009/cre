package com.bonc.frame.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.commonresource.ApiGroup;
import com.bonc.frame.entity.commonresource.ModelGroup;
import com.bonc.frame.entity.commonresource.VariableGroup;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.kpi.KpiFetchLimiters;
import com.bonc.frame.entity.kpi.KpiGroup;
import com.bonc.frame.entity.metadata.MetaDataColumn;
import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportResult;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report.*;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.report.ImportObjectBaseInfo;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.report.ObjectImportReport;
import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.rulefolder.RuleFolder;
import com.bonc.frame.entity.variable.Variable;
import com.bonc.frame.entity.variable.VariableTreeNode;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/13 14:30
 */
public class ExportUtil {


    public static String getObjectId(String type, Object o) {
        if (StringUtils.isBlank(type) || o == null) {
            return null;
        }
        String id = null;
        switch (type) {
            case ExportConstant.FOLDER:
                if (o instanceof RuleFolder) {
                    id = ((RuleFolder) o).getFolderId();
                }
                break;
            case ExportConstant.MODEL_GROUP:
                if (o instanceof ModelGroup) {
                    id = ((ModelGroup) o).getModelGroupId();
                }
                break;
            case ExportConstant.MODEL_HEADER:
                if (o instanceof RuleDetailHeader) {
                    id = ((RuleDetailHeader) o).getRuleName();
                }
                break;
            case ExportConstant.MODEL_VERSION:
                if (o instanceof RuleDetailWithBLOBs) {
                    id = ((RuleDetailWithBLOBs) o).getRuleId();
                }
                break;
            case ExportConstant.VARIABLE_GROUP:
                if (o instanceof VariableGroup) {
                    id = ((VariableGroup) o).getVariableGroupId();
                }
                break;
            case ExportConstant.VARIABLE:
                if (o instanceof Variable) {
                    id = ((Variable) o).getVariableId();
                }
                break;
            case ExportConstant.KPI_GROUP:
                if (o instanceof KpiGroup) {
                    id = ((KpiGroup) o).getKpiGroupId();
                }
                break;
            case ExportConstant.KPI:
                if (o instanceof KpiDefinition) {
                    id = ((KpiDefinition) o).getKpiId();
                }
                break;
            case ExportConstant.API_GROUP:
                if (o instanceof ApiGroup) {
                    id = ((ApiGroup) o).getApiGroupId();
                }
                break;
            case ExportConstant.API:
                if (o instanceof ApiConf) {
                    id = ((ApiConf) o).getApiId();
                }
                break;
            case ExportConstant.DB:
                if (o instanceof DataSource) {
                    id = ((DataSource) o).getDbId();
                }
                break;
            case ExportConstant.PUB_DB_TABLE:
            case ExportConstant.PRI_DB_TABLE:
                if (o instanceof MetaDataTable) {
                    id = ((MetaDataTable) o).getTableId();
                }
                break;

            case ExportConstant.DB_COLUNM:
                if (o instanceof MetaDataColumn) {
                    id = ((MetaDataColumn) o).getColumnId();
                }
                break;
        }
        return id;
    }

    public static String getObjectName(String type, Object o) {
        if (StringUtils.isBlank(type) || o == null) {
            return null;
        }
        String name = null;
        switch (type) {
            case ExportConstant.FOLDER:
                if (o instanceof RuleFolder) {
                    name = ((RuleFolder) o).getFolderName();
                }
                break;
            case ExportConstant.MODEL_GROUP:
                if (o instanceof ModelGroup) {
                    name = ((ModelGroup) o).getModelGroupName();
                }
                break;
            case ExportConstant.MODEL_HEADER:
                if (o instanceof RuleDetailHeader) {
                    name = ((RuleDetailHeader) o).getModuleName();
                }
                break;
            case ExportConstant.MODEL_VERSION:
                if (o instanceof RuleDetailWithBLOBs) {
                    name = ((RuleDetailWithBLOBs) o).getVersion();
                }
                break;
            case ExportConstant.VARIABLE_GROUP:
                if (o instanceof VariableGroup) {
                    name = ((VariableGroup) o).getVariableGroupName();
                }
                break;
            case ExportConstant.VARIABLE:
                if (o instanceof Variable) {
                    name = ((Variable) o).getVariableAlias();
                }
                break;
            case ExportConstant.KPI_GROUP:
                if (o instanceof KpiGroup) {
                    name = ((KpiGroup) o).getKpiGroupName();
                }
                break;
            case ExportConstant.KPI:
                if (o instanceof KpiDefinition) {
                    name = ((KpiDefinition) o).getKpiName();
                }
                break;
            case ExportConstant.API_GROUP:
                if (o instanceof ApiGroup) {
                    name = ((ApiGroup) o).getApiGroupName();
                }
                break;
            case ExportConstant.API:
                if (o instanceof ApiConf) {
                    name = ((ApiConf) o).getApiName();
                }
                break;
            case ExportConstant.DB:
                if (o instanceof DataSource) {
                    name = ((DataSource) o).getDbAlias();
                }
                break;
            case ExportConstant.PUB_DB_TABLE:
            case ExportConstant.PRI_DB_TABLE:
                if (o instanceof MetaDataTable) {
                    name = ((MetaDataTable) o).getTableName();
                }
                break;

            case ExportConstant.DB_COLUNM:
                if (o instanceof MetaDataColumn) {
                    name = ((MetaDataColumn) o).getColumnName();
                }
                break;
        }
        return name;
    }

    public static String getObjectCode(String type, Object o) {
        if (StringUtils.isBlank(type) || o == null) {
            return null;
        }
        String code = null;
        switch (type) {
            case ExportConstant.VARIABLE:
                if (o instanceof Variable) {
                    code = ((Variable) o).getVariableCode();
                }
                break;

            case ExportConstant.KPI:
                if (o instanceof KpiDefinition) {
                    code = ((KpiDefinition) o).getKpiCode();
                }
                break;

            case ExportConstant.PUB_DB_TABLE:
            case ExportConstant.PRI_DB_TABLE:
                if (o instanceof MetaDataTable) {
                    code = ((MetaDataTable) o).getTableCode();
                }
                break;

            case ExportConstant.DB_COLUNM:
                if (o instanceof MetaDataColumn) {
                    code = ((MetaDataColumn) o).getColumnCode();
                }
                break;
        }
        return code;
    }

    public static String getObjectIsPublic(String type, Object o) {
        if (StringUtils.isBlank(type) || o == null) {
            return null;
        }
        String isPublic = null;
        switch (type) {
            case ExportConstant.FOLDER:
                if (o instanceof RuleFolder) {
                    isPublic = "0";
                }
                break;
            case ExportConstant.MODEL_GROUP:
                if (o instanceof ModelGroup) {
                    isPublic = "1";
                }
                break;
            case ExportConstant.MODEL_HEADER:
                if (o instanceof RuleDetailHeader) {
                    isPublic = ((RuleDetailHeader) o).getIsPublic();
                }
                break;
            case ExportConstant.MODEL_VERSION:
                if (o instanceof RuleDetailWithBLOBs) {
                    isPublic = ((RuleDetailWithBLOBs) o).getIsPublic();
                }
                break;
            case ExportConstant.VARIABLE_GROUP:
                if (o instanceof VariableGroup) {
                    isPublic = "1";
                }
                break;
            case ExportConstant.VARIABLE:
                if (o instanceof Variable) {
                    isPublic = ((Variable) o).getIsPublic();
                }
                break;
            case ExportConstant.KPI_GROUP:
                if (o instanceof KpiGroup) {
                    isPublic = "1";
                }
                break;
            case ExportConstant.KPI:
                if (o instanceof KpiDefinition) {
                    isPublic = "1";
                }
                break;
            case ExportConstant.API_GROUP:
                if (o instanceof ApiGroup) {
                    isPublic = "1";
                }
                break;
            case ExportConstant.API:
                if (o instanceof ApiConf) {
                    isPublic = ((ApiConf) o).getIsPublic();
                }
                break;
            case ExportConstant.DB:
                if (o instanceof DataSource) {
                    isPublic = "1";
                }
                break;
            case ExportConstant.PUB_DB_TABLE:
            case ExportConstant.PRI_DB_TABLE:
                if (o instanceof MetaDataTable) {
                    isPublic = "1";
                }
                break;

            case ExportConstant.DB_COLUNM:
                if (o instanceof MetaDataColumn) {
                    isPublic = "1";
                }
                break;
        }
        return isPublic;
    }

    public static String setObjectIsPublic(String type, Object o, String isPublic) {
        if (StringUtils.isBlank(type) || o == null) {
            return null;
        }
        switch (type) {
            case ExportConstant.MODEL_HEADER:
                if (o instanceof RuleDetailHeader) {
                    ((RuleDetailHeader) o).setIsPublic(isPublic);
                }
                break;
            case ExportConstant.MODEL_VERSION:
                if (o instanceof RuleDetailWithBLOBs) {
                    ((RuleDetailWithBLOBs) o).setIsPublic(isPublic);
                }
                break;
            case ExportConstant.VARIABLE:
                if (o instanceof Variable) {
                    ((Variable) o).setIsPublic(isPublic);
                }
                break;
            case ExportConstant.API:
                if (o instanceof ApiConf) {
                    ((ApiConf) o).setIsPublic(isPublic);
                }
                break;
        }
        return isPublic;
    }

    public static Object copyObject(String type, Object needCopyObject) throws Exception {
        Object toData = newObject(type);
        if (toData == null) {
            throw new Exception("创建对象失败,type:" + type + "  实体:" + needCopyObject);
        }
        try {
            org.springframework.beans.BeanUtils.copyProperties(needCopyObject, toData);
        } catch (Exception e) {
            throw new Exception("实体类型转换失败,type:" + type + "  实体:" + needCopyObject, e);
        }
        if (type.equals(ExportConstant.KPI)) {
            KpiDefinition toDataKpi = (KpiDefinition) toData;
            KpiDefinition needCopyKpi = (KpiDefinition) needCopyObject;
            List<KpiFetchLimiters> kpiFetchLimitersList = needCopyKpi.getKpiFetchLimitersList();
            List<KpiFetchLimiters> toDataKpiFetch = null;
            if (kpiFetchLimitersList != null) {
                toDataKpiFetch = new ArrayList<>(kpiFetchLimitersList.size());
                for (KpiFetchLimiters needCopyKpiFetch : kpiFetchLimitersList) {
                    toDataKpiFetch.add(needCopyKpiFetch.clone());
                }
            }
            toDataKpi.setKpiFetchLimitersList(toDataKpiFetch);
        }
        return toData;
    }

    public static Object newObject(String type) throws Exception {
        if (StringUtils.isBlank(type)) {
            throw new Exception("创建对象失败,类型为null,需要创建对象的类型type:[" + type + "]");
        }
        Object o = null;
        switch (type) {
            case ExportConstant.FOLDER:
                o = new RuleFolder();
                break;
            case ExportConstant.MODEL_GROUP:
                o = new ModelGroup();
                break;
            case ExportConstant.MODEL_HEADER:
                o = new RuleDetailHeader();
                break;
            case ExportConstant.MODEL_VERSION:
                o = new RuleDetailWithBLOBs();
                break;
            case ExportConstant.VARIABLE_GROUP:
                o = new VariableGroup();
                break;
            case ExportConstant.VARIABLE:
                o = new VariableTreeNode();
                break;
            case ExportConstant.KPI_GROUP:
                o = new KpiGroup();
                break;
            case ExportConstant.KPI:
                o = new KpiDefinition();
                break;
            case ExportConstant.API_GROUP:
                o = new ApiGroup();
                break;
            case ExportConstant.API:
                o = new ApiConf();
                break;
            case ExportConstant.DB:
                o = new DataSource();
                break;
            case ExportConstant.PUB_DB_TABLE:
            case ExportConstant.PRI_DB_TABLE:
                o = new MetaDataTable();
                break;
            case ExportConstant.DB_COLUNM:
                o = new MetaDataColumn();
                break;
        }
        return o;
    }

    /**
     * idOrNameValue 与 o2中的属性进行比较
     *
     * @param objectPropertyType o2中的属性类型
     */
    public static boolean compareObjectProperty(String objectPropertyType, String idOrNameValue, Object o2) {
        if (StringUtils.isBlank(objectPropertyType)) {
            return false;
        }
        if (o2 == null) {
            return false;
        }
//        String objectFolderId = getObjectFolderId("", o2);
        boolean result = ModelComparUtil.isEqualsString(idOrNameValue, getObjectPropertyKeyValue(objectPropertyType, o2));
//        switch (objectPropertyType) {
//            case ExportConstant.FOLDER_NAME:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((RuleFolder) o2).getFolderName());
//                break;
//            case ExportConstant.FOLDER_ID:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((RuleFolder) o2).getFolderId());
//                break;
//            case ExportConstant.MODEL_GROUP_NAME:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((ModelGroup) o2).getModelGroupName());
//                break;
//            case ExportConstant.MODEL_GROUP_ID:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((ModelGroup) o2).getModelGroupId());
//                break;
//            case ExportConstant.MODEL_RULE_NAME:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((RuleDetailHeader) o2).getRuleName());
//                break;
//            case ExportConstant.MODEL_NAME:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((RuleDetailHeader) o2).getFolderId() + "-" + ((RuleDetailHeader) o2).getModuleName());
//                break;
//            case ExportConstant.MODEL_VERSION_ID:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((RuleDetailWithBLOBs) o2).getRuleId());
//                break;
//            case ExportConstant.MODEL_VERSION_NUMBER:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((RuleDetailWithBLOBs) o2).getRuleName() + "-" + ((RuleDetailWithBLOBs) o2).getVersion());
//                break;
//            case ExportConstant.VARIABLE_GROUP_NAME:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((VariableGroup) o2).getVariableGroupName());
//                break;
//            case ExportConstant.VARIABLE_GROUP_ID:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((VariableGroup) o2).getVariableGroupId());
//                break;
//            case ExportConstant.VARIABLE_CODE:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((Variable) o2).getFolderId() + "-" + ((Variable) o2).getVariableCode());
//                break;
//            case ExportConstant.VARIABLE_ALIAS:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((Variable) o2).getFolderId() + "-" + ((Variable) o2).getVariableAlias());
//                break;
//            case ExportConstant.VARIABLE_ID:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((Variable) o2).getVariableId());
//                break;
//            case ExportConstant.VARIABLE_ENTITY_ID:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((Variable) o2).getEntityId());
//                break;
//            case ExportConstant.KPI_GROUP_ID:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((KpiGroup) o2).getKpiGroupId());
//                break;
//            case ExportConstant.KPI_GROUP_NAME:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((KpiGroup) o2).getKpiGroupName());
//                break;
//            case ExportConstant.KPI_CODE:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((KpiDefinition) o2).getKpiCode());
//                break;
//            case ExportConstant.KPI_NAME:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((KpiDefinition) o2).getKpiName());
//                break;
//            case ExportConstant.KPI_ID:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((KpiDefinition) o2).getKpiId());
//                break;
//            case ExportConstant.API_GROUP_ID:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((ApiGroup) o2).getApiGroupId());
//                break;
//            case ExportConstant.API_GROUP_NAME:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((ApiGroup) o2).getApiGroupName());
//                break;
//            case ExportConstant.API_ID:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((ApiConf) o2).getApiId());
//                break;
//            case ExportConstant.API_NAME:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((ApiConf) o2).getFolderId() + ((ApiConf) o2).getApiName());
//                break;
//            case ExportConstant.DB_ALIAS:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((DataSource) o2).getDbAlias());
//                break;
//            case ExportConstant.DB_ID:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((DataSource) o2).getDbId());
//                break;
//            case ExportConstant.PUB_DB_TABLE_CODE:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((MetaDataTable) o2).getDbId() + ((MetaDataTable) o2).getTableCode());
//                break;
//            case ExportConstant.PUB_DB_TABLE_ID:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((MetaDataTable) o2).getTableId());
//                break;
//            case ExportConstant.PUB_DB_TABLE_NAME:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((MetaDataTable) o2).getDbId() + ((MetaDataTable) o2).getTableName());
//                break;
//            case ExportConstant.DB_COLUNM_CODE:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((MetaDataColumn) o2).getTableId() + ((MetaDataColumn) o2).getColumnCode());
//                break;
//            case ExportConstant.DB_COLUNM_ID:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((MetaDataColumn) o2).getColumnId());
//                break;
//            case ExportConstant.DB_COLUNM_NAME:
//                result = ModelComparUtil.isEqualsString(idOrNameValue, ((MetaDataColumn) o2).getTableId() + ((MetaDataColumn) o2).getColumnName());
//                break;
////            case ExportConstant.API_URL:
////                result = ModelComparUtil.isEqualsString(((ApiConf) o1).paseApiUrlWithReturnAndParam(), ((ApiConf) o2).paseApiUrlWithReturnAndParam());
////                break;
////            case ExportConstant.DB_URL:
////                result = ModelComparUtil.isEqualsString(((DataSource) o1).paseUrlWithReturnAndParam(), ((DataSource) o2).paseUrlWithReturnAndParam());
////                break;
//            default:
//                System.err.println("异常类型type:" + objectPropertyType);
//                break;
//        }
        return result;
    }

    public static String getObjectPropertyKeyValue(String objectPropertyType, Object o2) {
        if (StringUtils.isBlank(objectPropertyType)) {
            return null;
        }
        if (o2 == null) {
            return null;
        }
//        String objectFolderId = getObjectFolderId("", o2);
        String result = null;
        switch (objectPropertyType) {
            case ExportConstant.FOLDER_NAME:
                result = ((RuleFolder) o2).getFolderName();
                break;
            case ExportConstant.FOLDER_ID:
                result = ((RuleFolder) o2).getFolderId();
                break;
            case ExportConstant.MODEL_GROUP_NAME:
                result = ((ModelGroup) o2).getModelGroupName();
                break;
            case ExportConstant.MODEL_GROUP_ID:
                result = ((ModelGroup) o2).getModelGroupId();
                break;
            case ExportConstant.MODEL_RULE_NAME:
                result = ((RuleDetailHeader) o2).getRuleName();
                break;
            case ExportConstant.MODEL_NAME:
                result = ((RuleDetailHeader) o2).getFolderId() + "-" + ((RuleDetailHeader) o2).getModuleName();
                break;
            case ExportConstant.MODEL_VERSION_ID:
                result = ((RuleDetailWithBLOBs) o2).getRuleId();
                break;
            case ExportConstant.MODEL_VERSION_NUMBER:
                result = ((RuleDetailWithBLOBs) o2).getRuleName() + "-" + ((RuleDetailWithBLOBs) o2).getVersion();
                break;
            case ExportConstant.VARIABLE_GROUP_NAME:
                result = ((VariableGroup) o2).getVariableGroupName();
                break;
            case ExportConstant.VARIABLE_GROUP_ID:
                result = ((VariableGroup) o2).getVariableGroupId();
                break;
            case ExportConstant.VARIABLE_CODE:
                result = ((Variable) o2).getFolderId() + "-" + ((Variable) o2).getVariableCode();
                break;
            case ExportConstant.VARIABLE_ALIAS:
                result = ((Variable) o2).getFolderId() + "-" + ((Variable) o2).getVariableAlias();
                break;
            case ExportConstant.VARIABLE_ID:
                result = ((Variable) o2).getVariableId();
                break;
            case ExportConstant.VARIABLE_ENTITY_ID:
                result = ((Variable) o2).getEntityId();
                break;
            case ExportConstant.KPI_GROUP_ID:
                result = ((KpiGroup) o2).getKpiGroupId();
                break;
            case ExportConstant.KPI_GROUP_NAME:
                result = ((KpiGroup) o2).getKpiGroupName();
                break;
            case ExportConstant.KPI_CODE:
                result = ((KpiDefinition) o2).getKpiCode();
                break;
            case ExportConstant.KPI_NAME:
                result = ((KpiDefinition) o2).getKpiName();
                break;
            case ExportConstant.KPI_ID:
                result = ((KpiDefinition) o2).getKpiId();
                break;
            case ExportConstant.API_GROUP_ID:
                result = ((ApiGroup) o2).getApiGroupId();
                break;
            case ExportConstant.API_GROUP_NAME:
                result = ((ApiGroup) o2).getApiGroupName();
                break;
            case ExportConstant.API_ID:
                result = ((ApiConf) o2).getApiId();
                break;
            case ExportConstant.API_NAME:
                result = ((ApiConf) o2).getFolderId() + ((ApiConf) o2).getApiName();
                break;
            case ExportConstant.DB_ALIAS:
                result = ((DataSource) o2).getDbAlias();
                break;
            case ExportConstant.DB_ID:
                result = ((DataSource) o2).getDbId();
                break;
            case ExportConstant.PUB_DB_TABLE_CODE:
                result = ((MetaDataTable) o2).getDbId() + ((MetaDataTable) o2).getTableCode();
                break;
            case ExportConstant.PUB_DB_TABLE_ID:
                result = ((MetaDataTable) o2).getTableId();
                break;
            case ExportConstant.PUB_DB_TABLE_NAME:
                result = ((MetaDataTable) o2).getDbId() + ((MetaDataTable) o2).getTableName();
                break;
            case ExportConstant.DB_COLUNM_CODE:
                result = ((MetaDataColumn) o2).getTableId() + ((MetaDataColumn) o2).getColumnCode();
                break;
            case ExportConstant.DB_COLUNM_ID:
                result = ((MetaDataColumn) o2).getColumnId();
                break;
            case ExportConstant.DB_COLUNM_NAME:
                result = ((MetaDataColumn) o2).getTableId() + ((MetaDataColumn) o2).getColumnName();
                break;
//            case ExportConstant.API_URL:
//                result = ModelComparUtil.isEqualsString(((ApiConf) o1).paseApiUrlWithReturnAndParam(), ((ApiConf) o2).paseApiUrlWithReturnAndParam();
//                break;
//            case ExportConstant.DB_URL:
//                result = ModelComparUtil.isEqualsString(((DataSource) o1).paseUrlWithReturnAndParam(), ((DataSource) o2).paseUrlWithReturnAndParam();
//                break;
            default:
                System.err.println("异常类型type:" + objectPropertyType);
                break;
        }
        return result;
    }


    /**
     * 获取o2的属性值
     *
     * @param objectPropertyType o2中的属性类型
     */
    public static String getObjectProperty(String objectPropertyType, Object o2) {
        if (StringUtils.isBlank(objectPropertyType)) {
            return null;
        }
        if (o2 == null) {
            return null;
        }

//        String objectFolderId = getObjectFolderId("", o2);
        String result = null;
        switch (objectPropertyType) {
            case ExportConstant.FOLDER_NAME:
                result = ((RuleFolder) o2).getFolderName();
                break;
            case ExportConstant.FOLDER_ID:
                result = ((RuleFolder) o2).getFolderId();
                break;
            case ExportConstant.MODEL_GROUP_NAME:
                result = ((ModelGroup) o2).getModelGroupName();
                break;
            case ExportConstant.MODEL_GROUP_ID:
                result = ((ModelGroup) o2).getModelGroupId();
                break;
            case ExportConstant.MODEL_RULE_NAME:
                result = ((RuleDetailHeader) o2).getRuleName();
                break;
            case ExportConstant.MODEL_NAME:
                result = ((RuleDetailHeader) o2).getModuleName();
                break;
            case ExportConstant.MODEL_VERSION_ID:
                result = ((RuleDetailWithBLOBs) o2).getRuleId();
                break;
            case ExportConstant.MODEL_VERSION_NUMBER:
                result = ((RuleDetailWithBLOBs) o2).getVersion();
                break;
            case ExportConstant.VARIABLE_GROUP_NAME:
                result = ((VariableGroup) o2).getVariableGroupName();
                break;
            case ExportConstant.VARIABLE_GROUP_ID:
                result = ((VariableGroup) o2).getVariableGroupId();
                break;
            case ExportConstant.VARIABLE_CODE:
                result = ((Variable) o2).getVariableCode();
                break;
            case ExportConstant.VARIABLE_ALIAS:
                result = ((Variable) o2).getVariableAlias();
                break;
            case ExportConstant.VARIABLE_ID:
                result = ((Variable) o2).getVariableId();
                break;
            case ExportConstant.VARIABLE_ENTITY_ID:
                result = ((Variable) o2).getEntityId();
                break;
            case ExportConstant.KPI_GROUP_ID:
                result = ((KpiGroup) o2).getKpiGroupId();
                break;
            case ExportConstant.KPI_GROUP_NAME:
                result = ((KpiGroup) o2).getKpiGroupName();
                break;
            case ExportConstant.KPI_CODE:
                result = ((KpiDefinition) o2).getKpiCode();
                break;
            case ExportConstant.KPI_NAME:
                result = ((KpiDefinition) o2).getKpiName();
                break;
            case ExportConstant.KPI_ID:
                result = ((KpiDefinition) o2).getKpiId();
                break;
            case ExportConstant.API_GROUP_ID:
                result = ((ApiGroup) o2).getApiGroupId();
                break;
            case ExportConstant.API_GROUP_NAME:
                result = ((ApiGroup) o2).getApiGroupName();
                break;
            case ExportConstant.API_ID:
                result = ((ApiConf) o2).getApiId();
                break;
            case ExportConstant.API_NAME:
                result = ((ApiConf) o2).getApiName();
                break;
            case ExportConstant.DB_ALIAS:
                result = ((DataSource) o2).getDbAlias();
                break;
            case ExportConstant.DB_ID:
                result = ((DataSource) o2).getDbId();
                break;

            case ExportConstant.PUB_DB_TABLE_CODE:
                result = ((MetaDataTable) o2).getTableCode();
                break;
            case ExportConstant.PUB_DB_TABLE_ID:
                result = ((MetaDataTable) o2).getTableId();
                break;
            case ExportConstant.PUB_DB_TABLE_NAME:
                result = ((MetaDataTable) o2).getTableName();
                break;
            case ExportConstant.DB_COLUNM_CODE:
                result = ((MetaDataColumn) o2).getColumnCode();
                break;
            case ExportConstant.DB_COLUNM_ID:
                result = ((MetaDataColumn) o2).getColumnId();
                break;
            case ExportConstant.DB_COLUNM_NAME:
                result = ((MetaDataColumn) o2).getColumnName();
                break;
//            case ExportConstant.API_URL:
//                result = ModelComparUtil.isEqualsString(((ApiConf) o1).paseApiUrlWithReturnAndParam(), ((ApiConf) o2).paseApiUrlWithReturnAndParam();
//                break;
//            case ExportConstant.DB_URL:
//                result = ModelComparUtil.isEqualsString(((DataSource) o1).paseUrlWithReturnAndParam(), ((DataSource) o2).paseUrlWithReturnAndParam();
//                break;
            default:
                System.err.println("异常类型type:" + objectPropertyType);
                break;
        }
        return result;
    }

    /**
     * 获取o2的属性值的key
     * 名称,code的属性值的key是  场景ID+属性值
     *
     * @param objectPropertyType o2中的属性类型
     */
    public static String getObjectPropertyKey(String objectType, String objectPropertyType, Object o2) {
        if (StringUtils.isBlank(objectPropertyType) || StringUtils.isBlank(objectType)) {
            return null;
        }
        if (o2 == null) {
            return null;
        }

        String objectFolderId = getObjectFolderId(objectType, o2);
        String result = null;
        switch (objectPropertyType) {
            case ExportConstant.FOLDER_NAME:
                result = ((RuleFolder) o2).getFolderName();
                break;
            case ExportConstant.FOLDER_ID:
                result = ((RuleFolder) o2).getFolderId();
                break;
            case ExportConstant.MODEL_GROUP_NAME:
                result = objectFolderId + "-" + ((ModelGroup) o2).getModelGroupName();
                break;
            case ExportConstant.MODEL_GROUP_ID:
                result = ((ModelGroup) o2).getModelGroupId();
                break;
            case ExportConstant.MODEL_RULE_NAME:
                result = ((RuleDetailHeader) o2).getRuleName();
                break;
            case ExportConstant.MODEL_NAME:
                result = ((RuleDetailHeader) o2).getModuleName();
                break;
            case ExportConstant.MODEL_VERSION_ID:
                result = ((RuleDetailWithBLOBs) o2).getRuleId();
                break;
            case ExportConstant.MODEL_VERSION_NUMBER:
                result = ((RuleDetailWithBLOBs) o2).getVersion();
                break;
            case ExportConstant.VARIABLE_GROUP_NAME:
                result = ((VariableGroup) o2).getVariableGroupName();
                break;
            case ExportConstant.VARIABLE_GROUP_ID:
                result = ((VariableGroup) o2).getVariableGroupId();
                break;
            case ExportConstant.VARIABLE_CODE:
                result = objectFolderId + "-" + ((Variable) o2).getVariableCode();
                break;
            case ExportConstant.VARIABLE_ALIAS:
                result = objectFolderId + "-" + ((Variable) o2).getVariableAlias();
                break;
            case ExportConstant.VARIABLE_ID:
                result = ((Variable) o2).getVariableId();
                break;
            case ExportConstant.VARIABLE_ENTITY_ID:
                result = ((Variable) o2).getEntityId();
                break;
            case ExportConstant.KPI_GROUP_ID:
                result = ((KpiGroup) o2).getKpiGroupId();
                break;
            case ExportConstant.KPI_GROUP_NAME:
                result = ((KpiGroup) o2).getKpiGroupName();
                break;
            case ExportConstant.KPI_CODE:
                result = ((KpiDefinition) o2).getKpiCode();
                break;
            case ExportConstant.KPI_NAME:
                result = ((KpiDefinition) o2).getKpiName();
                break;
            case ExportConstant.KPI_ID:
                result = ((KpiDefinition) o2).getKpiId();
                break;
            case ExportConstant.API_GROUP_ID:
                result = ((ApiGroup) o2).getApiGroupId();
                break;
            case ExportConstant.API_GROUP_NAME:
                result = ((ApiGroup) o2).getApiGroupName();
                break;
            case ExportConstant.API_ID:
                result = ((ApiConf) o2).getApiId();
                break;
            case ExportConstant.API_NAME:
                result = ((ApiConf) o2).getApiName();
                break;

            case ExportConstant.DB_ALIAS:
                result = ((DataSource) o2).getDbAlias();
                break;
            case ExportConstant.DB_ID:
                result = ((DataSource) o2).getDbId();
                break;

            case ExportConstant.PUB_DB_TABLE_CODE:
                result = ((MetaDataTable) o2).getTableCode();
                break;
            case ExportConstant.PUB_DB_TABLE_ID:
                result = ((MetaDataTable) o2).getTableId();
                break;
            case ExportConstant.PUB_DB_TABLE_NAME:
                result = ((MetaDataTable) o2).getTableName();
                break;
            case ExportConstant.DB_COLUNM_CODE:
                result = ((MetaDataColumn) o2).getColumnCode();
                break;
            case ExportConstant.DB_COLUNM_ID:
                result = ((MetaDataColumn) o2).getColumnId();
                break;
            case ExportConstant.DB_COLUNM_NAME:
                result = ((MetaDataColumn) o2).getColumnName();
                break;
//            case ExportConstant.API_URL:
//                result = ModelComparUtil.isEqualsString(((ApiConf) o1).paseApiUrlWithReturnAndParam(), ((ApiConf) o2).paseApiUrlWithReturnAndParam();
//                break;
//            case ExportConstant.DB_URL:
//                result = ModelComparUtil.isEqualsString(((DataSource) o1).paseUrlWithReturnAndParam(), ((DataSource) o2).paseUrlWithReturnAndParam();
//                break;
            default:
                System.err.println("异常类型type:" + objectPropertyType);
                break;
        }
        return result;
    }

    /**
     * idOrNameValue 与 o2中的属性进行比较
     *
     * @param objectPropertyType o2中的属性类型
     */
    public static void setObjectProperty(String objectPropertyType, String objectPropertyValue, Object o2) {
        if (StringUtils.isBlank(objectPropertyType)) {
            return;
        }
        if (o2 == null) {
            return;
        }
//        String objectFolderId = getObjectFolderId("", o2);
        switch (objectPropertyType) {
            case ExportConstant.FOLDER_NAME:
                ((RuleFolder) o2).setFolderName(objectPropertyValue);
                break;
            case ExportConstant.FOLDER_ID:
                ((RuleFolder) o2).setFolderId(objectPropertyValue);
                break;
            case ExportConstant.MODEL_GROUP_NAME:
                ((ModelGroup) o2).setModelGroupName(objectPropertyValue);
                break;
            case ExportConstant.MODEL_GROUP_ID:
                ((ModelGroup) o2).setModelGroupId(objectPropertyValue);
                break;
            case ExportConstant.MODEL_RULE_NAME:
                ((RuleDetailHeader) o2).setRuleName(objectPropertyValue);
                break;
            case ExportConstant.MODEL_NAME:
                ((RuleDetailHeader) o2).setModuleName(objectPropertyValue);
                break;
            case ExportConstant.MODEL_VERSION_ID:
                ((RuleDetailWithBLOBs) o2).setRuleId(objectPropertyValue);
                break;
            case ExportConstant.MODEL_VERSION_NUMBER:
                ((RuleDetailWithBLOBs) o2).setVersion(objectPropertyValue);
                break;
            case ExportConstant.VARIABLE_GROUP_NAME:
                ((VariableGroup) o2).setVariableGroupName(objectPropertyValue);
                break;
            case ExportConstant.VARIABLE_GROUP_ID:
                ((VariableGroup) o2).setVariableGroupId(objectPropertyValue);
                break;
            case ExportConstant.VARIABLE_CODE:
                ((Variable) o2).setVariableCode(objectPropertyValue);
                break;
            case ExportConstant.VARIABLE_ALIAS:
                ((Variable) o2).setVariableAlias(objectPropertyValue);
                break;
            case ExportConstant.VARIABLE_ID:
                ((Variable) o2).setVariableId(objectPropertyValue);
                break;
            case ExportConstant.VARIABLE_ENTITY_ID:
                ((Variable) o2).setEntityId(objectPropertyValue);
                break;
            case ExportConstant.KPI_GROUP_ID:
                ((KpiGroup) o2).setKpiGroupId(objectPropertyValue);
                break;
            case ExportConstant.KPI_GROUP_NAME:
                ((KpiGroup) o2).setKpiGroupName(objectPropertyValue);
                break;
            case ExportConstant.KPI_CODE:
                ((KpiDefinition) o2).setKpiCode(objectPropertyValue);
                break;
            case ExportConstant.KPI_NAME:
                ((KpiDefinition) o2).setKpiName(objectPropertyValue);
                break;
            case ExportConstant.KPI_ID:
                ((KpiDefinition) o2).setKpiId(objectPropertyValue);
                break;
            case ExportConstant.API_GROUP_ID:
                ((ApiGroup) o2).setApiGroupId(objectPropertyValue);
                break;
            case ExportConstant.API_GROUP_NAME:
                ((ApiGroup) o2).setApiGroupName(objectPropertyValue);
                break;
            case ExportConstant.API_ID:
                ((ApiConf) o2).setApiId(objectPropertyValue);
                break;
            case ExportConstant.API_NAME:
                ((ApiConf) o2).setApiName(objectPropertyValue);
                break;
            case ExportConstant.DB_ALIAS:
                ((DataSource) o2).setDbAlias(objectPropertyValue);
                break;
            case ExportConstant.DB_ID:
                ((DataSource) o2).setDbId(objectPropertyValue);
                break;
            case ExportConstant.PUB_DB_TABLE_CODE:
                ((MetaDataTable) o2).setTableCode(objectPropertyValue);
                break;
            case ExportConstant.PUB_DB_TABLE_ID:
                ((MetaDataTable) o2).setTableId(objectPropertyValue);
                break;
            case ExportConstant.PUB_DB_TABLE_NAME:
                ((MetaDataTable) o2).setTableName(objectPropertyValue);
                break;
            case ExportConstant.DB_COLUNM_CODE:
                ((MetaDataColumn) o2).setColumnCode(objectPropertyValue);
                break;
            case ExportConstant.DB_COLUNM_ID:
                ((MetaDataColumn) o2).setColumnId(objectPropertyValue);
                break;
            case ExportConstant.DB_COLUNM_NAME:
                ((MetaDataColumn) o2).setColumnName(objectPropertyValue);
                break;
//            case ExportConstant.API_URL:
//                 ModelComparUtil.isEqualsString(((ApiConf) o1).paseApiUrlWithReturnAndParam(), ((ApiConf) o2).paseApiUrlWithReturnAndParam();
//                break;
//            case ExportConstant.DB_URL:
//                 ModelComparUtil.isEqualsString(((DataSource) o1).paseUrlWithReturnAndParam(), ((DataSource) o2).paseUrlWithReturnAndParam();
//                break;
            default:
                System.err.println("异常类型type:" + objectPropertyType);
                break;
        }
    }

    public static String getTypeName(String type) {
        if (StringUtils.isBlank(type)) {
            return null;
        }
        String name = null;
        switch (type) {
            case ExportConstant.FOLDER:
                name = "场景";
                break;
            case ExportConstant.MODEL_GROUP:
                name = "模型组";
                break;
            case ExportConstant.MODEL_HEADER:
                name = "模型";
                break;
            case ExportConstant.MODEL_VERSION:
                name = "模型";
                break;
            case ExportConstant.VARIABLE_GROUP:
                name = "参数组";
                break;
            case ExportConstant.VARIABLE:
                name = "参数";
                break;
            case ExportConstant.KPI_GROUP:
                name = "指标组";
                break;
            case ExportConstant.KPI:
                name = "指标";
                break;
            case ExportConstant.API_GROUP:
                name = "接口组";
                break;
            case ExportConstant.API:
                name = "接口";
                break;
            case ExportConstant.DB:
                name = "数据源";
                break;
            case ExportConstant.PUB_DB_TABLE:
            case ExportConstant.PRI_DB_TABLE:
                name = "数据源表";
                break;
            case ExportConstant.DB_COLUNM:
                name = "数据源列";
                break;
        }
        return name;
    }

    public static String getObjectFolderId(String type, Object o) {
        if (StringUtils.isBlank(type) || o == null) {
            return null;
        }
        String id = null;
        switch (type) {
            case ExportConstant.FOLDER:
                if (o instanceof RuleFolder) {
                    id = ((RuleFolder) o).getFolderId();
                }
                break;
            case ExportConstant.MODEL_HEADER:
                if (o instanceof RuleDetailHeader) {
                    id = ((RuleDetailHeader) o).getFolderId();
                }
                break;
            case ExportConstant.MODEL_VERSION:
                if (o instanceof RuleDetailWithBLOBs) {
                    id = ((RuleDetailWithBLOBs) o).getFolderId();
                }
                break;
            case ExportConstant.VARIABLE:
                if (o instanceof Variable) {
                    id = ((Variable) o).getFolderId();
                }
                break;
            case ExportConstant.API:
                if (o instanceof ApiConf) {
                    id = ((ApiConf) o).getFolderId();
                }
                break;
        }
        return id;
    }

    public static void setObjectFolderId(String type, Object o, String folderId, String groupId) {
        if (StringUtils.isBlank(type) || o == null) {
            return;
        }
        switch (type) {
            case ExportConstant.FOLDER:
                if (o instanceof RuleFolder) {
                    ((RuleFolder) o).setFolderId(folderId);
                }
                break;
            case ExportConstant.MODEL_GROUP:
                if (o instanceof ModelGroup) {
                    ((ModelGroup) o).setModelGroupId(groupId);
                }
                break;
            case ExportConstant.MODEL_HEADER:
                if (o instanceof RuleDetailHeader) {
                    if ((((RuleDetailHeader) o).getIsPublic()) != null && "1".equals(((RuleDetailHeader) o).getIsPublic())) {
                        ((RuleDetailHeader) o).setModelGroupId(groupId);
                    } else {
                        ((RuleDetailHeader) o).setFolderId(folderId);
                    }
                }
                break;
            case ExportConstant.MODEL_VERSION:
                if (o instanceof RuleDetailWithBLOBs) {
                    if ((((RuleDetailWithBLOBs) o).getIsPublic()) != null && "1".equals(((RuleDetailWithBLOBs) o).getIsPublic())) {
                        ((RuleDetailWithBLOBs) o).setModelGroupId(groupId);
                    } else {
                        ((RuleDetailWithBLOBs) o).setFolderId(folderId);
                    }
                }
                break;
            case ExportConstant.VARIABLE_GROUP:
                if (o instanceof VariableGroup) {
                    ((VariableGroup) o).setVariableGroupId(groupId);
                }
                break;
            case ExportConstant.VARIABLE:
                if (o instanceof Variable) {
                    if ((((Variable) o).getIsPublic()) != null && "1".equals(((Variable) o).getIsPublic())) {
                        ((Variable) o).setVariableGroupId(groupId);
                    } else {
                        ((Variable) o).setFolderId(folderId);
                    }
                }
                break;
            case ExportConstant.KPI_GROUP:
                if (o instanceof KpiGroup) {
                    ((KpiGroup) o).setKpiGroupId(groupId);
                }
                break;
            case ExportConstant.KPI:
                if (o instanceof KpiDefinition) {

                    ((KpiDefinition) o).setKpiGroupId(groupId);

                }
                break;
            case ExportConstant.API_GROUP:
                if (o instanceof ApiGroup) {
                    ((ApiGroup) o).setApiGroupId(groupId);
                }
                break;
            case ExportConstant.API:
                if (o instanceof ApiConf) {
                    if ((((ApiConf) o).getIsPublic()) != null && "1".equals(((ApiConf) o).getIsPublic())) {
                        ((ApiConf) o).setApiGroupId(groupId);
                    } else {
                        ((ApiConf) o).setFolderId(folderId);
                    }
                }
                break;
        }
    }

    public static void setObjectGroupId(String type, Object o, String groupId) {
        if (StringUtils.isBlank(type) || o == null) {
            return;
        }
        switch (type) {
            case ExportConstant.MODEL_GROUP:
                if (o instanceof ModelGroup) {
                    ((ModelGroup) o).setModelGroupId(groupId);
                }
                break;
            case ExportConstant.MODEL_HEADER:
                if (o instanceof RuleDetailHeader) {
                    ((RuleDetailHeader) o).setModelGroupId(groupId);
//                    ((RuleDetailHeader) o).setIsPublic("1");
                }
                break;
            case ExportConstant.MODEL_VERSION:
                if (o instanceof RuleDetailWithBLOBs) {
                    ((RuleDetailWithBLOBs) o).setModelGroupId(groupId);
//                    ((RuleDetailHeader) o).setIsPublic("1");

                }
                break;
            case ExportConstant.VARIABLE_GROUP:
                if (o instanceof VariableGroup) {
                    ((VariableGroup) o).setVariableGroupId(groupId);
                }
                break;
            case ExportConstant.VARIABLE:
                if (o instanceof Variable) {
                    ((Variable) o).setVariableGroupId(groupId);
//                    ((Variable) o).setIsPublic("1");
                }
                break;
            case ExportConstant.KPI_GROUP:
                if (o instanceof KpiGroup) {
                    ((KpiGroup) o).setKpiGroupId(groupId);
                }
                break;
            case ExportConstant.KPI:
                if (o instanceof KpiDefinition) {
                    ((KpiDefinition) o).setKpiGroupId(groupId);
                }
                break;
            case ExportConstant.API_GROUP:
                if (o instanceof ApiGroup) {
                    ((ApiGroup) o).setApiGroupId(groupId);
                }
                break;
            case ExportConstant.API:
                if (o instanceof ApiConf) {
                    ((ApiConf) o).setApiGroupId(groupId);
//                    ((ApiConf) o).setIsPublic("1");
                }
                break;
        }
    }

    public static String getObjectGroupId(String type, Object o) {
        if (StringUtils.isBlank(type) || o == null) {
            return null;
        }
        String id = null;
        switch (type) {
            case ExportConstant.MODEL_GROUP:
                if (o instanceof ModelGroup) {
                    id = ((ModelGroup) o).getModelGroupId();
                }
                break;

            case ExportConstant.MODEL_HEADER:
                if (o instanceof RuleDetailHeader) {
                    id = ((RuleDetailHeader) o).getModelGroupId();
                }
                break;
            case ExportConstant.MODEL_VERSION:
                if (o instanceof RuleDetailWithBLOBs) {
                    id = ((RuleDetailWithBLOBs) o).getModelGroupId();
                }
                break;
            case ExportConstant.VARIABLE_GROUP:
                if (o instanceof VariableGroup) {
                    id = ((VariableGroup) o).getVariableGroupId();
                }
                break;
            case ExportConstant.VARIABLE:
                if (o instanceof Variable) {
                    id = ((Variable) o).getVariableGroupId();
                }
                break;
            case ExportConstant.KPI_GROUP:
                if (o instanceof KpiGroup) {
                    id = ((KpiGroup) o).getKpiGroupId();
                }
                break;
            case ExportConstant.KPI:
                if (o instanceof KpiDefinition) {
                    id = ((KpiDefinition) o).getKpiGroupId();
                }
                break;
            case ExportConstant.API_GROUP:
                if (o instanceof ApiGroup) {
                    id = ((ApiGroup) o).getApiGroupId();
                }
                break;
            case ExportConstant.API:
                if (o instanceof ApiConf) {
                    id = ((ApiConf) o).getApiGroupId();
                }
                break;
            case ExportConstant.DB:
                if (o instanceof DataSource) {
                    id = ((DataSource) o).getDbId();
                }
                break;
            case ExportConstant.PUB_DB_TABLE:
            case ExportConstant.PRI_DB_TABLE:
                if (o instanceof MetaDataTable) {
                    id = ((MetaDataTable) o).getDbId();
                }
                break;

        }
        return id;
    }

    public static String getGroupType(String type) {
        String result = null;
        switch (type) {
            case ExportConstant.MODEL_GROUP:
                result = ExportConstant.MODEL_GROUP;
                break;
            case ExportConstant.MODEL_HEADER:
                result = ExportConstant.MODEL_GROUP;
                break;
            case ExportConstant.MODEL_VERSION:
                result = ExportConstant.MODEL_GROUP;
                break;
            case ExportConstant.VARIABLE_GROUP:
                result = ExportConstant.VARIABLE_GROUP;
                break;
            case ExportConstant.VARIABLE:
                result = ExportConstant.VARIABLE_GROUP;
                break;
            case ExportConstant.KPI_GROUP:
                result = ExportConstant.KPI_GROUP;
                break;
            case ExportConstant.KPI:
                result = ExportConstant.KPI_GROUP;
                break;
            case ExportConstant.API_GROUP:
                result = ExportConstant.API_GROUP;
                break;
            case ExportConstant.API:
                result = ExportConstant.API_GROUP;
                break;
            case ExportConstant.DB:
                result = ExportConstant.DB;
                break;
            case ExportConstant.PUB_DB_TABLE:
                result = ExportConstant.DB;
                break;
            case ExportConstant.PRI_DB_TABLE:
                result = ExportConstant.DB;
                break;
//            case ExportConstant.DB_COLUNM:
//                result = ExportConstant.DB;
//                break;
        }
        return result;
    }

    public static BaseReport paseReport(String type, Object o) {
        if (StringUtils.isBlank(type) || o == null) {
            return null;
        }
        String id = null;
        BaseReport report = null;
        switch (type) {
            case ExportConstant.FOLDER:
                report = paseFolderReport(o);
                break;
            case ExportConstant.MODEL_GROUP:
                report = paseModelGroupReport(o);
                break;
            case ExportConstant.MODEL_HEADER:
                report = paseModelHeaderReport(o);
                break;
            case ExportConstant.MODEL_VERSION:
                report = paseModelVersionReport(o);
                break;
            case ExportConstant.VARIABLE_GROUP:
                report = paseVariableGroupReport(o);
                break;
            case ExportConstant.VARIABLE:
                report = paseVariableReport(o);
                break;
            case ExportConstant.KPI_GROUP:
                report = paseKpiGroupReport(o);
                break;
            case ExportConstant.KPI:
                report = paseKpiReport(o);
                break;
            case ExportConstant.API_GROUP:
                report = paseApiGroupReport(o);

                break;
            case ExportConstant.API:
                report = paseApiReport(o);

                break;
            case ExportConstant.DB:
                report = paseDBReport(o);

                break;
            case ExportConstant.PUB_DB_TABLE:
            case ExportConstant.PRI_DB_TABLE:
                report = paseDBTableReport(o);
                break;
            case ExportConstant.DB_COLUNM:
                report = paseDBColumnReport(o);

                break;
        }
        return report;
    }

    /**
     * 将id通过类型返回一个对象
     *
     * @param type
     * @param id
     * @return
     */
    public static Object paseObjectIdToObject(String type, String id) {
        if (StringUtils.isBlank(type)) {
            return null;
        }

        switch (type) {
            case ExportConstant.VARIABLE_GROUP:
                VariableGroup variableGroup = new VariableGroup();
                variableGroup.setVariableGroupId(id);
                return variableGroup;
            case ExportConstant.VARIABLE:
                VariableTreeNode variable = new VariableTreeNode();
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
        }
        return null;
    }

    public static VariableTreeNode paseVariableCodeToVariable(String variableCode) {
        if (StringUtils.isBlank(variableCode)) {
            return null;
        }
        VariableTreeNode variable = new VariableTreeNode();
//        variable.setVariableId(variableCode);
        variable.setVariableCode(variableCode);
        return variable;
    }


    public static FolderReport paseFolderReport(Object o) {
        if (o != null) {
            if (o instanceof RuleFolder) {
                RuleFolder folder = (RuleFolder) o;
                String folderId = folder.getFolderId();
                FolderReport folderReport = new FolderReport();
                folderReport.setFolderId(folderId);
                folderReport.setFolderName(folder.getFolderName());
                folderReport.setFolderDesc(folder.getFolderDesc());

            }
        }
        return null;
    }

    public static ModelGroupReport paseModelGroupReport(Object o) {
        if (o != null && o instanceof ModelGroup) {
            ModelGroup modelGroup = (ModelGroup) o;
            String modelGroupId = modelGroup.getModelGroupId();
            ModelGroupReport modelGroupReport = new ModelGroupReport();
            modelGroupReport.setModelGroupId(modelGroupId);
            modelGroupReport.setModelGroupName(modelGroup.getModelGroupName());
            return modelGroupReport;

        }
        return null;
    }

    public static ModelReport paseModelHeaderReport(Object o) {
        if (o != null && o instanceof RuleDetailHeader) {
            RuleDetailHeader ruleDetailWithBLOBs = (RuleDetailHeader) o;
            String modelId = ruleDetailWithBLOBs.getRuleName();
            ModelReport modelReport = new ModelReport();
            modelReport.setRuleName(ruleDetailWithBLOBs.getRuleName());
            modelReport.setModelName(modelId);
            return modelReport;

        }
        return null;
    }

    public static ModelVersionReport paseModelVersionReport(Object o) {
        if (o != null && o instanceof RuleDetailWithBLOBs) {
            RuleDetailWithBLOBs ruleDetailWithBLOBs = (RuleDetailWithBLOBs) o;
            String ruleId = ruleDetailWithBLOBs.getRuleId();
            ModelVersionReport modelVersionReport = new ModelVersionReport();
//                modelVersionReport.setVERSION(ruleDetailWithBLOBs.getVersion());
            modelVersionReport.setVersionId(ruleId);
            return modelVersionReport;

        }
        return null;
    }

    public static VariableGroupReport paseVariableGroupReport(Object o) {
        if (o != null && o instanceof VariableGroup) {
            VariableGroup variableGroup = (VariableGroup) o;
            String variableGroupId = variableGroup.getVariableGroupId();
            VariableGroupReport variableGroupReport = new VariableGroupReport();
            variableGroupReport.setVariableGroupId(variableGroupId);
            variableGroupReport.setVariableGroupName(variableGroup.getVariableGroupName());
            return variableGroupReport;

        }
        return null;
    }

    public static VariableReport paseVariableReport(Object o) {
        if (o != null && o instanceof Variable) {
            Variable variable = (Variable) o;
            String variableId = variable.getVariableId();
            VariableReport variableReport = new VariableReport();
            variableReport.setVariableId(variableId);
            variableReport.setVariableAlias(variable.getVariableAlias());
            variableReport.setVariableCode(variable.getVariableCode());
            return variableReport;

        }
        return null;
    }


    public static KpiGroupReport paseKpiGroupReport(Object o) {
        if (o != null && o instanceof KpiGroup) {
            KpiGroup kpiGroup = (KpiGroup) o;
            String kpiGroupId = kpiGroup.getKpiGroupId();
            KpiGroupReport kpiGroupReport = new KpiGroupReport();
            kpiGroupReport.setKpiGroupId(kpiGroupId);
            kpiGroupReport.setKpiGroupName(kpiGroup.getKpiGroupName());
            kpiGroupReport.setKpiGroupDesc(kpiGroup.getKpiGroupDesc());
            return kpiGroupReport;

        }
        return null;
    }

    public static KpiReport paseKpiReport(Object o) {
        if (o != null && o instanceof KpiDefinition) {
            KpiDefinition kpiDefinition = (KpiDefinition) o;
            String kpiId = kpiDefinition.getKpiId();
            KpiReport kpiReport = new KpiReport();
            kpiReport.setKpiId(kpiId);
            kpiReport.setKpiDesc(kpiDefinition.getKpiDesc());
            kpiReport.setKpiName(kpiDefinition.getKpiName());
            return kpiReport;

        }
        return null;
    }

    public static ApiGroupReport paseApiGroupReport(Object o) {
        if (o != null && o instanceof ApiGroup) {
            ApiGroup apiGroup = (ApiGroup) o;
            String apiGroupId = apiGroup.getApiGroupId();
            ApiGroupReport apiGroupReport = new ApiGroupReport();
            apiGroupReport.setApiGroupId(apiGroupId);
            apiGroupReport.setApiGroupName(apiGroup.getApiGroupName());
            return apiGroupReport;

        }
        return null;
    }

    public static ApiReport paseApiReport(Object o) {
        if (o != null && o instanceof ApiConf) {
            ApiConf apiConf = (ApiConf) o;
            String apiId = apiConf.getApiId();

            ApiReport apiReport = new ApiReport();
            apiReport.setApiId(apiId);
            apiReport.setApiDesc(apiConf.getApiDesc());
            apiReport.setApiName(apiConf.getApiName());
            return apiReport;

        }
        return null;
    }

    public static DBReport paseDBReport(Object o) {
        if (o != null && o instanceof DataSource) {
            DataSource dataSource = (DataSource) o;
            String dbId = dataSource.getDbId();
            DBReport dbReport = new DBReport();
            dbReport.setDbId(dbId);
            dbReport.setDbAlias(dataSource.getDbAlias());
            return dbReport;

        }
        return null;
    }

    public static DBTableReport paseDBTableReport(Object o) {
        if (o != null && o instanceof MetaDataTable) {
            MetaDataTable dataTable = (MetaDataTable) o;
            String tableId = dataTable.getTableId();
            DBTableReport dbTableReport = new DBTableReport();
            dbTableReport.setTableId(tableId);
            dbTableReport.setTableName(dataTable.getTableName());
            dbTableReport.setTableCode(dataTable.getTableCode());
            return dbTableReport;

        }
        return null;
    }

    public static DBColumnReport paseDBColumnReport(Object o) {
        if (o != null && o instanceof MetaDataColumn) {
            MetaDataColumn metaDataColumn = (MetaDataColumn) o;
            String columnId = metaDataColumn.getColumnId();
            DBColumnReport dbColumnReport = new DBColumnReport();
            dbColumnReport.setColumnId(columnId);
            dbColumnReport.setColumnName(metaDataColumn.getColumnName());
            dbColumnReport.setColumnCode(metaDataColumn.getColumnCode());
            return dbColumnReport;

        }
        return null;
    }


    public static String getSpacing(int size) {
        StringBuilder result = new StringBuilder();
        if (size > 0) {
            for (; size >= 0; size--) {
                result.append(" ");
            }
        }
        return result.toString();
    }

    public ObjectImportReport paseObjectImportReport(String type, ImportAdjustObject object) throws Exception {
        if (StringUtils.isBlank(type) || object == null) {
            throw new Exception("将调整后的对象转回为导入报告对象失败:type:" + type + ",对象:" + object);
        }
        ObjectImportReport result = new ObjectImportReport();
        result.setType(type);
        result.setImportSuccessType(object.getImportSuccessType());
        Object toData = object.getToData();
        Object fromData = object.getFromData();
        result.setFromData(ExportUtil.paseImportObjectBaseInfo(type, fromData));
        result.setToData(ExportUtil.paseImportObjectBaseInfo(type, toData));
        return result;

    }

    public static ImportObjectBaseInfo paseImportObjectBaseInfo(String type, Object object) {
        if (StringUtils.isBlank(type) || object == null) {
            return null;
        }
        String objectId = getObjectId(type, object);
        String objectName = getObjectName(type, object);
        String objectCode = getObjectCode(type, object);
        String objectIsPublic = getObjectIsPublic(type, object);
        return new ImportObjectBaseInfo(type, objectId, objectName, objectCode, objectIsPublic);
    }


//    public static List<Variable> getVariableFromApi(ExportResult result, ApiConf apiConf) {
//        if (apiConf == null) {
//            return Collections.emptyList();
//        }
//        if (result == null) {
//            result = new ExportResult();
//        }
//        List<VariableTreeNode> apiReturnVariableTree = variableService.getApiReturnVariableTree(apiConf.getApiId(), null);
//    }

    public static List<String> getVarIdFromApiList(ExportResult result, List<ApiConf> apiList) {
        if (apiList == null || apiList.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> results = new HashSet<>();
        for (ApiConf apiConf : apiList) {
            Set<String> varIdFromApi = getVarIdFromApi(result, apiConf);
            results.addAll(varIdFromApi);
        }
//        Set<String> varName = getVarNameFromApiList(apiList);
//        List<String> varIds = variableService.selectVariableByVariableCodeList(new ArrayList<String>(varName));
//        if (!CollectionUtil.isEmpty(varIds)) {
//            results.addAll(varIds);
//        }
        return new ArrayList<>(results);
    }

//    public static Set<String> getVarNameFromApiList(ExportResult result, List<ApiConf> apiList) {
//        if (apiList == null || apiList.isEmpty()) {
//            return Collections.emptySet();
//        }
//        Set<String> results = new HashSet<>();
//        for (ApiConf api : apiList) {
//            Set<String> varNameFromApi = getVarNameFromApi(result, api);
//            if (varNameFromApi != null && !varNameFromApi.isEmpty()) {
//                results.addAll(varNameFromApi);
//            }
//        }
//        results.remove(null);
//        results.remove("");
//        return results;
//    }

    public static Set<String> getVarIdFromApi(ExportResult result, ApiConf api) {
        Set<String> variableIdSet = new HashSet<>();
        if (api == null) {
            return variableIdSet;
        }
        String apiContent = api.getApiContent();
        if (!StringUtils.isBlank(apiContent)) {
            JSONObject apiContentJSON = JSONObject.parseObject(apiContent);
            if (apiContentJSON != null && !apiContentJSON.isEmpty()) {
                String returnValueId = apiContentJSON.getString("newReturnParamId");
                if (!StringUtils.isBlank(returnValueId)) {
                    variableIdSet.add(returnValueId);
                }
//                variableService.selectVariableByVariableIdList()
                JSONArray newParamId = apiContentJSON.getJSONArray("newParamId");
                if (newParamId != null && !newParamId.isEmpty()) {
                    List<String> paramIdList = newParamId.toJavaList(String.class);
                    if (paramIdList != null && !paramIdList.isEmpty()) {
                        variableIdSet.addAll(paramIdList);
                    }
                }
            }
        }
        variableIdSet.remove(null);
        variableIdSet.remove("");
        if (result != null) {
            result.putDataIdBatch(ExportConstant.VARIABLE, new ArrayList<Object>(variableIdSet), ExportConstant.API, api);
        }
        return variableIdSet;
    }

    //  版本v1.0的从接口中获取参数Id
//    public static Set<String> getVarIdFromApi(ExportResult result, ApiConf api) {
//        Set<String> variableIdSet = new HashSet<>();
//        Set<String> variableNameSet = new HashSet<>();
//        if (api == null) {
//            return variableNameSet;
//        }
//        String apiContent = api.getApiContent();
//        if (!StringUtils.isBlank(apiContent)) {
//
//            JSONObject apiContentJSON = JSONObject.parseObject(apiContent);
//            if (apiContentJSON != null && !apiContentJSON.isEmpty()) {
//                String returnValue = apiContentJSON.getString("returnValue");
//                if (!StringUtils.isBlank(returnValue)) {
//                    variableNameSet.add(returnValue);
//                }
//                List<String> basicTypeVariableNames = new ArrayList<>();
//                List<String> entityTypeVariableNames = new ArrayList<>();
//                // 1:基本类型   2:实体类型
//                if ("1".equals(apiContentJSON.getString("returnValueType"))) {
//                    basicTypeVariableNames.add(returnValue);
//                } else if ("2".equals(apiContentJSON.getString("returnValueType"))) {
//                    entityTypeVariableNames.add(returnValue);
//                }
////                variableService.selectVariableByVariableIdList()
//                List<String> apiNames = apiContentJSON.getJSONArray("param").toJavaList(String.class);
//                if (apiNames != null && !apiNames.isEmpty()) {
//                    basicTypeVariableNames.addAll(apiNames);
////                    variableNameSet.addAll(apiNames);
//                }
//                List<Variable> baseTypeVariables = variableService.selectVariableByVariableCodeList(basicTypeVariableNames, api.getFolderId(), paseApiVarTypeToVariableType("1"));
//                List<Variable> entityTypeVariables = variableService.selectVariableByVariableCodeList(entityTypeVariableNames, api.getFolderId(), paseApiVarTypeToVariableType("2"));
//                for (Variable variable : baseTypeVariables) {
//                    if (variable != null) {
//                        variableNameSet.remove(variable.getVariableCode());
//                        variableIdSet.add(variable.getVariableId());
//                    }
//                }
//                for (Variable variable : entityTypeVariables) {
//                    if (variable != null) {
//                        variableNameSet.remove(variable.getVariableCode());
//                        variableIdSet.add(variable.getVariableId());
//                    }
//                }
//                if (variableNameSet != null && !variableNameSet.isEmpty()) {
//                    List<Variable> reSeltctVariables = variableService.selectVariableByVariableCodeList(basicTypeVariableNames, api.getFolderId(), null);
//                    if (!CollectionUtil.isEmpty(reSeltctVariables)) {
//                        for (Variable variable : reSeltctVariables) {
//                            if (variable != null) {
//                                variableNameSet.remove(variable.getVariableCode());
//                                variableIdSet.add(variable.getVariableId());
//                            }
//                        }
//                    }
//                }
//                if (variableNameSet != null && !variableNameSet.isEmpty()) {
//                    for (String variableName : variableIdSet) {
//                        Variable variable = ExportUtil.paseVariableCodeToVariable(variableName);
//                        result.putlackData(ExportConstant.VARIABLE, variable, ExportConstant.API, api);
//                    }
//                }
//            }
//        }
//        variableIdSet.remove(null);
//        variableIdSet.remove("");
//        return variableIdSet;
//    }

    private static List<String> paseApiVarTypeToVariableType(String apiVarType) {
        if (StringUtils.isBlank(apiVarType)) {
            return null;
        }
        List<String> result = new ArrayList<>();
        if ("1".equals(apiVarType)) {
            result.add("2");
            result.add("1");
            result.add("4");
            result.add("6");
            result.add("7");
            result.add("8");
            result.add("3");
            result.add("5");
        } else if ("2".equals(apiVarType)) {
            result.add("3");
        }
        return result;
    }

    public static Set<String> getVarIdFromKpiList(ExportResult result, List<KpiDefinition> kpiDefinitionList) {
        if (CollectionUtil.isEmpty(kpiDefinitionList)) {
            return Collections.emptySet();
        }
        Set<String> results = new HashSet<>();
        for (KpiDefinition kpiDefinition : kpiDefinitionList) {
            if (kpiDefinition == null) {
                continue;
            }
            //接口中的取值参数
            if ("1".equals(kpiDefinition.getFetchType()) && StringUtils.isNotBlank(kpiDefinition.getApiId())) {
                String kpiValueSource = kpiDefinition.getKpiValueSource();
                if (!StringUtils.isBlank(kpiValueSource)) {
                    results.add(kpiValueSource);
                    if (result != null)
                        result.putDataId(ExportConstant.VARIABLE, kpiValueSource, ExportConstant.KPI, kpiDefinition);
                }
            }
            List<KpiFetchLimiters> kpiFetchLimitersList = kpiDefinition.getKpiFetchLimitersList();
            if (CollectionUtil.isEmpty(kpiFetchLimitersList)) {
                continue;
            }
            for (KpiFetchLimiters kpiFetchLimiters : kpiFetchLimitersList) {
                if (kpiFetchLimiters == null) {
                    continue;
                }
                String variableId = kpiFetchLimiters.getVariableId();
                if (!StringUtils.isBlank(variableId)) {
                    results.add(variableId);
                    if (result != null)
                        result.putDataId(ExportConstant.VARIABLE, variableId, ExportConstant.KPI, kpiDefinition);

                }
            }
        }
        return results;
    }

    public static Set<String> getApiIdFromKpiList(ExportResult result, List<KpiDefinition> kpiDefinitionList) {
        if (CollectionUtil.isEmpty(kpiDefinitionList)) {
            return Collections.emptySet();
        }
        Set<String> apiids = new HashSet<>();
        for (KpiDefinition kpiDefinition : kpiDefinitionList) {
            if (kpiDefinition == null) {
                continue;
            }
            String apiId = kpiDefinition.getApiId();
            if (!StringUtils.isBlank(apiId)) {
                apiids.add(apiId);
                if (result != null)
                    result.putDataId(ExportConstant.API, apiId, ExportConstant.KPI, kpiDefinition);
            }
        }
        return apiids;
    }


    public static Set<String> getDbIdsFromKpiList(ExportResult result, List<KpiDefinition> kpiDefinitionList) {
        if (CollectionUtil.isEmpty(kpiDefinitionList)) {
            return Collections.emptySet();
        }
        Set<String> results = new HashSet<>();
        for (KpiDefinition kpiDefinition : kpiDefinitionList) {
            if (kpiDefinition == null || !"0".equals(kpiDefinition.getFetchType())) {
                continue;
            }
            String dbId = kpiDefinition.getDbId();
            if (!StringUtils.isBlank(dbId)) {
                results.add(dbId);
                if (result != null) {
                    result.putDataId(ExportConstant.DB, dbId, ExportConstant.KPI, kpiDefinition);
                }
            }
        }
        return results;
    }


    public static Set<String> getPubTableIdsFromKpiList(ExportResult
                                                                result, List<KpiDefinition> kpiDefinitionList) {
        if (CollectionUtil.isEmpty(kpiDefinitionList)) {
            return Collections.emptySet();
        }
        Set<String> results = new HashSet<>();
        for (KpiDefinition kpiDefinition : kpiDefinitionList) {
            if (kpiDefinition == null || !"0".equals(kpiDefinition.getFetchType())) {
                continue;
            }
            String tableId = kpiDefinition.getTableId();
            if (!StringUtils.isBlank(tableId)) {
                results.add(tableId);
                if (result != null) {
                    result.putDataId(ExportConstant.PUB_DB_TABLE, tableId, ExportConstant.KPI, kpiDefinition);
                }
            }
        }
        return results;
    }


    public static Set<String> getColumnIdsFromKpiList(ExportResult result, List<KpiDefinition> kpiDefinitionList) {
        if (CollectionUtil.isEmpty(kpiDefinitionList)) {
            return Collections.emptySet();
        }
        Set<String> results = new HashSet<>();
        for (KpiDefinition kpiDefinition : kpiDefinitionList) {
            if (kpiDefinition == null || !"0".equals(kpiDefinition.getFetchType())) {
                continue;
            }
            // 取值字段
            String kpiValueSource = kpiDefinition.getKpiValueSource();
            if (!StringUtils.isBlank(kpiValueSource)) {
                results.add(kpiValueSource);
                if (result != null)
                    result.putDataId(ExportConstant.DB_COLUNM, kpiValueSource, ExportConstant.KPI, kpiDefinition);
            }
            List<KpiFetchLimiters> kpiFetchLimitersList = kpiDefinition.getKpiFetchLimitersList();
            if (CollectionUtil.isEmpty(kpiFetchLimitersList)) {
                continue;
            }
            for (KpiFetchLimiters kpiFetchLimiters : kpiFetchLimitersList) {
                if (kpiFetchLimiters == null) {
                    continue;
                }
                String columnId = kpiFetchLimiters.getColumnId();
                if (!StringUtils.isBlank(columnId)) {
                    results.add(columnId);
                    if (result != null)
                        result.putDataId(ExportConstant.DB_COLUNM, columnId, ExportConstant.KPI, kpiDefinition);
                }
            }
        }
        return results;
    }

    public static Set<String> getVarIdFromKpi(ExportResult result, KpiDefinition kpiDefinition) {

        Set<String> results = new HashSet<>();
        if (kpiDefinition != null) {
            if ("1".equals(kpiDefinition.getFetchType()) && StringUtils.isNotBlank(kpiDefinition.getApiId())) {
                String kpiValueSource = kpiDefinition.getKpiValueSource();
                results.add(kpiValueSource);
                if (result != null)
                    result.putDataId(ExportConstant.VARIABLE, kpiValueSource, ExportConstant.KPI, kpiDefinition);

            }
            List<KpiFetchLimiters> kpiFetchLimitersList = kpiDefinition.getKpiFetchLimitersList();
            if (!CollectionUtil.isEmpty(kpiFetchLimitersList)) {

                for (KpiFetchLimiters kpiFetchLimiters : kpiFetchLimitersList) {
                    if (kpiFetchLimiters == null) {
                        continue;
                    }
                    String variableId = kpiFetchLimiters.getVariableId();
                    if (!StringUtils.isBlank(variableId)) {
                        results.add(variableId);
                        if (result != null)
                            result.putDataId(ExportConstant.VARIABLE, variableId, ExportConstant.KPI, kpiDefinition);

                    }
                }
            }
        }
        return results;
    }

    public static String getApiIdFromKpi(ExportResult result, KpiDefinition kpiDefinition) {

        String apiids = null;
        if (kpiDefinition != null) {
            String apiId = kpiDefinition.getApiId();
            if (!StringUtils.isBlank(apiId)) {
                apiids = apiId;
                if (result != null) {
                    result.putDataId(ExportConstant.API, apiId, ExportConstant.KPI, kpiDefinition);
                }
            }
        }
        return apiids;
    }

    public static String getDbIdsFromKpi(ExportResult result, KpiDefinition kpiDefinition) {

        String results = null;
        if (kpiDefinition == null || !"0".equals(kpiDefinition.getFetchType())) {
            return results;
        }
        String dbId = kpiDefinition.getDbId();
        if (!StringUtils.isBlank(dbId)) {
            results = dbId;
            if (result != null) {
                result.putDataId(ExportConstant.DB, dbId, ExportConstant.KPI, kpiDefinition);
            }
        }
        return results;
    }

    public static String getPubTableIdsFromKpi(ExportResult result, KpiDefinition kpiDefinition) {

        String results = null;
        if (kpiDefinition == null || !"0".equals(kpiDefinition.getFetchType())) {
            return results;
        }
        String tableId = kpiDefinition.getTableId();
        if (!StringUtils.isBlank(tableId)) {
            results = tableId;
            if (result != null) {
                result.putDataId(ExportConstant.PUB_DB_TABLE, tableId, ExportConstant.KPI, kpiDefinition);
            }
        }
        return results;
    }

    public static Set<String> getColumnIdsFromKpi(ExportResult result, KpiDefinition kpiDefinition) {
        Set<String> results = new HashSet<>();
        if (kpiDefinition != null && "0".equals(kpiDefinition.getFetchType())) {
            String kpiValueSource = kpiDefinition.getKpiValueSource();
            if (!StringUtils.isBlank(kpiValueSource)) {
                results.add(kpiValueSource);
                if (result != null)
                    result.putDataId(ExportConstant.DB_COLUNM, kpiValueSource, ExportConstant.KPI, kpiDefinition);
            }
            List<KpiFetchLimiters> kpiFetchLimitersList = kpiDefinition.getKpiFetchLimitersList();
            if (!CollectionUtil.isEmpty(kpiFetchLimitersList)) {
                for (KpiFetchLimiters kpiFetchLimiters : kpiFetchLimitersList) {
                    if (kpiFetchLimiters == null) {
                        continue;
                    }
                    String columnId = kpiFetchLimiters.getColumnId();
                    if (!StringUtils.isBlank(columnId)) {
                        results.add(columnId);
                        if (result != null)
                            result.putDataId(ExportConstant.DB_COLUNM, columnId, ExportConstant.KPI, kpiDefinition);
                    }
                }
            }
        }
        return results;
    }

}
