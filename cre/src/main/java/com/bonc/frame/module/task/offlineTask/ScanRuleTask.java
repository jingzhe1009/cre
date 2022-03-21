package com.bonc.frame.module.task.offlineTask;

import com.bonc.frame.config.Config;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.task.ColumnMapping;
import com.bonc.frame.entity.task.RuleTask;
import com.bonc.frame.entity.task.RuleTaskLog;
import com.bonc.frame.entity.task.VariableMapping;
import com.bonc.frame.module.db.DbOperatorFactory;
import com.bonc.frame.module.db.operator.hbase.HbaseOperator;
import com.bonc.frame.module.db.operator.hbase.HbaseResult;
import com.bonc.frame.module.kafka.v0821.MyProducer;
import com.bonc.frame.module.task.ruleTask.RuleTaskJobInfo;
import com.bonc.frame.service.datasource.DataSourceService;
import com.bonc.frame.service.task.ColumnMappingService;
import com.bonc.frame.service.task.RuleTaskLogService;
import com.bonc.frame.service.task.RuleTaskService;
import com.bonc.frame.util.ResponseResult;
import com.bonc.frame.util.SpringUtils;
import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 扫描规则任务
 * 分页扫描
 *
 * @author yedunyao
 * @date 2019/5/31 16:13
 */
public class ScanRuleTask {

    private static Log log = LogFactory.getLog(ScanRuleTask.class);

    private RuleTask ruleTask;

    private DataSourceService dataSourceService;

    private RuleTaskService ruleTaskService;

    private ColumnMappingService columnMappingService;

    private RuleTaskLogService ruleTaskLogService;

    public ScanRuleTask(RuleTask ruleTask) {
        this.ruleTask = ruleTask;
        dataSourceService = (DataSourceService) SpringUtils.getBean("dataSourceService");
        ruleTaskService = (RuleTaskService) SpringUtils.getBean("ruleTaskService");
        columnMappingService = (ColumnMappingService) SpringUtils.getBean("columnMappingService");
        ruleTaskLogService = (RuleTaskLogService) SpringUtils.getBean("ruleTaskLogService");
    }

    public void pagedScan() throws Exception {
        log.info("开始执行离线任务，任务信息：[" + ruleTask.toString() + "]");
        if (!Config.OFFLINE_TASK_ENABLE) {
            log.warn("未开启离线任务扫描器");
            return;
        }
        final String taskId = ruleTask.getTaskId();
        try (final MyProducer simpleProducer = new MyProducer(
                Config.PRODUCER_PROPERTIES, Config.TASK_TOPIC, taskId
        )) {
            final String inputSql = ruleTask.getInputSql();
            final String inputDbId = ruleTask.getInputDbId();
            long scanOffset = ruleTask.getScanOffset();
            final long scanNextOffset = ruleTask.getScanNextOffset();
            final long scanMaxSize = ruleTask.getScanMaxSize();
            long scanPageSize = Config.RULE_TASK_SCAN_PAGE_SIZE;

            final String taskStatus = ruleTask.getTaskStatus();

            if (!RuleTask.RUNNING.equals(taskStatus)) {
                log.warn("非运行状态，本次扫描结束，任务信息：[" + ruleTask.toString() + "]");
                return;
            }

            // 注意：如果是重新执行暂停任务，scanNextOffset大于等于scanOffset，
            // 如果是重新执行停用的任务，则应将scanNextOffset置为0
            if (scanNextOffset > 0) {
//                 运行中状态突然宕机或暂停状态中恢复，继续跑
                scanOffset = Math.max(scanOffset, scanNextOffset);
            }

//            long needScanSize = scanMaxSize - scanOffset + 1;
            long needScanSize = scanMaxSize;    // 每个执行周期最大获取任务数
            if (needScanSize <= 0) {
                log.warn("扫描元数据表的最大记录个数不大于0，本次扫描结束，任务信息：[" + ruleTask.toString() + "]");
                return;
            }

            final DataSource dataSource = dataSourceService.selectByPrimaryKey(inputDbId);
            final String dbType = dataSource.getDbType();

            List<VariableMapping> variableMappings = null;
            ColumnMapping columnMapping = columnMappingService.selectByTaskId(taskId);
            if ("8".equals(dbType)) {   // hbse
                final ResponseResult detail = ruleTaskService.detail(taskId);
                RuleTask ruleTaskDetail = (RuleTask) detail.getData();
                final List<VariableMapping> inputVariableMappings = ruleTaskDetail.getInputVariableMappings();
                columnMapping = ruleTaskDetail.getColumnMapping();

                VariableMapping keyMapping = new VariableMapping();
                keyMapping.setColumnCode(columnMapping.getInputColumnCode());
                keyMapping.setTableCode(columnMapping.getInputTableCode());
                keyMapping.setVariableCode(columnMapping.getInputColumnCode());
                variableMappings = Lists.newLinkedList();
                variableMappings.add(keyMapping);
                variableMappings.addAll(inputVariableMappings);
            }


            while (needScanSize > 0) {
                log.info("开始扫描元数据表，任务信息：[" + ruleTask.toString() + "]");

                scanPageSize = Math.min(scanPageSize, needScanSize);

                try {
                    ResponseResult responseResult = null;
                    if ("8".equals(dbType)) {   // hbse
                        final HbaseOperator hbaseOperator = new HbaseOperator(dataSource.getDbId());
                        responseResult = hbaseOperator.pagedQuery(ruleTask.getInputTableCode(),
                                ruleTask.getInputTableWhereClause(), variableMappings,
                                ruleTask.getScanNextRowKey(), (int) scanPageSize);
                    } else {
                        responseResult = DbOperatorFactory.pagedQuery(inputSql, dataSource,
                                scanOffset, scanPageSize);
                    }

                    if (responseResult != null) {
                        List<Map<String, Object>> resultData;
                        if ("8".equals(dbType)) {   // hbse
                            final HbaseResult hbaseResult = (HbaseResult) responseResult.getData();
                            resultData = hbaseResult.getResult();
                        } else {
                            resultData = (List<Map<String, Object>>) responseResult.getData();
                        }

                        if (resultData == null || resultData.isEmpty()) {
                            log.info("没有数据，本次扫描结束，任务信息：[" + ruleTask.toString() + "]");
                            updateScanNext(ruleTask, dbType, scanOffset, responseResult);
                            break;
                        }

                        if (log.isTraceEnabled()) {
                            log.trace("扫描到的数据：" + resultData);
                        }

                        producerJob(resultData, taskId, columnMapping, simpleProducer);

                        if (resultData.size() < scanPageSize) {
                            // 没有足够的数据直接结束
                            log.info("没有更多的数据，本次扫描结束，任务信息：[" + ruleTask.toString() + "]");
                            scanOffset += resultData.size();
                            updateScanNext(ruleTask, dbType, scanOffset, responseResult);
                            break;
                        } else {
                            // 有足够的数据更新分页并等待下次扫描
                            needScanSize -= scanPageSize;
                            scanOffset += scanPageSize;
                            updateScanNext(ruleTask, dbType, scanOffset, responseResult);
                            successWait();
                        }
                    } else {
                        log.info("没有数据，本次扫描结束，任务信息：[" + ruleTask.toString() + "]");
                        updateScanNext(ruleTask, dbType, scanOffset, responseResult);
                        break;
                    }
                } catch (Exception e) {
                    log.error(e);
                    throw e;
                }
            }
        }
    }

