package com.bonc.frame.module.task.offlineTask;

import com.bonc.frame.config.Config;
import com.bonc.frame.entity.task.RuleTask;
import com.bonc.frame.module.quartz.TaskService;
import com.bonc.frame.service.task.RuleTaskService;
import com.bonc.frame.util.SpringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 扫描停用的离线任务
 *
 * @author yedunyao
 * @date 2019/5/31 16:09
 */
public class ScanNeedStopOfflineTask implements Runnable {

    private Log log = LogFactory.getLog(ScanNeedStopOfflineTask.class);

    private RuleTaskService ruleTaskService;

    private TaskService taskService;

    public ScanNeedStopOfflineTask() {
        this.ruleTaskService = (RuleTaskService) SpringUtils.getBean("ruleTaskService");
        this.taskService = (TaskService) SpringUtils.getBean("taskService");
    }

    public void run() {
        int whileCount = 1;             // 循环次数

        final String currentThreadName = Thread.currentThread().getName();

        while (true) {
            log.info(String.format("[CRE-%s] 开始第%d次获取停用离线任务", currentThreadName, whileCount++));

            try {
                final List<RuleTask> needStopTask = scanNeedStopTask();
                if (needStopTask != null && !needStopTask.isEmpty()) {
                    final int ruleTasksSize = needStopTask.size();
                    log.info(String.format("[CRE-%s] 本次循环获取的待停止的任务（个）: %d",
                            currentThreadName, ruleTasksSize));
                    for (RuleTask ruleTask : needStopTask) {
                        ruleTaskService.processStopTaskWithQuartz(ruleTask);
                    }
                }
            } catch (Exception e) {
                log.error(String.format("[CRE-%s] 扫描停用定时任务失败", currentThreadName), e);
            }

            try {
                TimeUnit.MINUTES.sleep(Config.OFFLINE_TASK_SCAN_STOP_INTERVAL);
            } catch (InterruptedException e) {

            }

        } // end while

    }

    private List<RuleTask> scanNeedStopTask() {
        final List<RuleTask> allNeedStopTask = ruleTaskService.getAllNeedStopTask();
        return allNeedStopTask;
    }

}
