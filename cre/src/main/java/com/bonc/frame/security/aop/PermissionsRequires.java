package com.bonc.frame.security.aop;

import com.bonc.frame.security.ResourceType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 资源访问的权限控制
 *
 * @author yedunyao
 * @date 2019/7/28 11:38
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionsRequires {

    String value() default "";

    Logical logical() default Logical.AND;

    ResourceType resourceType();

}
