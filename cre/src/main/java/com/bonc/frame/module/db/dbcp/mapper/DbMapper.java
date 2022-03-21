package com.bonc.frame.module.db.dbcp.mapper;


import com.bonc.frame.module.db.DbHelper;
import com.bonc.frame.module.db.dbcp.DbPoolConstant;
import com.bonc.frame.module.db.dbcp.entity.DbEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** 
 * 数据库操作类
 * @author  qxl
 * @date    2017年7月19日 下午2:32:49 
 * @version 1.0
 */
public class DbMapper {
	
	
	/**
	 * 根据数据库Id查询数据库连接配置参数
	 * @param dbId
	 * @return
	 * @throws Exception
	 */
	public DbEntity getDbConfigByDbId(String dbId) throws Exception{
		String sql = "SELECT DB_ID dbId, DB_IP dbIp, DB_PORT dbPort, DB_TYPE dbType, "
				+ "DB_SERVICE_NAME dbServiceName, DB_USERNAME dbUsername, DB_PASSWORD dbPassword,"
				+ "IS_POOL isPool, MAX_CONNECT maxConnect, MAX_IDLE maxIdle "
				+ "FROM CRE_DB_CONNECTION "
				+ "WHERE DB_ID = ? ";
		DbEntity dbConfig = null;
		try {
			dbConfig = DbHelper.query4Object(sql, DbEntity.class, dbId);
		} catch (SQLException e) {
			throw new Exception("select dbconfig exception,dbId["+dbId+"].",e);
		}
		return dbConfig;
	}
	
	/**
	 * 查询所有启用的数据库连接池的dbId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,Object>> getAllDbPoolId() throws Exception{
		String sql = "SELECT DB_ID dbId FROM CRE_DB_CONNECTION "
				+ "WHERE IS_POOL = ? ";
		try {
			List<Map<String,Object>> result = DbHelper.query4MapList(sql, DbPoolConstant.IS_USE);
			return result;
		} catch(SQLException e) {
			throw new Exception("select all in use dbPool exception.");
		}
	}
	
	/**
	 * 更新数据库表中连接池的状态
	 * @param dbId
	 * @param status
	 * @throws Exception
	 */
	public void updateDbPoolState(String dbId,String status) throws Exception{
		String sql = "UPDATE CRE_DB_CONNECTION set IS_POOL = ? WHERE DB_ID = ? ";
		try {
			DbHelper.update(sql, status,dbId);
		} catch (SQLException e) {
			throw new Exception("update dbpool status exception,dbId["+dbId+"],status["+status+"].");
		}
	}
	
}
