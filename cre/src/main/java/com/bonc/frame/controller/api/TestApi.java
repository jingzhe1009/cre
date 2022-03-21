package com.bonc.frame.controller.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/ws")
public class TestApi {
	
	@RequestMapping("/bigData")
	@ResponseBody
	public Map<String,Object> bigData(String data){
		System.out.println("bigData-------"+data);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resCode", "1");
		map.put("carInfo", "0");
		map.put("eduInfo", "0");
		return map;
	}
	@RequestMapping("/personalCredit")
	@ResponseBody
	public Map<String,Object> personalCredit(String personalCredit){
		System.out.println("personalCredit-------"+personalCredit);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resCode", "1");
		map.put("fiveClass", "1");
		map.put("overDay", "70");
		return map;
	}
	@RequestMapping("/creditTrial")
	@ResponseBody
	public Map<String,Object> creditTrial(String creditTrial){
		System.out.println("creditTrial-------"+creditTrial);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resCode", "1");
		map.put("businessType", "1");
		return map;
	}

}
