package com.bonc.frame.module.kafka.v0821;

import com.bonc.frame.module.task.ruleTask.RuleTaskJobInfo;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Closeable;
import java.util.Properties;

/**
 * @author yedunyao
 * @date 2019/6/20 11:47
 */
public class MyProducer implements Closeable {

    private static final String NAME_PREFFIX = "producer-";

    private Log log = LogFactory.getLog(MyProducer.class);

    private Producer producer;

    private Properties props;

    private String topic;

    private String name;

    public MyProducer(Properties props, String topic, String name) {
        this.props = props;
        this.topic = topic;
        this.name = NAME_PREFFIX + name;

        //创建producer 对象
        producer = new Producer<String, Object>(new ProducerConfig(props));
    }

    public void sendMessage(RuleTaskJobInfo ruleTaskJobInfo) throws Exception {
        sendMessage(ruleTaskJobInfo.getFinalKey(), ruleTaskJobInfo);
    }

    public void sendMessage(String key, Object message) throws Exception {
        producer.send(new KeyedMessage<>(this.topic, key, message));
        log.info(name + " 发送消息成功，key: " + key);
    }

    public void close() {
        if (this.producer != null) {
            this.producer.close();
            log.info(name + " closed.");
        }
    }

}
