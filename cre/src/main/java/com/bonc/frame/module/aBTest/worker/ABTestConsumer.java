package com.bonc.frame.module.aBTest.worker;

import com.alibaba.fastjson.JSON;
import com.bonc.frame.config.Config;
import com.bonc.frame.engine.EngineManager;
import com.bonc.frame.entity.aBTest.ABTest;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.module.aBTest.metric.ABTestMetricJobInfo;
import com.bonc.frame.module.cache.ICache;
import com.bonc.frame.module.kafka.v0821.MyProducer;
import com.bonc.frame.service.aBTest.ABTestService;
import com.bonc.frame.service.rule.RuleDetailService;
import com.bonc.frame.util.SpringUtils;
import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.executor.context.impl.ExecutorResponse;
import com.bonc.framework.rule.executor.context.impl.ModelExecutorType;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.ConsumerTimeoutException;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;

import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取A模型日志，并执行B模型
 * 统计数量
 * 1. zk原子加1
 * 2. oracle 乐观锁 for update
 *
 * @author yedunyao
 * @since 2020/9/14 11:04
 */
public class ABTestConsumer implements Runnable {
    Log log = LogFactory.getLog(getClass());
    private static final Pattern abTestIdPattern = Pattern.compile("aBTestId='(\\w*)'");

    private CuratorFramework client;

    private ConsumerConnector consumerConnector;
    private ABTestService abTestService;
    private RuleDetailService ruleDetailService;

    private KafkaStream<byte[], byte[]> kafkaStream;
    final ConsumerIterator<byte[], byte[]> consumerIterator;

    private final ICache<String, ABTest> aBTestCache;

    private final MyProducer abTestMetricProducer = new MyProducer(Config.AB_TEST_PRODUCER_PROPERTIES, Config.CRE_ABTEST_MANAGER_ABTESTMETRIC_TOPIC, "cre-abTestMetric");


    public ABTestConsumer(ConsumerConnector consumerConnector, KafkaStream<byte[], byte[]> kafkaStream,
                          ICache<String, ABTest> aBTestCache, CuratorFramework client) {
        this.consumerConnector = consumerConnector;
        this.kafkaStream = kafkaStream;
        abTestService = (ABTestService) SpringUtils.getBean("abTestService");
        ruleDetailService = (RuleDetailService) SpringUtils.getBean("ruleDetailService");
        this.aBTestCache = aBTestCache;
//        this.abTestMetricICache = abTestMetricICache;
        this.client = client;
        this.consumerIterator = this.kafkaStream.iterator();
    }

    public void run() {

        while (true) {
            boolean hasNext = false;
            try {
                hasNext = consumerIterator.hasNext();
            } catch (ConsumerTimeoutException e) {
                log.warn(e);
            }
            if (hasNext) {
                //是hasNext抛出异常,而不是next抛出
                final String ruleLogString;
                try {
                    final MessageAndMetadata<byte[], byte[]> next = consumerIterator.next();
                    ruleLogString = new StringDecoder(null).fromBytes(next.message());
                } catch (Exception e) {
                    log.error("A/B测试执行失败:反序列化规则任务信息失败", e);
                    continue;
                }
                try {
                    new ABTestExecuteModel(ruleLogString).run();
                } catch (Exception e) {
                    log.error("A/B测试执行失败:A/B测试模型执行失败:" + e.getMessage(), e);
                }

            }
        }
    }


    class ABTestExecuteModel implements Runnable {
        private String ruleLogString;

        public ABTestExecuteModel(String ruleLogString) {
            this.ruleLogString = ruleLogString;
        }

