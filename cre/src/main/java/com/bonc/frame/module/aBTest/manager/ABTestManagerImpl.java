package com.bonc.frame.module.aBTest.manager;

import com.bonc.frame.config.Config;
import com.bonc.frame.entity.aBTest.ABTest;
import com.bonc.frame.entity.aBTest.ABTestDetail;
import com.bonc.frame.module.aBTest.manager.config.ABTestManagerProperties;
import com.bonc.frame.module.aBTest.manager.schedule.DispatcherService;
import com.bonc.frame.module.aBTest.manager.task.ManagerTask;
import com.bonc.frame.module.aBTest.manager.task.ScanOmissionTask;
import com.bonc.frame.module.aBTest.metric.ABTestMetric;
import com.bonc.frame.module.aBTest.metric.CpuMetric;
import com.bonc.frame.module.service.ServiceManager;
import com.bonc.frame.service.aBTest.ABTestService;
import com.bonc.frame.util.DateFormatUtil;
import com.bonc.frame.util.os.SystemTool;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author yedunyao
 * @since 2020/9/14 10:32
 */
public class ABTestManagerImpl implements ABTestManager {
    private final Logger logger = LogManager.getLogger(ABTestManagerImpl.class);

    /**
     * 传入字段
     */

    private ScheduledExecutorService aBTestManagerExecutor;

    private ScheduledExecutorService persistModelInfoExecuor;

    private ScheduledExecutorService scanOmissionExecuor;
    private ScheduledExecutorService abTestMetircConsumerExecutor;
    private ScheduledExecutorService abTestMetircDatePersistenceExecutor;

    private DispatcherService dispatcherService;

    private ABTestService abTestService;

    /**
     * 初始化字段
     */

    private BlockingQueue<ABTest> runningTasks;
    private List<ABTestMetric> abTestMetrics;

    private ServiceManager serviceManager;
    private CuratorFramework client;
    private ConsumerConnector consumerConnector;

    private String abTestMetricTopic;
    private int abTestMetricPoolSize;
    private final Map<String, ABTestMetric> abTestMetricICache;

    private FetchService fetchService;
    private double loadThreshold;
    private double percentThreshold;
    private String workersThreshold;
    private long dispatcherInterval;
    private long persistInterval;
    private long scanOmissionInterval;

    public ABTestManagerImpl(ABTestManagerProperties properties,
                             ScheduledExecutorService aBTestManagerExecutor,
                             ScheduledExecutorService persistModelInfoExecuor,
                             ScheduledExecutorService scanOmissionExecuor,
                             ScheduledExecutorService abTestMetircConsumerExecutor,
                             ABTestService abTestService,
                             ScheduledExecutorService abTestMetircDatePersistenceExecutor,
                             DispatcherService dispatcherService) {
        logger.info("启动 ABTestManagerImpl");
        this.aBTestManagerExecutor = aBTestManagerExecutor;
        this.persistModelInfoExecuor = persistModelInfoExecuor;
        this.scanOmissionExecuor = scanOmissionExecuor;
        this.abTestMetircConsumerExecutor = abTestMetircConsumerExecutor;
        this.abTestMetircDatePersistenceExecutor = abTestMetircDatePersistenceExecutor;
        this.abTestService = abTestService;
        this.dispatcherService = dispatcherService;

        runningTasks = new MyArrayBlockingQueue<>(100);
        abTestMetrics = new LinkedList<>();
        ABTestManagerProperties.Fetch propertiesFetch = properties.getFetch();
        ABTestManagerProperties.Dispatcher dispatcher = properties.getDispatcher();
        ABTestManagerProperties.Persist persist = properties.getPersist();
        ABTestManagerProperties.ScanOmission scanOmission = properties.getScanOmission();
        ABTestManagerProperties.ABTestMetric abTestMetricProperty = properties.getAbTestMetric();

        loadThreshold = dispatcher.getLoadThreshold();
        percentThreshold = dispatcher.getPercentThreshold();
        workersThreshold = dispatcher.getWorkersThreshold();
        dispatcherInterval = dispatcher.getInterval();

        persistInterval = persist.getInterval();
        scanOmissionInterval = scanOmission.getInterval();
        fetchService = new FetchService(propertiesFetch);
        if (abTestMetricProperty != null) {
            this.abTestMetricPoolSize = abTestMetricProperty.getPoolSize();
            this.abTestMetricTopic = abTestMetricProperty.getTopic();
        }
        abTestMetricICache = new ConcurrentHashMap<>();
    }

