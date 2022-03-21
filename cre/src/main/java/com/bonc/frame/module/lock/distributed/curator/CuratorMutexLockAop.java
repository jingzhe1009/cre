package com.bonc.frame.module.lock.distributed.curator;

import com.bonc.frame.security.zk.CREInterProcessMutex;
import com.bonc.frame.util.ReflectException;
import com.bonc.frame.util.ReflectUtil;
import com.bonc.frame.util.SpringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


@Aspect
@Component
public class CuratorMutexLockAop {

    private Log log = LogFactory.getLog(CuratorMutexLockAop.class);

    @Autowired
    private LockInternalTransaction lockInternalTransaction;

    @Around("@annotation(com.bonc.frame.module.lock.distributed.curator.CuratorMutexLock)")
    public Object processLock(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Map LockData = needLock(proceedingJoinPoint);
        if (null != LockData) {
            CREInterProcessMutex creInterProcessMutex = null;
            try {
                //,args[0]默认第一个变量为新对象，必须这样传递
                creInterProcessMutex = new CREInterProcessMutex(LockData.get("lockDataType"),
                        LockData.get("lockDataProperty"));
            } catch (Exception e) {
                throw new RuntimeException("创建分布式锁失败", e);
            }
            creInterProcessMutex.getLock();
            Object result = null;
            try {
                result = lockInternalTransaction.addTransactToLockLogic(proceedingJoinPoint);
            } catch (Throwable e) {
                throw e;
            } finally {
                creInterProcessMutex.releaseLock();
                creInterProcessMutex.stop();
            }
            return result;
        } else {
            return lockInternalTransaction.addTransactToLockLogic(proceedingJoinPoint);
        }
    }

    private Map<String, Object> needLock(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        final Method method = methodSignature.getMethod();
        // 方法的参数值
        final Object[] args = proceedingJoinPoint.getArgs();
        // 获取注解
        final CuratorMutexLock annotation = method.getAnnotation(CuratorMutexLock.class);
        String addLockCondition = annotation.addLockCondition();
        Map<String, Object> result = null;
        final String[] lockPathPro = annotation.value();
        final String[] lockAlias = annotation.lockAlias();
        final String lockDataType = annotation.lockDataType().getType();
        boolean flag = false;
        if ("true".equals(addLockCondition)) {//直接加锁构建加锁数据
            flag = true;
        } else {
            // 方法的参数名列表
            String[] parameterNames = methodSignature.getParameterNames();
            ExpressionParser parser = new SpelExpressionParser();
            EvaluationContext evaluationContext = new StandardEvaluationContext();
            int paramSize = parameterNames.length;
            for (int i = 0; i < paramSize; i++) {
                evaluationContext.setVariable(parameterNames[i], args[i]);
            }

            if (addLockCondition.contains("needLock")) {//接口
                evaluationContext.setVariable("myService", SpringUtils.getBean(method.getDeclaringClass()));
            }
            String needLock = parser.parseExpression(addLockCondition, new TemplateParserContext()).getValue(evaluationContext, String.class);
            flag = parser.parseExpression(needLock).getValue(evaluationContext, Boolean.class);

        }
        if (flag) {
            result = new HashMap();
            result.put("lockDataType", lockDataType);
            result.put("lockDataProperty", parse2Lockey(lockPathPro, lockAlias, args[0]));
        }
        return result;
    }

    private String[] parse2Lockey(String[] properties, String[] lockAlias, Object newObject) {
        int propertySize = properties.length;
        String[] valueList = new String[propertySize];
        try {
            for (int i = 0; i < propertySize; i++) {
                String alias = getLockAlias(lockAlias, i);
                if ("first".equals(properties[i])) {
                    if (StringUtils.isNotBlank(alias)) {
                        valueList[i] = alias + "%" + properties[i];
                    } else {
                        valueList[i] = properties[i] + "%" + properties[i];
                    }
                } else {
                    if (StringUtils.isNotBlank(alias)) {
                        valueList[i] = alias + "%" + ReflectUtil.getFieldStringValue(newObject, properties[i]);
                    } else {
                        valueList[i] = properties[i] + "%" + ReflectUtil.getFieldStringValue(newObject, properties[i]);
                    }
                }

            }
            return valueList;
        } catch (ReflectException e) {
            log.error(e);
            throw new IllegalStateException("获取请求值失败");
        }
    }

    private String getLockAlias(String[] lockAlias, int i) {
        if (lockAlias != null && lockAlias.length > i) {
            return lockAlias[i];
        }
        return null;
    }


}
