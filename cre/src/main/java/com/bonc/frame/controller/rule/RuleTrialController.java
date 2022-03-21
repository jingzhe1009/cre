package com.bonc.frame.controller.rule;

import com.bonc.frame.engine.EngineManager;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.rulelog.ModelTestLog;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissionsRequires;
import com.bonc.frame.service.reference.RuleReferenceService;
import com.bonc.frame.service.rule.RuleDetailService;
import com.bonc.frame.service.rulelog.ModelTestLogService;
import com.bonc.frame.service.rulelog.RuleLogService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.util.ConstantUtil;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.ExceptionUtil;
import com.bonc.frame.util.ResponseResult;
import com.bonc.framework.rule.executor.context.impl.ExecutorResponse;
import com.bonc.framework.rule.executor.context.impl.ModelExecutorType;
import com.bonc.framework.util.IdUtil;
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

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模型试算
 * <p>
 * 执行前需要输入指标值，指标值不需要再通过数据源或接口的方式获取
 *
 * @author qxl
 * @version 1.0
 * @date 2018年4月9日 上午10:35:28
 */
@Controller
@RequestMapping("/ruleTrial")
public class RuleTrialController {
    private Log log = LogFactory.getLog(getClass());

    @Autowired
    private VariableService variableService;

    @Autowired
    private RuleDetailService ruleDetailService;

    @Autowired
    private RuleReferenceService ruleReferenceService;

    @Autowired
    private ModelTestLogService modelTestLogService;

    @Autowired
    private RuleLogService ruleLogService;


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
            throw new Exception("模型不存在[" + ruleId + "]");
        }

        //根据模型Id获取变量列表
//        List<Map<String, Object>> variableList = variableService.getVariableByRuleId(ruleId, folderId);

        // 获取模型引用的所有非实体（但包括实体参数内的嵌套参数）参数 包括接口引用的参数
        List<Map<String, Object>> variableList = variableService.getVariableMapWithoutKpiByRuleId(ruleId, "K1");
