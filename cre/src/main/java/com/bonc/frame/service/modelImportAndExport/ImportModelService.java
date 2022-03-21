package com.bonc.frame.service.modelImportAndExport;

import com.bonc.frame.entity.modelImportAndExport.ImportAndExportOperateLog;
import com.bonc.frame.entity.modelImportAndExport.modelExport.ExportParam;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/4/7 2:56
 */
public interface ImportModelService {
    ImportAndExportOperateLog import_V1(String importFileString, ExportParam target, String modelImportStrategy) throws Exception;
}
