package com.bonc.frame.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author jsj
 *
 */
@Controller
@RequestMapping("/")
public class passwordController {
	@RequestMapping("/modifyPassword")
	public String main() {
	    return "/pages/modifyPsd";
	}
	
	@ResponseBody
	@RequestMapping(value = "/modPsd", method = RequestMethod.POST)
    public String workbenchSubmit(HttpServletRequest request) throws Exception {
		System.out.println(request.getParameter("data"));
		return request.getParameter("data");
    }
}
