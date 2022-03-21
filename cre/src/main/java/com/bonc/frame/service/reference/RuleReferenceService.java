package com.bonc.frame.service.reference;

import com.bonc.frame.entity.kpi.KpiRule;
import com.bonc.frame.entity.model.ModelContentInfo;
import com.bonc.frame.entity.variable.reference.VariableRule;

import java.util.List;
import java.util.Map;

/**
 * 模型的引用管理类
 *
 * @author yedunyao
 * @date 2019/9/24 17:58
 */
public interface RuleReferenceService {
    /**
     * 获取模型引用的参数和指标的 code和name
     *
     * @return Map<code:name>
     */
    Map<String, Object> getVariableKpiCodeAndName(String ruleName, String ruleId);

    // ------------ 模型-参数（私有、公有） ------------

    // 获取模型对参数（私有、公有）的引用
    List<String> selectVariableIdsByRuleId(String ruleId);

    // 批量插入模型-参数引用关系
    void insertVariableRuleBatch(String ruleId, List<String> newVarIds);

    void insertVariableRuleBatch(List<VariableRule> needInsertList);

    // 批量删除模型-参数引用关系
    void deleteVariableRuleBatch(String ruleId, List<String> needDeleteVarIds);

    // 删除模型-参数引用关系
    void deleteVariableRule(String ruleId);

    // ------------ 模型-接口（私有、公有） ------------

    // 获取模型对接口（私有、公有）的引用
    List<String> selectApiIdsByRuleId(String ruleId);

    // 批量插入模型-接口引用关系
    void insertApiRuleBatch(String ruleId, List<String> newApiIds);

    // 批量删除模型-接口引用关系
    void deleteApiRuleBatch(String ruleId, List<String> needDeleteApiIds);

    // 删除模型-接口引用关系
    void deleteApiRule(String ruleId);

    // ------------ 模型-公共规则集 ------------

    // 获取模型对公共规则集的引用
    List<String> selectRuleSetIdsByRuleId(String ruleId);

    // 批量插入模型-公共规则集引用关系
    void insertRuleSetRuleBatch(String ruleId, List<String> newRuleSetIds);

    // 批量删除模型-公共规则集引用关系
    void deleteRuleSetRuleBatch(String ruleId, List<String> needDeleteRuleSetIds);

    // 删除模型-公共规则集引用关系
    void deleteRuleSetRule(String ruleId);

    // -------------模型-指标 -------------
    // 通过模型id获取该模型引用的所有指标的id
    List<String> selectKpiIdsByRuleId(String ruleId);

    /**
     * 获取模型引用的所有指标信息
     *
     * @param ruleId
     * @return
     */
    List<Map<String, Object>> selectKpiInfoByRuleId(String ruleId);

    List<Map<String, Object>> selectKpiInfoByRuleIdOrRuleName(String ruleName, String ruleId);

    Map<String, Object> getKpiCodeAndNameMap(String ruleName, String ruleId);

    // 通过指标id获取 引用该指标的所有模型的id
    List<String> selectByKpiId(String kpiId);

    // 通过指标id获取 获取改指标的被引用数
    int countByKpiId(String kpiId);

    // 批量插入模型-指标引用关系
    void insertKpiRuleBatch(String ruleId, List<String> newKpiIds);

    void insertKpiRuleBatch(List<KpiRule> needInsertList);

    // 批量删除模型-指标引用关系
    void deleteBatchByRuleIdsKpiIds(String ruleId, List<String> needDeleteKpiIds);

    // 删除模型所有的指标引用关系
    void deleteKpiByRuleId(String ruleId);

    // 删除模型-指标之间的引用关系
    void deleteByRuleIdKpiId(String ruleId, String kpiId);

    void saveVariableRuleAndKpiRule(ModelContentInfo modelContentInfo, String ruleId);


}
