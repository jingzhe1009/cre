package com.bonc.frame.controller.rule;

import com.bonc.frame.service.rule.RuleService;
import com.bonc.frame.util.ControllerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ruleProp")
public class RulePropController {
	
	@Autowired
	private RuleService ruleService;

	@RequestMapping("/ruleType")
	@ResponseBody
	public List<Map<String, Object>> ruleType(){
		List<Map<String, Object>> ruleTypeList = this.ruleService.getRuleType();
		return ruleTypeList;
	}
	
	@RequestMapping("/ruleFolder")
	@ResponseBody
	public List<Map<String, Object>> ruleFolder(HttpServletRequest request){
		List<Map<String, Object>> ruleFolderList = this.ruleService.getRuleFolder();
		return ruleFolderList;
	}

}
