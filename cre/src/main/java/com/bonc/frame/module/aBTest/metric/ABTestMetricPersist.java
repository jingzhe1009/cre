package com.bonc.frame.module.aBTest.metric;

import com.bonc.frame.entity.aBTest.ABTest;
import com.bonc.frame.service.aBTest.ABTestService;
import org.apache.curator.framework.CuratorFramework;

import java.util.List;

/**
 * @author yedunyao
 * @since 2020/9/22 11:46
 */
public class ABTestMetricPersist {

    public static void persistMetric(ABTestMetric abTestMetric, ABTestService abTestService) throws Exception {
        String aBTestId = abTestMetric.getaBTestId();
        ExecutorMetric executorMetric = abTestMetric.getbExecutorMetric();
//        long count = executorMetric.getCount();
        long successCount = executorMetric.getSuccessCount();
        long failedCount = executorMetric.getFailedCount();
        long count = successCount + failedCount;
        ABTest abTest = new ABTest();
        abTest.setaBTestId(aBTestId);
        abTest.setbExecuteCount(count);
        abTest.setbSuccessCount(successCount);
        abTest.setbFailedCount(failedCount);
        abTestService.updateExecuteStatus(abTest);
    }

    public static ABTestMetric removeAndPersistABTestMetric(List<ABTestMetric> abTestMetrics,
                                                            String aBTestId,
                                                            ABTestService abTestService,
                                                            CuratorFramework client) throws Exception {
        ABTestMetric metric = null;
        int index = 0;
        for (ABTestMetric abTestMetric : abTestMetrics) {
            if (aBTestId.equals(abTestMetric.getaBTestId())) {
                metric = abTestMetric;
                break;
            }
            index++;
        }
        if (metric != null) {
            // delete
            persistMetric(metric, abTestService);
            abTestMetrics.remove(index);
            metric.delete(client);
        }
        return metric;
    }

}
