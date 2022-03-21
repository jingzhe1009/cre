package com.bonc.frame.engine.builder;

import com.bonc.frame.entity.function.RuleFunction;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.variable.VariableExt;
import com.bonc.frame.service.kpi.KpiService;
import com.bonc.frame.util.ConstantUtil;
import com.bonc.frame.util.ResponseResult;
import com.bonc.frame.util.SpringUtils;
import com.bonc.framework.rule.RuleEngineFactory;
import com.bonc.framework.rule.RuleType;
import com.bonc.framework.rule.exception.LoadContextException;
import com.bonc.framework.rule.exception.ParseRuleException;
import com.bonc.framework.rule.executor.IConContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 核心规则引擎构造实现
 *
 * @author qxl
 * @version 1.0
 * @date 2018年4月2日 下午6:52:42
 */
public class RuleEngineBuilder extends AbstractEngineBuilder {

    private final Log log = LogFactory.getLog(RuleEngineBuilder.class);

    private final String _mybitsId = "com.bonc.frame.engine.mapper.RuleEngineMapper.";
    private final String _entity_mybitsId = "com.bonc.frame.engine.mapper.EntityEngineMapper.";

    private final KpiService kpiService = (KpiService) SpringUtils.getBean("kpiService");


//	private final String FUN_PREFIX = "f_";

    @Override
    public void builder(String folderId, String ruleId, boolean isTest) throws Exception {
        if (StringUtils.isBlank(ruleId)) {
            throw new IllegalArgumentException("参数[ruleId]不能为空");
        }
        if (StringUtils.isBlank(folderId)) {
            throw new IllegalArgumentException("参数[folderId]不能为空");
        }

        //编译单个规则
        if (this.isBuild(folderId, ruleId)) {
            return;
        }
        // 先查询正式表，增加查询草稿表
        RuleDetailWithBLOBs ruleDetail = getModel(ruleId);
        if (ruleDetail == null) {
            throw new LoadContextException("模型不存在,ruleId:[" + ruleId + "]");
        }
        boolean isLog = false;
        if ("1".equals(ruleDetail.getIsLog())) {
            isLog = true;
        }
        if (!isTest && !ConstantUtil.RULE_STATUS_RUNNING.equals(ruleDetail.getRuleStatus())) {
            throw new LoadContextException("模型未启用，ruleId:[" + ruleId + "]");
        }
        this.compileRule(folderId, ruleDetail.getRuleId(), ruleDetail.getRuleType(),
                ruleDetail.getRuleName(), ruleDetail.getRuleContent(), isLog);
    }

    /**
     * 编译规则
     */
    private void compileRule(String folderId, String ruleId, String ruleType, String ruleName,
                             String ruleContent, boolean isLog) throws Exception {
        if (this.isBuild(folderId, ruleId)) {
            return;
        }
        RuleEngineFactory.getRuleEngine().clean(folderId, ruleId);
//		ruleContent = parseVariable(ruleContent, folderId);//转换变量
        //外部类加载初始化后的对象的map
//		ruleContent = parseFunction(ruleContent,folderId);//转换函数
        try {
            buildConContext(folderId, ruleId);
        } catch (Exception e) {
            throw new LoadContextException("创建模型执行上下文环境失败:" + e.getMessage() +
                    "folderId:[" + folderId + "],ruleId:[" + ruleId + "]", e);
        }
        //编译规则
        try {
            RuleEngineFactory.getRuleEngine().compileRule(folderId, ruleId,
                    RuleType.parseToEnum(ruleType), ruleName, ruleContent, isLog);
        } catch (Exception e) {
            throw new ParseRuleException("解析模型失败:" + e.getMessage() +
                    ",folderId:[" + folderId + "],ruleId:[" + ruleId + "]", e);
        }
        this.buildOver(folderId, ruleId);
    }

