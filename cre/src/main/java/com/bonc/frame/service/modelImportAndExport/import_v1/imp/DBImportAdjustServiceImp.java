package com.bonc.frame.service.modelImportAndExport.import_v1.imp;

import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelImport.ImportParam;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportIdOrNameCacheObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.importContext.ImportContext;
import com.bonc.frame.service.modelImportAndExport.import_v1.ModelImportType;
import com.bonc.frame.util.CollectionUtil;
import com.bonc.frame.util.ExportUtil;
import com.bonc.frame.util.IdUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/25 13:26
 */
@Service
public class DBImportAdjustServiceImp extends AbstractImportAdjustServiceImp {
    @Override
    public ModelImportType getSupport() {
        return ModelImportType.DB;
    }

//    @Override
//    public String updateReference(String type, ImportAdjustObject o, ImportParam importParam, ImportContext context) throws Exception {
//        String success = "0";
//        String updateApiFolderOrGroupSuccess = updateObjectFolderOrGroup(type, o, null, importParam, context);
//        if ("-1".equals(updateApiFolderOrGroupSuccess)) {
//            success = "-1";
//        }
//        return success;
//
//    }

    @Override
    public String adjustById(String type, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        String success = ImportAdjustObject.CONTINUE_SUCCESS_TYPE;
        Object fromData = importAdjustObject.getFromData();
        Object toData = importAdjustObject.getToData();
        String fromDataId = null;
        if (fromData != null) {
            fromDataId = ExportUtil.getObjectId(type, fromData);
        }
        List<DataSource> dbObjects = dataSourceService.selectDBByProperty(fromDataId, null, null);
        if (dbObjects != null && !dbObjects.isEmpty()) {
            for (DataSource dbObject : dbObjects) {
                if (toData.equals(dbObject)) {
                    success = "1";
                    importAdjustObject.setToData(dbObject);
                    importAdjustObject.setImportSuccessType(ExportConstant.importSuccessType_useSystemData);
                    importAdjustObject.setSuccess(success);
                    if (log.isDebugEnabled()) {
                        log.debug("对象使用系统中的数据,type:[" + type + "],o:[" + toData + "],数据库中的数据:[" + dbObject + "]");
                    }
                    return success;
                }
            }
        }
        return success;
    }

    @Override
    public String adjustKey(String type, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        String success = adjustDBByDBUrl(type, ExportConstant.DB_URL, true, importAdjustObject, null, toFolderId, importParam, context);
        // 将名称 Id, 放到缓存种
        Object fromData = importAdjustObject.getFromData();
        String fromDataId = null;
        if (fromData != null) {
            fromDataId = ExportUtil.getObjectId(type, fromData);
        }
        DataSource toData = (DataSource) importAdjustObject.getToData();
        String urlWithReturnAndParam = null;
        if (fromData != null) {
            urlWithReturnAndParam = toData.paseUrlWithReturnAndParam();
        }
        context.putneedSaveTmpIdAndName(ExportConstant.DB_URL, urlWithReturnAndParam, type, fromDataId);
        return success;
    }

