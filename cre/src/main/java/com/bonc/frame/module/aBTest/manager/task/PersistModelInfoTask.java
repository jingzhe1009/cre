package com.bonc.frame.module.aBTest.manager.task;

import com.bonc.frame.module.aBTest.metric.ABTestMetric;
import com.bonc.frame.module.aBTest.metric.ABTestMetricPersist;
import com.bonc.frame.service.aBTest.ABTestService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * 同步执行信息到数据库中
 *
 * @author yedunyao
 * @since 2020/9/21 17:50
 */
public class PersistModelInfoTask implements Runnable {

    private final Logger logger = LogManager.getLogger(PersistModelInfoTask.class);

    private List<ABTestMetric> abTestMetrics;
    private ABTestService abTestService;

    public PersistModelInfoTask(List<ABTestMetric> abTestMetrics,
                                ABTestService abTestService) {
        this.abTestMetrics = abTestMetrics;
        this.abTestService = abTestService;
    }

    @Override
    public void run() {
        logger.info("开始更新任务执行状态");
        // persist
        for (ABTestMetric abTestMetric : abTestMetrics) {
            try {
                if (logger.isDebugEnabled())
                    logger.debug("更新任务信息: [{}]", abTestMetric);
                ABTestMetricPersist.persistMetric(abTestMetric, abTestService);
            } catch (Exception e) {
                logger.error("保存任务执行状态失败， aBTestId[{}]", abTestMetric.getaBTestId(), e);
            }
        }
        logger.info("完成更新任务执行状态");
    }


}
