package com.bonc.frame.module.db.meta.core.exception;

public class SqlExecuteException extends Exception{
	
	private String msg ;
	private String sql;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6558525235182457909L;

	public SqlExecuteException(String msg){
		super(msg);
	}
	
	
	public SqlExecuteException(String msg,String sql){
		super(msg);
		this.msg = msg;
		this.sql = sql;
	}


	public String getMsg() {
		return msg;
	}


	public void setMsg(String msg) {
		this.msg = msg;
	}


	public String getSql() {
		return sql;
	}


	public void setSql(String sql) {
		this.sql = sql;
	}
}
