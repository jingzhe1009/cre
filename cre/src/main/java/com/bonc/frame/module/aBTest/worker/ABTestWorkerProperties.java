package com.bonc.frame.module.aBTest.worker;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wangzhengbao
 */
@ConfigurationProperties(prefix = "cre.aBTest.worker")
public class ABTestWorkerProperties {

    private boolean enable = Boolean.FALSE;
    private Consumer consumer = new Consumer();
    private CpuMetric cpuMetric;

    public static class Consumer {
        //        private String idleWaitingTime; // 没有数据时,sleep的时间
        private int poolSize = 10;
        private String topic;

//        public String getIdleWaitingTime() {
//            return idleWaitingTime;
//        }
//
//        public void setIdleWaitingTime(String idleWaitingTime) {
//            this.idleWaitingTime = idleWaitingTime;
//        }

        public int getPoolSize() {
            return poolSize;
        }

        public void setPoolSize(int poolSize) {
            this.poolSize = poolSize;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }
    }

    public static class CpuMetric {
        private long interval;

        public long getInterval() {
            return interval;
        }

        public void setInterval(long interval) {
            this.interval = interval;
        }

    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public CpuMetric getCpuMetric() {
        return cpuMetric;
    }

    public void setCpuMetric(CpuMetric cpuMetric) {
        this.cpuMetric = cpuMetric;
    }
}
