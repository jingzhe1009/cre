package com.bonc.frame.module.db.meta.core.scan;


import com.bonc.frame.module.db.meta.core.IConnection;
import com.bonc.frame.module.db.meta.core.IScan;
import com.bonc.frame.module.db.meta.core.model.ScanTask;
import com.bonc.frame.module.db.meta.core.model.StructInfo;
import com.bonc.frame.module.db.meta.core.model.TableInfo;
import com.bonc.frame.util.MD5Util;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class OracleTableScanProvider implements IScan<TableInfo> {
	
	private IConnection conn;
	private ScanTask scanTask;

	public OracleTableScanProvider(IConnection conn, ScanTask scanTask) throws SQLException {
		this.conn     = conn;
		this.scanTask = scanTask;
	}

	@Override
	public List<TableInfo> scanObjectMetaInfo() throws SQLException{//为了支持“,”分隔多个字符匹配 所以进行重写封装
		checkConnAlive();
		List<TableInfo> list = new ArrayList<TableInfo>();
		try{
			if(scanTask.getTaskKey().isEmpty()){
				list.addAll(this.scanObjectMetaInfo1(""));
			    return list;
			}
			String keys[] = scanTask.getTaskKey().split(",");
			for(String key:keys){
				List<TableInfo> temps = this.scanObjectMetaInfo1(key);
				for(TableInfo t : temps){
					if(!list.contains(t)){
						list.add(t);
					}
				}
			}
			return list;
		}catch(Exception e){
			throw new SQLException(e.getMessage());
		}finally{
			conn.releaseConnection();//釋放連接
		}
	}
	
	
	private List<TableInfo> scanObjectMetaInfo1(String key) throws SQLException, NoSuchAlgorithmException{
		ResultSet tSet = null;
		ResultSet cpkSet = null;
		ResultSet cSet = null;
		try{
			DatabaseMetaData dbmd = conn.getDatabaseMetaData();
			List<TableInfo> list = new ArrayList<TableInfo>();
			String [] args = scanTask.getTaskItems().split(",");
			//tSet = dbmd.getTables(null, conn.getConnectionUser(), key.isEmpty()?null:key, args);
			tSet =  dbmd.getTables(null, "%", key.isEmpty()?null:key, args);
				while (tSet.next()) {
					TableInfo tableInfo = new TableInfo();
					//tableInfo.setTableId(MD5Util.Bit16(conn.getDBResourceCode()+tSet.getString("TABLE_NAME")+scanTask.getPackageId()));
					final String table_name = tSet.getString("TABLE_NAME");
					tableInfo.setTableId(MD5Util.Bit16(conn.getDBResourceCode() + table_name + scanTask.getPackageId()));
					tableInfo.setTableCode(table_name);
					tableInfo.setTableName(tSet.getString("REMARKS") == null ||
							tSet.getString("REMARKS").isEmpty() ? table_name : tSet.getString("REMARKS"));
					tableInfo.setResId(conn.getDBResourceCode());
					tableInfo.setTableType(tSet.getString("TABLE_TYPE"));
					tableInfo.setTableKind(scanTask.getTableKind());
					//2017-09-09 qxl将获取列的方法dbmd.getColumns(null, conn.getConnectionUser(), tSet.getString("TABLE_NAME"), "%")
					//替换成dbmd.getColumns(null, getSchema(conn.getConnection()),tSet.getString("TABLE_NAME"), "%")
//					cSet = dbmd.getColumns(null, conn.getConnectionUser(), tSet.getString("TABLE_NAME"), "%");
					cSet = dbmd.getColumns(null, getSchema(conn.getConnection()), table_name, "%");
					cpkSet = dbmd.getPrimaryKeys(null, conn.getConnectionUser(), table_name);
				    List<String> paramaryKeys = new LinkedList<String>();
				    while(cpkSet.next()){
				    	paramaryKeys.add(cpkSet.getString("COLUMN_NAME"));
				    }
				    List<StructInfo> structs = new LinkedList<StructInfo>();
				    while(cSet.next()){
				    	StructInfo structInfo = new StructInfo();
						structInfo.setColumnId(MD5Util.Bit16(tableInfo.getTableId() + conn.getDBResourceCode() + table_name + cSet.getString("COLUMN_NAME") + scanTask.getPackageId()));
				    	structInfo.setTableId(tableInfo.getTableId());
				    	structInfo.setColumnCode(cSet.getString("COLUMN_NAME"));
				    	String remarks = null;
				    	if(cSet.getString("REMARKS")==null||cSet.getString("REMARKS").isEmpty()||cSet.getString("REMARKS").length()>32){
				    		remarks = cSet.getString("COLUMN_NAME");
				    	}else{
				    		remarks = cSet.getString("REMARKS");
				    	}
				    	structInfo.setColumnName(remarks);
				        //structInfo.setColumnType(cSet.getString("TYPE_NAME"));
				    	structInfo.setColumnType(changeDbType(cSet.getString("TYPE_NAME")));
				        structInfo.setColumnSize(cSet.getObject("COLUMN_SIZE")==null?0:cSet.getInt("COLUMN_SIZE"));
				        structInfo.setIsPk((short)0);
				        structInfo.setDataType(cSet.getShort("DATA_TYPE"));
				        structInfo.setIsNullable(cSet.getShort("NULLABLE"));
				        structInfo.setColumnDefault(cSet.getString("COLUMN_DEF"));
						structInfo.setScanId(scanTask.getTaskId());
				        for(String k:paramaryKeys){
				        	if(k.equals(cSet.getString("COLUMN_NAME"))){
				        		structInfo.setIsPk((short)1);
				        	}
				        }
				        structs.add(structInfo);
				    }
				    tableInfo.setStructs(structs);
					tableInfo.setScanId(scanTask.getTaskId());
					list.add(tableInfo);
				}
			return list;
		}catch(Exception e){
			throw new SQLException(e);
		}finally{
			if (tSet != null) {
				tSet.close();
			}
			if (cpkSet != null) {
				cpkSet.close();
			}
			if (cSet != null) {
				cSet.close();
			}
		}
	}
	
	//其他数据库不需要这个方法 oracle和db2需要
	private static String getSchema(Connection conn) throws Exception {
		String schema;
		schema = conn.getMetaData().getUserName();
		if ((schema == null) || (schema.length() == 0)) {
			throw new Exception("ORACLE数据库模式不允许为空");
		}
		return schema.toUpperCase().toString();

	}
	
	private void checkConnAlive() throws SQLException{
		if(!conn.isAlive()){
			conn.doConnection();
		}
	}
	
	
	@Override
	public String getScanTaskId() {
		return this.scanTask.getTaskId();
	}
	
	
	/**
	 * 根据数据库中的数据类型，转成相应的java类型
	 * @param dbType 数据库中的字段类型
	 * @return 相应的java中的字段类型
	 */
	private String changeDbType(String dbType) {
		dbType = dbType.toUpperCase().trim();
		switch(dbType){
			case "VARCHAR":
			case "VARCHAR2":
			case "CHAR":
				return "1";
			case "INT":
			case "SMALLINT":
			case "INTEGER":
				return "2";
			case "NUMBER":
			case "DECIMAL":
			case "BIGINT":
            case "FLOAT":
				return "4";
			case "DATETIME":
			case "TIMESTAMP":
			case "DATE":
				return "8";
			case "10":
				return "1";
			default:
				return "1";
		}
	}
	
}
