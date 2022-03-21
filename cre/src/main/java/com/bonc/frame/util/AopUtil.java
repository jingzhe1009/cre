package com.bonc.frame.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import javax.servlet.ServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2019/12/3 12:14
 */
public class AopUtil {
    static Log log = LogFactory.getLog(AopUtil.class);

    /**
     * 获取传入方法的的参数,会排除 request,response等
     */
    public static Map<String, Object> getParms(JoinPoint joinPoint, MethodSignature methodSignature) {
        if (joinPoint == null) {
            log.warn("传入的JoinPoint:[" + joinPoint + "],为null");
            return new HashMap<>();
        }
        if (methodSignature == null) {
            methodSignature = (MethodSignature) joinPoint.getSignature();
        }
        // 方法的参数值
        final Object[] args = joinPoint.getArgs();
        // 方法的参数名列表
        final String[] parameterNames = methodSignature.getParameterNames();

        Map<String, Object> parms = new HashMap<>(parameterNames.length);

        for (int i = 0; i < parameterNames.length; i++) {
            if (args[i] != null && args[i] instanceof ServletRequest) {
                continue;
            }
            try {
                parms.put(parameterNames[i], JSONObject.toJSONString(args[i]));
            } catch (Exception ignored) {
                //这里的异常,一定是JSONObject.toJSONString的异常,并且这个异常是 request,response等,不能通过JSON转化为String所以会报错
                log.warn("key:[" + parameterNames[i] + "],value:[" + args[i] + "]试图放入参数值中失败");
            }
        }
        return parms;
    }
}
