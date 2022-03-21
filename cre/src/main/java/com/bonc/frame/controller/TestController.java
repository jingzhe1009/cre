package com.bonc.frame.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/test")
public class TestController {
	
	@RequestMapping("/setSession")
	@ResponseBody
	public Map<String, Object> setSession(HttpServletRequest request){
		Map<String, Object> map = new HashMap<>();
		request.getSession().setAttribute("test", request.getRequestURL());
		map.put("test", request.getRequestURL());
		return map;
	}
	
	@RequestMapping(value="/getSessionId")
	@ResponseBody
	public String getSessionId(HttpServletRequest request){
		Object o = request.getSession().getAttribute("springboot");
		if(o == null){
			o = "spring boot 牛逼了!!!有端口"+request.getLocalPort()+"生成";
			request.getSession().setAttribute("springboot", o);
		}
		
		InetAddress address= null;
		try {
			address = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}//获取的是本地的IP地址 //PC-20140317PXKX/192.168.0.121
		String hostAddress = address.getHostAddress();
		
		return "hostAddress:"+hostAddress+"</br>端口=" + request.getLocalPort() +  " sessionId=" + request.getSession().getId() +"<br/>"+o;
	}

}
