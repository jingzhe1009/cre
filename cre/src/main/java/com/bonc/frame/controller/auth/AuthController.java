package com.bonc.frame.controller.auth;

import com.bonc.frame.entity.auth.Authority;
import com.bonc.frame.entity.auth.GrantAuthRequest;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissionsRequires;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * web界面：用户-权限管理-角色赋权
 * @author yedunyao
 * @date 2019/5/9 16:24
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthorityService authorityService;

    @PermissionsRequires(value = "/auth", resourceType = ResourceType.MENU)
    @RequestMapping("/view")
    public String view(String idx, String childOpen, String tabId, Model model, HttpServletRequest request) {
        model.addAttribute("idx", idx);//菜单状态标识
        model.addAttribute("childOpen", childOpen);
        model.addAttribute("tabId", tabId);
        return "/pages/authority/authority";
    }

    // ---------------------------------- 全部资源 ----------------------------------

    //当前角色是否拥有全部权限
    @RequestMapping("/all/isGranted")
    @ResponseBody
    public ResponseResult isRoleHasAllPermits(String roleId) {
        final boolean roleHasAllPermits = authorityService.isRoleHasAllPermits(roleId);
        if (roleHasAllPermits) {
            return ResponseResult.createSuccessInfo("当前角色拥有全部权限", 1);
        }
        return ResponseResult.createSuccessInfo("当前角色未拥有全部权限", 0);
    }
     //修改角色 赋予全部权限
    @PermissionsRequires(value = "/auth/update", resourceType = ResourceType.BUTTON)
    @RequestMapping("/all/grant")
    @ResponseBody
    public ResponseResult grantAllPermits(String roleId, HttpServletRequest request) {
        final String currentUser = ControllerUtil.getLoginUserId(request);
        return authorityService.grantAllPermits(roleId, currentUser);
    }
    //修改角色 取消全部权限
    @PermissionsRequires(value = "/auth/update", resourceType = ResourceType.BUTTON)
    @RequestMapping("/all/cancle")
    @ResponseBody
    public ResponseResult cancleAllPermits(String roleId) {
        return authorityService.cancleAllPermits(roleId);
    }
    //修改角色 删除全部权限
    @PermissionsRequires(value = "/auth/update", resourceType = ResourceType.BUTTON)
    @RequestMapping("/all/delete")
    @ResponseBody
    public ResponseResult deleteAllPermits(String roleId) {
        return authorityService.deleteAllPermits(roleId);
    }

    //查看角色权限
    @PermissionsRequires(value = "/auth/view", resourceType = ResourceType.BUTTON)
    @RequestMapping("/all/view")
    @ResponseBody
    public ResponseResult viewAllPermits(String roleId) {
        Map<String, Object> result = new HashMap<>();
        // 是否拥有全部权限
        result.put("isGranted", authorityService.isRoleHasAllPermits(roleId) ? 1 : 0);
        // 菜单权限
        result.put("menus", authorityService.getMenuResourcesAndPermits(roleId));
        // 模型权限
        result.put("rules", authorityService.getModelResourcesAndPermits(roleId,
                null, null, null, "0", "10"));
        // 数据源权限
        result.put("dataSources", authorityService.getDataSourceResourcesAndPermits(roleId,
                null, null, null, "0", "10"));
        // 元数据权限
        result.put("metaTables", authorityService.getMetaResourcesAndPermits(roleId,
                null, null, null, "0", "10"));
        // 公共参数
        result.put("pubVariables", authorityService.getPubVariableResourcesAndPermits(roleId,
                null, null, null, null,
                null, "0", "10"));
        // 公共接口
        result.put("pubApis", authorityService.getPubApiResourcesAndPermits(roleId,
                null, null, null, null,
                "0", "10"));

        //模型库
        result.put("pubRules", authorityService.getPubModelBasesResourcesAndPermits(roleId,
                null, null, null, null,null,
                "0", "10"));

        //规则库
        result.put("pubRuleSets", authorityService.getPubRuleSetResourcesAndPermits(roleId,
                null, null, null, null,
                "0", "10"));

        // 离线任务
        result.put("tasks", authorityService.getTaskResourcesAndPermits(roleId,
                null, null, null, null,
                "0", "10"));

        return ResponseResult.createSuccessInfo("", result);
    }

    // ---------------------------------- 菜单 ----------------------------------

    //修改菜单
    @PermissionsRequires(value = "/auth/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/menu/grant", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseResult grantMenus(String roleId, @RequestBody List<Authority> authorities,String isAllAuth ,
                                     HttpServletRequest request) {
        final String currentUser = ControllerUtil.getLoginUserId(request);
        return authorityService.grantMenuAndButton(authorities, currentUser, roleId , isAllAuth);
    }

    //查看菜单
    @PermissionsRequires(value = "/auth/view", resourceType = ResourceType.BUTTON)
    @RequestMapping("/menu/view")
    @ResponseBody
    public ResponseResult viewMenus(String roleId) {
        final Map<String, Object> menuResourcesAndPermits = authorityService.getMenuResourcesAndPermits(roleId);
        return ResponseResult.createSuccessInfo("", menuResourcesAndPermits);
    }

    // ================================== 数据级权限  ==================================

    // ---------------------------------- 模型 ----------------------------------

    @PermissionsRequires(value = "/auth/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/rule/grant", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult grantRules(@RequestBody GrantAuthRequest grantAuthRequest,
                                     HttpServletRequest request) {
        final String currentUser = ControllerUtil.getLoginUserId(request);
        return authorityService.grant(grantAuthRequest, currentUser, ResourceType.DATA_MODEL.getType());
    }

    @PermissionsRequires(value = "/auth/view", resourceType = ResourceType.BUTTON)
    @RequestMapping("/rule/view")
    @ResponseBody
    public ResponseResult viewRules(String roleId, String moduleName, String ruleType,
                                    String folderName, String start, String length) {
        final Map<String, Object> modelResourcesAndPermits = authorityService.getModelResourcesAndPermits(
                roleId, moduleName, ruleType, folderName, start, length);
        return ResponseResult.createSuccessInfo("", modelResourcesAndPermits);
    }

    // ---------------------------------- 数据源 ----------------------------------

    @PermissionsRequires(value = "/auth/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/dataSource/grant", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult grantDataSources(@RequestBody GrantAuthRequest grantAuthRequest,
                                          HttpServletRequest request) {
        final String currentUser = ControllerUtil.getLoginUserId(request);
        return authorityService.grant(grantAuthRequest, currentUser, ResourceType.DATA_DATASOURCE.getType());
    }

    @PermissionsRequires(value = "/auth/view", resourceType = ResourceType.BUTTON)
    @RequestMapping("/dataSource/view")
    @ResponseBody
    public ResponseResult viewDataSources(String roleId, String dbAlias,
                                          String dbIp, String dbType,
                                          String start, String length) {
        final Map<String, Object> metaResourcesAndPermits = authorityService.getDataSourceResourcesAndPermits(
                roleId, dbAlias, dbIp, dbType, start, length);
        return ResponseResult.createSuccessInfo("", metaResourcesAndPermits);
    }

    // ---------------------------------- 元数据 ----------------------------------

    @PermissionsRequires(value = "/auth/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/metaTable/grant", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult grantMetaTables(@RequestBody GrantAuthRequest grantAuthRequest,
                                          HttpServletRequest request) {
        final String currentUser = ControllerUtil.getLoginUserId(request);
        return authorityService.grant(grantAuthRequest, currentUser, ResourceType.DATA_METADATA.getType());
    }

    @PermissionsRequires(value = "/auth/view", resourceType = ResourceType.BUTTON)
    @RequestMapping("/metaTable/view")
    @ResponseBody
    public ResponseResult viewMetaTables(String roleId, String tableName, String dbAlias,
                                         String folderName, String start, String length) {
        final Map<String, Object> metaResourcesAndPermits = authorityService.getMetaResourcesAndPermits(
                roleId, tableName, dbAlias, folderName, start, length);
        return ResponseResult.createSuccessInfo("", metaResourcesAndPermits);
    }

    // ---------------------------------- 公共参数 ----------------------------------

    @PermissionsRequires(value = "/auth/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/pub/variable/grant", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult grantPubVariables(@RequestBody GrantAuthRequest grantAuthRequest,
                                            HttpServletRequest request) {
        final String currentUser = ControllerUtil.getLoginUserId(request);
        authorityService.updatePubVariableAuthList(grantAuthRequest);
        return authorityService.grant(grantAuthRequest, currentUser, ResourceType.DATA_PUB_VARIABLE.getType());
    }

    @PermissionsRequires(value = "/auth/view", resourceType = ResourceType.BUTTON)
    @RequestMapping("/pub/variable/view")
    @ResponseBody
    public ResponseResult viewPubVariables(String roleId,
                                           String variableAlias,
                                           String variableGroupName,
                                           String variableTypeId,
                                           String startDate, String endDate,
                                           String start, String length) {
        final Map<String, Object> variablesResourcesAndPermits = authorityService.getPubVariableResourcesAndPermits(
                roleId, variableAlias, variableGroupName, variableTypeId, startDate, endDate, start, length);
        return ResponseResult.createSuccessInfo("", variablesResourcesAndPermits);
    }

    // ---------------------------------- 公共接口 ----------------------------------

    @PermissionsRequires(value = "/auth/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/pub/api/grant", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult grantPubApis(@RequestBody GrantAuthRequest grantAuthRequest,
                                       HttpServletRequest request) {
        final String currentUser = ControllerUtil.getLoginUserId(request);
        return authorityService.grant(grantAuthRequest, currentUser, ResourceType.DATA_PUB_API.getType());
    }

    @PermissionsRequires(value = "/auth/view", resourceType = ResourceType.BUTTON)
    @RequestMapping("/pub/api/view")
    @ResponseBody
    public ResponseResult viewPubApis(String roleId,
                                      String apiName, String apiGroupName,
                                      String startDate, String endDate,
                                      String start, String length) {
        final Map<String, Object> apisResourcesAndPermits = authorityService.getPubApiResourcesAndPermits(
                roleId, apiName, apiGroupName, startDate, endDate, start, length);
        return ResponseResult.createSuccessInfo("", apisResourcesAndPermits);
    }


    // ------------------------------- 规则库 ----------------------------------
    @PermissionsRequires(value = "/auth/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/pub/ruleSet/grant", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult grantPubruleSets(@RequestBody GrantAuthRequest grantAuthRequest,
                                       HttpServletRequest request) {
        final String currentUser = ControllerUtil.getLoginUserId(request);
        return authorityService.grant(grantAuthRequest, currentUser, ResourceType.DATA_PUB_RULE_SET.getType());
    }

    @PermissionsRequires(value = "/auth/view", resourceType = ResourceType.BUTTON)
    @RequestMapping("/pub/ruleSet/view")
    @ResponseBody
    public ResponseResult viewPubruleSet(String roleId,
                                         String ruleSetName, String ruleSetGroupName,
                                         String startDate, String endDate,
                                         String start, String length) {
        final Map<String, Object> apisResourcesAndPermits = authorityService.getPubRuleSetResourcesAndPermits(
                roleId, ruleSetName, ruleSetGroupName,  startDate, endDate, start, length);
        return ResponseResult.createSuccessInfo("", apisResourcesAndPermits);
    }

    // ------------------------------- 模型库 ----------------------------------
    @PermissionsRequires(value = "/auth/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/pub/modelBase/grant", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult grantPubModelBases(@RequestBody GrantAuthRequest grantAuthRequest,
                                       HttpServletRequest request) {
        final String currentUser = ControllerUtil.getLoginUserId(request);
        return authorityService.grant(grantAuthRequest, currentUser, ResourceType.DATA_PUB_MODEL.getType());
    }

    @PermissionsRequires(value = "/auth/view", resourceType = ResourceType.BUTTON)
    @RequestMapping("/pub/modelBase/view")
    @ResponseBody
    public ResponseResult viewPubModelBases(String roleId,
                                      String moduleName, String ruleType,String modelGroupName,
                                      String startDate, String endDate,
                                      String start, String length) {
        final Map<String, Object> apisResourcesAndPermits = authorityService.getPubModelBasesResourcesAndPermits(roleId ,
                moduleName, ruleType, modelGroupName, startDate, endDate, start, length);
        return ResponseResult.createSuccessInfo("", apisResourcesAndPermits);
    }

    // ---------------------------------- 离线任务 ----------------------------------

    @PermissionsRequires(value = "/auth/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/task/grant", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult grantTasks(@RequestBody GrantAuthRequest grantAuthRequest,
                                           HttpServletRequest request) {
        final String currentUser = ControllerUtil.getLoginUserId(request);
        return authorityService.grant(grantAuthRequest, currentUser, ResourceType.DATA_TASK.getType());
    }

    @PermissionsRequires(value = "/auth/view", resourceType = ResourceType.BUTTON)
    @RequestMapping("/task/view")
    @ResponseBody
    public ResponseResult viewTasks(String roleId,
                                    String taskName, String packageName,
                                    String ruleName, String ruleVersion,
                                    String start, String length) {
        final Map<String, Object> taskResourcesAndPermits = authorityService.getTaskResourcesAndPermits(
                roleId, taskName, packageName, ruleName, ruleVersion, start, length);
        return ResponseResult.createSuccessInfo("", taskResourcesAndPermits);
    }

    // ---------------------------------- 指标 ----------------------------------

    @PermissionsRequires(value = "/auth/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/kpi/grant", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult grantKpis(@RequestBody GrantAuthRequest grantAuthRequest,
                                     HttpServletRequest request) {
        final String currentUser = ControllerUtil.getLoginUserId(request);
        return authorityService.grant(grantAuthRequest, currentUser, ResourceType.DATA_KPI.getType());
    }

    @PermissionsRequires(value = "/auth/view", resourceType = ResourceType.BUTTON)
    @RequestMapping("/kpi/view")
    @ResponseBody
    public ResponseResult viewKpis(String roleId,
                                   @Nullable String kpiName,
                                   @Nullable String kpiGroupName,
                                   @Nullable String kpiType,
                                   @Nullable String fetchType,
                                   String start, String length) {
        final Map<String, Object> kpiResourcesAndPermits = authorityService.getKpiResourcesAndPermits(
                roleId, kpiName, kpiGroupName, kpiType, fetchType, start, length);
        return ResponseResult.createSuccessInfo("", kpiResourcesAndPermits);
    }

    // ---------------------------------- 场景 ----------------------------------

    @PermissionsRequires(value = "/auth/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/folder/grant", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult grantFolders(@RequestBody GrantAuthRequest grantAuthRequest,
                                    HttpServletRequest request) {
        final String currentUser = ControllerUtil.getLoginUserId(request);
        return authorityService.grant(grantAuthRequest, currentUser, ResourceType.DATA_FOLDER.getType());
    }

    @PermissionsRequires(value = "/auth/view", resourceType = ResourceType.BUTTON)
    @RequestMapping("/folder/view")
    @ResponseBody
    public ResponseResult viewFolders(String roleId,
                                   @Nullable String folderName,
                                   String start, String length) {
        final Map<String, Object> folderResourcesAndPermits = authorityService.getFolderResourcesAndPermits(
                roleId, folderName, start, length);
        return ResponseResult.createSuccessInfo("", folderResourcesAndPermits);
    }

    // ------------------------------- 权限校验 -------------------------------

    @PermissionsRequires(value = "/auth/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/update/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult update() {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/auth/view", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/view/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult view() {
        return ResponseResult.createSuccessInfo();
    }

}
