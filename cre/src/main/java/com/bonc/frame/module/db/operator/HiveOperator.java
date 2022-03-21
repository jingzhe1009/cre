package com.bonc.frame.module.db.operator;


import com.bonc.frame.module.db.Page;
import com.bonc.frame.module.db.dbcp.DbPoolFactory;
import com.bonc.frame.module.db.dialect.Dialect;
import com.bonc.frame.module.db.dialect.HiveDialect;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.common.type.HiveBaseChar;
import org.apache.hadoop.hive.common.type.HiveDecimal;

import java.math.BigDecimal;
import java.sql.*;
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
public class HiveOperator extends AbstractDbOperator {

    private Log log = LogFactory.getLog(getClass());

    private Dialect dialect = new HiveDialect();

    public HiveOperator(String dbId) {
        super(dbId);
    }

    @Override
    public ResponseResult execute(Connection conn, String sql, boolean isClose) throws Exception {
        if (conn == null) {
            throw new Exception("the [connection] is null.");
        }
        PreparedStatement pstat = null;
        try {
            pstat = conn.prepareStatement(sql);
//			pstat.setQueryTimeout(10);
            log.debug("开始执行，sql: " + sql);
            boolean hasResult = pstat.execute();
            if (!hasResult) {//无结果集
                int count = pstat.getUpdateCount();
                log.info("操作成功.影响记录条数:[" + count + "].");
                return ResponseResult.createSuccessInfo("操作成功.影响记录条数:[" + count + "].", count);
            }
            //获取结果集
            ResultSet rs = pstat.getResultSet();
            ResultSetMetaData rm = rs.getMetaData();
            int columnCount = rm.getColumnCount();//列数
            //查询结果
            List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
            while (rs.next()) {
                Map<String, Object> mp = new HashMap<String, Object>();
                for (int c = 1; c <= columnCount; c++) {
                    // FIXME: hard code
                    String column = rm.getColumnLabel(c);
                    column = column.replaceFirst("(T|t)\\.", "");    // 删除t.
                    mp.put(column, handlerCloumnValue(rs, c));
                }
                resultList.add(mp);
            }
            return ResponseResult.createSuccessInfo("查询成功.", resultList);
        } finally {
            closeGracefully(pstat, conn, isClose);
        }
    }

    @Override
    public ResponseResult pagedQuery(String sql, long offset, long size) throws Exception {
        if (sql == null || sql.isEmpty() || sql.trim().isEmpty()) {
            return ResponseResult.createFailInfo("The [sql] is null.");
        }
        try {
            Connection conn = DbPoolFactory.getDbPool().getConnection(this.dbId);
            if (conn == null) {
                throw new IllegalStateException("Get connection fail.The dbId is illegal or the db config is error.");
            }
            sql = sql.trim();
            log.debug("The sql before doPage is :" + sql);
            Page page = new Page(offset, size);
            sql = dialect.getPageSql(sql, page);
            log.info("The sql after doPage is :" + sql);
            return execute(conn, sql);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public ResponseResult createTable(String existsTable, String createTable) throws Exception {
        throw new UnsupportedOperationException("暂不支持hive类型数据源的create table操作");
    }

    @Override
    public List<Map<String, String>> getAllTables(String dbId) throws Exception {
        List<Map<String, String>> tables = new ArrayList<>();
        Connection conn = null;
        ResultSet rs = null;// 存储表元数据
        Statement stmt = null;
        try {
            conn = DbPoolFactory.getDbPool().getConnection(dbId);
            stmt = conn.createStatement();
            String descTableSQL = "show tables";
            rs = stmt.executeQuery(descTableSQL);
            Map<String, String> tableInfo = null;
            while (rs.next()) {
                tableInfo = new HashMap<String, String>();
                tableInfo.put("tableName", rs.getString(1));
                tableInfo.put("tableType", "TABLE");
                tableInfo.put("tableRemarks", rs.getString(1));
                tables.add(tableInfo);
            }
        } catch (Exception e) {
            log.error(e);
            throw e;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    log.error(e);
                }
            }
            closeGracefully(stmt, conn);
        }
        return tables;
    }

    @Override
    public void deleteTable(String dbId, String tableName) throws Exception {
        if (tableName == null || tableName.isEmpty()) {
            return;
        }
        Connection conn = null;
        Statement stat = null;
        try {
            String sql = "DROP TABLE IF EXISTS " + tableName.toLowerCase();
            conn = DbPoolFactory.getDbPool().getConnection(dbId);
            stat = conn.createStatement();
            stat.execute(sql);
        } catch (Exception e) {
            throw e;
        } finally {
            closeGracefully(stat, conn, true);
        }
    }

    @Override
    public Object handlerCloumnValue(ResultSet rs, int columnIndex) throws SQLException {
        Object obj = rs.getObject(columnIndex);
        if (obj == null) {
            return null;
        }
        if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj);
        }
        if (obj instanceof HiveDecimal) {
            return ((HiveDecimal) obj).bigDecimalValue();
        }
        if (obj instanceof HiveBaseChar) {
            return ((HiveBaseChar) obj).toString();
        }
        return obj;
    }

}
