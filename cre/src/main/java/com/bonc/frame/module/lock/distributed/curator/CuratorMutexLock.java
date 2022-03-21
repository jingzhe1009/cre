package com.bonc.frame.module.lock.distributed.curator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CuratorMutexLock {
    /**
     * 加锁的属性名 , 值为first,表示对这个方法加锁
     */
    String[] value() default {"first"};

    /**
     * 存到zookeeper中的值为
     * /cre/lockDataType/lockAlias[i]%属性值
     */
    String[] lockAlias() default {};

    // String oldObject() ;
    // String newObject() ;

    /**
     * 加锁条件
     */
    String addLockCondition() default "true";


    /**
     * 资源类型
     * /cre/lockDataType/lockAlias[i]%属性值
     */
    LockDataType lockDataType() default LockDataType.MODEL;
}
