package com.bonc.frame.service.modelImportAndExport.import_v1;

import com.bonc.frame.entity.modelImportAndExport.modelImport.ImportParam;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.importContext.ImportContext;
import com.bonc.frame.util.SpringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/31 17:33
 */
public class ImportHelper {
    private Map<String, ImportAdjustService> importAdjustServiceMap;
    private static ImportHelper instance;

    private ImportHelper() {
        Map<String, ImportAdjustService> importServiceBeanMap = SpringUtils.getBeansOfType(ImportAdjustService.class);
        importAdjustServiceMap = new HashMap<>();
        if (importServiceBeanMap != null && !importServiceBeanMap.isEmpty()) {
            for (ImportAdjustService fetchService : importServiceBeanMap.values()) {
                importAdjustServiceMap.put(fetchService.getSupport().getValue(), fetchService);
            }
        }
    }

    public static ImportHelper getInstance() {
        if (instance == null) {
            instance = new ImportHelper();
        }
        return instance;
    }

    public void importAdjusObject(String type, String id, ImportParam importParam, ImportContext context) throws Exception {
        ImportAdjustService importAdjustService = importAdjustServiceMap.get(type);
        if (importAdjustService != null) {
            importAdjustService.adjust(type, id, importParam, context);
        } else {
            throw new Exception("缺少类型" + type + "的service");
        }
    }

    // 返回值为   1:成功直接   0:成功继续    -1:失败
    public void saveObject(String type, String fileObjectId, ImportAdjustObject object, ImportContext context) throws Exception {
        ImportAdjustService importAdjustService = importAdjustServiceMap.get(type);
        if (importAdjustService != null) {
            importAdjustService.saveObject(type, fileObjectId, object, context);
        } else {
            throw new Exception("缺少类型" + type + "的service");
        }
    }

//    public void importOneVariable() {
//
//    }
}
