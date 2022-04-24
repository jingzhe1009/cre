package com.bonc.frame.controller.api;

import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.api.ApiConfGroup;
import com.bonc.frame.entity.commonresource.ApiGroup;
import com.bonc.frame.entity.variable.VariableTreeNode;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissionsRequires;
import com.bonc.frame.service.api.ApiService;
import com.bonc.frame.service.syslog.SysLogService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.JsonUtils;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author qxl
 * @version 1.0
 * @date 2018年3月20日 上午10:06:28
 */
@Controller
@RequestMapping("/api")
public class ApiController {

    @Resource
    private ApiService apiService;

    @Autowired
    private VariableService variableService;

    @Autowired
    private SysLogService sysLogService;

    //接口管理主页面
    @RequestMapping("/index")
    public String index(String folderId, String childOpen, Model model) throws Exception {
        if (folderId == null) {
            throw new Exception("The folder id is null.");
        }
        model.addAttribute("folderId", folderId);
        model.addAttribute("idx", folderId);
        model.addAttribute("childOpen", childOpen);
        List<Map<String, Object>> baseVariableType = variableService.getVariableType();
        model.addAttribute("baseVariableType", JsonUtils.beanToJson(baseVariableType).toString());
        List<Map<String, Object>> entityType = variableService.getEntityType(folderId);
        model.addAttribute("entityType", JsonUtils.beanToJson(entityType).toString());
        List<Map<String, Object>> baseVariable = variableService.getBaseVariable(folderId);
        model.addAttribute("baseVariable", JsonUtils.beanToJson(baseVariable).toString());
        return "/pages/api/apiIndex";
    }

    //获取所有接口列表
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> getApiList(String apiName, String folderId, HttpServletRequest request) {
        String start = request.getParameter("start");
        String size = request.getParameter("length");
        Map<String, Object> result = apiService.getApiList(apiName, folderId, start, size);
        return result;
    }

    @RequestMapping("/variables")
    @ResponseBody
    public ResponseResult getVariablesByApiId(String apiId) {
        if (StringUtils.isBlank(apiId)) {
            return ResponseResult.createFailInfo("参数[apiId]不能为空");
        }
        return apiService.getVariablesByApiId(apiId);
    }

    //新建接口主页面
    @RequestMapping("/createApiIndex")
    public String createIndex(String folderId, String childOpen, Model model, HttpSession session) throws Exception {
        if (folderId == null) {
            throw new Exception("The folder id is null.");
        }

        model.addAttribute("folderId", folderId);
        List<Map<String, Object>> list = apiService.getAllApiType();
        model.addAttribute("apiType", JsonUtils.beanToJson(list));
        model.addAttribute("idx", folderId);
        model.addAttribute("childOpen", childOpen);
        List<Map<String, Object>> baseVariableType = variableService.getVariableType();
        model.addAttribute("baseVariableType", JsonUtils.beanToJson(baseVariableType).toString());
        List<Map<String, Object>> entityType = variableService.getEntityType(folderId);
        model.addAttribute("entityType", JsonUtils.beanToJson(entityType).toString());
        List<Map<String, Object>> baseVariable = variableService.getBaseVariable(folderId);
        model.addAttribute("baseVariable", JsonUtils.beanToJson(baseVariable).toString());
        return "/pages/api/createApi";
    }

    //获取所有的api接口类型
    @RequestMapping("/apiType")
    @ResponseBody
    public List<Map<String, Object>> getAllApiType() {
        List<Map<String, Object>> list = apiService.getAllApiType();
        return list;
    }

    //保存新建的接口
    @RequestMapping("/insertApi")
    @ResponseBody
    public ResponseResult insertApi(ApiConf apiConf, HttpServletRequest request) {
        apiConf.setCreateDate(new Date());
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        apiConf.setCreatePersion(loginUserId);
        ResponseResult result = apiService.insertApi(apiConf, loginUserId);


        return result;
    }

    //修改接口
    @RequestMapping("/updateApi")
    @ResponseBody
    public ResponseResult updateApi(ApiConf apiConf, HttpServletRequest request) {
        if (apiConf == null) {
            return ResponseResult.createFailInfo("参数[apiConf]不能为空");
        }
        if (apiConf.getApiId() == null) {
            return ResponseResult.createFailInfo("参数[apiId]不能为空");
        }
        apiConf.setUpdateDate(new Date());
        apiConf.setUpdatePersion(ControllerUtil.getLoginUserId(request));
        ResponseResult result = apiService.updateApi(apiConf);
        return result;
    }

    //删除接口
    @RequestMapping("/deleteApi")
    @ResponseBody
    public ResponseResult deleteApi(String apiId, String folderId) {
        ResponseResult result = apiService.deleteApi(apiId, folderId);
        return result;
    }

    @RequestMapping("/checkUsed")
    @ResponseBody
    public ResponseResult checkUsed(String apiId) {
        return apiService.checkUsed(apiId);
    }

    // ------------------------------ 公共接口 ------------------------------

    /**
     * 分页获取Api
     */
    @RequestMapping("/pub/list")
    @ResponseBody
    public Map<String, Object> pubGetApiList(String apiName, String apiGroupName,
                                             String startDate, String endDate,
                                             String start, String length) {
        return apiService.pubGetApiList(apiName, apiGroupName,
                startDate, endDate, start, length);
    }

    /**
     * 获取所有的Api
     */
    @RequestMapping("/pub/listAll")
    @ResponseBody
    public ResponseResult pubGetApiListAll(String apiName, String apiGroupName,
                                           String startDate, String endDate) {

        List<ApiConfGroup> results = apiService.pubGetApiListAll(apiName, apiGroupName,
                startDate, endDate);
        return ResponseResult.createSuccessInfo("", results);
    }

