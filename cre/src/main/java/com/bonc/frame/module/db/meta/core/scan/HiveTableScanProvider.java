package com.bonc.frame.module.db.meta.core.scan;

import com.bonc.frame.module.db.meta.core.IConnection;
import com.bonc.frame.module.db.meta.core.IScan;
import com.bonc.frame.module.db.meta.core.model.ScanTask;
import com.bonc.frame.module.db.meta.core.model.StructInfo;
import com.bonc.frame.module.db.meta.core.model.TableInfo;
import com.bonc.frame.util.MD5Util;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HiveTableScanProvider implements IScan<TableInfo> {
	
	private IConnection conn;
	private ScanTask scanTask;
	
	public HiveTableScanProvider(IConnection conn, ScanTask scanTask) throws SQLException{
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
				List<TableInfo> temps = 	this.scanObjectMetaInfo1(key);
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
		ResultSet cSet = null;
		Statement stat = null;
		try{
			stat = conn.getConnection().createStatement();
			DatabaseMetaData dbmd = conn.getDatabaseMetaData();
			List<TableInfo> list = new ArrayList<TableInfo>();
			String [] args = scanTask.getTaskItems().split(",");
			//tSet = dbmd.getTables(null, conn.getConnectionUser(), key.isEmpty()?null:key, args);
			//tSet = stat.executeQuery("show tables like '"+key+"'");
			//while (tSet.next()) {
//				String tableName = tSet.getString(1);
				TableInfo tableInfo1 = new TableInfo();
			tableInfo1.setTableId(MD5Util.Bit16(conn.getDBResourceCode() + key + scanTask.getPackageId()));
				tableInfo1.setTableCode(key);
				tableInfo1.setTableName(key);
				tableInfo1.setResId(conn.getDBResourceCode());
				tableInfo1.setTableType("TABLE");
				tableInfo1.setTableKind(scanTask.getTableKind());
            tableInfo1.setScanId(scanTask.getTaskId());
				list.add(tableInfo1);
			//}
			for(TableInfo tableInfo : list){
				String tableName = tableInfo.getTableCode();
				cSet = stat.executeQuery("describe "+tableName);
			    List<StructInfo> structs = new LinkedList<StructInfo>();
			    while(cSet.next()){
			    	String columnCode = cSet.getString(1);
					if (columnCode == null || (columnCode = columnCode.trim()).isEmpty()) {
			    		continue;
			    	}
			    	if(columnCode.startsWith("#")){
			    		break;
			    	}
			    	String columnType = cSet.getString(2);
			    	String columnName = cSet.getString(3);
			    	StructInfo structInfo = new StructInfo();
			    	structInfo.setColumnId(MD5Util.Bit16(tableInfo.getTableId()+conn.getDBResourceCode()+tableName+columnCode+scanTask.getPackageId()));
			    	structInfo.setTableId(tableInfo.getTableId());
			    	structInfo.setColumnCode(columnCode);
					if (columnName == null || (columnName = columnName.trim()).isEmpty()) {
			    		structInfo.setColumnName(columnCode);
			    	}else if(columnName.length()>32){
			    		columnName = columnName.substring(0,32);
			    		String regEx = "[`~!@#$%^&*()\\-+={}':;,\\[\\].<>/?￥%…（）_+|【】‘；：”“’。，、？\\s]";
			    	    Pattern p = Pattern.compile(regEx);
			    	    Matcher m = p.matcher(columnName);
			    	    columnName = m.replaceAll("").trim();
			    		structInfo.setColumnName(columnName);
			    	}else{
			    		String regEx = "[`~!@#$%^&*()\\-+={}':;,\\[\\].<>/?￥%…（）_+|【】‘；：”“’。，、？\\s]";
			    	    Pattern p = Pattern.compile(regEx);
			    	    Matcher m = p.matcher(columnName);
			    	    columnName = m.replaceAll("").trim();
			    	    structInfo.setColumnName(columnName);
			    	}
			        //structInfo.setColumnType(cSet.getString("TYPE_NAME"));
			    	structInfo.setColumnType(changeDbType(columnType));
			        structInfo.setColumnSize(null);
			        structInfo.setIsPk((short)0);
			        structInfo.setDataType(null);
			        structInfo.setIsNullable(null);
			        structInfo.setColumnDefault(null);
                    structInfo.setScanId(scanTask.getTaskId());
			        structs.add(structInfo);
			    }
			    //cSet.close();
			    tableInfo.setStructs(structs);
			}
			return list;
		}catch(Exception e){
			throw new SQLException(e);
		}finally{
			try{
				if(tSet != null){
					tSet.close();
				}
				if(cSet!=null){
					cSet.close();
				}
				if(stat != null){
					stat.close();
				}
			}catch(Exception e){
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
		if(dbType == null){
			return "--";
		}
		dbType = dbType.toUpperCase().trim();
		switch(dbType){
			case "STRING":
			case "CHAR":
			case "VARCHAR":
                return "1";
			case "INT":
			case "TINYINT":
			case "SMALLINT":
                return "2";
			case "FLOAT":
			case "DOUBLE":
			case "DECIMAL":
			case "BIGINT":
				return "4";
			case "TIMESTAMP":
			case "DATE":
				return "8";
			case "BOOLEAN":
				return "1";
			case "CLOB":
				return "1";
			default:
                return "1";
		}
	}
	
}
