package com.bonc.frame.controller.rule;

import com.bonc.frame.entity.rule.RuleDetail;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.rulefolder.RuleFolder;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissionsRequires;
import com.bonc.frame.service.rule.RuleDetailService;
import com.bonc.frame.service.rule.RuleFolderService;
import com.bonc.frame.service.rule.RuleService;
import com.bonc.frame.service.syslog.SysLogService;
import com.bonc.frame.util.ConstantUtil;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.JsonUtils;
import com.bonc.frame.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author jsj
 */
@Controller
@RequestMapping("/ruleFolder")
public class RulePackageMgrController {

    @Autowired
    private RuleService ruleService;

    @Autowired
    private RuleDetailService ruleDetailService;

    @Autowired
    private RuleFolderService ruleFolderService;

    @Autowired
    private SysLogService sysLogService;

    @RequestMapping("/ruleName")
    @ResponseBody
    public List<Map<String, Object>> getRuleNameList(HttpServletRequest request) {
        String foldId = ControllerUtil.getParam(request, "foldId");
        List<Map<String, Object>> ruleFolderList = this.ruleDetailService.getRuleNameList(foldId);
        return ruleFolderList;
    }

    @RequestMapping("/ruleName/inHeader")
    @ResponseBody
    public List<Map<String, Object>> getRuleNameInHeaderList(HttpServletRequest request) {
        String foldId = ControllerUtil.getParam(request, "foldId");
        List<Map<String, Object>> ruleFolderList = this.ruleDetailService.getRuleNameInHeaderList(foldId);
        return ruleFolderList;
    }

    @RequestMapping("/rulePackageMgr")
    public String main(String folderId, String childOpen, Model model) throws Exception {
        if (folderId == null) {
            throw new Exception("The folderId is null.");
        }
        RuleFolder ruleFolder = ruleFolderService.getRuleFolderDetail(folderId, null);
        if (ruleFolder != null) {
            model.addAttribute("folderName", ruleFolder.getFolderName());
        }
        List<RuleDetail> list = ruleDetailService.getRuleHeaderListByFolderId(folderId);
        model.addAttribute("ruleList", list);
        model.addAttribute("folderId", folderId);
        model.addAttribute("idx", folderId);
        model.addAttribute("childOpen", childOpen);
        return "/pages/rule/rulePackage";
    }
    /**
     *
     * 查看模型权限校验
     * */

    @PermissionsRequires(value = "/pub/modelGroup/modelView?modelGroupId", resourceType = ResourceType.DATA_PUB_MODEL_GROUP)
    @RequestMapping(value = "/group/modelView/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkModelGroup(String modelGroupId) {
        return ResponseResult.createSuccessInfo();
    }

    @RequestMapping("/foldersIndex")
    public String foldersIndex(String idx, String childOpen, Model model) throws Exception {
        List<Map<String, Object>> ruleFolderList = this.ruleService.getRuleFolder();
        model.addAttribute("idx", idx);
        model.addAttribute("childOpen", childOpen);
        model.addAttribute("ruleFolderList", JsonUtils.beanToJson(ruleFolderList).toString());
        return "/pages/folder/foldersIndex";
    }

    @RequestMapping("/ruleFolderList")
    @ResponseBody
    public List<Map<String, Object>> getRuleFolderList(HttpServletRequest request) {
        List<Map<String, Object>> ruleFolderList = this.ruleService.getRuleFolder();
        return ruleFolderList;
    }

    @RequestMapping("/modelBaseId")
    @ResponseBody
    public ResponseResult getModelBaseId() {
        String modelBaseId = this.ruleFolderService.getModelBaseId();
        return ResponseResult.createSuccessInfo("", modelBaseId);
    }

    @PermissionsRequires(value = "/folder/delete?folderId", resourceType = ResourceType.DATA_FOLDER)
    @RequestMapping("/deleteFolder")
    @ResponseBody
    public ResponseResult deleteFolder(String folderId, HttpServletRequest request) {
        String accountUser = ControllerUtil.getRealUserAccount(request);
        ResponseResult result = ruleFolderService.deleteFolder(folderId, ControllerUtil.getLoginUserId(request), accountUser);
        return result;
    }

    @RequestMapping("/copyRule")
    @ResponseBody
    @Deprecated
    public ResponseResult copyRule(String ruleId, String ruleName, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        RuleDetailWithBLOBs rule = ruleDetailService.getRule(ruleId);
        rule.setCreateDate(new Date());
        rule.setCreatePerson(loginUserId);
        rule.setRuleName(ruleName);
        rule.setUpdateDate(null);
        rule.setUpdatePerson(null);
        rule.setRuleStatus(ConstantUtil.RULE_STATUS_READY);
        ResponseResult result = ruleDetailService.insertRule(rule, rule.getRuleContent(), loginUserId);
        return result;
    }

    @RequestMapping("/insertRule")
    @ResponseBody
    @Deprecated
    public ResponseResult insertRule(String ruleName, String ruleFloder, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        RuleDetailWithBLOBs rule = new RuleDetailWithBLOBs();
        rule.setCreateDate(new Date());
        rule.setFolderId(ruleFloder);
        rule.setRuleType("1");
        rule.setCreatePerson(loginUserId);
        rule.setRuleName(ruleName);
        rule.setUpdateDate(null);
        rule.setUpdatePerson(null);
        rule.setRuleStatus(ConstantUtil.RULE_STATUS_READY);
        ResponseResult result = ruleDetailService.insertRule(rule, rule.getRuleContent(), loginUserId);
        return result;
    }

    // ------------------------------- 权限校验 -------------------------------

    @PermissionsRequires(value = "/folder/update?folderId", resourceType = ResourceType.DATA_FOLDER)
    @RequestMapping(value = "/update/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult update(String folderId) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/folder/delete?folderId", resourceType = ResourceType.DATA_FOLDER)
    @RequestMapping(value = "/delete/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult delete(String folderId) {
        return ResponseResult.createSuccessInfo();
    }
}
