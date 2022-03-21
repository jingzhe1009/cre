package com.bonc.frame.module.kafka.v0821;

import com.bonc.frame.config.Config;
import com.bonc.frame.engine.EngineManager;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.task.RuleTask;
import com.bonc.frame.entity.task.RuleTaskLog;
import com.bonc.frame.module.db.DbOperatorFactory;
import com.bonc.frame.module.task.ruleTask.RuleTaskJobInfo;
import com.bonc.frame.service.datasource.DataSourceService;
import com.bonc.frame.service.task.RuleTaskLogService;
import com.bonc.frame.service.task.RuleTaskService;
import com.bonc.frame.util.SpringUtils;
import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.executor.context.impl.ModelExecutorType;
import com.bonc.framework.rule.kpi.KpiConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * 规则任务处理
 *
 * @author yedunyao
 * @date 2019/6/4 17:52
 */
public class RuleTaskRunner {

    private static Log log = LogFactory.getLog(RuleTaskRunner.class);

    private RuleTaskService ruleTaskService;

    private RuleTaskJobInfo ruleTaskJobInfo;

    private RuleTaskLogService ruleTaskLogService;

    public RuleTaskRunner(RuleTaskJobInfo ruleTaskJobInfo) {
        this.ruleTaskJobInfo = ruleTaskJobInfo;
        this.ruleTaskService = (RuleTaskService) SpringUtils.getBean("ruleTaskService");
        ruleTaskLogService = (RuleTaskLogService) SpringUtils.getBean("ruleTaskLogService");
    }

    public void run() {
        log.info("开始执行规则任务，任务信息：" + ruleTaskJobInfo);
        final RuleTaskLog ruleTaskLog = new RuleTaskLog(ruleTaskJobInfo);

        try {
            final String taskId = ruleTaskJobInfo.getTaskId();
            final RuleTask ruleTask = ruleTaskService.selectByPrimaryKey(taskId);
            //FIXME: 暂未实现绝对的执行一次,isExists和插入日志操作不在同一事务内 isExists 需要添加分布式锁
            //仅执行一次 并且 判断日志是否存在 如果是异常的任务同样认为不存在
            if (ruleTask.getIsAllowedRepeat().equals("0") && ruleTaskLogService.isExists(ruleTaskLog)) {
                log.warn("该任务已被执行，将跳过执行，任务信息：" + ruleTaskJobInfo);
                return;
            }

            // 执行成功再记录日志
            ruleTaskLog.setStatus(RuleTaskLog.RUNNING);
            ruleTaskLogService.upsert(ruleTaskLog);

            // 执行任务
            executeRule(ruleTask);

            save(ruleTask);

            ruleTaskLog.setStatus(RuleTaskLog.FINISH);
            ruleTaskLogService.upsert(ruleTaskLog);
        } catch (Exception e) {
            log.error("执行规则失败，任务信息：" + ruleTaskJobInfo, e);
            ruleTaskLog.setStatus(RuleTaskLog.EXCEPTION);
            ruleTaskLog.setMessage(e.getMessage());
            ruleTaskLogService.upsert(ruleTaskLog);
        }
    }

    public void executeRule(RuleTask ruleTask) throws Exception {
        final Map<String, Object> data = this.ruleTaskJobInfo.getData();
        log.info("开始进行规则计算，任务信息：" + ruleTaskJobInfo);
        if (log.isDebugEnabled()) {
            log.debug("开始进行规则计算，任务信息：" + ruleTaskJobInfo + "，执行参数：" + ruleTaskJobInfo.getData());
        }

        final String folderId = ruleTask.getPackageId();
        final String ruleId = ruleTask.getRuleId();

        if (StringUtils.isBlank(ruleId)) {
            throw new IllegalArgumentException("参数[ruleId]不能为空");
        }
        if (StringUtils.isBlank(folderId)) {
            throw new IllegalArgumentException("参数[folderId]不能为空");
        }

        // 渠道号
        final ConsumerInfo consumerInfo = new ConsumerInfo(Config.LOG_RULE_TASK_CONSUMERID,
                null, null);
        // 执行
        //TODO : 指标策略的粒度:如果要做的话 , 应该控制在 指标-模型的引用上;这样的话,现在指标获取的策略得重新进行修改
        EngineManager.getInstance().executeRule(folderId, ruleId,
                data, false, true, KpiConstant.LOADER_KPI_STRATEGY_TYPE_RUNTIME_EVERY, ModelExecutorType.TASK, null, consumerInfo);
    }

    // FIXME: 批量执行保存
    public void save(RuleTask ruleTask) throws Exception {
        final Map<String, Object> data = this.ruleTaskJobInfo.getData();
        log.info("保存规则执行结果，任务信息：" + ruleTaskJobInfo);
        if (log.isDebugEnabled()) {
            log.debug("保存规则执行结果，任务信息：" + ruleTaskJobInfo + "，执行结果：" + ruleTaskJobInfo.getData());
        }

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
}