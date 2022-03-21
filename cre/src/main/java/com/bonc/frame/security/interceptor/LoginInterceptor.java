package com.bonc.frame.security.interceptor;

import com.bonc.frame.config.ConstantFinal;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.jwt.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author 作者：limf
 * @version 版本： 1.0 说明：
 * @date 创建时间：2018年1月31日 下午5:32:42
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    private Log log = LogFactory.getLog(LoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();

        String token = ControllerUtil.getToken(request);

        if (StringUtils.isNotBlank(token)) {
            log.debug("开始校验token: " + token);
            if (JwtUtil.isVerify(token)) {
//                session.setAttribute(ConstantFinal.SESSION_KEY, ControllerUtil.getUserFromToken(token));
                return true;
            } else {
                log.error("校验token失败，token已过期");
                throw new RuntimeException("token已过期");
            }
        }

        if (session != null) {
            // 判断session中的信息是否为空，不为空则其他请求不拦截
            String user = (String) session.getAttribute(ConstantFinal.SESSION_KEY);
            if (StringUtils.isNotBlank(user)
                    || request.getRequestURI().contains("/login")
                    || request.getRequestURI().contains("/tologin")
                    || request.getRequestURI().contains("/ws/")
                    ) {

                return true;
            }
        }

        // 跳转登录
        String contentPath = request.getContextPath();
        String url = ConstantFinal.TOLOGIN_URL;

        response.sendRedirect(contentPath + url);
        return false;
    }

}
