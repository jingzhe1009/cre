package com.bonc.frame.service.impl.reference;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.kpi.KpiRuleSet;
import com.bonc.frame.entity.variable.reference.VariableRuleSet;
import com.bonc.frame.service.reference.RuleSetReferenceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公共规则集的引用管理类
 *
 * @author yedunyao
 * @date 2019/9/24 16:58
 */
@Service("ruleSetReferenceService")
public class RuleSetReferenceServiceImpl implements RuleSetReferenceService {

    @Autowired
    private DaoHelper daoHelper;

    /**
     * 规则集-参数引用中间表
     */
    private static final String _VARIABLE_RULE_SET_MAPPER = "com.bonc.frame.mapper.variable.VariableRuleSetMapper.";

    /**
     * 规则集-指标引用中间表
     */
    private final String _KPI_RULE_SET_PREFIX = "com.bonc.frame.mapper.kpi.KpiRuleSetMapper.";

    // ====== 规则集对参数、指标的引用 ======

    // ------------ 规则集-参数（公有） ------------

    public static VariableRuleSet buildVariableRuleSet(String ruleSetId, String varId) {
        final VariableRuleSet variableRuleSet = new VariableRuleSet();
        variableRuleSet.setRuleSetId(ruleSetId);
        variableRuleSet.setVariableId(varId);
        return variableRuleSet;
    }

    /**
     * 获取规则集对参数（公有）的引用
     */
    @Override
    public List<String> selectVariableIdsByRuleSetId(String ruleSetId) {
        if (StringUtils.isBlank(ruleSetId)) {
            throw new IllegalArgumentException("参数[ruleSetId]不能为空");
        }
        List<String> variableIds = daoHelper.queryForList(_VARIABLE_RULE_SET_MAPPER +
                "selectVariableIdsByRuleSetId", ruleSetId);
        return variableIds;
    }

    /**
     * 批量插入规则集-参数引用关系
     *
     * @param ruleSetId
     * @param newVarIds
     */
    @Override
    @Transactional
    public void insertVariableRuleSetBatch(String ruleSetId, List<String> newVarIds) {
        if (StringUtils.isBlank(ruleSetId) || newVarIds == null || newVarIds.isEmpty()) {
            return;
        }
        List<VariableRuleSet> needInsertList = new ArrayList<>(newVarIds.size());
        for (String varId : newVarIds) {
            final VariableRuleSet variableRuleSet = buildVariableRuleSet(ruleSetId, varId);
            needInsertList.add(variableRuleSet);
        }
        daoHelper.insertBatch(_VARIABLE_RULE_SET_MAPPER + "insertBatch", needInsertList);
    }

    /**
     * 批量删除规则集-参数引用关系
     *
     * @param ruleSetId
     * @param needDeleteVarIds
     */
    @Override
    @Transactional
    public void deleteVariableRuleSetBatch(String ruleSetId, List<String> needDeleteVarIds) {
        if (StringUtils.isBlank(ruleSetId) || needDeleteVarIds == null || needDeleteVarIds.isEmpty()) {
            return;
        }
        List<VariableRuleSet> needDeleteList = new ArrayList<>(needDeleteVarIds.size());
        for (String varId : needDeleteVarIds) {
            final VariableRuleSet variableRuleSet = buildVariableRuleSet(ruleSetId, varId);
            needDeleteList.add(variableRuleSet);
        }
        daoHelper.delete(_VARIABLE_RULE_SET_MAPPER + "deleteBatchByVariableIdsRuleSetIds", needDeleteList);
    }

    /**
     * 删除规则集-参数引用关系
     *
     * @param ruleSetId
     */
    @Override
    @Transactional
    public void deleteVariableRuleSet(String ruleSetId) {
        daoHelper.delete(_VARIABLE_RULE_SET_MAPPER + "deleteByRuleSetId", ruleSetId);
    }

    /**
     * 根据规则集头id删除其下所有版本引用的参数
     *
     * @param ruleSetHeaderId
     */
    @Override
    @Transactional
    public void deleteVariablesByRuleSetHeaderId(String ruleSetHeaderId) {
        daoHelper.delete(_VARIABLE_RULE_SET_MAPPER + "deleteByRuleSetHeaderId", ruleSetHeaderId);
    }

    // --------------------规则集-指标-----------------------------

    public static KpiRuleSet buildKpiRuleSet(String ruleSetId, String kpiId) {
        final KpiRuleSet kpiRuleSet = new KpiRuleSet();
        kpiRuleSet.setRuleSetId(ruleSetId);
        kpiRuleSet.setKpiId(kpiId);
        return kpiRuleSet;
    }

