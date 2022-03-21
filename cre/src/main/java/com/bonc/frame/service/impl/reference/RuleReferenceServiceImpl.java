package com.bonc.frame.service.impl.reference;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.api.ApiRule;
import com.bonc.frame.entity.commonresource.RuleSetRule;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.kpi.KpiRule;
import com.bonc.frame.entity.model.ConditionExpression;
import com.bonc.frame.entity.model.ConditionExpressionReference;
import com.bonc.frame.entity.model.ModelContentInfo;
import com.bonc.frame.entity.model.VariableKpiRange;
import com.bonc.frame.entity.variable.Variable;
import com.bonc.frame.entity.variable.reference.VariableRule;
import com.bonc.frame.service.kpi.KpiService;
import com.bonc.frame.service.reference.RuleReferenceService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.util.CollectionUtil;
import com.bonc.framework.rule.executor.resolver.rule.RuleFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模型的引用管理类
 *
 * @author yedunyao
 * @date 2019/9/24 16:58
 */
@Service("ruleReferenceService")
public class RuleReferenceServiceImpl implements RuleReferenceService {

    Log log = LogFactory.getLog(RuleReferenceServiceImpl.class);

    @Autowired
    private DaoHelper daoHelper;

    /**
     * 模型-参数引用中间表
     */
    private final String _VARIABLE_RULE_PREFIX = "com.bonc.frame.mapper.variable.VariableRuleMapper.";

    /**
     * 模型-接口中间表
     */
    private final String _API_RULE_PREFIX = "com.bonc.frame.mapper.api.ApiRuleMapper.";

    /**
     * 模型-规则库规则集引用中间表
     */
    private final String _RULE_SET_RULE_PREFIX = "com.bonc.frame.mapper.commonresource.RuleSetRuleMapper.";

    /**
     * 模型-指标中间表
     */
    private final String _KPI_RULE_PREFIX = "com.bonc.frame.mapper.kpi.KpiRuleMapper.";

    @Autowired
    private VariableService variableService;

    @Autowired
    private KpiService kpiService;


    public Map<String, Object> getVariableKpiCodeAndName(String ruleName, String ruleId) {

        Map<String, Object> variableCodeAndNameMap = variableService.getVariableCodeAndNameMap(ruleName, ruleId);

        Map<String, Object> kpiCodeAndNameMap = getKpiCodeAndNameMap(ruleName, ruleId);

        Map<String, Object> results = new HashMap<>(3);
        if (variableCodeAndNameMap != null && !variableCodeAndNameMap.isEmpty()) {
            results.putAll(variableCodeAndNameMap);
        }
        if (kpiCodeAndNameMap != null && !kpiCodeAndNameMap.isEmpty()) {
            results.putAll(kpiCodeAndNameMap);
        }
        return results;
    }


    // ====== 模型对参数（私有、公有）、接口（私有、公有）、公共规则集的引用 ======

    // ------------ 模型-参数（私有、公有） ------------

    public static VariableRule buildVariableRule(String ruleId, String varId) {
        final VariableRule variableRule = new VariableRule();
        variableRule.setRuleId(ruleId);
        variableRule.setVariableId(varId);
        return variableRule;
    }

    /**
     * 获取模型对参数（私有、公有）的引用
     */
    @Override
    public List<String> selectVariableIdsByRuleId(String ruleId) {
        if (StringUtils.isBlank(ruleId)) {
            throw new IllegalArgumentException("参数[ruleId]不能为空");
        }
        List<String> variableIds = daoHelper.queryForList(_VARIABLE_RULE_PREFIX +
                "selectVariableIdsByRuleId", ruleId);
        return variableIds;
    }

    /**
     * 批量插入模型-参数引用关系
     *
     * @param ruleId
     * @param newVarIds
     */
    @Override
    @Transactional
    public void insertVariableRuleBatch(String ruleId, List<String> newVarIds) {
        if (StringUtils.isBlank(ruleId) || newVarIds == null || newVarIds.isEmpty()) {
            return;
        }
        List<VariableRule> needInsertList = new ArrayList<>(newVarIds.size());
        for (String varId : newVarIds) {
            final VariableRule variableRule = buildVariableRule(ruleId, varId);
            needInsertList.add(variableRule);
        }
        daoHelper.insertBatch(_VARIABLE_RULE_PREFIX + "insertBatch", needInsertList);
    }

    @Override
    @Transactional
    public void insertVariableRuleBatch(List<VariableRule> needInsertList) {
        daoHelper.insertBatch(_VARIABLE_RULE_PREFIX + "insertBatch", needInsertList);
    }

