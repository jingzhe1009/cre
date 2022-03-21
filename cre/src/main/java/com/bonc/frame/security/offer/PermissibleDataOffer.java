package com.bonc.frame.security.offer;

import com.bonc.frame.security.ResourceType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 北京银行风控平台 数据显示的权限控制
 *
 * @author yedunyao
 * @date 2020/10/9 18:23
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissibleDataOffer {

    /**
     * 过滤用的列名
     *
     * @return
     */
    @AliasFor("key")
    String value() default "";

    /**
     * 过滤用的列名
     *
     * @return
     */
    @AliasFor("value")
    String key() default "";

    /**
     * 是否需要分页
     *
     * @return
     */
    boolean isPageHelper() default false;

}
