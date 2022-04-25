package com.bonc.frame.controller.commonresource;

import com.bonc.frame.entity.auth.DeptChannelTree;
import com.bonc.frame.entity.commonresource.*;
import com.bonc.frame.entity.rule.RuleDetail;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissionsRequires;
import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.service.modelBase.ModelBaseService;
import com.bonc.frame.service.rule.RuleDetailService;
import com.bonc.frame.service.rule.RuleFolderService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 *
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
    private RuleDetailService ruleDetailService;

    @Autowired
    private RuleFolderService ruleFolderService;

    @RequestMapping("/view")
    public String view(String idx, String childOpen, String jumpRuleName, Model model, HttpServletRequest request) {
        model.addAttribute("idx", idx);//菜单状态标识
        model.addAttribute("childOpen", childOpen);
        model.addAttribute("jumpRuleName", jumpRuleName);
        String modelBaseId = ruleFolderService.getModelBaseId();
        model.addAttribute("folderId", modelBaseId);
        if (idx.equals("21")) {
            return "/pages/modelBase/modelGroup";
        }
        return "/pages/modelBase/modelBaseIndex";
    }

    // ------------------------ 模型组管理 ------------------------

    @RequestMapping("/group/list")
    @ResponseBody
    public ResponseResult getModelGroups(String modelGroupName, HttpServletRequest request) {
        if (modelGroupName == null) {
            modelGroupName = "";
        }
        // 验证数据权限
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return modelBaseService.getModelGroups(modelGroupName,loginUserId);
    }

    @RequestMapping("/group/paged")
    @ResponseBody
    public Map<String, Object> getModelGroupsPaged(String modelGroupName,String channelId, String start, String length,HttpServletRequest request) {
        if (modelGroupName == null) {
            modelGroupName = "";
        }
        if (channelId == null || channelId.equals("")) {
            channelId = null;
        }
        // 验证数据权限
        final String loginUserId = ControllerUtil.getLoginUserId(request);

        return modelBaseService.getModelGroupsPaged(loginUserId,modelGroupName,channelId, start, length);
    }

    @RequestMapping("/group/create")
    @ResponseBody
    public ResponseResult createModelGroup(ModelGroupDto modelGroup, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return modelBaseService.createModelGroup(modelGroup, loginUserId);
    }

    @PermissionsRequires(value = "/pub/modelGroup/update?modelGroupId", resourceType = ResourceType.DATA_PUB_MODEL_GROUP)
    @RequestMapping("/group/update")
    @ResponseBody
    public ResponseResult updateModelGroup(ModelGroupDto modelGroup, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return modelBaseService.updateModelGroup(modelGroup, loginUserId);
    }

    @PermissionsRequires(value = "/pub/modelGroup/delete?modelGroupId", resourceType = ResourceType.DATA_PUB_MODEL_GROUP)
    @RequestMapping("/group/delete")
    @ResponseBody
    public ResponseResult deleteRuleSetGroup(String modelGroupId) {
        return modelBaseService.deleteModelGroup(modelGroupId);
    }

    @PermissionsRequires(value = "/pub/modelGroup/update?modelGroupId", resourceType = ResourceType.DATA_PUB_MODEL_GROUP)
    @RequestMapping(value = "/group/update/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkUpdateGroup(String modelGroupId) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/pub/modelGroup/delete?modelGroupId", resourceType = ResourceType.DATA_PUB_MODEL_GROUP)
    @RequestMapping(value = "/group/delete/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkDeleteGroup(String modelGroupId) {
        return ResponseResult.createSuccessInfo();
    }

    /**
     * 产品设置调用渠道
     * @param modelGroupId  产品的id
     * @param channelIds  渠道的id的集合
     * @return 操作结果
     */

    @PermissionsRequires(value = "/pub/modelGroup/channel?modelGroupId", resourceType = ResourceType.DATA_PUB_MODEL_GROUP)
    @RequestMapping(value = "/addChannel", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult groupAddChannel(String modelGroupId, List<String> channelIds) {
        return modelBaseService.groupAddChannel(modelGroupId, channelIds);
    }
    /**
     *
     * 设置调用渠道校验
     *
     * */
    @PermissionsRequires(value = "/pub/modelGroup/channel?modelGroupId", resourceType = ResourceType.DATA_PUB_MODEL_GROUP)
    @RequestMapping(value = "/group/channel/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult checkGroup(String modelGroupId) {
        return ResponseResult.createSuccessInfo();
    }

    /**
     * 展示渠道数据用于关联与选择
     * @param request 获取权限数据
     * @param modelGroupId 产品id，新增时可以传null
     * @return 渠道树
     */
    @RequestMapping(value = "/channelList")
    @ResponseBody
    public ResponseResult channelList(HttpServletRequest request, String modelGroupId) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        List<ModelGroupChannelVo> voList = modelBaseService.channelList(loginUserId,modelGroupId);
        return ResponseResult.createSuccessInfo("success", voList);
    }

    /**
     * 产品查看其下所有的模型-页面跳转后数据展示
     * @param modelGroupId 产品的id
     * @param request 区分权限
     * @return 模型结果集
     */
    @PermissionsRequires(value = "/pub/modelGroup/modelView?modelGroupId", resourceType = ResourceType.DATA_PUB_MODEL_GROUP)
    @RequestMapping("/group/getModel")
    @ResponseBody
    public ResponseResult getModelByGroupId(String modelGroupId, HttpServletRequest request) {
        // 验证数据权限
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        ModelGroupInfo info = ruleDetailService.getModelByGroupId(modelGroupId, loginUserId);
        return ResponseResult.createSuccessInfo("success", info);
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


    /**
     * 产品中添加（其他）分类中的模型到本产品
     * 直接将对应模型的所属产品id切换
     * @param modelList 要添加的模型集合
     * @param modelGroupId 要添加到的产品的id
     * @return 结果
     */
    @RequestMapping("/group/addModel")
    @ResponseBody
    public ResponseResult groupAddModel(List<RuleDetailHeader> modelList,String modelGroupId) {
        return ruleDetailService.groupAddModel(modelList,modelGroupId);
    }

    /**
     * 根据id获取产品信息
     * @param modelGroupId 产品id
     * @return 产品信息
     */
    @RequestMapping("/group/getGroupInfoById")
    @ResponseBody
    public ResponseResult getGroupInfoById(String modelGroupId) {
        // 验证数据权限
        ModelGroup mg = modelBaseService.getGroupInfoById(modelGroupId);
        return ResponseResult.createSuccessInfo("success", mg);
    }

    /**
     * 根据模型头id获取版本号及对应id
     * @param modelId 模型id
     * @return id及版本号
     */
    @RequestMapping("/group/modelGetVersion")
    @ResponseBody
    public ResponseResult modelGetVersion(String modelId) {
        List<ModelVersion> list = modelBaseService.modelGetVersion(modelId);
        return ResponseResult.createSuccessInfo("success", list);
    }

    /**
     * 根据模型版本id获取关联的规则集
     * @param modelId 模型id
     * @return 规则集数据
     */
    @RequestMapping("/group/modelVersionWithRuleSet")
    @ResponseBody
    public ResponseResult modelVersionWithRuleSet(String modelId) {
        List<RuleSetForModel> list = modelBaseService.modelVersionWithRuleSet(modelId);
        return ResponseResult.createSuccessInfo("success", list);
    }


}
