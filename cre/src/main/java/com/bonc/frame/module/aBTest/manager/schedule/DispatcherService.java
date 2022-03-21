package com.bonc.frame.module.aBTest.manager.schedule;

import com.bonc.frame.module.aBTest.metric.ClusterMetric;

import java.util.concurrent.BlockingQueue;

/**
 * @author yedunyao
 * @since 2020/9/22 14:16
 */
public interface DispatcherService<T> {

    SchedulePolicy getSupport();

    T availableTask(BlockingQueue<T> queue, ClusterMetric clusterMetric);
}
