package com.bonc.frame.module.quartz;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

/**
 * @author yedunyao
 * @date 2019/5/28 9:41
 */
@Component("simpleAdaptableJobFactory")
public class SimpleAdaptableJobFactory extends SpringBeanJobFactory {

    @Autowired
    private AutowireCapableBeanFactory autowireCapableBeanFactory;

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object object = super.createJobInstance(bundle);
        autowireCapableBeanFactory.autowireBean(object);
        return object;
    }

}
