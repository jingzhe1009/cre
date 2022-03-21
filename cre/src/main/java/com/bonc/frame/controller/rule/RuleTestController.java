package com.bonc.frame.controller.rule;

import com.bonc.frame.engine.EngineManager;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissionsRequires;
import com.bonc.frame.service.rule.RuleDetailService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.util.ExceptionUtil;
import com.bonc.frame.util.ResponseResult;
import com.bonc.framework.rule.executor.context.impl.ExecutorResponse;
import com.bonc.framework.rule.executor.context.impl.ModelExecutorType;
import com.bonc.framework.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 规则测试
 *
 * @author qxl
 * @version 1.0
 * @date 2018年4月9日 上午10:35:28
 */
@Controller
@RequestMapping("/ruleTest")
public class RuleTestController {
    private Log log = LogFactory.getLog(getClass());

    @Autowired
    private VariableService variableService;

    @Autowired
    private RuleDetailService ruleDetailService;

    //规则测试主页面
    @RequestMapping("/index")
    public String ruleTestIndex(String ruleId, String folderId, String childOpen, String ruleName, String moduleName, Model model) throws Exception {
        if (StringUtils.isBlank(ruleId)) {
            throw new IllegalArgumentException("参数[ruleId]不能为空");
        }
        if (StringUtils.isBlank(folderId)) {
            throw new IllegalArgumentException("参数[folderId]不能为空");
        }

        RuleDetailWithBLOBs rule = ruleDetailService.getRule(ruleId);
        if (rule == null) {
            throw new IllegalArgumentException("模型不存在[" + ruleId + "].");
        }

        // 获取模型引用的非实体（但包括实体参数内的嵌套参数）公共参数 包括接口、指标引用的参数
        List<Map<String, Object>> variableList = variableService.getVariableMapByRuleId(ruleId, "K1");

        model.addAttribute("variableList", variableList);
        model.addAttribute("ruleId", ruleId);
        model.addAttribute("ruleName", ruleName);
        model.addAttribute("moduleName", moduleName);
        model.addAttribute("folderId", folderId);
        model.addAttribute("idx", folderId);
        model.addAttribute("childOpen", childOpen);
        return "/pages/rule/ruleTest";
    }

    //测试
//	@PermissionsRequires(value = "/rule/test?ruleId", resourceType = ResourceType.DATA_MODEL)
    @SuppressWarnings("unchecked")
    @RequestMapping("/test")
    @ResponseBody
    public ResponseResult test(String ruleId, String folderId, String paramStr,
                               @RequestParam(required = false, defaultValue = "false") boolean isSkipValidation,
                               @RequestParam(required = false) String loaderKpiStrategyType) {
        if (StringUtils.isBlank(ruleId)) {
            throw new IllegalArgumentException("参数[ruleId]不能为空");
        }
        if (StringUtils.isBlank(folderId)) {
            throw new IllegalArgumentException("参数[folderId]不能为空");
        }

        RuleDetailWithBLOBs ruleDetail = ruleDetailService.getRule(ruleId);
        if (ruleDetail == null) {
            return ResponseResult.createFailInfo("ERROR", "找不到模型[" + ruleId + "]");
        }

        // 校验权限
        RuleTestController currentProxy = (RuleTestController) AopContext.currentProxy();
        if ("1".equals(ruleDetail.getIsPublic())) {
            currentProxy.checkAuthTestForPub(ruleDetail.getRuleName());
        } else {
            currentProxy.checkAuthTest(ruleDetail.getRuleName());
        }

       /* boolean isRunning = ConstantUtil.RULE_STATUS_RUNNING.equals(ruleDetail.getRuleStatus());
        if (isRunning) {
            return ResponseResult.createFailInfo("ERROR", "模型正在执行中，不能进行试算.");
        }*/

        Map<String, Object> paramMap = JsonUtils.toBean(paramStr, HashMap.class);

        log.info("测试接口--测试模型");
        if (log.isDebugEnabled()) {
            String debugInfo = String.format("测试接口--测试模型[ruleId: %s, folderId: %s, paramStr: %s]", ruleId, folderId, paramStr);
            log.debug(debugInfo);
        }

        try {
            ExecutorResponse executorResponse = EngineManager.getInstance().testRule(folderId, ruleId, paramMap,
                    isSkipValidation, true, loaderKpiStrategyType, ModelExecutorType.TEST);
            if (!executorResponse.isSuccessed()) {
                return ResponseResult.createFailInfo(executorResponse.getMessage());
            } else {
                // 增加参数名-结果值
                Map<Object, Object> paramNameValueMap = new HashMap<>(paramMap.size());
                // 获取模型引用的所有输出非实体（但包括实体参数内的嵌套参数）参数
                List<Map<String, Object>> variableList = variableService.getOutVariableMapByRuleId(ruleId);
                for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                    String variableCode = entry.getKey();
                    Object variableResult = entry.getValue();
                    if (StringUtils.isBlank(variableCode)) {
                        continue;
                    }
                    for (Map<String, Object> outputVaribale : variableList) {
                        if (variableCode.equals(outputVaribale.get("variableCode"))) {
                            paramNameValueMap.put(outputVaribale.get("variableAlias"), variableResult);
                        }
                    }
                }

                Map<String, Object> result = new HashMap<>(2);
                result.put("paramMap", paramMap);
                result.put("paramNameValueMap", paramNameValueMap);

                return ResponseResult.createSuccessInfo("end", JsonUtils.collectToString(result));
            }
        } catch (Exception e) {
            log.error("测试模型失败", e);
            String str = ExceptionUtil.getStackTrace(e);
            Map<String, String> errorMsg = new HashMap<String, String>();
            errorMsg.put("message", "执行异常");
            errorMsg.put("e", str);
            return ResponseResult.createFailInfo("ERROR", JsonUtils.collectToString(errorMsg));
        }
    }

    @PermissionsRequires(value = "/pub/rule/test?ruleName", resourceType = ResourceType.DATA_PUB_MODEL)
    @RequestMapping(value = "/pub/test/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthTestForPub(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/rule/test?ruleName", resourceType = ResourceType.DATA_MODEL)
    @RequestMapping(value = "/test/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthTest(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }

}
