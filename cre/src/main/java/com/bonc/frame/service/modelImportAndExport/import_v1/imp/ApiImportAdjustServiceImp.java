package com.bonc.frame.service.modelImportAndExport.import_v1.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.modelImportAndExport.ImportAndExportOperateLog;
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
public class ApiImportAdjustServiceImp extends AbstractImportAdjustServiceImp {
    @Override
    public ModelImportType getSupport() {
        return ModelImportType.API;
    }

    @Override
    public String updateReference(String type, ImportAdjustObject o, ImportParam importParam, ImportContext context) throws Exception {
        String success = "0";
        ApiConf apiConf = (ApiConf) o.getToData();
        String updateApiFolderOrGroupSuccess = updateObjectFolderOrGroup(type, o, ExportConstant.API_GROUP, importParam, context);
        if ("-1".equals(updateApiFolderOrGroupSuccess)) {
            success = "-1";
        }
        String updateApiRefVariableSuccess = updateApiRefVariable(apiConf, importParam, context);
        if ("-1".equals(updateApiRefVariableSuccess)) {
            success = "-1";
        }
        return success;
    }

    private String updateApiRefVariable(ApiConf api, ImportParam importParam, ImportContext context) throws Exception {
        String success = "0";
        if (api == null) {
            throw new Exception("修改接口的引用失败,接口内容为null");
        }
        String systemVersion = "2.0";
        ImportAndExportOperateLog info = context.getImportFileObject().getInfo();
        if (info != null && StringUtils.isNotBlank(info.getSystemVersion())) {
            systemVersion = info.getSystemVersion();
        }
        String apiRefVariableSuccess = null;
        if ("v1.0".equals(systemVersion)) {
            apiRefVariableSuccess = updateApiRefVariable_V1(api, importParam, context);
        } else {
            apiRefVariableSuccess = updateApiRefVariable_V2(api, importParam, context);
        }
        if ("-1".equals(apiRefVariableSuccess)) {
            success = "-1";
        }
        return success;
    }

    private String updateApiRefVariable_V1(ApiConf api, ImportParam importParam, ImportContext context) throws Exception {
        String success = "0";
        if (api == null) {
            throw new Exception("修改接口的引用失败,接口内容为null");
        }
        String apiContent = api.getApiContent();
        String returnValue;
        String newReturnParamId;
        List<String> newParamId = new ArrayList<>();
        List<String> param;
        List<String> toDataParam;
        List<Object> newParam = new ArrayList<>();
        if (!StringUtils.isBlank(apiContent)) {
            JSONObject apiContentJSON = JSONObject.parseObject(apiContent);
            if (apiContentJSON != null && !apiContentJSON.isEmpty()) {
                returnValue = apiContentJSON.getString("returnValue");
                if (!StringUtils.isBlank(returnValue)) {
                    Variable returnValueVariable = getVariableInContextByVariableCode(ExportConstant.API, api, returnValue, importParam, context);
                    if (returnValueVariable == null) {
                        success = "-1";
                    } else {
                        returnValue = returnValueVariable.getVariableCode();
                        newReturnParamId = returnValueVariable.getVariableId();
                        apiContentJSON.put("returnValue", returnValue);
                        apiContentJSON.put("newReturnParamId", newReturnParamId);
                    }
                }
                param = apiContentJSON.getJSONArray("param").toJavaList(String.class);
                if (param != null && !param.isEmpty()) {
                    toDataParam = new ArrayList<>(param);
                    for (String variableCode : param) {
                        if (!StringUtils.isBlank(variableCode)) {
                            Variable newVariable = getVariableInContextByVariableCode(ExportConstant.API, api, variableCode, importParam, context);
                            if (newVariable == null) {
                                success = "-1";
                                continue;
                            }
                            String newVariableCode = newVariable.getVariableCode();
                            String newVariableId = newVariable.getVariableId();
                            toDataParam.remove(variableCode);
                            toDataParam.add(newVariableCode);
                            newParam.add(newVariable);
                            newParamId.add(newVariableId);
                        }
                    }
                    apiContentJSON.put("newParamId", JSONObject.toJSONString(newParamId));
                    apiContentJSON.put("newParam", newParam);
                    apiContentJSON.put("param", toDataParam);
                }
                api.setApiContent(apiContentJSON.toJSONString());
            }
        }
        return success;
    }

