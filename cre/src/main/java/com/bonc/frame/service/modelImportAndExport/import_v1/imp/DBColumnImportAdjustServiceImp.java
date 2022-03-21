package com.bonc.frame.service.modelImportAndExport.import_v1.imp;

import com.bonc.frame.entity.metadata.MetaDataColumn;
import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelImport.ImportParam;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportIdOrNameCacheObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.importContext.ImportContext;
import com.bonc.frame.service.modelImportAndExport.import_v1.ImportHelper;
import com.bonc.frame.service.modelImportAndExport.import_v1.ModelImportType;
import com.bonc.frame.util.CollectionUtil;
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
public class DBColumnImportAdjustServiceImp extends AbstractImportAdjustServiceImp {
    @Override
    public ModelImportType getSupport() {
        return ModelImportType.DB_COLUNM;
    }

    @Override
    public String updateReference(String type, ImportAdjustObject o, ImportParam importParam, ImportContext context) throws Exception {
        String success = "0";
        String updateDbTableRefDBSuccess = updateDbColumnRefTable(type, o, importParam, context);
        if ("-1".equals(updateDbTableRefDBSuccess)) {
            success = "-1";
        }
        return success;
    }

    private String updateDbColumnRefTable(String type, ImportAdjustObject o, ImportParam importParam, ImportContext context) {
        String success = "0";
        MetaDataColumn toDataTable = (MetaDataColumn) o.getToData();

        String toDataFileDbId = toDataTable.getTableId();
        if (!StringUtils.isBlank(toDataFileDbId)) {
            ImportAdjustObject successDataKpi = getSuccessDataFromContext(ExportConstant.PUB_DB_TABLE, toDataFileDbId, ExportConstant.DB_COLUNM, toDataTable, importParam, context);
            if (successDataKpi == null) {
                success = "-1";
            } else {
                MetaDataTable toData = (MetaDataTable) successDataKpi.getToData();
                String toDataApiId = toData.getTableId();
                toDataTable.setTableId(toDataApiId);
            }
            o.setToData(toDataTable);
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
            success = checkAdjustObjectProperty(type, ExportConstant.DB_COLUNM_CODE, true, true, importAdjustObject, suffix, toFolderId, importParam, context);
            suffix = getNextSuffix(suffix);
        } while (success == null || "-1".equals(success));

        return success;
    }

    @Override
    public String adjustOtherProperty(String type, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        String suffix = null;
        String success = null;
        do {
            success = checkAdjustObjectProperty(type, ExportConstant.DB_COLUNM_NAME, false, true, importAdjustObject, suffix, toFolderId, importParam, context);
            suffix = getNextSuffix(suffix);
        } while (success == null || "-1".equals(success));

        // id
        suffix = null;
        success = null;
        do {
            success = checkAdjustObjectProperty(type, ExportConstant.DB_COLUNM_ID, false, false, importAdjustObject, suffix, toFolderId, importParam, context);
            suffix = getNextSuffix(suffix);
        } while (success == null || "-1".equals(success));
        return importAdjustObject.getSuccess();
    }

    @Override
    public List<Object> updatePropertyAndgetDbObjectList(String type, ImportIdOrNameCacheObject importIdOrNameObject, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        MetaDataColumn toData = (MetaDataColumn) importAdjustObject.getToData();
        String tableId = toData.getTableId();

        String idOrNameType = importIdOrNameObject.getIdOrNameType();
        String idOrNameValue = importIdOrNameObject.getIdOrName();
        String idOrNameKey = importIdOrNameObject.getIdOrNameKey();
        String suffix = importIdOrNameObject.getSuffix();
        List<Object> result = new ArrayList<>();

        // 通过获取数据库中的值, 获取缓存中的值
        List<MetaDataColumn> dbVariable = null;
        ImportIdOrNameCacheObject cacheneedSaveIdAndName = null;
        if (ExportConstant.DB_COLUNM_CODE.equals(idOrNameType)) {
            if (!StringUtils.isBlank(idOrNameValue)) {
                if (StringUtils.isNotBlank(suffix)) {
                    idOrNameValue = idOrNameValue + suffix;
                }
                idOrNameKey = tableId + idOrNameValue;
                importIdOrNameObject.setIdOrName(idOrNameValue);
                importIdOrNameObject.setIdOrNameKey(idOrNameKey);

                dbVariable = dbMetaDataMgrService.selectColumnInfoByProperty(null, null, idOrNameValue);
            }
        } else if (ExportConstant.DB_COLUNM_NAME.equals(idOrNameType)) {
            if (!StringUtils.isBlank(idOrNameValue)) {
                if (StringUtils.isNotBlank(suffix)) {
                    idOrNameValue = idOrNameValue + suffix;
                }
                idOrNameKey = tableId + idOrNameValue;
                importIdOrNameObject.setIdOrName(idOrNameValue);
                importIdOrNameObject.setIdOrNameKey(idOrNameKey);

                dbVariable = dbMetaDataMgrService.selectColumnInfoByProperty(null, idOrNameValue, null);

            }
        } else if (ExportConstant.DB_COLUNM_ID.equals(idOrNameType)) {
            if (!StringUtils.isBlank(idOrNameValue)) {
                if (StringUtils.isNotBlank(suffix)) {
                    idOrNameValue = IdUtil.createId();
                }
                idOrNameKey = idOrNameValue;
                importIdOrNameObject.setIdOrName(idOrNameValue);
                importIdOrNameObject.setIdOrNameKey(idOrNameKey);
                dbVariable = dbMetaDataMgrService.selectColumnInfoByProperty(idOrNameValue, null, null);

            }
        } else {
            throw new Exception("指标调整指标时,传入的类型错误,类型:[" + type + "],属性类型:[" + idOrNameType + "],调整结果对象:[" + importAdjustObject + "]");
        }

        if (!CollectionUtil.isEmpty(dbVariable)) {
            result.addAll(dbVariable);
        }
        return result;
    }

    @Override
    public void dataPersistenceObjectRef(String type, ImportAdjustObject o, ImportContext context) throws Exception {
        MetaDataColumn kpiDefinition = (MetaDataColumn) o.getFromData();
        String tableId = kpiDefinition.getTableId();
        if (!StringUtils.isEmpty(tableId)) {
            ImportHelper.getInstance().saveObject(ExportConstant.PUB_DB_TABLE, tableId, null, context);
        }
    }

    @Override
    public void dataPersistenceObject(String type, Object o, ImportContext context) throws Exception {
        if ((getSupport().getValue()).equals(type)) {
            MetaDataColumn metaDataTable = (MetaDataColumn) o;
            dbMetaDataMgrService.insertColumn(metaDataTable);
        }
    }

}
