package com.bonc.frame.module.db.meta.core.sql;

import com.bonc.frame.module.db.meta.core.ISqlBuilder;
import com.bonc.frame.module.db.meta.core.model.StructInfo;
import com.bonc.frame.module.db.meta.core.model.TableInfo;

import java.util.List;


public abstract class SqlBuilder implements ISqlBuilder {
	
	private static final String defaultAlias = "TABLE";

	protected int defaultPages  = 10;//默认分页数
	
	protected String alias;
	
	protected String sql;
	
	protected TableInfo tableInfo;
	
	public SqlBuilder(TableInfo tableInfo){
		this.alias = defaultAlias+tableInfo.getTableId();
		this.tableInfo = tableInfo;
	}
	
	public void setDefaultPages(int defaultPages) {
		this.defaultPages = defaultPages;
	}

	public String getSql(){
		return this.sql;
	}
	
	public void buildSql(){
		buildSelectItem();
		buildFromTable();
		buildWhere();
		buildPage();
	}
	
	public void buildSelectItem(){
		StringBuffer sb = new StringBuffer();
		List<StructInfo> list = tableInfo.getStructs();
		sb.append("select ");
		for(StructInfo struct:list){
			sb.append(this.alias).append(".").append(struct.getColumnCode()).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		this.sql = sb.toString();
	}
	
	public void buildFromTable(){
		StringBuffer sb = new StringBuffer();
		sb.append(" from ").append(tableInfo.getTableCode()).append(" ").append(this.alias);
	    this.sql += sb.toString();
	}
	
	public void buildWhere(){
		
	}
	
	public abstract void buildPage();
}
