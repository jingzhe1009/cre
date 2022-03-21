package com.bonc.frame.module.aBTest.manager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yedunyao
 * @since 2020/9/11 16:20
 */
@ConfigurationProperties(prefix = "cre.aBTest.manager")
public class ABTestManagerProperties {

    private boolean enable;

    private Fetch fetch;
    private Dispatcher dispatcher;
    private Persist persist;
    private ScanOmission scanOmission;
    private ABTestMetric abTestMetric;


    public static class Fetch {
        private String countUrl;
        private String earliestDateUrl;
        private String fetchUrl;

        public String getCountUrl() {
            return countUrl;
        }

        public void setCountUrl(String countUrl) {
            this.countUrl = countUrl;
        }

        public String getEarliestDateUrl() {
            return earliestDateUrl;
        }

        public void setEarliestDateUrl(String earliestDateUrl) {
            this.earliestDateUrl = earliestDateUrl;
        }

        public String getFetchUrl() {
            return fetchUrl;
        }

        public void setFetchUrl(String fetchUrl) {
            this.fetchUrl = fetchUrl;
        }
    }

    public static class Dispatcher {
        private long interval;
        // workers cpu 阈值
        private double loadThreshold;
        private double percentThreshold;
        private String workersThreshold;
        private String policy;

        public long getInterval() {
            return interval;
        }

        public void setInterval(long interval) {
            this.interval = interval;
        }

        public double getLoadThreshold() {
            return loadThreshold;
        }

        public void setLoadThreshold(double loadThreshold) {
            this.loadThreshold = loadThreshold;
        }

        public double getPercentThreshold() {
            return percentThreshold;
        }

        public void setPercentThreshold(double percentThreshold) {
            this.percentThreshold = percentThreshold;
        }

        public String getWorkersThreshold() {
            return workersThreshold;
        }

        public void setWorkersThreshold(String workersThreshold) {
            this.workersThreshold = workersThreshold;
        }

        public String getPolicy() {
            return policy;
        }

        public void setPolicy(String policy) {
            this.policy = policy;
        }
    }

    public static class Persist {
        private long interval;

        public long getInterval() {
            return interval;
        }

        public void setInterval(long interval) {
            this.interval = interval;
        }
    }

    public static class ScanOmission {
        private long interval;

        public long getInterval() {
            return interval;
        }

        public void setInterval(long interval) {
            this.interval = interval;
        }
    }

    public static class ABTestMetric {
        private int poolSize;
        private String topic;

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


    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Fetch getFetch() {
        return fetch;
    }

    public void setFetch(Fetch fetch) {
        this.fetch = fetch;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public Persist getPersist() {
        return persist;
    }

    public void setPersist(Persist persist) {
        this.persist = persist;
    }

    public ScanOmission getScanOmission() {
        return scanOmission;
    }

    public void setScanOmission(ScanOmission scanOmission) {
        this.scanOmission = scanOmission;
    }

    public ABTestMetric getAbTestMetric() {
        return abTestMetric;
    }

    public void setAbTestMetric(ABTestMetric abTestMetric) {
        this.abTestMetric = abTestMetric;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ABTestFetchProperties{");
        sb.append("enable=").append(enable);
        sb.append(", fetch=").append(fetch);
        sb.append('}');
        return sb.toString();
    }
}
