package com.bonc.frame.service.modelImportAndExport;

import com.bonc.frame.entity.modelImportAndExport.modelExport.ExportModelParam;
import com.bonc.frame.entity.modelImportAndExport.modelExport.ExportParam;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportResult;
import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.rulefolder.RuleFolder;

import java.util.List;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/13 13:45
 */
public interface ExportModelService {

    ExportResult exportWithExportParam(ExportResult result, List<ExportParam> exportParams);

    ExportResult exportWithExportModelParam(ExportResult result, List<ExportModelParam> exportParams);

    ExportResult export(List<String> folderId, List<String> modelGroupIds, List<String> modelHeaderIds, List<String> modelIds) throws Exception;

    // -------------------- 整个系统----------------------------
    void addSystemAll(ExportResult result) throws Exception;

    // ------------------- 场景 ----------------------------

    /**
     * 导出场景中的所有模型,参数,接口
     */
    void addFolderAll(ExportResult result, RuleFolder folder) throws Exception;

    void addFolderAllByFolderId(ExportResult result, String folderId) throws Exception;

    void addFolderAllByFolderIdBatch(ExportResult result, List<String> folderId) throws Exception;

    void addFolderAllByFolderName(ExportResult result, String folderName) throws Exception;

    void addFolderAllByFolderNameBatch(ExportResult result, List<String> folderNames) throws Exception;


    // ------------------------------------------ 模型组 ---------------------------------------
    void addModelGroupAll(ExportResult result, String modelGroupId) throws Exception;

    //----------------------------------------- 模型头 ---------------------------------
    void addModelHeaderWithReferencesById(ExportResult result, String modelHeaderId) throws Exception;

    void addModelHeaderWithReferencesByIdBatch(ExportResult result, List<String> modelHeaderIds) throws Exception;

    void addModelHeaderWithReferences(ExportResult result, RuleDetailHeader ruleDetailHeader) throws Exception;

    //-------------------------模型版本-------------------------------------
    void addModelVersionWithReferencesById(ExportResult result, String ruleId) throws Exception;

    void addModelVersionWithReferencesByIdBatch(ExportResult result, List<String> modelVersionIds) throws Exception;

    void addModelVersionWithReferences(ExportResult result, RuleDetailWithBLOBs rule) throws Exception;

    void addModelVersionWithReferencesBatch(ExportResult result, List<RuleDetailWithBLOBs> modelVersionIds) throws Exception;


//    void addModel(ExportResult result, RuleDetailWithBLOBs rule) throws Exception;
//
//    void addApi(ExportResult result, ApiConf apiConf) throws Exception;
//
//    void addVariable(ExportResult result, Variable variable) throws Exception;
//
//    void addKpi(ExportResult result, KpiDefinition kpiDefinition) throws Exception;

}
