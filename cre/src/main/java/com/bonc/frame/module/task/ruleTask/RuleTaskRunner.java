/*
package com.bonc.frame.module.task.ruleTask;

import com.bonc.frame.engine.EngineManager;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.task.RuleTask;
import com.bonc.frame.entity.task.RuleTaskLog;
import com.bonc.frame.module.db.DbOperatorFactory;
import com.bonc.frame.service.datasource.DataSourceService;
import com.bonc.frame.service.task.RuleTaskLogService;
import com.bonc.frame.service.task.RuleTaskService;
import com.bonc.frame.util.SpringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

*/
/**
 * 规则任务处理
 *
 * @author yedunyao
 * @date 2019/6/4 17:52
 *//*

public class RuleTaskRunner implements Runnable {

    private Log log = LogFactory.getLog(getClass());

    private CountDownLatch latch;

    private RuleTaskService ruleTaskService;

    private RuleTaskJobInfo ruleTaskJobInfo;

    private RuleTaskLogService ruleTaskLogService;

    public RuleTaskRunner(RuleTaskJobInfo ruleTaskJobInfo, CountDownLatch latch) {
        this.latch = latch;
        this.ruleTaskJobInfo = ruleTaskJobInfo;
        this.ruleTaskService = (RuleTaskService) SpringUtils.getBean("ruleTaskService");
        ruleTaskLogService = (RuleTaskLogService) SpringUtils.getBean("ruleTaskLogService");
    }

    @Override
    public void run() {
        log.info("开始执行规则任务，任务信息：" + ruleTaskJobInfo);
        final RuleTaskLog ruleTaskLog = new RuleTaskLog(ruleTaskJobInfo);

        try {
            if (ruleTaskLogService.isExists(ruleTaskLog)) {
                log.warn("该任务已被执行，将跳过执行，任务信息：" + ruleTaskJobInfo);
                return;
            }

            ruleTaskLog.setStatus(RuleTaskLog.RUNNING);
            ruleTaskLogService.insert(ruleTaskLog);

            final String taskId = ruleTaskJobInfo.getTaskId();
            final RuleTask ruleTask = ruleTaskService.selectByPrimaryKey(taskId);
            executeRule(ruleTask);

            save(ruleTask);

            ruleTaskLog.setStatus(RuleTaskLog.FINISH);
            ruleTaskLogService.update(ruleTaskLog);
        } catch (Exception e) {
            log.error("执行规则失败，任务信息：" + ruleTaskJobInfo, e);
            ruleTaskLog.setStatus(RuleTaskLog.EXCEPTION);
            ruleTaskLog.setMessage(e.getMessage());
            ruleTaskLogService.update(ruleTaskLog);
        } finally {
            latch.countDown();
        }
    }

    public void executeRule(RuleTask ruleTask) throws Exception {
        final Map<String, Object> data = this.ruleTaskJobInfo.getData();
        log.info("开始进行规则计算，任务信息：" + ruleTaskJobInfo);
        final String folderId = ruleTask.getPackageId();
        final String ruleId = ruleTask.getRuleId();

        // 执行
        EngineManager.getInstance().executeRule(folderId, ruleId,
                data, false, true);
    }

    public void save(RuleTask ruleTask) throws Exception {
        final Map<String, Object> data = this.ruleTaskJobInfo.getData();
        log.info("保存规则执行结果，任务信息：" + ruleTaskJobInfo);

        final String outputDbId = ruleTask.getOutputDbId();
        final String existTableSql = ruleTask.getExistTableSql();
        final String createTableSql = ruleTask.getCreateTableSql();

        DataSourceService dataSourceService = (DataSourceService) SpringUtils.getBean("dataSourceService");
        final DataSource dbInfo = dataSourceService.selectByPrimaryKey(outputDbId);

        final Object keyColunmVal = ruleTaskJobInfo.getJobKey();   // 输入表主键的值
        final String inserSql = ruleTaskService.convertInserSql(ruleTask, dbInfo, data, keyColunmVal);
        log.info("规则执行后的插入sql：" + inserSql + " 任务信息：" + ruleTaskJobInfo);
        DbOperatorFactory.saveAndCreateTable(existTableSql, createTableSql, inserSql, dbInfo);
    }
}*/
