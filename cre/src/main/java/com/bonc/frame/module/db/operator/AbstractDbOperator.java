package com.bonc.frame.module.db.operator;

import com.bonc.frame.module.db.IDbOperator;
import com.bonc.frame.module.db.dbcp.DbPoolFactory;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qxl
 * @version 1.0
 * @date 2017年8月21日 上午11:02:29
 */
public abstract class AbstractDbOperator implements IDbOperator {

    private Log log = LogFactory.getLog(getClass());

    protected String dbId;

    protected AbstractDbOperator(String dbId) {
        this.dbId = dbId;
    }

    @Override
    public ResponseResult execute(String sql) throws Exception {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("execute 参数[sql]不能为空");
        }

        Connection conn = DbPoolFactory.getDbPool().getConnection(this.dbId);
        if (conn == null) {
            throw new IllegalStateException("Get connection fail.The dbId is illegal or the db config is error.");
        }
        sql = sql.trim();
        return execute(conn, sql);
    }

    @Override
    public ResponseResult execute(Connection conn, String sql) throws Exception {
        return execute(conn, sql, true);
    }

    public ResponseResult execute(Connection conn, String sql, boolean isClose) throws Exception {
        if (conn == null) {
            log.error("the connection is null.");
            throw new Exception("the connection is null.");
        }
//		conn.setAutoCommit(true);
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
            List<Map<String, Object>> resultList = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> mp = new HashMap<>();
                for (int c = 1; c <= columnCount; c++) {
                    mp.put(rm.getColumnLabel(c), handlerCloumnValue(rs, c));
                }
                resultList.add(mp);
            }
//			conn.commit();
            return ResponseResult.createSuccessInfo("查询成功.", resultList);
        } finally {
            closeGracefully(pstat, conn, isClose);
        }
    }

    public static void closeGracefully(Connection conn) {
        closeGracefully(null, conn, true);
    }

    public static void closeGracefully(Statement stat, Connection conn) {
        closeGracefully(stat, conn, true);
    }

    public static void closeGracefully(Statement stat, Connection conn, boolean isClose) {
        if (stat != null) {
            try {
                stat.close();
            } catch (SQLException e) {
            }
        }
        if (isClose) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    //获取库中所有的表
    //适用于mysql、oracle、等常见数据库
    @Override
    public List<Map<String, String>> getAllTables(String dbId) throws Exception {
        Connection conn = null;
        try {
            conn = DbPoolFactory.getDbPool().getConnection(dbId);

            List<Map<String, String>> tables = new ArrayList<Map<String, String>>();

            DatabaseMetaData dbMetaData = conn.getMetaData();

            // 可为:"TABLE", "VIEW", "SYSTEM TABLE",
            // "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"
            String[] types = {"TABLE", "VIEW"};

            //2017-07-20增加getSchema,解决oracle查询全部用户表问题
            ResultSet tabs = dbMetaData.getTables(null, getSchema(conn), null, types);
            /*
             * 记录集的结构如下:
             * TABLE_CAT 	String => table catalog (may be null)
             * TABLE_SCHEM 	String => table schema (may be null)
             * TABLE_NAME 	String => table name
             * TABLE_TYPE 	String => table type.
             * REMARKS 		String => explanatory
             * comment on the table
             * TYPE_CAT 	String => the types catalog (may be null)
             * TYPE_SCHEM 	String => the types schema (may be null)
             * TYPE_NAME	String => type name (may be null)
             * SELF_REFERENCING_COL_NAME String =>name of the designated "identifier" column of a typed table (may be
             * null)
             * REF_GENERATION String => specifies how values in
             * SELF_REFERENCING_COL_NAME are created. Values are "SYSTEM", "USER","DERIVED". (may be null)
             */
            Map<String, String> tableInfo = null;
            while (tabs.next()) {
                tableInfo = new HashMap<String, String>();
                tableInfo.put("tableName", tabs.getObject("TABLE_NAME").toString());
                tableInfo.put("tableType", tabs.getObject("TABLE_TYPE").toString());
                tableInfo.put("tableRemarks", tabs.getString("REMARKS"));
                tables.add(tableInfo);
            }
            //		for(Map map : tables){
            //			System.out.println(map);
            //		}
            //		System.out.println("total table："+tables.size());
            return tables;
        } catch (Exception e) {
            log.error(e);
            throw e;
        } finally {
            closeGracefully(conn);
        }
    }

    //其他数据库不需要这个方法 oracle和db2需要
    public static String getSchema(Connection conn) throws Exception {
        String schema;
        schema = conn.getMetaData().getUserName();
        if ((schema == null) || (schema.length() == 0)) {
            throw new Exception("数据库模式不允许为空");
        }
        return schema.toUpperCase().toString();

    }

    @Override
    public Object handlerCloumnValue(ResultSet rs, int columnIndex) throws SQLException {
        Object obj = rs.getObject(columnIndex);
        if (obj == null) {
            return null;
        }
        if (obj instanceof Clob) {
            try {
                return ClobToString((Clob) obj);
            } catch (IOException e) {
                throw new SQLException("Clob 转换成 String 失败", e);
            }
        }
        return obj;
    }

    public static String ClobToString(Clob clob) throws SQLException, IOException {
        if (clob == null) {
            return null;
        }
        String reString = "";
        Reader is = clob.getCharacterStream();// 得到流
        BufferedReader br = new BufferedReader(is);
        String s = br.readLine();
        StringBuffer sb = new StringBuffer();
        while (s != null) {// 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
            sb.append(s);
            s = br.readLine();
        }
        reString = sb.toString();
        return reString;
    }

}
