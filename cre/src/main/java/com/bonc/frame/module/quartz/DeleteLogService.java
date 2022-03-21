package com.bonc.frame.module.quartz;

import com.bonc.frame.config.Config;
import com.bonc.frame.entity.task.RuleTask;
import com.bonc.frame.module.log.deleteLog.DeleteLogJob;
import com.bonc.frame.module.log.deleteLog.DeleteRuleTaskLogJob;
import com.bonc.frame.service.rulelog.ModelTestLogService;
import com.bonc.frame.service.rulelog.RuleLogService;
import com.bonc.frame.service.task.RuleTaskLogService;
import com.bonc.frame.service.task.RuleTaskService;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/1/2 16:43
 */
@Service
public class DeleteLogService {

    private Log log = LogFactory.getLog(DeleteLogService.class);
    @Autowired
    private RuleLogService ruleLogService;
    @Autowired
    private RuleTaskService ruleTaskService;
    @Autowired
    private RuleTaskLogService ruleTaskLogService;
    @Autowired
    private ModelTestLogService modelTestLogService;
    @Autowired
    private Scheduler scheduler;

    public void startDeleteRuleLog() {
//        JobKey jobKey = new JobKey("deleteLogname", "deleteLog");
        String cron = Config.CRE_TASK_DELETELOG_DATABASE_CRON;
        int day = Config.CRE_TASK_DELETELOG_DATABASE_DAY;

        boolean checkResult = isValidExpression(cron);
        if (!checkResult) {
            log.error("非法的cron表达式，cron：" + cron);
            throw new IllegalArgumentException("非法的cron表达式，cron：" + cron);
        }
        if (day < 0) {
            log.error("保留的日志天数,不合法:" + day);
            throw new IllegalArgumentException("保留的日志天数,不合法:" + day);
        }
        try {
//            final Map<String, String> describe = RuleTask.covertToMap(ruleTask);
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("jobName", "deleteLog");
            jobDataMap.put("cron", cron);
            jobDataMap.put("day", day);

//            jobDataMap.putAll(describe);

            JobDetail jobDetail = JobBuilder.newJob(DeleteLogJob.class)
//                    .withIdentity(jobKey)
                    .usingJobData(jobDataMap)
                    .build();

//            TriggerKey triggerKey = new TriggerKey("deleteLogname", "deleteLog");
            CronTrigger trigger = TriggerBuilder.newTrigger()
//                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .build();

            CronTriggerImpl cronTrigger = (CronTriggerImpl) trigger;
            cronTrigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
            scheduler.scheduleJob(jobDetail, cronTrigger);
            scheduler.start();
        } catch (Exception e) {
            throw new IllegalStateException("创建具体定时任务失败", e);
        }
    }

    public void startDeleteRuleTaskLog() {
//        JobKey jobKey = new JobKey("deleteLogname", "deleteLog");
        String cron = Config.CRE_TASK_DELETE_RULETASKLOG_DATABASE_CRON;
        int day = Config.CRE_TASK_DELETE_RULETASKLOG_DATABASE_DAY;

        boolean checkResult = isValidExpression(cron);
        if (!checkResult) {
            log.error("定时删除离线任务非法的cron表达式，cron：" + cron);
            throw new IllegalArgumentException("定时删除离线任务非法的cron表达式，cron：" + cron);
        }
        if (day < 0) {
            log.error("保留的离线任务的日志天数,不合法:" + day);
            throw new IllegalArgumentException("保留的离线任务的日志天数,不合法:" + day);
        }
        try {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("jobName", "deleteRuleTaskLog");
            jobDataMap.put("cron", cron);
            jobDataMap.put("day", day);


            JobDetail jobDetail = JobBuilder.newJob(DeleteRuleTaskLogJob.class)
//                    .withIdentity(jobKey)
                    .usingJobData(jobDataMap)
                    .build();

//            TriggerKey triggerKey = new TriggerKey("deleteLogname", "deleteLog");
            CronTrigger trigger = TriggerBuilder.newTrigger()
//                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .build();

            CronTriggerImpl cronTrigger = (CronTriggerImpl) trigger;
            cronTrigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
            scheduler.scheduleJob(jobDetail, cronTrigger);
            scheduler.start();
        } catch (Exception e) {
            throw new IllegalStateException("创建具体定时任务失败", e);
        }
    }

    private boolean isValidExpression(String cronExpression) {
        return cronExpression != null && CronExpression.isValidExpression(cronExpression);
    }

    public ResponseResult deleteRuleLogByDay(String day) {
        ruleLogService.deleteRuleLogByDay(day);
        return ResponseResult.createSuccessInfo();

    }

    public ResponseResult deleteRuleLogDetailByDay(String day) {
        ruleLogService.deleteRuleLogDetailByDay(day);
        return ResponseResult.createSuccessInfo();

    }

    public ResponseResult deleteRuleTrialLogByDay(String day) {
        modelTestLogService.deleteRuleTrialLogByDay(day);
        return ResponseResult.createSuccessInfo();
    }

    public ResponseResult deleteLog(String day) {
        log.info(String.format("定时删除任务：" + "删除执行日志"));
        ruleLogService.deleteRuleLogByDay(day);
        log.info(String.format("定时删除任务：" + "删除执行日志详情"));
        ruleLogService.deleteRuleLogDetailByDay(day);
        log.info(String.format("定时删除任务：" + "删除试算日志"));
        modelTestLogService.deleteRuleTrialLogByDay(day);
        return ResponseResult.createSuccessInfo();
    }

    public ResponseResult deleteRuleTaskLog(String day) {
        // 获取所有  允许重复的离线任务
        List<RuleTask> ruleTaskByProperty = ruleTaskService.getRuleTaskByPropertyNoAuth(null, null, null, null,
                null, null, RuleTask.IS_ALLOWED_REPEAT_ONLY_ONE);
        if (ruleTaskByProperty == null || ruleTaskByProperty.isEmpty()) {
            return ResponseResult.createSuccessInfo();
        }
        // 删除所有日志
        List<String> ruleTaskIdList = new ArrayList<>(ruleTaskByProperty.size());
        for (RuleTask ruleTask : ruleTaskByProperty) {
            if (ruleTask != null) {
                ruleTaskIdList.add(ruleTask.getTaskId());
            }
        }
        ruleTaskLogService.deleteByTaskIdListAndDay(ruleTaskIdList, day);

        return ResponseResult.createSuccessInfo();
    }
}
