package com.bonc.frame.service.modelImportAndExport.import_v1.imp;

import com.bonc.frame.entity.modelImportAndExport.modelExport.ExportParam;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelImport.ImportParam;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportIdOrNameCacheObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.importContext.ImportContext;
import com.bonc.frame.entity.rulefolder.RuleFolder;
import com.bonc.frame.service.modelImportAndExport.import_v1.ModelImportType;
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
public class FolderImportAdjustServiceImp extends AbstractImportAdjustServiceImp {

    @Override
    public ModelImportType getSupport() {
        return ModelImportType.FOLDER;
    }

    @Override
    public String updateReference(String type, ImportAdjustObject o, ImportParam importParam, ImportContext context) throws Exception {
        String success = "0";
        RuleFolder toData = (RuleFolder) o.getToData();
        ExportParam importTo = null;
        if (importParam != null) {
            importTo = importParam.getImportTo();
        }
        String ObjectFolderId = toData.getFolderId();
        if (!StringUtils.isBlank(ObjectFolderId)) { // 当这个对象的场景ID不等于null,说明他是私有的
            if (importTo != null) {
                String folderId = importTo.getFolderId();
                if (!StringUtils.isBlank(folderId)) {
                    RuleFolder toFolderDetail = ruleFolderService.getRuleFolderDetail(folderId, null);
                    if (toFolderDetail == null) {
                        throw new Exception("导入到的场景为null,类型:" + type + ",对象:" + o + ",导入参数:" + importParam);
                    }
                    toData = toFolderDetail;
                    o.setToData(toData);
                    o.setImportSuccessType(ExportConstant.importSuccessType_useSystemData);
                    success = "1";

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
    public String adjustKey(String type, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        String suffix = null;
        String success = null;
        do {
            success = checkAdjustObjectProperty(type, ExportConstant.FOLDER_NAME, true, true, importAdjustObject, suffix, toFolderId, importParam, context);
            suffix = getNextSuffix(suffix);
        } while (success == null || "-1".equals(success));

        return success;
    }

    @Override
    public String adjustOtherProperty(String type, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        // id
        String suffix = null;
        String success = null;
        do {
            success = checkAdjustObjectProperty(type, ExportConstant.FOLDER_ID, false, false, importAdjustObject, suffix, toFolderId, importParam, context);
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

        Object dbObject = null;
        ImportIdOrNameCacheObject cacheneedSaveIdAndName = null;
        if ((ExportConstant.FOLDER_NAME).equals(idOrNameType)) {
            if (!StringUtils.isBlank(idOrNameValue)) {
                if (StringUtils.isNotBlank(suffix)) {
                    idOrNameValue = idOrNameValue + suffix;
                }
                idOrNameKey = idOrNameValue;
                importIdOrNameObject.setIdOrName(idOrNameValue);
                importIdOrNameObject.setIdOrNameKey(idOrNameKey);

                dbObject = ruleFolderService.getRuleFolderDetail(null, idOrNameValue);

            }
        } else if ((ExportConstant.FOLDER_ID).equals(idOrNameType)) {
            if (!StringUtils.isBlank(idOrNameValue)) {
                if (StringUtils.isNotBlank(suffix)) {
                    idOrNameValue = IdUtil.createId();
                }
                idOrNameKey = idOrNameValue;
                importIdOrNameObject.setIdOrName(idOrNameValue);
                importIdOrNameObject.setIdOrNameKey(idOrNameKey);

                dbObject = ruleFolderService.getRuleFolderDetail(idOrNameValue, null);
            }
        } else {
            throw new Exception("参数调整参数时,传入的类型错误,类型:[" + type + "],属性类型:[" + idOrNameType + "],调整结果对象:[" + importAdjustObject + "]");
        }
        if (dbObject != null) {
            result.add(dbObject);
        }
        return result;
    }

    @Override
    public void dataPersistenceObject(String type, Object o, ImportContext context) throws Exception {
        if ((getSupport().getValue()).equals(type)) {
            RuleFolder kpiGroup = (RuleFolder) o;
            ruleService.insertRuleFolderDataPersistence(kpiGroup);
        }
    }


}
