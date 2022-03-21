package com.bonc.frame.module.aBTest.manager.task;

import com.bonc.frame.entity.aBTest.ABTest;
import com.bonc.frame.module.aBTest.manager.ABTestManagerImpl;
import com.bonc.frame.module.aBTest.metric.ABTestMetric;
import com.bonc.frame.service.aBTest.ABTestService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * 扫描遗漏的任务
 *
 * @author yedunyao
 * @since 2020/9/21 17:50
 */
public class ScanOmissionTask implements Runnable {

    private final Logger logger = LogManager.getLogger(ScanOmissionTask.class);

    private ABTestManagerImpl abTestManagerImpl;

    private BlockingQueue<ABTest> runningTasks;

    private List<ABTestMetric> abTestMetrics;
    private ABTestService abTestService;
    private CuratorFramework client;

    public ScanOmissionTask(BlockingQueue<ABTest> runningTasks,
                            List<ABTestMetric> abTestMetrics,
                            ABTestService abTestService,
                            CuratorFramework client,
                            ABTestManagerImpl abTestManagerImpl) {
        this.runningTasks = runningTasks;
        this.abTestMetrics = abTestMetrics;
        this.abTestService = abTestService;
        this.client = client;
        this.abTestManagerImpl = abTestManagerImpl;
    }

    @Override
    public void run() {
        logger.info("开始检查遗漏的任务");
        List<ABTest> ready = Collections.emptyList();
        try {
            ready = abTestService.getByStatus(ABTest.READY);
        } catch (Exception e) {
            logger.warn("从数据库中查询就绪任务失败", e);
        }
        for (ABTest abTest : ready) {
            logger.info("提交遗漏的开始任务 id[{}], name[{}]", abTest.getaBTestId(), abTest.getaBTestName());
            // manager 初始化未完成 请等候
            try {
                abTestManagerImpl.initExecutorMetric(abTest);
            } catch (Exception e) {
                logger.warn("初始化任务失败，A/B测试id[{}]，A/B测试名称[{}]",
                        abTest.getaBTestId(), abTest.getaBTestName(), e);
                continue;
            }
            try {
                abTestService.startInternal(abTest.getaBTestId());
            } catch (Exception e) {
                logger.warn("启用任务更新数据数据库失败，id[{}], name[{}]",
                        abTest.getaBTestId(), abTest.getaBTestName(), e);
            }
        }

        List<ABTest> stopping = Collections.emptyList();
        try {
            stopping = abTestService.getByStatus(ABTest.STOPPING);
        } catch (Exception e) {
            logger.warn("从数据库中查询停用中任务失败", e);
        }
        for (ABTest abTest : stopping) {
            logger.info("提交遗漏的停止任务 id[{}], name[{}]", abTest.getaBTestId(), abTest.getaBTestName());
            runningTasks.remove(abTest);   // full lock
            try {
                abTestService.stopInternal(abTest.getaBTestId());
            } catch (Exception e) {
                logger.warn("停用任务更新数据数据库失败，id[{}], name[{}]",
                        abTest.getaBTestId(), abTest.getaBTestName(), e);
            }
//            String aBTestId = abTest.getaBTestId();
            // persist
           /* try {
                ABTestMetricPersist.removeAndPersistABTestMetric(abTestMetrics, aBTestId, abTestService, client);
            } catch (KeeperException.NoNodeException e) {
                logger.warn("停用遗漏的任务失败，节点已不存在，aBTestId[{}]", aBTestId, e);
            } catch (Exception e) {
                logger.error("停用遗漏的任务失败， aBTestId[{}]", aBTestId, e);
            }*/
        }
        logger.info("完成检查遗漏的任务");
    }

}
