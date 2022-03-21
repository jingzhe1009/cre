package com.bonc.frame.service.modelImportAndExport;

import com.bonc.frame.entity.modelImportAndExport.ImportAndExportOperateLog;

import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/4/16 14:14
 */
public interface ImpoerAndExportLogService {
    /**
     * 分页获取导入导出日志的基本信息 ---- 不包括导入导出报告
     */
    Map<String, Object> selectImportAndExportOperateLogBaseInfoPage(String logId, String operateType, String success, String systemVersion, String fileName, String modelImportStrategy,
                                                                    String operatePerson, String startDate, String endDate, String ip,
                                                                    String start, String length);

    /**
     * 分页获取导入导出日志 ---- 包括导入导出报告
     */
    Map<String, Object> selectImportAndExportOperateLogWithContentPage(String logId, String operateType, String success, String systemVersion, String fileName, String modelImportStrategy,
                                                                       String operatePerson, String startDate, String endDate, String ip,
                                                                       String start, String length);

    ImportAndExportOperateLog selectOneImportAndExportOperateLogWithContent(String logId, String operateType, String success, String systemVersion, String fileName,
                                                                            String operatePerson, String startDate, String endDate, String ip);

    void insertImportAndExportOperateLog(ImportAndExportOperateLog importAndExportOperateLog);

}
