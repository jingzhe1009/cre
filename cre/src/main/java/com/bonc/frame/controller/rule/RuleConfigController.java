package com.bonc.frame.controller.rule;

import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.rule.RuleDetail;
import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.variable.reference.VariableRule;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissionsRequires;
import com.bonc.frame.service.UserService;
import com.bonc.frame.service.api.ApiService;
import com.bonc.frame.service.function.FunctionService;
import com.bonc.frame.service.impl.rule.RuleDetailServiceImpl;
import com.bonc.frame.service.kpi.KpiService;
import com.bonc.frame.service.reference.RuleReferenceService;
import com.bonc.frame.service.rule.RuleDetailService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.util.*;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 规则详情内容新增与修改Controller
 *
 * @author jsj
 */
@Api(description = "模型管理接口")
@Controller
@RequestMapping("/rule")
public class RuleConfigController {
    private static final Log log = LogFactory.getLog(RuleDetailServiceImpl.class);


    private static final String MODE_INSERT = "insert";
    private static final String MODE_UPDATE = "update";
    private static final String MODE_VIEW = "view";

    @Autowired
    private RuleDetailService ruleDetailService;

    @Autowired
    private VariableService variableService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApiService apiService;

    @Autowired
    private FunctionService functionService;

    @Autowired
    private KpiService kpiService;

    @Autowired
    private RuleReferenceService ruleReferenceService;

    //新增规则页面
    @RequestMapping("/ruleConfig")
    public String main(String folderId, String ruleType,
                       String childOpen, String pageType,
                       String ruleName, Model model) throws Exception {
        if (folderId == null || folderId.isEmpty()) {
            throw new Exception("The folder id is null.");
        }
        if (ruleType == null || ruleType.isEmpty()) {
            throw new Exception("The ruleType is null.");
        }
        List<Map<String, Object>> funcMethodTree = this.functionService.funcMethodTree(folderId, null);
        model.addAttribute("funcMethodTree", JsonUtils.beanToJson(funcMethodTree).toString());

        Map<String, Object> variableMap = variableService.getVariableTree(folderId);
        model.addAttribute("inVariableTree", JsonUtils.beanToJson(variableMap.get("inVariableTree")).toString());
        model.addAttribute("outVariableTree", JsonUtils.beanToJson(variableMap.get("outVariableTree")).toString());
        //FIXME : 临时的一个属性,用修改弹框时处理,之后会将这两个参数删除,改成上面的
        Map<String, Object> variableMap_new = variableService.getVariableTree_new(folderId);
        model.addAttribute("inVariableTree_new", JsonUtils.beanToJson(variableMap_new.get("inVariableTree")).toString());
        model.addAttribute("outVariableTree_new", JsonUtils.beanToJson(variableMap_new.get("outVariableTree")).toString());

        List<ApiConf> apiList = apiService.getAllApiByFolderId(folderId);
        model.addAttribute("apiList", apiList);

        // 公共接口
        final List<ApiConf> pubApiList = apiService.pubGetAllApiList();
        model.addAttribute("pubApiList", pubApiList);

        // 指标类型
        List<Map<String, Object>> kpiTypeList = kpiService.getKpiType();
        model.addAttribute("kpiTypeList", JsonUtils.beanToJson(kpiTypeList).toString());

        model.addAttribute("kpiMap", "[]");

        model.addAttribute("folderId", folderId);
        model.addAttribute("ruleContent", "{}");
        model.addAttribute("ruleType", ruleType);
        model.addAttribute("optMode", MODE_INSERT);
        model.addAttribute("idx", "2");
        model.addAttribute("childOpen", childOpen);
        model.addAttribute("pageType", pageType);
        model.addAttribute("ruleName", ruleName);
        return "/pages/ruleConfig";
    }

