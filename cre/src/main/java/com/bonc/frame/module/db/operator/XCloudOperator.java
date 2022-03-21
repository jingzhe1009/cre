package com.bonc.frame.module.db.operator;

import com.bonc.frame.module.db.Page;
import com.bonc.frame.module.db.dbcp.DbPoolFactory;
import com.bonc.frame.module.db.dialect.Dialect;
import com.bonc.frame.module.db.dialect.XCloudDialect;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * oracle数据库中执行sql实现
 *
 * @author qxl
 * @version 1.0
 * @date 2017年8月25日 上午9:30:53
 */
public class XCloudOperator extends AbstractDbOperator {

    private Log log = LogFactory.getLog(getClass());

    private Dialect dialect = new XCloudDialect();

    public XCloudOperator(String dbId) {
        super(dbId);
    }

    @Override
    public ResponseResult pagedQuery(String sql, long offset, long size) throws Exception {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("execute 参数[sql]不能为空");
        }

        sql = sql.trim();
        if (log.isDebugEnabled()) {
            log.debug("分页前的参数，[sql: " + sql + ", offset: " + offset + ", size: " + size + "]");
        }

        Page page = new Page(offset, size);
        sql = dialect.getPageSql(sql, page);
        log.info("分页后的sql: " + sql);
        return execute(sql);
    }

    // -------------------------- DML --------------------------

    public ResponseResult insertBatch(String path) throws Exception {
        if (path == null || (path = path.trim()).isEmpty()) {
            // 数据为空直接返回
            log.warn("参数[path]为空");
            return ResponseResult.createFailInfo("参数[path]为空");
        }

        // TODO: 写入xcloud
        String sql = "INSERT INTO xcloud_user (id,age) 'CLIENT:E:\\user.csv' SEPARATOR '\t' QUOTED";
        StringBuilder sb = new StringBuilder("sql");
        sb.append(" 'CLIENT:").append(path).append("' SEPARATOR '\t' QUOTED");

        Connection conn = null;
        Statement statement = null;

        try {
            conn = DbPoolFactory.getDbPool().getConnection(this.dbId);
            if (conn == null)
                throw new IllegalStateException("获取数据源连接失败，dbId: " + dbId);
            statement = conn.createStatement();

            final boolean hasResult = statement.execute(sql);
            if (!hasResult) {
                final int updateCount = statement.getUpdateCount();
                log.info("插入数据个数：dbId: taskId: " + updateCount);
            }

            return ResponseResult.createSuccessInfo();
        } finally {
            AbstractDbOperator.closeGracefully(statement, conn);
        }
    }

    // -------------------------- DDL ---------------------------

    @Override
    public ResponseResult createTable(String existsTable, String createTable) {
        throw new UnsupportedOperationException("暂不支持创建表");
    }

    @Override
    public void deleteTable(String dbId, String tableName) {
        throw new UnsupportedOperationException("暂不支持删除表");
    }

    @Override
    public List<Map<String, String>> getAllTables(String dbId) throws Exception {
        Connection conn = null;
        try {
            conn = DbPoolFactory.getDbPool().getConnection(dbId);

            List<Map<String, String>> tables = new ArrayList<Map<String, String>>();

            DatabaseMetaData dbMetaData = conn.getMetaData();

            // 可为:"TABLE", "VIEW", "SYSTEM TABLE",
            String[] types = {"TABLE"};

            ResultSet tabs = dbMetaData.getTables(null, getSchema(conn), null, types);
            Map<String, String> tableInfo = null;
            while (tabs.next()) {
                tableInfo = new HashMap<>();
                tableInfo.put("tableName", tabs.getObject("TABLE_NAME").toString());
                tableInfo.put("tableType", tabs.getObject("TABLE_TYPE").toString());
                tableInfo.put("tableRemarks", tabs.getString("REMARKS"));
                tables.add(tableInfo);
            }
            return tables;
        } catch (Exception e) {
            log.error(e);
            throw e;
        } finally {
            closeGracefully(conn);
        }
    }

}
