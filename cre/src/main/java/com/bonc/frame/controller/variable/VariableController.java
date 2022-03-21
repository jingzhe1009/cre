package com.bonc.frame.controller.variable;

import com.bonc.frame.entity.commonresource.VariableGroup;
import com.bonc.frame.entity.commonresource.VariableGroupExt;
import com.bonc.frame.entity.variable.Variable;
import com.bonc.frame.entity.variableentity.VariableEntity;
import com.bonc.frame.entity.variableentityrelation.VariableEntityRelation;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissionsRequires;
import com.bonc.frame.service.syslog.SysLogService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/variable")
public class VariableController {

    @Autowired
    private VariableService variableService;

    @Autowired
    private SysLogService sysLogService;

    @RequestMapping("/variableConf")
    public String variableConf() {
        return "/variableConf";
    }

    /*
     *	修改变量
     */
    @RequestMapping("/updateVariable")
    @ResponseBody
    public ResponseResult updateVariable(Variable variable, HttpServletRequest request) {
        variable.setUpdateDate(new Date());
        variable.setUpdatePerson(ControllerUtil.getLoginUserId(request));
        ResponseResult result = variableService.updateVariable(variable);
        return result;
    }

    /*
     *	删除变量
     */
    @RequestMapping("/deleteVariable")
    @ResponseBody
    public ResponseResult deleteVariable(Variable variable, HttpServletRequest request) {
        ResponseResult result = variableService.deleteVariable(variable);
        return result;
    }

    @RequestMapping("/isDeleteEntity")
    @ResponseBody
    public ResponseResult isDeleteEntity(Variable variable, HttpServletRequest request) {
        ResponseResult result = variableService.deleteVariable(variable);
        return result;
    }

    /*
     *	查询变量
     */
    @RequestMapping("/queryVariable")
    @ResponseBody
    public Map<String, Object> queryVariable(Variable variable, HttpServletRequest request) {
        //每页显示10条数据
        String start = request.getParameter("start");
        String size = request.getParameter("length");
        //folderId 规则属组ID
        Map<String, Object> variableMap = variableService.selectVariables(variable, start, size);
        return variableMap;
    }

    @RequestMapping("/queryVariablesByFoldId")
    @ResponseBody
    public Map<String, Object> selectVariablesByFolderId(Variable variable, HttpServletRequest request) {
        //每页显示10条数据
        String start = request.getParameter("start");
        String size = request.getParameter("length");
        //folderId 规则属组ID
        Map<String, Object> variableMap = variableService.selectVariablesByFolderId(variable, start, size);
        return variableMap;
    }

    /**
     * 扁平化 查询所有非实体类型的参数，包括嵌套的基本类型参数
     *
     * @param variable
     * @param request
     * @return
     */
    @RequestMapping("/queryVariablesByFoldId/flat")
    @ResponseBody
    public Map<String, Object> selectFlatVariablesByFolderId(Variable variable, HttpServletRequest request) {
        //每页显示10条数据
        String start = request.getParameter("start");
        String size = request.getParameter("length");
        //folderId 规则属组ID
        Map<String, Object> variableMap = variableService.selectFlatVariablesByFolderId(variable, start, size);
        return variableMap;
    }

    /**
     * 查询模型引用的所有非实体参数(默认为输入参数)，包括公共参数、私有参数、接口引用参数、指标引用参数
     *
     * @param ruleId
     * @return
     */
    @RequestMapping("/queryVariablesByRuleId/flat")
    @ResponseBody
    public ResponseResult getVariableMapByRuleId(String ruleId, @RequestParam(defaultValue = "K1") String kindId) {
        List<Map<String, Object>> variableMapByRuleId = variableService.getVariableMapByRuleId(ruleId, kindId);
        return ResponseResult.createSuccessInfo("", variableMapByRuleId);
    }

    /**
     * 查询模型引用的所有输出非实体输入参数，包括公共参数、私有参数
     *
     * @param ruleId
     * @return
     */
    @RequestMapping("/queryOutVariablesByRuleId/flat")
    @ResponseBody
    public ResponseResult getOutVariableMapByRuleId(String ruleId) {
        List<Map<String, Object>> variableMapByRuleId = variableService.getOutVariableMapByRuleId(ruleId);
        return ResponseResult.createSuccessInfo("", variableMapByRuleId);
    }

