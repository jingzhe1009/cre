package com.bonc.frame.module.log.aop.operatorLog;

import com.bonc.frame.config.ConstantFinal;
import com.bonc.frame.entity.syslog.SysLog;
import com.bonc.frame.service.syslog.SysLogService;
import com.bonc.frame.util.AopUtil;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.IdUtil;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于记录操作日志,
 *      注: 对没有登录的操作暂时不可用
 * @Author: wangzhengbao
 * @DATE: 2019/11/28 10:31
 */
@Aspect
@Component
public class OperatorLogDataPersistenceAOP {
    Log log = LogFactory.getLog(OperatorLogDataPersistenceAOP.class);

    @Autowired
    private SysLogService sysLogService;

    @AfterReturning(value = "@annotation(com.bonc.frame.module.log.aop.operatorLog.OperatorLogDataPersistence)", returning = "returnValue")
    public void dataPersistenceOperatorLogFile(JoinPoint joinPoint, Object returnValue) {
        SysLog sysLog = paseSysLog(joinPoint, returnValue);
        if (sysLog != null) {
            sysLogService.insert(sysLog);
        }
    }


    public SysLog paseSysLog(JoinPoint joinPoint, Object returnValue) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final Method method = methodSignature.getMethod();
        // 获取注解
        final OperatorLogDataPersistence annotation = method.getAnnotation(OperatorLogDataPersistence.class);
        if (annotation == null) {
            return null;
        }
        String logType = annotation.logType();
        String operatorType = annotation.operatorType();

        // TODO :类型不等于登录登出
        Map<String, Object> parms = AopUtil.getParms(joinPoint, methodSignature);
        if (parms == null) {
            parms = new HashMap<>(0);
        }
        //TODO: operateResult
        String operateResult = null;
        if (returnValue instanceof ResponseResult) {
            ResponseResult responseResult = (ResponseResult) returnValue;
            int status = responseResult.getStatus();
            if (status == ResponseResult.SUCCESS_STAUS) {
                operateResult = operatorType + "成功," + responseResult.getMsg();
            } else {
                operateResult = operatorType + "失败," + responseResult.getMsg();
            }
        }


        // 获取真实用户和IP
        String realUser = null;
        String userId = null;
        String ip = null;
        try {
            HttpSession session = ControllerUtil.getCurrentSession();
            if ("1".equals(logType)) {
                realUser = (String) session.getAttribute(ConstantFinal.SESSION_KEY_USER_NAME);
            } else {
                realUser = String.valueOf(parms.get("username"));
            }
            userId = (String) session.getAttribute(ConstantFinal.SESSION_KEY_USER_ID);
            ip = (String) session.getAttribute(ConstantFinal.SESSION_KEY_USER_IP);
        } catch (Exception e) {
            log.warn("写入日志,获取当前用户和ip失败,日志ID:");
            return null;
        }

        return SysLog.buildSysLog().setLogId(IdUtil.createId())
                .setUserId(userId).setUserName(realUser).setIp(ip)
                .setLogType(logType).setOperateType(operatorType)
                .setOperateInfo(parms.toString())
                .setOperateResult(operateResult)
                .setOperateTime(new Date())
                .build();
    }

}
