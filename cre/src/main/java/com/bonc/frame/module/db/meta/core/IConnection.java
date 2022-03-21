package com.bonc.frame.module.db.meta.core;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;


public interface IConnection {

	public void doConnection() throws SQLException;
	
	public Connection getConnection();
	
	public void releaseConnection();
	
	public DatabaseMetaData getDatabaseMetaData() throws SQLException;
	
	public String getConnectionUser();
	
	public String getConnectionPassword();
	
	public String getDBResourceCode();
	
	public boolean isAlive();
}
