package com.bonc.frame.controller.auth;

import com.bonc.frame.entity.auth.Group;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissionsRequires;
import com.bonc.frame.service.auth.GroupService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private GroupService groupService;

    @PermissionsRequires(value = "/user/group", resourceType = ResourceType.MENU)
    @RequestMapping("/view")
    public String view(String idx, Model model, HttpServletRequest request) {
        model.addAttribute("idx", idx);//菜单状态标识
        return "/pages/group/groupIndex";
    }

    @PermissionsRequires(value = "/user/group/add", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult save(@RequestBody Group group, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return groupService.save(group, loginUserId);
    }

    @PermissionsRequires(value = "/user/group/view", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> list(String groupName, String start, String length) {
        Map<String, Object> result = groupService.list(groupName, start, length);
        return result;
    }

    @PermissionsRequires(value = "/user/group/delete", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult delete(String groupId) {
        return groupService.delete(groupId);
    }

    @PermissionsRequires(value = "/user/group/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult update(@RequestBody Group group, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return groupService.update(group, loginUserId);
    }


    // ------------------------------- 权限校验 -------------------------------

    @PermissionsRequires(value = "/user/group/add", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/save/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult save() {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/user/group/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/update/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult update() {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/user/group/delete", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/delete/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult delete() {
        return ResponseResult.createSuccessInfo();
    }


}
