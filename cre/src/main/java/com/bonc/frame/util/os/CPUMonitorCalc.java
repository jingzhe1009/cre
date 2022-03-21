package com.bonc.frame.util.os;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * cpu average load 3
 * cpu usage 80%
 *
 * @author yedunyao
 * @since 2020/9/15 16:18
 */
public class CPUMonitorCalc {

    private static CPUMonitorCalc instance = new CPUMonitorCalc();

    private OperatingSystemMXBean osMxBean;
    private ThreadMXBean threadBean;
    private long preTime = System.nanoTime();
    private long preUsedTime = 0;

    private CPUMonitorCalc() {
        osMxBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        threadBean = ManagementFactory.getThreadMXBean();
    }

    public static CPUMonitorCalc getInstance() {
        return instance;
    }

    public double getProcessCpu() {
        return osMxBean.getProcessCpuLoad();
    }

    public double getSystemCpu() {
        return osMxBean.getSystemCpuLoad() * 100;
    }

    public double getSystemLoadAverage() {
        return osMxBean.getSystemLoadAverage();
    }

}
