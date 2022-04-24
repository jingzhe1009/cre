package com.bonc.frame.controller.commonresource;

import com.bonc.frame.entity.commonresource.RuleSet;
import com.bonc.frame.entity.commonresource.RuleSetGroup;
import com.bonc.frame.entity.commonresource.RuleSetHeader;
import com.bonc.frame.entity.commonresource.RuleSetReferenceExt;
import com.bonc.frame.entity.rule.RuleSetWithModel;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissionsRequires;
import com.bonc.frame.service.kpi.KpiService;
import com.bonc.frame.service.ruleSetBase.RuleSetBaseService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.JsonUtils;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 规则库
 *
 * @author yedunyao
 * @date 2019/9/2 10:59
 */
@Controller
@RequestMapping("/ruleSet")
public class RuleSetBaseController {

    private static Log log = LogFactory.getLog(RuleSetBaseController.class);

    @Autowired
    private RuleSetBaseService ruleSetBaseService;

    @Autowired
    private VariableService variableService;

    @Autowired
    private KpiService kpiService;

    @RequestMapping("/view")
    public String view(String idx, String childOpen, Model model, HttpServletRequest request) {
        model.addAttribute("idx", idx);//菜单状态标识
        model.addAttribute("childOpen", childOpen);

        Map<String, Object> variableMap = variableService.getPubVariableTree();
        model.addAttribute("outVariableTree", JsonUtils.beanToJson(variableMap.get("outVariableTree")).toString());

        // 带有选择指标的
        Map<String, Object> variableAndKpiMap = variableService.getPubVariableAndKpiTree();
        model.addAttribute("variableAndKpiTree", JsonUtils.beanToJson(variableAndKpiMap.get("variableAndKpiTree")).toString());

        // 指标类型
        List<Map<String, Object>> kpiTypeList = kpiService.getKpiType();
        model.addAttribute("kpiTypeList", JsonUtils.beanToJson(kpiTypeList).toString());

        return "/pages/ruleSet/ruleSetIndex";
    }

    @RequestMapping("/view/offer")
    public String viewOffer(String idx, String childOpen, Model model, HttpServletRequest request) {
        model.addAttribute("idx", idx);//菜单状态标识
        model.addAttribute("childOpen", childOpen);

        Map<String, Object> variableMap = variableService.getPubVariableTree();
        model.addAttribute("outVariableTree", JsonUtils.beanToJson(variableMap.get("outVariableTree")).toString());

        // 带有选择指标的
        Map<String, Object> variableAndKpiMap = variableService.getPubVariableAndKpiTree();
        model.addAttribute("variableAndKpiTree", JsonUtils.beanToJson(variableAndKpiMap.get("variableAndKpiTree")).toString());

        // 指标类型
        List<Map<String, Object>> kpiTypeList = kpiService.getKpiType();
        model.addAttribute("kpiTypeList", JsonUtils.beanToJson(kpiTypeList).toString());

        return "/pages/ruleSet/ruleSetIndexOffer";
    }

    // ------------------------ 规则集头信息管理 ------------------------

    @RequestMapping("/header/check/ruleSetName")
    @ResponseBody
    public ResponseResult checkNameIsExist(String ruleSetName, @Nullable String ruleSetHeaderId) {
        if (StringUtils.isBlank(ruleSetName)) {
            return ResponseResult.createFailInfo("请求参数[ruleSetName]不能为空");
        }
        if (ruleSetBaseService.checkNameIsExist(ruleSetName, ruleSetHeaderId)) {
            return ResponseResult.createFailInfo("规则集名称已存在");
        }
        return ResponseResult.createSuccessInfo();
    }

    @RequestMapping("/header/check/existEnableVersion")
    @ResponseBody
    public ResponseResult checkAnyVersionIsEnable(String ruleSetHeaderId) {
        if (StringUtils.isBlank(ruleSetHeaderId)) {
            return ResponseResult.createFailInfo("请求参数[ruleSetHeaderId]不能为空");
        }
        if (ruleSetBaseService.checkAnyVersionIsEnable(ruleSetHeaderId)) {
            return ResponseResult.createFailInfo("规则集存在已启用的版本");
        }
        return ResponseResult.createSuccessInfo();
    }

    @RequestMapping("/header/list")
    @ResponseBody
    public ResponseResult getHeaderList(@Nullable String ruleSetName,
                                        @Nullable String ruleSetGroupName,
                                        @Nullable String startDate,
                                        @Nullable String endDate) {
        return ruleSetBaseService.getHeaderList(ruleSetName, ruleSetGroupName,
                startDate, endDate);
    }

