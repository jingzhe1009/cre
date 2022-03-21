package com.bonc.frame.module.threadpool;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yedunyao
 * @since 2019/12/5 19:50
 */
@Configuration
@EnableConfigurationProperties(ThreadPoolProperties.class)
public class ThreadPoolAutoConfiguration {

    private final ThreadPoolProperties properties;

    public ThreadPoolAutoConfiguration(ThreadPoolProperties properties) {
        this.properties = properties;
    }

    @Bean
    public ThreadPoolManager graghExecutor(ThreadPoolProperties properties) {
        return new ThreadPoolManager();
    }

}
