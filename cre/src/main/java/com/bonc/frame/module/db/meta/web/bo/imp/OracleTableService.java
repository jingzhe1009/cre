package com.bonc.frame.module.db.meta.web.bo.imp;

import com.bonc.frame.module.db.meta.core.IConnection;
import com.bonc.frame.module.db.meta.core.IScan;
import com.bonc.frame.module.db.meta.core.connection.ConnectionProvider;
import com.bonc.frame.module.db.meta.core.exception.AfterScanException;
import com.bonc.frame.module.db.meta.core.exception.BeforeScanException;
import com.bonc.frame.module.db.meta.core.exception.ScanException;
import com.bonc.frame.module.db.meta.core.model.DatabaseResource;
import com.bonc.frame.module.db.meta.core.model.ScanTask;
import com.bonc.frame.module.db.meta.core.model.StructInfo;
import com.bonc.frame.module.db.meta.core.model.TableInfo;
import com.bonc.frame.module.db.meta.core.scan.OracleTableScanProvider;
import com.bonc.frame.module.db.meta.core.service.AbstractScanService;
import com.bonc.frame.module.db.meta.util.Constants;
import com.bonc.frame.module.db.meta.web.bo.TableMetaInfoBO;
import com.bonc.frame.module.db.meta.web.vo.MetaChangeLog;
import com.bonc.frame.module.db.meta.web.vo.TableMetaInfoWapper;
import com.bonc.framework.util.IdUtil;
import com.bonc.framework.util.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.SQLException;
import java.util.*;


public class OracleTableService extends AbstractScanService<TableInfo> {

    private static final Log Log = LogFactory.getLog(OracleTableService.class);

    private IScan<TableInfo> scan;
    private IConnection conn;

    private List<TableMetaInfoWapper> needInsertTables;
    private List<TableMetaInfoWapper> needUpdateTables;

    private TableMetaInfoBO tableMetaInfoBO;

    public OracleTableService(ScanTask scanTask, DatabaseResource databaseResource, TableMetaInfoBO tableMetaInfoBO)
            throws SQLException {
        super(scanTask);
        this.conn = new ConnectionProvider(databaseResource);
        this.scan = new OracleTableScanProvider(this.conn, scanTask);
        this.tableMetaInfoBO = tableMetaInfoBO;
        tableMetaInfoBO.setScanTask(scanTask);
        this.needInsertTables = new ArrayList<>();
        this.needUpdateTables = new ArrayList<>();
    }


    @Override
    public void beforeScan() throws BeforeScanException {
        this.needInsertTables.clear();
        this.needUpdateTables.clear();
        try {
            //更新任务状态为执行中
            this.scanTask.setTaskStatus(Constants.Task_Status_Scanning);
            this.tableMetaInfoBO.updateScanTask(scanTask);
        } catch (Exception e) {
            throw new BeforeScanException(e.getMessage());
        }
    }

    @Override
    public void scan() throws ScanException {
        try {
            // 扫描元数据信息
            List<TableInfo> tables = scan.scanObjectMetaInfo();
            for (TableInfo table : tables) {
                TableInfo tempTableInfo = tableMetaInfoBO.selectTableStructMetaInfo(table.getTableId(), this.scanTask.getPackageId());
                if (tempTableInfo == null) {
                    //如果数据库中没有，则表示为新增表
                    //设置初始化资源状态
                    table.setResStatus((short) 0);
                    this.needInsertTables.add(new TableMetaInfoWapper(table));
                    continue;
                }
                tempTableInfo.setScanId(scanTask.getTaskId());
                //以数据库中的信息记录key value
                Map<String, StructInfo> orgTempMap = new HashMap<>();
                //以更新的信息记录key value
                Map<String, StructInfo> targetTempMap = new HashMap<>();
                //必须设置数据库中的资源状态
                table.setResStatus(tempTableInfo.getResStatus());
                TableMetaInfoWapper tempTableWapper = new TableMetaInfoWapper(table);
                //循环处理数据库中的数据结构类型
                for (StructInfo orgStructInfo : tempTableInfo.getStructs()) {
                    orgTempMap.put(orgStructInfo.getColumnId(), orgStructInfo);
                }
                for (StructInfo targetStructInfo : table.getStructs()) {
                    targetTempMap.put(targetStructInfo.getColumnId(), targetStructInfo);
                }
                //遍历新的表结构
                for (StructInfo tarStructInfo : table.getStructs()) {
                    tarStructInfo.setScanId(scanTask.getTaskId());
                    if (orgTempMap.containsKey(tarStructInfo.getColumnId())) {
                        //存在这个结构
                        StructInfo orgStructInfo = orgTempMap.get(tarStructInfo.getColumnId());
                        if (!orgStructInfo.equals(tarStructInfo)) {
                            tempTableWapper.addUpdateStructs(tarStructInfo);
                        }
                    } else {
                        //如果不存在这个结构，则插入
                        tempTableWapper.addInsertStructs(tarStructInfo);
                    }
                }
                //遍历旧表的结构
                for (StructInfo orgStructInfo : tempTableInfo.getStructs()) {
                    if (!targetTempMap.containsKey(orgStructInfo.getColumnId())) {
                        //判断如果新结构没有原来的结构，则表示删除
                        tempTableWapper.addDeleteStructs(orgStructInfo);
                    }
                }
                //设置是否更新表结构
                tempTableWapper.setUpdateTableInfo(!table.equals(tempTableInfo));
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
            //更新数据库
            this.tableMetaInfoBO.updateTableStructMetaInfos(needInsertTables, needUpdateTables);
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
            //统计更新详细信息
            MetaChangeLog mcl = new MetaChangeLog(needInsertTables.size(),
                    total, needInsertTables, needUpdateTablesLog);
            String logDetail = JsonUtils.toJSONString(mcl);
            //设置更新明细数据
            this.scanTaskLog.setTaskDetail(logDetail);
        } catch (Exception e) {
            Log.error(e);
            this.scanTaskLog.setTaskDetail(e.getMessage());
            throw new AfterScanException();
        }
    }

    @Override
    public void makeLog() {
        //更新任务状态为结束并且记录日志
        this.scanTask.setTaskStatus(Constants.Task_Status_Finish);
        this.scanTask.setEndDate(new Date());
        this.tableMetaInfoBO.updateScanTask(scanTask);
        scanTaskLog.setLogId(IdUtil.createId());
        this.tableMetaInfoBO.insertScanTaskLog(scanTaskLog);
    }

    public void setTableMetaInfoBO(TableMetaInfoBO tableMetaInfoBO) {
        this.tableMetaInfoBO = tableMetaInfoBO;
    }
}