    /**
     * 批量删除模型-参数引用关系
     *
     * @param ruleId
     * @param needDeleteVarIds
     */
    @Override
    @Transactional
    public void deleteVariableRuleBatch(String ruleId, List<String> needDeleteVarIds) {
        if (StringUtils.isBlank(ruleId) || needDeleteVarIds == null || needDeleteVarIds.isEmpty()) {
            return;
        }
        List<VariableRule> needDeleteList = new ArrayList<>(needDeleteVarIds.size());
        for (String varId : needDeleteVarIds) {
            final VariableRule variableRule = buildVariableRule(ruleId, varId);
            needDeleteList.add(variableRule);
        }
        daoHelper.delete(_VARIABLE_RULE_PREFIX + "deleteBatchByRuleIdVariableId", needDeleteList);
    }

    /**
     * 删除模型-参数引用关系
     *
     * @param ruleId
     */
    @Override
    @Transactional
    public void deleteVariableRule(String ruleId) {
        daoHelper.delete(_VARIABLE_RULE_PREFIX + "deleteByRuleId", ruleId);
    }


    // ------------ 模型-接口（私有、公有） ------------

    public static ApiRule buildApiRule(String ruleId, String apiId) {
        final ApiRule apiRule = new ApiRule();
        apiRule.setRuleId(ruleId);
        apiRule.setApiId(apiId);
        return apiRule;
    }

    // 获取模型对接口（私有、公有）的引用
    @Override
    public List<String> selectApiIdsByRuleId(String ruleId) {
        if (StringUtils.isBlank(ruleId)) {
            throw new IllegalArgumentException("参数[ruleId]不能为空");
        }
        List<String> apiIds = daoHelper.queryForList(_API_RULE_PREFIX +
                "selectApiIdsByRuleId", ruleId);
        return apiIds;
    }

    // 批量插入模型-接口引用关系
    @Override
    @Transactional
    public void insertApiRuleBatch(String ruleId, List<String> newApiIds) {
        if (StringUtils.isBlank(ruleId) || newApiIds == null || newApiIds.isEmpty()) {
            return;
        }
        List<ApiRule> needInsertList = new ArrayList<>(newApiIds.size());
        for (String apiId : newApiIds) {
            final ApiRule apiRule = buildApiRule(ruleId, apiId);
            List<String> aIds = daoHelper.queryForList("com.bonc.frame.mapper.api.ApiRuleMapper.selectByApiId", apiId);
            if(aIds.size()<1) {
            	needInsertList.add(apiRule);
            }
        }
        if(needInsertList.size()>0) {
        	daoHelper.insertBatch(_API_RULE_PREFIX + "insertBatch", needInsertList);
        }
    }

    // 批量删除模型-接口引用关系
    @Override
    @Transactional
    public void deleteApiRuleBatch(String ruleId, List<String> needDeleteApiIds) {
        if (StringUtils.isBlank(ruleId) || needDeleteApiIds == null || needDeleteApiIds.isEmpty()) {
            return;
        }
        List<ApiRule> needDeleteList = new ArrayList<>(needDeleteApiIds.size());
        for (String apiId : needDeleteApiIds) {
            final ApiRule apiRule = buildApiRule(ruleId, apiId);
            needDeleteList.add(apiRule);
        }
        daoHelper.delete(_API_RULE_PREFIX + "deleteBatchByRuleIdApiId", needDeleteList);
    }

    // 删除模型-接口引用关系
    @Override
    @Transactional
    public void deleteApiRule(String ruleId) {
        daoHelper.delete(_API_RULE_PREFIX + "deleteByRuleId", ruleId);
    }

    // ------------ 模型-公共规则集 ------------

    public static RuleSetRule buildRuleSetRule(String ruleId, String ruleSetId) {
        final RuleSetRule ruleSetRule = new RuleSetRule();
        ruleSetRule.setRuleId(ruleId);
        ruleSetRule.setRuleSetId(ruleSetId);
        return ruleSetRule;
    }

    // 获取模型对公共规则集的引用
    @Override
    public List<String> selectRuleSetIdsByRuleId(String ruleId) {
        if (StringUtils.isBlank(ruleId)) {
            throw new IllegalArgumentException("参数[ruleId]不能为空");
        }
        List<String> ruleSetIds = daoHelper.queryForList(_RULE_SET_RULE_PREFIX +
                "selectRuleSetIdsByRuleId", ruleId);
        return ruleSetIds;
    }

