package com.bonc.frame.module.db;

import com.bonc.frame.module.db.dialect.Dialect;
import com.bonc.frame.module.db.dialect.MySqlDialect;
import com.bonc.frame.module.db.dialect.OracleDialect;
import com.bonc.frame.util.SpringUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


/** 
 * @author 作者: jxw 
 * @date 创建时间: 2017年4月14日 上午10:11:12 
 * @version 版本: 1.0 
*/
public class DbHelper {

	private static DataSource dataSource;
	//数据库方言-默认为MySQL
	private static Dialect dialect;

	static  {
		dataSource = (DataSource) SpringUtils.getBean("dataSource");
		dialect = new OracleDialect();
	}

	public static void setDataSource(DataSource dataSource) {
		DbHelper.dataSource = dataSource;
	}
	
	public static void setDialect(Dialect dialect){
		DbHelper.dialect = dialect;
	}

	/**
	 * 查询List
	 * @param sql		查询的SQL
	 * @param clazz		List中的对象
	 * @param params	参数
	 * @return
	 * @throws SQLException
	 */
	public static <T> List<T> query4List(String sql,Class<T> clazz,Object... params) throws SQLException{
		Connection conn = getConnection();
		try{
			QueryRunner qRunner = new QueryRunner();
			return qRunner.query(conn, sql,  new BeanListHandler<T>(clazz), params) ;
		}catch(SQLException e){
			throw new SQLException(e);
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
	}
	
	/**
	 * 查询List
	 * @param conn      数据库链接
	 * @param sql		查询的SQL
	 * @param clazz		List中的对象
	 * @param params	参数
	 * @return
	 * @throws SQLException
	 */
	public static <T> List<T> query4List(Connection conn ,String sql,Class<T> clazz,Object... params) throws SQLException{
		try{
			QueryRunner qRunner = new QueryRunner();
			return qRunner.query(conn, sql,  new BeanListHandler<T>(clazz), params) ;
		}catch(SQLException e){
			throw new SQLException(e);
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
	}
	
	/**
	 * 查询List,list中为Map对象
	 * @param sql		查询SQL
	 * @param params	查询参数
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, Object>> query4MapList(String sql,Object... params) throws SQLException{
		Connection conn = getConnection();
		try{
			QueryRunner qRunner = new QueryRunner();
			List<Map<String, Object>> list = qRunner.query(conn, sql,  new MapListHandler(), params) ;
			return list;
		}catch(SQLException e){
			throw new SQLException(e);
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
	}
	
	/**
	 * 查询List,list中为Map对象
	 * @param conn      数据库连接池
	 * @param sql		查询SQL
	 * @param params	查询参数
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, Object>> query4MapList(Connection conn ,String sql,Object... params) throws SQLException{
		try{
			QueryRunner qRunner = new QueryRunner();
			List<Map<String, Object>> list = qRunner.query(conn, sql,  new MapListHandler(), params) ;
			return list;
		}catch(SQLException e){
			throw new SQLException(e);
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
	}
	
	
	/**
	 * 查询Map
	 * @param sql		查询SQL
	 * @param params	查询参数
	 * @return
	 * @throws SQLException
	 */
	public static Map<String,Object> query4Map(String sql,Object... params) throws SQLException{
		//创建SQL执行工具   
		Connection conn = getConnection();
		try{
			QueryRunner qRunner = new QueryRunner();
			Map<String,Object> map = qRunner.query(conn, sql,  new MapHandler(), params) ;
			return map;
		}catch(SQLException e){
			throw new SQLException(e);
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
	}
	
	/**
	 * 查询Map
	 * @param sql		查询SQL
	 * @param params	查询参数
	 * @param conn      数据库连接池
	 * @return
	 * @throws SQLException
	 */
	public static Map<String,Object> query4Map(Connection conn ,String sql,Object... params) throws SQLException{
		try{
			QueryRunner qRunner = new QueryRunner();
			Map<String,Object> map = qRunner.query(conn, sql,  new MapHandler(), params) ;
			return map;
		}catch(SQLException e){
			throw new SQLException(e);
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
	}
	
	/**
	 * 查询对象
	 * @param sql	查询SQL
	 * @param clazz	对象的class
	 * @return
	 * @throws SQLException
	 */
	public static <T> T query4Object(String sql,Class<T> clazz,Object... params) throws SQLException{
		//创建SQL执行工具   
		Connection conn = getConnection();
		try{
			QueryRunner qRunner = new QueryRunner();
	        T t = qRunner.query(conn, sql, new BeanHandler<T>(clazz), params) ;
	        return t;
		}catch(SQLException e){
			throw new SQLException(e);
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
	}
	
	/**
	 * 执行更新操作
	 * @param sql	SQL语句
	 * @return
	 * @throws SQLException
	 */
	public static int update(String sql,Object... params) throws SQLException{
		//创建SQL执行工具   
		Connection conn = getConnection();
		try{
			QueryRunner qRunner = new QueryRunner();
			int result = qRunner.update(conn, sql, params);
	        return result;
		}catch(SQLException e){
			throw new SQLException(e);
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
	}
	
	/**
	 * 插入操作
	 * @param sql
	 * @param clazz
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public static <T> T insert(String sql,Class<T> clazz,Object... params) throws SQLException{
		Connection conn = getConnection();
		try{
			QueryRunner qRunner = new QueryRunner();
			T t = qRunner.insert(conn, sql, new BeanHandler<T>(clazz), params);
	        return t;
		}catch(SQLException e){
			throw new SQLException(e);
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
	}
	
	/**
	 * 插入操作
	 * @param conn
	 * @param sql
	 * @param clazz
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public static <T> T insert(Connection conn,String sql,Class<T> clazz,Object... params) throws SQLException{
		try{
			QueryRunner qRunner = new QueryRunner();
			T t = qRunner.insert(conn, sql, new BeanHandler<T>(clazz), params);
	        return t;
		}catch(SQLException e){
			throw new SQLException(e);
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
	}
	
	/**
	 * 分页查询List
	 * @param sql		查询的SQL
	 * @param clazz		List中的对象
	 * @param params	参数
	 * @return
	 * @throws SQLException
	 */
	public static <T> Page<T> pageQuery4List(String sql,String currentPage,String pageSize,Class<T> clazz,Object... params) throws SQLException{
		Connection conn = getConnection();
		try{
			QueryRunner qRunner = new QueryRunner();
			//拼一个查询总条数的SQL
			String countSql = "select count(*) count from ( "+sql +" ) table_count";
			Map<String,Object> map = qRunner.query(conn, countSql, new MapHandler(),params);
			long totalCount = Long.parseLong(map.get("count")==null?"0":map.get("count").toString());
			int size = Integer.parseInt((pageSize == null || "0".equals(pageSize)) ? String.valueOf(Page.PAGE_SIZE) : pageSize);
			int pageNo = Integer.parseInt(currentPage == null ? "0" : currentPage);
			Page<T> page = new Page<T>();
			page.setPageNo(pageNo);
			page.setPageSize(size);
			page.setTotalCount(totalCount);
			sql = dialect.getPageSql(sql, page);
			List<T> result = qRunner.query(conn, sql,  new BeanListHandler<T>(clazz), params);
			page.setResult(result);
			return page ;
		}catch(SQLException e){
			throw new SQLException(e);
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
	}
	
	
	/**
	 * 分页查询List
	 * @param sql		查询的SQL
	 * @param clazz		List中的对象
	 * @param params	参数
	 * @return
	 * @throws SQLException
	 */
	public static <T> Page<T> pageQuery4List(Connection conn, Dialect dialect, String sql,String currentPage,String pageSize,Class<T> clazz,Object... params) throws SQLException{
		try{
			QueryRunner qRunner = new QueryRunner();
			//拼一个查询总条数的SQL
			String countSql = "select count(*) count from ( "+sql +" ) table_count";
			Map<String,Object> map = qRunner.query(conn, countSql, new MapHandler(),params);
			long totalCount = Long.parseLong(map.get("count")==null?"0":map.get("count").toString());
			int size = Integer.parseInt((pageSize == null || "0".equals(pageSize)) ? String.valueOf(Page.PAGE_SIZE) : pageSize);
			int pageNo = Integer.parseInt(currentPage == null ? "0" : currentPage);
			Page<T> page = new Page<T>();
			page.setPageNo(pageNo);
			page.setPageSize(size);
			page.setTotalCount(totalCount);
			sql = dialect.getPageSql(sql, page);
			List<T> result = qRunner.query(conn, sql,  new BeanListHandler<T>(clazz), params);
			page.setResult(result);
			return page ;
		}catch(SQLException e){
			throw new SQLException(e);
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
	}
	
	private static Connection getConnection() throws SQLException{
		if(dataSource==null){
			throw new SQLException("datasource is null.");
		}
		return dataSource.getConnection();
	}
}

