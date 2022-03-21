package com.bonc.frame.util;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.bonc.frame.config.ConstantFinal;
import com.bonc.frame.service.rule.RuleService;

/**
 * 
 * @author qxl
 * @date 2018年3月29日 下午5:49:54
 * @version 1.0
 */
public class FolderMenuUtil {
	
	public static void setFolderMenu(RuleService ruleService,HttpSession session) {
		String currentUserId =  (String) session.getAttribute(ConstantFinal.SESSION_KEY_USER_ID);
		//设置菜单session
        List<Map<String, Object>> ruleFolderList = ruleService.getRuleFolder();
        session.setAttribute(ConstantUtil.SESSION_KEY_FOLDERMENU, ruleFolderList);
//        session.setMaxInactiveInterval(24*3600);//session过期时间单位秒
	}

}