    private String updateApiRefVariable_V2(ApiConf api, ImportParam importParam, ImportContext context) throws Exception {
        String success = "0";
        if (api == null) {
            throw new Exception("修改接口的引用失败,接口内容为null");
        }
        String apiContent = api.getApiContent();
        String returnValue;
        String newReturnParamId;
        List<String> toDataNewParamId;
        List<String> param;
        List<Object> newParam = new ArrayList<>();
        if (!StringUtils.isBlank(apiContent)) {
            JSONObject apiContentJSON = JSONObject.parseObject(apiContent);
            if (apiContentJSON != null && !apiContentJSON.isEmpty()) {
                returnValue = apiContentJSON.getString("returnValue");
                newReturnParamId = apiContentJSON.getString("newReturnParamId");
                if (!StringUtils.isBlank(newReturnParamId)) {
                    ImportAdjustObject successData = getSuccessDataFromContext(ExportConstant.VARIABLE, newReturnParamId, ExportConstant.API, api, importParam, context);
                    if (successData == null) {
                        success = "-1";
                    } else {
                        Variable fromData = (Variable) successData.getFromData();
                        Variable toData = (Variable) successData.getToData();
                        returnValue = toData.getVariableCode();
                        newReturnParamId = toData.getVariableId();
                        apiContentJSON.put("returnValue", returnValue);
                        apiContentJSON.put("newReturnParamId", newReturnParamId);

                    }
                }
                param = apiContentJSON.getJSONArray("param").toJavaList(String.class);
                String newParamIdString = apiContentJSON.getString("newParamId");
                List<String> newParamId = null;
                if (StringUtils.isNotBlank(newParamIdString)) {
                    newParamId = JSONArray.parseArray(newParamIdString, String.class);
                }

                if (newParamId != null && !newParamId.isEmpty()) {
                    toDataNewParamId = new ArrayList<>(newParamId);
                    for (String variableId : newParamId) {
                        if (!StringUtils.isBlank(variableId)) {
                            ImportAdjustObject successData = getSuccessDataFromContext(ExportConstant.VARIABLE, variableId, ExportConstant.API, api, importParam, context);
                            if (successData == null) {
                                success = "-1";
                            } else {
                                Variable fromData = (Variable) successData.getFromData();
                                Variable toData = (Variable) successData.getToData();
                                String fromDataCode = fromData.getVariableCode();
                                String toDataId = toData.getVariableId();
                                String toDataCode = toData.getVariableCode();
                                param.remove(fromDataCode);
                                param.add(toDataCode);
                                toDataNewParamId.remove(variableId);
                                toDataNewParamId.add(toDataId);
                                newParam.add(toData);
                            }
                        }
                    }
                    apiContentJSON.put("newParamId", JSONObject.toJSONString(toDataNewParamId));
                    apiContentJSON.put("newParam", newParam);
                    apiContentJSON.put("param", param);
                }
            }
        }
        return success;
    }

    @Override
    public String adjustById(String type, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        return null;
    }

    @Override
    public String adjustKey(String type, ImportAdjustObject importAdjustObject, String
            toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        String success = null;
        success = adjustApiByApiContent(type, ExportConstant.API_URL, true, importAdjustObject, null, toFolderId, importParam, context);
        // 将名称 Id, 放到缓存种
        ApiConf fromData = (ApiConf) importAdjustObject.getFromData();
        String fromDataId = null;
        if (fromData != null) {
            fromDataId = fromData.getApiId();
        }
        ApiConf toData = (ApiConf) importAdjustObject.getToData();
        String urlWithReturnAndParam = null;
        if (fromData != null) {
            urlWithReturnAndParam = toData.paseApiUrlWithReturnAndParam();
        }
        context.putneedSaveTmpIdAndName(ExportConstant.API_URL, urlWithReturnAndParam, type, fromDataId);
        return success;
    }

    @Override
    public String adjustOtherProperty(String type, ImportAdjustObject importAdjustObject, String
            toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        String suffix = null;
        String success = null;
        do {
            success = checkAdjustObjectProperty(type, ExportConstant.API_ID, false, false, importAdjustObject, suffix, toFolderId, importParam, context);
            suffix = getNextSuffix(suffix);
        } while (success == null || "-1".equals(success));
        suffix = null;
        success = null;
        do {
            success = checkAdjustObjectProperty(type, ExportConstant.API_NAME, false, true, importAdjustObject, suffix, toFolderId, importParam, context);
            suffix = getNextSuffix(suffix);
        } while (success == null || "-1".equals(success));

        return importAdjustObject.getSuccess();
    }

