package com.bonc.frame.module.kafka.v0821;

import com.bonc.frame.config.Config;
import com.bonc.frame.module.task.ruleTask.RuleTaskJobInfo;
import com.bonc.frame.util.BeanUtils;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.ConsumerTimeoutException;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yedunyao
 * @date 2019/6/20 11:40
 */
public class MyConsumer {

    private static final String AUTO_COMMIT_KEY = "enable.auto.commit";
    private static final String MAX_POLL_RECORDS = "max.poll.records";
    private static final String MAX_POLL_WAIT_MS = "cre.max.poll.wait.ms";

    private Log log = LogFactory.getLog(MyConsumer.class);

    private ConsumerConnector consumerConnector;

    private Properties props;

    private String topic;

    private boolean autoCommit;

    private int batchSize;

    private long pollWait = 1000L;

    private int threadNum;

    private ExecutorService executorService;


    public MyConsumer(Properties props, String topic) {
        this.props = props;
        this.topic = topic;
        this.autoCommit = Boolean.valueOf(props.getProperty(AUTO_COMMIT_KEY, "false"));
//        batchSize = Integer.valueOf(props.getProperty(MAX_POLL_RECORDS));
//        pollWait = Long.valueOf(props.getProperty(MAX_POLL_WAIT_MS, "1000"));

        ConsumerConfig consumerConfig = new kafka.consumer.ConsumerConfig(props);
        consumerConnector = kafka.consumer.Consumer.createJavaConsumerConnector(consumerConfig);

        threadNum = Config.RULE_TASK_CONSUMER_POOL_SIZE;
        executorService = Executors.newFixedThreadPool(threadNum);
        log.info("消费线程池大小：" + threadNum);
    }

    public void subscribe() {
        Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(this.topic, threadNum);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector
                .createMessageStreams(topicCountMap);

        for (KafkaStream<byte[], byte[]> kafkaStream : consumerMap.get(this.topic)) {
            executorService.submit(new ConsumerRunner(consumerConnector, kafkaStream));
        }
    }

    private static class ConsumerRunner implements Runnable {

        private Log log = LogFactory.getLog(ConsumerRunner.class);

        private ConsumerConnector consumerConnector;

        private KafkaStream kafkaStream;

        public ConsumerRunner(ConsumerConnector consumerConnector, KafkaStream kafkaStream) {
            this.consumerConnector = consumerConnector;
            this.kafkaStream = kafkaStream;
        }

        public void run() {
            final ConsumerIterator<byte[], byte[]> consumerIterator = kafkaStream.iterator();

            while (true) {
                boolean hasNext = false;
                try {
                    hasNext = consumerIterator.hasNext();
                } catch (ConsumerTimeoutException e) {
                    log.warn(e);
                }
                if (hasNext) {
                    //是hasNext抛出异常,而不是next抛出
                    final MessageAndMetadata<byte[], byte[]> next = consumerIterator.next();
                    final byte[] message = next.message();
                    RuleTaskJobInfo ruleTaskJobInfo = null;
                    try {
                        ruleTaskJobInfo = (RuleTaskJobInfo) BeanUtils.byte2Obj(message);

                        new RuleTaskRunner(ruleTaskJobInfo).run();
                    } catch (Exception e) {
                        log.error("执行规则任务失败", e);
                    } finally {
                        consumerConnector.commitOffsets();
                        log.info(String.format("%s 同步offset成功%n", Thread.currentThread().getName()));
                    }
                }
            }
        }

    }


}
