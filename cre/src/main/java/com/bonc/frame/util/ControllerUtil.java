package com.bonc.frame.util;

import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.config.ConstantFinal;
import com.bonc.frame.entity.user.UserAccountEn;
import com.bonc.frame.util.jwt.JwtUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 作者: jxw
 * @version 版本: 1.0
 * @date 创建时间: 2016-9-8 上午11:32:32
 */
public class ControllerUtil {
    static Log log = LogFactory.getLog(ControllerUtil.class);

    /**
     * 分页方法，调用自定义查询方法前调用此方法，就可以实现分页查询，支持多种数据库。
     * 使用方法：
     * 1.调用分页方法 ControllerUtil.doPage();
     * 2.List list = 调用用户自定义查询方法
     * 说明：配合datatable插件使用时，不用管start和length，若自己写的分页，需在前台传start和length到后台。
     * start为查询记录开始编号，length为每页数据条数。
     *
     * @param request
     */
    public static void doPage(HttpServletRequest request) {
        String start = request.getParameter("start");
        String rows = request.getParameter("length");
        int pageSize = Integer.parseInt((rows == null || "0".equals(rows)) ? "5" : rows);
        int currentPage = Integer.parseInt((start == null) ? "0" : start) / pageSize + 1;
        // System.out.println("####[doPage]currentPage--"+currentPage+",pageSize--"+pageSize);
        PageHelper.startPage(currentPage, pageSize);
    }

    public static <E> Map<String, Object> createGridResult(List<E> result) {
        PageInfo<E> page = new PageInfo<E>(result);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("total", page.getTotal());
        resultMap.put("data", page.getList());
        return resultMap;
    }

    /**
     * 可以将从数据库分页查询出来的结果封装成datatable可以识别的对象，再转成json，DT即可自动生成表格。
     * 使用方法：
     * 1.调用分页方法 ControllerUtil.doPage();
     * 2.List list = 调用用户自定义查询方法--此方法需返回List
     * 3.调用ControllerUtil.createDtResult(list)--传入第二步返回的List
     *
     * @param result 数据库查询结果，其中Map中的data为查询的数据，recordsTotal为记录总条数
     * @return
     */
    public static <E> Map<String, Object> createDtResult(List<E> result) {
        PageInfo<E> page = new PageInfo<E>(result);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("recordsTotal", page.getTotal());
        resultMap.put("recordsFiltered", page.getTotal());
        resultMap.put("data", page.getList());
        return resultMap;
    }

    /**
     * 获得登录用户的登录ID
     *
     * @param request
     * @return
     */
    public static String getLoginUserId(HttpServletRequest request) {
        String currentUserId = (String) request.getSession().getAttribute(ConstantFinal.SESSION_KEY_USER_ID);
        if (currentUserId == null) {
            String token = getToken(request);
            return getUserFromToken(token); // may be empty
        }
        return currentUserId;
    }

    public static String getLoginUserId() {
        return getLoginUserId(getCurrentRequest());
    }

    public static String getParam(HttpServletRequest request, String key) {
        String value = request.getParameter(key);
        if (value != null) {
            value = value.trim();
        }
        return value;
    }

    public static HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpSession getCurrentSession() {
        HttpServletRequest currentRequest = getCurrentRequest();
        if (currentRequest == null) {
            throw new IllegalStateException("获取当前线程中的请求对象失败");
        }
        return currentRequest.getSession();
    }

    public static String getCurrentUser() {
        return (String) getCurrentSession().getAttribute(ConstantFinal.SESSION_KEY);
    }

    public static String getToken() {
        return getToken(getCurrentRequest());
    }

    public static String getToken(HttpServletRequest currentRequest) {
        if (currentRequest == null) {
            throw new IllegalStateException("获取当前线程中的请求对象失败");
        }

        String token = currentRequest.getHeader("Authorization");
        // 如果存在token，并且token合法，则可以直接访问
        if (StringUtils.isBlank(token)) {
            Map<String, String[]> parameterMap = currentRequest.getParameterMap();
            log.trace("请求URI: " + currentRequest.getRequestURI());
            log.trace("请求中所有的参数: " + JSONObject.toJSONString(parameterMap));
            String[] tokens = parameterMap.get("token");
            if (tokens != null) {
                token = tokens[0];
            }
        }
        log.trace("请求中获取的最终token值:" + token);
        return token;
    }

    private static String getRealUserFromToken(String token) {
        String realUser = getRealUserAndUserFromToken(token);
        if (realUser.contains("$")) {
            return realUser.split("\\$")[1];
        }
        return "";
    }

    public static String getUserFromToken(String token) {
        String realUser = getRealUserAndUserFromToken(token);
        if (realUser.contains("$")) {
            return realUser.split("\\$")[0];
        }
        return realUser;
    }

    private static String getRealUserAndUserFromToken(String token) {
        log.debug("token解析:开始解析token:[" + token + "]");
        if (StringUtils.isNotBlank(token)) {
            if (JwtUtil.isVerify(token)) {
                Map<String, String> jwtInfo;
                try {
                    jwtInfo = JwtUtil.parseJWT(token);
                    log.debug("token解析:解析结果jwtInfo:" + jwtInfo);
                } catch (Exception e) {
                    throw new RuntimeException("token解析失败");
                }
                String realUser = jwtInfo.get(ConstantFinal.REAL_USER);
                log.debug("token解析:从解析结果中获取到的真实用户,realUser:" + realUser);
                return realUser;
            } else {
                throw new RuntimeException("token已过期");
            }
        }
        log.debug("token解析:真实用户为null");
        return "";
    }

    // 获取系统用户
    public static String getUserFromToken() {
        String token = getToken();
        return getUserFromToken(token);
    }

    // 获取业务用户（非本系统用户）
    public static String getRealUserAndUserAccount() {
        String token = getToken();
        return getRealUserAndUserFromToken(token);
    }

    public static String getRealUserAndUserAccount(HttpServletRequest currentRequest) {
        String token = getToken(currentRequest);
        log.debug("从请求中获取到的token值为, token:[" + token + "]");
        return getRealUserAndUserFromToken(token);
    }

    public static String getRealUserAccount() {
        String token = getToken();
        return getRealUserFromToken(token);
    }

    public static String getRealUserAccount(HttpServletRequest currentRequest) {
        String token = getToken(currentRequest);
        return getRealUserFromToken(token);
    }

}
