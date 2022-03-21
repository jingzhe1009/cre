package com.bonc.frame.module.aBTest.manager.config;

import com.bonc.frame.module.aBTest.manager.ABTestManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author yedunyao
 * @since 2020/9/23 23:52
 */
@Component
public class ABTestManagerListener implements ApplicationListener<ApplicationReadyEvent> {

    private final Logger logger = LogManager.getLogger(ABTestManagerListener.class);

    @Autowired
    private ABTestManager abTestManager;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            abTestManager.init();
        } catch (Exception e) {
            logger.error("ABTestManager 初始化失败", e);
            System.exit(1);
        }
    }
}
