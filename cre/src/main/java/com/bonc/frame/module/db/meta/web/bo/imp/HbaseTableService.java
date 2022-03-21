package com.bonc.frame.module.db.meta.web.bo.imp;

import com.bonc.frame.module.db.meta.core.IScan;
import com.bonc.frame.module.db.meta.core.exception.AfterScanException;
import com.bonc.frame.module.db.meta.core.exception.BeforeScanException;
import com.bonc.frame.module.db.meta.core.exception.ScanException;
import com.bonc.frame.module.db.meta.core.model.DatabaseResource;
import com.bonc.frame.module.db.meta.core.model.ScanTask;
import com.bonc.frame.module.db.meta.core.model.StructInfo;
import com.bonc.frame.module.db.meta.core.model.TableInfo;
import com.bonc.frame.module.db.meta.core.scan.HbaseScanProvider;
import com.bonc.frame.module.db.meta.core.service.AbstractScanService;
import com.bonc.frame.module.db.meta.util.Constants;
import com.bonc.frame.module.db.meta.web.bo.TableMetaInfoBO;
import com.bonc.frame.module.db.meta.web.vo.MetaChangeLog;
import com.bonc.frame.module.db.meta.web.vo.TableMetaInfoWapper;
import com.bonc.frame.module.db.operator.hbase.HbaseConfig;
import com.bonc.framework.util.IdUtil;
import com.bonc.framework.util.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * @author yedunyao
 * @date 2019/7/9 18:37
 */
public class HbaseTableService extends AbstractScanService<TableInfo> {

    private static final Log Log = LogFactory.getLog(HbaseTableService.class);

    private IScan<TableInfo> scan;

    private HbaseConfig hbaseConfig;

    private TableMetaInfoBO tableMetaInfoBO;

    private List<TableMetaInfoWapper> needInsertTables;     // 新增表
    private List<TableMetaInfoWapper> needUpdateTables;     // 需要更新的表

    public HbaseTableService(ScanTask scanTask, DatabaseResource databaseResource, TableMetaInfoBO tableMetaInfoBO) {
        super(scanTask);
        hbaseConfig = HbaseConfig.builder()
                .dbId(databaseResource.getResId())
                .host(databaseResource.getResIp())
                .port(String.valueOf(databaseResource.getResPort()))
                .namespace(databaseResource.getResService())
                .poolSize(databaseResource.getResMax())
                .build();
        this.scan = new HbaseScanProvider(this.hbaseConfig, scanTask);
        this.tableMetaInfoBO = tableMetaInfoBO;
        tableMetaInfoBO.setScanTask(scanTask);
        this.needInsertTables = new ArrayList<TableMetaInfoWapper>();
        this.needUpdateTables = new ArrayList<TableMetaInfoWapper>();
    }

    @Override
    public void beforeScan() throws BeforeScanException {
        this.needInsertTables.clear();
        this.needUpdateTables.clear();
        try {
            this.scanTask.setTaskStatus(Constants.Task_Status_Scanning);
            this.tableMetaInfoBO.updateScanTask(scanTask);//更新任务状态为执行中
        } catch (Exception e) {
            throw new BeforeScanException(e.getMessage());
        }
    }