    @Override
    public List<Object> updatePropertyAndgetDbObjectList(String type, ImportIdOrNameCacheObject importIdOrNameObject, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        String idOrNameType = importIdOrNameObject.getIdOrNameType();
        String idOrNameValue = importIdOrNameObject.getIdOrName();
        String idOrNameKey = importIdOrNameObject.getIdOrNameKey();
        String suffix = importIdOrNameObject.getSuffix();
        List<Object> result = new ArrayList<>();

        List<ApiConf> dbApiList = null;
        ImportIdOrNameCacheObject cacheneedSaveIdAndName = null;
//        if (ExportConstant.API_URL.equals(idOrNameType)) {
//            if (!StringUtils.isBlank(url)) {
//                dbApiList = apiService.selectApiByProperty(null, null, url, "0", toFolderId);
//                if (dbApiList == null) {
//                    dbApiList = apiService.selectApiByProperty(null, null, url, "1", toFolderId);
//                }
//                String urlWithReturnAndParam = toData.paseApiUrlWithReturnAndParam();
//                cacheneedSaveIdAndName = context.getCacheNeedSaveIdAndName(idOrNameType, urlWithReturnAndParam);
//            }
//        } else
        if (ExportConstant.API_NAME.equals(idOrNameType)) {
            if (!StringUtils.isBlank(idOrNameValue)) {
                if (StringUtils.isNotBlank(suffix)) {
                    idOrNameValue = idOrNameValue + suffix;
                }
                idOrNameKey = toFolderId + idOrNameValue;
                importIdOrNameObject.setIdOrName(idOrNameValue);
                importIdOrNameObject.setIdOrNameKey(idOrNameKey);

                if (StringUtils.isNotBlank(toFolderId)) {
                    dbApiList = apiService.selectApiByProperty(null, idOrNameValue, null, "0", toFolderId);
                }
                List<ApiConf> apiConfs = apiService.selectApiByProperty(null, idOrNameValue, null, "1", null);
                if (CollectionUtil.isEmpty(dbApiList)) {
                    dbApiList = apiConfs;
                } else {
                    if (!CollectionUtil.isEmpty(apiConfs)) {
                        dbApiList.addAll(apiConfs);
                    }
                }
            }
        } else if (ExportConstant.API_ID.equals(idOrNameType)) {
            if (!StringUtils.isBlank(idOrNameValue)) {
                if (StringUtils.isNotBlank(suffix)) {
                    idOrNameValue = IdUtil.createId();
                }
                idOrNameKey = idOrNameValue;
                importIdOrNameObject.setIdOrName(idOrNameValue);
                importIdOrNameObject.setIdOrNameKey(idOrNameKey);

                dbApiList = apiService.selectApiByProperty(idOrNameValue, null, null, null, null);
            }
        } else {
            throw new Exception("参数调整参数时,传入的类型错误,类型:[" + type + "],属性类型:[" + idOrNameType + "],调整结果对象:[" + importAdjustObject + "]");
        }

        if (!CollectionUtil.isEmpty(dbApiList)) {
            result.addAll(dbApiList);
        }
        return result;
    }

