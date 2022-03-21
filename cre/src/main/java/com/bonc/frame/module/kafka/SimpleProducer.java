/*
package com.bonc.frame.module.kafka;

import com.bonc.frame.module.task.ruleTask.RuleTaskJobInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.Closeable;
import java.util.Properties;

*/
/**
 * @author yedunyao
 * @date 2019/5/30 15:13
 *//*

public class SimpleProducer implements Closeable {

    private static final String NAME_PREFFIX = "producer-";

    private Log log = LogFactory.getLog(SimpleProducer.class);

    private Properties props;

    private String topic;

    private String name;

    private final Producer<String, Object> producer;

    public SimpleProducer(Properties props, String topic, String name) {
        this.props = props;
        this.topic = topic;
        this.name = NAME_PREFFIX + name;
        producer = new KafkaProducer<>(props);
    }

    public void sendMessage(RuleTaskJobInfo ruleTaskJobInfo) throws Exception {
        sendMessage(ruleTaskJobInfo.getFinalKey(), ruleTaskJobInfo);
    }

    public void sendMessage(String key, Object message) throws Exception {
        // 阻塞发送数据
        final RecordMetadata metadata = this.producer.send(
                new ProducerRecord<>(topic, key, message)
        ).get();
        log.info(name + " the offset of the record we just sent is: " + metadata.offset());

        */
/*this.producer.send(new ProducerRecord<>(topic, key, message), new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception != null) {
                    log.warn(exception);
                }
                if (metadata != null) {
                    log.info(name + " the offset of the record we just sent is: " + metadata.offset());
                }
            }
        });*//*

    }

    public void close() {
        if (this.producer != null) {
            this.producer.close();
            log.info(name + " closed.");
        }
    }

}
*/
