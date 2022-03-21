package com.bonc.frame.service.modelImportAndExport.import_v1.imp;

import com.bonc.frame.entity.model.ModelContentInfo;
import com.bonc.frame.entity.modelCompare.entity.ModelOperateLog;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelImport.ImportParam;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportIdOrNameCacheObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.importContext.ImportContext;
import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.service.modelImportAndExport.import_v1.ImportHelper;
import com.bonc.frame.service.modelImportAndExport.import_v1.ModelImportType;
import com.bonc.frame.util.CollectionUtil;
import com.bonc.frame.util.ConstantUtil;
import com.bonc.frame.util.ExportUtil;
import com.bonc.frame.util.IdUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/25 13:26
 */
@Service
public class ModelVersionImportAdjustServiceImp extends AbstractImportAdjustServiceImp {
    @Override
    public ModelImportType getSupport() {
        return ModelImportType.MODEL_VERSION;
    }

    public String updateReference(String type, ImportAdjustObject o, ImportParam importParam, ImportContext context) throws Exception {
        String success = "0";
//        String updateApiFolderOrGroupSuccess = updateObjectFolderOrGroup(type, o, ExportConstant.MODEL_GROUP, importParam, context);
//        if ("-1".equals(updateApiFolderOrGroupSuccess)) {
//            success = "-1";
//        }
        String updateModelHeaderSuccess = updateModelHeader(type, o, importParam, context);
        if ("-1".equals(updateModelHeaderSuccess)) {
            success = "-1";
        }
        String updateModelVersionRefObjectIdSuccess = updateModelVersionRefObjectId(type, o, importParam, context);
        if ("-1".equals(updateModelVersionRefObjectIdSuccess)) {
            success = "-1";
        }
        return success;

    }

    @Override
    public String adjustById(String type, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        return null;
    }

    @Override
    public String adjustKey(String type, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        String success = null;
        RuleDetailWithBLOBs toData = (RuleDetailWithBLOBs) importAdjustObject.getToData();
        String toDataRuleName = toData.getRuleName();
        String toDataVersionOldNum = toData.getVersion();
        String toDataVersionNum = ruleDetailService.generateVersion(toDataRuleName, false, true);
        success = checkAdjustObjectProperty(type, ExportConstant.MODEL_VERSION_NUMBER, true, true, importAdjustObject, toDataVersionNum, toFolderId, importParam, context);
        while (success == null || "-1".equals(success)) {
            BigDecimal big100 = BigDecimal.valueOf(100f);
            toDataVersionNum = String.valueOf(BigDecimal.valueOf(Double.parseDouble(toDataVersionNum)).multiply(big100).add(BigDecimal.valueOf(1f)).divide(big100));
            success = checkAdjustObjectProperty(type, ExportConstant.MODEL_VERSION_NUMBER, true, true, importAdjustObject, toDataVersionNum, toFolderId, importParam, context);
        }
        String importSuccessType = importAdjustObject.getImportSuccessType();
        if (StringUtils.isBlank(importSuccessType) || !ExportConstant.importSuccessType_useSystemData.equals(importSuccessType)) {
            if (StringUtils.isBlank(toDataVersionOldNum) || !toDataVersionOldNum.equals(toDataVersionNum)) {
                importSuccessType = ExportConstant.importSuccessType_useUpdateData;
            } else {
                importSuccessType = ExportConstant.importSuccessType_useImportData;
            }
            importAdjustObject.setImportSuccessType(importSuccessType);
        }
        return success;
    }

    @Override
    public String adjustOtherProperty(String type, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        String suffix = null;
        String success = null;
        do {
            success = checkAdjustObjectProperty(type, ExportConstant.MODEL_VERSION_ID, false, false, importAdjustObject, suffix, toFolderId, importParam, context);
            suffix = getNextSuffix(suffix);
        } while (success == null || "-1".equals(success));
        return importAdjustObject.getSuccess();
    }

