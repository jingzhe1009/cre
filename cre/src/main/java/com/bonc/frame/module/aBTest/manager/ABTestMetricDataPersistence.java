package com.bonc.frame.module.aBTest.manager;

import com.alibaba.fastjson.JSON;
import com.bonc.frame.entity.aBTest.ABTestDetail;
import com.bonc.frame.module.aBTest.metric.ABTestMetric;
import com.bonc.frame.module.aBTest.metric.ExecutorMetric;
import com.bonc.frame.service.aBTest.ABTestService;
import com.bonc.frame.util.SpringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/9/25 16:27
 */
public class ABTestMetricDataPersistence implements Runnable {
    Log log = LogFactory.getLog(getClass());

    private ABTestService abTestService;

    private final Map<String, ABTestMetric> abTestMetricTmpCache; // 当前线程中一定时间内的统计


    public ABTestMetricDataPersistence(Map<String, ABTestMetric> abTestMetricTmpCache) {
        this.abTestMetricTmpCache = abTestMetricTmpCache;
        abTestService = (ABTestService) SpringUtils.getBean("abTestService");
    }

    @Override
    public void run() {
        // 0. 五分钟遍历一次
        // 1. 需要更新的写库 -- 遍历map count>0且 时间戳超时的入库 初始化
        // 2. 删除 时间戳超过3d,并且count==0
        if (!abTestMetricTmpCache.isEmpty()) {
            try {
                List<String> deleteAbTestIds = new ArrayList<>();
                log.debug("A/B测试执行统计临时缓存:" + JSON.toJSONString(abTestMetricTmpCache));
                for (String abTestId : abTestMetricTmpCache.keySet()) {
                    ABTestMetric abTestMetric = abTestMetricTmpCache.get(abTestId);
                    if (abTestMetric == null) {
                        continue;
                    }
                    Date dateTemp = abTestMetric.getbExecutorMetric().getDateTemp();
                    if (dateTemp == null) {
                        dateTemp = new Date();
                        abTestMetric.getbExecutorMetric().setDateTemp(dateTemp);
                    }
                    // 更新数据库
                    ExecutorMetric executorMetric = abTestMetric.getbExecutorMetric();
                    long successCount = executorMetric.getSuccessCount();
                    long failedCount = executorMetric.getFailedCount();
                    long count = successCount + failedCount;
                    String aBTestId = abTestMetric.getaBTestId();
                    ABTestDetail abTest = abTestService.getDetail(aBTestId);
                    if (abTest == null) {
                        log.error("A/B测试更新执行统计失败:请检查A/B测试是否已被删除,abTestId:[" + aBTestId + "]",
                                new NullPointerException("A/B测试更新执行统计失败:请检查A/B测试是否已被删除,abTestId:[" + aBTestId + "]"));
                        deleteAbTestIds.add(aBTestId);
                        continue;
                    }
                    long beforbExecuteCount = abTest.getbExecuteCount();
                    long beforbSuccessCount = abTest.getbSuccessCount();
                    long beforbFailedCount = abTest.getbFailedCount();
                    // 如果缓存中的总数与数据库中的不相等,则更新数据库
                    if (count != beforbExecuteCount) {
                        abTest.setbExecuteCount(count);
                        abTest.setbSuccessCount(successCount);
                        abTest.setbFailedCount(failedCount);
                        log.debug("A/B测试更新执行统计:A/B测试id:[" + abTest.getaBTestId() + "],A/B测试名称:[" + abTest.getaBTestName() + "]," +
                                "更新前的统计值:count:[" + beforbExecuteCount + "],success[" + beforbSuccessCount + "],failed:[" + beforbFailedCount + "]," +
                                "更新后的统计值:count:[" + abTest.getbExecuteCount() + "],success[" + abTest.getbSuccessCount() + "],failed:[" + abTest.getbFailedCount() + "]");
                        abTestService.updateExecuteStatus(abTest);

                        abTestMetric.getbExecutorMetric().setDateTemp(new Date());
                    } else {
                        // 没有更新,并且超过3天,删除缓存中的ab测试
                        if (getDatePoorMinute(new Date(), dateTemp) >= 3 * 24 * 60) {
//                    if (getDatePoorMinute(new Date(), dateTemp) >= 3) {
                            deleteAbTestIds.add(abTestMetric.getaBTestId());
                        }
                    }
                }
                if (!deleteAbTestIds.isEmpty()) {
                    for (String abTestId : deleteAbTestIds) {
                        abTestMetricTmpCache.remove(abTestId);
                        log.debug("A/B测试更新执行统计:删除缓存中的统计:AB测试Id:[" + abTestId + "]");
                    }
                }
            } catch (Exception e) {
                log.error("A/B测试更新执行统计失败:" + e.getMessage(), e);
            }
        }

    }

    public static int getDatePoorMinute(Date endDate, Date nowDate) {
        return Math.abs((int) ((endDate.getTime() - nowDate.getTime())) / (1000 * 60));
    }


}