//        variableList.addAll(pubVariableMapList);

        // 获取模型引用的指标
        List<Map<String, Object>> kpiMapList = ruleReferenceService.selectKpiInfoByRuleId(ruleId);

        // 输出参数
        List<Map<String, Object>> outVariableList = variableService.getOutVariableMapByRuleId(ruleId);

        model.addAttribute("variableList", JsonUtils.beanToJson(variableList).toString());
        model.addAttribute("kpiMapList", JsonUtils.beanToJson(kpiMapList).toString());
        model.addAttribute("outVariableList", JsonUtils.beanToJson(outVariableList).toString());
        model.addAttribute("ruleId", ruleId);
        model.addAttribute("ruleName", ruleName);
        model.addAttribute("moduleName", moduleName);
        model.addAttribute("version", rule.getVersion());
        model.addAttribute("folderId", folderId);
        model.addAttribute("ruleContent", rule.getRuleContent());
        model.addAttribute("idx", folderId);
        model.addAttribute("childOpen", childOpen);
        return "/pages/rule/ruleTrial";
    }

    @RequestMapping("/index/offer")
    public String ruleTestIndexOffer(String ruleId, String folderId, String childOpen, String ruleName, String moduleName, Model model) throws Exception {
        if (StringUtils.isBlank(ruleId)) {
            throw new IllegalArgumentException("参数[ruleId]不能为空");
        }
        if (StringUtils.isBlank(folderId)) {
            throw new IllegalArgumentException("参数[folderId]不能为空");
        }

        // 获取模型引用的所有非实体（但包括实体参数内的嵌套参数）参数 包括接口引用的参数
        List<Map<String, Object>> variableList = variableService.getVariableMapWithoutKpiByRuleId(ruleId, "K1");

        // 获取模型引用的指标
        List<Map<String, Object>> kpiMapList = ruleReferenceService.selectKpiInfoByRuleId(ruleId);

        // 输出参数
        List<Map<String, Object>> outVariableList = variableService.getOutVariableMapByRuleId(ruleId);

        RuleDetailWithBLOBs rule = ruleDetailService.getRule(ruleId);
        if (rule == null) {
            throw new Exception("There is no rule[" + ruleId + "].");
        }

        model.addAttribute("variableList", JsonUtils.beanToJson(variableList).toString());
        model.addAttribute("outVariableList", JsonUtils.beanToJson(outVariableList).toString());
        model.addAttribute("kpiMapList", JsonUtils.beanToJson(kpiMapList).toString());
        model.addAttribute("ruleId", ruleId);
        model.addAttribute("ruleName", ruleName);
        model.addAttribute("moduleName", moduleName);
        model.addAttribute("version", rule.getVersion());
        model.addAttribute("folderId", folderId);
        model.addAttribute("ruleContent", rule.getRuleContent());
        model.addAttribute("idx", folderId);
        model.addAttribute("childOpen", childOpen);
        model.addAttribute("token", ControllerUtil.getToken());
        return "/pages/rule/ruleTrialOffer";
    }

    /**
     * 试算
     *
     * @param ruleId
     * @param folderId
     * @param paramStr         指标和参数
     * @param isSkipValidation
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/trial")
    @ResponseBody
    public ResponseResult trial(String ruleId, String folderId, String paramStr,
                                @RequestParam(required = false, defaultValue = "false") boolean isSkipValidation,
                                @RequestParam(required = false) String loaderKpiStrategyType,
                                HttpServletRequest request) {
        if (StringUtils.isBlank(ruleId)) {
            throw new IllegalArgumentException("参数[ruleId]不能为空");
        }
        if (StringUtils.isBlank(folderId)) {
            throw new IllegalArgumentException("参数[folderId]不能为空");
        }

        RuleDetailWithBLOBs ruleDetail = ruleDetailService.getRule(ruleId);
        if (ruleDetail == null) {
            return ResponseResult.createFailInfo("找不到模型[" + ruleId + "]");
        }

        if (StringUtils.isBlank(ruleDetail.getIsPublic())) {
            throw new IllegalArgumentException("参数[isPublic]不能为空");
        }

        // 校验权限
        RuleTrialController currentProxy = (RuleTrialController) AopContext.currentProxy();
        if ("1".equals(ruleDetail.getIsPublic())) {
            currentProxy.checkAuthTrialForPub(ruleDetail.getRuleName());
        } else {
            currentProxy.checkAuthTrial(ruleDetail.getRuleName());
        }

        Map<String, Object> paramMap = JsonUtils.toBean(paramStr, HashMap.class);
        log.info("试算接口--试算模型");
        if (log.isDebugEnabled()) {
            String debugInfo = String.format("试算接口--试算模型[ruleId: %s, folderId: %s, paramStr: %s]", ruleId, folderId, paramStr);
            log.debug(debugInfo);
        }

        ModelTestLog modelTestLog = new ModelTestLog();
        String logId = IdUtil.createId();
        modelTestLog.setLogId(logId);
        modelTestLog.setModelId(ruleId);
        modelTestLog.setModelName(ruleDetail.getModuleName());
        modelTestLog.setModelVersion(ruleDetail.getVersion());
        modelTestLog.setOperateIsSuccess(ConstantUtil.TRIAL_INIT);

        // FIXME: 为业务团队对接临时实现的一种方案 真实用户
        if (log.isDebugEnabled()) {
            log.debug("试算开始获取真实用户");
        }
        String realUser = ControllerUtil.getRealUserAndUserAccount(request);

        if (log.isDebugEnabled()) {
            log.debug("获取到token中的用户：" + realUser);
        }

        if (StringUtils.isBlank(realUser)) {
            realUser = ControllerUtil.getCurrentUser();
            if (log.isDebugEnabled()) {
                log.debug("获取到session中的用户：" + realUser);
            }
        }
        modelTestLog.setOperatePerson(realUser);

        modelTestLog.setOperateContent(paramStr);

        ExecutorResponse executorResponse = null;
        try {
            executorResponse = EngineManager.getInstance().trailRule(folderId, ruleId, paramMap, isSkipValidation,
                    false, loaderKpiStrategyType, ModelExecutorType.TRIAL);
            modelTestLog.setOperateResult(JsonUtils.collectToString(paramMap));
            modelTestLog.setExecutorRuleLogId(executorResponse.getExecutorLogId());
            modelTestLogService.insert(modelTestLog);
            if (!executorResponse.isSuccessed()) {
                return ResponseResult.createFailInfo(executorResponse.getMessage());
            } else {
                Map<String, Object> result = new HashMap<>(3);
                result.put("logId", logId);
                result.put("paramMap", paramMap);

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
                result.put("paramNameValueMap", paramNameValueMap);
                result.put("executorDetail", executorResponse.getExecutorDetail());
                result.put("executorLogId", executorResponse.getExecutorLogId());
                return ResponseResult.createSuccessInfo("end", JsonUtils.collectToString(result));
            }
        } catch (Exception e) {
            log.error("试算模型失败", e);
            String str = ExceptionUtil.getStackTrace(e);
            Map<String, String> errorMsg = new HashMap<>(1);
            errorMsg.put("e", str);
            if (executorResponse != null) {
                modelTestLog.setExecutorRuleLogId(executorResponse.getExecutorLogId());
            }
            modelTestLog.setOperateResult(JsonUtils.collectToString(errorMsg));
            modelTestLog.setOperateIsSuccess(ConstantUtil.TRIAL_FAILED);
            modelTestLogService.insert(modelTestLog);
            return ResponseResult.createFailInfo(e.getMessage(), JsonUtils.collectToString(errorMsg));
        }
    }

    @RequestMapping("/trial/success")
    @ResponseBody
    public ResponseResult trialSuccess(String logId) {
        return modelTestLogService.trialSuccess(logId);
    }

    @RequestMapping("/trial/failed")
    @ResponseBody
    public ResponseResult trialFailed(String logId) {
        return modelTestLogService.trialFailed(logId);
    }

    /**
     * @param ruleName ruleName在这里是模型头的id(模型表中的ruleName)
     * @return
     */
    @RequestMapping("/trial/history")
    @ResponseBody
    public Map<String, Object> trialHistory(String ruleName,
                                            String modelId,
                                            String operateType,
                                            String operateIsSuccess,
                                            String startDate, String endDate,
                                            String sort, String order,
                                            String start, String length) {
        return modelTestLogService.getModelTestLogList(ruleName, modelId, operateType, operateIsSuccess,
                startDate, endDate, sort, order, start, length);
    }

    /**
     * @return
     */
    @RequestMapping("/trial/historyDetail")
    @ResponseBody
    public Map<String, Object> trialHistoryDetail(String modelTrialLogId, String start, String size) {
        return ruleLogService.getRuleLogDetail(modelTrialLogId, "task", start, size);
    }

    @PermissionsRequires(value = "/rule/trial?ruleName", resourceType = ResourceType.DATA_MODEL)
    @RequestMapping(value = "/trial/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthTrial(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/pub/rule/trial?ruleName", resourceType = ResourceType.DATA_PUB_MODEL)
    @RequestMapping(value = "/pub/trial/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthTrialForPub(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }


    @RequestMapping("/getVariableAndKpiDesc")
    @ResponseBody
    public ResponseResult getVariableAndKpiDesc(String ruleName, String ruleId) throws Exception {
        Map<String, Object> results = ruleReferenceService.getVariableKpiCodeAndName(ruleName, ruleId);
        return ResponseResult.createSuccessInfo("", results);
    }

}
