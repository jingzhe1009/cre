package com.bonc.frame.module.db.entity;

/**
 * 
 * @author qxl
 * @date 2017年8月21日 下午4:34:11
 * @version 1.0
 */
public class DbInfo {
	private String dbId;//数据库链接标识
	
	private String dbAlias;//数据库链接别名
	
	private String dbIp;//数据库链接IP
	
	private Integer dbPort;//数据库链接端口
	
	private String dbType;//数据库链接类型
	
	private String taskTypeId;//任务执行的类型 allSql:流程,conSql:条件 
	
	private String dbServiceName;//数据库服务名
	
	private String dbUsername;//数据库用户名
	
	public String getTaskTypeId() {
		return taskTypeId;
	}

	public void setTaskTypeId(String taskTypeId) {
		this.taskTypeId = taskTypeId;
	}

	private String dbPassword;//数据库密码
	
	private String isPool;//是否启用数据库连接池
	
	private Integer maxConnect;//最大连接数 
	
	private Integer maxIdle;//最大活跃连接数

	public String getDbId() {
		return dbId;
	}

	public void setDbId(String dbId) {
		this.dbId = dbId;
	}

	public String getDbAlias() {
		return dbAlias;
	}

	public void setDbAlias(String dbAlias) {
		this.dbAlias = dbAlias;
	}

	public String getDbIp() {
		return dbIp;
	}

	public void setDbIp(String dbIp) {
		this.dbIp = dbIp;
	}

	public Integer getDbPort() {
		return dbPort;
	}

	public void setDbPort(Integer dbPort) {
		this.dbPort = dbPort;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getDbServiceName() {
		return dbServiceName;
	}

	public void setDbServiceName(String dbServiceName) {
		this.dbServiceName = dbServiceName;
	}

	public String getDbUsername() {
		return dbUsername;
	}

	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public String getIsPool() {
		return isPool;
	}

	public void setIsPool(String isPool) {
		this.isPool = isPool;
	}

	public Integer getMaxConnect() {
		return maxConnect;
	}

	public void setMaxConnect(Integer maxConnect) {
		this.maxConnect = maxConnect;
	}

	public Integer getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(Integer maxIdle) {
		this.maxIdle = maxIdle;
	}
	
	
}
