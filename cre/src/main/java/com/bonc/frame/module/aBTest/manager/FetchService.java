package com.bonc.frame.module.aBTest.manager;

import com.bonc.frame.module.aBTest.manager.config.ABTestManagerProperties;
import com.bonc.frame.util.DateFormatUtil;
import com.bonc.frame.util.http.HttpClient;
import com.bonc.frame.util.retry.ExponentialBackoffRetry;
import com.bonc.frame.util.retry.RetryPolicy;
import com.bonc.framework.api.exception.CallApiException;
import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Map;

/**
 * @author yedunyao
 * @since 2020/9/18 9:47
 */
public class FetchService {

    private Logger logger = LogManager.getLogger(FetchService.class);

    private String countUrl;
    private String earliestDateUrl;
    private String fetchUrl;

    private RetryPolicy retryPolicy;

    public FetchService(ABTestManagerProperties.Fetch fetch) {
        this.countUrl = fetch.getCountUrl();
        this.earliestDateUrl = fetch.getEarliestDateUrl();
        this.fetchUrl = fetch.getFetchUrl();
        retryPolicy = new ExponentialBackoffRetry(100, 5, 1000);
    }

    public FetchService(String countUrl, String earliestDateUrl, String fetchUrl) {
        this.countUrl = countUrl;
        this.earliestDateUrl = earliestDateUrl;
        this.fetchUrl = fetchUrl;
        retryPolicy = new ExponentialBackoffRetry(100, 5, 1000);
    }

    public long count(String modelId, String startDate) throws Exception {
        String endDateStr = String.valueOf(Integer.valueOf(startDate) + 1);
        return count(modelId, startDate, endDateStr);
    }

    /**
     * 获取模型日期范围内执行数量
     *
     * @param modelId
     * @param startDate
     * @param endDate
     * @return
     */
    public long count(String modelId, String startDate, String endDate) throws Exception {
        Map<String, String> param = ImmutableMap.of(
                "ruleid", modelId,
                "startdate", startDate,
                "enddate", endDate
        );
        if (logger.isDebugEnabled()) {
            logger.debug("请求日志平台模型日志统计，url:{}, param:{}", countUrl, param);
        }
        Map result = HttpClient.doGetReturnMap(countUrl, param, retryPolicy);
        if (logger.isDebugEnabled()) {
            logger.debug("请求日志平台模型日志统计，url:{}, param:{}, result:{}", countUrl, param, result);
        }
        Object total = result.get("total");
        if (total == null) {
            throw new CallApiException("请求日志平台模型日志统计返回结果不合法，返回结果：" + result);
        }
        long value = -1;
        try {
            value = Long.valueOf(total.toString());
        } catch (NumberFormatException e) {
            throw new CallApiException("请求日志平台模型日志统计返回结果不合法，返回结果：" + result);
        }
        return value;
    }

    /**
     * 获取模型最早执行日期
     *
     * @param modelId
     * @return
     */
    public String getEarliestExecutionDate(String modelId) throws Exception {
        Map<String, String> param = ImmutableMap.of(
                "ruleid", modelId
                );
        if (logger.isDebugEnabled()) {
            logger.debug("请求访问日志平台模型日志最早执行日期，url:{}, param:{}", earliestDateUrl, param);
        }
        Map result = HttpClient.doGetReturnMap(earliestDateUrl, param, retryPolicy);
        if (logger.isDebugEnabled()) {
            logger.debug("请求访问日志平台模型日志最早执行日期，url:{}, param:{}, result:{}", earliestDateUrl, param, result);
        }
        Object firstTime = result.get("firstTime");
        if (firstTime == null) {
            throw new CallApiException("返回结果不合法，返回结果：" + result);
        }
        return firstTime.toString();
    }

    /**
     * 获取数据 默认获取一天
     *
     * @param modelId
     * @param startDate
     * @param aBTestId
     */
    public long fetch(String modelId, Date startDate, String aBTestId) throws Exception {
        String startDateStr = DateFormatUtil.format(startDate, DateFormatUtil.YYYYMMDD_PATTERN);
        String endDateStr = DateFormatUtil.format(DateFormatUtil.tomorrow(startDate), DateFormatUtil.YYYYMMDD_PATTERN);
//        String endDateStr = String.valueOf(Integer.valueOf(startDateStr) + 1);
        return fetch(modelId, startDateStr, endDateStr, aBTestId);
    }

    public long fetch(String modelId, String startDate, String aBTestId) throws Exception {
        String endDateStr = String.valueOf(Integer.valueOf(startDate) + 1);
        return fetch(modelId, startDate, endDateStr, aBTestId);
    }

    /**
     * 获取数据
     *
     * @param modelId
     * @param startDate
     * @param endDate
     * @param aBTestId
     */
    public long fetch(String modelId, String startDate, String endDate, String aBTestId) throws Exception {
        Map<String, String> param = ImmutableMap.of(
                "ruleid", modelId,
                "startdate", startDate,
                "enddate", endDate,
                "aBTestId", aBTestId
        );
        if (logger.isDebugEnabled()) {
            logger.debug("请求日志平台获取模型日志，url:{}, param:{}", fetchUrl, param);
        }
        Map result = HttpClient.doGetReturnMap(fetchUrl, param, retryPolicy);
        if (logger.isDebugEnabled()) {
            logger.debug("请求日志平台获取模型日志，url:{}, param:{}, result:{}", fetchUrl, param, result);
        }
        Object status = result.get("status");
        if (status == null) {
            throw new CallApiException("请求日志平台获取模型日志返回结果不合法，返回结果：" + result);
        }
        long total = -1;
        if ("ok".equalsIgnoreCase(status.toString())) {
            Object totalObj = result.get("HBaseNumTotal");
            if (totalObj != null) {
                try {
                    total = Long.valueOf(totalObj.toString());
                } catch (NumberFormatException e) {
//                    logger.warn("请求访问日志平台获取模型日志结果不合法，result[{}]", result, e);
                    throw new CallApiException("请求日志平台获取模型日志返回结果不合法，返回结果：" + result);
                }
            } else {
                throw new CallApiException("请求日志平台获取模型日志返回结果不合法，返回结果：" + result);
            }

        } else {
            throw new CallApiException("请求日志平台获取模型日志失败，返回结果：" + result);
        }
        return total;
    }

}
