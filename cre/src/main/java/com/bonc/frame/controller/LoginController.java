package com.bonc.frame.controller;

import com.bonc.frame.config.ConstantFinal;
import com.bonc.frame.entity.user.UserAccountEn;
import com.bonc.frame.security.cache.token.TokenCacheService;
import com.bonc.frame.service.UserService;
import com.bonc.frame.service.auth.SubjectService;
import com.bonc.frame.service.syslog.SysLogService;
import com.bonc.frame.util.ConstantUtil;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.os.IPUtil;
import com.bonc.frame.util.MD5Util;
import com.bonc.frame.util.jwt.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *@author	作者：limf	
 *@date	创建时间：2018年1月31日 下午14:35:23
 *@version	版本： 1.0
 *说明：控制层
 */

@Controller
@RequestMapping("/")
public class LoginController {

    private static final Log log = LogFactory.getLog(LoginController.class);

	@Autowired
	private UserService userService;

	@Autowired
    private SysLogService sysLogService;

	@Autowired
	private SubjectService subjectService;

	@Autowired
    private TokenCacheService tokenCacheService;

	@RequestMapping("/index")
    public String index(@SessionAttribute("user") String account, Model model) {
        model.addAttribute("name", account);
        return "/index";
    }

    @RequestMapping("/tologin")
    public String login() {
        return "/login";
    }

    /**
     * 登录
     *
     * @param username
     * @param password
     * @param realUserAccount 真实用户工号，为业务团队对接规则引擎登录暂时增加的字段，用于后续调用接口时记录操作人
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/login")
    public @ResponseBody Map<String, Object> loginPost(String username, String password,
                                                       @Nullable String realUserAccount,
                                                       @Nullable String kpi,
                                                       HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Map<String, Object> map = new HashMap<>();
        if (username == null || "".equals(username = (username.trim()))) {
            response.sendRedirect("cre/tologin");
            return null;
        }
        if (password == null || "".equals(password = (password.trim()))) {
            response.sendRedirect("cre/tologin");
            return null;
        }

        final HttpSession session = request.getSession();

        final String ipAddr = IPUtil.getIpAddr(request);


        String md5_pwd = MD5Util.Bit32(password);
        UserAccountEn userAccountEn = userService.queryUserIdAndPassword(username, password);
        UserAccountEn md5_userAccountEn = userService.queryUserIdAndPassword(username, md5_pwd);
        if(userAccountEn == null && md5_userAccountEn == null){
            sysLogService.logSys(ConstantUtil.OPERATE_LOGIN, "用户名或密码错误", session);

            map.put("success", false);
            map.put("message", "用户名或密码错误！");
            return map;
        }

        // 设置session
        session.setAttribute(ConstantFinal.SESSION_KEY, username);
        session.setAttribute(ConstantFinal.SESSION_KEY_USER_ID, md5_userAccountEn.getUserId());
        session.setAttribute(ConstantFinal.SESSION_KEY_USER_NAME, md5_userAccountEn.getUserName());
        session.setAttribute(ConstantFinal.SESSION_KEY_USER_IP, ipAddr);

        List<String> subjects = subjectService.selectSubjectsByUserId(username);
        session.setAttribute(ConstantFinal.SESSION_KEY_SUBJECT_ID,subjects);
        log.debug("当前用户的主体-----"+ConstantFinal.SESSION_KEY_SUBJECT_ID+" : "+ subjects );
        // session中权限主体的放置更新时间
        session.setAttribute(ConstantFinal.SESSION_KEY_SUBJECT_TIMESTAMP, System.currentTimeMillis());

        // FIXME: 为业务团队对接设置的临时方案，需要为业务团队提供超级用户的用户名、密码，
        // FIXME: 使用realUserAccount接收真实用户工号，记日志时用session中的realUser（用户名$真实用户工号）记录日志
        StringBuilder realUserBuilder = new StringBuilder(username);
        if (StringUtils.isNotBlank(realUserAccount)) {
            realUserBuilder.append("$").append(realUserAccount);
        }
        final String realUser = realUserBuilder.toString();
//        session.setAttribute(ConstantFinal.REAL_USER, realUser);

        //设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //为获取当前系统时间
        String currentDate = df.format(new Date());
        String loginDate = null;
        if(userAccountEn !=null){
            loginDate = userAccountEn.getLoginDate();
        }else if(md5_userAccountEn != null){
            loginDate = md5_userAccountEn.getLoginDate();
        }

        session.setAttribute("loginDate", loginDate);
        //登陆日期入库
        userService.updateLoginDate(username, currentDate);

        sysLogService.logSys(ConstantUtil.OPERATE_LOGIN, "登录成功", session);

        // 生成token
        String jwtToken = JwtUtil.createJWT(username, realUser);

        // 记录有权限的指标列表
        if (StringUtils.isNotBlank(kpi)) {
            tokenCacheService.set(jwtToken, kpi);
        }

        map.put("success", true);
        map.put("message", "登录成功");
        map.put("token", jwtToken);

        response.addHeader("Authorization", jwtToken);
        return map;
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpSession session) throws Exception {
	    if (session == null) {
	        return "/login";
        }

        sysLogService.logSys(ConstantUtil.OPERATE_LOGOUT, "登出成功", session);

//    	String userName = (String) session.getAttribute("user");
        /*移除session*/
        session.removeAttribute(ConstantFinal.SESSION_KEY);
        session.removeAttribute(ConstantFinal.SESSION_KEY_USER_ID);
        session.removeAttribute(ConstantFinal.SESSION_KEY_USER_NAME);
        session.removeAttribute(ConstantFinal.SESSION_KEY_USER_IP);
        session.invalidate();//让session永远失效

        String token = ControllerUtil.getToken(request);

        if (StringUtils.isNotBlank(token)) {
            tokenCacheService.del(token);
        }

        return "/login";
    }
    
    @RequestMapping("/modifypassword")
    public @ResponseBody Map<String, Object> modifyPassword(String oldPsd, String newPsd, HttpSession session) throws Exception{
        Map<String, Object> map = new HashMap<String, Object>();
    	String  username = (String) session.getAttribute("user");
    	UserAccountEn userAccountEn = userService.queryUserIdAndPassword(username, oldPsd);
    	String md5_oldPwd = MD5Util.Bit32(oldPsd);
    	UserAccountEn md5_userAccountEn = userService.queryUserIdAndPassword(username, md5_oldPwd);
    	
    	if(userAccountEn == null && md5_userAccountEn == null){
    		map.put("success", false);
    		map.put("message", "当前密码输入错误");
    		return map;
    	}
    	
    	//加密密码（MD5加密）
    	String md5_newPwd  = MD5Util.Bit32(newPsd);
    	//加密密码入库
    	if(userAccountEn != null){
        	userService.updatePassword(username, oldPsd, md5_newPwd);
    	}else if(md5_userAccountEn != null){
    		userService.updatePassword(username, md5_oldPwd, md5_newPwd);
		}
    	
    	map.put("success", true);
        map.put("message", "密码修改成功");
    	return map;
    }
    
}

