package com.bonc.frame.module.db.dbcp.entity;


import com.bonc.frame.applicationrunner.StaticDataLoader;
import com.bonc.frame.entity.staticdata.StaticDataVo;
import com.bonc.frame.util.SpringUtils;

import java.util.Properties;

/** 
 * 数据库连接参数实体
 * @author  qxl
 * @date    2017年7月19日 上午9:05:00 
 * @version 1.0
 */
public class DbEntity {
	private String dbId;//数据库链接标识
	
	private String dbAlias;//数据库链接别名
	
	private String dbIp;//数据库链接IP
	
	private Integer dbPort;//数据库链接端口
	
	private String dbType;//数据库链接类型
	
	private String dbServiceName;//数据库服务名
	
	private String dbUsername;//数据库用户名
	
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

	/**
	 * 将实体转成配置文件
	 * @return
	 */
	public Properties parse() throws Exception{
		Properties prop = new Properties();
		StaticDataLoader staticDataLoader = (StaticDataLoader) SpringUtils.getBean("staticDataLoader");
		StaticDataVo dbTypeInfoVo = staticDataLoader.getDatasourceCodeVo(getDbType());
		if(dbTypeInfoVo == null){
			throw new Exception("The dbType is null or illegal.dbType["+getDbType()+"].");
		}
		String driverClassName = dbTypeInfoVo.getRemarks1();
		String testSql = dbTypeInfoVo.getRemarks2();
		
		//jdbc:oracle:thin:@${ip}:${port}/${serviceName}
		String url = dbTypeInfoVo.getRemarks();
		url = url.replace("${ip}", getDbIp());
		url = url.replace("${port}", getDbPort().toString());
		url = url.replace("${serviceName}", getDbServiceName());
		
		prop.setProperty("driverClassName", driverClassName);
		prop.setProperty("url", url);
		prop.setProperty("username", getDbUsername());
		prop.setProperty("password", getDbPassword());

		prop.setProperty("maxActive", String.valueOf(getMaxConnect()));
		prop.setProperty("maxIdle", String.valueOf(getMaxIdle()));

		prop.setProperty("minIdle", "1");
		prop.setProperty("initialSize", "1");//初始化连接
		prop.setProperty("logAbandoned", "true");//连接被泄露时是否打印
		prop.setProperty("removeAbandoned", "true");//是否自动回收超时连接
		prop.setProperty("removeAbandonedTimeout", "3600");//超时时间(以秒数为单位)
		prop.setProperty("maxWait", "10000");//最长等待时间(毫秒)
		prop.setProperty("validationQuery", testSql);

		prop.setProperty("testOnBorrow", String.valueOf(true));

		return prop;
	}

    @Override
    public String toString() {
        return "DbEntity{" +
                "dbId='" + dbId + '\'' +
                ", dbAlias='" + dbAlias + '\'' +
                ", dbIp='" + dbIp + '\'' +
                ", dbPort=" + dbPort +
                ", dbType='" + dbType + '\'' +
                ", dbServiceName='" + dbServiceName + '\'' +
                ", dbUsername='" + dbUsername + '\'' +
                ", isPool='" + isPool + '\'' +
                ", maxConnect=" + maxConnect +
                ", maxIdle=" + maxIdle +
                '}';
    }
}
