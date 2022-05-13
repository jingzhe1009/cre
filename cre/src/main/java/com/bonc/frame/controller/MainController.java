package com.bonc.frame.controller;

import com.bonc.frame.entity.monitor.IndexParam;
import com.bonc.frame.entity.rulelog.RuleLog;
import com.bonc.frame.entity.rulelog.RuleLogDetail;
import com.bonc.frame.entity.user.UserAccountEn;
import com.bonc.frame.service.UserService;
import com.bonc.frame.service.rule.RuleService;
import com.bonc.frame.service.workbench.WorkbenchService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.FolderMenuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * @author jsj
 *
 */
@Controller
@RequestMapping("/")
public class MainController {
	@Autowired
	private RuleService ruleService;
	
	private Map<String, Object> map = new HashMap<String, Object>();
	@Autowired
	private UserService userService;
	@Autowired
	private WorkbenchService workbenchService;
	
	@RequestMapping("/main")
	public String main(String idx, String childOpen, HttpSession session,Model model) throws Exception {
		FolderMenuUtil.setFolderMenu(ruleService, session);
		if(idx==null || idx.isEmpty()){
			idx = "28";
		}
		childOpen = "c";
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

	@RequestMapping("/form")
	public String form() {
	    return "/pages/form";
	}
	@RequestMapping("/table")
	public String table() {
	    return "/pages/table";
	}
	@RequestMapping("/tree")
	public String tree() {
	    return "/pages/tree";
	}
	@RequestMapping("/workbench")
	public String workbench() {
	    return "/pages/workbench";
	}
	
	@ResponseBody
	@RequestMapping(value = "/workbenchSubmit", method = RequestMethod.POST)
    public String workbenchSubmit(HttpServletRequest request) throws Exception {
		System.out.println(request.getParameter("data"));
		return request.getParameter("data");
    }
	
	@RequestMapping("/queryInfo")
	public @ResponseBody Map<String, Object> queryInfo(HttpSession session) throws Exception{
		String username = (String) session.getAttribute("user");
		UserAccountEn userAccountEn = userService.queryInfoByUsername(username);
		
		if(userAccountEn == null){
			map.put("success", false);
    		map.put("message", "查询用户信息失败!");
    		return map;
		}
		//loginId
		map.put("jobNumber", userAccountEn.getJobNumber());
//		map.put("userRole", userAccountEn.getUserRole());
		map.put("phoneNumber", userAccountEn.getPhoneNumber());
		map.put("email", userAccountEn.getEmail());
//		map.put("org", userAccountEn.getOrg());
		map.put("success", true);
		
		return map;
	}
	@RequestMapping("/ruleExecuteNum")
	@ResponseBody
	public Map<String, Object> ruleExecuteNum(int day){
		List<Map<String, Object>> allList = this.workbenchService.ruleExecuteAll(day);
		List<Map<String, Object>> failList = this.workbenchService.ruleExecuteAllFail(day);
		List<Map<String, Object>> successList = this.workbenchService.ruleExecuteAllSuccess(day);
		List<Map<String, Object>> hitRateList = this.workbenchService.ruleHitRate(day);
//		List<Map<String, Object>> folderRuleList = this.workbenchService.folderRuleNum(day);
//		List<String> folderRuleListLegend = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		map.put("allData", getDayDataList(allList,day));
		map.put("failData", getDayDataList(failList,day));
		map.put("successData", getDayDataList(successList,day));
		map.put("hitRateData", hitRateList);
//		for(Map<String, Object> param:folderRuleList){
//			folderRuleListLegend.add((String)param.get("name"));
//		}
//		map.put("folderRuleDataLegend", folderRuleListLegend);
//		map.put("folderRuleData", folderRuleList);
		return map;
	}


	/**
	 * 
	 * 查询规则包下的规则执行条数
	 * */
	@RequestMapping("/ruleExecuteNumByFolder")
	@ResponseBody
	public Map<String, Object> ruleExecuteNumByFolder(String folderId,int day){
		List<Map<String, Object>> allList = this.workbenchService.ruleExecuteAllByFolder(folderId, day);
		Map<String, Object> map = new HashMap<>();
		List<String> allRuleListLegend = new ArrayList<>();
		for(Map<String, Object> param:allList){
			allRuleListLegend.add((String)param.get("name"));
		}
		map.put("allRuleDataLegend", allRuleListLegend);
		map.put("allRuleData", allList);
		
		List<Map<String, Object>> folderRuleList = this.workbenchService.folderRuleNum(day);
		List<String> folderRuleListLegend = new ArrayList<>();
		for(Map<String, Object> param:folderRuleList){
			folderRuleListLegend.add((String)param.get("name"));
		}
		map.put("folderRuleDataLegend", folderRuleListLegend);
		map.put("folderRuleData", folderRuleList);
		
		return map;
	}
	
	@RequestMapping("/queryExecuteRule")
	@ResponseBody
	public Map<String, Object> queryExecuteRule(RuleLog ruleLog,HttpServletRequest request){
		//每页显示10条数据
		String start = request.getParameter("start");
		String size = request.getParameter("length");
		Map<String,Object> executeRuleMap = this.workbenchService.queryExecuteRule(ruleLog, start, size);
		return executeRuleMap;
	}

	/**
	 * 前几天的list如果为空那么填0
	 * 
	 * */
	
	public List<String> getDayDataList(List<Map<String, Object>> list,int day){
		List<String> result = new ArrayList<>();
		for(int i=day-1;i>=0;i--){
			boolean flag = false;
			for(Map<String, Object> param:list){
				if(getDelayDay(-i).equals(param.get("time"))){
					result.add(String.valueOf(param.get("num")));
					flag = true;
					break;
				}
			}
			if(!flag){
				result.add("0");
			}
		}
		return result;
	}
	
	@RequestMapping("/ruleFolderList")
	@ResponseBody
	public List<Map<String, Object>> ruleFolderList(HttpServletRequest request){
		List<Map<String, Object>> ruleFolderList = this.ruleService.getRuleFolder();
		return ruleFolderList;
	}
	
	/**
	 * 根据logId查询规则详情
	 * */
	@RequestMapping("/ruleDetailList")
	@ResponseBody
	public List<RuleLogDetail> ruleDetailList(String logId,HttpServletRequest request){
		List<RuleLogDetail> ruleFolderList = this.workbenchService.ruleDetailList(logId);
		return ruleFolderList;
	}
	
	/**
	 * 获取前几天的日期,返回字符串
	 * 
	 * */
	public String getDelayDay(int dayDelay){
		Date date = new Date();//取时间
	    Calendar calendar  =   Calendar.getInstance();
	 
	    calendar.setTime(date); //需要将date数据转移到Calender对象中操作
	    calendar.add(calendar.DATE, dayDelay);//把日期往后增加n天.正数往后推,负数往前移动
	    date=calendar.getTime();   //这个时间就是日期往后推一天的结果
	    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	    
  	    return df.format(date).toString();
	}
	
}