    /**
     * 查询对象属性变量
     */
    @RequestMapping("/queryVariableRelation")
    @ResponseBody
    public Map<String, Object> queryVariableRelation(String entityId, HttpServletRequest request) {
        //每页显示10条数据
        String start = request.getParameter("start");
        String size = request.getParameter("length");
        //folderId 规则属组ID
        Map<String, Object> variableMap = variableService.selectVariableEntityRelation(entityId, start, size);
        return variableMap;
    }

    @RequestMapping("/insertVariableRelation")
    @ResponseBody
    public ResponseResult insertVariableRelation(Variable variable, VariableEntity variableEntity, VariableEntityRelation variableEntityRelation, HttpServletRequest request) {
        variable.setUpdateDate(new Date());
        variable.setUpdatePerson(ControllerUtil.getLoginUserId(request));
        ResponseResult result = variableService.insertVariableRelation(variable, variableEntity, variableEntityRelation);
        return result;
    }

    /*
     *	修改变量
     */
    @RequestMapping("/updateVariableRelation")
    @ResponseBody
    public ResponseResult updateVariableRelation(Variable variable, VariableEntity variableEntity, VariableEntityRelation variableEntityRelation, HttpServletRequest request) {
        variable.setUpdateDate(new Date());
        variable.setUpdatePerson(ControllerUtil.getLoginUserId(request));
        ResponseResult result = variableService.updateVariableRelation(variable, variableEntity, variableEntityRelation);
        return result;
    }

    /*
     *	删除变量
     */
    @RequestMapping("/deleteVariableRelation")
    @ResponseBody
    public ResponseResult deleteVariableRelation(String variableId, String entityId) {
        ResponseResult result = variableService.deleteVariableRelation(variableId, entityId);
        return result;
    }

    // ------------------------------ 公共参数 ------------------------------
    @RequestMapping("/checkUsed")
    @ResponseBody
    public ResponseResult checkUsed(String variableId) {
        return this.variableService.checkUsed(variableId);
    }


    // 获取参数类型列表
    @RequestMapping("/variableType/list")
    @ResponseBody
    public ResponseResult variableTypeList() {
        List<Map<String, Object>> variableTypeList = this.variableService.getVariableType();
        return ResponseResult.createSuccessInfo("success", variableTypeList);
    }

    @RequestMapping("/pub/list")
    @ResponseBody
    public Map<String, Object> pubSelectVariables(String variableAlias, String variableGroupName,
                                                  String variableTypeId,
                                                  String kindId,
                                                  String startDate, String endDate,
                                                  String start, String length) {
        return variableService.pubSelectVariables(variableAlias, variableGroupName,
                variableTypeId, kindId, startDate, endDate, start, length);
    }

    // 扁平化展示
    @RequestMapping("/pub/list/flat")
    @ResponseBody
    public Map<String, Object> pubSelectFlatVariables(String variableAlias,
                                                      String variableGroupName,
                                                      String variableTypeId, String kindId,
                                                      String startDate, String endDate,
                                                      String start, String length) {
        return variableService.pubSelectFlatVariables(variableAlias, variableGroupName,
                variableTypeId, kindId, startDate, endDate, start, length);
    }

    // 扁平化展示
    @RequestMapping("/pub/list/flat/list")
    @ResponseBody
    public ResponseResult pubSelectFlatVariables(String variableAlias,
                                                 String variableGroupName,
                                                 String variableTypeId,
                                                 String startDate, String endDate) {
        List<VariableGroupExt> variableGroupExts = variableService.pubSelectFlatVariables(variableAlias,
                variableGroupName, variableTypeId, startDate, endDate);
        return ResponseResult.createSuccessInfo("", variableGroupExts);
    }

    @RequestMapping("/pub/variablesByRule")
    @ResponseBody
    public ResponseResult pubVariablesByRule(String ruleId) {
        if (StringUtils.isBlank(ruleId)) {
            return ResponseResult.createFailInfo("参数[ruleId]不能为空");
        }
        return variableService.pubVariablesByRule(ruleId);
    }

    @RequestMapping("/pub/variablesByFolder")
    @ResponseBody
    public ResponseResult pubVariablesByFolder(String folderId) {
        if (StringUtils.isBlank(folderId)) {
            return ResponseResult.createFailInfo("参数[folderId]不能为空");
        }
        return variableService.pubVariablesByFolder(folderId);
    }

