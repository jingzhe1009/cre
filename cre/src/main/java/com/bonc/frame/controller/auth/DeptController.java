package com.bonc.frame.controller.auth;

import com.bonc.frame.entity.auth.Dept;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissionsRequires;
import com.bonc.frame.service.auth.DeptService;
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
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dept")
public class DeptController {
    @Autowired
    DeptService deptService;

    @PermissionsRequires(value = "/org", resourceType = ResourceType.MENU)
    @RequestMapping("/view")
    public String view(String idx, String childOpen, String tabId, Model model, HttpServletRequest request) {
        model.addAttribute("idx", idx);//菜单状态标识
        model.addAttribute("childOpen", childOpen);
        model.addAttribute("tabId", tabId);
        return "/pages/authority/authority";
    }

    @PermissionsRequires(value = "/org/add", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult save(@RequestBody Dept dept, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return deptService.save(dept, loginUserId);
    }

    @PermissionsRequires(value = "/org/view", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> list(String deptName, String start, String length) {
        Map<String, Object> result = deptService.list(deptName, start, length);
        return result;
    }
    
    @RequestMapping(value = "/chl", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> chl(String deptName, String start, String length) {
        Map<String, Object> result = deptService.list(deptName, start, length);
        return result;
    }

    @PermissionsRequires(value = "/org/view", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/nameList", method = RequestMethod.GET)
    @ResponseBody
    public List<Object> nameList() {
        List<Object> result = deptService.nameList();
        return result;
    }

    @PermissionsRequires(value = "/org/delete", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult delete(String deptId) {
        return deptService.delete(deptId);
    }

    @PermissionsRequires(value = "/org/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult update(@RequestBody Dept dept, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return deptService.update(dept, loginUserId);
    }


    // ------------------------------- 权限校验 -------------------------------

    @PermissionsRequires(value = "/org/add", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/save/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult save() {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/org/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/update/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult update() {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/org/delete", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/delete/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult delete() {
        return ResponseResult.createSuccessInfo();
    }

}
