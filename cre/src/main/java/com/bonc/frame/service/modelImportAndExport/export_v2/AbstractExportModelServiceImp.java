package com.bonc.frame.service.modelImportAndExport.export_v2;

import com.alibaba.fastjson.JSON;
import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.commonresource.ApiGroup;
import com.bonc.frame.entity.commonresource.ModelGroup;
import com.bonc.frame.entity.commonresource.VariableGroup;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.kpi.KpiGroup;
import com.bonc.frame.entity.metadata.MetaDataColumn;
import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.entity.model.ModelContentInfo;
import com.bonc.frame.entity.modelImportAndExport.modelExport.ExportModelParam;
import com.bonc.frame.entity.modelImportAndExport.modelExport.ExportParam;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportResult;
import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.rulefolder.RuleFolder;
import com.bonc.frame.entity.variable.VariableTreeNode;
import com.bonc.frame.service.api.ApiService;
import com.bonc.frame.service.datasource.DataSourceService;
import com.bonc.frame.service.kpi.KpiService;
import com.bonc.frame.service.metadata.DBMetaDataMgrService;
import com.bonc.frame.service.modelBase.ModelBaseService;
import com.bonc.frame.service.modelImportAndExport.ExportModelService;
import com.bonc.frame.service.rule.RuleDetailService;
import com.bonc.frame.service.rule.RuleFolderService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.util.CollectionUtil;
import com.bonc.frame.util.ExportUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/1/10 9:24
 */
@Service
public class AbstractExportModelServiceImp implements ExportModelService {
    Log log = LogFactory.getLog(AbstractExportModelServiceImp.class);

    @Autowired
    private RuleFolderService ruleFolderService;

    @Autowired
    private RuleDetailService ruleDetailService;

    @Autowired
    private ModelBaseService modelBaseService;

    @Autowired
    private KpiService kpiService;

    @Autowired
    private ApiService apiService;

    @Autowired
    private VariableService variableService;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private DBMetaDataMgrService dbMetaDataMgrService;