    @RequestMapping("/header/paged")
    @ResponseBody
    public Map<String, Object> getHeaderList(@Nullable String ruleSetName,
                                             @Nullable String ruleSetGroupName,
                                             @Nullable String startDate,
                                             @Nullable String endDate,
                                             String start, String length) {
        return ruleSetBaseService.getHeaderList(ruleSetName, ruleSetGroupName,
                startDate, endDate, start, length);
    }

    @RequestMapping(value = "/header/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult createRuleSetHeader(RuleSetHeader ruleSetHeader, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return ruleSetBaseService.createRuleSetHeader(ruleSetHeader, loginUserId);
    }

    @PermissionsRequires(value = "/pub/ruleSet/update?ruleSetHeader.ruleSetHeaderId", resourceType = ResourceType.DATA_PUB_RULE_SET)
    @RequestMapping(value = "/header/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult updateRuleSetHeader(RuleSetHeader ruleSetHeader, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return ruleSetBaseService.updateRuleSetHeader(ruleSetHeader, loginUserId);
    }

    @PermissionsRequires(value = "/pub/ruleSet/delete?ruleSetHeaderId", resourceType = ResourceType.DATA_PUB_RULE_SET)
    @RequestMapping("/header/delete")
    @ResponseBody
    public ResponseResult deleteRuleSetHeader(String ruleSetHeaderId) {
        return ruleSetBaseService.deleteRuleSetHeader(ruleSetHeaderId);
    }

    // ------------------------ 规则集版本管理 ------------------------

    @RequestMapping("/check/used")
    @ResponseBody
    public ResponseResult checkRuleSetUsed(String ruleSetId) {
        boolean isUsed = ruleSetBaseService.checkRuleSetUsed(ruleSetId);
        if (isUsed) {
            return ResponseResult.createFailInfo("当前版本的规则集正在使用中");
        }
        return ResponseResult.createSuccessInfo();
    }

    @RequestMapping("/getOne")
    @ResponseBody
    public ResponseResult getRuleSetByRuleSetId(String ruleSetId) {
        RuleSetReferenceExt ruleSet = ruleSetBaseService.getRuleSetByRuleSetId(ruleSetId);
        return ResponseResult.createSuccessInfo("success", ruleSet);
    }

    @RequestMapping("/list")
    @ResponseBody
    public ResponseResult getRuleSetVersionList(String ruleSetHeaderId,
                                                @Nullable String enable,
                                                @Nullable String startDate,
                                                @Nullable String endDate) {
        return ruleSetBaseService.getRuleSetVersionList(ruleSetHeaderId, enable,
                startDate, endDate);
    }

    @RequestMapping("/paged")
    @ResponseBody
    public Map<String, Object> getRuleSetVersionList(String ruleSetHeaderId,
                                                     @Nullable String enable,
                                                     @Nullable String startDate,
                                                     @Nullable String endDate,
                                                     String start, String length) {
        return ruleSetBaseService.getRuleSetVersionList(ruleSetHeaderId, enable,
                startDate, endDate, start, length);
    }

    @PermissionsRequires(value = "/pub/ruleSet/version/add?ruleSet.ruleSetHeaderId", resourceType = ResourceType.DATA_PUB_RULE_SET)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult createRuleSet(RuleSet ruleSet, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        if(null==ruleSet.getRuleSetName()){
            return ResponseResult.createFailInfo("参数[ruleSetName]不能为空");
        }
        return ruleSetBaseService.createRuleSet(ruleSet, loginUserId);
    }

    @PermissionsRequires(value = "/pub/ruleSet/update?ruleSet.ruleSetHeaderId", resourceType = ResourceType.DATA_PUB_RULE_SET)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult updateRuleSet(RuleSet ruleSet, String oldRuleContent, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return ruleSetBaseService.updateRuleSet(ruleSet, oldRuleContent, loginUserId);
    }

