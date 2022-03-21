package com.bonc.frame.module.aBTest.metric;

import org.apache.curator.framework.CuratorFramework;

/**
 * @author yedunyao
 * @since 2020/9/11 18:18
 */
public class ABTestMetric {

    public static final String METRIC_PATH_PRE = "/cre/abTest/";

    private String aBTestId;

    // 任务状态
    // 基础信息

    private ExecutorMetric aExecutorMetric;

    private ExecutorMetric bExecutorMetric;

    public void initAExecutorMetric(long count, long successCount, long failedCount) {
//        String path = METRIC_PATH_PRE + aBTestId + "/aExecutorMetric";
        if (aExecutorMetric == null) {
            aExecutorMetric = new ExecutorMetric(count, successCount, failedCount, aBTestId);
        } else {
            aExecutorMetric.init(count, successCount, failedCount);
        }
    }

    public void initBExecutorMetric(long count, long successCount, long failedCount) {
//        String path = METRIC_PATH_PRE + aBTestId + "/bExecutorMetric";
        if (bExecutorMetric == null) {
            bExecutorMetric = new ExecutorMetric(count, successCount, failedCount, aBTestId);
        } else {
            bExecutorMetric.init(count, successCount, failedCount);
        }
    }

    public void delete(CuratorFramework client) throws Exception {
//        if (client == null) {
//            throw new Exception("zookeeper client不能为空");
//        }
//        ZkUtil.recursivelyDelete(METRIC_PATH_PRE + aBTestId, client);
    }

    public ABTestMetric(String aBTestId) {
        this.aBTestId = aBTestId;
    }

    public String getaBTestId() {
        return aBTestId;
    }

    public void setaBTestId(String aBTestId) {
        this.aBTestId = aBTestId;
    }

    public ExecutorMetric getaExecutorMetric() {
        return aExecutorMetric;
    }

    public void setaExecutorMetric(ExecutorMetric aExecutorMetric) {
        this.aExecutorMetric = aExecutorMetric;
    }

    public ExecutorMetric getbExecutorMetric() {
        return bExecutorMetric;
    }

    public void setbExecutorMetric(ExecutorMetric bExecutorMetric) {
        this.bExecutorMetric = bExecutorMetric;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ABTestMetric{");
        sb.append("aBTestId='").append(aBTestId).append('\'');
        sb.append(", aExecutorMetric=").append(aExecutorMetric);
        sb.append(", bExecutorMetric=").append(bExecutorMetric);
        sb.append('}');
        return sb.toString();
    }
}
