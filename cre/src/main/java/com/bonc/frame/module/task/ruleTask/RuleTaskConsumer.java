/*
package com.bonc.frame.module.task.ruleTask;

import com.bonc.frame.config.Config;
import com.bonc.frame.module.kafka.ConsumerRecordHandler;
import com.bonc.frame.module.kafka.SimpleConsumer;
import com.bonc.frame.module.threadpool.ThreadFactoryImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

*/
/**
 * @author yedunyao
 * @date 2019/6/4 17:35
 *//*

public class RuleTaskConsumer implements Runnable {

    private Log log = LogFactory.getLog(RuleTaskConsumer.class);

    private SimpleConsumer simpleConsumer;

    public RuleTaskConsumer() {
        final int ruleTaskConsumerPoolSize = Config.RULE_TASK_CONSUMER_POOL_SIZE;
        log.info("ruleTaskExecutor 线程池大小: " + ruleTaskConsumerPoolSize);
        final ExecutorService executorService = Executors.newFixedThreadPool(ruleTaskConsumerPoolSize,
                new ThreadFactoryImpl("RuleTaskExecutor"));
        final ConsumerRecordHandler ruleTaskHandler = new RuleTaskHandler(executorService);
        simpleConsumer = new SimpleConsumer(Config.CONSUMER_PROPERTIES, Config.TASK_TOPIC, ruleTaskHandler);
    }

    @Override
    public void run() {
        simpleConsumer.subscribe();
    }
}
*/