    private String adjustApiByApiContent(String type, String idOrNameType, boolean isKey, ImportAdjustObject
            importAdjustObject, String suffix, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        if (importAdjustObject == null) {
            throw new Exception("通过参数code调整参数失败, 文件中的参数不存在,调整结果:[" + importAdjustObject + "]");
        }
        String success = null;
        ApiConf fromData = (ApiConf) importAdjustObject.getFromData();
        String fromDataId = fromData.getApiId();

        ApiConf toData = (ApiConf) importAdjustObject.getToData();
        String url = toData.paseApiUrl();

        // 通过获取数据库中的值, 获取缓存中的值
        List<ApiConf> dbApiList = null;
        ImportIdOrNameCacheObject cacheneedSaveIdAndName = null;
        if (ExportConstant.API_URL.equals(idOrNameType)) {
            if (!StringUtils.isBlank(url)) {
                if (StringUtils.isNotBlank(toFolderId)) {
                    dbApiList = apiService.selectApiByProperty(null, null, url, "0", toFolderId);
                }
                List<ApiConf> apiConfs = apiService.selectApiByProperty(null, null, url, "1", null);
                if (CollectionUtil.isEmpty(dbApiList)) {
                    dbApiList = apiConfs;
                } else {
                    if (!CollectionUtil.isEmpty(apiConfs)) {
                        dbApiList.addAll(apiConfs);
                    }
                }
                String urlWithReturnAndParam = toData.paseApiUrlWithReturnAndParam();
                cacheneedSaveIdAndName = context.getCacheNeedSaveIdAndName(idOrNameType, urlWithReturnAndParam);
            }
        } else {
            throw new Exception("参数调整参数时,传入的类型错误,类型:[" + type + "],属性类型:[" + idOrNameType + "],调整结果对象:[" + importAdjustObject + "]");
        }
        // 与数据库中获取的进行比较
        if (dbApiList != null && !dbApiList.isEmpty()) {
            if (isKey) {
                for (ApiConf dbApi : dbApiList) {
                    if (toData.equals(dbApi)) {
                        success = "1";
                        importAdjustObject.setToData(dbApi);
                        importAdjustObject.setImportSuccessType(ExportConstant.importSuccessType_useSystemData);
                        importAdjustObject.setSuccess(success);
                        return success;
                    }
                }
            }
        }
        // 与缓存中的值进行比较
        if (cacheneedSaveIdAndName != null) {
            String cacheFromObjectId = cacheneedSaveIdAndName.getFromObjectId();

            ImportAdjustObject cacheImportAdjustData = context.getSuccessData(type, cacheFromObjectId);
            if (cacheImportAdjustData != null && cacheImportAdjustData.getToData() != null) {
                Object cacheData = cacheImportAdjustData.getToData();
                //fromObjectId与 理论上这里不会相等
                if (fromDataId.equals(cacheFromObjectId)) {
                    success = "1";
                    importAdjustObject.setToData(cacheData);
                    importAdjustObject.setSuccess(success);
                    importAdjustObject.setImportSuccessType(ExportConstant.importSuccessType_useImportData);
                    return success;
                }
                if (isKey) {
                    String importSuccessType = cacheImportAdjustData.getImportSuccessType();
                    //TODO : 两个同样的参数, code不一样, 但是修改过之后一样了, 这样的话导入几个?   比如 v1和v2都是文件中的两个一样的参数   v1改名之后改成了v2 ,这样的话,导入几个参数?
                    if (toData.equals(cacheData)) {
                        success = "1";
                        if (ExportConstant.importSuccessType_useSystemData.equals(importSuccessType)) {
                            importAdjustObject.setToData(cacheData);
                            importAdjustObject.setImportSuccessType(ExportConstant.importSuccessType_useSystemData);
                        } else {
                            importAdjustObject.setToData(cacheData);
                            importAdjustObject.setImportSuccessType(ExportConstant.importSuccessType_useUpdateData);
                        }
                    } //两个参数比较结束
                }
                importAdjustObject.setSuccess(success);
            }
        } // 缓存中获取参数比较结束
        if (success == null || "0".equals(success)) {
            success = "0";
            importAdjustObject.setToData(toData);
            importAdjustObject.setSuccess(success);
            String importSuccessType = importAdjustObject.getImportSuccessType();
            if (StringUtils.isBlank(importSuccessType) || ExportConstant.importSuccessType_useImportData.equals(importSuccessType)) {
                if (StringUtils.isNotBlank(suffix)) {
                    importSuccessType = ExportConstant.importSuccessType_useUpdateData;
                } else {
                    importSuccessType = ExportConstant.importSuccessType_useImportData;
                }
                importAdjustObject.setImportSuccessType(importSuccessType);
            }
        }
        importAdjustObject.setSuccess(success);
        return success;
    }

//    private String adjustApi(String type, String idOrNameType, boolean isKey, ImportAdjustObject
//            importAdjustObject, String suffix, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
//        if (importAdjustObject == null) {
//            throw new Exception("通过参数code调整参数失败, 文件中的参数不存在,调整结果:[" + importAdjustObject + "]");
//        }
//        String success = null;
//        ApiConf fromData = (ApiConf) importAdjustObject.getFromData();
//        String fromDataId = fromData.getApiId();
//
//        ApiConf toData = (ApiConf) importAdjustObject.getToData();
//        String url = toData.paseApiUrl();
//        String toDataApiId = toData.getApiId();
//        String toDataApiName = toData.getApiName();
//        if (!StringUtils.isBlank(suffix)) {
//            toDataApiId = IdUtil.createId();
//            toDataApiName = toDataApiName + suffix;
//        }
//        String idOrNameValue = null;
//
//        // 通过获取数据库中的值, 获取缓存中的值
//        List<ApiConf> dbApiList = null;
//        ImportIdOrNameCacheObject cacheneedSaveIdAndName = null;
////        if (ExportConstant.API_URL.equals(idOrNameType)) {
////            if (!StringUtils.isBlank(url)) {
////                dbApiList = apiService.selectApiByProperty(null, null, url, "0", toFolderId);
////                if (dbApiList == null) {
////                    dbApiList = apiService.selectApiByProperty(null, null, url, "1", toFolderId);
////                }
////                String urlWithReturnAndParam = toData.paseApiUrlWithReturnAndParam();
////                cacheneedSaveIdAndName = context.getCacheNeedSaveIdAndName(idOrNameType, urlWithReturnAndParam);
////            }
////        } else
//        if (ExportConstant.API_NAME.equals(idOrNameType)) {
//            if (!StringUtils.isBlank(toDataApiName)) {
//                if (StringUtils.isNotBlank(toFolderId)) {
//                    dbApiList = apiService.selectApiByProperty(null, toDataApiName, null, "0", toFolderId);
//                }
//                List<ApiConf> apiConfs = apiService.selectApiByProperty(null, toDataApiName, null, "1", null);
//                if (CollectionUtil.isEmpty(dbApiList)) {
//                    dbApiList = apiConfs;
//                } else {
//                    if (!CollectionUtil.isEmpty(apiConfs)) {
//                        dbApiList.addAll(apiConfs);
//                    }
//                }
//                cacheneedSaveIdAndName = context.getCacheNeedSaveIdAndName(idOrNameType, toDataApiName);
//                idOrNameValue = toDataApiName;
//            }
//        } else if (ExportConstant.API_ID.equals(idOrNameType)) {
//            if (!StringUtils.isBlank(toDataApiId)) {
//                dbApiList = apiService.selectApiByProperty(toDataApiId, null, null, null, null);
//                cacheneedSaveIdAndName = context.getCacheNeedSaveIdAndName(idOrNameType, toDataApiId);
//                idOrNameValue = toDataApiId;
//            }
//        } else {
//            throw new Exception("参数调整参数时,传入的类型错误,类型:[" + type + "],属性类型:[" + idOrNameType + "],调整结果对象:[" + importAdjustObject + "]");
//        }
//        // 比较
//        success = compareObjectCacheObject(type, idOrNameType, idOrNameValue, isKey, importAdjustObject, dbApiList, cacheneedSaveIdAndName, importParam, context);
//        if ("1".equals(success)) {
//            return success;
//        }
//        if (success == null || "0".equals(success)) {
//            success = "0";
//            if (ExportConstant.API_NAME.equals(idOrNameType)) {
//                toData.setApiName(toDataApiName);
//            } else if (ExportConstant.API_ID.equals(idOrNameType)) {
//                toData.setApiId(toDataApiId);
//                suffix = null; // 这里设置为null 是为了在后面判断导入类型的时候需要 , id进行了改变,不影响他的导入类型
//            } else if (ExportConstant.API_URL.equals(idOrNameType)) {
//            } else {
//                throw new Exception("参数调整参数时,传入的类型错误,类型:[" + type + "],属性类型:[" + idOrNameType + "],调整结果对象:[" + importAdjustObject + "]");
//            }
//            importAdjustObject.setToData(toData);
//            importAdjustObject.setSuccess(success);
//            // 添加成功类型
//            String importSuccessType = importAdjustObject.getImportSuccessType();
//            if (StringUtils.isBlank(importSuccessType) || ExportConstant.importSuccessType_useImportData.equals(importSuccessType)) {
//                if (StringUtils.isNotBlank(suffix)) {
//                    importSuccessType = ExportConstant.importSuccessType_useUpdateData;
//                } else {
//                    importSuccessType = ExportConstant.importSuccessType_useImportData;
//                }
//                importAdjustObject.setImportSuccessType(importSuccessType);
//            }
//        }
//        importAdjustObject.setSuccess(success);
//        return success;
//    }

    @Override
    public void dataPersistenceObjectRef(String type, ImportAdjustObject o, ImportContext context) throws Exception {
        super.dataPersistenceObjectRef(type, o, context);
        ApiConf apiConf = (ApiConf) o.getFromData();
        Set<String> varIdFromApi = ExportUtil.getVarIdFromApi(null, apiConf);
        if (!CollectionUtil.isEmpty(varIdFromApi)) {
            for (String varId : varIdFromApi) {
                if (!StringUtils.isEmpty(varId)) {
                    ImportHelper.getInstance().saveObject(ExportConstant.VARIABLE, varId, null, context);
                }
            }
        }

    }

    @Override
    public void dataPersistenceObject(String type, Object o, ImportContext context) throws Exception {
        if ((getSupport().getValue()).equals(type)) {
            ApiConf kpiGroup = (ApiConf) o;
            apiService.insertApiDataPersistence(kpiGroup);
        }
    }

}
