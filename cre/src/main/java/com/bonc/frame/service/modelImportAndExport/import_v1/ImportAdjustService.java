package com.bonc.frame.service.modelImportAndExport.import_v1;

import com.bonc.frame.entity.modelImportAndExport.modelImport.ImportParam;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.importContext.ImportContext;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/6 15:18
 */
public interface ImportAdjustService {

    ModelImportType getSupport();

    void adjust(String type, String id, ImportParam importParam, ImportContext context) throws Exception;

    void saveObject(String type, String fileObjectId, ImportAdjustObject object, ImportContext context) throws Exception;

    void addExport() throws Exception;
}
