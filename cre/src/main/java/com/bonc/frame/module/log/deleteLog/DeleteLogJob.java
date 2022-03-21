package com.bonc.frame.module.log.deleteLog;

import com.bonc.frame.entity.task.RuleTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/1/2 19:32
 */
public class DeleteLogJob extends QuartzJobBean {
    private Log log = LogFactory.getLog(DeleteLogJob.class);

    private DeleteLogTask deleteLogTask = new DeleteLogTask();

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        RuleTask ruleTask = null;

        // 统计上一任务周期之前完成的数据量
//        ruleTaskLogService

        try {
            // 重新获取RuleTask，避免重复执行
            final Map<String, Object> wrappedMap = jobDataMap.getWrappedMap();
            String day = wrappedMap.get("day").toString();
            log.debug("定时删除模型日志:" + wrappedMap);
//            ruleTask = RuleTask.convertToObj(wrappedMap);
//            log.info(String.format("开始定时删除任务：" + "删除执行日志"));
//
//            log.info(String.format("开始定时删除任务：" + "删除执行日志详情"));
//            log.info(String.format("开始定时删除任务：" + "删除试算日志"));
            deleteLogTask.deleteLog(day);
        } catch (Exception e) {
            // 记录失败日志
            log.error("定时删除日志失败", e);

        }
    }
}
