package com.bonc.frame.module.aBTest.worker;

import com.bonc.frame.module.aBTest.metric.CpuMetric;
import com.bonc.frame.module.service.ServiceManager;
import com.bonc.frame.util.os.CPUMonitorCalc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 统计 cpu 占用情况
 *
 * @author yedunyao
 * @since 2020/9/11 18:15
 */
public class CpuMetricTask implements Runnable {
    private final Logger logger = LogManager.getLogger(getClass());


    public static final String CPU_PATH = "/cre/cpu/";
    private ServiceManager serviceManager;
    private CpuMetric cpuMetric;


    public CpuMetricTask(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void run() {
        logger.info("开始获取CPU状态:serverId[" + serviceManager.getServiceId() + "]");
        try {
            if (cpuMetric == null) {
                cpuMetric = new CpuMetric();
            }
            double processCpu = CPUMonitorCalc.getInstance().getSystemCpu();
            double loadAverage = CPUMonitorCalc.getInstance().getSystemLoadAverage();
            cpuMetric.setCpuPercent(processCpu);
            cpuMetric.setCpuLoad(loadAverage);

            serviceManager.set(cpuMetric);

            if (logger.isDebugEnabled()) {
                logger.debug(serviceManager.getServiceId() + "-CPU:" + cpuMetric);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
