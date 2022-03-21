package com.bonc.frame.module.aBTest.manager.schedule;

import com.bonc.frame.entity.aBTest.ABTest;
import com.bonc.frame.module.aBTest.manager.MyArrayBlockingQueue;
import com.bonc.frame.module.aBTest.metric.ClusterMetric;
import com.bonc.frame.util.DateFormatUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * @author yedunyao
 * @since 2020/9/22 14:16
 */

public class ShuffleDispatcherService<T> implements DispatcherService<T> {

    private final Logger logger = LogManager.getLogger(ShuffleDispatcherService.class);

    @Override
    public SchedulePolicy getSupport() {
        return SchedulePolicy.RANDOM;
    }

    public T availableTask(BlockingQueue<T> queue, ClusterMetric clusterMetric) {
        MyArrayBlockingQueue queue1 = (MyArrayBlockingQueue) queue;
        List<T> list = new LinkedList<>();
        int count = queue1.randomView(list);

        T task = null;
        if (count > 0) {
            for (T t : list) {
                ABTest abTest = (ABTest) t;
                Date startTime = abTest.getaFetchStartTime();
                Date now = new Date();
                if (DateFormatUtil.distanceDays(startTime, now) > 1) {
                    task = t;
                }
            }
            if (task == null) {
                logger.info("当前所有任务的请求日志时间已到临界值，等待下次获取日志");
            }
        }
        list = null;    // 释放引用
        return task;
    }
}
