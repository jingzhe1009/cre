package com.bonc.frame.module.db;


import com.bonc.frame.util.ResponseResult;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 数据库相关操作接口
 * @author qxl
 * @date 2017年8月21日 上午10:53:57
 * @version 1.0
 */
public interface IDbOperator {

	/**
	 * 执行指定sql
	 * @param sql
	 * @return
	 */
    ResponseResult execute(String sql) throws Exception;
	
	/**
	 * 执行指定sql
	 * @param conn
	 * @param sql
	 * @return
	 */
    ResponseResult execute(Connection conn, String sql) throws Exception;

    ResponseResult pagedQuery(String sql, long offset, long size) throws Exception;

    /**
     * 转化查询结果
     *
     * @param rs
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    Object handlerCloumnValue(ResultSet rs, int columnIndex) throws SQLException;

    // ---------------------- DDL ----------------------

    ResponseResult createTable(String existsTable, String createTable) throws Exception;
	
	/**
	 * 获取库中所有的表
	 * @param dbId
	 * @return
	 * @throws Exception 
	 */
	List<Map<String, String>> getAllTables(String dbId) throws Exception;
	
	/**
	 * 删除表
	 * @param dbId
	 * @param tableName
	 */
	void deleteTable(String dbId, String tableName) throws Exception;

}
