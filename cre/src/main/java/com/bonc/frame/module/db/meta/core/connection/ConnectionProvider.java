package com.bonc.frame.module.db.meta.core.connection;

import com.bonc.frame.applicationrunner.StaticDataLoader;
import com.bonc.frame.entity.staticdata.StaticDataVo;
import com.bonc.frame.module.db.meta.core.IConnection;
import com.bonc.frame.module.db.meta.core.model.DatabaseResource;
import com.bonc.frame.util.SpringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class ConnectionProvider implements IConnection {
	
	private DatabaseResource databaseResource;
	private Connection conn;
	
	public ConnectionProvider(DatabaseResource databaseResource){
		this.databaseResource = databaseResource;
	}

	@Override
	public void doConnection() throws SQLException{
		StaticDataLoader staticDataLoader = (StaticDataLoader) SpringUtils.getBean("staticDataLoader");
		StaticDataVo dic   = staticDataLoader.getDatasourceCodeVo(String.valueOf(databaseResource.getResType()));

		String className = dic.getRemarks1();
		String url       = this.replaceDBUrl(dic.getRemarks().trim());
		Properties props = new Properties();
		props.put("user", databaseResource.getResUsername());
		props.put("password", databaseResource.getResPassword());
		props.put("remarksReporting","true");
        try {
			Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new SQLException(e.getMessage());
		}
        conn =  DriverManager.getConnection(url, props);
	}

	@Override
	public Connection getConnection() {
		return this.conn;
	}

	@Override
	public void releaseConnection() {
		try {
			if(conn!=null&&!conn.isClosed()){
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public DatabaseMetaData getDatabaseMetaData() throws SQLException {
		return conn.getMetaData();
	}

	@Override
	public String getConnectionUser() {
		return this.databaseResource.getResUsername();
	}

	@Override
	public String getConnectionPassword() {
		return this.databaseResource.getResPassword();
	}

	
	public String replaceDBUrl(String orgUrl){
		String target = orgUrl.replace("${ip}", this.databaseResource.getResIp())
				              .replace("${port}", String.valueOf(this.databaseResource.getResPort()))
				              .replace("${serviceName}", this.databaseResource.getResService());
		return target;
	}

	@Override
	public String getDBResourceCode() {
		return this.databaseResource.getResId();
	}

	@Override
	public boolean isAlive() {
		try {
			if(conn!=null&&!conn.isClosed()) return true;
		} catch (SQLException e) {
			return false;
		}
		return false;
	}
}
