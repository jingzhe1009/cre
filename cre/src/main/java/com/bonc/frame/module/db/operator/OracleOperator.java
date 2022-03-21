package com.bonc.frame.module.db.operator;

import com.bonc.frame.module.db.Page;
import com.bonc.frame.module.db.dbcp.DbPoolFactory;
import com.bonc.frame.module.db.dialect.Dialect;
import com.bonc.frame.module.db.dialect.OracleDialect;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * oracle数据库中执行sql实现
 *
 * @author qxl
 * @version 1.0
 * @date 2017年8月25日 上午9:30:53
 */
public class OracleOperator<T> extends AbstractDbOperator {

    private Log log = LogFactory.getLog(getClass());

    private Dialect dialect = new OracleDialect();

    public OracleOperator(String dbId) {
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

    public ResponseResult insert(String sql) throws Exception {
        return execute(sql);
    }

    public ResponseResult insertBatch(List<String> data) throws Exception {
        if (data == null || data.isEmpty()) {
            // 数据为空直接返回
            log.warn("参数[data]为空");
            return ResponseResult.createFailInfo("参数[data]为空");
        }

        Connection conn = null;
        Statement statement = null;

        try {
            conn = DbPoolFactory.getDbPool().getConnection(this.dbId);
            if (conn == null)
                throw new IllegalStateException("获取数据源连接失败，dbId: " + dbId);
            statement = conn.createStatement();
            for (String sql : data)
                statement.addBatch(sql);
            final int[] ints = statement.executeBatch();

            return ResponseResult.createSuccessInfo();
        } finally {
            AbstractDbOperator.closeGracefully(statement, conn);
        }
    }

    // -------------------------- DDL ---------------------------

    @Override
    public ResponseResult createTable(String existsTable, String createTable) throws Exception {
        log.info("即将创建表，表是否存在sql: " + existsTable + " 创建表sql: " + createTable);
        if (existsTable == null || existsTable.trim().isEmpty()) {
            throw new IllegalArgumentException("The [existsTable] sql is null");
        }
        if (createTable == null || createTable.trim().isEmpty()) {
            throw new IllegalArgumentException("The [createTable] sql is null");
        }
        Connection conn = DbPoolFactory.getDbPool().getConnection(dbId);
        if (conn == null) {
            throw new IllegalStateException("Get connection fail.The dbId is illegal or the db config is error.");
        }
        final ResponseResult existResult = execute(conn, existsTable, false);
        if (existResult != null) {
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) existResult.getData();
            if (Integer.parseInt(resultList.get(0).get("NUM").toString()) <= 0) {
                return execute(conn, createTable, false);
            }
        }
        throw new RuntimeException("Execute exists sql failed, dbId: " + dbId + " existsTable: " + existsTable);
    }

    @Override
    public void deleteTable(String dbId, String tableName) throws Exception {
        if (tableName == null || tableName.isEmpty()) {
            return;
        }
        Connection conn = null;
        Statement stat = null;
        try {
            String sql = "drop table " + tableName.toUpperCase();
            conn = DbPoolFactory.getDbPool().getConnection(dbId);
            stat = conn.createStatement();
            stat.execute(sql);
        } catch (Exception e) {
            throw e;
        } finally {
            closeGracefully(stat, conn);
        }
    }

}
