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
 * 扫描离线任务
 *
 * @author yedunyao
 * @date 2019/5/31 16:09
 */
public class ScanOfflineTask implements Runnable {

    private Log log = LogFactory.getLog(ScanOfflineTask.class);

    private RuleTaskService ruleTaskService;

    private TaskService taskService;

    private boolean init = true;

    public ScanOfflineTask() {
        this.ruleTaskService = (RuleTaskService) SpringUtils.getBean("ruleTaskService");
        this.taskService = (TaskService) SpringUtils.getBean("taskService");
    }

    public void run() {
        int whileCount = 1;             // 循环次数
        int runningTaskCount = 0;       // 已提交的任务个数
        int curRunningTaskCount = 0;    // 当前已提交的任务个数

        final String currentThreadName = Thread.currentThread().getName();

        while (true) {
            log.info(String.format("[CRE-%s] 开始第%d次获取离线任务", currentThreadName, whileCount++));

            try {
                // 扫表
                final List<RuleTask> ruleTasks = scanNeedRunTask();
                if (ruleTasks != null && !ruleTasks.isEmpty()) {
                    final int ruleTasksSize = ruleTasks.size();
                    log.info(String.format("[CRE-%s] 本次循环获取的待运行的任务（个）: %d",
                            currentThreadName, ruleTasksSize));

                    for (RuleTask ruleTask : ruleTasks) {
                        // 只处理取模运算得到的任务
                        if (ruleTask.getNum() % Config.SERVER_NUM == Config.SERVER_INDEX) {
                            final String taskId = ruleTask.getTaskId();
                            ruleTaskService.runningTask(ruleTask, null);
                            // 创建定时任务
                            try {
                                taskService.addCronJob(ruleTask);
                                runningTaskCount++;
                                curRunningTaskCount++;
                            } catch (Exception e) {
                                log.error(String.format("[CRE-%s] 创建定时任务失败", currentThreadName), e);
                                ruleTaskService.exceptionTask(ruleTask, e, null);
                            }
                        }
                    } // end for

                    log.info(String.format("[CRE-%s] 本次循环已创建的定时任务（个）: %d", currentThreadName, curRunningTaskCount));
                    log.info(String.format("[CRE-%s] 所有已创建的定时任务（个）: %d", currentThreadName, runningTaskCount));

                } // end if fetch task isEmpty
            } catch (Exception e) {
                log.error(String.format("[CRE-%s] 扫描待运行定时任务失败", currentThreadName), e);
            }

            try {
                TimeUnit.MINUTES.sleep(Config.OFFLINE_TASK_SCAN_INTERVAL);
            } catch (InterruptedException e) {

            }

            curRunningTaskCount = 0;
        } // end while

    }

    private List<RuleTask> scanNeedRunTask() {
        if (isInit()) {
            // 避免宕机情况
            setInit(false);
            return ruleTaskService.getAllNeedRunTask();
        } else {
            return ruleTaskService.getAllReadyTask();
        }
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }
}
