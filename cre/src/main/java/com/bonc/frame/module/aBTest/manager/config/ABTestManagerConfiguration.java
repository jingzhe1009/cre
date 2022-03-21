package com.bonc.frame.module.aBTest.manager.config;

import com.bonc.frame.module.aBTest.manager.ABTestManager;
import com.bonc.frame.module.aBTest.manager.ABTestManagerImpl;
import com.bonc.frame.module.aBTest.manager.ABTestManagerStandby;
import com.bonc.frame.module.aBTest.manager.schedule.DispatcherService;
import com.bonc.frame.module.aBTest.manager.schedule.FIFODispatcherService;
import com.bonc.frame.module.aBTest.manager.schedule.ShuffleDispatcherService;
import com.bonc.frame.module.threadpool.ThreadFactoryImpl;
import com.bonc.frame.service.aBTest.ABTestService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author yedunyao
 * @since 2019/12/5 19:50
 */
@Configuration
@EnableConfigurationProperties(ABTestManagerProperties.class)
public class ABTestManagerConfiguration {

    private Logger logger = LogManager.getLogger(ABTestManagerConfiguration.class);

    private final ABTestManagerProperties properties;

    @Autowired
    private ABTestService abTestService;

    public ABTestManagerConfiguration(ABTestManagerProperties properties) {
        this.properties = properties;
    }

    @Bean("aBTestManagerExecutor")
    @ConditionalOnProperty(name = "cre.aBTest.manager.enable", havingValue = "true")
    public ScheduledExecutorService aBTestManagerExecutor() {
        return Executors.newScheduledThreadPool(1, new ThreadFactoryImpl("ABTest-Manager-", true));
    }

    @Bean("persistModelInfoExecuor")
    @ConditionalOnProperty(name = "cre.aBTest.manager.enable", havingValue = "true")
    public ScheduledExecutorService persistModelInfoExecuor() {
        return Executors.newScheduledThreadPool(1, new ThreadFactoryImpl("ABTest-Manager-PersistModelInfo-", true));
    }

    @Bean("scanOmissionExecuor")
    @ConditionalOnProperty(name = "cre.aBTest.manager.enable", havingValue = "true")
    public ScheduledExecutorService scanOmissionExecuor() {
        return Executors.newScheduledThreadPool(1, new ThreadFactoryImpl("ABTest-Manager-ScanOmission-", true));
    }

    @Bean("abTestMetircConsumerExecutor")
    @ConditionalOnProperty(name = "cre.aBTest.manager.enable", havingValue = "true")
    public ScheduledExecutorService abTestMetircConsumerExecutor() {
        return Executors.newScheduledThreadPool(1, new ThreadFactoryImpl("ABTest-Manager-MetircConsumer-", true));
    }

    @Bean("abTestMetircDatePersistenceExecutor")
    @ConditionalOnProperty(name = "cre.aBTest.manager.enable", havingValue = "true")
    public ScheduledExecutorService abTestMetircDatePersistenceExecutor() {
        return Executors.newScheduledThreadPool(1, new ThreadFactoryImpl("ABTest-Manager-MetircPersistence-", true));
    }

    @Bean("dispatcherService")
    public DispatcherService dispatcherService() {
        String policy = properties.getDispatcher().getPolicy();
        switch (policy) {
            case "FIFO":
                return new FIFODispatcherService();
            case "RANDOM":
                return new ShuffleDispatcherService();
            default:
                return new ShuffleDispatcherService();
        }
    }

    @Bean("abTestManager")
    public ABTestManager abTestManager() {
        if (properties.isEnable()) {
            logger.info("创建 ABTestManagerImpl");
            return new ABTestManagerImpl(properties,
                    aBTestManagerExecutor()
                    , persistModelInfoExecuor()
                    , scanOmissionExecuor()
                    , abTestMetircConsumerExecutor()
                    , abTestService,
                    abTestMetircDatePersistenceExecutor(),
                    dispatcherService());
        } else {
            logger.info("创建 ABTestManagerStandby");
            return new ABTestManagerStandby();
        }
    }
}
