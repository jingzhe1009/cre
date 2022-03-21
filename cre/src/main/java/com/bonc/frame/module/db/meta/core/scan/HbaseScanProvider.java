package com.bonc.frame.module.db.meta.core.scan;

import com.bonc.frame.module.db.dbcp.DbPoolFactory;
import com.bonc.frame.module.db.meta.core.IScan;
import com.bonc.frame.module.db.meta.core.model.ScanTask;
import com.bonc.frame.module.db.meta.core.model.StructInfo;
import com.bonc.frame.module.db.meta.core.model.TableInfo;
import com.bonc.frame.module.db.operator.hbase.HbaseConfig;
import com.bonc.frame.module.db.operator.hbase.HbaseHelper;
import com.bonc.frame.util.MD5Util;
import com.google.common.collect.ImmutableList;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author yedunyao
 * @date 2019/7/9 18:04
 */
public class HbaseScanProvider implements IScan<TableInfo> {

    private String dbId;

    private ScanTask scanTask;

    private HbaseConfig hbaseConfig;

    public HbaseScanProvider(HbaseConfig hbaseConfig, ScanTask scanTask) {
        this.dbId = hbaseConfig.getDbId();
        this.scanTask = scanTask;
        this.hbaseConfig = hbaseConfig;
    }

    @Override
    public List<TableInfo>
    scanObjectMetaInfo() throws SQLException {
        try {
            HbaseHelper hbaseHelper = DbPoolFactory.getHbaseDbPool().getHbaseHelper(hbaseConfig);
            final HTableDescriptor tableDescriptor = hbaseHelper.desc(scanTask.getTaskKey());
            if (tableDescriptor == null) {
                return Collections.emptyList();
            }

            final TableInfo tableInfo = parse(tableDescriptor);
            return ImmutableList.of(tableInfo);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public String getScanTaskId() {
        return this.scanTask.getTaskId();
    }

    private TableInfo parse(HTableDescriptor tableDescriptor) throws Exception {
        TableInfo tableInfo = new TableInfo();
        final String table_name = tableDescriptor.getNameAsString();
        tableInfo.setTableId(MD5Util.Bit16(dbId + table_name + scanTask.getPackageId()));
        tableInfo.setTableCode(table_name);
        tableInfo.setTableName(table_name);
        tableInfo.setResId(dbId);
        tableInfo.setTableType("TABLE");
        tableInfo.setTableKind(scanTask.getTableKind());

        List<StructInfo> structs = new LinkedList<>();
        final Collection<HColumnDescriptor> families = tableDescriptor.getFamilies();
        for (HColumnDescriptor columnDescriptor : families) {
            StructInfo structInfo = new StructInfo();
            structInfo.setColumnId(MD5Util.Bit16(tableInfo.getTableId() + dbId + table_name +
                    columnDescriptor.getNameAsString() + scanTask.getPackageId()));
            structInfo.setTableId(tableInfo.getTableId());
            structInfo.setColumnCode(columnDescriptor.getNameAsString());
            structInfo.setColumnName(columnDescriptor.getNameAsString());
            structInfo.setColumnType("1");     // FIXME: 字段默认值 String
            structInfo.setColumnSize(0);
            structInfo.setIsPk((short) 0);
            structInfo.setScanId(scanTask.getTaskId());
            structs.add(structInfo);
        }

        // FIXME: need RowKey?
        // RowKey
        StructInfo structInfo = new StructInfo();
        structInfo.setColumnId(MD5Util.Bit16(tableInfo.getTableId() + dbId + table_name +
                "RowKey" + scanTask.getPackageId()));
        structInfo.setTableId(tableInfo.getTableId());
        structInfo.setColumnCode("RowKey");
        structInfo.setColumnName("RowKey");
        structInfo.setColumnType("1");
        structInfo.setColumnSize(0);
        structInfo.setIsPk((short) 1);
        structInfo.setScanId(scanTask.getTaskId());
        structs.add(structInfo);

        tableInfo.setStructs(structs);
        tableInfo.setScanId(scanTask.getTaskId());
        return tableInfo;
    }
}
