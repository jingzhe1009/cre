package com.bonc.frame.service.modelImportAndExport.import_v1.imp;

import com.bonc.frame.entity.commonresource.ModelGroup;
import com.bonc.frame.entity.modelCompare.entity.ModelOperateLog;
import com.bonc.frame.entity.modelImportAndExport.modelExport.ExportParam;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelImport.ImportParam;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportIdOrNameCacheObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.importContext.ImportContext;
import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.rulefolder.RuleFolder;
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
public class ModelHeaderImportAdjustServiceImp extends AbstractImportAdjustServiceImp {
    @Override
    public ModelImportType getSupport() {
        return ModelImportType.MODEL_HEADER;
    }

    @Override
    public String updateReference(String type, ImportAdjustObject o, ImportParam importParam, ImportContext context) throws Exception {
        String success = "0";
        String updateApiFolderOrGroupSuccess = updateObjectFolderOrGroup(type, o, ExportConstant.MODEL_GROUP, importParam, context);
        if ("-1".equals(updateApiFolderOrGroupSuccess)) {
            success = "-1";
        }
        updateModelHeaderName(o);
        return success;

    }

    private void updateModelHeaderName(ImportAdjustObject o) {
        RuleDetailHeader toData = (RuleDetailHeader) o.getToData();
        String toDataModuleName = toData.getModuleName();
        if (StringUtils.isBlank(toDataModuleName)) {
            toDataModuleName = toData.getRuleName();
            toData.setModuleName(toDataModuleName);
        }
        toData.setIsHeader("1");
    }

    @Override
    public String adjustById(String type, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        return null;
    }

    @Override
    public String adjustKey(String type, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        String s = null;
        if (importParam != null) {
            s = importParam.getImportStrategy().get(type);
        }
        if (StringUtils.isBlank(s)) {
            s = "default";
        }
        String suffix = null;
        String success = null;
        do {
            switch (s) {
                case "default":
                case "removal": // 冲突重命名
                    success = checkAdjustObjectProperty(type, ExportConstant.MODEL_NAME, false, true, importAdjustObject, suffix, toFolderId, importParam, context);
                    break;
                case "upLine": // 上线 -- 冲突新建版本
                    success = checkAdjustObjectProperty(type, ExportConstant.MODEL_NAME, true, true, importAdjustObject, suffix, toFolderId, importParam, context);
                    break;
            }
            suffix = getNextSuffix(suffix);
        } while (success == null || "-1".equals(success));
        return success;
    }

