package com.bonc.frame.service.reference;

import java.util.List;
import java.util.Map;

/**
 * 公共规则集的引用管理类
 *
 * @author yedunyao
 * @date 2019/9/24 17:58
 */
public interface RuleSetReferenceService {


    // ------------ 公共规则集-参数（公有） ------------

    /**
     * 获取公共规则集对参数（公有）的引用
     */
    List<String> selectVariableIdsByRuleSetId(String ruleSetId);

    /**
     * 批量插入公共规则集-参数引用关系
     *
     * @param ruleSetId
     * @param newVarIds
     */
    void insertVariableRuleSetBatch(String ruleSetId, List<String> newVarIds);

    /**
     * 批量删除公共规则集-参数引用关系
     *
     * @param ruleSetId
     * @param needDeleteVarIds
     */
    void deleteVariableRuleSetBatch(String ruleSetId, List<String> needDeleteVarIds);

    /**
     * 删除公共规则集-参数引用关系
     *
     * @param ruleSetId
     */
    void deleteVariableRuleSet(String ruleSetId);

    void deleteVariablesByRuleSetHeaderId(String ruleSetHeaderId);

    // -------------公共规则集-指标 -------------

    /**
     * 通过公共规则集id获取该公共规则集引用的所有指标的id
     *
     * @param ruleSetId
     * @return
     */
    List<String> selectKpiIdsByRuleSetId(String ruleSetId);

    /**
     * 获取公共规则集引用的所有指标信息
     *
     * @param ruleSetId
     * @return
     */
    List<Map<String, Object>> selectKpiInfoByRuleSetId(String ruleSetId);

    /**
     * 通过指标id获取 引用该指标的所有公共规则集的id
     *
     * @param kpiId
     * @return
     */
    List<String> selectRuleSetByKpiId(String kpiId);

    /**
     * 通过指标id获取 获取改指标的被引用数
     *
     * @param kpiId
     * @return
     */
    int countKpiUsedByRuleSet(String kpiId);

    /**
     * 批量插入公共规则集-指标引用关系
     *
     * @param ruleSetId
     * @param newKpiIds
     */
    void insertKpiRuleSetBatch(String ruleSetId, List<String> newKpiIds);

    /**
     * 批量删除公共规则集-指标引用关系
     *
     * @param ruleSetId
     * @param needDeleteKpiIds
     */
    void deleteBatchByRuleSetIdsKpiIds(String ruleSetId, List<String> needDeleteKpiIds);

    /**
     * 删除公共规则集所有的指标引用关系
     *
     * @param ruleSetId
     */
    void deleteKpiByRuleSetId(String ruleSetId);

    /**
     * 删除公共规则集-指标之间的引用关系
     *
     * @param ruleSetId
     * @param kpiId
     */
    void deleteByRuleSetIdKpiId(String ruleSetId, String kpiId);


}
