package com.bonc.frame.controller.task;

import com.bonc.frame.entity.task.RuleTask;
import com.bonc.frame.module.quartz.TaskService;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissionsRequires;
import com.bonc.frame.service.task.RuleTaskLogService;
import com.bonc.frame.service.task.RuleTaskService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.JsonUtils;
import com.bonc.frame.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/5/28 16:56
 */
@Controller
@RequestMapping("/task")
public class RuleTaskController {

    @Autowired
    private RuleTaskService ruleTaskService;
    @Autowired
    private RuleTaskLogService ruleTaskLogService;

    @Autowired
    private TaskService taskService;

//    @PermissionsRequires(value = "/task", resourceType = ResourceType.MENU)
    @RequestMapping("/view")
    public String view(String idx, String childOpen, Model model, HttpServletRequest request) {
        model.addAttribute("idx", idx);//菜单状态标识
        model.addAttribute("childOpen", childOpen);
        return "/pages/task/taskIndex";
    }

//    @PermissionsRequires(value = "/task/view", resourceType = ResourceType.BUTTON)
    @RequestMapping("list")
    public @ResponseBody
    Map<String, Object> list(HttpServletRequest request) {
        String start = ControllerUtil.getParam(request, "start");
        String size = ControllerUtil.getParam(request, "length");
        String taskName = ControllerUtil.getParam(request, "taskName");
        String packageName = ControllerUtil.getParam(request, "packageName");
        String ruleName = ControllerUtil.getParam(request, "ruleName");
        String ruleVersion = ControllerUtil.getParam(request, "ruleVersion");
        String taskStatus = ControllerUtil.getParam(request, "taskStatus");
        String sort = ControllerUtil.getParam(request, "sort");
        String order = ControllerUtil.getParam(request, "order");
        Map<String, Object> result = ruleTaskService.getRuleTaskList(taskName, packageName, ruleName, ruleVersion,
                taskStatus, sort, order, start, size);
        return result;
    }

    // 查看任务配置详情
    @RequestMapping("/detail")
    @ResponseBody
    public ResponseResult detail(String taskId) {
        return ruleTaskService.detail(taskId);
    }

//    @PermissionsRequires(value = "/task/add", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResponseResult save(@RequestBody RuleTask ruleTask, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return ruleTaskService.save(ruleTask, loginUserId);
    }

    @PermissionsRequires(value = "/task/update?ruleTask.taskId", resourceType = ResourceType.DATA_TASK)
    @RequestMapping("/update")
    @ResponseBody
    public ResponseResult update(@RequestBody RuleTask ruleTask, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return ruleTaskService.update(ruleTask, loginUserId);
    }

    @PermissionsRequires(value = "/task/delete?taskId", resourceType = ResourceType.DATA_TASK)
    @RequestMapping("/delete")
    @ResponseBody
    public ResponseResult delete(String taskId, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return ruleTaskService.deleteTask(taskId, loginUserId);
    }

//    @PermissionsRequires(value = "/task/delete", resourceType = ResourceType.DATA_TASK)
    @RequestMapping("/deleteByIds")
    @ResponseBody
    public ResponseResult deleteByIds(String taskIds, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        final List<String> ids = JsonUtils.toList(taskIds, String.class);
        return ruleTaskService.deleteTasks(ids, loginUserId);
    }

    @RequestMapping("/run")
    @ResponseBody
    public ResponseResult run(String taskId, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return ruleTaskService.runTask(taskId, loginUserId);
    }

    @RequestMapping("/stop")
    @ResponseBody
    public ResponseResult stop(String taskId, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return ruleTaskService.stopQuartzTask(taskId, loginUserId);
    }

    @RequestMapping("/validExpression")
    @ResponseBody
    public boolean validExpression(String cron) {
        return taskService.isValidExpression(cron);
    }

    // ------------------------------- 离线任务日志 ---------------------------
    @RequestMapping("/deleteRuleTaskLog")
    @ResponseBody
    public ResponseResult deleteRuleTaskLog(String taskId) {
        return ruleTaskLogService.deleteByTaskId(taskId);
    }

    // ------------------------------- 权限校验 -------------------------------

    /*@PermissionsRequires(value = "/task/add", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/save/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult save() {
        return ResponseResult.createSuccessInfo();
    }*/

    @PermissionsRequires(value = "/task/update?taskId", resourceType = ResourceType.DATA_TASK)
    @RequestMapping(value = "/update/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult update(String taskId) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/task/delete?taskId", resourceType = ResourceType.DATA_TASK)
    @RequestMapping(value = "/delete/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult delete(String taskId) {
        return ResponseResult.createSuccessInfo();
    }

}
