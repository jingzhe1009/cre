package com.bonc.frame.module.log.aop.operatorLog;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 将改注解标注的方法的参数,返回值等写入到数据库
 *
 * @Author: wangzhengbao
 * @DATE: 2019/11/28 10:22
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
//@OperatorLog
public @interface OperatorLogDataPersistence {
    /**
     * 日志类型,    0:登录日志
     * 1:操作日志
     */
    @AliasFor("logType")
    String value() default "";

    @AliasFor("value")
    String logType() default "";


    @AliasFor("operatorType")
    String operatorType() default "";
}
