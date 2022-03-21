package com.bonc.frame.module.quartz;

import com.bonc.frame.entity.task.RuleTask;
import com.bonc.frame.module.task.offlineTask.OfflineTaskJob;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * @author yedunyao
 * @date 2019/5/28 9:57
 */

@Service
public class TaskService {

    private Log log = LogFactory.getLog(TaskService.class);

    @Autowired
    private Scheduler scheduler;

    public void addCronJob(RuleTask ruleTask) throws SchedulerException {
        JobKey jobKey = new JobKey(ruleTask.getTaskId(), Scheduler.DEFAULT_GROUP);
        boolean flag = isExistJob(jobKey);
        if (flag) {
            log.warn("已存在该任务，jobName：" + ruleTask.getTaskId() + " jobGroupName:" + Scheduler.DEFAULT_GROUP);
            return;
        }

        boolean checkResult = isValidExpression(ruleTask.getCron());
        if (!checkResult) {
            log.error("非法的cron表达式，cron：" + ruleTask.getCron());
            throw new IllegalArgumentException("非法的cron表达式，cron：" + ruleTask.getCron());
        }

        try {
//            final Map<String, String> describe = RuleTask.covertToMap(ruleTask);
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("taskId", ruleTask.getTaskId());
//            jobDataMap.putAll(describe);

            JobDetail jobDetail = JobBuilder.newJob(OfflineTaskJob.class)
                    .withIdentity(jobKey)
                    .usingJobData(jobDataMap)
                    .build();

            // FIXME: 次数
            TriggerKey triggerKey = new TriggerKey(ruleTask.getTaskId(), Scheduler.DEFAULT_GROUP);
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(ruleTask.getCron()))
                    .build();

            CronTriggerImpl cronTrigger = (CronTriggerImpl) trigger;
            cronTrigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
            scheduler.scheduleJob(jobDetail, cronTrigger);
            scheduler.start();
        } catch (Exception e) {
            throw new IllegalStateException("创建具体定时任务失败", e);
        }
    }

    /**
     * 新添加一个调度作业
     *
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     * @param jobClazz
     * @param cron
     * @param dataMap
     * @return
     * @throws SchedulerException
     */
    public boolean addCronJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName,
                              Class<? extends Job> jobClazz, String cron, JobDataMap dataMap) throws SchedulerException {

        log.info("添加一个job");
        JobKey jobKey = new JobKey(jobName, jobGroupName);
        log.info("校验job是否存在");
        boolean flag = isExistJob(jobKey);
        if (flag) {
            log.error("已存在该任务，jobName：" + jobName + " jobGroupName:" + jobGroupName);
            return false;
        }

        boolean checkResult = isValidExpression(cron);
        if (!checkResult) {
            log.error("非法的cron表达式，cron：" + cron);
            return false;
        }

        JobDetail jobDetail = JobBuilder.newJob(jobClazz).withIdentity(jobKey).usingJobData(dataMap).build();
        TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroupName);
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        CronTriggerImpl cronTrigger = (CronTriggerImpl) trigger;
        cronTrigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        scheduler.scheduleJob(jobDetail, cronTrigger);
        scheduler.start();
        return true;
    }


    /**
     * 检查一个job是否存在
     *
     * @param jobKey jobKey
     * @return true存在，false不存在
     * @throws SchedulerException
     */

    public boolean isExistJob(JobKey jobKey) throws SchedulerException {
        return CollectionUtils.isNotEmpty(scheduler.getTriggersOfJob(jobKey));
    }


    /**
     * 是否存在job
     *
     * @param jobName      jobName
     * @param jobGroupName jobGroupName
     * @return
     * @throws SchedulerException
     */
    public boolean isExistJob(String jobName, String jobGroupName) throws SchedulerException {
        return isExistJob(new JobKey(jobName, jobGroupName));
    }

    public boolean isExistJob(String jobName) throws SchedulerException {
        return isExistJob(new JobKey(jobName, Scheduler.DEFAULT_GROUP));
    }


    /**
     * 判断是否是正确的cron quarz 表达式
     *
     * @param cronExpression cron表达式
     * @return boolean
     */

    public boolean isValidExpression(String cronExpression) {
        return cronExpression != null && CronExpression.isValidExpression(cronExpression);
    }


    public boolean modifyJobTriggerTime(String triggerName, String triggerGroupName, String cron) throws SchedulerException {

        boolean flag = isValidExpression(cron);
        if (flag) {
            log.error("不是一个正确的cron表达式");
            return false;
        }

        Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupStartsWith(triggerGroupName));
        if (CollectionUtils.isEmpty(triggerKeys)) {
            log.error("找不到触发器");
            return false;
        }
        for (TriggerKey triggerKey : triggerKeys) {
            scheduler.unscheduleJob(triggerKey);
        }

        TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroupName);

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(new TriggerKey(triggerName, triggerGroupName))
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();
        CronTriggerImpl cronTrigger = (CronTriggerImpl) trigger;
        cronTrigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        scheduler.rescheduleJob(triggerKey, cronTrigger);
        return true;
    }


    public boolean removeJob(String jobName) throws SchedulerException {
        return removeJob(jobName, Scheduler.DEFAULT_GROUP);
    }

    public boolean removeJob(String jobName, String jobGroupName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, jobGroupName);
        return scheduler.deleteJob(jobKey);
    }


    public boolean pauseJob(String jobName, String groupName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, groupName);
        scheduler.pauseJob(jobKey);
        return true;
    }


    public boolean resumeJob(String jobName, String groupName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, groupName);
        scheduler.resumeJob(jobKey);
        return true;
    }

    public boolean shutDownScheduler() throws SchedulerException {
        scheduler.shutdown(true);
        return true;
    }


    public static List<Date> getNextFireTimeDate(String cron, Integer numTimes) {

        List<Date> dates = null;
        try {
            CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
            cronTriggerImpl.setCronExpression(cron);
            dates = TriggerUtils.computeFireTimes(cronTriggerImpl, null, numTimes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dates;
    }


    public List<Integer> getCurrentExecutingTaskIds() {
        List<Integer> taskIds = Lists.newArrayList();
        try {
            List<JobExecutionContext> jobContexts = scheduler.getCurrentlyExecutingJobs();
            if (CollectionUtils.isNotEmpty(jobContexts)) {
                for (JobExecutionContext context : jobContexts) {
                    JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
                    String taskId = (String) jobDataMap.get("");
                    taskIds.add(Integer.valueOf(taskId));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taskIds;
    }


    public String getTriggerState(String triggerName, String triggerGroup) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
        Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
        return triggerState.name();

    }

}