    @Override
    public void scan() throws ScanException {
        try {
            List<TableInfo> tables = scan.scanObjectMetaInfo();
            for (TableInfo table : tables) {
                TableInfo tempTableInfo = tableMetaInfoBO.selectTableStructMetaInfo(table.getTableId(), this.scanTask.getPackageId());
                if (tempTableInfo == null) {//如果数据库中没有，则表示为新增表
                    table.setResStatus((short) 0);//设置初始化资源状态
                    this.needInsertTables.add(new TableMetaInfoWapper(table));
                    continue;
                }
                tempTableInfo.setScanId(scanTask.getTaskId());
                Map<String, StructInfo> orgTempMap = new HashMap<String, StructInfo>();//以数据库中的信息记录key value
                Map<String, StructInfo> targetTempMap = new HashMap<String, StructInfo>();//以更新的信息记录key value
                table.setResStatus(tempTableInfo.getResStatus());//必须设置数据库中的资源状态
                TableMetaInfoWapper tempTableWapper = new TableMetaInfoWapper(table);
                for (StructInfo orgStructInfo : tempTableInfo.getStructs()) {//循环处理数据库中的数据结构类型
                    orgTempMap.put(orgStructInfo.getColumnId(), orgStructInfo);
                }
                for (StructInfo targetStructInfo : table.getStructs()) {
                    targetTempMap.put(targetStructInfo.getColumnId(), targetStructInfo);
                }
                for (StructInfo tarStructInfo : table.getStructs()) {//遍历新的表结构
                    tarStructInfo.setScanId(scanTask.getTaskId());
                    if (orgTempMap.containsKey(tarStructInfo.getColumnId())) {//存在这个结构
                        StructInfo orgStructInfo = orgTempMap.get(tarStructInfo.getColumnId());
                        if (!orgStructInfo.equals(tarStructInfo)) {
                            tempTableWapper.addUpdateStructs(tarStructInfo);
                        }
                    } else {//如果不存在这个结构，则插入
                        tempTableWapper.addInsertStructs(tarStructInfo);
                    }
                }
                for (StructInfo orgStructInfo : tempTableInfo.getStructs()) {//遍历旧表的结构
                    if (!targetTempMap.containsKey(orgStructInfo.getColumnId())) {//判断如果新结构没有原来的结构，则表示删除
                        tempTableWapper.addDeleteStructs(orgStructInfo);
                    }
                }
                tempTableWapper.setUpdateTableInfo(!table.equals(tempTableInfo));//设置是否更新表结构
                this.needUpdateTables.add(tempTableWapper);
            }
        } catch (Exception e) {
            Log.error(e);
            throw new ScanException(e.getMessage());
        }
    }

    @Override
    public void afterScan() throws AfterScanException {
        try {
            this.tableMetaInfoBO.updateTableStructMetaInfos(needInsertTables, needUpdateTables);//更新数据库
            //格式化日志
            for (TableMetaInfoWapper tw : needInsertTables) {
                tw.setUpdateTableInfo(true);
                tw.setNeedInsertStructs(tw.getTableInfo().getStructs());
                tw.getTableInfo().setStructs(null);
            }
            int total = 0;
            List<TableMetaInfoWapper> needUpdateTablesLog = new ArrayList<TableMetaInfoWapper>();
            ;
            for (TableMetaInfoWapper tw : needUpdateTables) {
                tw.getTableInfo().setStructs(null);
                if (tw.isUpdateTableInfo() || tw.getNeedInsertStructs().size() > 0 || tw.getNeedUpdateStructs().size() > 0) {
                    total++;
                    needUpdateTablesLog.add(tw);
                }
            }
            MetaChangeLog mcl = new MetaChangeLog(needInsertTables.size(), //统计更新详细信息
                    total, needInsertTables, needUpdateTablesLog);
            String logDetail = JsonUtils.toJSONString(mcl);
            this.scanTaskLog.setTaskDetail(logDetail);//设置更新明细数据
        } catch (Exception e) {
            this.scanTaskLog.setTaskDetail(e.getMessage());
            throw new AfterScanException();
        }
    }

    @Override
    public void makeLog() {
        this.scanTask.setTaskStatus(Constants.Task_Status_Finish);//更新任务状态为结束并且记录日志
        this.scanTask.setEndDate(new Date());
        this.tableMetaInfoBO.updateScanTask(scanTask);
        scanTaskLog.setLogId(IdUtil.createId());
        this.tableMetaInfoBO.insertScanTaskLog(scanTaskLog);
    }

    public void setTableMetaInfoBO(TableMetaInfoBO tableMetaInfoBO) {
        this.tableMetaInfoBO = tableMetaInfoBO;
    }
}