    @Override
    public String adjustOtherProperty(String type, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        String suffix = null;
        String success = null;
        do {
            success = checkAdjustObjectProperty(type, ExportConstant.MODEL_RULE_NAME, false, false, importAdjustObject, suffix, toFolderId, importParam, context);
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
        List<RuleDetailHeader> ruleDetailHeaderList = null;
        ImportIdOrNameCacheObject cacheneedSaveIdAndName = null;
        if (ExportConstant.MODEL_NAME.equals(idOrNameType)) {
            if (!StringUtils.isBlank(idOrNameValue)) {
                if (StringUtils.isNotBlank(suffix)) {
                    idOrNameValue = idOrNameValue + suffix;
                }
                idOrNameKey = toFolderId + "-" + idOrNameValue;
                importIdOrNameObject.setIdOrName(idOrNameValue);
                importIdOrNameObject.setIdOrNameKey(idOrNameKey);

                if (StringUtils.isNotBlank(toFolderId)) {
                    ruleDetailHeaderList = ruleDetailService.getModelHeaderListByProperty(null, idOrNameValue, "0", toFolderId, null);
                }
                List<RuleDetailHeader> modelHeaderListByProperty = ruleDetailService.getModelHeaderListByProperty(null, idOrNameValue, "1", null, null);
                if (CollectionUtil.isEmpty(ruleDetailHeaderList)) {
                    ruleDetailHeaderList = modelHeaderListByProperty;
                } else {
                    if (!CollectionUtil.isEmpty(modelHeaderListByProperty)) {
                        ruleDetailHeaderList.addAll(modelHeaderListByProperty);
                    }
                }
            }
        } else if (ExportConstant.MODEL_RULE_NAME.equals(idOrNameType)) {
            if (!StringUtils.isBlank(idOrNameValue)) {
                if (StringUtils.isNotBlank(suffix)) {
                    idOrNameValue = IdUtil.createId();
                }
                idOrNameKey = idOrNameValue;
                importIdOrNameObject.setIdOrName(idOrNameValue);
                importIdOrNameObject.setIdOrNameKey(idOrNameKey);

                ruleDetailHeaderList = ruleDetailService.getModelHeaderListByProperty(idOrNameValue, null, null, null, null);
            }
        } else {
            throw new Exception("参数调整参数时,传入的类型错误,类型:[" + type + "],属性类型:[" + idOrNameType + "],调整结果对象:[" + importAdjustObject + "]");
        }

        if (ruleDetailHeaderList != null && !ruleDetailHeaderList.isEmpty()) {
            result.addAll(ruleDetailHeaderList);
        }
        return result;
    }

    protected String updateObjectFolderOrGroup(String type, ImportAdjustObject o, String groupType, ImportParam importParam, ImportContext context) throws Exception {
        String success = "0";
        Object fromData = o.getFromData();
        Object toData = o.getToData();

        ExportParam importTo = null;
        if (importParam != null) {
            importTo = importParam.getImportTo();
        }
        String objectFolderId = ExportUtil.getObjectFolderId(type, toData);
        String objectGroupId = ExportUtil.getObjectGroupId(type, toData);
        if (importTo != null) {
            String toFolderId = importTo.getFolderId();
            if (!StringUtils.isBlank(toFolderId)) { // 表示 这个场景
                if (!ExportConstant.FOLDER_PUB_ID.equals(toFolderId)) { // 导入到场景
                    if (StringUtils.isBlank(objectFolderId)) {
                        objectFolderId = ExportConstant.FOLDER_PUB_ID;
                        ExportUtil.setObjectFolderId(type, toData, objectFolderId, null);
                        ExportUtil.setObjectFolderId(type, fromData, objectFolderId, null);
                    }
                    ImportAdjustObject successDataFolder = getSuccessDataFromContext(ExportConstant.FOLDER, objectFolderId, type, o, importParam, context);
                    if (successDataFolder != null) {
                        RuleFolder toFolder = (RuleFolder) successDataFolder.getToData();
                        String folderId = toFolder.getFolderId();
                        if (!StringUtils.isBlank(folderId)) {
                            ExportUtil.setObjectIsPublic(type, toData, "0");
                            ExportUtil.setObjectFolderId(type, toData, folderId, null);
                            ExportUtil.setObjectGroupId(type, toData, null);
                        } else {
                            success = "-1";
                            o.setSuccess(success);
                            o.addExceptionMessage("导入模型头失败:缺失导入到的场景");
                        }
                    } else {
                        success = "-1";
                        o.setSuccess(success);
                        o.addExceptionMessage("导入模型头失败:缺失导入到的场景");
                    }
                    return success;
                } else { // 导入到模型库下的模型组
                    String toModerGroupId = importTo.getModelGroupId();
                    if (StringUtils.isBlank(objectFolderId)) {
                        objectFolderId = ExportConstant.FOLDER_PUB_ID;
                        ExportUtil.setObjectFolderId(type, toData, objectFolderId, null);
                        ExportUtil.setObjectFolderId(type, fromData, objectFolderId, null);
                    }
                    if (StringUtils.isBlank(objectGroupId)) {
                        objectGroupId = ExportConstant.MODEL_GROUP_TMP_ID;
                        ExportUtil.setObjectGroupId(type, toData, objectGroupId);
                        ExportUtil.setObjectGroupId(type, fromData, objectGroupId);
                        objectFolderId = ExportConstant.FOLDER_PUB_ID;
                        ExportUtil.setObjectFolderId(type, toData, objectFolderId, null);
                    }
                    ImportAdjustObject successDataModelGroup = getSuccessDataFromContext(ExportConstant.MODEL_GROUP, objectGroupId, type, o, importParam, context);
                    if (successDataModelGroup != null) {
                        ModelGroup toModelGroup = (ModelGroup) successDataModelGroup.getToData();
                        String modelGroupId = toModelGroup.getModelGroupId();
                        if (!StringUtils.isBlank(modelGroupId) && !ExportConstant.MODEL_GROUP_TMP_ID.equals(modelGroupId)) {
                            ExportUtil.setObjectIsPublic(type, toData, "1");
                            ExportUtil.setObjectGroupId(type, toData, modelGroupId);
                        } else {
                            success = "-1";
                            o.setSuccess(success);
                            o.addExceptionMessage("导入模型头失败:缺失导入到的模型组");
                        }
                    } else {
                        success = "-1";
                        o.setSuccess(success);
                        o.addExceptionMessage("导入模型头失败:缺失导入到的模型组");
                    }
                    return success;
                }
            }
        }
        success = super.updateObjectFolderOrGroup(type, o, groupType, importParam, context);
        return success;
    }


    // -----------------------------------save ---------------------------------------------------

    @Override
    public void dataPersistenceObject(String type, Object o, ImportContext context) throws Exception {
        if ((getSupport().getValue()).equals(type)) {
            RuleDetailHeader ruleDetailHeader = (RuleDetailHeader) o;
            RuleDetailWithBLOBs saveRuleHeader = new RuleDetailWithBLOBs();
            org.springframework.beans.BeanUtils.copyProperties(ruleDetailHeader, saveRuleHeader);
            if (StringUtils.isBlank(saveRuleHeader.getRuleId())) {
                saveRuleHeader.setRuleId(IdUtil.createId());
            }
            saveRuleHeader.setIsHeader("1");
            ruleDetailService.insertRuleToOfficialDataPersistence(saveRuleHeader, ModelOperateLog.COMMIT_MODEL_TYPE);
        }
    }

}
