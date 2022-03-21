package com.bonc.frame.module.kafka.v0821;

import com.bonc.frame.config.Config;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author yedunyao
 * @date 2019/6/4 17:35
 */
public class RuleTaskConsumer implements Runnable {

    private Log log = LogFactory.getLog(RuleTaskConsumer.class);

    private MyConsumer myConsumer;

    public RuleTaskConsumer() {
        /*final int ruleTaskConsumerPoolSize = Config.RULE_TASK_CONSUMER_POOL_SIZE;
        log.info("ruleTaskExecutor 线程池大小: " + ruleTaskConsumerPoolSize);
        final ExecutorService executorService = Executors.newFixedThreadPool(ruleTaskConsumerPoolSize,
                new ThreadFactoryImpl("RuleTaskExecutor"));*/
        myConsumer = new MyConsumer(Config.CONSUMER_PROPERTIES, Config.TASK_TOPIC);
    }

    @Override
    public void run() {
        myConsumer.subscribe();
    }
}
