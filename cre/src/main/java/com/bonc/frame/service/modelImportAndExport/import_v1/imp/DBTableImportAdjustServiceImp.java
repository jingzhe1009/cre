package com.bonc.frame.service.modelImportAndExport.import_v1.imp;

import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelImport.ImportParam;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportIdOrNameCacheObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.importContext.ImportContext;
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
public class DBTableImportAdjustServiceImp extends AbstractImportAdjustServiceImp {
    @Override
    public ModelImportType getSupport() {
        return ModelImportType.PUB_DB_TABLE;
    }

    @Override
    public String updateReference(String type, ImportAdjustObject o, ImportParam importParam, ImportContext context) throws Exception {
        String success = "0";
        String updateDbTableRefDBSuccess = updateDbTableRefDB(type, o, importParam, context);
        if ("-1".equals(updateDbTableRefDBSuccess)) {
            success = "-1";
        }
        return success;
    }

    private String updateDbTableRefDB(String type, ImportAdjustObject o, ImportParam importParam, ImportContext context) {
        String success = "0";
        MetaDataTable toDataTable = (MetaDataTable) o.getToData();

        String toDataFileDbId = toDataTable.getDbId();
        if (!StringUtils.isBlank(toDataFileDbId)) {
            ImportAdjustObject successDataKpi = getSuccessDataFromContext(ExportConstant.DB, toDataFileDbId, ExportConstant.PUB_DB_TABLE, toDataTable, importParam, context);
            if (successDataKpi == null) {
                success = "-1";
            } else {
                DataSource toData = (DataSource) successDataKpi.getToData();
                String toDataApiId = toData.getDbId();
                toDataTable.setDbId(toDataApiId);
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
            success = checkAdjustObjectProperty(type, ExportConstant.PUB_DB_TABLE_CODE, true, true, importAdjustObject, suffix, toFolderId, importParam, context);
            suffix = getNextSuffix(suffix);
        } while (success == null || "-1".equals(success));

        return success;
    }

    @Override
    public String adjustOtherProperty(String type, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        String suffix = null;
        String success = null;
        do {
            success = checkAdjustObjectProperty(type, ExportConstant.PUB_DB_TABLE_NAME, false, true, importAdjustObject, suffix, toFolderId, importParam, context);
            suffix = getNextSuffix(suffix);
        } while (success == null || "-1".equals(success));

        // id
        suffix = null;
        success = null;
        do {
            success = checkAdjustObjectProperty(type, ExportConstant.PUB_DB_TABLE_ID, false, false, importAdjustObject, suffix, toFolderId, importParam, context);
            suffix = getNextSuffix(suffix);
        } while (success == null || "-1".equals(success));
        return importAdjustObject.getSuccess();
    }

    @Override
    public List<Object> updatePropertyAndgetDbObjectList(String type, ImportIdOrNameCacheObject importIdOrNameObject, ImportAdjustObject importAdjustObject, String toFolderId, ImportParam importParam, ImportContext context) throws Exception {
        MetaDataTable toData = (MetaDataTable) importAdjustObject.getToData();
        String dbId = toData.getDbId();

        String idOrNameType = importIdOrNameObject.getIdOrNameType();
        String idOrNameValue = importIdOrNameObject.getIdOrName();
        String idOrNameKey = importIdOrNameObject.getIdOrNameKey();
        String suffix = importIdOrNameObject.getSuffix();
        List<Object> result = new ArrayList<>();

        List<MetaDataTable> dbVariable = null;
        ImportIdOrNameCacheObject cacheneedSaveIdAndName = null;
        if (ExportConstant.PUB_DB_TABLE_CODE.equals(idOrNameType)) {
            if (!StringUtils.isBlank(idOrNameValue)) {

                if (StringUtils.isNotBlank(suffix)) {
                    idOrNameValue = idOrNameValue + suffix;
                }
                idOrNameKey = dbId + idOrNameValue;
                importIdOrNameObject.setIdOrName(idOrNameValue);
                importIdOrNameObject.setIdOrNameKey(idOrNameKey);

                dbVariable = dbMetaDataMgrService.selectTableByTableProperty(null, null, idOrNameValue);
            }
        } else if (ExportConstant.PUB_DB_TABLE_NAME.equals(idOrNameType)) {
            if (!StringUtils.isBlank(idOrNameValue)) {

                if (StringUtils.isNotBlank(suffix)) {
                    idOrNameValue = idOrNameValue + suffix;
                }
                idOrNameKey = dbId + idOrNameValue;
                importIdOrNameObject.setIdOrName(idOrNameValue);
                importIdOrNameObject.setIdOrNameKey(idOrNameKey);

                dbVariable = dbMetaDataMgrService.selectTableByTableProperty(null, idOrNameValue, null);
            }
        } else if (ExportConstant.PUB_DB_TABLE_ID.equals(idOrNameType)) {
            if (!StringUtils.isBlank(idOrNameValue)) {

                if (StringUtils.isNotBlank(suffix)) {
                    idOrNameValue = IdUtil.createId();
                }
                idOrNameKey = idOrNameValue;
                importIdOrNameObject.setIdOrName(idOrNameValue);
                importIdOrNameObject.setIdOrNameKey(idOrNameKey);

                dbVariable = dbMetaDataMgrService.selectTableByTableProperty(idOrNameValue, null, null);
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
    public void dataPersistenceObject(String type, Object o, ImportContext context) throws Exception {
        if ((getSupport().getValue()).equals(type)) {
            MetaDataTable metaDataTable = (MetaDataTable) o;
            dbMetaDataMgrService.insertTablePersistence(metaDataTable);
        }
    }

}