    @Override
    public List<Object> updatePropertyAndgetDbObjectList(String type, ImportIdOrNameCacheObject importIdOrNameObject, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        RuleDetailWithBLOBs toData = (RuleDetailWithBLOBs) importAdjustObject.getToData();
        String toDataRuleName = toData.getRuleName();

        String idOrNameType = importIdOrNameObject.getIdOrNameType();
        String idOrNameValue = importIdOrNameObject.getIdOrName();
        String idOrNameKey = importIdOrNameObject.getIdOrNameKey();
        String suffix = importIdOrNameObject.getSuffix();
        List<Object> result = new ArrayList<>();


        List<RuleDetailWithBLOBs> dbRuleDetailWithBLOBsList = null;
        ImportIdOrNameCacheObject cacheneedSaveIdAndName = null;
        if (ExportConstant.MODEL_VERSION_NUMBER.equals(idOrNameType)) {

            if (StringUtils.isNotBlank(suffix)) {
                idOrNameValue = suffix.replace("_", "");
            }
            idOrNameKey = toDataRuleName + "-" + idOrNameValue;
            importIdOrNameObject.setIdOrName(idOrNameValue);
            importIdOrNameObject.setIdOrNameKey(idOrNameKey);

            if (!StringUtils.isBlank(idOrNameValue)) {
                dbRuleDetailWithBLOBsList = ruleDetailService.getRuleDetailWithBLOBsByProperty(toDataRuleName, null, null, false);
            }
        } else if (ExportConstant.MODEL_VERSION_ID.equals(idOrNameType)) {

            if (StringUtils.isNotBlank(suffix)) {
                idOrNameValue = IdUtil.createId();
            }
            idOrNameKey = idOrNameValue;
            importIdOrNameObject.setIdOrName(idOrNameValue);
            importIdOrNameObject.setIdOrNameKey(idOrNameKey);

            if (!StringUtils.isBlank(idOrNameValue)) {
                dbRuleDetailWithBLOBsList = ruleDetailService.getRuleDetailWithBLOBsByProperty(null, idOrNameValue, null, false);
            }
        } else {
            throw new Exception("参数调整参数时,传入的类型错误,类型:[" + type + "],属性类型:[" + idOrNameType + "],调整结果对象:[" + importAdjustObject + "]");
        }
        if (dbRuleDetailWithBLOBsList != null && !dbRuleDetailWithBLOBsList.isEmpty()) {
            result.addAll(dbRuleDetailWithBLOBsList);
        }
        return result;
    }

    private String updateModelHeader(String type, ImportAdjustObject o, ImportParam importParam, ImportContext context) {
        String success = "0";
        RuleDetailWithBLOBs ruleDetailWithBLOBs = (RuleDetailWithBLOBs) o.getToData();
        String ruleName = ruleDetailWithBLOBs.getRuleName();
        ImportAdjustObject successDataModelHeader = getSuccessDataFromContext(ExportConstant.MODEL_HEADER, ruleName, ExportConstant.MODEL_VERSION, ruleDetailWithBLOBs, importParam, context);
        if (successDataModelHeader == null) {
            success = "-1";
            o.setSuccess(success);
            o.addExceptionMessage("导入模型失败:导入模型头失败;");
        } else {
            RuleDetailHeader toData = (RuleDetailHeader) successDataModelHeader.getToData();
            // TODO, 可能会有问题, 这样会把创建人也给添加进去
            org.springframework.beans.BeanUtils.copyProperties(toData, ruleDetailWithBLOBs);
            ruleDetailWithBLOBs.setIsHeader("0");
        }
        o.setToData(ruleDetailWithBLOBs);
        return success;
    }

    private String updateModelVersionRefObjectId(String type, ImportAdjustObject o, ImportParam importParam, ImportContext context) throws Exception {
        String success = "0";
        RuleDetailWithBLOBs ruleDetailWithBLOBs = (RuleDetailWithBLOBs) o.getToData();
        ruleDetailWithBLOBs.setRuleStatus(ConstantUtil.RULE_STATUS_READY);
        String ruleContent = ruleDetailWithBLOBs.getRuleContent();
        if (StringUtils.isNotBlank(ruleContent)) {
            ModelContentInfo modelContentInfo = ruleDetailService.getModelContentInfoExcudeRange(ruleContent);
            List<String> kpiIdsFromModle = modelContentInfo.getKpiIdSet();
            List<String> apiIds = modelContentInfo.getApiIdSet();
            List<String> variableIds = modelContentInfo.getVariableIdSet();

            String updateRuleContentRefKpiIdsSuccess = updateRuleContentRefIds(ExportConstant.KPI, kpiIdsFromModle, ruleDetailWithBLOBs, o, importParam, context);
            if ("-1".equals(updateRuleContentRefKpiIdsSuccess)) {
                success = "-1";
            }

            String updateRuleContentRefApiIdsSuccess = updateRuleContentRefIds(ExportConstant.API, apiIds, ruleDetailWithBLOBs, o, importParam, context);
            if ("-1".equals(updateRuleContentRefApiIdsSuccess)) {
                success = "-1";
            }
            String updateRuleContentRefVariableIdsSuccess = updateRuleContentRefIds(ExportConstant.VARIABLE, variableIds, ruleDetailWithBLOBs, o, importParam, context);
            if ("-1".equals(updateRuleContentRefVariableIdsSuccess)) {
                success = "-1";
            }
            o.setToData(ruleDetailWithBLOBs);
        }
        return success;
    }