    public IConContext buildConContext(String folderId, String ruleId) throws Exception {
        IConContext conContext = RuleEngineFactory.getRuleEngine().getConContext();
        try {
            List<VariableExt> variableExts = this.getDaoHelper().queryForList(_entity_mybitsId + "selectByFolder", folderId);

            //  获取场景下模型引用的所有公共参数
            List<VariableExt> pubVariableExts = this.getDaoHelper().queryForList(_entity_mybitsId + "getPubVariableByFolderId", folderId);
            variableExts.addAll(pubVariableExts);


            List<com.bonc.framework.rule.executor.entity.VariableExt> ruleVariableExts = new ArrayList<>();
            for (VariableExt variableExt : variableExts) {
                com.bonc.framework.rule.executor.entity.VariableExt ruleVariableExt =
                        new com.bonc.framework.rule.executor.entity.VariableExt();
                org.springframework.beans.BeanUtils.copyProperties(variableExt, ruleVariableExt);
                ruleVariableExts.add(ruleVariableExt);
            }
            conContext.setVariables(folderId, ruleVariableExts);
        } catch (Exception e) {
//            log.error("加载模型引用参数失败。" + e.getMessage(), e);
            throw new LoadContextException("加载模型引用参数失败。", e);
        }
        try {
            List<RuleFunction> funList = this.getDaoHelper().queryForList(_mybitsId + "getFunList", folderId);

            List<com.bonc.framework.rule.executor.entity.RuleFunction> ruleFunList = new ArrayList<>();
            for (RuleFunction func : funList) {
                com.bonc.framework.rule.executor.entity.RuleFunction ruleFunc =
                        new com.bonc.framework.rule.executor.entity.RuleFunction();
                org.springframework.beans.BeanUtils.copyProperties(func, ruleFunc);
                ruleFunList.add(ruleFunc);
            }
            conContext.setRuleFunctions(folderId, ruleFunList);
        } catch (Exception e) {
//            log.error("加载模型引用函数失败。" + e.getMessage(), e);
            throw new LoadContextException("加载模型引用函数失败。", e);
        }

        // 获取模型引用的指标
        try {
            ResponseResult kpiBaseInfoByRuleId = kpiService.getKpiBaseInfoByRuleIdNoAuth(ruleId);
            List<KpiDefinition> kpiDefinitions = (List<KpiDefinition>) kpiBaseInfoByRuleId.getData();

            List<com.bonc.framework.rule.executor.entity.kpi.KpiDefinition> kpiList = new ArrayList<>();
            for (KpiDefinition kpiDefinition : kpiDefinitions) {
                com.bonc.framework.rule.executor.entity.kpi.KpiDefinition kpi =
                        new com.bonc.framework.rule.executor.entity.kpi.KpiDefinition();
                org.springframework.beans.BeanUtils.copyProperties(kpiDefinition, kpi);
                kpiList.add(kpi);
            }
            conContext.setKpiDefinitions(ruleId, kpiList);
        } catch (Exception e) {
//            log.error("加载模型引用指标失败。" + e.getMessage(), e);
            throw new LoadContextException("加载模型引用指标失败。", e);
        }
        return conContext;
    }

    @Override
    public boolean isBuild(String folderId, String ruleId) {
        if (ruleId == null || ruleId.isEmpty()) {
            return super.isBuild(folderId, ruleId);
        }
        if ("true".equals(isBuildCache.get(ruleId))) {
            return true;
        }
        return false;
    }

    @Override
    public void clean(String folderId, String ruleId) {
        super.clean(folderId, ruleId);
        if (isBuildCache.containsKey(ruleId)) {
            isBuildCache.remove(ruleId);
        }
        RuleEngineFactory.getRuleEngine().clean(folderId, ruleId);
    }

    @Override
    public void buildOver(String folderId, String ruleId) {
        if (ruleId == null || ruleId.isEmpty()) {
            super.isBuild(folderId, ruleId);
            return;
        }
        isBuildCache.put(ruleId, "true");
    }

    @Override
    void build(String folderId, String ruleId, boolean isTest) throws Exception {
    }

    private RuleDetailWithBLOBs getModel(String ruleId) {
        RuleDetailWithBLOBs ruleDetail = (RuleDetailWithBLOBs) this.getDaoHelper()
                .queryOne(_mybitsId + "selectRuleDetailByRuleId", ruleId);
        if (ruleDetail == null) {
            // 从草稿表中查询
            ruleDetail = (RuleDetailWithBLOBs) this.getDaoHelper()
                    .queryOne(_mybitsId + "selectRuleDetailByRuleIdForDraft", ruleId);
        }
        return ruleDetail;
    }
}