    // 批量插入模型-公共规则集引用关系
    @Override
    @Transactional
    public void insertRuleSetRuleBatch(String ruleId, List<String> newRuleSetIds) {
        if (StringUtils.isBlank(ruleId) || newRuleSetIds == null || newRuleSetIds.isEmpty()) {
            return;
        }
        List<RuleSetRule> needInsertList = new ArrayList<>(newRuleSetIds.size());
        for (String ruleSetId : newRuleSetIds) {
            final RuleSetRule ruleSetRule = buildRuleSetRule(ruleId, ruleSetId);
            needInsertList.add(ruleSetRule);
        }
        daoHelper.insertBatch(_RULE_SET_RULE_PREFIX + "insertBatch", needInsertList);
    }


    // 批量删除模型-公共规则集引用关系
    @Override
    @Transactional
    public void deleteRuleSetRuleBatch(String ruleId, List<String> needDeleteRuleSetIds) {
        if (StringUtils.isBlank(ruleId) || needDeleteRuleSetIds == null || needDeleteRuleSetIds.isEmpty()) {
            return;
        }
        List<RuleSetRule> needDeleteList = new ArrayList<>(needDeleteRuleSetIds.size());
        for (String ruleSetId : needDeleteRuleSetIds) {
            final RuleSetRule ruleSetRule = buildRuleSetRule(ruleId, ruleSetId);
            needDeleteList.add(ruleSetRule);
        }
        daoHelper.delete(_RULE_SET_RULE_PREFIX + "deleteBatchByRuleIdsRuleSetIds", needDeleteList);
    }

    // 删除模型-公共规则集引用关系
    @Override
    @Transactional
    public void deleteRuleSetRule(String ruleId) {
        daoHelper.delete(_RULE_SET_RULE_PREFIX + "deleteByRuleId", ruleId);
    }

    // --------------------模型-指标-----------------------------

    public static KpiRule buildKpiRule(String ruleId, String kpiId) {
        final KpiRule kpiRule = new KpiRule();
        kpiRule.setRuleId(ruleId);
        kpiRule.setKpiId(kpiId);
        return kpiRule;
    }

    // 通过模型id获取该模型引用的所有指标的id
    @Override
    public List<String> selectKpiIdsByRuleId(String ruleId) {
        if (StringUtils.isBlank(ruleId)) {
            throw new IllegalArgumentException("参数[ruleId]不能为空");
        }
        return daoHelper.queryForList(_KPI_RULE_PREFIX +
                "selectKpiIdsByRuleId", ruleId);
    }

    /**
     * 获取模型引用的所有指标信息
     *
     * @param ruleId
     * @return
     */
    @Override
    public List<Map<String, Object>> selectKpiInfoByRuleId(String ruleId) {
        if (StringUtils.isBlank(ruleId)) {
            throw new IllegalArgumentException("参数[ruleId]不能为空");
        }
        Map<String, Object> params = new HashMap<>(2);
        params.put("ruleId", ruleId);
        return daoHelper.queryForList(_KPI_RULE_PREFIX +
                "selectKpiInfoByRuleIdOrRuleName", params);
    }

    /**
     * 获取模型引用的所有指标信息
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> selectKpiInfoByRuleIdOrRuleName(String ruleName, String ruleId) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("ruleName", ruleName);
        params.put("ruleId", ruleId);
        return daoHelper.queryForList(_KPI_RULE_PREFIX +
                "selectKpiInfoByRuleIdOrRuleName", params);
    }

    /**
     * 获取模型引用的所有指标Map<code:别名>
     *
     * @return Map<code:别名>
     */
    @Override
    public Map<String, Object> getKpiCodeAndNameMap(String ruleName, String ruleId) {
        List<Map<String, Object>> kpiMapByRuleId = selectKpiInfoByRuleIdOrRuleName(ruleName, ruleId);
        if (kpiMapByRuleId == null) {
            return new HashMap<>(1);
        }
        Map<String, Object> kpiCodeDescMap = new HashMap<>(kpiMapByRuleId.size());
        for (Map<String, Object> kpiMap : kpiMapByRuleId) {
            if (kpiMap != null) {
                String kpiCode = (String) kpiMap.get("kpiCode");
                String kpiName = (String) kpiMap.get("kpiName");
                if (!StringUtils.isBlank(kpiCode)) {
                    if (StringUtils.isBlank(kpiName)) {
                        kpiName = kpiCode;
                    }
                    kpiCodeDescMap.put(kpiCode, kpiName);
                }
            }
        }
        return kpiCodeDescMap;
    }

    // 通过指标id获取 引用该指标的所有模型的id
    @Override
    public List<String> selectByKpiId(String kpiId) {
        if (StringUtils.isBlank(kpiId)) {
            throw new IllegalArgumentException("参数[kpiId]不能为空");
        }
        return daoHelper.queryForList(_KPI_RULE_PREFIX +
                "selectByKpiId", kpiId);
    }

