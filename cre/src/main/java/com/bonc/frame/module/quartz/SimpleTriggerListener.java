package com.bonc.frame.module.quartz;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

/**
 * @author yedunyao
 * @date 2019/5/28 10:28
 */
public class SimpleTriggerListener implements TriggerListener {

    private Log logger = LogFactory.getLog(SimpleTriggerListener.class);

    @Override
    public String getName() {
        return "SimpleTriggerListener";
    }


    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {

    }

    /**
     * 在 Trigger 触发后，Job 将要被执行时由 Scheduler 调用这个方法。
     * 假如这个方法返回 true，这个 Job 将不会为此次 Trigger 触发而得到执行。
     */
    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
       /* logger.info("trigger开始执行,开始获取redis锁");
        JobKey jobKey = trigger.getJobKey();
        String lockKey =jobKey.getGroup()+":"+jobKey.getName();
        boolean flag = distributedLock.lock(lockKey);
        if(!flag){
            //获取到锁了
            logger.warn("没获取到锁，本次调度结束");
            return true;
        }
        logger.info("获取到锁了，继续往下执行");*/
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {

    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        /*logger.info("调度执行完毕，开始释放锁");
        RedisDistributedLock distributedLock = (RedisDistributedLock) SpringUtils.getBean(RedisDistributedLock.class);
        JobKey jobKey = trigger.getJobKey();
        String lockKey =jobKey.getGroup()+":"+jobKey.getName();
        distributedLock.unLock(lockKey);
        logger.error("本次作业执行结束");*/
    }

}