    @RequestMapping(value = "/pub/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult pubInsert(Variable variable, HttpServletRequest request) {
        if (variable == null) {
            return ResponseResult.createFailInfo("参数[variable]不能为空");
        }
        return variableService.pubInsert(variable, ControllerUtil.getLoginUserId(request));
    }

    /*@PermissionsRequires(value = "/pub/varaible/update?variable.variableId",
            resourceType = ResourceType.DATA_PUB_VARIABLE)*/
    @RequestMapping(value = "/pub/insertVariableRelation", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult pubInsertVariableRelation(Variable variable, VariableEntity variableEntity,
                                                    VariableEntityRelation variableEntityRelation,
                                                    HttpServletRequest request) {
        if (variable == null) {
            return ResponseResult.createFailInfo("参数[variable]不能为空");
        }
        if (variableEntity == null) {
            return ResponseResult.createFailInfo("参数[variableEntity]不能为空");
        }
        if (variableEntityRelation == null) {
            return ResponseResult.createFailInfo("参数[variableEntityRelation]不能为空");
        }
        return variableService.pubInsertVariableRelation(variable, variableEntity, variableEntityRelation,
                ControllerUtil.getLoginUserId(request));
    }

    /**
     * 将私有参数变为公有参数
     *
     * @param variable
     * @param variableGroupId
     * @param request
     * @return
     */
    @RequestMapping(value = "/upgrade", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult upgrade(Variable variable, String variableGroupId, HttpServletRequest request) {
        if (StringUtils.isBlank(variableGroupId)) {
            return ResponseResult.createFailInfo("参数[variableGroupId]不能为空");
        }

        final String currentUser = ControllerUtil.getLoginUserId(request);
        return variableService.upgrade(variable, variableGroupId, currentUser);
    }
    
    /**
     * 批量将私有参数变为公有参数
     * @param variable
     * @param variableGroupId
     * @param request
     * @return
     */
    @RequestMapping(value = "/batchUpgrade", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult batchUpgrade(Variable variable,HttpServletRequest request) {
    	String variableGroupId = variable.getFolderId();
        if (StringUtils.isBlank(variableGroupId)) {
            return ResponseResult.createFailInfo("参数[variableGroupId]不能为空");
        }

        final String currentUser = ControllerUtil.getLoginUserId(request);
        
        //批量设参数公有
        return variableService.batchUpgrade(variable, variableGroupId, currentUser);
    }

    @RequestMapping(value = "/pub/group/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult pubInsertVariableGroup(VariableGroup variableGroup, HttpServletRequest request) {
        if (variableGroup == null) {
            return ResponseResult.createFailInfo("参数[variableGroup]不能为空");
        }
        return variableService.pubInsertVariableGroup(variableGroup, ControllerUtil.getLoginUserId(request));
    }

    @RequestMapping(value = "/pub/group/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult pubUpdateVariableGroup(VariableGroup variableGroup, HttpServletRequest request) {
        return variableService.pubUpdateVariableGroup(variableGroup, ControllerUtil.getLoginUserId(request));
    }

    @RequestMapping(value = "/pub/group/delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult pubDeleteVariableGroup(String variableGroupId) {
        return variableService.pubDeleteVariableGroup(variableGroupId);
    }

    @RequestMapping(value = "/pub/group/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseResult pubVariableGroups(String variableGroupName) {

        return variableService.pubVariableGroups(variableGroupName);
    }

    @RequestMapping(value = "/pub/group/page/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> pubVariableGroupsPaged(String variableGroupName, String start, String length) {

        return variableService.pubVariableGroupsPaged(variableGroupName, start, length);
    }

    // 获取指定参数组id的所有变量
    @RequestMapping(value = "/pub/group/variable/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseResult pubSelectVariablesByGroupId(String variableGroupId) {
        if (variableGroupId == null) {
            return ResponseResult.createFailInfo("参数[variableGroupId]不能为空");
        }
        return variableService.pubSelectVariablesByGroupId(variableGroupId);
    }

    // ------------------------------- 权限校验 -------------------------------

    @PermissionsRequires(value = "/pub/variable/update?variableId", resourceType = ResourceType.DATA_PUB_VARIABLE)
    @RequestMapping(value = "/update/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult update(String variableId) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/pub/variable/delete?variableId", resourceType = ResourceType.DATA_PUB_VARIABLE)
    @RequestMapping(value = "/delete/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult delete(String variableId) {
        return ResponseResult.createSuccessInfo();
    }


}