    private void updateScanNext(RuleTask ruleTask, String dbType, long scanOffset, ResponseResult responseResult) {
        if ("8".equals(dbType)) {   // hbse
            if (responseResult != null) {
                final HbaseResult hbaseResult = (HbaseResult) responseResult.getData();
                if (hbaseResult != null) {
                    final String lastRow = hbaseResult.getLastRow();
                    if (lastRow != null) {
                        ruleTask.setScanNextRowKey(lastRow);
                        ruleTaskService.updateScanNextRowKeyByPrimaryKey(ruleTask);
                    }
                }
            }
        } else {
            ruleTask.setScanNextOffset(scanOffset);
            ruleTaskService.updateScanNextOffsetByPrimaryKey(ruleTask);
        }
    }

    private void producerJob(List<Map<String, Object>> resultData, String taskId,
                             ColumnMapping columnMapping, MyProducer simpleProducer) {
        // 由消费端去重解决重复跑问题（消息已经推送完，但没有及时更新scanNextOffset）
        // 推送到kafka
        for (Map<String, Object> data : resultData) {
            // 获取输入表的key值
//            final ColumnMapping columnMapping = columnMappingService.selectByTaskId(taskId);
            if (columnMapping == null) {
                throw new IllegalStateException("输入输出表字段映射不能为空，taskId: " + taskId);
            }
            final String inputColumnCode = columnMapping.getInputColumnCode();
            final Object keyColunmVal = data.get(inputColumnCode);

            final RuleTaskJobInfo ruleTaskJobInfo = new RuleTaskJobInfo(taskId, keyColunmVal, data);
            try {
                simpleProducer.sendMessage(ruleTaskJobInfo);
                if (log.isDebugEnabled()) {
                    log.debug("推送任务信息：" + ruleTaskJobInfo);
                }
            } catch (Exception e) {
                log.error("推送失败，任务信息：" + ruleTaskJobInfo, e);
                // 记录发送失败日志
                final RuleTaskLog ruleTaskLog = new RuleTaskLog(ruleTaskJobInfo);
                ruleTaskLog.setMessage(e.getMessage());
                ruleTaskLogService.saveSendFailed(ruleTaskLog);
            }

        }
    }

    private void waitSeconds(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {

        }
    }

    private void successWait() {
        final long scanInterval = Config.RULE_TASK_SCAN_SUCCESS_INTERVAL;
        log.info(String.format("扫描成功，等待[%d]s进行下一次扫描", scanInterval));
        waitSeconds(scanInterval);
    }

    private void exceptionWait() {
        final long scanInterval = Config.RULE_TASK_SCAN_FAIL_INTERVAL;
        log.info(String.format("扫描异常或没有更多数据，等待[%d]s进行下一次扫描", scanInterval));
        waitSeconds(scanInterval);
    }

}
