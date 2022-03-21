/*
package com.bonc.frame.module.task.ruleTask;

import com.bonc.frame.module.kafka.ConsumerRecordHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

*/
/**
 * @author yedunyao
 * @date 2019/6/4 17:44
 *//*

public class RuleTaskHandler implements ConsumerRecordHandler {

    private Executor executor;

    public RuleTaskHandler(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void handler(ConsumerRecord<String, Object> record, CountDownLatch latch) {
        // 多线程处理任务
        final RuleTaskJobInfo value = (RuleTaskJobInfo) record.value();
        executor.execute(new RuleTaskRunner(value, latch));
    }
}
*/
