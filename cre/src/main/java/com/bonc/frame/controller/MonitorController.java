package com.bonc.frame.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bonc.frame.entity.monitor.DescResult;
import com.bonc.frame.entity.monitor.ExcuteResult;
import com.bonc.frame.service.monitor.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bonc.frame.entity.commonresource.ModelGroup;
import com.bonc.frame.entity.monitor.IndexParam;
import com.bonc.frame.entity.monitor.MonitorParam;
import com.bonc.frame.service.UserService;
import com.bonc.frame.service.auth.ChannelService;
import com.bonc.frame.service.modelBase.ModelBaseService;
import com.bonc.frame.service.rule.RuleDetailService;
import com.bonc.frame.service.rule.RuleService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.FolderMenuUtil;

@Controller
@RequestMapping("/monitor")
public class MonitorController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RuleService ruleService;
	
	@Autowired
    ChannelService channelService;
	
	@Autowired
	ModelBaseService modelGroupService;
	
	@Autowired
    private RuleDetailService ruleDetailService;

	@Autowired
	private MonitorService monitorService;
	
	@RequestMapping({"/","/indexView"})
	public String main(String idx, String childOpen, HttpSession session,Model model) throws Exception {
		FolderMenuUtil.setFolderMenu(ruleService, session);
		if(idx==null || idx.isEmpty()){
			idx = "28";
		}
		IndexParam param = new IndexParam();
		param.setChannelCount(childOpen);
		param.setCountModel(childOpen);
		param.setCurrentDayFailTimes(childOpen);
		param.setCurrentDayUseTime(childOpen);
		param.setCurrentDaySucTimes(childOpen);
		param.setKpiCount(childOpen);
		param.setProductCount(childOpen);
		param.setRuleModel(childOpen);
		param.setRuleSetCount(childOpen);
		param.setScoreModel(childOpen);
		model.addAttribute("idx", idx);
		model.addAttribute("childOpen", childOpen);
		model.addAttribute("param", param);
	    return "/pages/monitor/indexView";
	}

	//调用分析-模型执行情况
	@RequestMapping("/excuteState")
	@ResponseBody
	public Map<String, Object> excuteState(@RequestBody MonitorParam monitorParam) {
		Map<String, Object> map = monitorService.excuteState(monitorParam);
		return map;
	}

	//调用分析-响应时间
	@RequestMapping("/useTime")
	@ResponseBody
	public Map<String, Object> useTime(@RequestBody MonitorParam monitorParam) {
		Map<String, Object> useTime = monitorService.getUseTime(monitorParam);
		return useTime;
	}

	//调用分析-执行统计
	@RequestMapping("/excuteCount")
	@ResponseBody
	public Map<String, Object> excuteCount(@RequestBody MonitorParam monitorParam) {
		Map<String, Object> excuteCount = monitorService.getExcuteCount(monitorParam);
		return excuteCount;
	}

	//调用分析-执行结果
	@RequestMapping("/excuteResult")
	@ResponseBody
	public Map<String, Object> excuteResult(MonitorParam monitorParam) {
		Map<String, Object> excuteResult = monitorService.excuteResult(monitorParam);
		return excuteResult;
	}

	@RequestMapping(value="/logPage",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> logPage(MonitorParam monitorParam,String start,String length) {

		// List<ExcuteResult> resultList = getData();
		Map<String, Object> map = monitorService.getLogPage(monitorParam,start,length);
		return map;
	}

	//日志详情
	@RequestMapping(value = "/logDesc",method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> logDesc(String logId) {
		DescResult desc = monitorService.getRuleLogDesc(logId);

		Map<String, Object> map = new HashMap<>();
		map.put("desc", desc);
		return map;
	}

	//调用分析-模型规则命中率
	@RequestMapping("/ruleHit")
    @ResponseBody
    public Map<String, Object> ruleHit(@RequestBody MonitorParam monitorParam) {
		Map<String, Object> map = monitorService.ruleHit(monitorParam);
		return map;
	}

	//调用分析-模型评分命中率
	@RequestMapping("/scoreHit")
    @ResponseBody
    public Map<String, Object> scoreHit(@RequestBody MonitorParam monitorParam) {
		Map<String, Object> map = monitorService.scoreHit(monitorParam);
		return map;
	}
	
	//调用分析-数据下载
	@RequestMapping("/desc")
    @ResponseBody
    public Map<String, Object> desc(@RequestBody MonitorParam monitorParam) {
		Map<String, Object> map = monitorService.desc(monitorParam);
        return map;
	}
	
	@RequestMapping("/channelName")
    @ResponseBody
    public List<Map<String, Object>> getChannelNameList(HttpServletRequest request) {
        String groupId = ControllerUtil.getParam(request, "groupId");
        List<Map<String, Object>> ruleFolderList = this.channelService.getChannelListByGroupId(groupId);
        return ruleFolderList;
    }
    
    @RequestMapping("/modelGroupName")
    @ResponseBody
    public List<ModelGroup> getModelGroupList(HttpServletRequest request) {
        List<ModelGroup> ruleFolderList = this.modelGroupService.getModelGroupList();
        return ruleFolderList;
    }
    
    @RequestMapping("/modelName")
    @ResponseBody
    public List<Map<String, Object>> getModelList(HttpServletRequest request) {
        String groupId = ControllerUtil.getParam(request, "groupId");
        String channelId = ControllerUtil.getParam(request, "channelId");
        List<Map<String, Object>> ruleFolderList = this.ruleDetailService.getModelList(groupId, channelId);
        return ruleFolderList;
    }

	@RequestMapping(value="/exportExcel")
	public void exportExcel(HttpServletResponse response, HttpServletRequest request) {
		monitorService.exportExcel(response,request);
	}
}
