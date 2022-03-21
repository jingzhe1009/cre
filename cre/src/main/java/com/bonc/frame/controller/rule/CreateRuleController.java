package com.bonc.frame.controller.rule;

import com.bonc.frame.config.Config;
import com.bonc.frame.engine.EngineManager;
import com.bonc.frame.entity.rulefolder.RuleFolder;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissionsRequires;
import com.bonc.frame.service.rule.RuleService;
import com.bonc.frame.service.syslog.SysLogService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.FolderMenuUtil;
import com.bonc.frame.util.JsonUtils;
import com.bonc.frame.util.ResponseResult;
import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.executor.context.impl.ModelExecutorType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author jsj
 */
@Controller
@RequestMapping("/")
public class CreateRuleController {

    @Autowired
    private RuleService ruleService;
    @Autowired
    private VariableService variableService;

    @Autowired
    private SysLogService sysLogService;

    @RequestMapping("/create")
    public String main(String ruleType, String ruleFolder, String idx, String childOpen, Model model, HttpServletRequest request) {
        List<Map<String, Object>> ruleTypeList = this.ruleService.getRuleType();
        List<Map<String, Object>> ruleFolderList = this.ruleService.getRuleFolder();
        List<Map<String, Object>> variableTypeList = this.variableService.getVariableType();
        List<Map<String, Object>> variableKindList = this.variableService.getVariableKind();

        model.addAttribute("ruleTypeList", JsonUtils.beanToJson(ruleTypeList).toString());
        model.addAttribute("ruleFolderList", JsonUtils.beanToJson(ruleFolderList).toString());
        model.addAttribute("variableTypeList", JsonUtils.beanToJson(variableTypeList).toString());
        model.addAttribute("variableKindList", JsonUtils.beanToJson(variableKindList).toString());
        model.addAttribute("ruleType", ruleType);
        model.addAttribute("ruleFolder", ruleFolder);
        if (idx == null || idx.isEmpty()) {
            idx = ruleFolder;
        }
        model.addAttribute("idx", idx);//菜单状态标识
        model.addAttribute("childOpen", childOpen);
        return "/pages/variable/createRule";
    }

    @RequestMapping("/ruleType")
    @ResponseBody
    public List<Map<String, Object>> ruleType() {
        List<Map<String, Object>> ruleTypeList = this.ruleService.getRuleType();
        return ruleTypeList;
    }

    @RequestMapping("/ruleFolder")
    @ResponseBody
    public List<Map<String, Object>> ruleFolder(HttpServletRequest request) {
        List<Map<String, Object>> ruleFolderList = this.ruleService.getRuleFolder();
        return ruleFolderList;
    }

    @RequestMapping("/insertRuleFolder")
    @ResponseBody
    public ResponseResult insertRuleFolder(RuleFolder ruleFolder, HttpServletRequest request, HttpSession session) {
        ruleFolder.setCreateDate(new Date());
        ruleFolder.setCreatePerson(ControllerUtil.getLoginUserId(request));
        ResponseResult result = this.ruleService.insertRuleFolder(ruleFolder);
        FolderMenuUtil.setFolderMenu(ruleService, session);
        return result;
    }

    @RequestMapping("/updateRuleFolder")
    @ResponseBody
    public ResponseResult updateRuleFolder(RuleFolder ruleFolder, HttpServletRequest request, HttpSession session) {
        ruleFolder.setUpdateDate(new Date());
        ruleFolder.setUpdatePerson(ControllerUtil.getLoginUserId(request));
        ResponseResult result = this.ruleService.updateRuleFolder(ruleFolder);
        FolderMenuUtil.setFolderMenu(ruleService, session);
        return result;
    }

    @PermissionsRequires(value = "/rule/test?ruleId", resourceType = ResourceType.DATA_MODEL)
    @RequestMapping("/testRule")
    @ResponseBody
    public Map<String, Object> testRule(String folderId, String ruleId, String param,
                                        @RequestParam(required = false, defaultValue = "false") boolean isSkipValidation,
                                        @RequestParam(required = false) String loaderKpiStrategyType) {

        if (StringUtils.isBlank(ruleId)) {
            throw new IllegalArgumentException("参数[ruleId]不能为空");
        }
        if (StringUtils.isBlank(folderId)) {
            throw new IllegalArgumentException("参数[folderId]不能为空");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> map = JsonUtils.stringToMap(param);
        try {
            // 渠道号
            final ConsumerInfo consumerInfo = new ConsumerInfo(Config.LOG_RULE_TEST_CONSUMERID, null, null);
            EngineManager.getInstance().executeRule(folderId, ruleId, map, isSkipValidation, true, loaderKpiStrategyType,
                    ModelExecutorType.TEST, null, consumerInfo);
            System.out.println(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

}