    public void init() throws Exception {
        logger.info("初始化 ABTestManagerImpl");
        client = CuratorFrameworkFactory.newClient(Config.ABTEST_ZOO_KEEPER_CONNECT,
                Config.ABTEST_ZOO_CURATOR_DEFAULT_SESSION_TIMEOUT, Config.ABTEST_ZOO_CURATOR_DEFAULT_CONNECTION_TIMEOUT,
                new ExponentialBackoffRetry(3000, 3));
        client.start();

        // kafka
        Properties consumerProperties = Config.AB_TEST_CONSUMER_PROPERTIES;
        ConsumerConfig consumerConfig = new ConsumerConfig(consumerProperties);
        consumerConnector = kafka.consumer.Consumer.createJavaConsumerConnector(consumerConfig);


        String serviceId = SystemTool.getCurrentProcessHost();
        serviceManager = new ServiceManager(client, "/cre/service", serviceId, CpuMetric.OPTIMISTIC_METRIC);
        serviceManager.register();
        logger.info("注册服务[{}]", serviceId);

        // 创建任务
        ManagerTask managerTask = new ManagerTask(runningTasks, serviceManager,
                loadThreshold, percentThreshold, workersThreshold, abTestService,
                fetchService, this, dispatcherService);
        managerTask.init();
        aBTestManagerExecutor.scheduleWithFixedDelay(managerTask, 0, dispatcherInterval, TimeUnit.MINUTES);
//        aBTestManagerExecutor.scheduleWithFixedDelay(managerTask, 0, 3, TimeUnit.SECONDS);

        ScanOmissionTask scanOmissionTask = new ScanOmissionTask(runningTasks, abTestMetrics,
                abTestService, client, this);
        scanOmissionExecuor.scheduleWithFixedDelay(scanOmissionTask, 1,
                scanOmissionInterval, TimeUnit.MINUTES);

//        PersistModelInfoTask persistModelInfoTask = new PersistModelInfoTask(abTestMetrics, abTestService);
//        persistModelInfoExecuor.scheduleWithFixedDelay(persistModelInfoTask, 2,
//                persistInterval, TimeUnit.MINUTES);
//        logger.info("初始化完成 ABTestManagerImpl");
        /**
         *  A/B测试执行统计
         */
        Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(abTestMetricTopic, 1);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector
                .createMessageStreams(topicCountMap);
        for (KafkaStream<byte[], byte[]> kafkaStream : consumerMap.get(this.abTestMetricTopic)) {
            abTestMetircConsumerExecutor.submit(new ABTestMetricConsumer(consumerConnector, kafkaStream, abTestMetricICache, client));
        }
        abTestMetircDatePersistenceExecutor.scheduleWithFixedDelay(new ABTestMetricDataPersistence(abTestMetricICache), Config.CRE_ABTEST_MANAGER_ABTESTMETRIC_UPDATE_INTERVAL,
                Config.CRE_ABTEST_MANAGER_ABTESTMETRIC_UPDATE_INTERVAL, TimeUnit.MINUTES);
    }


    /**
     * 1. 重启时从数据库中扫描 （需要记录当前任务跑到哪一天） 2. 页面点击，确认从某一天开始
     *
     * @param abTest
     * @return
     */
    public boolean start(ABTest abTest) throws Exception {
        logger.info("提交开始任务 id[{}], name[{}]", abTest.getaBTestId(), abTest.getaBTestName());
        // manager 初始化未完成 请等候
        initExecutorMetric(abTest);
        return true;
    }

    /**
     * 从执行队列中移除任务 将任务状态置为停用
     *
     * @param abTest
     * @return
     */
    public boolean stop(ABTest abTest) throws Exception {
        logger.info("提交停止任务 id[{}], name[{}]", abTest.getaBTestId(), abTest.getaBTestName());
        // 停用
        runningTasks.remove(abTest);
//        String aBTestId = abTest.getaBTestId();
//        ABTestMetricPersist.removeAndPersistABTestMetric(abTestMetrics, aBTestId, abTestService, client);
        return true;
    }

    public void initExecutorMetric(ABTest abTest) throws Exception {
        String aBTestId = abTest.getaBTestId();
        /*long count = abTest.getbExecuteCount();
        long successCount = abTest.getbSuccessCount();
        long failedCount = abTest.getbFailedCount();
        ABTestMetric abTestMetric = new ABTestMetric(aBTestId);
        abTestMetric.initBExecutorMetric(count, successCount, failedCount);*/

        initDate(abTest);

//        abTestMetrics.add(abTestMetric);
        runningTasks.offer(abTest);
    }

    public String initDate(ABTest abTest) {
        String aBTestId = abTest.getaBTestId();
        final Date earliestDate = DateFormatUtil.earliestDate;
        final String earliestDateStr = DateFormatUtil.earliestDateStr;
        String ruleId = abTest.getaRuleId();
        Date startTime = abTest.getaFetchStartTime();
        String startDateStr = DateFormatUtil.format(earliestDate, DateFormatUtil.YYYYMMDD_PATTERN);

        // 如果是第一次执行初始化执行位置
        if (startTime == null || earliestDateStr.equals(DateFormatUtil.format(startTime, DateFormatUtil.YYYYMMDD_PATTERN))) {
//            logger.debug("初始化任务日期，A/B测试id[{}]，A模型id[{}]", aBTestId, ruleId);
            try {
                startDateStr = fetchService.getEarliestExecutionDate(ruleId);
                startTime = DateFormatUtil.parse(startDateStr, DateFormatUtil.YYYYMMDD_PATTERN);
                logger.info("初始化任务日期，A/B测试id[{}]，A/B测试名称[{}], 日期[{}]", aBTestId,
                        abTest.getaBTestName(), startDateStr);
            } catch (Exception e) {
                logger.warn("请求日志平台获取模型最早执行日期失败，" +
                        "A/B测试id[{}]，A模型id[{}]", aBTestId, ruleId, e);
                // 发生异常 将日期默认设置为19700101
                startTime = earliestDate;
            } finally {
                updateABTestDate(startTime, abTest);
            }
        } else {
            startDateStr = DateFormatUtil.format(startTime, DateFormatUtil.YYYYMMDD_PATTERN);
        }
        return startDateStr;
    }

    public void updateABTestDate(Date startTime, ABTest abTest) {
        // 执行成功 更新日期  写库
        String aBTestId = abTest.getaBTestId();
        try {
            abTest.setaFetchStartTime(startTime);
            ABTestDetail update = new ABTestDetail();
            update.setaBTestId(aBTestId);
            update.setaFetchStartTime(startTime);
            abTestService.updateFetchStartTime(update);
        } catch (Exception e) {
            logger.error("更新任务日期失败，A/B测试id[{}]，日期[{}]",
                    aBTestId, startTime, e);
        }
    }

}
