package com.bonc.frame.service.modelImportAndExport.import_v1.imp;

import com.alibaba.fastjson.JSON;
import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelImport.ImportParam;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportIdOrNameCacheObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.importContext.ImportContext;
import com.bonc.frame.entity.rulefolder.RuleFolder;
import com.bonc.frame.entity.variable.Variable;
import com.bonc.frame.service.api.ApiService;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.datasource.DataSourceService;
import com.bonc.frame.service.kpi.KpiService;
import com.bonc.frame.service.metadata.DBMetaDataMgrService;
import com.bonc.frame.service.modelBase.ModelBaseService;
import com.bonc.frame.service.modelImportAndExport.import_v1.ImportAdjustService;
import com.bonc.frame.service.modelImportAndExport.import_v1.ImportHelper;
import com.bonc.frame.service.modelImportAndExport.import_v1.ModelImportType;
import com.bonc.frame.service.rule.RuleDetailService;
import com.bonc.frame.service.rule.RuleFolderService;
import com.bonc.frame.service.rule.RuleService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.util.ExportUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/25 12:08
 */
@Service
public abstract class AbstractImportAdjustServiceImp implements ImportAdjustService {
    Log log = LogFactory.getLog(getClass());

    @Autowired
    DaoHelper daoHelper;

    @Autowired
    RuleService ruleService;

    @Autowired
    RuleFolderService ruleFolderService;

    @Autowired
    RuleDetailService ruleDetailService;

    @Autowired
    ModelBaseService modelBaseService;

    @Autowired
    KpiService kpiService;

    @Autowired
    ApiService apiService;

    @Autowired
    VariableService variableService;

    @Autowired
    DataSourceService dataSourceService;

    @Autowired
    DBMetaDataMgrService dbMetaDataMgrService;

    @Autowired
    AuthorityService authorityService;

    @Override
    public ModelImportType getSupport() {
        return ModelImportType.ABS;
    }

    @Override
    public void adjust(String type, String id, ImportParam importParam, ImportContext context) throws Exception {
        if (context == null) {
            throw new Exception("导入上下文为null,importContext:" + context);
        }
        try {
            Object successData = context.getSuccessData(type, id);
            if (successData != null) {
                return;
            }
            Object o = context.getFileObject(type, id);
            if (o == null) {
                throw new Exception("获取文件中的对象失败，文件中不存在该对象。获取文件对象类型:[" + type + "],获取对象Id:[" + id + "]");
            }
            if (log.isDebugEnabled()) {
                log.debug("开始导入对象:type:[" + type + "],Object:[" + o + "]");
            }
            adjustObject(type, o, importParam, context);
        } catch (Exception e) {
            log.error("导入失败:type:" + type, e);
//            e.printStackTrace();
            context.putlackData(type, id, null, null);
        }
    }


    public void adjustObject(String type, Object fromData, ImportParam importParam, ImportContext context) throws Exception {
        Object toData = ExportUtil.copyObject(type, fromData);

        String success = "0";
        // 通过场景名称，获取数据库中已经存在的场景，如果已经存在，就不导入，用已存在的场景
        ImportAdjustObject importAdjustObject = new ImportAdjustObject();
        importAdjustObject.setType(type);
        importAdjustObject.setFromData(fromData);
        importAdjustObject.setToData(toData);
        importAdjustObject.setImportSuccessType(ExportConstant.importSuccessType_useImportData);
        // 导入引用的对象或实体
        String updateReferenceSuccess = updateReference(type, importAdjustObject, importParam, context);
        String toFolderId = ExportUtil.getObjectFolderId(type, importAdjustObject.getToData());
        if ("-1".equals(updateReferenceSuccess)) {
            success = "-1";
            if (log.isDebugEnabled()) {
                log.debug("导入对象失败:导入引用失败:type:[" + type + "],Object:[" + toData + "]");
            }
        } else if (updateReferenceSuccess == null || "0".equals(updateReferenceSuccess)) {
            // 通过 某个属性,判断是不是已经存在了这个属性
            String adjustByIdSuccess = adjustById(type, importAdjustObject, toFolderId, importParam, context);
            if ("-1".equals(adjustByIdSuccess)) {
                success = "-1";
                if (log.isDebugEnabled()) {
                    log.debug("导入对象失败:通过Id调整失败:type:[" + type + "],Object:[" + toData + "]");
                }
            } else if (adjustByIdSuccess == null || "0".equals(adjustByIdSuccess)) {
                String adjustKeySuccess = adjustKey(type, importAdjustObject, toFolderId, importParam, context);
                if ("-1".equals(adjustKeySuccess)) {
                    success = "-1";
                    if (log.isDebugEnabled()) {
                        log.debug("导入对象失败:调整主键失败:type:[" + type + "],Object:[" + toData + "]");
                    }
                } else if (adjustKeySuccess == null || "0".equals(adjustKeySuccess)) {// 如果不是采用系统对象和缓存中的对象,而是本体导入,并且缓存中没有, 调整其他属性;
                    String adjustOtherPropertySuccess = adjustOtherProperty(type, importAdjustObject, toFolderId, importParam, context);
                    if ("-1".equals(adjustOtherPropertySuccess)) {
                        success = "-1";
                        if (log.isDebugEnabled()) {
                            log.debug("导入对象失败:调整其它属性失败:type:[" + type + "],Object:[" + toData + "]");
                        }
                    }
                }
            }
        }
        if (!"-1".equals(success)) {
            log.debug("导入对象成功:type:[" + type + "],导入类型:[" + importAdjustObject.getImportSuccessType() + ",Object:[" + toData + "]");
            context.putAdjustTmpSuccessData(type, ExportUtil.getObjectId(type, fromData), importAdjustObject);
        } else {
            context.putFailImportData(importAdjustObject);
        }
    }