    /**
     * 修改规则页面
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/updateRule")
    public String updateModelRuleIndex(String ruleId,
                                       String folderId,
                                       String childOpen,
                                       @Nullable String modelGroupId,
                                       String pageType,
                                       Model model,
                                       HttpServletRequest request) throws Exception {
        final String currentUserId = ControllerUtil.getLoginUserId(request);
        if (ruleId == null || ruleId.isEmpty()) {
            throw new Exception("The ruleId id is null.");
        }
        if (folderId == null || folderId.isEmpty()) {
            throw new Exception("The folder id is null.");
        }
        String ruleContent = null;
        RuleDetailWithBLOBs rule = ruleDetailService.getRule(ruleId);
        if (rule == null) {
            throw new Exception("There is no rule[" + ruleId + "].");
        }
        ruleContent = rule.getRuleContent();
        if (ruleContent == null || ruleContent.isEmpty()) {
            ruleContent = "{}";
        }
        model.addAttribute("modelGroupId", modelGroupId);
        model.addAttribute("ruleContent", ruleContent.trim());
        model.addAttribute("ruleName", rule.getRuleName());
        model.addAttribute("moduleName", rule.getModuleName());
        model.addAttribute("ruleType", rule.getRuleType());
        model.addAttribute("ruleDesc", rule.getRuleDesc());
        model.addAttribute("version", rule.getVersion());
        model.addAttribute("versionDesc", rule.getVersionDesc());

        Map<String, Object> variableMap = variableService.getVariableTree(folderId);
        List<Map<String, Object>> funcMethodTree = this.functionService.funcMethodTree(folderId, "");
        model.addAttribute("funcMethodTree", JsonUtils.beanToJson(funcMethodTree).toString());
        model.addAttribute("inVariableTree", JsonUtils.beanToJson(variableMap.get("inVariableTree")).toString());
        model.addAttribute("outVariableTree", JsonUtils.beanToJson(variableMap.get("outVariableTree")).toString());
        model.addAttribute("orgTree", JsonUtils.beanToJson(variableMap.get("outVariableTree")).toString());
        List<ApiConf> apiList = apiService.getAllApiByFolderId(folderId);
        model.addAttribute("apiList", apiList);

        // 公共接口
        final List<ApiConf> pubApiList = apiService.pubGetAllApiList();
        model.addAttribute("pubApiList", pubApiList);

        // 指标类型
        List<Map<String, Object>> kpiTypeList = kpiService.getKpiType();
        model.addAttribute("kpiTypeList", JsonUtils.beanToJson(kpiTypeList).toString());

        // 模型引用的 指标
        List<Map<String, Object>> kpiInfoByRuleId = ruleReferenceService.selectKpiInfoByRuleId(ruleId);
        model.addAttribute("kpiMap", JsonUtils.beanToJson(kpiInfoByRuleId).toString());

        model.addAttribute("ruleId", ruleId);
        model.addAttribute("folderId", folderId);
        model.addAttribute("isLog", rule.getIsLog());
        model.addAttribute("optMode", MODE_UPDATE);
        if (rule != null && ConstantUtil.RULE_STATUS_RUNNING.equals(rule.getRuleStatus())) {
            model.addAttribute("optMode", MODE_VIEW);
        }

        model.addAttribute("idx", folderId);
        model.addAttribute("childOpen", childOpen);
        model.addAttribute("pageType", pageType);
        return "/pages/ruleConfig";

    }

    /**
     * 保存规则
     *
     * @return
     */
    @RequestMapping("/saveRuleDetail")
    @ResponseBody
    @Deprecated
    public ResponseResult saveRule(RuleDetail rule, String optMode, String data, HttpServletRequest request) {
        final String currentUser = ControllerUtil.getLoginUserId(request);
        String realUserAccount = ControllerUtil.getRealUserAccount(request);
        if (MODE_INSERT.equals(optMode)) {
            String ruleId = IdUtil.createId();
            rule.setRuleId(ruleId);
            rule.setPlatformCreateUserJobNumber(realUserAccount);
            rule.setCreatePerson(currentUser);
            rule.setCreateDate(new Date());
            ResponseResult result = ruleDetailService.insertRule(rule, data, currentUser);
            return result;
        } else if (MODE_UPDATE.equals(optMode) || MODE_VIEW.equals(optMode)) {
            // 校验权限
            RuleConfigController currentProxy = (RuleConfigController) AopContext.currentProxy();
            currentProxy.checkAuthUpdate(rule.getRuleId());

            rule.setPlatformUpdateUserJobNumber(realUserAccount);
            rule.setUpdatePerson(currentUser);
            rule.setUpdateDate(new Date());
            ResponseResult result = ruleDetailService.updateRule(rule, data);
            return result;
        } else {
            return ResponseResult.createFailInfo("The optMode is error.");
        }

    }

    /**
     * 更改模型状态 启用/停用
     *
     * @return
     */
//    @PermissionsRequires(value = "/rule/enable?ruleId", resourceType = ResourceType.DATA_MODEL)
    @RequestMapping("/updateRuleStatus")
    @ResponseBody
    public ResponseResult updateRuleStatus(String folderId, String ruleId, String status, HttpServletRequest
            request) {
        String realUserAccount = ControllerUtil.getRealUserAccount(request);
        ResponseResult result = ruleDetailService.updateRuleStatus(folderId, ruleId, status, realUserAccount);
        return result;
    }

    /**
     * 更改规则缓存
     *
     * @return
     */
    @Deprecated
    @RequestMapping("/updateRuleCache")
    @ResponseBody
    public ResponseResult updateRuleCache(String folderId, String ruleId) {
        ResponseResult result = ruleDetailService.updateRuleCache(folderId, ruleId);
        return result;
    }

    /**
     * 删除规则
     *
     * @return
     */
    @PermissionsRequires(value = "/rule/delete?ruleName", resourceType = ResourceType.DATA_MODEL)
    @RequestMapping("/deleteRule")
    @ResponseBody
    public ResponseResult deleteRule(String ruleName, String ruleId, HttpServletRequest request) {
        String realUserAccount = ControllerUtil.getRealUserAccount(request);
        ResponseResult result = ruleDetailService.deleteRule(ruleId, realUserAccount);
        return result;
    }

    // ------------------------------ 引用参数 ------------------------------

    // 模型在分支上添加参数
    @RequestMapping("/variable/addOnFork")
    @ResponseBody
    public ResponseResult addVariableOnFork(VariableRule variableRule) {
        ResponseResult result = ruleDetailService.addVariableOnFork(variableRule);
        return result;
    }

    // 模型在规则集上添加参数
    @RequestMapping("/variable/addOnRuleSet")
    @ResponseBody
    public ResponseResult addVariableOnRuleSet(VariableRule variableRule) {
        ResponseResult result = ruleDetailService.addVariableOnRuleSet(variableRule);
        return result;
    }

