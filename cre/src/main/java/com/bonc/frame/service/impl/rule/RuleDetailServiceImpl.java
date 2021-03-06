package com.bonc.frame.service.impl.rule;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.dao.rule.RuleDetailMapper;
import com.bonc.frame.engine.EngineManager;
import com.bonc.frame.entity.auth.Channel;
import com.bonc.frame.entity.commonresource.ModelGroup;
import com.bonc.frame.entity.commonresource.ModelGroupInfo;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.model.*;
import com.bonc.frame.entity.modelCompare.entity.ModelOperateLog;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.rule.RuleDetail;
import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.variable.Variable;
import com.bonc.frame.entity.variable.reference.VariableRule;
import com.bonc.frame.module.lock.distributed.curator.CuratorMutexLock;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.service.aBTest.ABTestService;
import com.bonc.frame.service.api.ApiService;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.kpi.KpiService;
import com.bonc.frame.service.reference.RuleReferenceService;
import com.bonc.frame.service.rule.ModelOperateLogService;
import com.bonc.frame.service.rule.RuleDetailService;
import com.bonc.frame.service.rule.RuleFolderService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.util.*;
import com.bonc.framework.rule.exception.CompileException;
import com.bonc.framework.rule.executor.resolver.rule.RuleFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scala.util.parsing.combinator.testing.Str;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ????????????????????????service
 *
 * @author qxl
 * @version 1.0
 * @date 2018???3???27??? ??????4:36:53
 */
@Service("ruleDetailService")
public class RuleDetailServiceImpl implements RuleDetailService {

    private static final Log log = LogFactory.getLog(RuleDetailServiceImpl.class);

    private static final Pattern VAR_ID = Pattern.compile("varId\\\\\":\\\\\"\\[([\\w|\\d]+)\\]");
    private static final Pattern COMPARE_VAR_ID = Pattern.compile("valueType\\\\\":\\\\\"t_variable\\\\\",\\\\\"value\\\\\":\\\\\"\\[([\\w|\\d]+)\\]");
    private static final Pattern KPI_ID = Pattern.compile("kpiId\\\\\":\\\\\"\\[([\\w|\\d]+)\\]");
    private static final Pattern COMPARE_KPI_ID = Pattern.compile("valueType\\\\\":\\\\\"t_variable\\\\\",\\\\\"isKpi\\\\\":true,\\\\\"value\\\\\":\\\\\"\\[([\\w|\\d]+)\\]");
    private static final Pattern API_ID = Pattern.compile("ointerface\\\":\\{\\\"value\\\":\\\"([\\w|\\d]+)\\\"\\}");
    private static final Pattern RULE_ID = Pattern.compile("ruleSetId\\\":\\{\\\"value\\\":\\\"([\\w|\\d]+)\\\"\\}");
    //    private static final Pattern CONDITION_EXPRESSION = Pattern.compile("\\{\\\\\"(var|kpi)Id\\\\\":\\\\\"\\[([\\w\\d]+)\\]\\\\\",\\\\\"opt\\\\\":(.*?)\\\\\",\\\\\"valueType\\\\\":\\\\\"([\\w\\d]+)\\\\\"((,\\\\\"isKpi\\\\\":(true|false),)|,)\\\\\"value\\\\\":\\\\\"(.*?)\\\\\"((,\\\\\"references\\\\\":\\[([\\{\\\\\"(varId|kpiId)\\\\\":\\\\\"\\[([\\w\\d]+)\\]\\\\\"\\},]*?)\\{\\\\\"(varId|kpiId)\\\\\":\\\\\"\\[([\\w\\d]+)\\]\\\\\"\\}\\]\\})|(\\}))");//{\"varId\":\"[2c90fcb76ed903ba016ed9060fa90001]\"}
    private static final String CONDITION_PATTERN_STRING = "\\\"\\{\\\\\"(var|kpi)Id\\\\\":\\\\\"\\[([\\w\\d]+)\\]\\\\\",\\\\\"opt\\\\\":(.*?)\\\\\",\\\\\"valueType\\\\\":\\\\\"([\\w\\d]+)\\\\\"((,\\\\\"isKpi\\\\\":(true|false),)|,)\\\\\"value\\\\\":\\\\\"(.*?)\\\\\"((,\\\\\"references\\\\\":\\[((\\{\\\\\"(varId|kpiId)\\\\\":\\\\\"\\[([\\w\\d]+)\\]\\\\\"\\},)*?)\\{\\\\\"(var|kpi)Id\\\\\":\\\\\"\\[([\\w\\d]+)\\]\\\\\"\\}\\]\\}\\\")|(\\}\\\"))";
    private static final Pattern CONDITION_EXPRESSION = Pattern.compile(CONDITION_PATTERN_STRING);//{\"varId\":\"[2c90fcb76ed903ba016ed9060fa90001]\"}        Set<String> varIdSet = new HashSet<>();
    /**
     * ??????: ????????????LHS???json????????????????????????","
     */
    private static final String LHS_JSON_PATTERN_STRING = "\\{\\\"union\\\":\\\"(or|and)\\\",\\\"condition\\\":\\[((" + CONDITION_PATTERN_STRING + ",)*?)" + CONDITION_PATTERN_STRING + "\\]\\},";
    private static final Pattern LHS_JSON = Pattern.compile(LHS_JSON_PATTERN_STRING);
    /**
     * ??????: ????????????pathCdt???json?????????????????????"}"
     */
    String PATHCDT_VALUE_JSON_PATTERN_STRING = "\\{\\\"isElse\\\":(true|false),\\\"condition\\\":\\[((" + CONDITION_PATTERN_STRING + ",)*?)" + CONDITION_PATTERN_STRING + "\\],\\\"union\\\":\\\"(or|and)\\\"";
    Pattern PATHCDT_VALUE_JSON = Pattern.compile(PATHCDT_VALUE_JSON_PATTERN_STRING);

    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private RuleFolderService ruleFolderService;

//    @Autowired
//    private SysLogService sysLogService;

    @Autowired
    private VariableService variableService;

    @Autowired
    private ApiService apiService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private RuleReferenceService ruleReferenceService;
    @Autowired
    private ModelOperateLogService modelOperateLogService;

    @Autowired
    private KpiService kpiService;

    @Autowired
    private RuleDetailMapper ruleDetailMapper;

    @Autowired
    private ABTestService abTestService;

    private final String _MYBITSID_PREFIX = "com.bonc.frame.dao.rule.RuleDetailMapper.";

    private static final String _MODEL_GROUP_MAPPER = "com.bonc.frame.mapper.resource.ModelGroupMapper.";

    private static final String _RULE_DETAIL_MAPPER = "com.bonc.frame.mapper.rule.RuleDetailMapper.";

    /**
     * ??????-?????????????????????
     */
    private final String _VARIABLE_RULE_PREFIX = "com.bonc.frame.mapper.variable.VariableRuleMapper.";

    @Override
    public List<RuleDetailWithBLOBs> getVersionWithBLOBSListSystemAll() {
        Map<String, String> param = new HashMap<>(5);

        List<RuleDetailWithBLOBs> ruleDetails = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getVersionWithBLOBSList", param);
        return ruleDetails;
    }


    // TODO: ???????????????????????????????????????????????????

