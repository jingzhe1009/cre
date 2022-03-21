package com.bonc.frame.module.aBTest.manager.task;

import com.bonc.frame.entity.aBTest.ABTest;
import com.bonc.frame.module.aBTest.manager.ABTestManagerImpl;
import com.bonc.frame.module.aBTest.manager.FetchService;
import com.bonc.frame.module.aBTest.manager.schedule.DispatcherService;
import com.bonc.frame.module.aBTest.manager.schedule.WorkersThreshold;
import com.bonc.frame.module.aBTest.metric.ClusterMetric;
import com.bonc.frame.module.aBTest.metric.CpuMetric;
import com.bonc.frame.module.service.ServiceManager;
import com.bonc.frame.service.aBTest.ABTestService;
import com.bonc.frame.util.DateFormatUtil;
import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * 监控执行状态 调度fetch任务
 * <p>
 * 1. 重启后重新初始化执行统计信息 2. 记录执行信息
 *
 * @author yedunyao
 * @since 2020/9/11 18:15
 */
public class ManagerTask implements Runnable {

    private final Logger logger = LogManager.getLogger(ManagerTask.class);

    private BlockingQueue<ABTest> runningTasks;

    private ServiceManager serviceManager;

    private ABTestService abTestService;

    private FetchService fetchService;

    private ABTestManagerImpl abTestManagerImpl;

//    Map<String, DispatcherService<ABTest>> dispatcherServiceMap;

    private double loadThreshold;
    private double percentThreshold;
    private String workersThreshold;

    private DispatcherService dispatcherService;


    public ManagerTask(BlockingQueue<ABTest> runningTasks,
                       ServiceManager serviceManager,
                       double loadThreshold,
                       double percentThreshold,
                       String workersThreshold,
                       ABTestService abTestService,
                       FetchService fetchService,
                       ABTestManagerImpl abTestManagerImpl,
                       DispatcherService dispatcherService) {
        this.runningTasks = runningTasks;
        this.serviceManager = serviceManager;
        this.loadThreshold = loadThreshold;
        this.percentThreshold = percentThreshold;
        this.workersThreshold = workersThreshold;
        this.abTestService = abTestService;
        this.fetchService = fetchService;
        this.abTestManagerImpl = abTestManagerImpl;
        this.dispatcherService = dispatcherService;
//        init();
    }

    public void init() throws Exception {
        // 加载运行中和停用中任务
        List<ABTest> running = abTestService.getByStatus(ABTest.RUNNING);

        // 初始化统计信息
        initExecutorMetric(running);

        logger.info("A/B测试 Manager 初始化成功");
    }

    // 初始化统计信息
    private void initExecutorMetric(List<ABTest> running) throws Exception {
        for (ABTest abTest : running) {
            abTestManagerImpl.initExecutorMetric(abTest);
        }
    }

    @Override
    public void run() {
        logger.info("开始调度");
        // 2. 统计消费情况 workers cpu、内存情况
        ClusterMetric clusterMetric = null; // 消费情况 workers情况
        try {
            clusterMetric = metric();
            logger.info("当前 workers 状态：[{}]", clusterMetric);
        } catch (Exception e) {
            logger.warn("获取workers 统计信息失败", e);
        }


        // 3. 判断是否可继续执行
        if (canRun(clusterMetric, WorkersThreshold.getWorkersThreshold(workersThreshold))) { // 控制任务数量
            // 4. 调度
//            Collection<ABTest> availableTasks = availableTasks();
            ABTest task = null;
            try {
                task = (ABTest) dispatcherService.availableTask(runningTasks, clusterMetric);
            } catch (Exception e) {
                logger.warn("获取可执行任务失败", e);
            }
            if (task != null) {
                try {
                    doRun(task);
                } catch (Exception e) {
                    logger.warn("执行任务失败", e);
                }
            } else {
                logger.info("未获取到可执行任务，当前任务队列大小：[{}]", runningTasks.size());
            }
        } else {
            logger.info("workers状态不好，未生成任务");
        }
        logger.info("完成调度");
    }