    // 模型更新参数
    @RequestMapping("/variable/update")
    @ResponseBody
    public ResponseResult updateVariable(String ruleId, String variableId, String oldVariableId) {
        ResponseResult result = ruleDetailService.updateVariable(ruleId, variableId, oldVariableId);
        return result;
    }

    // 模型删除参数
    @RequestMapping("/variable/delete")
    @ResponseBody
    public ResponseResult deleteVariable(String ruleId, String variableId) {
        ResponseResult result = ruleDetailService.deleteVariable(ruleId, variableId);
        return result;
    }

    // ------------------------------- 权限校验 -------------------------------

    @PermissionsRequires(value = "/rule/view?ruleName", resourceType = ResourceType.DATA_MODEL)
    @RequestMapping(value = "/view/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthView(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/rule/update?ruleName", resourceType = ResourceType.DATA_MODEL)
    @RequestMapping(value = "/update/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthUpdate(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/rule/delete?ruleName", resourceType = ResourceType.DATA_MODEL)
    @RequestMapping(value = "/delete/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthDelete(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/pub/rule/delete?ruleName", resourceType = ResourceType.DATA_PUB_MODEL)
    @RequestMapping(value = "/pub/delete/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthDeleteForPub(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/rule/enable?ruleName", resourceType = ResourceType.DATA_MODEL)
    @RequestMapping(value = "/enable/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthEnable(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/pub/modelBase/version/enable?ruleName", resourceType = ResourceType.DATA_PUB_MODEL)
    @RequestMapping(value = "/pub/enable/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthEnableForPub(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/rule/clone?ruleName", resourceType = ResourceType.DATA_MODEL)
    @RequestMapping(value = "/clone/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthClone(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/pub/rule/clone?ruleName", resourceType = ResourceType.DATA_PUB_MODEL)
    @RequestMapping(value = "/pub/clone/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthCloneForPub(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/rule/stage?ruleName", resourceType = ResourceType.DATA_MODEL)
    @RequestMapping(value = "/stage/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthStage(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/pub/rule/stage?ruleName", resourceType = ResourceType.DATA_PUB_MODEL)
    @RequestMapping(value = "/pub/stage/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthStageForPub(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/rule/commit?ruleName", resourceType = ResourceType.DATA_MODEL)
    @RequestMapping(value = "/commit/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthCommit(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/pub/rule/commit?ruleName", resourceType = ResourceType.DATA_PUB_MODEL)
    @RequestMapping(value = "/pub/commit/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthCommitForPub(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/rule/publish?ruleName", resourceType = ResourceType.DATA_MODEL)
    @RequestMapping(value = "/publish/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthPublish(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/pub/rule/publish?ruleName", resourceType = ResourceType.DATA_PUB_MODEL)
    @RequestMapping(value = "/pub/publish/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthPublishForPub(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/pub/rule/test?ruleName", resourceType = ResourceType.DATA_PUB_MODEL)
    @RequestMapping(value = "/pub/test/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthTestForPub(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/rule/test?ruleName", resourceType = ResourceType.DATA_MODEL)
    @RequestMapping(value = "/test/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthTest(String ruleName) {
        return ResponseResult.createSuccessInfo();
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


    // ------------------------------ 模型库 头信息管理 ------------------------------

    @ApiOperation("获取单个模型库模型头信息")
    @RequestMapping(value = "/public/header/info", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult getOneHeaderInfo(String ruleName) {
        return ruleDetailService.getOneHeaderInfo(ruleName);
    }

    @ApiOperation("获取模型库模型头信息列表")
    @RequestMapping(value = "/public/header/list", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult getHeaderList(@Nullable String ruleName, String moduleName,
                                        @Nullable String ruleType,
                                        @Nullable String modelGroupId,
                                        @Nullable String modelGroupName,
                                        @Nullable String deptId,
                                        @Nullable String deptName,
                                        @Nullable String partnerId,
                                        @Nullable String partnerName,
                                        @Nullable String productCode,
                                        @Nullable String productName,
                                        @Nullable String systemCode,
                                        @Nullable String systemName,
                                        @Nullable String platformCreateUserJobNumber,
                                        @Nullable String platformUpdateUserJobNumber,
                                        @Nullable String startDate,
                                        @Nullable String endDate) {
        List<RuleDetailHeader> ruleDetailHeaders = ruleDetailService.getHeaderList(ruleName, moduleName, ruleType, "1", null, modelGroupId, modelGroupName, deptId, deptName, partnerId, partnerName, productCode, productName, systemCode, systemName, platformCreateUserJobNumber, platformUpdateUserJobNumber, startDate, endDate);
        return ResponseResult.createSuccessInfo("", ruleDetailHeaders);
    }

    @ApiOperation("分页获取模型库模型头信息列表")
    @RequestMapping(value = "/public/header/paged", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getHeaderList(@Nullable String ruleName, @Nullable String moduleName,
                                             @Nullable String ruleType,
                                             @Nullable String modelGroupName,
                                             @Nullable String deptId,
                                             @Nullable String deptName,
                                             @Nullable String partnerId,
                                             @Nullable String partnerName,
                                             @Nullable String productCode,
                                             @Nullable String productName,
                                             @Nullable String systemCode,
                                             @Nullable String systemName,
                                             @Nullable String platformCreateUserJobNumber,
                                             @Nullable String platformUpdateUserJobNumber,
                                             @Nullable String startDate,
                                             @Nullable String endDate,
                                             String start, String length,HttpServletRequest request) {
        String logUserId = ControllerUtil.getLoginUserId(request);
        return ruleDetailService.getHeaderList(ruleName, moduleName, ruleType, "1",
                null, null, modelGroupName,
                deptId, deptName, partnerId, partnerName, productCode,
                productName, systemCode, systemName, platformCreateUserJobNumber,
                platformUpdateUserJobNumber, startDate, endDate, start, length,logUserId);
    }

    @ApiOperation("创建模型头信息及新版本")
    @RequestMapping(value = "/public/header/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult createHeaderAndVersion(@ApiParam(name = "模型详细信息", required = true) RuleDetailWithBLOBs
                                                         ruleDetailWithBLOBs,
                                                 @RequestParam String data,
                                                 HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        ruleDetailWithBLOBs.setRuleContent(data);
        return ruleDetailService.createHeaderAndVersion(ruleDetailWithBLOBs, loginUserId);
    }

    @ApiOperation("克隆模型")
    @RequestMapping(value = "/public/folder/clone", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult cloneRuleWithFolder(RuleDetail cloneHeaderInfo,
                                              HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        String realUserAccount = ControllerUtil.getRealUserAccount(request);
        cloneHeaderInfo.setPlatformCreateUserJobNumber(realUserAccount);
        try {
            return ruleDetailService.cloneRuleWithFolder(cloneHeaderInfo, loginUserId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.createFailInfo("克隆失败。");
        }
    }


    @ApiOperation("更新模型头信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "folderId", value = "修改后的场景id", required = false, dataType = "String"),
            @ApiImplicitParam(name = "ruleName", value = "修改后的模型名称", required = false, dataType = "String"),
            @ApiImplicitParam(name = "ruleDesc", value = "修改后的模型描述", required = false, dataType = "String"),
            @ApiImplicitParam(name = "ruleType", value = "修改后的模型类型", required = false, dataType = "String"),
            @ApiImplicitParam(name = "modelGroupId", value = "修改后的模型组id", required = false, dataType = "String"),
            @ApiImplicitParam(name = "oldRuleName", value = "模型原有名称，需要根据此名称去查询更新", required = true, dataType = "String")
    })
    @RequestMapping(value = "/public/header/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult updateHeader(RuleDetailHeader ruleDetailHeader,
                                       String oldRuleName, String oldModuleName,
                                       HttpServletRequest request) {
        if (StringUtils.isBlank(ruleDetailHeader.getIsPublic())) {
            return ResponseResult.createFailInfo("参数[isPublic]不能为空");
        }
        RuleConfigController currentProxy = (RuleConfigController) AopContext.currentProxy();
        if ("1".equals(ruleDetailHeader.getIsPublic())) {
            currentProxy.checkAuthUpdateModelForPub(oldRuleName);
        } else {
            currentProxy.checkAuthUpdateModel(oldRuleName);
        }
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        String realUserAccount = ControllerUtil.getRealUserAccount(request);
        ruleDetailHeader.setPlatformUpdateUserJobNumber(realUserAccount);
        ruleDetailHeader.setRuleName(oldRuleName);
        return ruleDetailService.updateHeader(ruleDetailHeader, oldRuleName, oldModuleName, loginUserId);
    }


    @PermissionsRequires(value = "/pub/modelBase/update?ruleName", resourceType = ResourceType.DATA_PUB_MODEL)
    @RequestMapping(value = "/update/checkAuthModelPub", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthUpdateModelForPub(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/rule/update?ruleName", resourceType = ResourceType.DATA_MODEL)
    @RequestMapping(value = "/update/checkAuthModel", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthUpdateModel(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }


    @ApiOperation("删除模型头信息及其下版本")
    @PermissionsRequires(value = "/pub/rule/delete?ruleName", resourceType = ResourceType.DATA_PUB_MODEL)
    @RequestMapping(value = "/public/header/delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult deleteHeader(String ruleName, HttpServletRequest request) {
        String realUserAccount = ControllerUtil.getRealUserAccount(request);
        return ruleDetailService.deleteRuleByName(ruleName, realUserAccount);
    }


    // ------------------------ 模型库 版本管理 ------------------------

    @ApiOperation("获取模型库模型版本列表")
    @RequestMapping(value = "/public/version/list", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult getVersionList(@RequestParam String ruleName,
                                         @Nullable String ruleStatus,
                                         @Nullable String isLog,
                                         @Nullable String startDate,
                                         @Nullable String endDate) {
        return ruleDetailService.getVersionList(ruleName, ruleStatus, isLog, startDate, endDate);
    }

    @ApiOperation("分页获取模型版本列表")
    @RequestMapping(value = "/public/version/paged", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getVersionList(String ruleName,
                                              @Nullable String ruleStatus,
                                              @Nullable String isLog,
                                              @Nullable String startDate,
                                              @Nullable String endDate,
                                              String start, String length) {
        return ruleDetailService.getVersionList(ruleName, ruleStatus, isLog,
                startDate, endDate, start, length);
    }

    @ApiOperation("创建新版本")
    @PermissionsRequires(value = "/pub/modelBase/version/add?ruleName", resourceType = ResourceType.DATA_PUB_MODEL)
    @RequestMapping(value = "/public/version/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult createVersion(String ruleName, String version,
                                        @Nullable String versionDesc,
                                        String data, HttpServletRequest request) {
        if (StringUtils.isBlank(ruleName)) {
            return ResponseResult.createFailInfo("参数[ruleName]不能为空");
        }
        if (StringUtils.isBlank(version)) {
            return ResponseResult.createFailInfo("参数[version]不能为空");
        }

        String loginUserId = ControllerUtil.getLoginUserId(request);
        return ruleDetailService.createVersion(ruleName, version,
                versionDesc, data, loginUserId);
    }

    /**
     * 更改模型库中的版本的状态  启用/停用
     *
     * @return
     */
    @PermissionsRequires(value = "/pub/modelBase/version/enable?ruleName", resourceType = ResourceType.DATA_PUB_MODEL)
    @RequestMapping("/public/version/updateModelBaseRuleStatus")
    @ResponseBody
    public ResponseResult updateModelBaseRuleStatus(String ruleName, String folderId, String ruleId, String status, HttpServletRequest request) {
        String realUserAccount = ControllerUtil.getRealUserAccount(request);
        ResponseResult result = ruleDetailService.updateRuleStatus(folderId, ruleId, status, realUserAccount);
        return result;
    }

    /**
     * 保存模型库的版本的规则
     *
     * @return
     * @throws Exception
     */
    @PermissionsRequires(value = "/pub/modelBase/update?rule.ruleName", resourceType = ResourceType.DATA_PUB_MODEL)
    @ResponseBody
    @RequestMapping("/public/version/saveModelBaseRule")
    @Deprecated
    public ResponseResult saveModelBaseRule(RuleDetail rule, String optMode, String data, HttpServletRequest request) {
        final String currentUser = ControllerUtil.getLoginUserId(request);
        String realUserAccount = ControllerUtil.getRealUserAccount(request);
        if (MODE_INSERT.equals(optMode)) {
            String ruleId = IdUtil.createId();
            rule.setRuleId(ruleId);
            rule.setCreatePerson(currentUser);
            rule.setPlatformCreateUserJobNumber(realUserAccount);
            rule.setCreateDate(new Date());
            ResponseResult result = ruleDetailService.insertRule(rule, data, currentUser);
            return result;
        } else if (MODE_UPDATE.equals(optMode) || MODE_VIEW.equals(optMode)) {
            rule.setUpdatePerson(currentUser);
            rule.setPlatformUpdateUserJobNumber(realUserAccount);
            rule.setUpdateDate(new Date());
            ResponseResult result = ruleDetailService.updateRule(rule, data);
            return result;
        } else {
            return ResponseResult.createFailInfo("The optMode is error.");
        }
    }

    /**
     * 删除模型库版本
     *
     * @return
     */
    @PermissionsRequires(value = "/pub/modelBase/delete?ruleName", resourceType = ResourceType.DATA_PUB_MODEL)
    @RequestMapping("/public/version/deleteModelBaseRule")
    @ResponseBody
    public ResponseResult deleteModelBaseRule(String ruleName, String ruleId, HttpServletRequest request) {
        String realUserAccount = ControllerUtil.getRealUserAccount(request);
        ResponseResult result = ruleDetailService.deleteRule(ruleId, realUserAccount);
        return result;
    }


    /**
     * 模型库版本的 测试接口鉴权
     */
    @PermissionsRequires(value = "/rule/test?ruleName", resourceType = ResourceType.DATA_PUB_MODEL)
    @RequestMapping(value = "/public/version/ModelBasetest/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthModelBaseTest(String ruleName) {
        return ResponseResult.createSuccessInfo();
    }

    @ApiOperation("发布模型")
    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult publish(RuleDetailHeader headerInfo,
                                  String oldFolderId,
                                  String ruleId, String version,
                                  @Nullable String versionDesc,
                                  HttpServletRequest request) {
        String ruleName = headerInfo.getRuleName();
        if (StringUtils.isBlank(ruleName)) {
            throw new IllegalArgumentException("参数[ruleName]不能为空");
        }
        if (StringUtils.isBlank(oldFolderId)) {
            throw new IllegalArgumentException("参数[oldFolderId]不能为空");
        }
        if (StringUtils.isBlank(ruleId)) {
            throw new IllegalArgumentException("参数[ruleId]不能为空");
        }
        if (StringUtils.isBlank(version)) {
            throw new IllegalArgumentException("参数[version]不能为空");
        }

        String loginUserId = ControllerUtil.getLoginUserId(request);
        return ruleDetailService.publish(headerInfo, oldFolderId, ruleId, version, versionDesc, loginUserId);
    }

    //暂存
    @ApiOperation("暂存模型")
    @RequestMapping(value = "/version/stage", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult stageWithVersion(RuleDetail ruleDetail, String data, String isFirst,
                                           HttpServletRequest request) {
        String moduleName = ruleDetail.getModuleName();
        String realUserAccount = ControllerUtil.getRealUserAccount(request);
        if (StringUtils.isBlank(moduleName)) {
            throw new IllegalArgumentException("参数[moduleName]不能为空");
        }
        if (StringUtils.isBlank(ruleDetail.getFolderId())) {
            throw new IllegalArgumentException("参数[folderId]不能为空");
        }
        if (StringUtils.isBlank(ruleDetail.getIsPublic())) {
            throw new IllegalArgumentException("参数[isPublic]不能为空");
        }
        String loginUserId = ControllerUtil.getLoginUserId(request);
        if ("0".equals(isFirst)) {
            // 校验权限
            RuleConfigController currentProxy = (RuleConfigController) AopContext.currentProxy();
            if ("1".equals(ruleDetail.getIsPublic())) {
                currentProxy.checkAuthStageForPub(ruleDetail.getRuleName());
            } else {
                currentProxy.checkAuthStage(ruleDetail.getRuleName());
            }
            ruleDetail.setPlatformUpdateUserJobNumber(realUserAccount);
        } else {
            try {
                ruleDetail.setPlatformCreateUserJobNumber(realUserAccount);
                Map result = ruleDetailService.stageOrCommitFirst(ruleDetail, data, loginUserId, false);
                if (null != result) {
                    return ResponseResult.createSuccessInfo("暂存成功", result);
                } else {
                    return ResponseResult.createFailInfo("规则名已经存在");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseResult.createFailInfo("暂存失败");
            }
        }
        try {
            return ruleDetailService.stageWithVersion(ruleDetail, data, loginUserId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.createFailInfo("暂存失败，原因：" + e.getMessage());
        }
    }

    @ApiOperation("提交模型带版本")
    @RequestMapping(value = "/version/commit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult commitWithVersion(RuleDetail ruleDetail, String data, String isFirst, String isCommit,
                                            HttpServletRequest request) {
        String moduleName = ruleDetail.getModuleName();
        if (StringUtils.isBlank(moduleName)) {
            throw new IllegalArgumentException("参数[ruleName]不能为空");
        }
        if (StringUtils.isBlank(ruleDetail.getFolderId())) {
            throw new IllegalArgumentException("参数[folderId]不能为空");
        }
        if (StringUtils.isBlank(ruleDetail.getIsPublic())) {
            throw new IllegalArgumentException("参数[isPublic]不能为空");
        }
        String loginUserId = ControllerUtil.getLoginUserId(request);
        String accountUser = ControllerUtil.getRealUserAccount(request);
        if ("0".equals(isFirst)) {//
            // 校验权限
            RuleConfigController currentProxy = (RuleConfigController) AopContext.currentProxy();
            if ("1".equals(ruleDetail.getIsPublic())) {
                currentProxy.checkAuthCommitForPub(ruleDetail.getRuleName());
            } else {
                currentProxy.checkAuthCommit(ruleDetail.getRuleName());
            }

            ruleDetail.setPlatformUpdateUserJobNumber(accountUser);
        } else {
            try {
                ruleDetail.setPlatformCreateUserJobNumber(accountUser);
                Map result = ruleDetailService.stageOrCommitFirst(ruleDetail, data, loginUserId, true);
                if (null != result) {
                    return ResponseResult.createSuccessInfo("提交成功", result);
                } else {
                    return ResponseResult.createFailInfo("模型名称已经存在");
                }

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseResult.createFailInfo("提交失败");
            }
        }

        try {
            return ruleDetailService.commitWithVersion(ruleDetail, data, loginUserId,isCommit);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.createFailInfo("提交失败");
        }
    }

    @ApiOperation("发布模型带版本")
    @RequestMapping(value = "/version/publish", method = RequestMethod.POST)
    @ResponseBody
    @Deprecated
    public ResponseResult publishWithVersion(RuleDetail ruleDetail, String data, String isFirst,
                                             HttpServletRequest request) {
        String moduleName = ruleDetail.getModuleName();
        if (StringUtils.isBlank(moduleName)) {
            throw new IllegalArgumentException("参数[ruleName]不能为空");
        }
        if (StringUtils.isBlank(ruleDetail.getFolderId())) {
            throw new IllegalArgumentException("参数[folderId]不能为空");
        }
        if (StringUtils.isBlank(ruleDetail.getIsPublic())) {
            throw new IllegalArgumentException("参数[isPublic]不能为空");
        }
        if ("0".equals(isFirst)) {
            // 校验权限
            RuleConfigController currentProxy = (RuleConfigController) AopContext.currentProxy();
            if ("1".equals(ruleDetail.getIsPublic())) {
                currentProxy.checkAuthPublishForPub(ruleDetail.getRuleName());
            } else {
                currentProxy.checkAuthPublish(ruleDetail.getRuleName());
            }
        } else {
            if (ruleDetailService.checkModuleNameIsExist(moduleName, null)) {
                return ResponseResult.createFailInfo("规则名已存在.");
            }
        }


        String loginUserId = ControllerUtil.getLoginUserId(request);
        return ruleDetailService.publishWithVersion(ruleDetail, data, loginUserId);
    }

    @ApiOperation("模型的有效版本")
    @RequestMapping(value = "/versions", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult getVersions(String ruleName, String isPublic, String needDraft) {
        if (StringUtils.isBlank(ruleName)) {
            throw new IllegalArgumentException("参数[ruleName]不能为空");
        }
        if ("-1".equals(needDraft)) {
            return ruleDetailService.getAllVersions(ruleName, null, isPublic, needDraft);
        } else {
            return ruleDetailService.getVersions(ruleName, null, isPublic);
        }
    }

    @ApiOperation("模型的有效版本")
    @RequestMapping(value = "/versions/paged", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getVersionsWithPage(String ruleName, String ruleStatus, String isPublic,
                                                   @Nullable String startDate,
                                                   @Nullable String endDate, String start, String length) {
        if (StringUtils.isBlank(ruleName)) {
            throw new IllegalArgumentException("参数[ruleName]不能为空");
        }
        return ruleDetailService.getVersionsForPageList(ruleName, ruleStatus, startDate, endDate, isPublic, start, length);
    }

    @ApiOperation("模型指定版本的启用，模型其他停用")
    @RequestMapping(value = "/changeToExecute", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult changeToExecute(String ruleId, HttpServletRequest request) {
        String realUserAccount = ControllerUtil.getRealUserAccount(request);
        RuleDetail ruleDetail = ruleDetailService.getOne(ruleId);
        // 校验权限
        RuleConfigController currentProxy = (RuleConfigController) AopContext.currentProxy();
        if ("1".equals(ruleDetail.getIsPublic())) {
            currentProxy.checkAuthPublishForPub(ruleDetail.getRuleName());
        } else {
            currentProxy.checkAuthPublish(ruleDetail.getRuleName());
        }

        return ruleDetailService.changeStatus(ruleId, realUserAccount);
    }

    @ApiOperation("模型历史版本状态查询，判断历史版本中是否存在已启用的版本")
    @RequestMapping(value = "/checkPreVersionStatus", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult checkPreVersionStatus(String ruleId) {
        return ruleDetailService.checkPreVersionStatus(ruleId);
    }

    @ApiOperation("模型名称是否该库已经存在")
    @RequestMapping(value = "/checkRuleNameIsExist", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult checkModuleNameIsExist(String moduleName) {
        boolean result = ruleDetailService.checkModuleNameIsExist(moduleName, null);
        return ResponseResult.createSuccessInfo("查询成功", result ? 1 : 0);
    }

    @ApiOperation("单个模型的信息信息")
    @RequestMapping(value = "/getRuleByRuleId", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult getRuleByRuleId(String ruleId) {
        RuleDetailWithBLOBs result = ruleDetailService.getRule(ruleId);
        return ResponseResult.createSuccessInfo("查询成功", result);
    }

    @ApiOperation("刪除模型公共")
    @RequestMapping(value = "/pub/deleteByName", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult deleteByNameForPub(String ruleName, HttpServletRequest request) {
        // 校验权限
        RuleConfigController currentProxy = (RuleConfigController) AopContext.currentProxy();
        currentProxy.checkAuthDeleteForPub(ruleName);
        String realUserAccount = ControllerUtil.getRealUserAccount(request);
        return ruleDetailService.deleteRuleByName(ruleName, realUserAccount);
    }

    @ApiOperation("刪除模型场景")
    @RequestMapping(value = "/deleteByName", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult deleteByName(String ruleName, HttpServletRequest request) {
        // 校验权限
        RuleConfigController currentProxy = (RuleConfigController) AopContext.currentProxy();
        currentProxy.checkAuthDelete(ruleName);
        String realUserAccount = ControllerUtil.getRealUserAccount(request);
        return ruleDetailService.deleteRuleByName(ruleName, realUserAccount);
    }

    @ApiOperation("获取启用的模型的版本列表")
    @RequestMapping(value = "/getEnableVersion/baseInfo", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult getEnableVersionBaseInfoByRuleName(String ruleName) {
        List<Map<String, Object>> enableVersionBaseInfo = ruleDetailService.getEnableVersionBaseInfoByRuleName(ruleName);
        return ResponseResult.createSuccessInfo("查询成功", enableVersionBaseInfo);
    }

    // ------------------------- AB测试查询接口 ---------------------------------

    // 根据模型名称、产品名称、模型类型查询模型名称列表
    @RequestMapping(value = "/getModelByProduct", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult getModelByProduct(String moduleName, String productCode,
                                            String productName,
                                            String folderId,
                                            @RequestParam(value = "ruleType", required = false, defaultValue = "0") String ruleType) {
        List<Map<String, Object>> modelList = ruleDetailService.getModelByProduct(moduleName, productCode, productName,
                folderId, ruleType);
        return ResponseResult.createSuccessInfo("查询成功", modelList);
    }

    // 根据模型id、模型状态查询版本列表
    @RequestMapping(value = "/getVersionListByStatus", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult getVersionListByStatus(String ruleName,
                                                 @RequestParam(value = "ruleStatus", required = false, defaultValue = "1") String ruleStatus) {
        List<Map<String, Object>> modelList = ruleDetailService.getVersionListByStatus(ruleName, ruleStatus);
        return ResponseResult.createSuccessInfo("查询成功", modelList);
    }

    // 根据模型id查询未启用的版本列表，包括草稿
    @RequestMapping(value = "/getVersionListWithDraftByStatus", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult getVersionListWithDraftByStatus(String ruleName) {
        return ruleDetailService.getVersionListWithDraftByStatus(ruleName);
    }

    // ------------------------- 提供业务对接页面跳转接口 ---------------------------------

    /**
     * 新增规则页面
     */
    @RequestMapping("/ruleConfig/offer")
    public String ruleConfigOffer(String folderId,
                                  String ruleType,
                                  String pageType,
                                  String ruleName,
                                  Model model) throws Exception {
        if (folderId == null || folderId.isEmpty()) {
            throw new Exception("The folder id is null.");
        }
        if (ruleType == null || ruleType.isEmpty()) {
            throw new Exception("The ruleType is null.");
        }

        Map<String, Object> variableMap = variableService.getVariableTree(folderId);
        model.addAttribute("inVariableTree", JsonUtils.beanToJson(variableMap.get("inVariableTree")).toString());
        model.addAttribute("outVariableTree", JsonUtils.beanToJson(variableMap.get("outVariableTree")).toString());
        List<ApiConf> apiList = apiService.getAllApiByFolderId(folderId);
        model.addAttribute("apiList", apiList);

        // 公共接口
        final List<ApiConf> pubApiList = apiService.pubGetAllApiList();
        model.addAttribute("pubApiList", pubApiList);

        // 指标类型
        List<Map<String, Object>> kpiTypeList = kpiService.getKpiType();
        model.addAttribute("kpiTypeList", JsonUtils.beanToJson(kpiTypeList).toString());
        model.addAttribute("kpiMap", "[]");

        model.addAttribute("folderId", folderId);
        model.addAttribute("ruleContent", "{}");
        model.addAttribute("ruleType", ruleType);
        model.addAttribute("optMode", MODE_INSERT);
        model.addAttribute("pageType", pageType);
        model.addAttribute("ruleName", ruleName);
        return "/pages/ruleConfigOffer";
    }

    /**
     * 修改规则页面
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/updateRule/offer")
    public String updateModelRuleIndexOffer(String ruleId,
                                            String folderId,
                                            @Nullable String modelGroupId,
                                            String pageType,
                                            Model model,
                                            HttpServletRequest request) throws Exception {
        final String currentUserId = ControllerUtil.getLoginUserId(request);
        if (ruleId == null || ruleId.isEmpty()) {
            throw new Exception("The ruleId id is null.");
        }
        if (folderId == null || folderId.isEmpty()) {
            throw new Exception("The folder id is null.");
        }
        String ruleContent = null;
        RuleDetailWithBLOBs rule = ruleDetailService.getRule(ruleId);
        if (rule == null) {
            throw new Exception("There is no rule[" + ruleId + "].");
        }
        ruleContent = rule.getRuleContent();
        if (ruleContent == null || ruleContent.isEmpty()) {
            ruleContent = "{}";
        }
        model.addAttribute("modelGroupId", modelGroupId);
        model.addAttribute("ruleContent", ruleContent.trim());
        model.addAttribute("ruleName", rule.getRuleName());
        model.addAttribute("moduleName", rule.getModuleName());
        model.addAttribute("ruleType", rule.getRuleType());
        model.addAttribute("ruleDesc", rule.getRuleDesc());
        model.addAttribute("version", rule.getVersion());
        model.addAttribute("versionDesc", rule.getVersionDesc());

        Map<String, Object> variableMap = variableService.getVariableTree(folderId);
        model.addAttribute("inVariableTree", JsonUtils.beanToJson(variableMap.get("inVariableTree")).toString());
        model.addAttribute("outVariableTree", JsonUtils.beanToJson(variableMap.get("outVariableTree")).toString());
        model.addAttribute("orgTree", JsonUtils.beanToJson(variableMap.get("outVariableTree")).toString());
        List<ApiConf> apiList = apiService.getAllApiByFolderId(folderId);
        model.addAttribute("apiList", apiList);

        // 公共接口
        final List<ApiConf> pubApiList = apiService.pubGetAllApiList();
        model.addAttribute("pubApiList", pubApiList);

        // 指标类型
        List<Map<String, Object>> kpiTypeList = kpiService.getKpiType();
        model.addAttribute("kpiTypeList", JsonUtils.beanToJson(kpiTypeList).toString());

        // 模型引用的 指标
        List<Map<String, Object>> kpiInfoByRuleId = ruleReferenceService.selectKpiInfoByRuleId(ruleId);
        model.addAttribute("kpiMap", JsonUtils.beanToJson(kpiInfoByRuleId).toString());

        model.addAttribute("ruleId", ruleId);
        model.addAttribute("folderId", folderId);
        model.addAttribute("isLog", rule.getIsLog());
        model.addAttribute("optMode", MODE_UPDATE);
        String token = ControllerUtil.getToken();
        model.addAttribute("token", token);
        if (rule != null && ConstantUtil.RULE_STATUS_RUNNING.equals(rule.getRuleStatus())) {
            model.addAttribute("optMode", MODE_VIEW);
        }

        model.addAttribute("pageType", pageType);
        return "/pages/ruleConfigOffer";

    }

    /**
     * 获取模型接口说明
     */
    @GetMapping("api/info")
    @ResponseBody
    public ResponseResult getModelApiInfo(String ruleId) {
        if (StringUtils.isBlank(ruleId)) {
            throw new IllegalArgumentException("参数[ruleId]不能为空");
        }

        RuleDetailWithBLOBs rule = ruleDetailService.getRule(ruleId);
        if (rule == null) {
            throw new IllegalArgumentException("模型不存在[" + ruleId + "].");
        }

        // 获取模型引用的非实体（但包括实体参数内的嵌套参数）公共参数 包括接口、指标引用的参数
        List<Map<String, Object>> variableList = variableService.getVariableMapByRuleId(ruleId, "K1");

        // 输出参数
        List<Map<String, Object>> outVariableList = variableService.getOutVariableMapByRuleId(ruleId);

        ImmutableMap result = ImmutableMap.of(
                "variableList", variableList,
                "outVariableList", outVariableList);

        return ResponseResult.createSuccessInfo("查询成功", result);
    }
}