    public ExportResult exportWithExportParam(ExportResult result, List<ExportParam> exportParams) {
        if (exportParams == null || exportParams.isEmpty()) {
            return null;
        }
        try {
            for (ExportParam exportParam : exportParams) {
                if (exportParam != null) {
                    if (result == null) {
                        result = new ExportResult();
                    }
                    String folderId = exportParam.getFolderId();
                    if (!StringUtils.isBlank(folderId) && !ExportConstant.FOLDER_PUB_ID.equals(folderId)) {
                        String type = exportParam.getType();
                        if (type != null && "all".equals(type)) {
                            addFolderAllByFolderId(result, folderId);
                        } else {
                            List<ExportModelParam> modelList = exportParam.getModelHeader();
                            if (modelList != null && !modelList.isEmpty()) {
                                exportWithExportModelParam(result, modelList);
                            }
                        }
                    }
                    String moderGroupId = exportParam.getModelGroupId();
                    if (!StringUtils.isBlank(moderGroupId)) {
                        String type = exportParam.getType();
                        if (type != null && "all".equals(type)) {
                            addModelGroupAll(result, moderGroupId);
                        } else {
                            List<ExportModelParam> modelList = exportParam.getModelHeader();
                            if (modelList != null && !modelList.isEmpty()) {
                                exportWithExportModelParam(result, modelList);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ExportResult exportWithExportModelParam(ExportResult result, List<ExportModelParam> exportModelParams) {
        if (exportModelParams == null || exportModelParams.isEmpty()) {
            return null;
        }
        try {
            for (ExportModelParam exportParam : exportModelParams) {
                if (exportParam != null) {
                    String ruleName = exportParam.getRuleName();
                    if (!StringUtils.isBlank(ruleName)) {
                        String type = exportParam.getType();
                        if (type != null && "all".equals(type)) {
                            addModelHeaderWithReferencesById(result, ruleName);
                        } else {
                            List<String> modelVersionIds = exportParam.getModelVersion();
                            if (modelVersionIds != null && !modelVersionIds.isEmpty()) {
                                addModelVersionWithReferencesByIdBatch(result, modelVersionIds);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ExportResult export(List<String> folderId, List<String> modelGroupIds, List<String> modelHeaderIds, List<String> modelIds) throws Exception {
        if (CollectionUtil.isEmpty(folderId) && CollectionUtil.isEmpty(modelGroupIds) && CollectionUtil.isEmpty(modelHeaderIds) && CollectionUtil.isEmpty(modelIds)) {
            throw new IllegalAccessException("参数为null");
        }
        ExportResult result = new ExportResult();
        if (!CollectionUtil.isEmpty(modelIds)) {
//            ExportResult result1 = exportByModelId(modelIds);
            addModelVersionWithReferencesByIdBatch(result, modelIds);
        }
        if (!CollectionUtil.isEmpty(modelHeaderIds)) {
            addModelHeaderWithReferencesByIdBatch(result, modelHeaderIds);
        }
        return result;
    }

    // -------------------- 整个系统----------------------------
    @Override
    public void addSystemAll(ExportResult result) throws Exception {
        List<RuleDetailWithBLOBs> versionWithBLOBSListSystemAll = ruleDetailService.getVersionWithBLOBSListSystemAll();
        if (versionWithBLOBSListSystemAll != null && !versionWithBLOBSListSystemAll.isEmpty()) {
            addModelVersionWithReferencesBatch(result, versionWithBLOBSListSystemAll);
        }
    }

    // ------------------- 场景 ----------------------------
    @Override
    public void addFolderAllByFolderId(ExportResult result, String folderId) throws Exception {
        if (StringUtils.isBlank(folderId)) {
            return;
        }
        RuleFolder ruleFolderDetail = ruleFolderService.getRuleFolderDetail(folderId, null);
        addFolderAll(result, ruleFolderDetail);

    }

    @Override
    public void addFolderAllByFolderIdBatch(ExportResult result, List<String> folderIds) throws Exception {
        if (!CollectionUtil.isEmpty(folderIds)) {
            for (String folderId : folderIds) {
                addFolderAllByFolderId(result, folderId);
            }
        }
    }


    public void addFolderAllByFolderName(ExportResult result, String folderName) throws Exception {
        if (StringUtils.isBlank(folderName)) {
            return;
        }
        RuleFolder ruleFolderDetail = ruleFolderService.getRuleFolderDetail(null, folderName);
        addFolderAll(result, ruleFolderDetail);

    }

    public void addFolderAllByFolderNameBatch(ExportResult result, List<String> folderNames) throws Exception {
        if (!CollectionUtil.isEmpty(folderNames)) {
            for (String folderName : folderNames) {
                addFolderAllByFolderName(result, folderName);
            }
        }
    }


    public void addFolderAll(ExportResult result, RuleFolder folder) throws Exception {
        if (result == null) {
            result = new ExportResult();
        }
        if (folder == null) {
            return;
        }
        String folderId = folder.getFolderId();
        result.add(ExportConstant.FOLDER, folder);

        //场景中所有的模型
        List<RuleDetailWithBLOBs> ruleList = ruleDetailService.getRuleDetailWhthBOLOBListByFolderId(folderId);
        if (ruleList != null) {
            for (RuleDetailWithBLOBs ruleDetailWithBLOBs : ruleList) {
                addModelVersionWithReferences(result, ruleDetailWithBLOBs);
            }
        }

        //添加接口
        List<ApiConf> allApiByFolderId = apiService.selectApiByProperty(null, null, null, null, folderId);
        if (!CollectionUtil.isEmpty(allApiByFolderId)) {
            result.putDataIdBatch(ExportConstant.API, new ArrayList<Object>(allApiByFolderId), ExportConstant.FOLDER, folder);
            for (ApiConf apiConf : allApiByFolderId) {
                addApi(result, apiConf);
            }
        }
        // 接口中的参数
        List<String> varIdFromApiList = ExportUtil.getVarIdFromApiList(result, allApiByFolderId);
        List<VariableTreeNode> variables = variableService.selectVariableTreeNodeWithNestedByVariableIdListy(varIdFromApiList);
        for (VariableTreeNode variable : variables) {
            addVariable(result, variable);
        }
        //场景中所有的参数


    }


    // ------------------------------------------ 模型组 ---------------------------------------
    @Override
    public void addModelGroupAll(ExportResult result, String modelGroupId) throws Exception {
        if (StringUtils.isBlank(modelGroupId)) {
            return;
        }
        if (result == null) {
            result = new ExportResult();
        }
        try {
            //模型组中所有的模型
            List<RuleDetailHeader> headerListByModelGroupId = ruleDetailService.getHeaderListByModelGroupId(modelGroupId);
            if (!CollectionUtil.isEmpty(headerListByModelGroupId)) {
                for (RuleDetailHeader ruleDetailHeader : headerListByModelGroupId) {
                    addModelHeaderWithReferences(result, ruleDetailHeader);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //----------------------------------------- 模型头 ---------------------------------
    @Override
    public void addModelHeaderWithReferencesById(ExportResult result, String modelHeaderId) throws Exception {
        if (result == null) {
            return;
        }
        List<RuleDetailWithBLOBs> versionList = ruleDetailService.getVersionWithBLOBSList(modelHeaderId);
        for (RuleDetailWithBLOBs rule : versionList) {
            if (rule == null) {
                continue;
            }
            addModelVersionWithReferences(result, rule);
        }
    }

    @Override
    public void addModelHeaderWithReferencesByIdBatch(ExportResult result, List<String> modelHeaderIds) throws Exception {
        if (result == null) {
            return;
        }
        if (!CollectionUtil.isEmpty(modelHeaderIds)) {
            for (String modelHeaderId : modelHeaderIds) {
                addModelHeaderWithReferencesById(result, modelHeaderId);
            }
        }
    }

    @Override
    public void addModelHeaderWithReferences(ExportResult result, RuleDetailHeader ruleDetailHeader) throws Exception {
        if (result == null) {
            return;
        }
        if (ruleDetailHeader != null) {
            List<RuleDetailWithBLOBs> versionList = ruleDetailService.getVersionWithBLOBSList(ruleDetailHeader.getRuleName());
            for (RuleDetailWithBLOBs rule : versionList) {
                if (rule == null) {
                    continue;
                }
                addModelVersionWithReferences(result, rule);
            }
        }
    }

    //-------------------------模型版本-------------------------------------
    public void addModelVersionWithReferencesById(ExportResult result, String ruleId) throws Exception {
        if (result == null) {
            return;
        }
        if (!result.containsKey(ExportConstant.MODEL_VERSION, ruleId)) {
            RuleDetailWithBLOBs rule = ruleDetailService.getRule(ruleId);
            if (rule != null) {
                addModelVersionWithReferences(result, rule);
            }
        }
    }

    @Override
    public void addModelVersionWithReferencesBatch(ExportResult result, List<RuleDetailWithBLOBs> modelVersionIds) throws Exception {
        if (result == null) {
            log.error("导出模型异常");
            return;
        }
        if (!CollectionUtil.isEmpty(modelVersionIds)) {
            for (RuleDetailWithBLOBs ruleDetailWithBLOBs : modelVersionIds) {
                if (!result.containsObject(ExportConstant.MODEL_VERSION, ruleDetailWithBLOBs)) {
                    addModelVersionWithReferences(result, ruleDetailWithBLOBs);
                }
            }
        }
    }

    @Override
    public void addModelVersionWithReferencesByIdBatch(ExportResult result, List<String> modelVersionIds) throws Exception {
        if (result == null) {
            log.error("导出模型异常");
            return;
        }
        if (!CollectionUtil.isEmpty(modelVersionIds)) {
            for (String modelVersionId : modelVersionIds) {
                if (!result.containsKey(ExportConstant.MODEL_VERSION, modelVersionId)) {
                    addModelVersionWithReferencesById(result, modelVersionId);
                }
            }
        }
    }

    @Override
    public void addModelVersionWithReferences(ExportResult result, RuleDetailWithBLOBs rule) throws Exception {
        log.debug("开始导出模型:" + JSON.toJSONString(rule));
        if (result == null) {
            return;
        }
        if (rule == null) {
            return;
        }
        String ruleId = rule.getRuleId();
        if (!result.containsKey(ExportConstant.MODEL_VERSION, ruleId)) {
            try {
                String ruleContent = rule.getRuleContent();
                ModelContentInfo modelContentInfo = ruleDetailService.getModelContentInfoExcudeRange(ruleContent);
                //模型引用的kpi
                List<String> kpiIdsFromModle = modelContentInfo.getKpiIdSet();
                result.putDataIdBatch(ExportConstant.KPI, new ArrayList<Object>(kpiIdsFromModle), ExportConstant.MODEL_VERSION, rule);
                List<String> apiIds = modelContentInfo.getApiIdSet();
                result.putDataIdBatch(ExportConstant.API, new ArrayList<Object>(apiIds), ExportConstant.MODEL_VERSION, rule);
                List<String> variableIds = modelContentInfo.getVariableIdSet();
                result.putDataIdBatch(ExportConstant.VARIABLE, new ArrayList<Object>(variableIds), ExportConstant.MODEL_VERSION, rule);

                List<KpiDefinition> kpis = kpiService.getKpiDetailWithLimiterBatch(kpiIdsFromModle);
                //指标引用的api
                Set<String> apiIdFromKpiList = ExportUtil.getApiIdFromKpiList(result, kpis);
                apiIds.addAll(apiIdFromKpiList);
                List<ApiConf> apiConfs = apiService.selectApiByApiIdList(apiIds);

                //接口引用的参数
                List<String> varIdFromApiList = ExportUtil.getVarIdFromApiList(result, apiConfs);
                //指标引用的参数
                Set<String> varIdFromKpiList = ExportUtil.getVarIdFromKpiList(result, kpis);
                variableIds.addAll(varIdFromApiList);
                variableIds.addAll(varIdFromKpiList);
                List<VariableTreeNode> variables = variableService.selectVariableTreeNodeWithNestedByVariableIdListy(variableIds);

                Set<String> dbIdsFromKpiList = ExportUtil.getDbIdsFromKpiList(result, kpis);
                List<DataSource> dataSources = dataSourceService.selectDataSourceByDBIdList(new ArrayList<String>(dbIdsFromKpiList));

                Set<String> pubTableIdsFromKpiList = ExportUtil.getPubTableIdsFromKpiList(result, kpis);
                List<MetaDataTable> metaDataTables = dbMetaDataMgrService.selectTablesByTableIdList(new ArrayList<String>(pubTableIdsFromKpiList));

                Set<String> columnIdsFromKpiList = ExportUtil.getColumnIdsFromKpiList(result, kpis);
                List<MetaDataColumn> metaDataColumns = dbMetaDataMgrService.selectColumnInfoBatch(new ArrayList<String>(columnIdsFromKpiList));

                //添加模型
                addModel(result, rule);
                //添加接口
                for (ApiConf apiConf : apiConfs) {
                    addApi(result, apiConf);
                }
                // 添加参数
                for (VariableTreeNode variable : variables) {
                    addVariable(result, variable);
                }
                //添加指标
                for (KpiDefinition kpiDefinition : kpis) {
                    addKpi(result, kpiDefinition);
                }
                for (DataSource dataSource : dataSources) {
                    result.add(ExportConstant.DB, dataSource);
                }
                for (MetaDataTable metaDataTable : metaDataTables) {
                    result.add(ExportConstant.PUB_DB_TABLE, metaDataTable);
                }
                for (MetaDataColumn metaDataColumn : metaDataColumns) {
                    result.add(ExportConstant.DB_COLUNM, metaDataColumn);
                }

            } catch (Exception e) {
                result.putlackData(ExportConstant.MODEL_VERSION, rule, null, null);
                e.printStackTrace();
            }
        }

    }

    private void addModel(ExportResult result, RuleDetailWithBLOBs rule) throws Exception {
        if (result == null) {
            result = new ExportResult();
        }
        if (rule == null) {
            return;
        }
        String isPublic = rule.getIsPublic();
        if ("0".equals(isPublic)) {
            String folderId = rule.getFolderId();
            if (!result.containsKey(ExportConstant.FOLDER, folderId)) {
                if (!StringUtils.isBlank(folderId)) {
                    RuleFolder ruleFolderDetail = ruleFolderService.getRuleFolderDetail(folderId, null);
                    result.add(ExportConstant.FOLDER, ruleFolderDetail);
                    result.putDataId(ExportConstant.FOLDER, ruleFolderDetail, ExportConstant.MODEL_HEADER, rule);
                }
            }
        } else {
            String modelGroupId = rule.getModelGroupId();
            if (!result.containsKey(ExportConstant.MODEL_GROUP, modelGroupId)) {
                if (!StringUtils.isBlank(modelGroupId)) {
                    ModelGroup modelGroupByModelId = modelBaseService.getModelGroupByModelId(modelGroupId);
                    result.add(ExportConstant.MODEL_GROUP, modelGroupByModelId);
                    result.putDataId(ExportConstant.MODEL_GROUP, modelGroupByModelId, ExportConstant.MODEL_HEADER, rule);
                    RuleFolder ruleFolderDetail = ruleFolderService.getRuleFolderDetail(ExportConstant.FOLDER_PUB_ID, null);
                    result.add(ExportConstant.FOLDER, ruleFolderDetail);
                }
            }
        }
        result.add(ExportConstant.MODEL_HEADER, rule);
        result.add(ExportConstant.MODEL_VERSION, rule);
    }

    private void addApi(ExportResult result, ApiConf apiConf) throws Exception {
        if (result == null || apiConf == null) {
            throw new Exception("");
        }
        String isPublic = apiConf.getIsPublic();
        if ("0".equals(isPublic)) {
            String folderId = apiConf.getFolderId();
            if (!result.containsKey(ExportConstant.FOLDER, folderId)) {
                if (!StringUtils.isBlank(folderId)) {
                    RuleFolder ruleFolderDetail = ruleFolderService.getRuleFolderDetail(folderId, null);
                    result.add(ExportConstant.FOLDER, ruleFolderDetail);
                    result.putDataId(ExportConstant.FOLDER, ruleFolderDetail, ExportConstant.API, apiConf);
                }
            }
        } else {
            String apiGroupId = apiConf.getApiGroupId();
            if (!result.containsKey(ExportConstant.API_GROUP, apiGroupId)) {
                if (!StringUtils.isBlank(apiGroupId)) {
                    List<ApiGroup> apiGroupList = apiService.getApiGroup(apiGroupId, null);
                    for (ApiGroup apiGroup : apiGroupList) {
                        result.add(ExportConstant.API_GROUP, apiGroup);
                        result.putDataId(ExportConstant.API_GROUP, apiGroup, ExportConstant.API, apiConf);
                    }
                }
            }
        }
        result.add(ExportConstant.API, apiConf);
    }

    private void addVariable(ExportResult result, VariableTreeNode variable) throws Exception {
        if (result == null || variable == null) {
            throw new Exception("");
        }
        String isPublic = variable.getIsPublic();
        if ("0".equals(isPublic)) {
            String folderId = variable.getFolderId();
            if (!result.containsKey(ExportConstant.FOLDER, folderId)) {
                if (!StringUtils.isBlank(folderId)) {
                    RuleFolder ruleFolderDetail = ruleFolderService.getRuleFolderDetail(folderId, null);
                    result.add(ExportConstant.FOLDER, ruleFolderDetail);
                    result.putDataId(ExportConstant.FOLDER, ruleFolderDetail, ExportConstant.VARIABLE, variable);

                }
            }
        } else {
            String variableGroupId = variable.getVariableGroupId();
            if (!result.containsKey(ExportConstant.VARIABLE_GROUP, variableGroupId)) {
                if (!StringUtils.isBlank(variableGroupId)) {
                    VariableGroup variableGroup = variableService.getVariableGroupById(variableGroupId, null);
                    result.add(ExportConstant.VARIABLE_GROUP, variableGroup);
                    result.putDataId(ExportConstant.VARIABLE_GROUP, variableGroup, ExportConstant.VARIABLE, variable);

                }
            }
        }
        result.add(ExportConstant.VARIABLE, variable);
    }

    private void addKpi(ExportResult result, KpiDefinition kpiDefinition) throws Exception {
        if (result == null || kpiDefinition == null) {
            throw new Exception("");
        }
        String kpiGroupId = kpiDefinition.getKpiGroupId();
        if (!result.containsKey(ExportConstant.KPI_GROUP, kpiGroupId)) {
            if (!StringUtils.isBlank(kpiGroupId)) {
                KpiGroup kpiGroup = kpiService.getKpiGroupByKpiGroupId(kpiGroupId);
                result.add(ExportConstant.KPI_GROUP, kpiGroup);
                result.putDataId(ExportConstant.KPI_GROUP, kpiGroup, ExportConstant.KPI, kpiDefinition);

            }
        }
        result.add(ExportConstant.KPI, kpiDefinition);

    }

//    public ModelInfo getModelInfoFromModelContent(String modelContent){
//
//    }


}
