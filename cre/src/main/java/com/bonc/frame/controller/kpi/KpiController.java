package com.bonc.frame.controller.kpi;

import com.bonc.frame.config.Config;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.kpi.KpiGroup;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissionsRequires;
import com.bonc.frame.service.kpi.KpiService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.JsonUtils;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 指标库
 *
 * @author yedunyao
 * @date 2019/10/14 11:54
 */
@Controller
@RequestMapping("/kpi")
public class KpiController {

    @Autowired
    private KpiService kpiService;

    @RequestMapping("/view")
    public String view(String idx, String childOpen, Model model, HttpServletRequest request) {
        model.addAttribute("idx", idx);//菜单状态标识
        model.addAttribute("childOpen", childOpen);

        List<Map<String, Object>> kpiTypeList = kpiService.getKpiType();
        model.addAttribute("kpiTypeList", JsonUtils.beanToJson(kpiTypeList).toString());

        return "/pages/kpi/kpiIndex";
    }

    // ------------------------ 指标管理 ------------------------
    @RequestMapping("/check/kpiUsed")
    @ResponseBody
    public ResponseResult checkKpiUsed(String kpiId) {
        if (!Config.CRE_REFERENCE_CHECK_ENABLE) {
            return ResponseResult.createSuccessInfo("");
        }
        if (kpiService.isKpiUsed(kpiId)) {
            return ResponseResult.createFailInfo("指标正在使用中");
        }
        return ResponseResult.createSuccessInfo("");
    }

    @RequestMapping("/getKpiType")
    @ResponseBody
    public ResponseResult getKpiType() {
        List<Map<String, Object>> kpiTypeList = kpiService.getKpiType();
        return ResponseResult.createSuccessInfo("", kpiTypeList);
    }
    //指标下查看关联规则集组信息
    @RequestMapping("/getRuleSetGroupByKpiId")
    @ResponseBody
    public List<Object> getRuleSetGroupByKpiId(String KpiId) {
        List<Object> List = kpiService.getRuleSetGroupByKpiId(KpiId);
        return List;
    }

    @RequestMapping("/check/kpiName")
    @ResponseBody
    public ResponseResult checkNameIsExist(String kpiName, @Nullable String kpiHeaderId) {
        if (!Config.CRE_REFERENCE_CHECK_ENABLE) {
            return ResponseResult.createSuccessInfo("");
        }
        if (StringUtils.isBlank(kpiName)) {
            return ResponseResult.createFailInfo("请求参数[kpiName]不能为空");
        }
        if (kpiService.checkNameIsExist(kpiName, kpiHeaderId)) {
            return ResponseResult.createFailInfo("指标名称已存在,指标名称不能于参数名重复");
        }
        return ResponseResult.createSuccessInfo();
    }

    @RequestMapping(value = "/list")
    @ResponseBody
    public ResponseResult getKpiBaseInfo(@Nullable String kpiName,
                                         @Nullable String kpiGroupName,
                                         @Nullable String kpiType,
                                         @Nullable String fetchType,
                                         @Nullable String startDate,
                                         @Nullable String endDate) {
        return kpiService.getKpiBaseInfo(kpiName, kpiGroupName, kpiType, fetchType, startDate, endDate);
    }

    @RequestMapping(value = "/paged")
    @ResponseBody
    public Map<String, Object> getKpiBaseInfo(@Nullable String kpiName,
                                              @Nullable String kpiGroupName,
                                              @Nullable String kpiType,
                                              @Nullable String fetchType,
                                              @Nullable String startDate,
                                              @Nullable String endDate,
                                              String start, String length) {
        return kpiService.getKpiBaseInfo(kpiName, kpiGroupName, kpiType, fetchType, startDate, endDate, start, length);
    }

    @RequestMapping(value = "/detail", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseResult getDetail(String kpiId) {
        return kpiService.getDetail(kpiId);
    }


    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult createKpiDefinition(@RequestBody KpiDefinition kpiDefinition,
                                              HttpServletRequest request) {
        String loginUserId = ControllerUtil.getLoginUserId(request);
        return kpiService.createKpiDefinition(kpiDefinition, loginUserId);
    }

    @PermissionsRequires(value = "/kpi/update?kpiDefinition.kpiId", resourceType = ResourceType.DATA_KPI)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult updateKpiDefinition(@RequestBody KpiDefinition kpiDefinition,
                                              HttpServletRequest request) {
        String loginUserId = ControllerUtil.getLoginUserId(request);
        return kpiService.updateKpiDefinition(kpiDefinition, loginUserId);
    }

    @RequestMapping(value = "/update/offer", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult updateKpiDefinitionOffer(KpiDefinition kpiDefinition,
                                                   HttpServletRequest request) {
        String loginUserId = ControllerUtil.getLoginUserId(request);
        return kpiService.updateKpiDefinitionOffer(kpiDefinition, loginUserId);
    }


    @PermissionsRequires(value = "/kpi/delete?kpiId", resourceType = ResourceType.DATA_KPI)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult deleteKpiDefinition(String kpiId) {
        return kpiService.deleteKpiDefinition(kpiId);
    }

    // ------------------------------- 权限校验 -------------------------------

    @PermissionsRequires(value = "/kpi/update?kpiId", resourceType = ResourceType.DATA_KPI)
    @RequestMapping(value = "/update/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult update(String kpiId) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/kpi/delete?kpiId", resourceType = ResourceType.DATA_KPI)
    @RequestMapping(value = "/delete/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult delete(String kpiId) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/kpi/group/update?kpiGroupId", resourceType = ResourceType.DATA_KPI_GROUP)
    @RequestMapping(value = "/group/update/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult updateGroup(String kpiGroupId) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/kpi/group/delete?kpiGroupId", resourceType = ResourceType.DATA_KPI_GROUP)
    @RequestMapping(value = "/group/delete/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult deleteGroup(String kpiGroupId) {
        return ResponseResult.createSuccessInfo();
    }


    // ------------------------ 指标组管理 ------------------------

    @RequestMapping(value = "/group/list")
    @ResponseBody
    public ResponseResult getKpiGroups(String kpiGroupName) {
        return kpiService.getKpiGroups(kpiGroupName);
    }

    @RequestMapping(value = "/group/paged")
    @ResponseBody
    public Map<String, Object> getKpiGroupsPaged(String kpiGroupName,
                                                 String start, String length) {
        return kpiService.getKpiGroupsPaged(kpiGroupName, start, length);
    }

    @RequestMapping(value = "/group/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult createKpiGroup(KpiGroup kpiGroup, HttpServletRequest request) {
        String loginUserId = ControllerUtil.getLoginUserId(request);
        return kpiService.createKpiGroup(kpiGroup, loginUserId);
    }
    @PermissionsRequires(value = "/kpi/group/update?kpiGroupId", resourceType = ResourceType.DATA_KPI_GROUP)
    @RequestMapping(value = "/group/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult updateKpiGroup(KpiGroup kpiGroup, HttpServletRequest request) {
        String loginUserId = ControllerUtil.getLoginUserId(request);
        return kpiService.updateKpiGroup(kpiGroup, loginUserId);
    }
    @PermissionsRequires(value = "/kpi/group/delete?kpiGroupId", resourceType = ResourceType.DATA_KPI_GROUP)
    @RequestMapping(value = "/group/delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult deleteKpiGroup(String kpiGroupId) {
        return kpiService.deleteKpiGroup(kpiGroupId);
    }


}
