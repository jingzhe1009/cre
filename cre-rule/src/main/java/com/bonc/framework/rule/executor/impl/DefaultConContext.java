package com.bonc.framework.rule.executor.impl;

import com.bonc.framework.rule.executor.IConContext;
import com.bonc.framework.rule.executor.entity.RuleFunction;
import com.bonc.framework.rule.executor.entity.VariableExt;
import com.bonc.framework.rule.executor.entity.kpi.KpiDefinition;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultConContext implements IConContext, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 467757570369613020L;

    /**
     * 场景-变量缓存
     */
    private Map<String, List<VariableExt>> variableMap = new ConcurrentHashMap<>(64);
    /**
     * 场景-函数缓存
     */
    private Map<String, List<RuleFunction>> ruleFunctionMap = new ConcurrentHashMap<>(64);
    /**
     * 模型-指标缓存
     */
    private Map<String, List<KpiDefinition>> kpiDefinitionMap = new ConcurrentHashMap<>(256);

    @Override
    public void setVariables(String folderId, List<VariableExt> variableExts) {
        if (variableExts == null) {
            return;
        }
        this.variableMap.put(folderId, variableExts);
    }

    @Override
    public List<VariableExt> queryVariables(String folderId) {
        return this.variableMap.get(folderId);
    }


    @Override
    public void setRuleFunctions(String folderId, List<RuleFunction> ruleFunctions) {
        if (ruleFunctions == null) {
            return;
        }
        this.ruleFunctionMap.put(folderId, ruleFunctions);
    }


    @Override
    public List<RuleFunction> queryRuleFunctions(String folderId) {
        return this.ruleFunctionMap.get(folderId);
    }

    @Override
    public void setKpiDefinitions(String ruleId, List<KpiDefinition> kpiDefinitions) {
        if (kpiDefinitions == null) {
            return;
        }
        this.kpiDefinitionMap.put(ruleId, kpiDefinitions);
    }

    @Override
    public List<KpiDefinition> queryKpiDefinitions(String ruleId) {
        return this.kpiDefinitionMap.get(ruleId);
    }

    @Override
    public void clean(String folderId) {
        variableMap.remove(folderId);
        ruleFunctionMap.remove(folderId);
    }

    @Override
    public void cleanByRule(String ruleId) {
        kpiDefinitionMap.remove(ruleId);
    }

    @Override
    public void clean() {
        variableMap.clear();
        ruleFunctionMap.clear();
        kpiDefinitionMap.clear();
    }

}