    @RequestMapping(value = "/pub/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult pubInsert(ApiConf apiConf, HttpServletRequest request) {
        if (apiConf == null) {
            return ResponseResult.createFailInfo("参数[apiConf]不能为空");
        }

        final ResponseResult responseResult = apiService.pubInsert(apiConf, ControllerUtil.getLoginUserId(request));
        return responseResult;
    }

    @PermissionsRequires(value = "/pub/api/update?apiConf.apiId", resourceType = ResourceType.DATA_PUB_API)
    @RequestMapping(value = "/pub/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult pubUpdateApi(ApiConf apiConf, HttpServletRequest request) {
        if (apiConf == null) {
            return ResponseResult.createFailInfo("参数[apiConf]不能为空");
        }
        if (apiConf.getApiId() == null) {
            return ResponseResult.createFailInfo("参数[apiId]不能为空");
        }

        final ResponseResult responseResult = apiService.pubUpdateApi(apiConf, ControllerUtil.getLoginUserId(request));
        return responseResult;
    }

    @PermissionsRequires(value = "/pub/api/delete?apiId", resourceType = ResourceType.DATA_PUB_API)
    @RequestMapping(value = "/pub/delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult pubDeleteApi(String apiId) {
        final ResponseResult responseResult = apiService.pubDeleteApi(apiId);
        return responseResult;
    }

    @RequestMapping(value = "/getApiValueTree")
    @ResponseBody
    public ResponseResult getApiReturnVariableTree(String apiId, String apiValueType) {
        //getApiReturnVariableTree
        List<VariableTreeNode> apiReturnVariableCodeTree = variableService.getApiReturnVariableTree(apiId, apiValueType);
        return ResponseResult.createSuccessInfo("", apiReturnVariableCodeTree);
    }

    /**
     * 私有接口变为公有接口
     * <p>
     * 当接口引用的变量中存在私有变量则拒绝升级
     *
     * @param apiId
     * @param apiGroupId
     * @param request
     * @return
     */
    @RequestMapping(value = "/upgrade", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult upgrade(String apiId, String apiGroupId, HttpServletRequest request) {
        if (StringUtils.isBlank(apiId)) {
            return ResponseResult.createFailInfo("参数[apiId]不能为空");
        }
        if (StringUtils.isBlank(apiGroupId)) {
            return ResponseResult.createFailInfo("参数[apiGroupId]不能为空");
        }

        final String currentUser = ControllerUtil.getLoginUserId(request);
        return apiService.upgrade(apiId, apiGroupId, currentUser);
    }

    @RequestMapping(value = "/pub/group/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult pubInsertApiGroup(ApiGroup apiGroup, HttpServletRequest request) {
        if (apiGroup == null) {
            return ResponseResult.createFailInfo("参数[apiGroup]不能为空");
        }
        return apiService.pubInsertApiGroup(apiGroup, ControllerUtil.getLoginUserId(request));
    }

    @PermissionsRequires(value = "/pub/apiGroup/update?apiGroupId", resourceType = ResourceType.DATA_PUB_API_GROUP)
    @RequestMapping(value = "/group/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult pubUpdateApiGroup(ApiGroup apiGroup, HttpServletRequest request) {
        if (apiGroup == null) {
            return ResponseResult.createFailInfo("参数[apiGroup]不能为空");
        }
        return apiService.pubUpdateApiGroup(apiGroup, ControllerUtil.getLoginUserId(request));
    }

    @PermissionsRequires(value = "/pub/apiGroup/delete?apiGroupId", resourceType = ResourceType.DATA_PUB_API_GROUP)
    @RequestMapping(value = "/group/delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult pubDeleteApiGroup(String apiGroupId) {
        return apiService.pubDeleteApiGroup(apiGroupId);
    }

    @RequestMapping(value = "/pub/group/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseResult pubApiGroups(String apiGroupName) {

        return apiService.pubApiGroups(apiGroupName);
    }

    @RequestMapping(value = "/pub/group/page/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> pubApiGroupsPaged(String apiGroupName, String start, String length) {

        return apiService.pubApiGroupsPaged(apiGroupName, start, length);
    }

    // 获取指定接口类型id的所有接口
    @RequestMapping(value = "/pub/group/api/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseResult pubSelectApisByGroupId(String apiGroupId) {
        if (apiGroupId == null) {
            return ResponseResult.createFailInfo("参数[apiGroupId]不能为空");
        }
        return apiService.pubSelectApisByGroupId(apiGroupId);
    }

    // ------------------------------- 权限校验 -------------------------------

    @PermissionsRequires(value = "/pub/api/update?apiId", resourceType = ResourceType.DATA_PUB_API)
    @RequestMapping(value = "/update/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult update(String apiId) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/pub/api/delete?apiId", resourceType = ResourceType.DATA_PUB_API)
    @RequestMapping(value = "/delete/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult delete(String apiId) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/pub/apiGroup/update?apiGroupId", resourceType = ResourceType.DATA_PUB_API_GROUP)
    @RequestMapping(value = "/group/update/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult updateGroup(String apiGroupId) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/pub/apiGroup/delete?apiGroupId", resourceType = ResourceType.DATA_PUB_API_GROUP)
    @RequestMapping(value = "/group/delete/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult deleteGroup(String apiGroupId){
        return ResponseResult.createSuccessInfo();
    }
}
