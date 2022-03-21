package com.bonc.frame.module.db.meta.core.connection;


import com.bonc.frame.module.db.meta.core.IConnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;


public class ConnectionPoolProvider implements IConnection {

	@Override
	public void doConnection() throws SQLException {

    }

	@Override
	public Connection getConnection() {
		return null;
	}

	@Override
	public void releaseConnection() {

    }

	@Override
	public DatabaseMetaData getDatabaseMetaData() throws SQLException {
		return null;
	}

	@Override
	public String getConnectionUser() {
		return null;
	}

	@Override
	public String getConnectionPassword() {
		return null;
	}

	@Override
	public String getDBResourceCode() {
		return null;
	}

	@Override
	public boolean isAlive() {
		return false;
	}

}