    public String updateReference(String type, ImportAdjustObject o, ImportParam importParam, ImportContext context) throws Exception {
        return "0";
    }

    public abstract String adjustById(String type, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception;

    public abstract String adjustKey(String type, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception;

    public abstract String adjustOtherProperty(String type, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception;
    // ------------------------------------------------ 比较  ---------------------------------------------------

    /**
     * 对调整的对象属性进行检查, -- 检查名称是否冲突等
     * 如果冲突,并且该属性是改对象进行一致性检验的key, 则进行比较,判断两个对象是否一致,如果一致,则使用系统
     *
     * @param type                     对象类型
     * @param idOrNameType             检验的属性类型
     * @param isKey                    改属性是不是这个对象 一致性检验的 的key
     * @param successUpdateIsUseUpdate 成功,并且进行了修改,导入成功类型是不是进行了调整  importSuccessType_useUpdateData
     *                                 目前对Id的调整不算是对这个对象进行了调整
     * @param importAdjustObject       对象
     * @param suffix                   属性添加的后缀
     * @param toFolderId               导入到的场景ID
     * @return -1:调整失败,属性跟数据库或缓存中的值冲突  0: 调整成功,继续后续的调整  1: 检验并调整成功,不进行后续操作,
     * @throws Exception
     */
    public String checkAdjustObjectProperty(String type, String idOrNameType, boolean isKey, boolean successUpdateIsUseUpdate,
                                            ImportAdjustObject importAdjustObject, String suffix, String toFolderId,
                                            ImportParam importParam, ImportContext context) throws Exception {
        if (importAdjustObject == null) {
            throw new Exception("通过参数code调整参数失败, 文件中的参数不存在,调整结果:[" + importAdjustObject + "]");
        }
        String success = "0";
        Object fromData = importAdjustObject.getFromData();
        String fromDataId = ExportUtil.getObjectId(type, fromData);

        Object toData = importAdjustObject.getToData();
        String sourceIdOrName = ExportUtil.getObjectProperty(idOrNameType, toData);

        ImportIdOrNameCacheObject importIdOrNameObject = new ImportIdOrNameCacheObject();
        importIdOrNameObject.setSourceIdOrName(sourceIdOrName);
        importIdOrNameObject.setIdOrName(sourceIdOrName);
        importIdOrNameObject.setIdOrNameType(idOrNameType);
        importIdOrNameObject.setSuffix(suffix);
        importIdOrNameObject.setFromObjectType(type);
        importIdOrNameObject.setFromObjectId(fromDataId);


        // 数据库中获取  并且给属性值添加后缀
        List<Object> dbVariable = updatePropertyAndgetDbObjectList(type, importIdOrNameObject,
                importAdjustObject, toFolderId, importParam, context);

        String idOrNameValue = importIdOrNameObject.getIdOrName();
        String idOrNameValueKey = importIdOrNameObject.getIdOrNameKey();
        ExportUtil.setObjectProperty(idOrNameType, idOrNameValue, toData);

        // 缓存中重复的对象
        ImportIdOrNameCacheObject cacheneedSaveIdAndName = context.getCacheNeedSaveIdAndName(idOrNameType, idOrNameValueKey);

        success = compareObjectCacheObject(type, idOrNameType, idOrNameValueKey, isKey,
                importAdjustObject, dbVariable, cacheneedSaveIdAndName, importParam, context);
        if ("1".equals(success)) {
            return success;
        } else if ("-1".equals(success)) {
            ExportUtil.setObjectProperty(idOrNameType, sourceIdOrName, toData);
        }

        // 如果 success为0,表示使用导入的数据,
        if (success == null || "0".equals(success)) {
            success = "0";
            // 将修改后的值赋给toData
            ExportUtil.setObjectProperty(idOrNameType, idOrNameValue, toData);
            // 修改后的值放到缓存中
//            context.putneedSaveTmpIdAndName(idOrNameType, idOrNameValueKey, idOrNameValue, type, fromDataId);
            context.putneedSaveTmpIdAndName(importIdOrNameObject);

            importAdjustObject.setToData(toData);
            importAdjustObject.setSuccess(success);
            // 判断成功导入的类型
            String importSuccessType = importAdjustObject.getImportSuccessType();
            if (StringUtils.isBlank(importSuccessType) || ExportConstant.importSuccessType_useImportData.equals(importSuccessType)) {
                if (StringUtils.isNotBlank(suffix) && successUpdateIsUseUpdate) {
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

    /**
     * 根据后缀修改属性值
     * 从数据库中获取同属性的值
     */
    public abstract List<Object> updatePropertyAndgetDbObjectList(String type, ImportIdOrNameCacheObject importIdOrNameObject, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception;

    /**
     * 对象 跟 数据库中冲突的数据以及缓存中冲突的数据进行比较
     *
     * @param type                         对象类型
     * @param idOrNameType                 属性类型
     * @param idOrNameKey                  属性的唯一主键  比如 模型版本号就是  ruleName-版本号
     * @param isKey                        是不是主键
     * @param importAdjustObject           调整对象
     * @param dbObjectList                 数据库中重复的数据
     * @param cacheObjectneedSaveIdAndName 缓存中重复的数据
     */
    public String compareObjectCacheObject(String type, String idOrNameType, String idOrNameKey, boolean isKey, ImportAdjustObject importAdjustObject,
                                           List<?> dbObjectList, ImportIdOrNameCacheObject cacheObjectneedSaveIdAndName, ImportParam importParam, ImportContext context) throws Exception {
        String success = "0";
        Object fromData = importAdjustObject.getFromData();
        String fromDataId = ExportUtil.getObjectId(type, fromData);
        if (fromDataId == null) {
            throw new RuntimeException("导入失败:fromDataId为空,type:[" + type + "],o:[" + JSON.toJSONString(importAdjustObject) + "]");
        }
        Object toData = importAdjustObject.getToData();
        // 与数据库中获取的进行比较
        if (dbObjectList != null && !dbObjectList.isEmpty()) {
            for (Object dbObject : dbObjectList) {
                if (ExportUtil.compareObjectProperty(idOrNameType, idOrNameKey, dbObject)) {
                    success = "-1";
                    if (log.isDebugEnabled()) {
                        log.debug("对象属性与系统中的数据重复,type:[" + type + "],o:[" + toData + "],数据库中的数据:[" + dbObject + "]");
                    }
                }
                if (isKey) {
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
//                    if (ExportConstant.MODEL_VERSION_NUMBER.equals(idOrNameType)) {
//                        success = "0";
//                    }
                }
            }
            importAdjustObject.setSuccess(success);
            if (log.isDebugEnabled() && success.equals("0")) {
                log.debug("与数据库中的数据不重复,type:[" + type + "],o:[" + toData + "]");
            }
            if (success.equals("-1")) {
                return success;
            }
        }
        // 与缓存中的值进行比较
        if (cacheObjectneedSaveIdAndName != null) {
            String cacheFromObjectId = cacheObjectneedSaveIdAndName.getFromObjectId();

            ImportAdjustObject cacheImportAdjustData = context.getSuccessData(type, cacheFromObjectId);
            if (cacheImportAdjustData != null && cacheImportAdjustData.getToData() != null) {
                Object cacheData = cacheImportAdjustData.getToData();
                if (ExportUtil.compareObjectProperty(idOrNameType, idOrNameKey, cacheData)) {
                    success = "-1";
                }
                //fromObjectId与 理论上这里不会相等
                if (fromDataId.equals(cacheFromObjectId)) { // 这里一般不会进来
                    success = "1";
                    importAdjustObject.setToData(cacheData);
                    importAdjustObject.setSuccess(success);
                    importAdjustObject.setImportSuccessType(cacheImportAdjustData.getImportSuccessType());
                    if (log.isDebugEnabled()) {
                        log.debug("对象使用缓存中的数据,type:[" + type + "],o:[" + toData + "],缓存中的数据:[" + cacheData + "]");
                    }
                    return success;
                }
                if (isKey) {
                    String importSuccessType = cacheImportAdjustData.getImportSuccessType();
                    //TODO : 两个同样的参数, code不一样, 但是修改过之后一样了, 这样的话导入几个?   比如 v1和v2都是文件中的两个一样的参数   v1改名之后改成了v2 ,这样的话,导入几个参数?
                    if (toData.equals(cacheData)) {
//                toData = (RuleFolder) cacheImportAdjustData;
                        success = "1";
                        if (ExportConstant.importSuccessType_useSystemData.equals(importSuccessType)) {
                            importAdjustObject.setToData(cacheData);
                            importAdjustObject.setImportSuccessType(ExportConstant.importSuccessType_useSystemData);
                        } else {
                            importAdjustObject.setToData(cacheData);
                            importAdjustObject.setImportSuccessType(ExportConstant.importSuccessType_useDuplicateData);
                        }
                    } //两个参数比较结束
                }
                importAdjustObject.setSuccess(success);
                if (success.equals("1") && log.isDebugEnabled()) {
                    log.debug("对象使用去重后的数据,type:[" + type + "],o:[" + toData + "]");
                }
                return success;
            }
        }
        // 与文件中的名称进行比较
        Object cachefileIdAndNameObject = context.getCachefileIdAndName(idOrNameType, idOrNameKey);
        if (cachefileIdAndNameObject != null) {
            String cachefileIdAndNameObjectId = ExportUtil.getObjectId(type, cachefileIdAndNameObject);
            if (StringUtils.isNotBlank(cachefileIdAndNameObjectId)) {
                // 如果id不一样,说明不是一个对象,名称与导入的名称冲突,success为-1
                if (!fromDataId.equals(cachefileIdAndNameObjectId)) {
                    success = "-1";
                }
            }
        }

        return success;
    }


    // --------------------------------save-------------------------------------------------
    @Override
//    @Transactional
    public void saveObject(String type, String fileObjectId, ImportAdjustObject object, ImportContext context) throws Exception {
//        Object todata = beforesSveObjectAdjust(type, id, object, context);
//        if (todata != null) {
//            dataPersistenceObject(type, todata);
//        }
        if (object == null) {
            object = getSuccessDataFromContext(type, fileObjectId, null, null, null, context);
        }
        if (object == null) {
            log.error("保存对象报错,需要保存的对象为null,type:" + type + ",object:" + object + " ,id:" + fileObjectId);
            throw new Exception("保存对象报错,需要保存的对象为null,type:" + type + ",object:" + object + " ,id:" + fileObjectId);
        }
        String putNeedSaveDataSuccess = context.putNeedSaveData(type, object, null, null);
        Object needSaveData = null;
        if ("0".equals(putNeedSaveDataSuccess)) {
            needSaveData = context.getNeedSaveData(type, object, null);
        }
        if (needSaveData != null) {
        	
        	//参数特殊化
        	/*if("variable".equals(type)) {
        		List<Variable> variableList = daoHelper.queryForList("com.bonc.frame.dao.variable.VariableMapper.selectByPrimaryKey", fileObjectId);
            	if(variableList.size()>0)
            		return;
        	}else if("api".equals(type)) {
        		Map<String, Object> param = new HashMap<>();
        		param.put("apiId", fileObjectId);
        		List<ApiConf> list = daoHelper.queryForList("com.bonc.frame.dao.api.ApiMapper.selectByPrimaryKey",param);
        		if(list.size()>0) return;
        	}*/
        	/*if("api".equals(type)) {
        		Map<String, Object> param = new HashMap<>();
        		param.put("apiId", fileObjectId);
        		List<ApiConf> list = daoHelper.queryForList("com.bonc.frame.dao.api.ApiMapper.selectByPrimaryKey",param);
        		if(list.size()>0) return;
        	}*/
            dataPersistenceObjectRef(type, object, context);
            dataPersistenceObject(type, needSaveData, context);
        }
    }

    //    public Object beforesSveObjectAdjust(String type, String id, ImportAdjustObject object, ImportContext context) throws Exception {
//        String putNeedSaveDataSuccess = context.putNeedSaveData(type, object, null, null);
//        Object needSaveData = null;
//        if ("0".equals(putNeedSaveDataSuccess)) {
//            needSaveData = context.getNeedSaveData(type, object, null);
//        }
//        return needSaveData;
//    }
    public void dataPersistenceObjectRef(String type, ImportAdjustObject o, ImportContext context) throws Exception {
        // 通过fromdata获取引用对象在文件中的Id, 通过Id,获取到修改后的对象,导入
        Object fromData = o.getFromData();
        Object toData = o.getToData();
        String objectFolderId = null;
        String fileObjectGroupId = null;
        if ("0".equals(ExportUtil.getObjectIsPublic(type, toData))) {
            objectFolderId = ExportUtil.getObjectFolderId(type, fromData);
        }
        if ("1".equals(ExportUtil.getObjectIsPublic(type, toData))) {
            fileObjectGroupId = ExportUtil.getObjectGroupId(type, fromData);
        }
        if (StringUtils.isNotBlank(objectFolderId)) {
            ImportHelper.getInstance().saveObject(ExportConstant.FOLDER, objectFolderId, null, context);
        }
        if (StringUtils.isNotBlank(fileObjectGroupId)) {
            String groupType = ExportUtil.getGroupType(type);
            ImportHelper.getInstance().saveObject(groupType, fileObjectGroupId, null, context);
        }

    }


    public abstract void dataPersistenceObject(String type, Object o, ImportContext context) throws Exception;


    // --------------------------------- 其他 ---------------------------------------
    public Variable getVariableInContextByVariableCode(String type, Object api, String variableCode, ImportParam importParam, ImportContext context) {

        Map<String, String> fileVariableColdIdMap = context.getFileVariableColdIdMap();
        if (fileVariableColdIdMap == null) {
            fileVariableColdIdMap = new HashMap<>();
        }
        Variable toData = null;
        if (StringUtils.isNotBlank(variableCode)) {
            try {
                String variableId = fileVariableColdIdMap.get(variableCode);
                if (StringUtils.isBlank(variableId)) { // 如果Id为null, 表示文件中没有这个参数, 将参数添加到缺失报告里面
                    Variable lackVariable = new Variable();
                    lackVariable.setVariableCode(variableCode);
                    context.putlackData(ExportConstant.VARIABLE, lackVariable, type, api);
                } else { // 如果存在的话, 去缓存中拿,
                    ImportAdjustObject successData = context.getSuccessData(ExportConstant.VARIABLE, variableId);
                    if (successData == null) {//如果缓存中不存在, 先去保存一次参数,再去缓存中拿
                        ImportHelper.getInstance().importAdjusObject(ExportConstant.VARIABLE, variableId, importParam, context);
                        successData = context.getSuccessData(ExportConstant.VARIABLE, variableId);
                    }
                    if (successData == null) { // 如果还是空, 则表示参数保存失败, -1
                        Variable lackVariable = new Variable();
                        lackVariable.setVariableCode(variableId);
                        lackVariable.setVariableCode(variableCode);
                        context.putlackData(ExportConstant.VARIABLE, lackVariable, type, api);
                        return null;
                    }
                    toData = (Variable) successData.getToData();
                    if (toData == null) {
                        Variable lackVariable = new Variable();
                        lackVariable.setVariableCode(variableId);
                        lackVariable.setVariableCode(variableCode);
                        context.putlackData(ExportConstant.VARIABLE, lackVariable, ExportConstant.API, api);
                        return null;
                    }
                }
            } catch (Exception e) {
                Variable lackVariable = new Variable();
                lackVariable.setVariableCode(variableCode);
                context.putlackData(ExportConstant.VARIABLE, lackVariable, type, api);
                log.error("导入参数失败", e);
//                e.printStackTrace();
            }
        }
        return toData;
    }

    public ImportAdjustObject getSuccessDataFromContext(String objectType, String fileObjectId, String fromType, Object fromObject, ImportParam importParam, ImportContext context) {
        ImportAdjustObject successData = null;
        try {
            if (StringUtils.isNotBlank(fileObjectId)) {
                successData = context.getSuccessData(objectType, fileObjectId);
                if (successData == null) {//如果缓存中不存在, 先去保存一次参数,再去缓存中拿
                    ImportHelper.getInstance().importAdjusObject(objectType, fileObjectId, importParam, context);
                    successData = context.getSuccessData(objectType, fileObjectId);
                }
                if (successData == null) { // 如果还是空, 则表示参数保存失败, -1
                    context.putlackData(objectType, fileObjectId, fromType, fromObject);
                    return null;
                }
                Object toData = successData.getToData();
                if (toData == null) {
                    context.putlackData(objectType, fileObjectId, fromType, fromObject);
                    return null;
                }
                Object fromData = successData.getFromData();
                if (fromData == null) {
                    context.putlackData(objectType, fileObjectId, fromType, fromObject);
                    return null;
                }
            }
        } catch (Exception e) {
            context.putlackData(objectType, fileObjectId, fromType, fromObject);
            e.printStackTrace();
        }
        return successData;
    }

//    protected String updateObjectSysV1ToV2(String type, ImportAdjustObject o, ImportParam importParam, ImportContext context) {
//
//    }

    protected String updateObjectFolderOrGroup(String type, ImportAdjustObject o, String groupType, ImportParam importParam, ImportContext context) throws Exception {
        String success = "0";
        Object toData = o.getToData();
        String ObjectFolderId = ExportUtil.getObjectFolderId(type, toData);
        String objectGroupId = ExportUtil.getObjectGroupId(type, toData);
        if (log.isDebugEnabled()) {
            log.debug("导入type:[" + type + "]所在的组或场景,FolderId:[" + ObjectFolderId + "],GroupId:[" + objectGroupId + "]" +
                    "\n             Object:" + o);
        }
        if (!StringUtils.isBlank(ObjectFolderId)) { // 当这个对象的场景ID不等于null,说明他是私有的
            ImportAdjustObject successDataFolder = getSuccessDataFromContext(ExportConstant.FOLDER, ObjectFolderId, type, o, importParam, context);
            if (successDataFolder != null) {
                RuleFolder toFolder = (RuleFolder) successDataFolder.getToData();
                String folderId = toFolder.getFolderId();
                if (StringUtils.isNotBlank(folderId)) {
                    ExportUtil.setObjectIsPublic(type, toData, "0");
                    ExportUtil.setObjectFolderId(type, toData, folderId, null);
                } else {
                    success = "-1";
                    o.setSuccess(success);
                    o.addExceptionMessage("导入" + type + "失败:缺失导入到的场景,类型" + type);
                    log.error("导入type:[" + type + "]所在的组或场景,FolderId:[" + ObjectFolderId + "],GroupId:[" + objectGroupId + "]");
                }
            } else {
                success = "-1";
                o.setSuccess(success);
                o.addExceptionMessage("导入" + type + "失败:缺失导入到的场景,类型" + type);
            }
        }
        if (!StringUtils.isBlank(objectGroupId)) { // 当这个对象的场景ID不等于null,说明他是私有的
            ImportAdjustObject successDataApiGroup = getSuccessDataFromContext(groupType, objectGroupId, type, o, importParam, context);
            if (successDataApiGroup == null) {
                success = "-1";
            } else {
                Object toDataApiGroup = successDataApiGroup.getToData();
                String apiGroupId = ExportUtil.getObjectGroupId(groupType, toDataApiGroup);
                if (!StringUtils.isBlank(apiGroupId)) {
                    ExportUtil.setObjectIsPublic(type, toData, "1");
                    ExportUtil.setObjectGroupId(type, toData, apiGroupId);
                } else {
                    success = "-1";
                    o.setSuccess(success);
                    o.addExceptionMessage("导入" + type + "失败:缺失导入到的组,组类型:" + groupType);
                }
            }
        }
        if (StringUtils.isBlank(ObjectFolderId) && StringUtils.isBlank(objectGroupId)) {    // 如果参数的场景和模型组都为null,则判断是不是有导入到的场景,如果没有,则改参数导入失败
            success = "-1";
            o.setSuccess(success);
            o.addExceptionMessage("导入" + type + "失败:导入到的组或场景都为null,类型:" + type + ",组类型:" + groupType);
        }
        return success;
    }


    public String getNextSuffix(String suffix) {
        if (StringUtils.isBlank(suffix)) {
            suffix = "1";
        } else {
            suffix = (Integer.parseInt(suffix) + 1) + "";
        }
        return suffix;
    }

    // --------------------------------------- export ---------------------------------------
    @Override
    public void addExport() throws Exception {

    }
}