    public ClusterMetric metric() {
        Map<String, CpuMetric> currentMembers = serviceManager.getCurrentMembers();
        Collection<CpuMetric> values = currentMembers.values();
        ClusterMetric clusterMetric = new ClusterMetric();
        clusterMetric.setWorkersMetric(values);
        return clusterMetric;
    }

    private boolean canRun(ClusterMetric clusterMetric, WorkersThreshold workersThreshold) {
        if (clusterMetric == null) {
            return false;
        }
        boolean overhead = false;
        try {
            overhead = clusterMetric.isOverhead(loadThreshold, percentThreshold, workersThreshold);
        } catch (Exception e) {
            logger.warn("检查workers负载失败", e);
            return false;
        }
        if (overhead) {
            return false;
        }
        return true;
    }


    /**
     * 调度 1. 从运行中的任务移除需要停止的任务 2. 从运行中的任务中按照调度策略获取可用的任务
     *
     * @return
     */
    public Collection<ABTest> availableTasks() {
        ABTest abTest = null;
        try {
            abTest = runningTasks.peek();
        } catch (Exception e) {
            logger.warn("从队列中获取任务失败", e);
            return ImmutableList.of(abTest);
        }
        if (abTest == null) {
            return Collections.emptyList();
        } else {
            // TODO 通过count控制调度 注意并发问题 有停止操作
            //  通过 count 接口获取任务总量
            /*String aBTestId = abTest.getaBTestId();
            String ruleId = abTest.getaRuleId();
            Date startTime = abTest.getaFetchStartTime();
            String startDateStr = DateFormatUtil.format(startTime, DateFormatUtil.YYYYMMDD_PATTERN);
            try {
                long count = fetchService.count(ruleId, startDateStr);
                logger.info("A/B测试id[{}]，A模型id[{}]，获取A模型总数[{}]", aBTestId, ruleId, count);
            } catch (Exception e) {
                logger.warn("请求日志平台获取模型总数失败，" +
                        "A/B测试id[{}]，A模型id[{}]", aBTestId, ruleId, e);
            }*/
        }

        return ImmutableList.of(abTest);
    }

    private void doRun(ABTest abTest) {
        // 5. 执行 fetch 任务  数据重复问题 fetch任务去重
        String aBTestId = abTest.getaBTestId();
        String aBTestName = abTest.getaBTestName();
        String ruleId = abTest.getaRuleId();
        Date startTime = abTest.getaFetchStartTime();
        String startDateStr = DateFormatUtil.format(startTime, DateFormatUtil.YYYYMMDD_PATTERN);

        try {
            //  只处理两天以前的数据 当前时间2020/10/13 只处理到2020/10/11 下次处理位置2020/10/12
            Date now = new Date();
            if (DateFormatUtil.distanceDays(startTime, now) > 1) {
                long total = fetchService.fetch(ruleId, startDateStr, aBTestId);
                logger.info("获取日志，A/B测试名称[{}]，id[{}]，A模型id[{}]，startDateStr[{}]，日志总数[{}]",
                        aBTestName, aBTestId, ruleId, startDateStr, total);
                // 执行成功 更新日期  写库
                Date tomorrow = DateFormatUtil.tomorrow(startTime);
                abTestManagerImpl.updateABTestDate(tomorrow, abTest);
            } else {
                logger.info("当前任务获取日志日期达到临界值，不再获取获取日志，" +
                        "A/B测试名称[{}]，id[{}]，A模型id[{}]，startDateStr[{}]", aBTestName, aBTestId, ruleId, startDateStr);
            }
        } catch (Exception e) {
            logger.warn("请求日志平台获取模型数据失败，" +
                    "A/B测试名称[{}]，id[{}]，A模型id[{}]，日期[{}]", aBTestName, aBTestId, ruleId, startDateStr, e);
        }
    }

}
