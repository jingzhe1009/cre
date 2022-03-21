package com.bonc.frame.controller.auth;

import com.bonc.frame.entity.auth.Role;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissionsRequires;
import com.bonc.frame.service.auth.RoleService;
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
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PermissionsRequires(value = "/role", resourceType = ResourceType.MENU)
    @RequestMapping("/view")
    public String view(String idx, String childOpen, String tabId, Model model, HttpServletRequest request) {
        model.addAttribute("idx", idx);//菜单状态标识
        model.addAttribute("childOpen", childOpen);
        model.addAttribute("tabId", tabId);
        return "/pages/authority/authority";
    }

    @PermissionsRequires(value = "/role/add", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult save(@RequestBody Role role, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return roleService.save(role, loginUserId);
    }

    @PermissionsRequires(value = "/role/view", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> list(String roleName, String start, String length) {
        Map<String, Object> result = roleService.list(roleName, start, length);

      /*  List<Role> deleteRoles = new ArrayList<>();

        List<Role> allRoles = (List<Role>) result.get("data");

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        Object user = session.getAttribute(ConstantFinal.SESSION_KEY);
        List<Role> rolesByUser = roleService.getRoleByUser(user.toString());
        List<Role> roleByroot = roleService.getRoleByroot();

        if(allRoles != null && !allRoles.isEmpty()) {
            for (int i= 0 ; i< allRoles.size() ; i++) {
                Role allrole = allRoles.get(i);
                if(!rolesByUser.isEmpty()){
                    for (Role userrole : rolesByUser) {
                        if (allrole.getRoleId().equals(userrole.getRoleId())) {
                            deleteRoles.add(allrole);
                        }
                    }
                }
                if(!roleByroot.isEmpty()){
                    for (Role rootrole : roleByroot) {
                        if (allrole.getRoleId().equals(rootrole.getRoleId())) {
                            deleteRoles.add(allrole);
                        }
                    }
                }
            }

            for(Role deleteRole :deleteRoles){
                allRoles.remove(deleteRole);
            }
            result.put("data",allRoles);
        }*/

        return result;
    }

    @PermissionsRequires(value = "/role/delete", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult delete(String roleId) {
        return roleService.delete(roleId);
    }

    @PermissionsRequires(value = "/role/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult update(@RequestBody Role role, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return roleService.update(role, loginUserId);
    }

    // ------------------------------- 权限校验 -------------------------------

    @PermissionsRequires(value = "/role/add", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/save/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult save() {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/role/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/update/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult update() {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/role/delete", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/delete/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult delete() {
        return ResponseResult.createSuccessInfo();
    }


}
