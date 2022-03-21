package com.bonc.frame.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 作者: jxw
 * @version 版本: 1.0
 * @date 创建时间: 2016年11月8日 下午2:35:35
 */
@Component
public class SpringUtils implements ApplicationContextAware {

    @Autowired
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static Object getBean(Class requiredType) {
        return applicationContext.getBean(requiredType);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
//        if(applicationContext == null){
//            applicationContext =
//        }
        return applicationContext.getBeansOfType(type);
    }
}

