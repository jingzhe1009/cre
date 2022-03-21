package com.bonc.frame.module.quartz;

import com.bonc.frame.config.Config;
import org.quartz.Scheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.concurrent.Executors;


@Configuration
public class QuartzConfig {

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setTaskExecutor(Executors.newFixedThreadPool(Config.OFFLINE_TASK_SCHEDULE_POOL_SIZE));
//        factory.setJobFactory(simpleAdaptableJobFactory);
//        SimpleJobListener jobListener = new SimpleJobListener();
        //添加job监听器,或其它trigger监听器
//        factory.setGlobalJobListeners(jobListener);
//        factory.setGlobalTriggerListeners(new SimpleTriggerListener());
        return factory;
    }

    @Bean
    public Scheduler scheduler() {
        return schedulerFactoryBean().getScheduler();
    }

}