    // 通过指标id获取 获取改指标的被引用数
    @Override
    public int countByKpiId(String kpiId) {
        if (StringUtils.isBlank(kpiId)) {
            throw new IllegalArgumentException("参数[kpiId]不能为空");
        }
        return (int) daoHelper.queryOne(_KPI_RULE_PREFIX +
                "countByKpiId", kpiId);
    }


    // 批量插入模型-指标引用关系
    @Override
    @Transactional
    public void insertKpiRuleBatch(String ruleId, List<String> newKpiIds) {
        if (StringUtils.isBlank(ruleId) || newKpiIds == null || newKpiIds.isEmpty()) {
            return;
        }
        List<KpiRule> needInsertList = new ArrayList<>(newKpiIds.size());
        for (String kpiId : newKpiIds) {
            final KpiRule kpiRule = buildKpiRule(ruleId, kpiId);
            needInsertList.add(kpiRule);
        }
        daoHelper.insertBatch(_KPI_RULE_PREFIX + "insertBatch", needInsertList);
    }

    @Override
    @Transactional
    public void insertKpiRuleBatch(List<KpiRule> needInsertList) {
        daoHelper.insertBatch(_KPI_RULE_PREFIX + "insertBatch", needInsertList);
    }


    // 批量删除模型-指标引用关系
    @Override
    @Transactional
    public void deleteBatchByRuleIdsKpiIds(String ruleId, List<String> needDeleteKpiIds) {
        if (StringUtils.isBlank(ruleId) || needDeleteKpiIds == null || needDeleteKpiIds.isEmpty()) {
            return;
        }
        List<KpiRule> needDeleteList = new ArrayList<>(needDeleteKpiIds.size());
        for (String kpiId : needDeleteKpiIds) {
            final KpiRule kpiRule = buildKpiRule(ruleId, kpiId);
            needDeleteList.add(kpiRule);
        }
        daoHelper.delete(_KPI_RULE_PREFIX + "deleteBatchByRuleIdsKpiIds", needDeleteList);
    }

    /**
     * 删除模型所有的指标引用关系
     *
     * @param ruleId
     */
    @Override
    @Transactional
    public void deleteKpiByRuleId(String ruleId) {
        daoHelper.delete(_KPI_RULE_PREFIX + "deleteByRuleId", ruleId);
    }

    /**
     * 删除模型-指标之间的引用关系
     *
     * @param ruleId
     * @param kpiId
     */
    @Override
    @Transactional
    public void deleteByRuleIdKpiId(String ruleId, String kpiId) {
        Map<String, String> param = new HashMap<>();
        param.put("ruleId", ruleId);
        param.put("kpiId", kpiId);

        daoHelper.delete(_KPI_RULE_PREFIX + "deleteByRuleIdKpiId", param);
    }

    public void saveVariableRuleAndKpiRule(ModelContentInfo modelContentInfo, String ruleId) {
        Map<String, VariableKpiRange> idRangesMap = modelContentInfo.getIdRangesMap();
        List<String> kpiIdSet = modelContentInfo.getKpiIdSet();
        List<String> varIdSet = modelContentInfo.getVariableIdSet();
        // 保存最后的结果
        List<VariableRule> variableRuleList = new ArrayList<>(varIdSet.size());
        List<KpiRule> kpiRuleList = new ArrayList<>(kpiIdSet.size());
        for (String varId : varIdSet) {
            VariableRule variableRule = new VariableRule();
            variableRule.setRuleId(ruleId);
            variableRule.setVariableId(varId);
            VariableKpiRange variableKpiRange = idRangesMap.get(varId);
            if(variableKpiRange != null){
                variableRule.setVariableRange(variableKpiRange.getResult());
            }
            List<String> vIds = daoHelper.queryForList("com.bonc.frame.mapper.variable.VariableRuleMapper.selectByVaribaleId", varId);
            if(vIds.size()<1) {
            	variableRuleList.add(variableRule);
            }
        }
        for (String kpiId : kpiIdSet) {
            KpiRule kpiRule = new KpiRule();
            kpiRule.setKpiId(kpiId);
            kpiRule.setRuleId(ruleId);
            VariableKpiRange variableKpiRange = idRangesMap.get(kpiId);
            if(variableKpiRange != null){
                kpiRule.setKpiRange(variableKpiRange.getResult());
            }
            kpiRuleList.add(kpiRule);
        }
        if (!CollectionUtil.isEmpty(variableRuleList)) {
            insertVariableRuleBatch(variableRuleList);
        }
        if (!CollectionUtil.isEmpty(kpiRuleList)) {
            insertKpiRuleBatch(kpiRuleList);
        }
    }



}

