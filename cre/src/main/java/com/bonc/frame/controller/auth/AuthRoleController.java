package com.bonc.frame.controller.auth;

import com.bonc.frame.entity.auth.Role;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissionsRequires;
import com.bonc.frame.service.auth.RoleService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/createCheck")
public class AuthRoleController {

    @Autowired
    private RoleService roleService;

    @RequestMapping(value = "/check")
    @ResponseBody
    public ResponseResult check(HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        ResponseResult result = roleService.getRoleByUser2(loginUserId);
        return result;
    }
}
