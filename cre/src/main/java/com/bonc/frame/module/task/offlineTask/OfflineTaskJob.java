package com.bonc.frame.module.task.offlineTask;

import com.bonc.frame.entity.task.RuleTask;
import com.bonc.frame.service.task.RuleTaskLogService;
import com.bonc.frame.service.task.RuleTaskService;
import com.bonc.frame.util.SpringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Map;

/**
 * @DisallowConcurrentExecution 不能并发执行同一个Job Definition,任务执行时间过长，下一个周期不会再执行此任务
 * @PersistJobDataAfterExecution 表示当正常执行完Job后, JobDataMap中的数据应该被改动, 以被下一次调用时用。
 *
 * @author yedunyao
 * @date 2019/5/31 15:53
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class OfflineTaskJob extends QuartzJobBean {

    private Log log = LogFactory.getLog(OfflineTaskJob.class);

    private RuleTaskService ruleTaskService;

    private RuleTaskLogService ruleTaskLogService;

    public OfflineTaskJob() {
        super();
        this.ruleTaskService = (RuleTaskService) SpringUtils.getBean("ruleTaskService");
        ruleTaskLogService = (RuleTaskLogService) SpringUtils.getBean("ruleTaskLogService");
    }

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
            final String taskId = (String) wrappedMap.get("taskId");
            ruleTask = ruleTaskService.selectByPrimaryKey(taskId);
//            ruleTask = RuleTask.convertToObj(wrappedMap);
            log.info(String.format("定时任务开始执行，任务信息：[%s]", ruleTask));
            new ScanRuleTask(ruleTask).pagedScan();
        } catch (Exception e) {
            // 记录失败日志
            log.error("定时任务执行失败", e);
            if (ruleTask != null) {
                ruleTaskService.exceptionTask(ruleTask, e, null);
            }
        }
    }

}
