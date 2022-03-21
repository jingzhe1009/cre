package com.bonc.frame.module.aBTest.metric;

import java.util.Date;

/**
 * @author yedunyao
 * @since 2020/9/11 18:17
 */
public class ExecutorMetric {

    private long count;

    private long successCount;

    private long failedCount;

    private Date dateTemp;

    public ExecutorMetric(long count, long successCount, long failedCount, String abTestId) {
        this.count = count;
        this.successCount = successCount;
        this.failedCount = failedCount;
        dateTemp = new Date();
    }

    public void init(long count, long successCount, long failedCount){
        this.count = count;
        this.successCount = successCount;
        this.failedCount = failedCount;
        dateTemp = new Date();
    }

    public void addCount() {
        count++;
    }

    public void addSuccessCount() {
        successCount++;
    }

    public void addFailedCount() {
        failedCount++;
    }

    public long getCount() {
        return count;
    }

    public long getSuccessCount() {
        return successCount;
    }

    public long getFailedCount() {
        return failedCount;
    }

    public Date getDateTemp() {
        return dateTemp;
    }

    public void setDateTemp(Date dateTemp) {
        this.dateTemp = dateTemp;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ExecutorMetric{");
        sb.append("count=").append(count);
        sb.append(", successCount=").append(successCount);
        sb.append(", failedCount=").append(failedCount);
        sb.append('}');
        return sb.toString();
    }
}
