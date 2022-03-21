/*
package com.bonc.frame.module.kafka;

import com.bonc.frame.config.Config;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

*/
/**
 * @author yedunyao
 * @date 2019/5/30 15:08
 *//*

public class SimpleConsumer {

    private static final String NAME_PREFFIX = "consumer-";
    private static final String DEFAULT_CONSUMER_NAME = NAME_PREFFIX + Config.SERVER_INDEX;

    private static final String AUTO_COMMIT_KEY = "enable.auto.commit";
    private static final String GROUP_KEY = "group.id";
    private static final String MAX_POLL_RECORDS = "max.poll.records";
    private static final String MAX_POLL_WAIT_MS = "cre.max.poll.wait.ms";

    private Log log = LogFactory.getLog(SimpleProducer.class);

    private Properties props;

    private List<String> topics;

    private String group;

    private String name;

    private boolean autoCommit;

    private int batchSize;

    private long pollWait = 1000L;

    private KafkaConsumer<String, Object> consumer;

    private ConsumerRecordHandler recordHandler;


    public SimpleConsumer(Properties props, String topic, String group,
                          String name, ConsumerRecordHandler recordHandler) {
        this(props, Arrays.asList(topic), group, name, true, recordHandler);
    }

    public SimpleConsumer(Properties props, String topic, String group,
                          String name, boolean autoCommit, ConsumerRecordHandler recordHandler) {
        this(props, Arrays.asList(topic), group, name, autoCommit, recordHandler);
    }

    public SimpleConsumer(Properties props, String topic,
                          ConsumerRecordHandler recordHandler) {
        this(props, Arrays.asList(topic), recordHandler);
    }

    public SimpleConsumer(Properties props, List<String> topics,
                          ConsumerRecordHandler recordHandler) {
        this.props = props;
        this.topics = topics;
        this.name = DEFAULT_CONSUMER_NAME;
        this.autoCommit = Boolean.valueOf(props.getProperty(AUTO_COMMIT_KEY, "false"));
        batchSize = Integer.valueOf(props.getProperty(MAX_POLL_RECORDS));
        pollWait = Long.valueOf(props.getProperty(MAX_POLL_WAIT_MS, "1000"));
        this.recordHandler = recordHandler;
        consumer = new KafkaConsumer<>(props);
    }

    public SimpleConsumer(Properties props, List<String> topics, String group,
                          String name, boolean autoCommit, ConsumerRecordHandler recordHandler) {
        this.props = props;
        this.topics = topics;
        this.group = group;
        this.name = NAME_PREFFIX + name;
        this.autoCommit = autoCommit;
        props.put(GROUP_KEY, group);
        props.put(AUTO_COMMIT_KEY, autoCommit);
        batchSize = Integer.valueOf(props.getProperty(MAX_POLL_RECORDS));
        pollWait = Long.valueOf(props.getProperty(MAX_POLL_WAIT_MS, "1000"));
        this.recordHandler = recordHandler;
        consumer = new KafkaConsumer<>(props);
    }

    public void subscribe() {
        consumer.subscribe(topics);
//        List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
        try {
            while (true) {
                ConsumerRecords<String, Object> records = consumer.poll(pollWait);
                final int count = records.count();
                if (count > 0) {
                    log.info(String.format("%s poll records count: %d\n", name, count));
                    CountDownLatch latch = new CountDownLatch(count);
                    for (ConsumerRecord<String, Object> record : records) {
                        log.info(String.format("%s poll record [offset = %d, key = %s]%n",
                                name, record.offset(), record.key()));

                        if (log.isDebugEnabled()) {
                            log.debug("订阅任务信息：" + record.value());
                        }
                        recordHandler.handler(record, latch);
                    }

                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        log.warn("等待规则执行结束失败", e);
                    }

                    if (autoCommit == false) {
                        consumer.commitSync();
                        log.info(String.format("%s 同步offset成功%n", name));
                    }
                }
            }
        } finally {
            consumer.close();
        }
    }
}
*/