        @Override
        public void run() {
            String abTestId = null;
            try {
                abTestId = getAbTestId(ruleLogString);
                if (StringUtils.isBlank(abTestId)) {
                    throw new RuntimeException("abTestId为空");
                }

            } catch (Exception e) {
                log.error("A/B测试执行失败:从消息中获取abTestId失败" + e.getMessage(), e);
                return;
            }
            ABTest abTest = aBTestCache.get(abTestId);
//            ABTestMetric abTestMetric = abTestMetricICache.get(abTestId);
            try {
                if (abTest == null) {
                    abTest = abTestService.getDetail(abTestId);
                    if (abTest == null) {
                        throw new RuntimeException("A/B测试执行失败:获取ab测试失败,abTestId:[" + abTestId + "]");
                    }
                    aBTestCache.put(abTestId, abTest);
                }

//                if (abTestMetric == null) {
//                    abTestMetric = new ABTestMetric(abTestId);
//                    abTestMetric.initAExecutorMetric(abTest.getaFetchCount(), abTest.getaSuccessCount(), abTest.getaFailedCount(), client);
//                    abTestMetric.initBExecutorMetric(abTest.getbExecuteCount(), abTest.getbSuccessCount(), abTest.getbFailedCount(), client);
//                    abTestMetricICache.put(abTestId, abTestMetric);
//                }
            } catch (Exception e) {
                log.error("A/B测试执行失败:获取ABTest失败:" + e.getMessage(), e);
                return;
            }
            ExecutorResponse executorResponse = new ExecutorResponse();
            executorResponse.setSuccessed(false);
            executorResponse.setExecutorLogId(abTestId);    // ExecutorLogId 作为mq中的key,作用仅限于分区使用，默认可为abTestId
            try {

                executorResponse = executeRule(abTest, ruleLogString);
            } catch (Exception e) {
                log.warn("A/B测试执行失败:" + e.getMessage() + "A/B测试:[" + JSON.toJSONString(abTest) + "]", e);
                executorResponse.setSuccessed(false);
            } finally {
                Properties consumerProperties = Config.AB_TEST_CONSUMER_PROPERTIES;
                boolean autoCommit = Boolean.valueOf(consumerProperties.getProperty("auto.commit.enable", "false"));
                if (!autoCommit) {
                    consumerConnector.commitOffsets();
                    log.info(String.format("%s 同步offset成功%n", Thread.currentThread().getName()));
                }
            }

            // ab测试统计
            if (executorResponse != null && executorResponse.isSuccessed()) {
                addSuccess(abTestId, executorResponse.getExecutorLogId());
            } else {
                addFailed(abTestId, executorResponse.getExecutorLogId());
            }
        }


        private void addSuccess(String abTestId, String executorLogId) {
            try {
                ABTestMetricJobInfo abTestMetricJobInfo = new ABTestMetricJobInfo(abTestId, executorLogId, true);
                String key = executorLogId;
                // FIXME: 减少 message 的长度 改为  abTestId:1
                abTestMetricProducer.sendMessage(key, JSON.toJSONString(abTestMetricJobInfo));

            } catch (Exception e) {
                log.error("A/B测试执行失败:发送成功统计消息失败,abTestId:[" + abTestId + "]", e);
            }
        }

        private void addFailed(String abTestId, String executorLogId) {
            try {
                ABTestMetricJobInfo abTestMetricJobInfo = new ABTestMetricJobInfo(abTestId, executorLogId, false);
                String key = executorLogId;
                // FIXME: 减少 message 的长度 改为  abTestId:0
                abTestMetricProducer.sendMessage(key, JSON.toJSONString(abTestMetricJobInfo));

            } catch (Exception e) {
                log.error("A/B测试执行失败:发送失败统计消息失败,abTestId:[" + abTestId + "]", e);
            }
        }

        private String getAbTestId(String ruleLogString) {
            String abTestId = null;
            if (StringUtils.isNotBlank(ruleLogString)) {
                Matcher m = abTestIdPattern.matcher(ruleLogString);
                while (m.find()) {
                    abTestId = m.group(1);
                }
            }
            return abTestId;
        }

        public ExecutorResponse executeRule(ABTest abTest, String ruleLogString) throws Exception {
            String folderId = abTest.getFolderId();
            final String aRuleId = abTest.getaRuleId();
            final String ruleId = abTest.getbRuleId();

            if (StringUtils.isBlank(ruleId)) {
                throw new IllegalArgumentException("参数[ruleId]不能为空");
            }
            if (StringUtils.isBlank(folderId)) {
                RuleDetailWithBLOBs rule = ruleDetailService.getRule(ruleId);
                if (rule == null) {
                    throw new RuntimeException("获取B模型失败:请检查B模型是否被删除:ruleId:[" + ruleId + "]");
                }
                String bRuleFolderId = rule.getFolderId();
                if (StringUtils.isBlank(bRuleFolderId)) {
                    throw new IllegalArgumentException("请检查,模型:[" + ruleId + "]的folderId,不能为空");
                }
                folderId = bRuleFolderId;
                abTest.setFolderId(folderId);
            }

            // 渠道号
            final ConsumerInfo consumerInfo = new ConsumerInfo(null,
                    null, null);
            // 执行
            return EngineManager.getInstance().executeRule(folderId, ruleId,
                    new HashMap<String, Object>(), true, false, true,
                    null, ModelExecutorType.ABTEST, abTest.getaBTestId(), ruleLogString, aRuleId, consumerInfo);
        }
    }

}