    @Override
    public String adjustOtherProperty(String type, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        String suffix = null;
        String success = null;
        do {
            success = checkAdjustObjectProperty(type, ExportConstant.DB_ALIAS, false, true, importAdjustObject, suffix, toFolderId, importParam, context);
            suffix = getNextSuffix(suffix);
        } while (success == null || "-1".equals(success));


        suffix = null;
        success = null;
        do {
            success = checkAdjustObjectProperty(type, ExportConstant.DB_ID, false, false, importAdjustObject, suffix, toFolderId, importParam, context);
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

        List<DataSource> dbApiList = null;
        ImportIdOrNameCacheObject cacheneedSaveIdAndName = null;
//        if (ExportConstant.DB_URL.equals(idOrNameType)) {
//            if (!StringUtils.isBlank(dbIp)) {
//                dbApiList = dataSourceService.selectDBByProperty(null, null, dbIp);
////                if (dbApiList == null) {
////                    dbApiList = apiService.selectApiByProperty(null, null, dbIp, "1", toFolderId);
////                }
//                String urlWithReturnAndParam = toData.paseUrlWithReturnAndParam();
//                cacheneedSaveIdAndName = context.getCacheNeedSaveIdAndName(idOrNameType, urlWithReturnAndParam);
//            }
//        } else
        if (ExportConstant.DB_ALIAS.equals(idOrNameType)) {
            if (!StringUtils.isBlank(idOrNameValue)) {
                if (StringUtils.isNotBlank(suffix)) {
                    idOrNameValue = idOrNameValue + suffix;
                }
                idOrNameKey = idOrNameValue;
                importIdOrNameObject.setIdOrName(idOrNameValue);
                importIdOrNameObject.setIdOrNameKey(idOrNameKey);

                dbApiList = dataSourceService.selectDBByProperty(null, idOrNameValue, null);

            }
        } else if (ExportConstant.DB_ID.equals(idOrNameType)) {
            if (!StringUtils.isBlank(idOrNameValue)) {
                if (StringUtils.isNotBlank(suffix)) {
                    idOrNameValue = IdUtil.createId();
                }
                idOrNameKey = idOrNameValue;
                importIdOrNameObject.setIdOrName(idOrNameValue);
                importIdOrNameObject.setIdOrNameKey(idOrNameKey);

                dbApiList = dataSourceService.selectDBByProperty(idOrNameValue, null, null);
            }
        } else {
            throw new Exception("参数调整参数时,传入的类型错误,类型:[" + type + "],属性类型:[" + idOrNameType + "],调整结果对象:[" + importAdjustObject + "]");
        }

        if (!CollectionUtil.isEmpty(dbApiList)) {
            result.addAll(dbApiList);
        }
        return result;
    }


    /**
     * 这个方法跟 adjustDB 的区别是, 如果重复了,不设置success为-1
     */
    private String adjustDBByDBUrl(String type, String idOrNameType, boolean isKey, ImportAdjustObject importAdjustObject, String suffix, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        if (importAdjustObject == null) {
            throw new Exception("通过参数code调整参数失败, 文件中的参数不存在,调整结果:[" + importAdjustObject + "]");
        }
        String success = null;
        DataSource fromData = (DataSource) importAdjustObject.getFromData();
        String fromDataId = fromData.getDbId();

        DataSource toData = (DataSource) importAdjustObject.getToData();
        String dbIp = toData.getDbIp();

        // 通过获取数据库中的值, 获取缓存中的值
        List<DataSource> dbApiList = null;
        ImportIdOrNameCacheObject cacheneedSaveIdAndName = null;
        if (ExportConstant.DB_URL.equals(idOrNameType)) {
            if (!StringUtils.isBlank(dbIp)) {
                dbApiList = dataSourceService.selectDBByProperty(null, null, dbIp);
//                if (dbApiList == null) {
//                    dbApiList = apiService.selectApiByProperty(null, null, dbIp, "1", toFolderId);
//                }
                String urlWithReturnAndParam = toData.paseUrlWithReturnAndParam();
                cacheneedSaveIdAndName = context.getCacheNeedSaveIdAndName(idOrNameType, urlWithReturnAndParam);
            }
        } else {
            throw new Exception("参数调整参数时,传入的类型错误,类型:[" + type + "],属性类型:[" + idOrNameType + "],调整结果对象:[" + importAdjustObject + "]");
        }
        // 与数据库中获取的进行比较
        if (dbApiList != null) {
            if (isKey) {
                for (DataSource dbApi : dbApiList) {
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

    @Override
    public void dataPersistenceObject(String type, Object o, ImportContext context) throws Exception {
        if ((getSupport().getValue()).equals(type)) {
            DataSource dataSource = (DataSource) o;
            dataSourceService.insertDBDataPersistence(dataSource);
        }
    }

}
