package com.bonc.frame.module.aBTest.manager;

import com.alibaba.fastjson.JSON;
import com.bonc.frame.entity.aBTest.ABTestDetail;
import com.bonc.frame.module.aBTest.metric.ABTestMetric;
import com.bonc.frame.module.aBTest.metric.ABTestMetricJobInfo;
import com.bonc.frame.service.aBTest.ABTestService;
import com.bonc.frame.util.SpringUtils;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.ConsumerTimeoutException;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;

import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/9/25 16:27
 */
public class ABTestMetricConsumer implements Runnable {
    Log log = LogFactory.getLog(getClass());
    private CuratorFramework client;

    private ConsumerConnector consumerConnector;
    private KafkaStream<byte[], byte[]> kafkaStream;
    private ABTestService abTestService;
    final ConsumerIterator<byte[], byte[]> consumerIterator;

    private final Map<String, ABTestMetric> abTestMetricTmpCache; // 当前线程中一定时间内的统计

    public ABTestMetricConsumer(ConsumerConnector consumerConnector, KafkaStream<byte[], byte[]> kafkaStream,
                                Map<String, ABTestMetric> abTestMetricTmpCache, CuratorFramework client) {
        this.abTestMetricTmpCache = abTestMetricTmpCache;
        this.consumerConnector = consumerConnector;
        this.kafkaStream = kafkaStream;
        this.client = client;
        abTestService = (ABTestService) SpringUtils.getBean("abTestService");
        this.consumerIterator = this.kafkaStream.iterator();

    }

    @Override
    public void run() {
        while (true) {
            // 处理数据
            boolean hasNext = false;
            try {
                hasNext = consumerIterator.hasNext();
            } catch (ConsumerTimeoutException e) {
                log.warn(e);
            }
            if (hasNext) {
                ABTestMetricJobInfo abTestMetricJobInfo = null;
                try {
                    final MessageAndMetadata<byte[], byte[]> next = consumerIterator.next();
                    String message = new StringDecoder(null).fromBytes(next.message());
                    abTestMetricJobInfo = JSON.parseObject(message, ABTestMetricJobInfo.class);
                    abTestMetricJobInfo.setMessage(message);
                } catch (Exception e) {
                    log.error("A/B测试统计失败:反序列化规则任务信息失败", e);
                    continue;
                }
                if (abTestMetricJobInfo == null) {
                    continue;
                }
                String abTestId = abTestMetricJobInfo.getAbTestId();
                try {
                    ABTestMetric abTestMetric = abTestMetricTmpCache.get(abTestId);
                    if (abTestMetric == null) {
                        ABTestDetail abTest = abTestService.getDetail(abTestId);
                        if (abTest == null) {
                            log.error("A/B测试统计失败:请检查A/B测试是否已被删除,abTestId:[" + abTestId + "]");
                            throw new NullPointerException("请检查A/B测试是否已被删除,abTestId:[" + abTestId + "]");
//                            continue;
                        }
                        abTestMetric = new ABTestMetric(abTestId);
                        abTestMetric.initBExecutorMetric(abTest.getbExecuteCount(), abTest.getbSuccessCount(), abTest.getbFailedCount());
                        abTestMetricTmpCache.put(abTestId, abTestMetric);
                    }
                    abTestMetric.getbExecutorMetric().addCount();
                    if (abTestMetricJobInfo.isSuccess()) {
                        abTestMetric.getbExecutorMetric().addSuccessCount();
                    } else {
                        abTestMetric.getbExecutorMetric().addFailedCount();
                    }
                } catch (Exception e) {
                    log.error("A/B测试统计失败:" + e.getMessage() + ",abTestMetricJobInfo:" + JSON.toJSONString(abTestMetricJobInfo), e);
                }
            }
        }
    }

}
