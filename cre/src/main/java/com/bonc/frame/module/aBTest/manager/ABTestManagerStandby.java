package com.bonc.frame.module.aBTest.manager;

import com.bonc.frame.entity.aBTest.ABTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author yedunyao
 * @since 2020/9/14 10:32
 */
public class ABTestManagerStandby implements ABTestManager {
    private final Logger logger = LogManager.getLogger(ABTestManagerStandby.class);

    public ABTestManagerStandby() {
        logger.info("启动 ABTestManagerStandby");
    }

    public void init() throws Exception {
        logger.info("初始化 ABTestManagerStandby");
    }

    /**
     *
     * @param abTest
     * @return
     */
    public boolean start(ABTest abTest) throws Exception {
        logger.info("提交开始任务 id[{}], name[{}]", abTest.getaBTestId(), abTest.getaBTestName());
        logger.trace("当前节点不支持任务的管理");
        return false;
    }

    /**
     * 从执行队列中移除任务 将任务状态置为停用
     *
     * @param abTest
     * @return
     */
    public boolean stop(ABTest abTest) throws Exception {
        logger.info("提交停止任务 id[{}], name[{}]", abTest.getaBTestId(), abTest.getaBTestName());
        logger.trace("当前节点不支持任务的管理");
        return false;
    }

}
