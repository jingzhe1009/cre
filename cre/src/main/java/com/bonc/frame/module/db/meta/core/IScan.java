package com.bonc.frame.module.db.meta.core;

import java.sql.SQLException;
import java.util.List;


public interface IScan<T> {

	public List<T> scanObjectMetaInfo()throws SQLException;
	
	public String getScanTaskId();
	
}
