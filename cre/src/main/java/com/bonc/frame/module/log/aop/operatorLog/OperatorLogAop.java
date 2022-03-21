package com.bonc.frame.module.log.aop.operatorLog;

import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.config.ConstantFinal;
import com.bonc.frame.entity.syslog.SysLogDetails;
import com.bonc.frame.security.aop.PermissionsRequiresAop;
import com.bonc.frame.util.AopUtil;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.IdUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2019/11/27 15:48
 */
@Aspect
@Component
public class OperatorLogAop {
    private Log log = LogFactory.getLog(PermissionsRequiresAop.class);

    //    @AfterReturning(value = "@annotation(com.bonc.frame.module.log.aop.operatorLog.OperatorLog)", returning = "returnValue")
    @AfterReturning(value = "@within(org.springframework.stereotype.Service)", returning = "returnValue")
    public void writerOperatorLogFileWithNormal(JoinPoint joinPoint, Object returnValue) {
        if (log.isTraceEnabled()) {
            SysLogDetails sysLogDetails = paseSysLog(joinPoint, returnValue, null, "0");
            if (sysLogDetails != null) {
                log.trace(sysLogDetails);
            }
        }
//        return sysLogDetails;
    }

    //    @AfterThrowing(value = "@annotation(com.bonc.frame.module.log.aop.operatorLog.OperatorLog)", throwing = "ex")
    @Deprecated
    /**
     * 异常不需要打日志
     */
    public SysLogDetails writerOperatorLogFileWithException(JoinPoint joinPoint, Exception ex) {
        SysLogDetails sysLogDetails = paseSysLog(joinPoint, null, ex, "0");
        log.error(sysLogDetails);
        return sysLogDetails;
    }

    /**
     * @param isException 是否是异常  1 : 异常     0 : 正常
     */
    public SysLogDetails paseSysLog(JoinPoint joinPoint, Object returnValue, Exception ex, String isException) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final Method method = methodSignature.getMethod();
        // 类的全命名
        String clazzName = methodSignature.getDeclaringTypeName();
        //方法名
        String methodName = methodSignature.getName();

        // 获取真实用户和IP
        String realUser = null;
        String userId = null;
        String ip = null;
        try {
            HttpSession session = ControllerUtil.getCurrentSession();
            realUser = (String) session.getAttribute(ConstantFinal.SESSION_KEY_USER_NAME);
            if (realUser == null) {
                realUser = "admin";
            }
            userId = (String) session.getAttribute(ConstantFinal.SESSION_KEY_USER_ID);
            ip = (String) session.getAttribute(ConstantFinal.SESSION_KEY_USER_IP);
        } catch (Exception e) {
            return null;
        }

        Map<String, Object> parms = AopUtil.getParms(joinPoint, methodSignature);

        return SysLogDetails.buildSysLogDetails().setLogId(IdUtil.createId())
                .setUserId(userId).setUserName(realUser).setIp(ip)
                .setClazzName(clazzName).setMethodName(methodName)
                .setOperateTime(new Date())
                .setParameters(parms)
                .setReturnValue(JSONObject.toJSONString(returnValue))
                .setException(ex)
                .build();
    }

}
