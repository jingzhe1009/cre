package com.bonc.frame.module.db.operator;

import com.bonc.frame.module.db.Page;
import com.bonc.frame.module.db.dbcp.DbPoolFactory;
import com.bonc.frame.module.db.dialect.Dialect;
import com.bonc.frame.module.db.dialect.MySqlDialect;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * mysql数据库中执行sql实现
 * @author qxl
 * @date 2017年8月21日 下午2:59:21
 * @version 1.0
 */
public class MySqlOperator extends AbstractDbOperator {

    private Log log = LogFactory.getLog(getClass());

	private Dialect dialect = new MySqlDialect();

	public MySqlOperator(String dbId){
        super(dbId);
	}

	@Override
	public ResponseResult pagedQuery(String sql, long offset, long size) throws Exception {
		if (sql == null || sql.isEmpty() || sql.trim().isEmpty()) {
			log.error("The sql is null.");
			return ResponseResult.createFailInfo("The sql is null.");
		}
		try {
			Connection conn = DbPoolFactory.getDbPool().getConnection(dbId);
			if (conn == null) {
				log.error("Get connection fail.The dbId is illegal or the db config is error.");
				throw new IllegalStateException("Get connection fail.The dbId is illegal or the db config is error.");
			}
			sql = sql.trim();
			log.debug("The sql before doPage is :" + sql);
			Page page = new Page(offset, size);
			sql = dialect.getPageSql(sql, page);
			log.debug("The sql after doPage is :" + sql);
			return execute(conn, sql);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public ResponseResult createTable(String existsTable, String createTable) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseResult execute(String sql) throws Exception {
		if(sql == null || sql.isEmpty() || sql.trim().isEmpty()){
			log.error("The sql is null.");
			return ResponseResult.createFailInfo("The sql is null.");
		}
		try {
			Connection conn = DbPoolFactory.getDbPool().getConnection(dbId);
			if(conn == null){
				throw new IllegalStateException("Get connection fail.The dbId is illegal or the db config is error.");
			}
			sql = sql.trim();
			return execute(conn, sql);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	public void deleteTable(String dbId, String tableName) throws Exception {
		if(tableName == null || tableName.isEmpty()){
			return;
		}
		Connection conn = null;
		Statement stat = null;
		try {
			String sql = "drop table "+tableName;
			conn = DbPoolFactory.getDbPool().getConnection(dbId);
			stat = conn.createStatement();
			stat.execute(sql);
		} catch (Exception e) {
			log.error(e);
			throw e;
		} finally {
			try {
				if(conn != null){
					conn.close();
				}
				stat.close();
			} catch (SQLException e) {
			}
		}
	}

}