    @PermissionsRequires(value = "/pub/ruleSet/version/enable?ruleSetHeaderId", resourceType = ResourceType.DATA_PUB_RULE_SET)
    @RequestMapping(value = "/enable", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseResult enable(String ruleSetHeaderId, String ruleSetId, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return ruleSetBaseService.enable(ruleSetId, loginUserId);
    }

    @PermissionsRequires(value = "/pub/ruleSet/version/enable?ruleSetHeaderId", resourceType = ResourceType.DATA_PUB_RULE_SET)
    @RequestMapping(value = "/disable", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseResult disable(String ruleSetHeaderId, String ruleSetId, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return ruleSetBaseService.disable(ruleSetId, loginUserId);
    }

    @PermissionsRequires(value = "/pub/ruleSet/delete?ruleSetHeaderId", resourceType = ResourceType.DATA_PUB_RULE_SET)
    @RequestMapping(value = "/delete")
    @ResponseBody
    public ResponseResult deleteRuleSet(String ruleSetHeaderId, String ruleSetId) {
        return ruleSetBaseService.deleteRuleSet(ruleSetId);
    }

    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult publish(RuleSetHeader ruleSetHeader,
                                  RuleSet ruleSet, String ruleId,
                                  final String folderId,
                                  HttpServletRequest request) {
        String loginUserId = ControllerUtil.getLoginUserId(request);
        return ruleSetBaseService.publish(ruleSetHeader, ruleSet, ruleId, folderId, loginUserId);
    }


    // ------------------------ 规则集组管理 ------------------------

    @RequestMapping("/group/list")
    @ResponseBody
    public ResponseResult getRuleSetGroups(String ruleSetGroupName) {
        if (ruleSetGroupName == null) {
            ruleSetGroupName = "";
        }
        return ruleSetBaseService.getRuleSetGroups(ruleSetGroupName);
    }

    //规则集查询指标信息
    @RequestMapping("/getKpiByRuleSetId")
    @ResponseBody
    public ResponseResult getKpiByRuleSetId(String ruleSetId) {
        List<Map<String,String>> list =ruleSetBaseService.getKpiByRuleSetId(ruleSetId);
        return ResponseResult.createSuccessInfo("success",list);
    }

    @PostMapping("/getRuleSetIdByHeader")
    @ResponseBody
    public List<Map<String,String>> getRuleSetIdByHeader(String ruleSetHeaderId){
        return ruleSetBaseService.getRuleSetIdByHeader(ruleSetHeaderId);
    }

    //规则集查看模型信息
    @RequestMapping("/getModelByRuleSetId")
    @ResponseBody
    public ResponseResult getModelByRuleSetId(String ruleSetId) {
        List<RuleSetWithModel> list =ruleSetBaseService.getModelByRuleSetId(ruleSetId);
        return ResponseResult.createSuccessInfo("success",list);
    }

    @RequestMapping("/group/paged")
    @ResponseBody
    public Map<String, Object> getRuleSetGroupsPaged(String ruleSetGroupName,
                                                     String start, String length) {
        if (ruleSetGroupName == null) {
            ruleSetGroupName = "";
        }
        return ruleSetBaseService.getRuleSetGroupsPaged(ruleSetGroupName, start, length);
    }

    @RequestMapping(value = "/group/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult createRuleSetGroup(RuleSetGroup ruleSetGroup, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return ruleSetBaseService.createRuleSetGroup(ruleSetGroup, loginUserId);
    }

    @RequestMapping(value = "/group/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult updateRuleSetGroup(RuleSetGroup ruleSetGroup, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return ruleSetBaseService.updateRuleSetGroup(ruleSetGroup, loginUserId);
    }

    @RequestMapping(value = "/group/delete")
    @ResponseBody
    public ResponseResult deleteRuleSetGroup(String ruleSetGroupId) {
        return ruleSetBaseService.deleteRuleSetGroup(ruleSetGroupId);
    }

    // ------------------------------- 权限校验 -------------------------------

    @PermissionsRequires(value = "/pub/ruleSet/update?ruleSetHeaderId", resourceType = ResourceType.DATA_PUB_RULE_SET)
    @RequestMapping(value = "/update/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkUpdate(String ruleSetHeaderId) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/pub/ruleSet/delete?ruleSetHeaderId", resourceType = ResourceType.DATA_PUB_RULE_SET)
    @RequestMapping(value = "/delete/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkDelete(String ruleSetHeaderId) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/pub/ruleSet/enable?ruleSetHeaderId", resourceType = ResourceType.DATA_PUB_RULE_SET)
    @RequestMapping(value = "/enable/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkAuthEnable(String ruleSetHeaderId) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/pub/ruleSet/version/add?ruleSetHeaderId", resourceType = ResourceType.DATA_PUB_RULE_SET)
    @RequestMapping(value = "/version/add/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkVersionAdd(String ruleSetHeaderId) {
        return ResponseResult.createSuccessInfo();
    }


}
