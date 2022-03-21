package com.bonc.frame.module.aBTest.worker;

import com.bonc.frame.config.Config;
import com.bonc.frame.entity.aBTest.ABTest;
import com.bonc.frame.module.aBTest.metric.ABTestMetric;
import com.bonc.frame.module.aBTest.metric.CpuMetric;
import com.bonc.frame.module.cache.ICache;
import com.bonc.frame.module.cache.heap.LRUCache;
import com.bonc.frame.module.cache.heap.SynchronizedCache;
import com.bonc.frame.module.service.ServiceManager;
import com.bonc.frame.util.os.CPUMonitorCalc;
import com.bonc.frame.util.os.SystemTool;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 消费数据
 * 监听cpu状态
 *
 * @author yedunyao
 * @since 2020/9/15 16:21
 */
@Component
@Order(4)
@EnableConfigurationProperties(ABTestWorkerProperties.class)
@ConditionalOnProperty(name = "cre.aBTest.worker.enable", matchIfMissing = true)
@ConditionalOnBean(ABTestWorkerConfiguration.class)
public class Worker implements CommandLineRunner {

    Log log = LogFactory.getLog(getClass());

    @Autowired
    private ABTestWorkerProperties abTestWorkerProperties;

    private String topic;
    private int threadNum;

    private ConsumerConnector consumerConnector;
    @Autowired
    private ScheduledExecutorService aBTestConsumerExecutor;
    @Autowired
    private ScheduledExecutorService aBTestCpuMetricExecutor;

    private final ICache<String, ABTest> aBTestCache;
    private final ICache<String, ABTestMetric> abTestMetricICache;

    private ServiceManager serviceManager;
    private CuratorFramework client;

    public Worker() {
        aBTestCache = new SynchronizedCache<>(new LRUCache<String, ABTest>(30));
        abTestMetricICache = new SynchronizedCache<>(new LRUCache<String, ABTestMetric>(30));
    }

    public void init() {
        try {
            Properties consumerProperties = Config.AB_TEST_CONSUMER_PROPERTIES;

            ConsumerConfig consumerConfig = new ConsumerConfig(consumerProperties);
            consumerConnector = kafka.consumer.Consumer.createJavaConsumerConnector(consumerConfig);

            topic = abTestWorkerProperties.getConsumer().getTopic();
            threadNum = abTestWorkerProperties.getConsumer().getPoolSize();

            client = CuratorFrameworkFactory.newClient(Config.ABTEST_ZOO_KEEPER_CONNECT,
                    Config.ABTEST_ZOO_CURATOR_DEFAULT_SESSION_TIMEOUT, Config.ABTEST_ZOO_CURATOR_DEFAULT_CONNECTION_TIMEOUT,
                    new ExponentialBackoffRetry(1000, 3));
            client.start();
        } catch (Exception e) {
            throw new RuntimeException("A/B测试 Worker 初始化失败:" + e.getMessage(), e);
        }
        log.info("A/B测试 Worker 成功");
    }

    @Override
    public void run(String... args) throws Exception {
        init();


        String serviceId = SystemTool.getCurrentProcessHost() + ServiceManager.WORKER_SUFFIX;

        CpuMetric cpuMetric = new CpuMetric();
        double processCpu = CPUMonitorCalc.getInstance().getSystemCpu();
        double loadAverage = CPUMonitorCalc.getInstance().getSystemLoadAverage();
        cpuMetric.setCpuPercent(processCpu);
        cpuMetric.setCpuLoad(loadAverage);
        serviceManager = new ServiceManager(client, ServiceManager.MEMBERSHIP_PATH, serviceId, cpuMetric);
        serviceManager.register();

        aBTestCpuMetricExecutor.scheduleWithFixedDelay(new CpuMetricTask(serviceManager), 1,
                abTestWorkerProperties.getCpuMetric().getInterval(), TimeUnit.SECONDS);

//        aBTestCpuMetricExecutor.submit(new CpuMetricTask(serviceManager));

        Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(this.topic, threadNum);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector
                .createMessageStreams(topicCountMap);
        for (KafkaStream<byte[], byte[]> kafkaStream : consumerMap.get(this.topic)) {
            aBTestConsumerExecutor.submit(new ABTestConsumer(consumerConnector, kafkaStream, aBTestCache, client));
        }
    }
}