    public String updateRuleContentRefIds(String type, List<String> idsFromModle, RuleDetailWithBLOBs ruleDetailWithBLOBs, ImportAdjustObject o, ImportParam importParam, ImportContext context) throws Exception {
        String success = "0";
        String ruleContent = ruleDetailWithBLOBs.getRuleContent();
        String ruleDetailWithBLOBsIsPublic = ruleDetailWithBLOBs.getIsPublic();
        if (idsFromModle != null && !idsFromModle.isEmpty()) {
            for (String id : idsFromModle) {
                if (StringUtils.isNotBlank(id)) {
                    ImportAdjustObject successDataKpi = getSuccessDataFromContext(type, id, ExportConstant.MODEL_VERSION, ruleDetailWithBLOBs, importParam, context);
                    if (successDataKpi == null) {
                        success = "-1";
                        o.setSuccess(success);
                        o.addExceptionMessage("导入模型失败:缺失引用的对象;引用类型:" + type + ",缺失Id:" + id);
                        continue;
                    }
                    Object toData = successDataKpi.getToData();
                    String toDataIsPublic = ExportUtil.getObjectIsPublic(type, toData);
                    if ("1".equals(ruleDetailWithBLOBsIsPublic) && "0".equals(toDataIsPublic)) { // 模型是公共的, 但是引用缺失私有的,导入失败
                        success = "-1";
                        o.setSuccess(success);
                        o.addExceptionMessage("导入模型失败:引用的对象是私有的;引用类型:" + type + ",引用对象Id:" + id + ",引用对象:" + successDataKpi);
                        continue;
                    }
//                    String toDataId = ExportUtil.getObjectId(type, toData);
//                    if (!id.equals(toDataId)) {
//                        ruleContent = ruleDetailService.updateRuleContentRefId(ruleContent, type, id, toDataId);
                    if (!ExportConstant.importSuccessType_useImportData.equals(successDataKpi.getImportSuccessType())) {
                        ruleContent = ruleDetailService.updateRuleContentRefIdAndName(ruleContent, successDataKpi);
                    }
//                    }
                }
            }
            ruleDetailWithBLOBs.setRuleContent(ruleContent);
        }
        return success;
    }

    // ------------------------------------------------- save --------------------------------------------------
    @Override
    public void dataPersistenceObjectRef(String type, ImportAdjustObject o, ImportContext context) throws Exception {
        RuleDetailWithBLOBs ruleDetailWithBLOBs = (RuleDetailWithBLOBs) o.getFromData();
        String ruleName = ruleDetailWithBLOBs.getRuleName();
        if (StringUtils.isNotBlank(ruleName)) {
            ImportHelper.getInstance().saveObject(ExportConstant.MODEL_HEADER, ruleName, null, context);
        }
        String ruleContent = ruleDetailWithBLOBs.getRuleContent();
        ModelContentInfo modelContentInfo = ruleDetailService.getModelContentInfoExcudeRange(ruleContent);
        List<String> kpiIdsFromModle = modelContentInfo.getKpiIdSet();
        List<String> apiIds = modelContentInfo.getApiIdSet();
        List<String> variableIds = modelContentInfo.getVariableIdSet();
        if (!CollectionUtil.isEmpty(kpiIdsFromModle)) {
            for (String kpiId : kpiIdsFromModle) {
                ImportHelper.getInstance().saveObject(ExportConstant.KPI, kpiId, null, context);
            }
        }
        if (!CollectionUtil.isEmpty(apiIds)) {
            for (String apiId : apiIds) {
                ImportHelper.getInstance().saveObject(ExportConstant.API, apiId, null, context);
            }
        }
        if (!CollectionUtil.isEmpty(variableIds)) {
            for (String variableId : variableIds) {
                ImportHelper.getInstance().saveObject(ExportConstant.VARIABLE, variableId, null, context);
            }
        }
    }

    @Override
    public void dataPersistenceObject(String type, Object o, ImportContext context) throws Exception {
        if ((getSupport().getValue()).equals(type)) {
            RuleDetailWithBLOBs ruleDetailWithBLOBs = (RuleDetailWithBLOBs) o;
            ruleDetailWithBLOBs.setIsHeader("0");
            ruleDetailWithBLOBs.setRuleStatus(ConstantUtil.RULE_STATUS_READY);
            ruleDetailService.insertRuleToOfficialDataPersistence(ruleDetailWithBLOBs, ModelOperateLog.COMMIT_MODEL_TYPE);
        }
    }
}
