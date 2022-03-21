package com.bonc.framework.rule.executor;

import com.bonc.framework.rule.executor.entity.RuleFunction;
import com.bonc.framework.rule.executor.entity.VariableExt;
import com.bonc.framework.rule.executor.entity.kpi.KpiDefinition;

import java.util.List;

/**
 * @author 作者: jxw
 * @version 版本: 1.0
 * 获取规则上下文环境，包括：输入参数、输出参数、中间变量及所有函数
 * @date 创建时间: 2017年4月13日 下午2:14:48
 */
public interface IConContext {

    public void setVariables(String folderId, List<VariableExt> variableExts);

    public List<VariableExt> queryVariables(String folderId);

    public void setRuleFunctions(String folderId, List<RuleFunction> ruleFunctions);

    public List<RuleFunction> queryRuleFunctions(String folderId);

    void setKpiDefinitions(String ruleId, List<KpiDefinition> kpiDefinitions);

    List<KpiDefinition> queryKpiDefinitions(String ruleId);

    public void clean(String folderId);

    void cleanByRule(String ruleId);

    public void clean();

}

