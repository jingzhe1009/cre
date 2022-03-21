package com.bonc.frame.module.aBTest.worker;

import com.bonc.frame.module.threadpool.ThreadFactoryImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author wangzhengbao
 */
@Configuration
@EnableConfigurationProperties(ABTestWorkerProperties.class)
@ConditionalOnProperty(name = "cre.aBTest.worker.enable", matchIfMissing = true)
public class ABTestWorkerConfiguration {

    private Logger logger = LogManager.getLogger(ABTestWorkerConfiguration.class);

    private final ABTestWorkerProperties properties;

    public ABTestWorkerConfiguration(ABTestWorkerProperties properties) {
        this.properties = properties;
    }
    //aBTestConsumerExecutor;
    //aBTestCpuMetricExecutor

    @Bean("aBTestConsumerExecutor")
    public ScheduledExecutorService aBTestConsumerExecutor() {
        return Executors.newScheduledThreadPool(properties.getConsumer().getPoolSize(), new ThreadFactoryImpl("ABTest-Worker-Consumer-", true));
    }

    @Bean("aBTestCpuMetricExecutor")
    public ScheduledExecutorService aBTestCpuMetricExecutor() {
        return Executors.newScheduledThreadPool(1, new ThreadFactoryImpl("ABTest-Worker-CpuMetric-", true));
    }


}
