package com.bonc.frame.controller.rule;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.variable.Variable;
import com.bonc.frame.service.kpi.KpiService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.util.CollectionUtil;
import com.bonc.frame.util.JsonUtils;
import com.bonc.frame.util.ResponseResult;
import com.bonc.framework.rule.executor.context.IQLExpressContext;
import com.bonc.framework.rule.executor.context.impl.QLExpressContext;
import com.bonc.framework.rule.executor.resolver.rule.RuleFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * */
@Controller
@RequestMapping("/rule")
public class ExpRuleConfigController {

    private static Log log = LogFactory.getLog(ExpRuleConfigController.class);

    @Autowired
    private VariableService variableService;

    @Autowired
    private KpiService kpiService;

    //新增规则页面
    @RequestMapping("/expRuleConfig")
    public String main(String folderId, String ruleType, Model model) throws Exception {
        Map<String, Object> variableMap = variableService.getVariableTree(folderId);
        model.addAttribute("inVariableTree", JsonUtils.beanToJson(variableMap.get("inVariableTree")).toString());
        model.addAttribute("outVariableTree", JsonUtils.beanToJson(variableMap.get("outVariableTree")).toString());
        return "/pages/rule/expRuleConfig";
    }

    @RequestMapping("/vaildateRule")
    @ResponseBody
    public ResponseResult vaildateRule(String folderId, String expRule, String references) {
        if (StringUtils.isBlank(references)) {
            // 使用旧版本，不通过引用获取，而是通过参数树获取，但这种方式无法支持指标
            return vaildateRule(folderId, expRule);
        } else {
            return vaildateRuleWithReference(expRule, references);
        }
    }

    /**
     * 校验表达式
     * 根据引用的指标id、参数id获取数据类型并填充默认值进行测试
     *
     * @param expRule
     * @param references
     * @return
     */
    public ResponseResult vaildateRuleWithReference(String expRule, String references) {
        String expRuleStr = expRule;
        JSONArray referenceArray;
        try {
            referenceArray = JSONArray.parseArray(references);
        } catch (Exception e) {
            throw new RuntimeException("编译表达式失败，引用列表不符合json array格式");
        }

        // 表达式中的指标名称
        int variableNameLen = 0;

//        Map<String, String> variableNameMap = new LinkedHashMap<>();
        int referenceLen = referenceArray.size();
        List<VaribleMapping> varibleMappings = new ArrayList<>(referenceLen);
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher m = pattern.matcher(expRuleStr);
        while (m.find()) {
            String replaceStr = m.group(0);
            String replace = m.group(1);
            variableNameLen++;
//            variableNameMap.put(replace, replaceStr);
            varibleMappings.add(new VaribleMapping(replace, replaceStr));
        }


//        Set<Map.Entry<String, String>> variableNameEntries = variableNameMap.entrySet();

        IQLExpressContext<String, Object> context = new QLExpressContext();

        if (variableNameLen != 0) {
            if (referenceLen != variableNameLen) {
                throw new RuntimeException("编译条件表达式失败，条件表达式引用参数列表长度不一致");
            } else {
                int i = 0;
                for (VaribleMapping variableNameEntry : varibleMappings) {
                    String variableName = variableNameEntry.variableName;
                    String variableNamePlaceHolder = variableNameEntry.variableExpress;

                    JSONObject jsonObject = referenceArray.getJSONObject(i);

                    String kpiId = getIVariableIdFromReference(jsonObject, "kpiId");
                    if (kpiId != null) {
                        KpiDefinition kpiDefinition = kpiService.getBaseInfo(kpiId);
                        if (kpiDefinition == null) {
                            throw new RuntimeException("编译条件表达式失败，未找到指标[" + kpiId + "]");
                        }
                        if (variableName.equals(kpiDefinition.getKpiName())) {
                            String kpiCode = kpiDefinition.getKpiCode();
                            expRuleStr = expRuleStr.replace(variableNamePlaceHolder, kpiCode);
                            String typeId = kpiDefinition.getKpiType();
                            assignContext(kpiCode, typeId, context);
                        } else {
                            throw new RuntimeException("编译条件表达式失败，条件表达式引用参数列表指标名称不一致");
                        }
                    } else {
                        String varId = getIVariableIdFromReference(jsonObject, "varId");
                        if (varId == null) {
                            throw new RuntimeException("编译条件表达式失败，条件表达式引用参数列表不合法");
                        }
                        Variable variable = variableService.selectVariableByKey(varId);
                        if (variable == null) {
                            throw new RuntimeException("编译条件表达式失败，未找到参数[" + varId + "]");
                        }
                        if (variableName.equals(variable.getVariableAlias())) {

                            String variableCode = variable.getVariableCode();
                            expRuleStr = expRuleStr.replace(variableNamePlaceHolder, variableCode);
                            String typeId = variable.getTypeId();
                            assignContext(variableCode, typeId, context);
                        } else {
                            throw new RuntimeException("编译条件表达式失败，条件表达式引用参数列表参数名称不一致");
                        }
                    }
                    i++;
                }
            }
        }

        expRuleStr = vaildateDate(expRuleStr);
        return vaildateExpression(expRuleStr, context);
    }

