package com.bonc.frame.controller.log;

import com.bonc.frame.service.apilog.ApiLogService;
import com.bonc.frame.service.rule.RuleFolderService;
import com.bonc.frame.service.rulelog.RuleLogService;
import com.bonc.frame.service.syslog.SysLogService;
import com.bonc.frame.util.ControllerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/5/7 16:41
 */
@Controller
@RequestMapping("/log")
public class LogController {

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private RuleLogService ruleLogService;

    @Autowired
    private ApiLogService apiLogService;

    @Autowired
    private RuleFolderService ruleFolderService;

    @RequestMapping("/view")
    public String view( String idx, String childOpen, Model model, HttpServletRequest request) {
        model.addAttribute("idx", idx);//菜单状态标识
        model.addAttribute("childOpen", childOpen);
        String modelBaseId = ruleFolderService.getModelBaseId();
        model.addAttribute("pubFolderId", modelBaseId);
        return "/pages/log/log";
    }

    @RequestMapping("/sys")
    public @ResponseBody Map<String, Object> sysLog(HttpServletRequest request) {
        String start = ControllerUtil.getParam(request, "start");
        String size = ControllerUtil.getParam(request, "length");
        String userName = ControllerUtil.getParam(request, "userName");
        String operateType = ControllerUtil.getParam(request, "operateType");
        String startDate = ControllerUtil.getParam(request, "startDate");
        String endDate = ControllerUtil.getParam(request, "endDate");
        String sort = ControllerUtil.getParam(request, "sort");
        String order = ControllerUtil.getParam(request, "order");
        Map<String,Object> result = sysLogService.getSysLogList(userName, startDate, endDate, operateType,
                sort, order,"0", start, size);
        return result;
    }

    @RequestMapping("/operate")
    public @ResponseBody Map<String, Object> operateLog(HttpServletRequest request) {
        String start = ControllerUtil.getParam(request, "start");
        String size = ControllerUtil.getParam(request, "length");
        String userName = ControllerUtil.getParam(request, "userName");
        String operateType = ControllerUtil.getParam(request, "operateType");
        String startDate = ControllerUtil.getParam(request, "startDate");
        String endDate = ControllerUtil.getParam(request, "endDate");
        String sort = ControllerUtil.getParam(request, "sort");
        String order = ControllerUtil.getParam(request, "order");
        Map<String,Object> result = sysLogService.getSysLogList(userName, startDate, endDate, operateType,
                sort, order,"1", start, size);
        return result;
    }

    @RequestMapping("/rule")
    public @ResponseBody Map<String, Object> ruleLog(HttpServletRequest request) {
        String start = request.getParameter("start");
        String size = request.getParameter("length");
        String state = ControllerUtil.getParam(request, "state");
        String folderId = ControllerUtil.getParam(request, "folderId");
        String ruleId = ControllerUtil.getParam(request, "ruleId");
        String ruleName = ControllerUtil.getParam(request, "ruleName");
        String isDraft = ControllerUtil.getParam(request, "isDraft");
        String startDate = ControllerUtil.getParam(request, "startDate");
        String endDate = ControllerUtil.getParam(request, "endDate");
        String sort = ControllerUtil.getParam(request, "sort");
        String order = ControllerUtil.getParam(request, "order");
        Map<String, Object> result = ruleLogService.getRuleLogList(folderId, ruleId, ruleName, isDraft,
                state, startDate, endDate, sort, order, start, size);
        return result;
    }

    @RequestMapping("/api")
    public @ResponseBody Map<String, Object> apiLog(HttpServletRequest request) {
        String start = request.getParameter("start");
        String size = request.getParameter("length");
        String startDate = ControllerUtil.getParam(request, "startDate");
        String apiName = ControllerUtil.getParam(request, "apiName");
        String endDate = ControllerUtil.getParam(request, "endDate");
        String sort = ControllerUtil.getParam(request, "sort");
        String order = ControllerUtil.getParam(request, "order");
        Map<String,Object> result = apiLogService.getApiLogList(apiName, startDate, endDate, sort, order, start, size);
        return result;
    }

}
