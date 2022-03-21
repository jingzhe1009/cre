package com.bonc.frame.module.aBTest;

import com.bonc.frame.entity.aBTest.ABTest;
import com.bonc.frame.module.aBTest.metric.ABTestMetric;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author yedunyao
 * @since 2020/9/22 11:31
 */
public class TaskQueue {

    private final Logger logger = LogManager.getLogger(TaskQueue.class);

    private BlockingQueue<ABTest> runningTasks;

    private List<ABTestMetric> abTestMetrics;

    public boolean offer(ABTest abTest) {
        return false;
    }

    public ABTest poll() {
        return null;
    }

    public ABTest poll(long timeout, TimeUnit unit) throws InterruptedException {
        return null;
    }

    public void remove(ABTest abTest) {

    }

    public int drainTo(Collection<ABTest> c) {
        return 0;
    }

    public int size() {
        return 0;
    }

}
