package com.bonc.frame.controller.commonresource;

import com.bonc.frame.entity.commonresource.ModelGroup;
import com.bonc.frame.service.modelBase.ModelBaseService;
import com.bonc.frame.service.rule.RuleFolderService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 模型库
 *
 * @author yedunyao
 * @date 2019/9/2 10:59
 */
@Controller
@RequestMapping("/modelBase")
public class ModelBaseController {

    @Autowired
    private ModelBaseService modelBaseService;

    @Autowired
    private RuleFolderService ruleFolderService;

    @RequestMapping("/view")
    public String view(String idx, String childOpen, String jumpRuleName, Model model, HttpServletRequest request) {
        model.addAttribute("idx", idx);//菜单状态标识
        model.addAttribute("childOpen", childOpen);
        model.addAttribute("jumpRuleName", jumpRuleName);
        String modelBaseId = ruleFolderService.getModelBaseId();
        model.addAttribute("folderId", modelBaseId);
        return "/pages/modelBase/modelBaseIndex";
    }

    // ------------------------ 模型组管理 ------------------------

    @RequestMapping("/group/list")
    @ResponseBody
    public ResponseResult getModelGroups(String modelGroupName) {
        if (modelGroupName == null) {
            modelGroupName = "";
        }
        return modelBaseService.getModelGroups(modelGroupName);
    }

    @RequestMapping("/group/paged")
    @ResponseBody
    public Map<String, Object> getModelGroupsPaged(String modelGroupName, String start, String length) {
        if (modelGroupName == null) {
            modelGroupName = "";
        }
        return modelBaseService.getModelGroupsPaged(modelGroupName, start, length);
    }

    @RequestMapping("/group/create")
    @ResponseBody
    public ResponseResult createModelGroup(ModelGroup modelGroup, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return modelBaseService.createModelGroup(modelGroup, loginUserId);
    }

    @RequestMapping("/group/update")
    @ResponseBody
    public ResponseResult updateModelGroup(ModelGroup modelGroup, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return modelBaseService.updateModelGroup(modelGroup, loginUserId);
    }

    @RequestMapping("/group/delete")
    @ResponseBody
    public ResponseResult deleteRuleSetGroup(String modelGroupId) {
        return modelBaseService.deleteModelGroup(modelGroupId);
    }

}