    /**
     * 通过公共规则集id获取该公共规则集引用的所有指标的id
     *
     * @param ruleSetId
     * @return
     */
    @Override
    public List<String> selectKpiIdsByRuleSetId(String ruleSetId) {
        if (StringUtils.isBlank(ruleSetId)) {
            throw new IllegalArgumentException("参数[ruleSetId]不能为空");
        }
        return daoHelper.queryForList(_KPI_RULE_SET_PREFIX +
                "selectKpiIdsByRuleSetId", ruleSetId);
    }

    /**
     * 获取规则集引用的所有指标信息
     *
     * @param ruleSetId
     * @return
     */
    @Override
    public List<Map<String, Object>> selectKpiInfoByRuleSetId(String ruleSetId) {
        if (StringUtils.isBlank(ruleSetId)) {
            throw new IllegalArgumentException("参数[ruleSetId]不能为空");
        }
        return daoHelper.queryForList(_KPI_RULE_SET_PREFIX +
                "selectKpiInfoByRuleSetId", ruleSetId);
    }

    /**
     * 通过指标id获取 引用该指标的所有公共规则集的id
     *
     * @param kpiId
     * @return
     */
    @Override
    public List<String> selectRuleSetByKpiId(String kpiId) {
        if (StringUtils.isBlank(kpiId)) {
            throw new IllegalArgumentException("参数[kpiId]不能为空");
        }
        return daoHelper.queryForList(_KPI_RULE_SET_PREFIX +
                "selectRuleSetByKpiId", kpiId);
    }

    /**
     * 通过指标id获取 获取改指标的被引用数
     *
     * @param kpiId
     * @return
     */
    @Override
    public int countKpiUsedByRuleSet(String kpiId) {
        if (StringUtils.isBlank(kpiId)) {
            throw new IllegalArgumentException("参数[kpiId]不能为空");
        }
        return (int) daoHelper.queryOne(_KPI_RULE_SET_PREFIX +
                "countByKpiId", kpiId);
    }


    /**
     * 批量插入公共规则集-指标引用关系
     *
     * @param ruleSetId
     * @param newKpiIds
     */
    @Override
    @Transactional
    public void insertKpiRuleSetBatch(String ruleSetId, List<String> newKpiIds) {
        if (StringUtils.isBlank(ruleSetId) || newKpiIds == null || newKpiIds.isEmpty()) {
            return;
        }
        List<KpiRuleSet> needInsertList = new ArrayList<>(newKpiIds.size());
        for (String kpiId : newKpiIds) {
            final KpiRuleSet kpiRuleSet = buildKpiRuleSet(ruleSetId, kpiId);
            needInsertList.add(kpiRuleSet);
        }
        daoHelper.insertBatch(_KPI_RULE_SET_PREFIX + "insertBatch", needInsertList);
    }


    /**
     * 批量删除公共规则集-指标引用关系
     *
     * @param ruleSetId
     * @param needDeleteKpiIds
     */
    @Override
    @Transactional
    public void deleteBatchByRuleSetIdsKpiIds(String ruleSetId, List<String> needDeleteKpiIds) {
        if (StringUtils.isBlank(ruleSetId) || needDeleteKpiIds == null || needDeleteKpiIds.isEmpty()) {
            return;
        }
        List<KpiRuleSet> needDeleteList = new ArrayList<>(needDeleteKpiIds.size());
        for (String kpiId : needDeleteKpiIds) {
            final KpiRuleSet kpiRuleSet = buildKpiRuleSet(ruleSetId, kpiId);
            needDeleteList.add(kpiRuleSet);
        }
        daoHelper.delete(_KPI_RULE_SET_PREFIX + "deleteBatchByRuleSetIdsKpiIds", needDeleteList);
    }

    /**
     * 删除公共规则集所有的指标引用关系
     *
     * @param ruleSetId
     */
    @Override
    @Transactional
    public void deleteKpiByRuleSetId(String ruleSetId) {
        daoHelper.delete(_KPI_RULE_SET_PREFIX + "deleteByRuleSetId", ruleSetId);
    }

    /**
     * 删除规则集-指标之间的引用关系
     *
     * @param ruleSetId
     * @param kpiId
     */
    @Override
    @Transactional
    public void deleteByRuleSetIdKpiId(String ruleSetId, String kpiId) {
        Map<String, String> param = new HashMap<>();
        param.put("ruleSetId", ruleSetId);
        param.put("kpiId", kpiId);

        daoHelper.delete(_KPI_RULE_SET_PREFIX + "deleteByRuleSetIdKpiId", param);
    }

}
