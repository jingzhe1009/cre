package com.bonc.frame.ws.controller.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bonc.frame.util.ResponseResult;
import com.bonc.frame.ws.service.rule.WsRuleService;

/**
 * 规则相关webservice接口
 * @author qxl
 * @date 2018年4月17日 上午10:25:58
 * @version 1.0
 */
@Controller
@RequestMapping("/ws/test")
public class WsTestController {
	
	@Autowired
	private WsRuleService wsRuleService;
	
	@RequestMapping("/testHfw")
	@ResponseBody
	public Map<String,Object> testHfw(String phoneNumber,String cardNum) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("hfw_success", 1);
		List<String> allmsglist = new ArrayList<String>();
		allmsglist.add("犯罪及嫌疑人名单");
		allmsglist.add("执行工开信息");
		allmsglist.add("命中犯罪通缉名单");
		allmsglist.add("命中法院被执行人名单");
		allmsglist.add("命中限制高消费名单");
		map.put("hfw_allmsglist", allmsglist);
		return map;
	}

	@RequestMapping("/testSmz")
	@ResponseBody
	public Map<String,Object> testSmz(String phoneNumber,String cardNum,String code) {
		String result = "2060";
		if(code != null && !code.isEmpty()) {
			result = code;
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("smz_resCode", 2060);
		return map;
	}
	
	@RequestMapping("/testSmz1")
	@ResponseBody
	public String testSmz1(String phoneNumber,String cardNum,String code) {
		String result = "2060";
		if(code != null && !code.isEmpty()) {
			result = code;
		}
		return result;
	}
	
	@RequestMapping("/testYys")
	@ResponseBody
	public Map<String,Object> testYys(String phoneNumber,String cardNum) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("yys_resCode", "2062");
		map.put("yys_mobPhoOperator", 2);
		map.put("yys_inUseTime", 10);
		return map;
	}
	
}
