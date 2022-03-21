package com.bonc.frame.module.aBTest.metric;

import java.io.Serializable;

/**
 * 统计当前进程占用CPU的情况
 *
 * @author yedunyao
 * @since 2020/9/15 15:46
 */
public class CpuMetric implements Serializable {

    private static final long serialVersionUID = -1438520602638526713L;

    public static final CpuMetric OPTIMISTIC_METRIC = new CpuMetric();

    private double cpuLoad = -1;

    private double cpuPercent = -1;

    public CpuMetric() {
    }

    public CpuMetric(double cpuLoad, short cpuPercent) {
        this.cpuLoad = cpuLoad;
        this.cpuPercent = cpuPercent;
    }

    public double getCpuPercent() {
        return cpuPercent;
    }

    public void setCpuPercent(double cpuPercent) {
        this.cpuPercent = cpuPercent;
    }

    public double getCpuLoad() {
//        cpuLoad = CPUMonitorCalc.getInstance().getProcessCpu();
        return cpuLoad;
    }

    public void setCpuLoad(double cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public boolean isOverhead(double loadThreshold, double percentThreshold) {
        if (cpuLoad > loadThreshold || cpuPercent > percentThreshold) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "CpuMetric{" +
                "cpuLoad=" + cpuLoad +
                ", cpuPercent=" + cpuPercent +
                '}';
    }
}
