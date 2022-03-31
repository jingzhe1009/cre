package com.bonc.frame.controller.auth;

import com.bonc.frame.entity.user.UserAccountEn;
import com.bonc.frame.entity.user.UserExt;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissionsRequires;
import com.bonc.frame.service.UserService;
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
/**
 * web界面：菜单-用户管理
 * */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PermissionsRequires(value = "/user", resourceType = ResourceType.MENU)
    @RequestMapping("/view")
    public String view(String idx, String childOpen, String tabId, Model model, HttpServletRequest request) {
        model.addAttribute("idx", idx);//菜单状态标识
        model.addAttribute("childOpen", childOpen);
        model.addAttribute("tabId", tabId);
        return "/pages/authority/authority";
    }

    @PermissionsRequires(value = "/user/add", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult save(@RequestBody UserExt user, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return userService.createUser(user, loginUserId);
    }

    @PermissionsRequires(value = "/user/view", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> list(String userId, String userName, String jobNumber, String start, String length) {
        Map<String, Object> result = userService.list(userId, userName, jobNumber, start, length);
        return result;
    }

    @PermissionsRequires(value = "/user/delete", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult delete(String userId) {
        return userService.deleteUser(userId);
    }

    @PermissionsRequires(value = "/user/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult update(@RequestBody UserAccountEn user, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return userService.updateUser(user, loginUserId);
    }

    // ------------------------------- 权限校验 -------------------------------

    @PermissionsRequires(value = "/user/view", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/view/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult view() {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/user/add", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/save/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult save() {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/user/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/update/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult update() {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/user/delete", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/delete/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult delete() {
        return ResponseResult.createSuccessInfo();
    }

}
