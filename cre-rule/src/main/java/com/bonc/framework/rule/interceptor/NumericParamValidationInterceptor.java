package com.bonc.framework.rule.interceptor;

import com.bonc.framework.rule.RuleEngineFactory;
import com.bonc.framework.rule.exception.RuleInterceptorException;
import com.bonc.framework.rule.executor.IConContext;
import com.bonc.framework.rule.executor.entity.IVariable;
import com.bonc.framework.rule.executor.entity.VariableExt;
import com.bonc.framework.rule.executor.entity.kpi.KpiDefinition;
import com.bonc.framework.rule.resources.RuleResource;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 数值型参数输入校验拦截器
 *
 * @author yedunyao
 * @date 2019/9/18 10:19
 */
public class NumericParamValidationInterceptor implements ValidationInterceptor {

    private final Log log = LogFactory.getLog(NumericParamValidationInterceptor.class);

    /**
     * @param param 输入参数
     * @param rule  {@link com.bonc.framework.rule.resources.RuleResource}
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(Map<String, Object> param, Object rule) throws Exception {
        log.debug("执行模型--参数校验拦截器");

        if (param == null || rule == null) {
            return true;
        }

        RuleResource resource = (RuleResource) rule;
        IConContext conContext = RuleEngineFactory.getRuleEngine().getConContext();
        String folderId = resource.getFolderId();
        String ruleId = resource.getRuleId();

        Preconditions.checkArgument(StringUtils.isNotBlank(folderId), "输入参数[folderId]不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(ruleId), "输入参数[ruleId]不能为空");

        List<VariableExt> variableExts = conContext.queryVariables(folderId);
        // 试算时对输入的指标值进行校验
        List<KpiDefinition> kpiDefinitions = conContext.queryKpiDefinitions(ruleId);
        List<IVariable> iVariableList = new LinkedList<>();
        if (!CollectionUtils.isEmpty(variableExts)) {
            iVariableList.addAll(variableExts);
        }
        if (!CollectionUtils.isEmpty(kpiDefinitions)) {
            iVariableList.addAll(kpiDefinitions);
        }


        if (iVariableList == null || iVariableList.isEmpty()) {
            log.warn(String.format("当前模型[folderId: %s, ruleId: %s]没有使用任何参数或指标", folderId, ruleId));
            return true;
        }

        for (String variableCode : param.keySet()) {
            for (IVariable iVariable : iVariableList) {
                if (variableCode.equals(iVariable.getCode())) {
                    Object inputVariableValue = param.get(variableCode);    // 输入值
                    String variableType = iVariable.getType();       // 变量类型

                    String inputVariableStr = null;
                    // 忽略空值校验
                    if (inputVariableValue == null ||
                            "".equals((inputVariableStr = inputVariableValue.toString().trim()))) {
                        // TODO: 赋予默认值
                        String defaultValue = iVariable.getDefaultValue();
                        /*if (defaultValue == null) {
                            defaultValue = "";
                        }*/
                        param.put(variableCode, defaultValue);
                        continue;
                    }

                    // 只对数值型的参数进行校验
                    if ("2".equals(variableType)) {
                        // 整型
                        try {
                            int value = Integer.parseInt(inputVariableStr);
                            param.put(variableCode, value);
                        } catch (NumberFormatException e) {
                            String variableAlias = iVariable.getName();
                            final String msg = String.format("数值型参数输入格式不合法，" +
                                    "[变量名称: %s, 变量编码: %s, 变量输入值: %s.]", variableAlias, variableCode, inputVariableValue);
                            throw new RuleInterceptorException(msg, e);
                        }
                    } else if ("4".equals(variableType)) {
                        // 浮点型
                        try {
                            double value = Double.parseDouble(inputVariableStr);
                            param.put(variableCode, value);
                        } catch (NumberFormatException e) {
                            String variableAlias = iVariable.getName();
                            final String msg = String.format("数值型参数输入格式不合法，" +
                                    "[变量名称: %s, 变量编码: %s, 变量输入值: %s.]", variableAlias, variableCode, inputVariableValue);
                            throw new RuleInterceptorException(msg, e);
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(Map<String, Object> param, Object rule) throws Exception {

    }

}