    @Override
    public ResponseResult getRuleContent(String ruleId) {
        if (ruleId == null || ruleId.isEmpty()) {
            return ResponseResult.createFailInfo("The ruleId is null.");
        }
        RuleDetailWithBLOBs ruleDetail = (RuleDetailWithBLOBs) daoHelper.queryOne(_MYBITSID_PREFIX + "selectWithBlobByPrimaryKey", ruleId);
        return ResponseResult.createSuccessInfo("success", ruleDetail.getRuleContent());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Deprecated
    public ResponseResult insertRule(RuleDetail rule, String data, String currentUser) {
        if (rule.getFolderId() == null || rule.getFolderId().isEmpty()) {
            return ResponseResult.createFailInfo("??????[folderId]????????????");
        }

        String moduleName = rule.getModuleName();
        if (StringUtils.isBlank(moduleName)) {
            return ResponseResult.createFailInfo("??????[moduleName]????????????");
        }
        if (checkModuleNameIsExist(moduleName, null)) {
            return ResponseResult.createFailInfo("??????????????????.");
        }

        RuleDetailWithBLOBs ruleDetail = new RuleDetailWithBLOBs();
        try {
            org.springframework.beans.BeanUtils.copyProperties(rule, ruleDetail);
        } catch (BeansException e) {
            throw new RuntimeException("????????????????????????");
        }

        final String ruleId = IdUtil.createId();
        ruleDetail.setIsLog("1");
        ruleDetail.setRuleName(ruleId);
        ruleDetail.setRuleStatus(ConstantUtil.RULE_STATUS_READY);
        ruleDetail.setIsDel(ConstantUtil.IS_DEL_NDEL);
        ruleDetail.setRuleContent(data);
        ruleDetail.setRuleId(ruleId);

        insertRuleToOfficialDataPersistence(ruleDetail, null);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertRuleToOfficialDataPersistence(RuleDetailWithBLOBs rule, String operateType) {
        if (rule != null) {
            String currentUser = ControllerUtil.getCurrentUser();
            Date date = new Date();
            if ("1".equals(rule.getIsHeader())) {
                rule.setCreateDate(date);
                rule.setCreatePerson(currentUser);

                rule.setUpdateDate(date);
                rule.setUpdatePerson(currentUser);
            } else {
                rule.setVersionCreateDate(date);
                rule.setVersionCreatePerson(currentUser);
                rule.setVersionUpdateDate(date);
                rule.setVersionUpdatePerson(currentUser);
            }
            //????????????
            daoHelper.insert(_MYBITSID_PREFIX + "insertSelective", rule);
            // ????????????
            String ruleContent = ((RuleDetailWithBLOBs) rule).getRuleContent();
            String ruleId = ((RuleDetailWithBLOBs) rule).getRuleId();
            // ????????????-???????????????????????????
            insertModelReference(ruleContent, ruleId);

            // ??????
            authorityService.autoGrantAuthToCurrentUser(rule.getRuleName(),
                    "0".equals(rule.getIsPublic()) ? ResourceType.DATA_MODEL : ResourceType.DATA_PUB_MODEL);

            modelOperateLogService.modeVersionAllInfoModelOperate(null, rule, null, operateType);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult updateRule(RuleDetail rule, String data) {
        final String ruleId = rule.getRuleId();
        if (ruleId == null || ruleId.isEmpty()) {
            return ResponseResult.createFailInfo("??????[ruleId]????????????");
        }

        String moduleName = rule.getModuleName();
        String ruleVersion = rule.getVersion();

        if (StringUtils.isBlank(ruleVersion)) { // ??????????????????
            if (checkModuleNameIsExist(moduleName, ruleId)) {
                return ResponseResult.createFailInfo("??????????????????.");
            }
        }

        RuleDetailWithBLOBs ruleDetail = new RuleDetailWithBLOBs();
        try {
            org.springframework.beans.BeanUtils.copyProperties(rule, ruleDetail);
        } catch (BeansException e) {
            throw new RuntimeException("????????????????????????");
        }
        ruleDetail.setRuleContent(data);

        // ??????
        final Set<String> newVarIdsFromModle = getVarIdsFromModle(data);
        final List<String> oldVarIdsFromModle = ruleReferenceService.selectVariableIdsByRuleId(ruleId);
        final List<String> needInsertVarIds = CollectionUtil.diffCollection(newVarIdsFromModle, oldVarIdsFromModle);
        ruleReferenceService.insertVariableRuleBatch(ruleId, needInsertVarIds);
        if (!CollectionUtil.isEmpty(oldVarIdsFromModle)) {
            List<String> needDeleteVarIds = ImmutableList.copyOf(oldVarIdsFromModle);
            ruleReferenceService.deleteVariableRuleBatch(ruleId, needDeleteVarIds);
        }

        // ??????
        final Set<String> newKpiIdsFromModle = getKpiIdsFromModle(data);
        final List<String> oldKpiIdsFromModle = ruleReferenceService.selectKpiIdsByRuleId(ruleId);
        final List<String> needInsertKpiIds = CollectionUtil.diffCollection(newKpiIdsFromModle, oldKpiIdsFromModle);
        ruleReferenceService.insertKpiRuleBatch(ruleId, needInsertKpiIds);
        if (!CollectionUtil.isEmpty(oldKpiIdsFromModle)) {
            List<String> needDeleteKpiIds = ImmutableList.copyOf(oldKpiIdsFromModle);
            ruleReferenceService.deleteBatchByRuleIdsKpiIds(ruleId, needDeleteKpiIds);
        }

        // ??????
        final Set<String> newApiIdsFromModle = getApiIdsFromModle(data);
        final List<String> oldApiIdsFromModle = ruleReferenceService.selectApiIdsByRuleId(ruleId);
        final List<String> needInsertApiIds = CollectionUtil.diffCollection(newApiIdsFromModle, oldApiIdsFromModle);
        ruleReferenceService.insertApiRuleBatch(ruleId, needInsertApiIds);
        if (!CollectionUtil.isEmpty(oldApiIdsFromModle)) {
            List<String> needDeleteApiIds = ImmutableList.copyOf(oldApiIdsFromModle);
            ruleReferenceService.deleteApiRuleBatch(ruleId, needDeleteApiIds);
        }

        // ?????????
        final Set<String> newRuleSetIdsFromModle = getRuleSetIdsFromModle(data);
        final List<String> oldRuleSetIdsFromModle = ruleReferenceService.selectRuleSetIdsByRuleId(ruleId);
        final List<String> needInsertRuleSetIds = CollectionUtil.diffCollection(newRuleSetIdsFromModle, oldRuleSetIdsFromModle);
        ruleReferenceService.insertRuleSetRuleBatch(ruleId, needInsertRuleSetIds);
        if (!CollectionUtil.isEmpty(oldRuleSetIdsFromModle)) {
            List<String> needDeleteRuleSetIds = ImmutableList.copyOf(oldRuleSetIdsFromModle);
            ruleReferenceService.deleteRuleSetRuleBatch(ruleId, needDeleteRuleSetIds);
        }

        daoHelper.update(_MYBITSID_PREFIX + "updateByPrimaryKeySelective", ruleDetail);
        return ResponseResult.createSuccessInfo();
    }


    @Override
    @Transactional
    public ResponseResult updateRuleStatus(String folderId, String ruleId, String status, String platformUpdateUserJobNumber) {
        if (ruleId == null || ruleId.isEmpty()) {
            return ResponseResult.createFailInfo("The ruleId is null.");
        }
        if (status == null || status.isEmpty()) {
            return ResponseResult.createFailInfo("The rule status is null.");
        }
        RuleDetailWithBLOBs oldModel = getRule(ruleId);
        RuleDetailWithBLOBs rule = new RuleDetailWithBLOBs();
        rule.setPlatformUpdateUserJobNumber(platformUpdateUserJobNumber);
        rule.setPlatformCreateUserJobNumber(oldModel.getPlatformCreateUserJobNumber());
        rule.setRuleId(ruleId);
        rule.setRuleStatus(status);
        rule.setUpdatePerson(ControllerUtil.getCurrentUser());
        rule.setUpdateDate(new Date());
        daoHelper.update(_MYBITSID_PREFIX + "updateByPrimaryKeySelective", rule);
        RuleDetailWithBLOBs newModel = getRule(ruleId);
        if (ConstantUtil.RULE_STATUS_RUNNING.equals(status)) {
            modelOperateLogService.modeVersionAllInfoModelOperate(oldModel, newModel, null, ModelOperateLog.ENABLE_TYPE);
        } else {
            modelOperateLogService.modeVersionAllInfoModelOperate(oldModel, newModel, null, ModelOperateLog.DIS_ENABLE_TYPE);

        }
        EngineManager.getInstance().cleanRule(folderId, ruleId);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult updateRuleCache(String folderId, String ruleId) {
        if (ruleId == null || ruleId.isEmpty()) {
            return ResponseResult.createFailInfo("The ruleId is null.");
        }
        EngineManager.getInstance().cleanRule(folderId, ruleId);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    //??????????????????
    public ResponseResult deleteRule(String ruleId, String platformUpdateUserJobNumber) {
        if (ruleId == null || ruleId.isEmpty()) {
            return ResponseResult.createFailInfo("The ruleId is null.");
        }
        if (!canModify(ruleId)) {
            return ResponseResult.createFailInfo("???????????????????????????????????????");
        }
        RuleDetailWithBLOBs deleteRuleDetailWithBLOBs = getRule(ruleId);
        if (deleteRuleDetailWithBLOBs != null) {
            deleteRuleDetailWithBLOBs.setRuleContent(null);
        }

        // ????????????????????????????????????????????????????????????
        deleteRuleReference(ruleId);

        RuleDetailWithBLOBs rule = new RuleDetailWithBLOBs();
        rule.setRuleId(ruleId);
        rule.setIsDel(ConstantUtil.IS_DEL_DEL);
        String currentUser = ControllerUtil.getCurrentUser();
        rule.setUpdatePerson(currentUser);
        Date date = new Date();
        rule.setUpdateDate(date);
        rule.setPlatformUpdateUserJobNumber(platformUpdateUserJobNumber);

        RuleDetail oldRule = (RuleDetail) daoHelper.queryOne(_MYBITSID_PREFIX +
                "selectRuleDetailByPrimaryKey", ruleId);
        if (null != oldRule) {//?????????
            daoHelper.update(_MYBITSID_PREFIX + "updateByPrimaryKeySelective", rule);
        } else {
            oldRule = (RuleDetail) daoHelper.queryOne(_MYBITSID_PREFIX +
                    "selectWithBlobByPrimaryKeyForDraft", ruleId);
            Map param = new HashMap();
            param.put("ruleName", oldRule.getRuleName());
            param.put("version", oldRule.getVersion().split("\\(??????\\)")[0]);
            param.put("updatePerson", currentUser);
            param.put("updateDate", date);
            daoHelper.update(_MYBITSID_PREFIX + "deleteDraftByRuld", param);
        }

        String ruleName = oldRule.getRuleName();
        if (isNeedDelHeader(ruleName, ruleId)) {
            this.deleteHeader(ruleName);

            // ????????????????????????????????????
            authorityService.deleteByResourceId(ruleName, "1".equals(oldRule.getIsPublic()) ?
                    ResourceType.DATA_PUB_MODEL.getType() : ResourceType.DATA_MODEL.getType());
        }

        final JSONObject info = new JSONObject();
        info.put("ruleId", ruleId);

        // ??????????????????
        modelOperateLogService.modeVersionAllInfoModelOperate(deleteRuleDetailWithBLOBs, null, null, ModelOperateLog.DELETE_VERSION_TYPE);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult deleteRuleByName(String ruleName, String platformUpdateUserJobNumber) {
        if (checkAnyVersionIsEnable(ruleName)) {
            return ResponseResult.createFailInfo("???????????????????????????????????????????????????");
        }

        List<RuleDetail> ruleDetails = (List<RuleDetail>) this.getVersions(ruleName, null, null).getData();
        if (!CollectionUtil.isEmpty(ruleDetails)) {
            for (RuleDetail ruleDetail : ruleDetails) {
                deleteRuleReference(ruleDetail.getRuleId());
            }
        }
        RuleDetailHeader deleteModelHeader = getOneHeader(ruleName);
        Map param = new HashMap();
        param.put("ruleName", ruleName);
        param.put("platformUpdateUserJobNumber", platformUpdateUserJobNumber);
        param.put("updatePerson", ControllerUtil.getCurrentUser());
        param.put("updateDate", new Date());

        this.daoHelper.update(_MYBITSID_PREFIX + "deleteForSingleRule", param);
        this.daoHelper.update(_MYBITSID_PREFIX + "deleteForSingleRuleForDraft", param);

        // ????????????
        if (deleteModelHeader != null) {
            authorityService.deleteByResourceId(ruleName, "1".equals(deleteModelHeader.getIsPublic()) ?
                    ResourceType.DATA_PUB_MODEL.getType() : ResourceType.DATA_MODEL.getType());
        }

        // ??????????????????
        modelOperateLogService.modelHeaderModelOperate(deleteModelHeader, null, null, ModelOperateLog.DELETE_MODEL_TYPE);
        return ResponseResult.createSuccessInfo();
    }


    // ????????????????????????????????????????????????????????????
    private void deleteRuleReference(String ruleId) {
        ruleReferenceService.deleteVariableRule(ruleId);
        ruleReferenceService.deleteKpiByRuleId(ruleId);
        ruleReferenceService.deleteApiRule(ruleId);
        ruleReferenceService.deleteRuleSetRule(ruleId);
    }

    //????????????????????????.true-???,false-???
    @Override
    public boolean canModify(String ruleId) {
        RuleDetail rule = (RuleDetail) daoHelper.queryOne(_MYBITSID_PREFIX + "selectByPrimaryKey", ruleId);
        if (rule != null && ConstantUtil.RULE_STATUS_RUNNING.equals(rule.getRuleStatus())) {
            return false;
        }
        return true;
    }

    //??????????????????????????????????????????
    @Override
    public List<RuleDetail> getRuleHeaderListByFolderId(String folderId) {
        /**
         * ???????????????????????????
         */
        Map<String, Object> param = new HashMap<>(2);
        param.put("folderId", folderId);
        param.put("needDraft", "1");
        List<RuleDetail> list = ruleDetailMapper.selectRuleDetailByFolderIdOrRuleName(param);
//        List<RuleDetail> list = daoHelper.queryForList(_MYBITSID_PREFIX + "selectRuleDetailByFolderIdOrRuleName", folderId);
        // ??????
        Map<String, List<RuleDetail>> stringListMap = sortRuleDetailByVersion(list);
        // ??????,????????????????????????????????????
        List<RuleDetail> result = new ArrayList<>();
        if (stringListMap != null && !stringListMap.isEmpty()) {
            for (List<RuleDetail> ruleDetailList : stringListMap.values()) {
                if (!CollectionUtil.isEmpty(ruleDetailList)) {
                    result.add(ruleDetailList.get(0));
                }
            }
        }
        return result;
    }

    /**
     * ?????? ruleName:????????????????????????
     *
     * @return ruleName:????????????????????????
     */
    public Map<String, List<RuleDetail>> sortRuleDetailByVersion(List<RuleDetail> needSortRuleDetailList) {
        Map<String, Map<String, RuleDetail>> setMap = new HashMap<>();
        if (CollectionUtil.isEmpty(needSortRuleDetailList)) {
            return new HashMap<>();
        }
        Comparator<String> versionComparator = new Comparator<String>() {
            /**
             *  o1?????????????????????
             *  ??????0: ?????????    ??????0:?????????   0:??????
             *   ??????   ?????????????????????
             *          ?????????????????????????????????
             *          ??????????????????????????? , ???????????????
             */
            @Override
            public int compare(String o1VersionString, String o2VersionString) {
                int exist = 0;
                int befor = -1;
                int after = 1;
                boolean o1VersionIsBlack = StringUtils.isBlank(o1VersionString);
                boolean o2VersionIsBlack = StringUtils.isBlank(o2VersionString);
                if (o1VersionIsBlack) {
                    return befor;
                }
                if (o2VersionIsBlack) {
                    return after;
                }
                if (o1VersionString.equals(o2VersionString)) {
                    return exist;
                }
                if (o1VersionString.endsWith("(??????)")) {
                    return befor;
                }
                if (o2VersionString.endsWith("(??????)")) {
                    return after;
                }
                boolean o1VersionIsNumber = StringUtil.stringIsNumber(o1VersionString);
                boolean o2VersionIsNumber = StringUtil.stringIsNumber(o2VersionString);
                if (!o1VersionIsNumber) {
                    return befor;
                }
                if (!o2VersionIsNumber) {
                    return after;
                }
                double v = Double.valueOf(o1VersionString) - Double.valueOf(o2VersionString);
                if (v > 0) {
                    return befor;
                } else {
                    return after;
                }
            }
        };
        for (RuleDetail ruleDetail : needSortRuleDetailList) {
            if (ruleDetail != null) {
                String ruleName = ruleDetail.getRuleName();
                if (StringUtils.isBlank(ruleName)) {
                    continue;
                }
                Map<String, RuleDetail> versionMap = setMap.get(ruleName);
                if (versionMap == null) {
                    versionMap = new TreeMap<>(versionComparator);
                    setMap.put(ruleName, versionMap);
                }
                String version = ruleDetail.getVersion();
                if (StringUtils.isNotBlank(version)) {
                    // 1.1-??????  --> 1.1  ????????????????????????1.1 , ????????????,???????????????
                    String versionDeleteDraft = version.replaceFirst("\\(??????\\)", "");
                    if (versionMap.containsKey(versionDeleteDraft)) {
                        continue;
                    }
                    // 1.1  --> 1.1-??????  ????????????1.1?????? , ??????1.1???????????????,????????????1.1??????
                    String versionADDDraft = version + "(??????)";
                    versionMap.remove(versionADDDraft);

                    versionMap.put(version, ruleDetail);
                }
            }
        }
        Map<String, List<RuleDetail>> result = new HashMap<>();
        for (String ruleName : setMap.keySet()) {
            Map<String, RuleDetail> versionRuleDetailMap = setMap.get(ruleName);
            if (versionRuleDetailMap != null && !versionRuleDetailMap.isEmpty()) {
                result.put(ruleName, new ArrayList<>(versionRuleDetailMap.values()));
            }
        }
        return result;
    }


    @Override
    public List<RuleDetailWithBLOBs> getRuleDetailWhthBOLOBListByFolderId(String folderId) {
        List<RuleDetailWithBLOBs> list = daoHelper.queryForList(_MYBITSID_PREFIX + "getRuleDetailWhthBOLOBListByFolderId", folderId);
        return list;
    }

    // ????????????????????????????????????????????????????????????
    @Override
    public Map<String, Object> pagedByRuleNameFolderNameRuleType(@Nullable String moduleName,
                                                                 @Nullable String ruleType,
                                                                 @Nullable String folderName,
                                                                 String start, String size) {
        Map<String, String> param = new HashMap<>();
        param.put("moduleName", moduleName);
        param.put("ruleType", ruleType);
        param.put("folderName", folderName);

        final Map<String, Object> result = daoHelper.queryForPageList(_MYBITSID_PREFIX +
                "pagedByRuleNameFolderNameRuleType", param, start, size);
        return result;
    }

    @Override
    public Map<String, Object> pagedBySubjectidAuthRuleNameFolderNameRuleType(@Nullable List<String> subjects,
                                                                              @Nullable String moduleName,
                                                                              @Nullable String ruleType,
                                                                              @Nullable String folderName,
                                                                              String start, String size) {
        Map<String, Object> param = new HashMap<>();
        param.put("subjects", subjects);
        param.put("moduleName", moduleName);
        param.put("ruleType", ruleType);
        param.put("folderName", folderName);

        final Map<String, Object> result = daoHelper.queryForPageList(_MYBITSID_PREFIX +
                "pagedBySubjectidAuthRuleNameFolderNameRuleType", param, start, size);
        return result;
    }

    @Override
    public List<Map<String, Object>> getRuleNameList(String folderId) {
        List<Map<String, Object>> list = daoHelper.queryForList(_MYBITSID_PREFIX + "selectRuleNameByFolder", folderId);
        return list;
    }

    @Override
    public List<Map<String, Object>> getRuleNameInHeaderList(String folderId) {
        List<Map<String, Object>> list = ruleDetailMapper.selectRuleNameInHeaderByFolder(folderId);
//        List<Map<String, Object>> list = daoHelper.queryForList(_MYBITSID_PREFIX + "selectRuleNameInHeaderByFolder", folderId);
        return list;
    }

    /**
     * ????????????????????????????????????
     * <p>
     * ??????????????????
     *
     * @param ruleName
     * @return
     */
    @Override
    public List<Map<String, Object>> getEnableVersionBaseInfoByRuleName(String ruleName) {
        List<Map<String, Object>> list = daoHelper.queryForList(_MYBITSID_PREFIX + "getEnableVersionBaseInfoByRuleName", ruleName);
        return list;
    }

    // ??????????????????????????????????????????????????????
    @Override
    public List<Map<String, Object>> getModelByProduct(String moduleName, String productCode, String productName,
                                                       String folderId, String ruleType) {
        Map<String, String> param = new HashMap<>(4);
        param.put("moduleName", moduleName);
        param.put("productCode", productCode);
        param.put("productName", productName);
        param.put("ruleType", ruleType);
        param.put("folderId", folderId);
        List<Map<String, Object>> list = daoHelper.queryForList(_MYBITSID_PREFIX + "getModelByProduct", param);
        return list;
    }

    // ????????????id?????????????????????
    @Override
    public List<Map<String, Object>> getVersionListByStatus(String ruleName, String ruleStatus) {
        Map<String, String> param = new HashMap<>(2);
        param.put("ruleName", ruleName);
        param.put("ruleStatus", ruleStatus);
        List<Map<String, Object>> list = daoHelper.queryForList(_MYBITSID_PREFIX + "getVersionListByStatus", param);
        return list;
    }


    @Override
    public List<RuleDetailWithBLOBs> getRuleBlobList(String folderId) {
        List<RuleDetailWithBLOBs> list = daoHelper.queryForList(_MYBITSID_PREFIX + "selectBlobsByFolderId", folderId);
        return list;
    }

    /**
     * ???????????????????????????
     *
     * @param ruleId
     * @return
     */
    @Override
    public boolean checkModuleNameIsExist(String moduleName, String ruleId) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("moduleName", moduleName);
        param.put("ruleId", ruleId);
        Integer obj = (Integer) daoHelper.queryOne(_MYBITSID_PREFIX + "getRuleCountByName", param);
        if (obj != null && obj > 0) {
            return true;
        }
        return false;
    }

    private boolean isNeedDelHeader(String ruleName, String ruleId) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("ruleName", ruleName);
        param.put("ruleId", ruleId);
        Integer obj = (Integer) daoHelper.queryOne(_MYBITSID_PREFIX + "isNeedDelHeader", param);
        if (obj != null && obj > 0) {
            return false;
        }
        return true;
    }

    /**
     * ???????????????????????????
     *
     * @param ruleId
     * @return
     */
    private boolean checkModuleNameIsExistForDraft(String moduleName, String ruleId) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("moduleName", moduleName);
        param.put("ruleId", ruleId);
        Integer obj = (Integer) daoHelper.queryOne(_MYBITSID_PREFIX + "getRuleCountByNameForDraft", param);
        if (obj != null && obj > 0) {
            return true;
        }
        return false;
    }

    @Override
    public RuleDetailWithBLOBs getRule(String ruleId) {
        RuleDetailWithBLOBs ruleDetail = getRuleFromOfficial(ruleId);
        if (ruleDetail == null) {
            ruleDetail = getRuleFromDraft(ruleId);
        }
        return ruleDetail;
    }

    /**
     * ????????????????????????
     *
     * @param ruleId
     * @return
     */
    @Override
    public RuleDetailWithBLOBs getRuleFromOfficial(String ruleId) {
        RuleDetailWithBLOBs ruleDetail = (RuleDetailWithBLOBs) daoHelper.queryOne(_MYBITSID_PREFIX +
                "selectWithBlobByPrimaryKey", ruleId);
        return ruleDetail;
    }

    /**
     * ????????????????????????
     *
     * @param ruleId
     * @return
     */
    @Override
    public RuleDetailWithBLOBs getRuleFromDraft(String ruleId) {
        RuleDetailWithBLOBs ruleDetail = (RuleDetailWithBLOBs) daoHelper.queryOne(_MYBITSID_PREFIX +
                "selectWithBlobByPrimaryKeyForDraft", ruleId);
        return ruleDetail;
    }

    @Override
    public List<RuleDetailWithBLOBs> getRuleDetailWithBLOBsByProperty(String ruleName, String ruleId, String version, boolean needDraft) {
        Map<String, Object> param = new HashMap<>();
        param.put("ruleName", ruleName);
        param.put("ruleId", ruleId);
        param.put("version", version);
        List<RuleDetailWithBLOBs> ruleDetail = daoHelper.queryForList(_MYBITSID_PREFIX +
                "selectWithBlobByProperty", param);
        if (needDraft) {
            List<RuleDetailWithBLOBs> ruleDetailDraft = daoHelper.queryForList(_MYBITSID_PREFIX +
                    "selectWithBlobByPropertyForDraft", param);
            ruleDetail.addAll(ruleDetailDraft);
        }
        return ruleDetail;
    }

    @Override
    public RuleDetail getOne(String ruleId) {
        RuleDetail ruleDetail = (RuleDetail) daoHelper.queryOne(_MYBITSID_PREFIX +
                "selectRuleDetailByPrimaryKey", ruleId);
        if (null == ruleDetail) {
            ruleDetail = (RuleDetail) daoHelper.queryOne(_MYBITSID_PREFIX +
                    "selectRuleDetailByPrimaryKeyForDraft", ruleId);
        }
        return ruleDetail;
    }

    /**
     * ??????????????????????????????????????????api?????????????????????????????????????????????????????????
     *
     * @param folderId
     * @return
     */
    @Override
    public ResponseResult checkCanModify(String id, String folderId) {
        if (id == null || id.isEmpty()) {
            return ResponseResult.createSuccessInfo();
        }
        List<RuleDetailWithBLOBs> list = daoHelper.queryForList(_MYBITSID_PREFIX + "selectBlobsByFolderId", folderId);
        if (list == null || list.size() == 0) {
            return ResponseResult.createSuccessInfo();
        }
        for (RuleDetailWithBLOBs ruleDetail : list) {
            String ruleContent = ruleDetail.getRuleContent();
            if (ruleContent == null || ruleContent.isEmpty() || !ConstantUtil.RULE_STATUS_RUNNING.equals(ruleDetail.getRuleStatus())) {
                continue;
            }
            int i = ruleContent.indexOf(id);
            if (i > 0) {
                return ResponseResult.createFailInfo("??????????????????" + ruleDetail.getRuleName() + "???????????????.");
            }
        }
        return ResponseResult.createSuccessInfo();
    }

    // ------------------------------ ???????????? ------------------------------

    // ??????????????????
    private ResponseResult addVariable(VariableRule variableRule) {
        daoHelper.insert(_VARIABLE_RULE_PREFIX + "insertSelective", variableRule);
        return ResponseResult.createSuccessInfo();
    }

    // ??????????????????????????????
    @Override
    @Transactional
    public ResponseResult addVariableOnFork(VariableRule variableRule) {
        variableRule.setRuleVariableType(VariableRule.FORK_VARAIBLE_CODE);
        return addVariable(variableRule);
    }

    // ?????????????????????????????????
    @Override
    @Transactional
    public ResponseResult addVariableOnRuleSet(VariableRule variableRule) {
        variableRule.setRuleVariableType(VariableRule.RULESET_VARAIBLE_CODE);
        return addVariable(variableRule);
    }

    // ??????????????????
    @Override
    @Transactional
    public ResponseResult updateVariable(String ruleId, String variableId, String oldVariableId) {
        Map<String, Object> param = ImmutableMap.<String, Object>of(
                "ruleId", ruleId,
                "variableId", variableId,
                "oldVariableId", oldVariableId);
        daoHelper.update(_VARIABLE_RULE_PREFIX + "updateVariableId", param);
        return ResponseResult.createSuccessInfo();
    }

    // ??????????????????
    @Override
    @Transactional
    public ResponseResult deleteVariable(String ruleId, String variableId) {
        Map<String, Object> param = ImmutableMap.<String, Object>of(
                "ruleId", ruleId,
                "variableId", variableId);
        daoHelper.delete(_VARIABLE_RULE_PREFIX + "deleteByVariableIdRuleId", param);
        return ResponseResult.createSuccessInfo();
    }

    // ???????????????json?????????????????????id
    public static Set<String> getVarIdsFromModle(String modle) {
        if (StringUtils.isBlank(modle)) {
            return Collections.emptySet();
        }
        Set<String> varIdSet = new HashSet<>();
        // ????????????
        final Matcher matcher = VAR_ID.matcher(modle);
        while (matcher.find()) {
            varIdSet.add(matcher.group(1));
        }

        // ????????????
        final Matcher matcher1 = COMPARE_VAR_ID.matcher(modle);
        while (matcher1.find()) {
            varIdSet.add(matcher1.group(1));
        }
        return varIdSet;
    }

    // ???????????????json?????????????????????id
    public static Set<String> getKpiIdsFromModle(String modle) {
        if (StringUtils.isBlank(modle)) {
            return Collections.emptySet();
        }
        Set<String> kpiIdSet = new HashSet<>();
        // ????????????
        final Matcher matcher = KPI_ID.matcher(modle);
        while (matcher.find()) {
            kpiIdSet.add(matcher.group(1));
        }

        // ????????????
        final Matcher matcher1 = COMPARE_KPI_ID.matcher(modle);
        while (matcher1.find()) {
            kpiIdSet.add(matcher1.group(1));
        }
        return kpiIdSet;
    }

    // ???????????????json?????????????????????id
    public static Set<String> getApiIdsFromModle(String modle) {
        if (StringUtils.isBlank(modle)) {
            return Collections.emptySet();
        }
        Set<String> apiIdSet = new HashSet<>();
        final Matcher matcher = API_ID.matcher(modle);
        while (matcher.find()) {
            apiIdSet.add(matcher.group(1));
        }
        return apiIdSet;
    }

    // ???????????????json???????????????????????????????????????????????????????????????
    public static Set<String> getRuleSetIdsFromModle(String modle) {
        if (StringUtils.isBlank(modle)) {
            return Collections.emptySet();
        }
        Set<String> ruleSetIdSet = new HashSet<>();
        final Matcher matcher = RULE_ID.matcher(modle);
        while (matcher.find()) {
            ruleSetIdSet.add(matcher.group(1));
        }
        return ruleSetIdSet;
    }

    public void insertModelReference(String modelContent, String ruleId) {
        //TODO: ??????????????????,??????????????????????????????,??????????????????????????????
        // ????????????-???????????????????????????
        if (StringUtils.isNotEmpty(modelContent)) {
            // ??????????????????????????????

//            final Set<String> varIdsFromModle = getVarIdsFromModle(modelContent);
//            final Set<String> kpiIdsFromModle = getKpiIdsFromModle(modelContent);
//            if (!varIdsFromModle.isEmpty()) {
//                final List<String> newVarIds = ImmutableList.copyOf(varIdsFromModle);
//                // ????????????-??????????????????
//                ruleReferenceService.insertVariableRuleBatch(ruleId, newVarIds);
//            }
//            if (!kpiIdsFromModle.isEmpty()) {
//                final List<String> newKpiIds = ImmutableList.copyOf(kpiIdsFromModle);
//                // ????????????-??????????????????
//                ruleReferenceService.insertKpiRuleBatch(ruleId, newKpiIds);
//            }
            ruleReferenceService.saveVariableRuleAndKpiRule(getModelContentInfo(modelContent), ruleId);

            // ?????????????????????????????????
            final Set<String> apiIdsFromModle = getApiIdsFromModle(modelContent);
            final Set<String> ruleSetIdsFromModle = getRuleSetIdsFromModle(modelContent);
            if (!apiIdsFromModle.isEmpty()) {
                final List<String> newApiIds = ImmutableList.copyOf(apiIdsFromModle);
                // ????????????-??????????????????
                ruleReferenceService.insertApiRuleBatch(ruleId, newApiIds);
            }
            if (!ruleSetIdsFromModle.isEmpty()) {
                final List<String> newRuleSetIds = ImmutableList.copyOf(ruleSetIdsFromModle);
                // ????????????-?????????????????????
                ruleReferenceService.insertRuleSetRuleBatch(ruleId, newRuleSetIds);
            }
        }
    }

    // ------------------------------ ????????? ??????????????? ------------------------------

    /**
     * ???????????????????????????
     * ??????????????????????????????
     *
     * @return
     */
    private boolean checkModuleNameIsExistByOldName(String moduleName, String oldModuleName) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("moduleName", moduleName);
        param.put("oldModuleName", oldModuleName);
        Integer obj = (Integer) daoHelper.queryOne(_MYBITSID_PREFIX + "countModuleNameByOld", param);
        if (obj != null && obj > 0) {
            return true;
        }
        return false;
    }

    /**
     * ????????????????????????????????????????????????
     * ???????????????????????????????????????????????????????????????????????????
     */
    @Override
    public boolean checkAnyVersionIsEnable(String ruleName) {
        int obj = (int) daoHelper.queryOne(_MYBITSID_PREFIX +
                "countEnableVersions", ruleName);
        if (obj > 0) {
            return true;
        }
        return false;
    }

    @Override
    public ResponseResult getOneHeaderInfo(String ruleName) {
        return ResponseResult.createSuccessInfo("", getOneHeader(ruleName));
    }

    private RuleDetailHeader getOneHeader(String ruleName) {
        if (StringUtils.isBlank(ruleName)) {
            throw new IllegalArgumentException("??????[ruleName]????????????");
        }
        RuleDetailHeader ruleDetailHeader = (RuleDetailHeader) daoHelper.queryOne(_MYBITSID_PREFIX +
                "getOneHeader", ruleName);
        return ruleDetailHeader;
    }

    /**
     * modelName???????????????
     *
     * @param moduleName
     * @return
     */
    @Override
    public List<RuleDetailHeader> getHeaderList(@Nullable String ruleName, String moduleName,
                                                @Nullable String ruleType,
                                                @Nullable String isPublic,
                                                @Nullable String folderId,
                                                @Nullable String modelGroupId,
                                                @Nullable String modelGroupName,
                                                @Nullable String deptId,
                                                @Nullable String deptName,
                                                @Nullable String partnerId,
                                                @Nullable String partnerName,
                                                @Nullable String productCode,
                                                @Nullable String productName,
                                                @Nullable String systemCode,
                                                @Nullable String systemName,
                                                @Nullable String platformCreateUserJobNumber,
                                                @Nullable String platformUpdateUserJobNumber,
                                                @Nullable String startDate,
                                                @Nullable String endDate) {
        Map<String, String> param = new HashMap<>(5);
        param.put("ruleName", ruleName);
        param.put("moduleName", moduleName);
        param.put("ruleType", ruleType);
        param.put("isPublic", isPublic);
        param.put("folderId", folderId);
        param.put("modelGroupId", modelGroupId);
        param.put("modelGroupName", modelGroupName);
        param.put("deptId", deptId);
        param.put("partnerName", partnerName);
        param.put("partnerId", partnerId);
        param.put("partnerName", partnerName);
        param.put("productCode", productCode);
        param.put("productName", productName);
        param.put("systemCode", systemCode);
        param.put("systemName", systemName);
        param.put("platformCreateUserJobNumber", platformCreateUserJobNumber);
        param.put("platformUpdateUserJobNumber", platformUpdateUserJobNumber);

        param.put("startDate", startDate);
        param.put("endDate", endDate);
        List<RuleDetailHeader> ruleDetailHeaders = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getHeaderList", param);
        return ruleDetailHeaders;
    }

    /**
     * modelName???????????????
     *
     * @param moduleName
     * @return
     */
    @Override
    public List<RuleDetailHeader> getModelHeaderListByProperty(@Nullable String ruleName, String moduleName,
                                                               @Nullable String isPublic,
                                                               @Nullable String folderId,
                                                               @Nullable String modelGroupId) {
        Map<String, String> param = new HashMap<>(5);
        param.put("ruleName", ruleName);
        param.put("moduleName", moduleName);
        param.put("isPublic", isPublic);
        param.put("folderId", folderId);
        param.put("modelGroupId", modelGroupId);

        List<RuleDetailHeader> ruleDetailHeaders = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getModelHeaderListByProperty", param);
        return ruleDetailHeaders;
    }

    @Override
    public List<RuleDetailHeader> getHeaderListByModelGroupId(String modelGroupId) {
        return getHeaderList(null, null, null, "1", null, modelGroupId, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }


    @Override
    public Map<String, Object> getHeaderList(@Nullable String ruleName, String moduleName,
                                             @Nullable String ruleType,
                                             @Nullable String isPublic,
                                             @Nullable String folderId,
                                             @Nullable String modelGroupId,
                                             @Nullable String modelGroupName,
                                             @Nullable String deptId,
                                             @Nullable String deptName,
                                             @Nullable String partnerId,
                                             @Nullable String partnerName,
                                             @Nullable String productCode,
                                             @Nullable String productName,
                                             @Nullable String systemCode,
                                             @Nullable String systemName,
                                             @Nullable String platformCreateUserJobNumber,
                                             @Nullable String platformUpdateUserJobNumber,
                                             @Nullable String startDate,
                                             @Nullable String endDate,
                                             String start, String length) {
        Map<String, String> param = new HashMap<>(5);
        param.put("ruleName", ruleName);
        param.put("moduleName", moduleName);
        param.put("ruleType", ruleType);
        param.put("isPublic", isPublic);
        param.put("folderId", folderId);
        param.put("modelGroupId", modelGroupId);
        param.put("modelGroupName", modelGroupName);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        param.put("deptId", deptId);
        param.put("deptName", deptName);
        param.put("partnerId", partnerId);
        param.put("partnerName", partnerName);
        param.put("productCode", productCode);
        param.put("productName", productName);
        param.put("systemCode", systemCode);
        param.put("systemName", systemName);
        param.put("platformCreateUserJobNumber", platformCreateUserJobNumber);
        param.put("platformUpdateUserJobNumber", platformUpdateUserJobNumber);
        return daoHelper.queryForPageList(_MYBITSID_PREFIX +
                "getHeaderList", param, start, length);
    }

    @Override
    public Map<String, Object> getHeaderListResource(@Nullable String moduleName,
                                                     @Nullable String ruleType,
                                                     @Nullable String modelGroupName,
                                                     @Nullable String startDate,
                                                     @Nullable String endDate,
                                                     String start, String length) {

        Map<String, String> param = new HashMap<>(5);
        param.put("moduleName", moduleName);
        param.put("ruleType", ruleType);
        param.put("modelGroupName", modelGroupName);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        Map<String, Object> results = daoHelper.queryForPageList(_MYBITSID_PREFIX +
                "getHeaderListResource", param, start, length);
        return results;
    }

    /**
     * ?????????????????????
     *
     * @param ruleDetailWithBLOBs
     * @param userId
     * @return
     */
    @Override
    @Transactional
    public RuleDetailWithBLOBs createHeader(RuleDetailWithBLOBs ruleDetailWithBLOBs, String userId) {
        // ??????????????????????????????
        if (checkModuleNameIsExistByOldName(ruleDetailWithBLOBs.getModuleName(), null)) {
            throw new IllegalStateException("?????????????????????");
        }


        RuleDetailWithBLOBs ruleDetailHeader = new RuleDetailWithBLOBs();//???????????????????????????
        final String uuid = IdUtil.createId();
        ruleDetailHeader.setFolderId(ruleDetailWithBLOBs.getFolderId());
        ruleDetailWithBLOBs.setRuleName(uuid);
        ruleDetailHeader.setRuleName(uuid);
        ruleDetailHeader.setVersionDesc(ruleDetailWithBLOBs.getVersionDesc());
        ruleDetailHeader.setModuleName(ruleDetailWithBLOBs.getModuleName());
        ruleDetailHeader.setRuleType(ruleDetailWithBLOBs.getRuleType());
        ruleDetailHeader.setModelGroupId(ruleDetailWithBLOBs.getModelGroupId());
        ruleDetailHeader.setModelGroupName(ruleDetailWithBLOBs.getModelGroupName());
        ruleDetailHeader.setRuleDesc(ruleDetailWithBLOBs.getRuleDesc());
        ruleDetailHeader.setDeptId(ruleDetailWithBLOBs.getDeptId());
        ruleDetailHeader.setDeptName(ruleDetailWithBLOBs.getDeptName());
        ruleDetailHeader.setPartnerCode(ruleDetailWithBLOBs.getPartnerCode());
        ruleDetailHeader.setPartnerName(ruleDetailWithBLOBs.getPartnerName());
        ruleDetailHeader.setProductCode(ruleDetailWithBLOBs.getProductCode());
        ruleDetailHeader.setProductName(ruleDetailWithBLOBs.getProductName());
        ruleDetailHeader.setSystemCode(ruleDetailWithBLOBs.getSystemCode());
        ruleDetailHeader.setSystemName(ruleDetailWithBLOBs.getSystemName());
        ruleDetailHeader.setIsPublic(ruleDetailWithBLOBs.getIsPublic());//chang
        ruleDetailHeader.setPlatformCreateUserJobNumber(ControllerUtil.getRealUserAccount());

        Date currentDate = new Date();
        ruleDetailHeader.setRuleId(uuid);
        ruleDetailHeader.setCreateDate(currentDate);
        ruleDetailHeader.setCreatePerson(userId);

        ruleDetailHeader.setIsDel(ConstantUtil.IS_DEL_NDEL);
        ruleDetailHeader.setIsLog("1");
        ruleDetailHeader.setRuleStatus(ConstantUtil.RULE_STATUS_READY);
        ruleDetailHeader.setVersionCreateDate(currentDate);
        ruleDetailHeader.setVersionCreatePerson(userId);

        ruleDetailHeader.setIsHeader(ConstantUtil.RULE_IS_HEADER);

        daoHelper.insert(_MYBITSID_PREFIX + "insertSelective", ruleDetailHeader);


        // ?????????????????????????????????????????????
        /*final Authority authority = new Authority();
        authority.setResourceId(ruleDetailHeader.getRuleName());
        authority.setResourceExpression("*");
        authorityService.grantToUser(authority, "0".equals(ruleDetailHeader.getIsPublic()) ? ResourceType.DATA_MODEL.getType() : ResourceType.DATA_PUB_MODEL.getType(), userId);*/
        authorityService.autoGrantAuthToCurrentUser(ruleDetailHeader.getRuleName(),
                "0".equals(ruleDetailHeader.getIsPublic()) ? ResourceType.DATA_MODEL : ResourceType.DATA_PUB_MODEL);

        return ruleDetailHeader;
    }


    /**
     * ????????????????????????????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult createHeaderAndVersion(RuleDetailWithBLOBs ruleDetailWithBLOBs, String userId) {

        //?????????????????????
        createHeader(ruleDetailWithBLOBs, userId);

        /**
         * ?????????????????????
         */
        final String uuid = IdUtil.createId();
        Date currentDate = new Date();
        ruleDetailWithBLOBs.setRuleId(uuid);
        ruleDetailWithBLOBs.setCreateDate(currentDate);
        ruleDetailWithBLOBs.setCreatePerson(userId);
        ruleDetailWithBLOBs.setIsPublic(ruleDetailWithBLOBs.getIsPublic());
        ruleDetailWithBLOBs.setIsDel(ConstantUtil.IS_DEL_NDEL);
        ruleDetailWithBLOBs.setIsLog("1");
        ruleDetailWithBLOBs.setRuleStatus(ConstantUtil.RULE_STATUS_READY);
        ruleDetailWithBLOBs.setVersionCreateDate(currentDate);
        ruleDetailWithBLOBs.setVersionCreatePerson(userId);
        ruleDetailWithBLOBs.setPlatformCreateUserJobNumber(ControllerUtil.getRealUserAccount());

        ruleDetailWithBLOBs.setIsHeader(ConstantUtil.RULE_IS_VERSION);
        daoHelper.insert(_MYBITSID_PREFIX + "insertSelective", ruleDetailWithBLOBs);

        return ResponseResult.createSuccessInfo("", uuid);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @CuratorMutexLock(value = {"moduleName"}, addLockCondition = "'#{#oldModuleName}'.equals('#{#ruleDetailHeader.moduleName}')")
    public ResponseResult updateHeader(RuleDetailHeader ruleDetailHeader, String oldRuleName, String oldModuleName, String userId) {
        if (ruleDetailHeader == null) {
            return ResponseResult.createFailInfo("????????????[ruleDetailHeader]????????????");
        }
        ruleDetailHeader.setRuleName(oldRuleName);
        RuleDetailHeader oldRuleDetailHeader = getOneHeader(oldRuleName);
        if (null == oldRuleDetailHeader) {
            return ResponseResult.createFailInfo("???????????????");
        }

        // ??????????????????????????????????????????????????????????????????
        String moduleName = ruleDetailHeader.getModuleName();
        if (StringUtils.isNotBlank(moduleName)) {
            if (checkModuleNameIsExistByOldName(moduleName, oldModuleName)) {
                return ResponseResult.createFailInfo("?????????????????????");
            }
        }

        ruleDetailHeader.setUpdateDate(new Date());
        ruleDetailHeader.setUpdatePerson(userId);
        ruleDetailHeader.setPlatformUpdateUserJobNumber(ControllerUtil.getRealUserAccount());

        Map map = null;
        try {
            map = MapBeanUtil.convertBean2Map(ruleDetailHeader);
        } catch (Exception e) {
            return ResponseResult.createFailInfo("????????????????????????");
        }
        map.put("oldRuleName", oldRuleName);
        daoHelper.update(_MYBITSID_PREFIX + "updateByRuleNameSelective", map);
        daoHelper.update(_MYBITSID_PREFIX + "updateByRuleNameSelectiveForDraft", map);

        //????????????????????? ruleName   ---- ?????????????????????id??? ruleName ?????? ??????ruleName??????,??????????????????????????? id
        // authorityService.updateRuleIdByOldResourceId(oldRuleName, ResourceType.DATA_PUB_MODEL.getType(), newRuleName);

        //????????????????????????
        RuleDetailHeader newRuleDetailHeader = getOneHeader(oldRuleName);
        modelOperateLogService.modelHeaderModelOperate(oldRuleDetailHeader, newRuleDetailHeader, null, ModelOperateLog.UPDATE_MODEL_HEADER_TYPE);

        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult deleteHeader(String ruleName) {
        if (StringUtils.isBlank(ruleName)) {
            return ResponseResult.createFailInfo("????????????[ruleName]????????????");
        }

        // ??????header
        Map param = new HashMap();
        param.put("ruleName", ruleName);
        param.put("platformUpdateUserJobNumber", ControllerUtil.getRealUserAccount());
        param.put("updatePerson", ControllerUtil.getCurrentUser());
        param.put("updateDate", new Date());

        daoHelper.update(_MYBITSID_PREFIX + "deleteByRuleName", param);
        return ResponseResult.createSuccessInfo();
    }

    // ------------------------ ????????? ???????????? ------------------------

    /**
     * ????????????????????????????????????
     *
     * @param ruleName
     * @return
     */
    private List<String> getVersionIdsByRuleName(String ruleName) {
        return daoHelper.queryForList(_MYBITSID_PREFIX +
                "getVersionIdsByRuleName", ruleName);
    }

    @Override
    public ResponseResult getVersionList(String ruleName,
                                         @Nullable String ruleStatus,
                                         @Nullable String isLog,
                                         @Nullable String startDate,
                                         @Nullable String endDate) {
        Map<String, String> param = new HashMap<>(5);
        param.put("ruleName", ruleName);
        param.put("ruleStatus", ruleStatus);
        param.put("isLog", isLog);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        List<RuleDetail> ruleDetails = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getVersionList", param);
        return ResponseResult.createSuccessInfo("", ruleDetails);
    }

    @Override
    public Map<String, Object> getVersionList(String ruleName,
                                              @Nullable String ruleStatus,
                                              @Nullable String isLog,
                                              @Nullable String startDate,
                                              @Nullable String endDate,
                                              String start, String length) {
        Map<String, String> param = new HashMap<>(5);
        param.put("ruleName", ruleName);
        param.put("ruleStatus", ruleStatus);
        param.put("isLog", isLog);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        return daoHelper.queryForPageList(_MYBITSID_PREFIX +
                "getVersionList", param, start, length);
    }

    @Override
    public List<RuleDetailWithBLOBs> getVersionWithBLOBSList(String ruleName) {
        Map<String, String> param = new HashMap<>(5);
        param.put("ruleName", ruleName);
        List<RuleDetailWithBLOBs> ruleDetails = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getVersionWithBLOBSList", param);
        return ruleDetails;
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param version
     * @param ruleName
     * @param ruleId
     * @return
     */
    @Override
    public boolean checkVersionIsExist(String version, String ruleName,
                                       @Nullable String ruleId) {
        Map<String, String> param = new HashMap<>(3);
        param.put("version", version);
        param.put("ruleName", ruleName);
        param.put("ruleId", ruleId);
        int obj = (int) daoHelper.queryOne(_MYBITSID_PREFIX +
                "countByVersionAndHeaderId", param);
        if (obj > 0) {
            return true;
        }
        return false;
    }

    // ???????????????????????????????????????????????????
    @Override
    public boolean checkEnable(String ruleId) {
        int obj = (int) daoHelper.queryOne(_MYBITSID_PREFIX +
                "checkEnable", ruleId);
        if (obj > 0) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public ResponseResult createVersion(String ruleName, String version,
                                        @Nullable String versionDesc,
                                        String data, String currentUser) {

        if (checkVersionIsExist(version, ruleName, null)) {
            return ResponseResult.createFailInfo("?????????????????????.");
        }

        // ??????????????????????????????????????????
        RuleDetailHeader exitsHeader = getOneHeader(ruleName);
        if (exitsHeader == null) {
            throw new IllegalStateException("?????????[" + ruleName + "]?????????????????????????????????");
        }

        RuleDetailWithBLOBs ruleDetail = new RuleDetailWithBLOBs();
        ruleDetail.setRuleName(exitsHeader.getRuleName());
        ruleDetail.setIsPublic(exitsHeader.getIsPublic());
        ruleDetail.setRuleDesc(exitsHeader.getRuleDesc());
        ruleDetail.setRuleType(exitsHeader.getRuleType());
        ruleDetail.setFolderId(exitsHeader.getFolderId());  // ?????????id
        ruleDetail.setModelGroupId(exitsHeader.getModelGroupId());  // ?????????id
        ruleDetail.setCreatePerson(exitsHeader.getCreatePerson());
        ruleDetail.setCreateDate(exitsHeader.getCreateDate());

        ruleDetail.setIsLog("1");
        ruleDetail.setRuleStatus(ConstantUtil.RULE_STATUS_READY);
        ruleDetail.setIsDel(ConstantUtil.IS_DEL_NDEL);
        ruleDetail.setVersionCreateDate(new Date());
        ruleDetail.setVersionCreatePerson(currentUser);
        ruleDetail.setRuleContent(data);
        ruleDetail.setVersion(version);
        ruleDetail.setVersionDesc(versionDesc);
        ruleDetail.setPlatformCreateUserJobNumber(ControllerUtil.getRealUserAccount());

        String ruleId = IdUtil.createId();
        ruleDetail.setRuleId(ruleId);

        daoHelper.insert(_MYBITSID_PREFIX + "insertSelective", ruleDetail);

        // ????????????-???????????????????????????
        insertModelReference(data, ruleId);

        return ResponseResult.createSuccessInfo();
    }

    /**
     * ????????????
     * ????????????????????????????????????
     * <p>
     * ???????????????????????????????????????????????????????????????
     * ???????????????
     * <p>
     * ?????????????????????
     *
     * @param headerInfo  ??????????????????????????????????????????????????????
     * @param oldFolderId ????????????????????????id
     * @param ruleId      ????????????id
     * @param version     ????????????????????????
     * @param versionDesc ???????????????????????????
     * @param userId      ????????????id
     */
    @Deprecated
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult publish(RuleDetailHeader headerInfo,
                                  String oldFolderId,
                                  String ruleId, String version,
                                  @Nullable String versionDesc,
                                  String userId) {
//        RuleDetailWithBLOBs oldRuleDetailWithBLOBs = getRule(ruleId);
        RuleDetailWithBLOBs ruleDetailWithBLOBs = getRule(ruleId);
        if (ruleDetailWithBLOBs == null) {
            throw new IllegalStateException("ruleId???[" + ruleId + "]??????????????????");
        }

        // ?????????????????????????????????????????????????????????
        List<String> variableIds = ruleReferenceService.selectVariableIdsByRuleId(ruleId);
        if (variableService.checkExitsPrivateVariable(variableIds, oldFolderId)) {
            throw new IllegalStateException("?????????????????????????????????????????????");
        }
        List<String> apiIds = ruleReferenceService.selectApiIdsByRuleId(ruleId);
        if (apiService.checkExitsPrivateApi(apiIds, oldFolderId)) {
            throw new IllegalStateException("?????????????????????????????????????????????");
        }

        String modelGroupId = headerInfo.getModelGroupId();
        String ruleName = headerInfo.getRuleName();
        Date currentDate = new Date();

        ruleDetailWithBLOBs.setRuleName(ruleName);
        ruleDetailWithBLOBs.setIsPublic(ConstantUtil.PUBLIC);

        // ???????????????id??????????????????????????????????????????????????????
        if (StringUtils.isNotEmpty(modelGroupId)) {
            if (checkModuleNameIsExistByOldName(ruleDetailWithBLOBs.getModuleName(), null)) {
                throw new IllegalStateException("?????????????????????");
            }

            String modelBaseId = ruleFolderService.getModelBaseId();    // ?????????id
            if (StringUtils.isBlank(modelBaseId)) {
                throw new IllegalStateException("??????????????????");
            }

            ruleDetailWithBLOBs.setRuleDesc(headerInfo.getRuleDesc());
            ruleDetailWithBLOBs.setRuleType(headerInfo.getRuleType());
            ruleDetailWithBLOBs.setFolderId(modelBaseId);  // ?????????id
            ruleDetailWithBLOBs.setModelGroupId(modelGroupId);  // ?????????id
            ruleDetailWithBLOBs.setCreatePerson(userId);
            ruleDetailWithBLOBs.setCreateDate(currentDate);
        } else {
            // ??????????????????????????????????????????
            RuleDetailHeader exitsHeader = getOneHeader(ruleName);
            if (exitsHeader == null) {
                throw new IllegalStateException("?????????[" + ruleName + "]?????????????????????????????????");
            }

            ruleDetailWithBLOBs.setRuleDesc(exitsHeader.getRuleDesc());
            ruleDetailWithBLOBs.setRuleType(exitsHeader.getRuleType());
            ruleDetailWithBLOBs.setFolderId(exitsHeader.getFolderId());  // ?????????id
            ruleDetailWithBLOBs.setModelGroupId(exitsHeader.getModelGroupId());  // ?????????id
            ruleDetailWithBLOBs.setCreatePerson(exitsHeader.getCreatePerson());
            ruleDetailWithBLOBs.setCreateDate(exitsHeader.getCreateDate());
        }

        // ????????????
        ruleDetailWithBLOBs.setIsLog("1");
        ruleDetailWithBLOBs.setIsDel(ConstantUtil.IS_DEL_NDEL);
        ruleDetailWithBLOBs.setRuleStatus(ConstantUtil.RULE_STATUS_READY);
        ruleDetailWithBLOBs.setVersion(version);
        ruleDetailWithBLOBs.setVersionDesc(versionDesc);
        ruleDetailWithBLOBs.setVersionCreatePerson(userId);
        ruleDetailWithBLOBs.setVersionCreateDate(currentDate);

        if (checkVersionIsExist(version, ruleName, null)) {
            throw new IllegalStateException("?????????????????????.");
        }

        daoHelper.update(_MYBITSID_PREFIX + "updateByPrimaryKeyForPublish", ruleDetailWithBLOBs);

        return ResponseResult.createSuccessInfo();
    }

    private void initDetailAndAuth(RuleDetailWithBLOBs ruleDetailWithBLOBs, String userId, String data, String version, String mapInterface, String ruleStatus) {

        //?????????
        final String ruleId = IdUtil.createId();
        ruleDetailWithBLOBs.setRuleId(ruleId);
        ruleDetailWithBLOBs.setIsHeader("0");
        ruleDetailWithBLOBs.setIsLog("1");
        ruleDetailWithBLOBs.setRuleStatus(ruleStatus);
        ruleDetailWithBLOBs.setIsDel(ConstantUtil.IS_DEL_NDEL);
        ruleDetailWithBLOBs.setRuleContent(data);
        ruleDetailWithBLOBs.setVersion(version);
        ruleDetailWithBLOBs.setVersionDesc(ruleDetailWithBLOBs.getVersionDesc());

        Date currentDate = new Date();
        ruleDetailWithBLOBs.setCreateDate(currentDate);
        ruleDetailWithBLOBs.setCreatePerson(userId);
        ruleDetailWithBLOBs.setVersionCreateDate(currentDate);
        ruleDetailWithBLOBs.setVersionCreatePerson(userId);

        ruleDetailWithBLOBs.setPlatformCreateUserJobNumber(ControllerUtil.getRealUserAccount());

        //????????????????????????
        daoHelper.insert(_MYBITSID_PREFIX + mapInterface, ruleDetailWithBLOBs);
    }


    private RuleDetailWithBLOBs mergeHeaderInfo(RuleDetail ruleDetail) {
        RuleDetailWithBLOBs ruleDetailWithBLOBs = new RuleDetailWithBLOBs();
        RuleDetailHeader ruleDetailHeader = (RuleDetailHeader) daoHelper.queryOne(_MYBITSID_PREFIX + "getOneHeader", ruleDetail.getRuleName());
        try {
            org.springframework.beans.BeanUtils.copyProperties(ruleDetailHeader, ruleDetailWithBLOBs);
            ruleDetailWithBLOBs.setRuleId(ruleDetail.getRuleId());
            ruleDetailWithBLOBs.setVersionDesc(ruleDetail.getVersionDesc());
            ruleDetailWithBLOBs.setIsPublic(ruleDetail.getIsPublic());

            ruleDetailWithBLOBs.setUpdateDate(new Date());
        } catch (BeansException e) {
            throw new RuntimeException("????????????????????????");
        }
        return ruleDetailWithBLOBs;
    }

    private Map afterLock(String oldRuleId, String data, String version,
                          RuleDetailWithBLOBs ruleDetailWithBLOBs,
                          String operateType, boolean isFirst) {
        RuleDetailWithBLOBs oldRuleDetailWithBLOBs = null;
        if (!StringUtils.isBlank(oldRuleId)) {
            oldRuleDetailWithBLOBs = getRule(oldRuleId);
        }

        // ????????????-???????????????????????????
        insertModelReference(data, ruleDetailWithBLOBs.getRuleId());

        // ?????????????????????????????????????????????
        if (isFirst) {
            authorityService.autoGrantAuthToCurrentUser(ruleDetailWithBLOBs.getRuleName(),
                    "0".equals(ruleDetailWithBLOBs.getIsPublic()) ? ResourceType.DATA_MODEL : ResourceType.DATA_PUB_MODEL);
        }

        // ??????????????????
        String oldVersion = null;
        if (oldRuleDetailWithBLOBs != null) {
            oldVersion = oldRuleDetailWithBLOBs.getVersion();
            if (oldVersion != null) {
                oldVersion = oldVersion.replace("(??????)", "");
                oldRuleDetailWithBLOBs.setVersion(oldVersion);
            }
        }
        if (version.equals(oldVersion)) {
            modelOperateLogService.modeVersionAllInfoModelOperate(oldRuleDetailWithBLOBs, ruleDetailWithBLOBs, null, operateType);
        } else {
            modelOperateLogService.modeVersionAllInfoModelOperate(null, ruleDetailWithBLOBs, oldRuleDetailWithBLOBs, operateType);
        }


        Map result = new HashMap();
        result.put("ruleId", ruleDetailWithBLOBs.getRuleId());
        result.put("version", ruleDetailWithBLOBs.getVersion());
        return result;
    }


    @Override
    @CuratorMutexLock(value = {"moduleName"})
    public Map stageOrCommitFirst(RuleDetail ruleDetail, String data,
                                  String userId, boolean isNoraml) throws Exception {
        RuleDetailWithBLOBs ruleDetailWithBLOBs = new RuleDetailWithBLOBs();//???????????????????????????
        if (!checkModuleNameIsExist(ruleDetail.getModuleName(), null)) {
            try {
                org.springframework.beans.BeanUtils.copyProperties(ruleDetail, ruleDetailWithBLOBs);
            } catch (BeansException e) {
                throw new RuntimeException("????????????????????????");
            }
            createHeader(ruleDetailWithBLOBs, userId);
            initDetailAndAuth(ruleDetailWithBLOBs, userId, data, "1.0", isNoraml ? "insertSelective" : "insertSelectiveForDraft", ConstantUtil.RULE_STATUS_READY);
            return afterLock(null, data, "1.0", ruleDetailWithBLOBs,
                    isNoraml ? ModelOperateLog.COMMIT_MODEL_TYPE : ModelOperateLog.STAGE_VERSION_TYPE, true);
        } else {
            return null;
        }
    }

    @Override
    @CuratorMutexLock(value = {"moduleName"})
    public ResponseResult cloneRuleWithFolder(RuleDetail ruleDetail, String loginUserId) throws Exception {

        String moduleName = ruleDetail.getModuleName();
        if (StringUtils.isBlank(moduleName)) {
            return ResponseResult.createFailInfo("??????[moduleName]????????????");
        }

        String errorMsg = null;
        String ruleId = "";
        RuleDetailWithBLOBs ruleDetailWithBLOBs = new RuleDetailWithBLOBs();//???????????????????????????
        RuleDetailWithBLOBs newRuleDetailWithBLOBs = null;
        RuleDetailWithBLOBs oldRule = null;
        if (!checkModuleNameIsExist(ruleDetail.getModuleName(), null)) {
            try {
                org.springframework.beans.BeanUtils.copyProperties(ruleDetail, ruleDetailWithBLOBs);
            } catch (BeansException e) {
                throw new RuntimeException("????????????????????????");
            }
            newRuleDetailWithBLOBs = createHeader(ruleDetailWithBLOBs, loginUserId);
            oldRule = getRule(ruleDetail.getRuleId());


            ruleId = IdUtil.createId();
            newRuleDetailWithBLOBs.setRuleId(ruleId);
            newRuleDetailWithBLOBs.setRuleContent(oldRule.getRuleContent());
            newRuleDetailWithBLOBs.setRuleIntercept(oldRule.getRuleIntercept());

            newRuleDetailWithBLOBs.setIsLog("1");
            newRuleDetailWithBLOBs.setRuleStatus(ConstantUtil.RULE_STATUS_READY);
            newRuleDetailWithBLOBs.setIsDel(ConstantUtil.IS_DEL_NDEL);
            newRuleDetailWithBLOBs.setVersion("1.0");//??????????????????
            newRuleDetailWithBLOBs.setIsHeader("0");

            Date currentDate = new Date();
            newRuleDetailWithBLOBs.setCreateDate(currentDate);
            newRuleDetailWithBLOBs.setCreatePerson(loginUserId);
            newRuleDetailWithBLOBs.setVersionCreateDate(currentDate);
            newRuleDetailWithBLOBs.setVersionCreatePerson(loginUserId);

            ruleDetailWithBLOBs.setPlatformCreateUserJobNumber(ControllerUtil.getRealUserAccount());

            daoHelper.insert(_MYBITSID_PREFIX + "insertSelective", newRuleDetailWithBLOBs);
        } else {
            errorMsg = "?????????????????????.";
        }

        if (null != errorMsg) {
            return ResponseResult.createFailInfo(errorMsg);
        } else {
            // ????????????-???????????????????????????
            insertModelReference(newRuleDetailWithBLOBs.getRuleContent(), ruleId);
            // ?????????????????????????????????????????????
            authorityService.autoGrantAuthToCurrentUser(ruleDetailWithBLOBs.getRuleName(),
                    "0".equals(ruleDetailWithBLOBs.getIsPublic()) ? ResourceType.DATA_MODEL : ResourceType.DATA_PUB_MODEL);

            modelOperateLogService.insertModelOperate(null, newRuleDetailWithBLOBs, oldRule, ModelOperateLog.CLONE_TYPE);
            return ResponseResult.createSuccessInfo();
        }
    }


    //???????????????
    @Override
    @CuratorMutexLock(value = {"ruleName"})
    public ResponseResult stageWithVersion(RuleDetail ruleDetail, String data,
                                           String userId) throws Exception {
        String oldRuleId = ruleDetail.getRuleId();
        RuleDetailWithBLOBs ruleDetailWithBLOBs = mergeHeaderInfo(ruleDetail);
        ruleDetailWithBLOBs.setUpdatePerson(userId);
        String version = generateVersion(ruleDetailWithBLOBs.getRuleName(), false, false);
        initDetailAndAuth(ruleDetailWithBLOBs, userId, data, version, "insertSelectiveForDraft", ConstantUtil.RULE_STATUS_READY);
        Map result = afterLock(oldRuleId, data, version, ruleDetailWithBLOBs, ModelOperateLog.STAGE_VERSION_TYPE, false);
        return ResponseResult.createSuccessInfo("????????????", result);
    }


    //???????????????
    @Override
    @CuratorMutexLock(value = {"ruleName"})
    public ResponseResult commitWithVersion(RuleDetail ruleDetail,
                                            String data,
                                            String userId) throws Exception {
        String oldRuleId = ruleDetail.getRuleId();
        RuleDetailWithBLOBs ruleDetailWithBLOBs = mergeHeaderInfo(ruleDetail);
        ruleDetailWithBLOBs.setUpdatePerson(userId);
        String version = generateVersion(ruleDetailWithBLOBs.getRuleName(), true, false);
        initDetailAndAuth(ruleDetailWithBLOBs, userId, data, version, "insertSelective", ConstantUtil.RULE_STATUS_READY);
        Map result = afterLock(oldRuleId, data, version, ruleDetailWithBLOBs, ModelOperateLog.COMMIT_MODEL_TYPE, false);
        return ResponseResult.createSuccessInfo("????????????", result);
    }

    //???????????????
    @Override
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult publishWithVersion(RuleDetail ruleDetail, String data,
                                             String userId) {
        RuleDetailWithBLOBs ruleDetailWithBLOBs = mergeHeaderInfo(ruleDetail);
        ruleDetailWithBLOBs.setUpdatePerson(userId);
        String ruleName = ruleDetailWithBLOBs.getRuleName();
        String version = generateVersion(ruleName, true, false);

        ruleDetailWithBLOBs.setPlatformUpdateUserJobNumber(ControllerUtil.getRealUserAccount());
        initDetailAndAuth(ruleDetailWithBLOBs, userId, data, version, "insertSelective", ConstantUtil.RULE_STATUS_RUNNING);

        //???????????????????????????????????????????????????????????????
        Map<String, String> param = new HashMap<>();
        param.put("ruleName", ruleName);
        param.put("ruleId", ruleDetailWithBLOBs.getRuleId());
        daoHelper.update(_MYBITSID_PREFIX + "updateStatusToNotBegin", param);

        return ResponseResult.createSuccessInfo();

    }

    //????????????????????????,?????????????????????
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult getVersions(String ruleName, String folderId,
                                      @Nullable String isPublic) {

        /**
         * ???????????????????????????
         */
        Map<String, Object> param = new HashMap<>(2);
        param.put("folderId", folderId);
        param.put("ruleName", ruleName);
        param.put("isPublic", isPublic);
        List<RuleDetail> list = ruleDetailMapper.selectRuleDetailByFolderIdOrRuleName(param);
//        List<RuleDetail> list = daoHelper.queryForList(_MYBITSID_PREFIX + "selectRuleDetailByFolderIdOrRuleName", folderId);
        // ??????
        Map<String, List<RuleDetail>> stringListMap = sortRuleDetailByVersion(list);
        // ??????,????????????????????????????????????
        List<RuleDetail> result = new ArrayList<>();
        for (List<RuleDetail> ruleDetailList : stringListMap.values()) {
            if (!CollectionUtil.isEmpty(ruleDetailList)) {
                result.addAll(ruleDetailList);
            }
        }
        return ResponseResult.createSuccessInfo("????????????", result);
    }

    //????????????????????????,?????????????????????
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult getAllVersions(String ruleName, String folderId,
                                         @Nullable String isPublic, String needDraft) {

        /**
         * ???????????????????????????
         */
        Map<String, Object> param = new HashMap<>(2);
        param.put("folderId", folderId);
        param.put("ruleName", ruleName);
        param.put("isPublic", isPublic);
        param.put("needDraft", needDraft);
        List<RuleDetail> list = ruleDetailMapper.selectAllVersionRuleDetailByFolderIdOrRuleName(param);
//        List<RuleDetail> list = daoHelper.queryForList(_MYBITSID_PREFIX + "selectRuleDetailByFolderIdOrRuleName", folderId);
//        // ??????
//        Map<String, List<RuleDetail>> stringListMap = sortRuleDetailByVersion(list);
//        // ??????,????????????????????????????????????
//        List<RuleDetail> result = new ArrayList<>();
//        for (List<RuleDetail> ruleDetailList : stringListMap.values()) {
//            if (!CollectionUtil.isEmpty(ruleDetailList)) {
//                result.addAll(ruleDetailList);
//            }
//        }
        return ResponseResult.createSuccessInfo("????????????", list);
    }

    //???????????????????????????
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> getVersionsForPageList(String ruleName, String ruleStatus,
                                                      @Nullable String startDate,
                                                      @Nullable String endDate,
                                                      String isPublic, String start, String length) {
        Map<String, String> param = new HashMap<>();
        param.put("ruleName", ruleName);
        param.put("isDraft", "0");
        param.put("isPublic", isPublic);
        param.put("ruleStatus", ruleStatus);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        Map<String, Object> ruleDetailsBase = daoHelper.queryForPageList(_MYBITSID_PREFIX +
                "getVersionList2", param, start, length);

        List<RuleDetail> ruleDetailsBaseAll = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getVersionList2", param);
        param.put("isDraft", "1");
        List<RuleDetail> ruleDetailDraftList = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getVersionList2", param);
        if (ruleDetailsBaseAll.size() < 1) {//?????????????????????????????????
            ruleDetailsBase = daoHelper.queryForPageList(_MYBITSID_PREFIX +
                    "getNewestForDraft", param, start, length);
            // return
        } else if (ruleDetailDraftList.size() < 1) {//?????????
            //  return ResponseResult.createSuccessInfo("??????",ruleDetailsBase);
        } else {
            RuleDetail ruleDetailsDraft = ruleDetailDraftList.get(0);
            param.put("ruleId", ruleDetailsDraft.getRuleId());
            if (ruleDetailsDraft.getVersionCreateDate().getTime() > ruleDetailsBaseAll.get(0).getVersionCreateDate().getTime()) {//???????????????????????????
                ruleDetailsBase = daoHelper.queryForPageList(_MYBITSID_PREFIX +
                        "getVersionList2Union", param, start, length);
            }
        }

        return ruleDetailsBase;
    }

    @Override
    public ResponseResult changeStatus(String ruleId, String platformUpdateUserJobNumber) {
        RuleDetailWithBLOBs ruleDetail = (RuleDetailWithBLOBs) daoHelper.queryOne(_MYBITSID_PREFIX + "selectWithBlobByPrimaryKey", ruleId);

        if (ruleDetail == null) {
            return ResponseResult.createFailInfo("??????????????????????????????");
        }
        String ruleName = ruleDetail.getRuleName();

        // ??????????????????????????????????????????
        List<RuleDetail> enableRules = daoHelper.queryForList(_MYBITSID_PREFIX + "selectEnableRuleDetail", ruleName);
        if (CollectionUtil.isEmpty(enableRules)) {
            return ResponseResult.createFailInfo("??????????????????????????????????????????");
        } else if (enableRules.size() != 1) {
            return ResponseResult.createFailInfo("?????????????????????????????????????????????");
        }
        RuleDetail enableRule = enableRules.get(0);

        Map<String, Object> param = new HashMap<>();
        param.put("ruleName", ruleName);
        param.put("ruleId", ruleDetail.getRuleId());
        param.put("updatePerson", ControllerUtil.getCurrentUser());
        param.put("updateDate", new Date());
        param.put("platformUpdateUserJobNumber", platformUpdateUserJobNumber);
        daoHelper.update(_MYBITSID_PREFIX + "updateStatusToNotBegin", param);

        daoHelper.update(_MYBITSID_PREFIX + "updateStatusToExecution", param);

        try {
            abTestService.switchRule(enableRule.getRuleId(), ruleId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        RuleDetailWithBLOBs new_ruleDetail = (RuleDetailWithBLOBs) daoHelper.queryOne(_MYBITSID_PREFIX + "selectWithBlobByPrimaryKey", ruleId);

        modelOperateLogService.insertModelOperate(ruleDetail, new_ruleDetail, null, ModelOperateLog.PUBLISH_TYPE);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    public ResponseResult checkPreVersionStatus(String ruleId) {
        RuleDetailWithBLOBs ruleDetail = (RuleDetailWithBLOBs) daoHelper.queryOne(_MYBITSID_PREFIX + "selectWithBlobByPrimaryKey", ruleId);
        Map<String, String> param = new HashMap<>();
        param.put("ruleName", ruleDetail.getRuleName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        param.put("versionCreateDate", sdf.format(ruleDetail.getVersionCreateDate()));
        int obj = (int) daoHelper.queryOne(_MYBITSID_PREFIX +
                "checkPreVersionEnable", param);
        return ResponseResult.createSuccessInfo("", obj);
    }

    /**
     * ?????????null ?????? ??????????????? ?????? 1.0
     * ??????????????? ??????
     *
     * @param isNormal         ?????????????????????????????????
     * @param isMaxVersionAdd1 ?????????????????????+1
     * @return
     */
    public String generateVersion(String ruleName, boolean isNormal, boolean isMaxVersionAdd1) {
        Map<String, String> param = new HashMap<>();
        param.put("ruleName", ruleName);
        param.put("isDraft", "0");
        // ?????????????????????????????? , ?????????????????????????????????????????? +1
        if (isMaxVersionAdd1) {
            double maxVersionNum = getMaxVersionNum(ruleName);
            if (maxVersionNum == 0.99) {
                return "1.0";
            }
            BigDecimal big100 = BigDecimal.valueOf(100f);
            return String.valueOf(BigDecimal.valueOf(maxVersionNum).multiply(big100).add(BigDecimal.valueOf(1f)).divide(big100));
        }
        List<RuleDetail> ruleDetails = daoHelper.queryForList(_MYBITSID_PREFIX + "getMaxVersion", param);
        if (ruleDetails != null && ruleDetails.size() >= 1) {//??????????????????
            RuleDetail ruleDetail = ruleDetails.get(0);
            BigDecimal big100 = BigDecimal.valueOf(100f);
            return String.valueOf(BigDecimal.valueOf(Double.parseDouble(ruleDetail.getVersion())).multiply(big100).add(BigDecimal.valueOf(1f)).divide(big100));
        } else {
            if (isNormal) {
                return "1.0";
            } else {
                param.put("isDraft", "1");
                List<RuleDetail> draftRules = daoHelper.queryForList(_MYBITSID_PREFIX + "getMaxVersion", param);
                if (draftRules != null && draftRules.size() >= 1) {
                    return draftRules.get(0).getVersion();
                } else {
                    return "1.0";
                }
            }
        }
    }

    /**
     * ?????????????????????
     */
    private double getMaxVersionNum(String ruleName) {
        double maxVersionNum = 0.99;
        Map<String, String> param = new HashMap<>();
        param.put("ruleName", ruleName);
        param.put("isDraft", "0");
        List<RuleDetail> ruleDetails = daoHelper.queryForList(_MYBITSID_PREFIX + "getMaxVersion", param);
        if (ruleDetails != null && ruleDetails.size() >= 1) {//??????????????????
            RuleDetail ruleDetail = ruleDetails.get(0);
            double version = Double.parseDouble(ruleDetail.getVersion());
            if (version > maxVersionNum) {
                maxVersionNum = version;
            }
        }
        param.put("isDraft", "1");
        List<RuleDetail> draftRules = daoHelper.queryForList(_MYBITSID_PREFIX + "getMaxVersion", param);
        if (draftRules != null && draftRules.size() >= 1) {
            RuleDetail ruleDetail = draftRules.get(0);
            double version = Double.parseDouble(ruleDetail.getVersion());
            if (version > maxVersionNum) {
                maxVersionNum = version;
            }
        }
        return maxVersionNum;
    }

    private void paseRange(ConditionExpression conditionExpression, Set<Object> ranges, String variableTypeId, Map<String, KpiDefinition> kpiDefinitionMap, Map<String, Variable> variableMap) {

        String valueType = conditionExpression.getValueType();
        String opt = conditionExpression.getOpt();
        String value = conditionExpression.getValue();
        String endValue = null;

        endValue = paseValue(valueType, value, conditionExpression, kpiDefinitionMap, variableMap);
        if (endValue == null) {
            return;
        }
        if ("2".equals(variableTypeId) || "4".equals(variableTypeId) || "6".equals(variableTypeId) || "7".equals(variableTypeId)) {
            VariableKpiRange variableKpiRange = null;
            if (ranges != null || !ranges.isEmpty()) {
                Object o = ranges.toArray()[0];
                if (o instanceof VariableKpiRange) {
                    variableKpiRange = (VariableKpiRange) o;
                }
            }
            if (variableKpiRange == null) {
                variableKpiRange = new VariableKpiRange();
                variableKpiRange.setType(variableTypeId);
            }
            Double max = variableKpiRange.getMax();
            Double min = variableKpiRange.getMin();
            String haveMin = variableKpiRange.getHaveMin();
            String haveMax = variableKpiRange.getHaveMax();

            if ("t_expRule".equals(valueType)) {
                try {
                    Object executeTmp = RuleFactory.getRule().execute(endValue, RuleFactory.getRule().createContext(new HashMap<String, Object>()));
                    if (executeTmp != null) {
                        endValue = executeTmp.toString();
                    }
                } catch (Exception e) {
                    log.error("?????????????????????:[" + endValue + "]", e);
                    return;
//                        e.printStackTrace();
                }
            }
            if (StringUtils.isBlank(endValue)) {
                return;
            }
            Double va = null;
            try {
                va = Double.valueOf(endValue);
            } catch (Exception e) {
                log.error("??????[" + conditionExpression + "]????????????[" + value + "],??????????????????[" + endValue + "]??????????????????", e);
                return;
            }

            if (va == null) {
                return;
            }
            if (">".equals(opt)) {
                if (max == null || max != Double.POSITIVE_INFINITY) {
                    max = Double.POSITIVE_INFINITY;
                    haveMax = ")";
                }
                if (min == null) {
                    min = va;
                    haveMin = "(";
                } else if (min > va) {
                    min = va;
                    haveMin = "(";
                }
            } else if ("<".equals(opt)) {
                if (min == null || min != Double.POSITIVE_INFINITY) {
                    min = Double.POSITIVE_INFINITY;
                    haveMin = "(";
                }
                if (max == null) {
                    max = va;
                    haveMax = ")";
                } else if (max < va) {
                    max = va;
                    haveMax = ")";
                }
            } else if (">=".equals(opt)) {
                if (max == null || max != Double.POSITIVE_INFINITY) {
                    max = Double.POSITIVE_INFINITY;
                    haveMax = ")";
                }
                if (min == null) {
                    min = va;
                    haveMin = "[";
                } else if (min > va) {
                    min = va;
                    haveMin = "[";
                }
            } else if ("<=".equals(opt)) {
                if (min == null || min != Double.POSITIVE_INFINITY) {
                    min = Double.POSITIVE_INFINITY;
                    haveMin = "(";
                }
                if (max == null) {
                    max = va;
                    haveMax = "]";
                } else if (max < va) {
                    max = va;
                    haveMax = "]";
                }
            } else if ("==".equals(opt)) {
                if (max == null) {
                    max = va;
                    haveMax = "]";
                } else if (max < va) {
                    max = va;
                    haveMax = "]";
                }
                if (min == null) {
                    min = va;
                    haveMin = "[";
                } else if (min > va) {
                    min = va;
                    haveMin = "[";
                }
            } else if ("!=".equals(opt)) {

            } else if ("in".equals(opt)) {

            }


        } else if ("1".equals(variableTypeId)) {
            if ("t_expRule".equals(valueType)) {
                // ???????????????????????????--- ?????????????????? "+" ??? " " ?????????
                endValue = endValue.replaceAll("(\\+| )", "");
//                    try {
//                        Object executeTmp = RuleFactory.getRule().execute(endValue, RuleFactory.getRule().createContext(new HashMap<String, Object>()));
//                        if (executeTmp != null) {
//                            endValue = executeTmp.toString();
//                        }
//                    } catch (Exception e) {
//                        log.error("?????????????????????:[" + endValue + "]", e);
//                        continue;
////                        e.printStackTrace();
//                    }
            }
            if (StringUtils.isBlank(endValue)) {
                return;
            }
            ranges.add(endValue);
        }
    }

    public void paseconditionExpressionToRangesMap(ConditionExpression conditionExpression, Map<String, Set<Object>> idRangesMap, Map<String, KpiDefinition> kpiDefinitionMap, Map<String, Variable> variableMap) {
        if (conditionExpression == null) {
            return;
        }
        String kpiId = conditionExpression.getKpiId();
        if (!StringUtils.isBlank(kpiId)) {
            KpiDefinition kpiDefinition = kpiDefinitionMap.get(kpiId);
            if (kpiDefinition == null) {
                //TODO : ?????????????????????, ???????????????,????????????
                return;
            }
            String kpiTypeId = kpiDefinition.getKpiType();
//            VariableKpiRange
            Set<Object> kpiRanges = idRangesMap.get(kpiId);
            if (kpiRanges == null) {
                kpiRanges = new HashSet<>();
                idRangesMap.put(kpiId, kpiRanges);
            }
            // TODO: ????????????
            String rightValueType = conditionExpression.getValueType();
            String opt = conditionExpression.getOpt();
            String value = conditionExpression.getValue();
            String endValue = null;

            endValue = paseValue(rightValueType, value, conditionExpression, kpiDefinitionMap, variableMap);

        }
    }

    //???????????????????????????
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult getVersionListWithDraftByStatus(String ruleName) {
        Map<String, String> param = new HashMap<>();
        param.put("ruleName", ruleName);
        param.put("isDraft", "0");
        List<RuleDetail> ruleDetailsBase = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getVersionList3", param);
        param.put("isDraft", "1");
        List<RuleDetail> ruleDetailsDraft = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getVersionList3", param);
        if (ruleDetailsDraft.size() >= 1) {
            RuleDetail draftRuleDeatil = ruleDetailsDraft.get(0);
            boolean chooseDraft = false;
            if (ruleDetailsBase.size() < 1) {
                chooseDraft = true;
            }
            if (ruleDetailsBase.size() > 0) {
                if (Float.parseFloat(draftRuleDeatil.getVersion()) > Float.parseFloat(ruleDetailsBase.get(0).getVersion())) {
                    chooseDraft = true;
                }

                if (Float.parseFloat(draftRuleDeatil.getVersion()) == Float.parseFloat(ruleDetailsBase.get(0).getVersion())) {
                    if (draftRuleDeatil.getVersionCreateDate().getTime() > ruleDetailsBase.get(0).getVersionCreateDate().getTime()) {
                        ruleDetailsBase.remove(0);
                        chooseDraft = true;
                    }
                }
            }

            if (chooseDraft) {
                draftRuleDeatil.setVersion(draftRuleDeatil.getVersion() + "(??????)");
                ruleDetailsBase.add(0, draftRuleDeatil);
            }
        }
        return ResponseResult.createSuccessInfo("????????????", ruleDetailsBase);
    }

    /**
     * ??????????????????????????????id???????????????????????????
     * @param modelGroupId ??????id
     * @param loginUserId ??????id
     * @return ????????????
     */
    @Override
    public ModelGroupInfo getModelByGroupId(String modelGroupId, String loginUserId) {
        // ????????????????????????

        ModelGroupInfo info = new ModelGroupInfo();
        // ????????????id??????????????????
        ModelGroup mg = (ModelGroup) daoHelper.queryOne(_MODEL_GROUP_MAPPER + "getGroupById", modelGroupId);
        info.getModelGroupInfo(info,mg);
        // ????????????id???????????????????????????
        List<Channel> nameList = daoHelper.queryForList(_MODEL_GROUP_MAPPER + "getChannelName", modelGroupId);
        info.setChannelList(nameList);
        // ????????????id???????????????????????????
        List<RuleDetailHeader> modelList = daoHelper.queryForList(_RULE_DETAIL_MAPPER + "getModelListByGroup", modelGroupId);
        info.setModelList(modelList);

        return info;
    }

    /**
     * ?????????????????????????????????????????????????????????
     * ????????????????????????????????????id??????
     * @param modelList ????????????????????????
     * @param modelGroupId ????????????????????????id
     * @return ??????
     */
    @Override
    @Transactional
    public ResponseResult groupAddModel(List<RuleDetailHeader> modelList, String modelGroupId) {
        // ???????????????ruleDetail???????????????-??????id?????????id???
        if (modelList.size() > 0) {
            for (RuleDetailHeader header : modelList) {
                Map<String, String> map = new HashMap<>();
                map.put("ruleId", header.getRuleName());
                map.put("modelGroupId", modelGroupId);
                daoHelper.update(_RULE_DETAIL_MAPPER + "groupAddModel", map);
            }
            return ResponseResult.createSuccessInfo("????????????");
        }
        return ResponseResult.createFailInfo("???????????????");
    }


    /**
     * ????????????????????????
     * ??????????????????,????????????Id,??????Id,??????Id
     *
     * @param model
     * @return
     */
    public ModelContentInfo getModelContentInfoExcudeRange(String model) {
        ModelContentInfo modelContentInfo = new ModelContentInfo();
        if (!StringUtils.isBlank(model)) {
            Set<String> varIdsFromModle = RuleDetailServiceImpl.getVarIdsFromModle(model);
            Set<String> kpiIdsFromModle = RuleDetailServiceImpl.getKpiIdsFromModle(model);
            modelContentInfo.setKpiIdSet(kpiIdsFromModle);
            modelContentInfo.setVariableIdSet(varIdsFromModle);
            Set<String> apiIdsFromModle = getApiIdsFromModle(model);
            Set<String> ruleSetIdsFromModle = getRuleSetIdsFromModle(model);
            modelContentInfo.setApiIdSet(apiIdsFromModle);
            modelContentInfo.setRuleSetIdSet(ruleSetIdsFromModle);
        }
        return modelContentInfo;
    }

    /**
     * ????????????????????????
     * ??????????????????,????????????Id,??????Id,?????????????????????
     *
     * @param model
     * @return
     */
    private ModelContentInfo getModelContentInfo(String model) {

        ModelContentInfo modelContentInfo = new ModelContentInfo();
        Set<String> varIdsFromModle = RuleDetailServiceImpl.getVarIdsFromModle(model);
        Set<String> kpiIdsFromModle = RuleDetailServiceImpl.getKpiIdsFromModle(model);
        modelContentInfo.setKpiIdSet(kpiIdsFromModle);
        modelContentInfo.setVariableIdSet(varIdsFromModle);
//        Set<String> apiIdsFromModle = getApiIdsFromModle(model);
//        Set<String> ruleSetIdsFromModle = getRuleSetIdsFromModle(model);
//        modelContentInfo.setApiIdSet(apiIdsFromModle);
//        modelContentInfo.setRuleSetIdSet(ruleSetIdsFromModle);
        // ????????????????????????ID, ??????????????????????????????
        Map<String, KpiDefinition> kpiDefinitionMap = kpiService.getKpiBaseInfoBatch(new ArrayList<String>(kpiIdsFromModle));
        Map<String, Variable> variableMap = variableService.selectVariableByKeyBatch(new ArrayList<String>(varIdsFromModle));
        // LHS???????????????
        Matcher lhsJson = LHS_JSON.matcher(model);
        // ?????????????????????
        Matcher pathCdtJson = PATHCDT_VALUE_JSON.matcher(model);
        // ??????????????????
        Map<String, VariableKpiRange> allIdRangesMap = new HashMap<>(varIdsFromModle.size() + kpiIdsFromModle.size());

//        final Matcher matcher = CONDITION_EXPRESSION.matcher(model);
        while (lhsJson.find()) {
            String conditionsString = lhsJson.group(0);
            if (!StringUtils.isBlank(conditionsString)) {
                String conditionsJSONString = conditionsString.substring(0, conditionsString.length() - 1);
                ConditionList conditionList = paseConditionList(conditionsJSONString, kpiDefinitionMap, variableMap);
                modelContentInfo.addConditionList(conditionList);

            }
        }
        while (pathCdtJson.find()) {
            String conditionsString = pathCdtJson.group(0);
            if (!StringUtils.isBlank(conditionsString)) {
                String conditionsJSONString = conditionsString + "}";
                ConditionList conditionList = paseConditionList(conditionsJSONString, kpiDefinitionMap, variableMap);
                modelContentInfo.addConditionList(conditionList);
            }
        }
        List<ConditionList> conditionListList = modelContentInfo.getConditionListList();
        if (conditionListList != null && !conditionListList.isEmpty()) {
            for (ConditionList conditionList : conditionListList) {
                if (conditionList == null) {
                    continue;
                }
                Map<String, VariableKpiRange> idRangesMap = conditionList.getIdRangesMap();
                if (idRangesMap != null && !idRangesMap.isEmpty()) {
                    for (String id : idRangesMap.keySet()) {
                        VariableKpiRange variableKpiRange = idRangesMap.get(id);
                        if (variableKpiRange == null) {
                            continue;
                        }
                        VariableKpiRange allVariableKpiRange = allIdRangesMap.get(id);
                        if (allVariableKpiRange == null) {
                            allIdRangesMap.put(id, variableKpiRange);
                        } else {
                            allVariableKpiRange = compareVariableKpiRange(allVariableKpiRange, variableKpiRange, variableKpiRange.getType(), "or");
                            allIdRangesMap.put(id, allVariableKpiRange);
                        }
                    }
                }
            }
        }
        modelContentInfo.setIdRangesMap(allIdRangesMap);

        return modelContentInfo;
    }

    private ConditionList paseConditionList(String conditionsJSONString, Map<String, KpiDefinition> kpiDefinitionMap, Map<String, Variable> variableMap) {
        JSONObject conditionsJSONObject = null;
        try {
            conditionsJSONObject = JSONObject.parseObject(conditionsJSONString);
        } catch (Exception e) {
            // ?????????
            return null;
        }
        if (conditionsJSONObject == null) {
            return null;
        }
        ConditionList conditionList = new ConditionList();
        String union = conditionsJSONObject.getString("union");
        conditionList.setUnion(union);
        JSONArray conditions = conditionsJSONObject.getJSONArray("condition");

        if (conditions == null || conditions.isEmpty()) {
            return null;
        }
        Map<String, VariableKpiRange> idRangesMap = new HashMap<>();
        for (int i = 0; i < conditions.size(); i++) {
            String conditionExperssionJSONString = conditions.getString(i);
            ConditionExpression conditionExpression = paseConditionExpression(conditionExperssionJSONString);

            String conditionId = conditionExpression.getConditionId();
            String kpiId = conditionExpression.getKpiId();
            String varId = conditionExpression.getVarId();
            String variableTypeId = null;
            if (!StringUtils.isBlank(kpiId)) {
                KpiDefinition kpiDefinition = kpiDefinitionMap.get(kpiId);
                if (kpiDefinition == null) {
                    throw new RuntimeException("???????????????????????????????????????id: " + kpiId);
                }
                variableTypeId = kpiDefinition.getKpiType();
            }
            if (!StringUtils.isBlank(varId)) {
                Variable variable = variableMap.get(varId);
                if (variable == null) {
                    throw new RuntimeException("???????????????????????????????????????id: " + varId);
                }
                variableTypeId = variable.getTypeId();
            }

            VariableKpiRange conditioRange = paseVariableKpiRange(conditionExpression, kpiDefinitionMap, variableMap);
            conditionExpression.setRange(conditioRange);

            VariableKpiRange idListRange = conditionList.getRange(conditionId);
            if (idListRange == null) {
                conditionList.putIdRange(conditionId, conditioRange);
            } else {
                conditionList.putIdRange(conditionId, compareVariableKpiRange(idListRange, conditioRange, variableTypeId, union));
            }

            conditionList.putKpiConditionExpression(conditionExpression);
        }
        return conditionList;
    }


    private VariableKpiRange paseVariableKpiRange(ConditionExpression conditionExpression, Map<String, KpiDefinition> kpiDefinitionMap, Map<String, Variable> variableMap) {
        VariableKpiRange variableKpiRange = new VariableKpiRange();
        String kpiId = conditionExpression.getKpiId();
        String varId = conditionExpression.getVarId();
        String variableTypeId = null;
        if (!StringUtils.isBlank(kpiId)) {
            KpiDefinition kpiDefinition = kpiDefinitionMap.get(kpiId);
            variableTypeId = kpiDefinition.getKpiType();
        }
        if (!StringUtils.isBlank(varId)) {
            Variable variable = variableMap.get(varId);
            variableTypeId = variable.getTypeId();
        }
        variableKpiRange.setType(variableTypeId);
        String valueType = conditionExpression.getValueType();
        String opt = conditionExpression.getOpt();
        String value = conditionExpression.getValue();
        String endValue = null;

        endValue = paseValue(valueType, value, conditionExpression, kpiDefinitionMap, variableMap);
        if (endValue == null) {
            return null;
        }
        if (StringUtils.isBlank(variableTypeId)) {
            return null;
        }
        if ("2".equals(variableTypeId) || "4".equals(variableTypeId) || "6".equals(variableTypeId) || "7".equals(variableTypeId)) {
            Double max = null;
            Double min = null;
            String haveMin = null;
            String haveMax = null;

            if ("t_expRule".equals(valueType)) {
                try {
                    Object executeTmp = RuleFactory.getRule().execute(endValue, RuleFactory.getRule().createContext(new HashMap<String, Object>()));
                    if (executeTmp != null) {
                        endValue = executeTmp.toString();
                    }
                } catch (Exception e) {
                    log.error("?????????????????????:[" + endValue + "]", e);
                    return null;
//                        e.printStackTrace();
                }
            }
            if (StringUtils.isBlank(endValue)) {
                return null;
            }
            Double va = null;
            try {
                va = Double.valueOf(endValue);
            } catch (Exception e) {
                log.error("??????[" + conditionExpression + "]????????????[" + value + "],??????????????????[" + endValue + "]??????????????????", e);
                return null;
            }

            if (">".equals(opt)) {
                max = Double.POSITIVE_INFINITY;
                haveMax = ")";
                min = va;
                haveMin = "(";
            } else if ("<".equals(opt)) {
                max = va;
                haveMax = ")";
                min = Double.NEGATIVE_INFINITY;
                haveMin = "(";
            } else if (">=".equals(opt)) {
                max = Double.POSITIVE_INFINITY;
                haveMax = ")";
                min = va;
                haveMin = "[";
            } else if ("<=".equals(opt)) {
                max = va;
                haveMax = "]";
                min = Double.NEGATIVE_INFINITY;
                haveMin = "(";
            } else if ("==".equals(opt)) {
                max = va;
                haveMax = "]";
                min = va;
                haveMin = "[";
            } else if ("!=".equals(opt)) {

            } else if ("in".equals(opt)) {

            }
            variableKpiRange.setMax(max);
            variableKpiRange.setMin(min);
            variableKpiRange.setHaveMax(haveMax);
            variableKpiRange.setHaveMin(haveMin);

        } else {
            if ("t_expRule".equals(valueType)) {
                // ???????????????????????????--- ?????????????????? "+" ??? " " ?????????
                endValue = endValue.replaceAll("(\\+| )", "");
//                    try {
//                        Object executeTmp = RuleFactory.getRule().execute(endValue, RuleFactory.getRule().createContext(new HashMap<String, Object>()));
//                        if (executeTmp != null) {
//                            endValue = executeTmp.toString();
//                        }
//                    } catch (Exception e) {
//                        log.error("?????????????????????:[" + endValue + "]", e);
//                        continue;
////                        e.printStackTrace();
//                    }
            }
            if (StringUtils.isBlank(endValue)) {
                return null;
            }
            variableKpiRange.addStrRange(endValue);
        }
        return variableKpiRange;
    }

    public VariableKpiRange compareVariableKpiRange(VariableKpiRange rangeA, VariableKpiRange rangeB, String variableTypeId, String union) {
        if (rangeA == null && rangeB == null) {
            return null;
        }
        if (rangeA == null) {
            return rangeB;
        }
        if (rangeB == null) {
            return rangeA;
        }
        if (StringUtils.isBlank(variableTypeId)) {
            return null;
        }
        if ("and".equals(union)) {
            return intersectionRange(rangeA, rangeB, variableTypeId);
        } else if ("or".equals(union)) {
            return unionRange(rangeA, rangeB, variableTypeId);
        }
        return null;
    }

    /**
     * ???????????????????????????
     * ??????????????????????????????
     * ??????????????????????????????
     *
     * @param rangeA
     * @param rangeB
     * @param variableTypeId
     * @return
     */
    public VariableKpiRange intersectionRange(VariableKpiRange rangeA, VariableKpiRange rangeB, String variableTypeId) {
        VariableKpiRange result = new VariableKpiRange();
        result.setType(variableTypeId);
        if ("2".equals(variableTypeId) || "4".equals(variableTypeId) || "6".equals(variableTypeId) || "7".equals(variableTypeId)) {
            Double maxA = rangeA.getMax();
            String haveMaxA = rangeA.getHaveMax();
            Double minA = rangeA.getMin();
            String haveMinA = rangeA.getHaveMin();

            Double maxB = rangeB.getMax();
            String haveMaxB = rangeB.getHaveMax();
            Double minB = rangeB.getMin();
            String haveMinB = rangeB.getHaveMin();

            result.setMax(getMin(maxA, maxB));
            if (result.getMax() == maxA) {
                result.setHaveMax(haveMaxA);
            } else if (result.getMax() == maxB) {
                result.setHaveMax(haveMaxB);
            }

            result.setMin(getMax(minA, minB));
            if (result.getMin() == minA) {
                result.setHaveMin(haveMinA);
            } else if (result.getMin() == minB) {
                result.setHaveMin(haveMinB);
            }
        } else {
            //????????????????????????
            Set<String> intersectionSet = ModelComparUtil.intersectionSet(rangeA.getStrRanges(), rangeB.getStrRanges());
            result.addAllStrRange(intersectionSet);
        }
        return result;
    }

    /**
     * ???????????????????????????
     * ??????????????????????????????
     * ??????????????????????????????
     *
     * @param rangeA
     * @param rangeB
     * @param variableTypeId
     * @return
     */
    public VariableKpiRange unionRange(VariableKpiRange rangeA, VariableKpiRange rangeB, String variableTypeId) {
        VariableKpiRange result = new VariableKpiRange();
        result.setType(variableTypeId);
        if ("2".equals(variableTypeId) || "4".equals(variableTypeId) || "6".equals(variableTypeId) || "7".equals(variableTypeId)) {
            Double maxA = rangeA.getMax();
            String haveMaxA = rangeA.getHaveMax();
            Double minA = rangeA.getMin();
            String haveMinA = rangeA.getHaveMin();

            Double maxB = rangeB.getMax();
            String haveMaxB = rangeB.getHaveMax();
            Double minB = rangeB.getMin();
            String haveMinB = rangeB.getHaveMin();


            result.setMax(getMax(maxA, maxB));
            if (result.getMax() == maxA) {
                result.setHaveMax(haveMaxA);
            } else if (result.getMax() == maxB) {
                result.setHaveMax(haveMaxB);
            }

            result.setMin(getMin(minA, minB));
            if (result.getMin() == minA) {
                result.setHaveMin(haveMinA);
            } else if (result.getMin() == minB) {
                result.setHaveMin(haveMinB);
            }
        } else {
            result.addAllStrRange(rangeA.getStrRanges());
            result.addAllStrRange(rangeB.getStrRanges());
        }
        return result;
    }

    public Double getMax(Double a, Double b) {
        return cpmpareNumber(a, b, "max");
    }

    public Double getMin(Double a, Double b) {
        return cpmpareNumber(a, b, "min");
    }

    public Double cpmpareNumber(Double a, Double b, String maxMinType) {
        if (a == null && b == null) {
            return null;
        }
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        if ("min".equals(maxMinType)) {
            return a < b ? a : b;
        } else {
            return a > b ? a : b;
        }
    }


    private String paseValue(String rightValueType, String value, ConditionExpression conditionExpression, Map<String, KpiDefinition> kpiDefinitionMap, Map<String, Variable> variableMap) {
        String endValue = null;
        if ("t_value".equals(rightValueType)) {
            endValue = value;
        } else if ("t_variable".equals(rightValueType)) {
            String valueVariableId = value.replaceAll("\\[|\\]", "");
            //FIXME : ?????????????????????,????????????,
            Variable valueVariable = variableMap.get(valueVariableId);
            if (valueVariable != null) {
                if ("K4".equals(valueVariable.getKindId())) {
                    endValue = valueVariable.getDefaultValue();
                }
            }
        } else if ("t_expRule".equals(rightValueType)) {
            List<ConditionExpressionReference> referenceList = conditionExpression.getReferences();
            if (CollectionUtil.isEmpty(referenceList)) {
                referenceList = new ArrayList<>();
            }
            Map<String, String> nameVariableDefaultMap = new HashMap<>();
            for (ConditionExpressionReference reference : referenceList) {
                if (reference == null) {
                    continue;
                }
                String kpiId = reference.getKpiId();
                // ??????KPI, ??????,????????????
                if (kpiId != null) {
                    return null;
                }
                String varId = reference.getVarId();
                Variable referenceVariable = variableMap.get(varId);
                if (referenceVariable == null) {
                    //TODO : ???????????????????????????
                    continue;
                }
                // ???????????????????????????, ??????,????????????
                if (!"K4".equals(referenceVariable.getKindId())) {
                    return null;
                }
                nameVariableDefaultMap.put(referenceVariable.getVariableAlias(), referenceVariable.getDefaultValue());
            }
            endValue = new String(value);
            //TODO : ??????????????????
            Pattern variableNamePattern = Pattern.compile("\\[(.*?)\\]");
            final Matcher matcher = variableNamePattern.matcher(value);
            while (matcher.find()) {
                String valueName = matcher.group(1);
                String variableDefault = nameVariableDefaultMap.get(valueName);
                System.out.println("endValue="+endValue);
                System.out.println("valueName="+valueName);
                System.out.println("variableDefault="+variableDefault);
                if(variableDefault!=null) {
                	endValue = endValue.replaceFirst("\\[" + valueName + "\\]", variableDefault);
                }
            }

        }
        return endValue;
    }

    private ConditionExpression paseConditionExpression(String conditionExperssionJSONString) {
        JSONObject conditionExpressionJSONObject = JSONObject.parseObject(conditionExperssionJSONString);
        ConditionExpression conditionExpression = new ConditionExpression();
        String kpiId = conditionExpressionJSONObject.getString("kpiId");
        String varId = conditionExpressionJSONObject.getString("varId");
        String opt = conditionExpressionJSONObject.getString("opt");
        Boolean isKpi = conditionExpressionJSONObject.getBoolean("isKpi");
        String valueType = conditionExpressionJSONObject.getString("valueType");
        String value = conditionExpressionJSONObject.getString("value");

        if (!StringUtils.isBlank(kpiId)) {
            kpiId = kpiId.replaceAll("(\\[|\\])", "");
            conditionExpression.setKpiId(kpiId);
            conditionExpression.setConditionId(kpiId);
        }
        if (!StringUtils.isBlank(varId)) {
            varId = varId.replaceAll("(\\[|\\])", "");
            conditionExpression.setVarId(varId);
            conditionExpression.setConditionId(varId);
        }

        conditionExpression.setOpt(opt);
        conditionExpression.setValueType(valueType);
        if ("t_variable".equals(valueType)) {
            value = value.replaceAll("(\\[|\\])", "");
            conditionExpression.setValue(value);
        } else {
            conditionExpression.setValue(value);
        }
        JSONArray references = conditionExpressionJSONObject.getJSONArray("references");
        if (references != null && !references.isEmpty()) {
            for (int i = 0; i < references.size(); i++) {
                JSONObject jsonObject = references.getJSONObject(i);
                String valueKpiId = jsonObject.getString("kpiId");
                String valueVarId = jsonObject.getString("varId");
                conditionExpression.addReference(valueKpiId, valueVarId);
            }
        }
        return conditionExpression;
    }

    public String updateRuleContentRefId(String ruleContent, String idType, String oldId, String newId) {
        if (StringUtils.isBlank(idType)) {
            return ruleContent;
        }
        if (StringUtils.isNotBlank(ruleContent)) {
            switch (idType) {
                case ExportConstant.KPI:
                    ruleContent = ruleContent.replaceAll("kpiId\\\\\":\\\\\"\\[" + oldId + "\\]", "kpiId\\\\\":\\\\\"\\[" + newId + "\\]");
                    ruleContent = ruleContent.replaceAll("valueType\\\\\":\\\\\"t_variable\\\\\",\\\\\"isKpi\\\\\":true,\\\\\"value\\\\\":\\\\\"\\[" + oldId + "\\]",
                            "valueType\\\\\":\\\\\"t_variable\\\\\",\\\\\"isKpi\\\\\":true,\\\\\"value\\\\\":\\\\\"\\[" + newId + "\\]");
                    break;
                case ExportConstant.VARIABLE:
                    ruleContent = ruleContent.replaceAll("varId\\\\\":\\\\\"\\[" + oldId + "\\]", "varId\\\\\":\\\\\"\\[" + newId + "\\]");
                    ruleContent = ruleContent.replaceAll("valueType\\\\\":\\\\\"t_variable\\\\\",\\\\\"value\\\\\":\\\\\"\\[" + oldId + "\\]", "valueType\\\\\":\\\\\"t_variable\\\\\",\\\\\"value\\\\\":\\\\\"\\[" + newId + "\\]");
                    break;
                case ExportConstant.API:
                    ruleContent = ruleContent.replaceAll("ointerface\\\":\\{\\\"value\\\":\\\"" + oldId + "\\\"\\}", "ointerface\\\":\\{\\\"value\\\":\\\"" + newId + "\\\"\\}");
                    break;
            }
        }
        return ruleContent;
    }

    public String updateRuleContentRefIdAndName(String ruleContent, ImportAdjustObject adjustObject) {
        String idType = adjustObject.getType();
        Object oldData = adjustObject.getFromData();
        Object newData = adjustObject.getToData();
        String oldId = ExportUtil.getObjectId(idType, oldData);
        String newId = ExportUtil.getObjectId(idType, newData);

        if (StringUtils.isBlank(idType)) {
            return ruleContent;
        }
        if (StringUtils.isBlank(oldId) || StringUtils.isBlank(newId)) {
            log.error("??????????????????????????????,??????,????????????:id??????,type:[" + idType + "]," +
                            "oldObject:[" + oldData + "],newObject:[" + newData + "]",
                    new NullPointerException("??????????????????????????????,??????,????????????:id??????,type:[" + idType + "]," +
                            "oldObject:[" + oldData + "],newObject:[" + newData + "]"));
            return ruleContent;
        }
        if (StringUtils.isNotBlank(ruleContent)) {
            switch (idType) {
                case ExportConstant.KPI:
                    if (oldData instanceof KpiDefinition) {
                        String oldName = ((KpiDefinition) oldData).getKpiName();
                        String newName = ((KpiDefinition) newData).getKpiName();
                        if (!oldName.equals(newName)) {
                            ruleContent = updateModelContentExpRuleRefName(ruleContent, adjustObject);
                        }
                        if (!oldId.equals(newId)) {
                            ruleContent = ruleContent.replaceAll("kpiId\\\\\":\\\\\"\\[" + oldId + "\\]", "kpiId\\\\\":\\\\\"\\[" + newId + "\\]");
                            ruleContent = ruleContent.replaceAll("valueType\\\\\":\\\\\"t_variable\\\\\",\\\\\"isKpi\\\\\":true,\\\\\"value\\\\\":\\\\\"\\[" + oldId + "\\]",
                                    "valueType\\\\\":\\\\\"t_variable\\\\\",\\\\\"isKpi\\\\\":true,\\\\\"value\\\\\":\\\\\"\\[" + newId + "\\]");
//                            ruleContent = ruleContent.replaceAll("kpiId\\\\\":\\\\\"\\[" + oldId + "\\]", "kpiId\\\\\":\\\\\"\\[" + newId + "\\]");
                        }
                    }
                    break;
                case ExportConstant.VARIABLE:
                    // TODO :  ???????????????????????????
                    if (oldData instanceof Variable) {
                        String oldName = ((Variable) oldData).getVariableAlias();
                        String newName = ((Variable) newData).getVariableAlias();
                        if (!oldName.equals(newName)) {
                            ruleContent = updateModelContentExpRuleRefName(ruleContent, adjustObject);
                        }
                        if (!oldId.equals(newId)) {
                            ruleContent = ruleContent.replaceAll("varId\\\\\":\\\\\"\\[" + oldId + "\\]", "varId\\\\\":\\\\\"\\[" + newId + "\\]");
                            ruleContent = ruleContent.replaceAll("valueType\\\\\":\\\\\"t_variable\\\\\",\\\\\"value\\\\\":\\\\\"\\[" + oldId + "\\]", "valueType\\\\\":\\\\\"t_variable\\\\\",\\\\\"value\\\\\":\\\\\"\\[" + newId + "\\]");
                        }
                    }
                    break;
                case ExportConstant.API:
                    if (!oldId.equals(newId)) {
                        ruleContent = ruleContent.replaceAll("ointerface\\\":\\{\\\"value\\\":\\\"" + oldId + "\\\"\\}", "ointerface\\\":\\{\\\"value\\\":\\\"" + newId + "\\\"\\}");
                    }
                    break;
            }
        }
        return ruleContent;
    }

    /**
     * ????????????????????? ??????????????????????????????????????????
     *
     * @param modelContent
     * @return
     */
    public String updateModelContentExpRuleRefName(String modelContent, ImportAdjustObject adjustObject) {
        Map<String, ImportAdjustObject> kpiAdjustObjectMap = new HashMap<>();
        Map<String, ImportAdjustObject> variableAdjustObjectMap = new HashMap<>();
        String type = adjustObject.getType();
        String oldDateId = ExportUtil.getObjectId(type, adjustObject.getFromData());
        if (StringUtils.isBlank(oldDateId)) {
            return modelContent;
        }
        switch (type) {
            case ExportConstant.KPI:
                kpiAdjustObjectMap.put(oldDateId, adjustObject);
                break;
            case ExportConstant.VARIABLE:
                variableAdjustObjectMap.put(oldDateId, adjustObject);
                break;
        }
        String newModelContent = new String(modelContent);
//        Pattern pattern = Pattern.compile("valueType\\\\\":\\\\\"t_expRule\\\\\",\\\\\"value\\\\\":\\\\\"(.*?)\\\\\",\\\\\"references\\\\\":(\\[(\\{\\\\\"(var|kpi)Id\\\\\":\\\\\"\\[[\\w\\d]+\\]\\\\\"\\},?)*?\\{\\\\\"(var|kpi)Id\\\\\":\\\\\"\\[(" + oldDateId + ")\\]\\\\\"\\},?(\\{\\\\\"(var|kpi)Id\\\\\":\\\\\"\\[[\\w\\d]+\\]\\\\\"\\},?)*?\\])\\}\\\"");
        Pattern pattern = Pattern.compile("valueType\\\\\":\\\\\"t_expRule\\\\\",\\\\\"value\\\\\":\\\\\"(.*?)\\\\\",\\\\\"references\\\\\":(\\[(\\{\\\\\"(var|kpi)Id\\\\\":\\\\\"\\[[\\w\\d]+\\]\\\\\"\\},?)*?\\])\\}\\\"");
        Matcher matcher = pattern.matcher(modelContent);
        while (matcher.find()) {
            String oldString = matcher.group(0);
            String oldValueString = matcher.group(1);
            String referencesString = matcher.group(2);
            if (!referencesString.contains(oldDateId)) {
                continue;
            }
            String newString = updateExpRuleRefName(oldString, oldValueString, referencesString, kpiAdjustObjectMap, variableAdjustObjectMap);
            if (!oldString.equals(newString)) {
                newModelContent = newModelContent.replace(oldString, newString);
            }
        }
        return newModelContent;
    }


    private String updateExpRuleRefName(String oldString, String oldValueString, String referencesString, Map<String, ImportAdjustObject> kpiAdjustObjectMap, Map<String, ImportAdjustObject> variableAdjustObjectMap) {

        referencesString = referencesString.replaceAll("\\\\", "");
        List<ConditionExpressionReference> references = JSONArray.parseArray(referencesString, ConditionExpressionReference.class);
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher m = pattern.matcher(oldValueString);
        StringBuilder newValueString = new StringBuilder(oldValueString);
        int nameIndex = 0;
        while (m.find()) {
            String expRuleStrRefName = m.group(1); // name
            if (StringUtils.isBlank(expRuleStrRefName)) {
                continue;
            }
            ConditionExpressionReference conditionExpressionReference = null;
            if (references.size() > nameIndex) {
                conditionExpressionReference = references.get(nameIndex);
                nameIndex++;
            } else {
                throw new RuntimeException("????????????????????????????????????????????????????????????????????????????????????????????????:" + oldString);
            }

            String kpiId = conditionExpressionReference.getKpiId();
            String varId = conditionExpressionReference.getVarId();
            String toDataName = null;
            if (StringUtils.isNotBlank(kpiId)) {
                kpiId = kpiId.replaceAll("(\\[|\\])", "");
                ImportAdjustObject importAdjustObject = kpiAdjustObjectMap.get(kpiId);
                if (importAdjustObject != null) {
//                    KpiDefinition fromData = (KpiDefinition) importAdjustObject.getFromData();
//                    String fromDateName = fromData.getKpiName();
//                    if (expRuleStrRefName.equals(fromDateName)) {
                    KpiDefinition toData = (KpiDefinition) importAdjustObject.getToData();
                    toDataName = toData.getKpiName();
//                    }
                }
            }
            if (StringUtils.isNotBlank(varId)) {
                varId = varId.replaceAll("(\\[|\\])", "");
                ImportAdjustObject importAdjustObject = variableAdjustObjectMap.get(varId);
                if (importAdjustObject != null) {
//                    Variable fromData = (Variable) importAdjustObject.getFromData();
//                    String fromDateName = fromData.getVariableAlias();
//                    if (expRuleStrRefName.equals(fromDateName)) {
                    Variable toData = (Variable) importAdjustObject.getToData();
                    toDataName = toData.getVariableAlias();
//                    }
                }
            }
            if (StringUtils.isNotBlank(toDataName) && !expRuleStrRefName.equals(toDataName)) {
                newValueString.replace(m.start(1), m.end(1), toDataName);
                m = pattern.matcher(newValueString);
                nameIndex = 0;
            }
        }
        if (!oldValueString.equals(newValueString.toString())) {
            oldString = oldString.replace(oldValueString, newValueString);
        }
        return oldString;
    }

//    private String updateExpRuleRefName(String oldString, String oldValueString, String referencesString, Map<String, ImportAdjustObject> kpiAdjustObjectMap, Map<String, ImportAdjustObject> variableAdjustObjectMap) {
//
//        referencesString = referencesString.replaceAll("\\\\", "");
//        List<ConditionExpressionReference> references = JSONArray.parseArray(referencesString, ConditionExpressionReference.class);
//        for (int i = 0; i < references.size(); i++) {
//            ConditionExpressionReference conditionExpressionReference = references.get(i);
//
//            String kpiId = conditionExpressionReference.getKpiId();
//            String varId = conditionExpressionReference.getVarId();
//            String toDataName = null;
//            if (StringUtils.isNotBlank(kpiId)) {
//                kpiId = kpiId.replaceAll("(\\[|\\])", "");
//                ImportAdjustObject importAdjustObject = kpiAdjustObjectMap.get(kpiId);
//                if (importAdjustObject != null) {
////                    KpiDefinition fromData = (KpiDefinition) importAdjustObject.getFromData();
////                    String fromDateName = fromData.getKpiName();
////                    if (expRuleStrRefName.equals(fromDateName)) {
//                    KpiDefinition toData = (KpiDefinition) importAdjustObject.getToData();
//                    toDataName = toData.getKpiName();
////                    }
//                }
//            }
//            if (StringUtils.isNotBlank(varId)) {
//                varId = varId.replaceAll("(\\[|\\])", "");
//                ImportAdjustObject importAdjustObject = variableAdjustObjectMap.get(varId);
//                if (importAdjustObject != null) {
////                    Variable fromData = (Variable) importAdjustObject.getFromData();
////                    String fromDateName = fromData.getVariableAlias();
////                    if (expRuleStrRefName.equals(fromDateName)) {
//                    Variable toData = (Variable) importAdjustObject.getToData();
//                    toDataName = toData.getVariableAlias();
////                    }
//                }
//            }
//            if (StringUtils.isNotBlank(toDataName) && !expRuleStrRefName.equals(toDataName)) {
//                newValueString.replace(m.start(1), m.end(1), toDataName);
//                m = pattern.matcher(newValueString);
//                nameIndex = 0;
//            }
//
//        }
//        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
//        Matcher m = pattern.matcher(oldValueString);
//        StringBuilder newValueString = new StringBuilder(oldValueString);
//        int nameIndex = 0;
//        while (m.find()) {
//            String expRuleStrRefName = m.group(1); // name
//            if (StringUtils.isBlank(expRuleStrRefName)) {
//                continue;
//            }
//            ConditionExpressionReference conditionExpressionReference = null;
//            if (references.size() > nameIndex) {
//                conditionExpressionReference = references.get(nameIndex);
//                nameIndex++;
//            } else {
//                throw new RuntimeException("????????????????????????????????????????????????????????????????????????????????????????????????:" + oldString);
//            }
//
//            String kpiId = conditionExpressionReference.getKpiId();
//            String varId = conditionExpressionReference.getVarId();
//            String toDataName = null;
//            if (StringUtils.isNotBlank(kpiId)) {
//                kpiId = kpiId.replaceAll("(\\[|\\])", "");
//                ImportAdjustObject importAdjustObject = kpiAdjustObjectMap.get(kpiId);
//                if (importAdjustObject != null) {
////                    KpiDefinition fromData = (KpiDefinition) importAdjustObject.getFromData();
////                    String fromDateName = fromData.getKpiName();
////                    if (expRuleStrRefName.equals(fromDateName)) {
//                    KpiDefinition toData = (KpiDefinition) importAdjustObject.getToData();
//                    toDataName = toData.getKpiName();
////                    }
//                }
//            }
//            if (StringUtils.isNotBlank(varId)) {
//                varId = varId.replaceAll("(\\[|\\])", "");
//                ImportAdjustObject importAdjustObject = variableAdjustObjectMap.get(varId);
//                if (importAdjustObject != null) {
////                    Variable fromData = (Variable) importAdjustObject.getFromData();
////                    String fromDateName = fromData.getVariableAlias();
////                    if (expRuleStrRefName.equals(fromDateName)) {
//                    Variable toData = (Variable) importAdjustObject.getToData();
//                    toDataName = toData.getVariableAlias();
////                    }
//                }
//            }
//            if (StringUtils.isNotBlank(toDataName) && !expRuleStrRefName.equals(toDataName)) {
//                newValueString.replace(m.start(1), m.end(1), toDataName);
//                m = pattern.matcher(newValueString);
//                nameIndex = 0;
//            }
//        }
//        if (!oldValueString.equals(newValueString.toString())) {
//            oldString = oldString.replace(oldValueString, newValueString);
//        }
//        return oldString;
//    }

}
