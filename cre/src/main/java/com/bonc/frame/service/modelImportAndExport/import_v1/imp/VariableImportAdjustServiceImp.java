package com.bonc.frame.service.modelImportAndExport.import_v1.imp;

import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelImport.ImportParam;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportIdOrNameCacheObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.importContext.ImportContext;
import com.bonc.frame.entity.variable.VariableTreeNode;
import com.bonc.frame.service.modelImportAndExport.import_v1.ImportHelper;
import com.bonc.frame.service.modelImportAndExport.import_v1.ModelImportType;
import com.bonc.frame.util.CollectionUtil;
import com.bonc.frame.util.IdUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/25 13:26
 */
@Service
public class VariableImportAdjustServiceImp extends AbstractImportAdjustServiceImp {
    @Override
    public ModelImportType getSupport() {
        return ModelImportType.VARIABLE;
    }

    public String updateReference(String type, ImportAdjustObject o, ImportParam importParam, ImportContext context) throws Exception {
        String success = "0";
        String updateApiFolderOrGroupSuccess = updateObjectFolderOrGroup(type, o, ExportConstant.VARIABLE_GROUP, importParam, context);
        if ("-1".equals(updateApiFolderOrGroupSuccess)) {
            success = "-1";
        }
        VariableTreeNode toData = (VariableTreeNode) o.getToData();
        String updateVariableNestedSuccess = updateVariableNested(type, o, importParam, context);
        if ("-1".equals(updateVariableNestedSuccess)) {
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
        String suffix = null;
        String success = null;
        do {
//            success = adjustVariable(type, ExportConstant.VARIABLE_CODE, true, importAdjustObject, suffix, toFolderId, importParam, context);
            success = checkAdjustObjectProperty(type, ExportConstant.VARIABLE_CODE, true, true, importAdjustObject, suffix, toFolderId, importParam, context);
            suffix = getNextSuffix(suffix);
        } while (success == null || "-1".equals(success));
        return success;
    }

    @Override
    public String adjustOtherProperty(String type, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        String suffix = null;
        String success = null;
        do {
//            success = adjustVariable(type, ExportConstant.VARIABLE_ALIAS, false, importAdjustObject, suffix, toFolderId, importParam, context);
            success = checkAdjustObjectProperty(type, ExportConstant.VARIABLE_ALIAS, false, true, importAdjustObject, suffix, toFolderId, importParam, context);
            suffix = getNextSuffix(suffix);
        } while (success == null || "-1".equals(success));

        // id
        suffix = null;
        success = null;
        do {
//            success = adjustVariable(type, ExportConstant.VARIABLE_ID, false, importAdjustObject, suffix, toFolderId, importParam, context);
            success = checkAdjustObjectProperty(type, ExportConstant.VARIABLE_ID, false, false, importAdjustObject, suffix, toFolderId, importParam, context);
            suffix = getNextSuffix(suffix);
        } while (success == null || "-1".equals(success));

        // 实体id
        suffix = null;
        success = null;
        do {
//            success = adjustVariable(type, ExportConstant.VARIABLE_ENTITY_ID, false, importAdjustObject, suffix, toFolderId, importParam, context);
            success = checkAdjustObjectProperty(type, ExportConstant.VARIABLE_ENTITY_ID, false, false, importAdjustObject, suffix, toFolderId, importParam, context);
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
        List<VariableTreeNode> dbVariable = null;
        if (ExportConstant.VARIABLE_CODE.equals(idOrNameType)) {
            if (!StringUtils.isBlank(idOrNameValue)) {
                // 根据后缀调整值
                if (StringUtils.isNotBlank(suffix)) {
                    if (idOrNameValue.length() > 28) {
//                        idOrNameValue = idOrNameValue.substring(0, idOrNameValue.length() - 2);
                        idOrNameValue = idOrNameValue.substring(0, 28);
                    }
                    idOrNameValue = idOrNameValue + suffix;
                }
                idOrNameKey = toFolderId + "-" + idOrNameValue;
                importIdOrNameObject.setIdOrName(idOrNameValue);
                importIdOrNameObject.setIdOrNameKey(idOrNameKey);

                // 获取数据库中的同属性的值
                if (StringUtils.isNotBlank(toFolderId)) {
                    dbVariable = variableService.selectVariableNodeWithNestedByVariableProperty(null, null, null, idOrNameValue, null, "0", toFolderId, null);
                }
                List<VariableTreeNode> dbPubVariable = variableService.selectVariableNodeWithNestedByVariableProperty(null, null, null, idOrNameValue, null, "1", null, null);
                if (CollectionUtil.isEmpty(dbVariable)) {
                    dbVariable = dbPubVariable;
                } else {
                    if (!CollectionUtil.isEmpty(dbPubVariable)) {
                        dbVariable.addAll(dbPubVariable);
                    }
                }
            }
        } else if (ExportConstant.VARIABLE_ALIAS.equals(idOrNameType)) {
            if (!StringUtils.isBlank(idOrNameValue)) {
                // 根据后缀调整值
                if (StringUtils.isNotBlank(suffix)) {
                    idOrNameValue = idOrNameValue + suffix;
                }
                idOrNameKey = toFolderId + "-" + idOrNameValue;
                importIdOrNameObject.setIdOrName(idOrNameValue);
                importIdOrNameObject.setIdOrNameKey(idOrNameKey);

                if (StringUtils.isNotBlank(toFolderId)) {
                    dbVariable = variableService.selectVariableNodeWithNestedByVariableProperty(null, null, idOrNameValue, null, null, "0", toFolderId, null);
                }
                List<VariableTreeNode> dbPubVariable = variableService.selectVariableNodeWithNestedByVariableProperty(null, null, idOrNameValue, null, null, "1", null, null);
                if (CollectionUtil.isEmpty(dbVariable)) {
                    dbVariable = dbPubVariable;
                } else {
                    if (!CollectionUtil.isEmpty(dbPubVariable)) {
                        dbVariable.addAll(dbPubVariable);
                    }
                }
            }
        } else if (ExportConstant.VARIABLE_ID.equals(idOrNameType)) {
            if (!StringUtils.isBlank(idOrNameValue)) {
                // 根据后缀调整值
                if (StringUtils.isNotBlank(suffix)) {
                    idOrNameValue = IdUtil.createId();
                }
                idOrNameKey = idOrNameValue;
                importIdOrNameObject.setIdOrName(idOrNameValue);
                importIdOrNameObject.setIdOrNameKey(idOrNameKey);

                dbVariable = variableService.selectVariableNodeWithNestedByVariableProperty(idOrNameValue, null, null, null, null, null, null, null);
            }
        } else if (ExportConstant.VARIABLE_ENTITY_ID.equals(idOrNameType)) {
            if (!StringUtils.isBlank(idOrNameValue)) {
                // 根据后缀调整值
                if (StringUtils.isNotBlank(suffix)) {
                    idOrNameValue = IdUtil.createId();
                }
                idOrNameKey = idOrNameValue;
                importIdOrNameObject.setIdOrName(idOrNameValue);
                importIdOrNameObject.setIdOrNameKey(idOrNameKey);

                dbVariable = variableService.selectVariableNodeWithNestedByVariableProperty(null, idOrNameValue, null, null, null, null, null, null);
            }
        } else {
            throw new Exception("参数调整参数时,传入的类型错误,类型:[" + type + "],属性类型:[" + idOrNameType + "],调整结果对象:[" + importAdjustObject + "]");
        }
        if (dbVariable != null && !dbVariable.isEmpty()) {
            result.addAll(dbVariable);
        }
        return result;
    }

    private String updateVariableNested(String type, ImportAdjustObject o, ImportParam importParam, ImportContext
            context) {
        String success = "0";
        VariableTreeNode variable = (VariableTreeNode) o.getToData();
        Set<String> variableNestedList = variable.getVariableNestedIdList();
        if (!CollectionUtil.isEmpty(variableNestedList)) {
            Set<String> toDateNewVariableNestedList = new HashSet<>(variableNestedList);
            for (String variableNestedId : variableNestedList) {
                ImportAdjustObject successDataModelHeader = getSuccessDataFromContext(ExportConstant.VARIABLE, variableNestedId, ExportConstant.VARIABLE, variable, importParam, context);
                if (successDataModelHeader == null) {
                    success = "-1";
                    o.setSuccess(success);
                    o.addExceptionMessage("导入实体类型参数失败:导入属性变量失败;");
                } else {
                    VariableTreeNode toData = (VariableTreeNode) successDataModelHeader.getToData();
                    toDateNewVariableNestedList.remove(variableNestedId);
                    toDateNewVariableNestedList.add(toData.getVariableId());
                }
            }
            variable.setVariableNestedIdList(toDateNewVariableNestedList);
            o.setToData(variable);
        }
        return success;
    }


    // ------------------------------save-------------------------------
    @Override
    public void dataPersistenceObjectRef(String type, ImportAdjustObject o, ImportContext context) throws Exception {
        VariableTreeNode fromDataVariable = (VariableTreeNode) o.getFromData();
        VariableTreeNode toDataVariable = (VariableTreeNode) o.getToData();
        String variableGroupId = fromDataVariable.getVariableGroupId();
        String folderId = fromDataVariable.getFolderId();
        if (StringUtils.isNotBlank(variableGroupId)) {
            ImportHelper.getInstance().saveObject(ExportConstant.VARIABLE_GROUP, variableGroupId, null, context);
        }
        if (StringUtils.isNotBlank(folderId)) {
            ImportHelper.getInstance().saveObject(ExportConstant.FOLDER, folderId, null, context);
        }
        Set<String> variableNestedIdList = fromDataVariable.getVariableNestedIdList();
        if (!CollectionUtil.isEmpty(variableNestedIdList)) {
            for (String nestedId : variableNestedIdList) {
                ImportHelper.getInstance().saveObject(ExportConstant.VARIABLE, nestedId, null, context);
            }
        }
    }

    @Override
    public void dataPersistenceObject(String type, Object o, ImportContext context) throws Exception {
        if ((getSupport().getValue()).equals(type)) {
            VariableTreeNode variable = (VariableTreeNode) o;
            variableService.instreVariableTreeNodeDataPersistence(variable);
        }
    }
}
