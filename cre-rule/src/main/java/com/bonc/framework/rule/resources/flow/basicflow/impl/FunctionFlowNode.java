package com.bonc.framework.rule.resources.flow.basicflow.impl;

import com.bonc.framework.rule.RuleEngineFactory;
import com.bonc.framework.rule.executor.entity.RuleFunction;
import com.bonc.framework.rule.executor.entity.VariableExt;
import com.bonc.framework.rule.resources.flow.INodeParse;
import com.bonc.framework.rule.resources.flow.annotation.FlowNodeAnno;
import com.bonc.framework.rule.resources.flow.basicflow.AbstractFlowNode;
import com.bonc.framework.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qxl
 * @version 1.0.0
 * @date 2016年11月14日 上午10:10:43
 */
@FlowNodeAnno(type = "funcMethod")
public class FunctionFlowNode extends AbstractFlowNode implements INodeParse {
    private static final long serialVersionUID = -525953309078652209L;

    private String function;

    private final String FUN_PREFIX = "f_";

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = parse(function);
    }

    @Override
    public void serialize(Map<String, String> data) {
        if (data.containsKey("funcMethod")) {
            this.setFunction(data.get("funcMethod"));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public String parse(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        List<RuleFunction> funList = RuleEngineFactory.getRuleEngine().getConContext().queryRuleFunctions(getFolderId());
        List<VariableExt> varList = RuleEngineFactory.getRuleEngine().getConContext().queryVariables(getFolderId());
        Map<String, String> varMap = new HashMap<String, String>();//key-variableId,value-variableCode
        if (varList != null) {
            for (VariableExt var : varList) {
                String variableId = var.getVariableId();
                String aliasCode = var.getEntityVariableAlias();
                String code = var.getVariableCode();
                if (!StringUtils.isBlank(aliasCode) && !StringUtils.isBlank(aliasCode)) {
                    code = aliasCode;
                }
                varMap.put(variableId, code);
            }
        }
        try {
            Map<String, Object> map = JsonUtils.stringToCollect(content);
            if (map == null || map.isEmpty()) {
                return null;
            }
            StringBuffer result = new StringBuffer();
            String funcId = (String) map.get("funcId");
            if (StringUtils.isBlank(funcId)) {
                throw new RuntimeException("The funcId is null.[" + this.getNodeName() + "]");
            }
            String funMethod = "";
            int paramNum = -1;
            if (funList == null || funList.size() == 0) {
                throw new RuntimeException("There is no function in db.");
            }
            for (RuleFunction fun : funList) {
                if (funcId.equals(fun.getFunctionId())) {
                    String functionId = fun.getFunctionId();
                    String classType = fun.getClassType();
                    String classPath = fun.getFunctionClassPath();
                    String className = fun.getClassName();
                    String methodName = fun.getFunctionMethodName();
                    if ("2".equals(classType)) {//系统外部的类
                        funMethod = FUN_PREFIX + functionId + "." + methodName;
                    } else {
                        funMethod = classPath + "." + className + "." + methodName;
                    }
                    try {
                        List<Map> paramList = JsonUtils.toList(fun.getFunctionParamsConf(), Map.class);
                        if (paramList != null) {
                            paramNum = paramList.size();
                        }
                    } catch (Exception e) {
                    }
                    break;
                }
            }
            if (StringUtils.isBlank(funMethod)) {
                throw new RuntimeException("There is no this function is db.[" + funcId + "]");
            }
            //将ID替换成编码
            String rtnResult = varMap.get((String) map.get("rtnResult"));
            if (rtnResult != null && !rtnResult.isEmpty()) {
                result.append(rtnResult).append("=");
            }
            result.append(funMethod).append("(");
            List<Map> paramList = (List<Map>) map.get("params");
            boolean isFirst = true;
            if (paramList == null || paramList.size() != paramNum) {
                throw new RuntimeException("The param number is illeagl.[" + funcId + "]");
            }
            for (Map paramMap : paramList) {
                if (!isFirst) {
                    result.append(",");
                }
                String paramCode = varMap.get(paramMap.get("variableId"));
                if (StringUtils.isBlank(paramCode)) {
                    throw new RuntimeException("The param is illeagl.[" + paramMap.get("variableId") + "]");
                }
                result.append(paramCode);
                isFirst = false;
            }
            result.append(")");
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
