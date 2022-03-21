package com.bonc.frame.module.aBTest.metric;

import com.bonc.frame.module.aBTest.manager.schedule.WorkersThreshold;
import com.bonc.frame.util.CollectionUtil;

import java.util.Collection;

/**
 * @author yedunyao
 * @since 2020/9/18 11:12
 */
public class ClusterMetric {

    private Collection<CpuMetric> workersMetric;

    // task metric 新任务 已执行


    public Collection<CpuMetric> getWorkersMetric() {
        return workersMetric;
    }

    public void setWorkersMetric(Collection<CpuMetric> workersMetric) {
        this.workersMetric = workersMetric;
    }

    public boolean isOverhead(double loadThreshold, double percentThreshold, WorkersThreshold workersThreshold) {
        if (CollectionUtil.isEmpty(workersMetric)) {
            return true;
        }
        int total = workersMetric.size();
        int threshold;
        switch (workersThreshold) {
            case ALL:
                threshold = total;
                break;
            case HALF:
                threshold = total >> 1;
                break;
            default:
                threshold = total;
                break;
        }
        threshold = Math.max(1, threshold);
        int count = 0;
        for (CpuMetric cpuMetric : workersMetric) {
            if (cpuMetric.isOverhead(loadThreshold, percentThreshold)) {
                count++;
            }
        }
        return count >= threshold ? true : false;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClusterMetric{");
        sb.append("workersMetric=").append(workersMetric);
        sb.append('}');
        return sb.toString();
    }
}