    @Deprecated
    public ResponseResult vaildateRule(String folderId, String expRule) {

        String expRuleStr = expRule;
        String oldExpRuleStr = expRule;
//		IExpressContext<String,Object> context = new DefaultContext<>();
        IQLExpressContext<String, Object> context = new QLExpressContext();

        List<Map<String, Object>> variableTree;
        if (StringUtils.isBlank(folderId)) {
            Map<String, Object> variableMap = variableService.getPubVariableTree();
            variableTree = (List<Map<String, Object>>) variableMap.get("outVariableTree");
        } else {
            Map<String, Object> variableMap = variableService.getVariableTree(folderId);
            variableTree = (List<Map<String, Object>>) variableMap.get("inVariableTree");
        }

        Pattern p = Pattern.compile("\\[(.*?)\\]");//(.*?)]
        Matcher m = p.matcher(expRule);
        while (m.find()) {
            String repaceStr = m.group(0);//[xxx]
            String replace = m.group(1);
            if (variableTree != null) {
                for (Map<String, Object> map : variableTree) {
                    List<Map<String, Object>> children = (List<Map<String, Object>>) map.get("children");
                    expRuleStr = replace(children, replace, repaceStr, expRuleStr, context);
                    if (!oldExpRuleStr.equals(expRuleStr)) {
                        // 如果发生改变说明本次变量替换已成功完成
                        oldExpRuleStr = expRuleStr;
                        break;
                    }
                }
            }
        }
        expRuleStr = vaildateDate(expRuleStr);
        return vaildateExpression(expRuleStr, context);
    }

    private String vaildateDate(String expRuleStr) {
        Pattern datePattern = Pattern.compile("java.text.SimpleDateFormat\\((.*?)\\).parse\\((.*?)\\)");
        Matcher dataMatcher = datePattern.matcher(expRuleStr);
        while (dataMatcher.find()) {
            String replaceStr = dataMatcher.group(0);
            expRuleStr = expRuleStr.replace(replaceStr, "Date()");
        }
        return expRuleStr;
    }

    private String replace(List<Map<String, Object>> children, String replace, String repaceStr, String expRuleStr, IQLExpressContext<String, Object> context) {
        if (!CollectionUtil.isEmpty(children)) {
            for (Map<String, Object> child : children) {
                if (child.containsKey("children")) {
                    List<Map<String, Object>> cs = (List<Map<String, Object>>) child.get("children");
                    expRuleStr = replace(cs, replace, repaceStr, expRuleStr, context);
                }
                String variableName = (String) child.get("name");
                if (replace.equals(variableName)) {
                    String typeId = (String) child.get("typeId");
                    String variableCode = (String) child.get("variableCode");
                    assignContext(variableCode, typeId, context);
                    expRuleStr = expRuleStr.replace(repaceStr, variableCode);
                }
            }
        }
        return expRuleStr;
    }

    private void assignContext(String code, String typeId, IQLExpressContext<String, Object> context) {
        if (typeId.equals("2")) {
            context.put(code, 1);
        } else if (typeId.equals("4")) {
            context.put(code, new Double(1));
        } else {
            context.put(code, "1234567890");
        }
    }

    private ResponseResult vaildateExpression(String expRuleStr, IQLExpressContext<String, Object> context) {
        expRuleStr = "vaildateV = " + expRuleStr;
        log.info("验证表达式编译结果：" + expRuleStr);
        log.info("验证表达式执行环境：" + context.toString());
        try {
            RuleFactory.getRule().execute(expRuleStr, context);
        } catch (Exception e) {
            log.error(e);
            return ResponseResult.createFailInfo();
        }

        return ResponseResult.createSuccessInfo();
    }

    private String getIVariableIdFromReference(JSONObject referenceObject, String id) {
        String idPlaceHolder = referenceObject.getString(id);
        if (StringUtils.isBlank(idPlaceHolder)) {
            return null;
        }
        return idPlaceHolder.replaceAll("\\[|\\]", "");
    }

    class VaribleMapping {
        String variableName;
        String variableExpress;

        public VaribleMapping(String variableName, String variableExpress) {
            this.variableName = variableName;
            this.variableExpress = variableExpress;
        }
    }

}
