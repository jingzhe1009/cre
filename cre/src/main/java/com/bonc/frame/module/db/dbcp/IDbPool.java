package com.bonc.frame.module.db.dbcp;

import java.sql.Connection;

/** 
 * 数据库连接池接口
 * @author  qxl
 * @date    2017年7月18日 下午5:57:19 
 * @version 1.0
 */
public interface IDbPool {

	/**
	 * 从数据库连接池中获取连接
	 * @param dbId
	 * @return
	 * @throws Exception
	 */
	public Connection getConnection(String dbId) throws Exception;
	/**
	 * 直接创建连接
	 * @param dbId
	 * @return
	 * @throws Exception
	 */
	public Connection getConnectionIm(String dbId) throws Exception;
	
	/**
	 * 关闭连接
	 * @param conn
	 * @throws Exception
	 */
	public void closeConnection(Connection conn) throws Exception;
	
	/**
	 * 创建数据库连接池
	 * @param dbId
	 * @throws Exception
	 */
	public void createDbPool(String dbId) throws Exception;
	
	/**
	 * 销毁数据库连接池
	 * @param dbId
	 * @throws Exception
	 */
	public void destroyDbPool(String dbId) throws Exception;
	
	/**
	 * 判断数据库连接池是否存在
	 * 
	 * @param dbId
	 * @return	true-存在;false-不存在
	 * @throws Exception
	 */
	public boolean isExistPool(String dbId) throws Exception;
	
	/**
	 * 获取内存中连接池的数量
	 * @return
	 */
	public int getDbPoolSize();
	
	/**
	 * 初始化所有的连接池
	 * @throws Exception
	 */
	public void initAllDbPool() throws Exception;
	
}
