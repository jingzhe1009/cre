package com.bonc.frame.module.log.aop.operatorLog;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 将改注解标注的方法的参数,返回值等写入到文件
 *
 * @Author: wangzhengbao
 * @DATE: 2019/11/27 10:45
 */
@Deprecated
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OperatorLog {
    /**
     * 注释
     */
    @AliasFor("comments")
    String value() default "";
}
