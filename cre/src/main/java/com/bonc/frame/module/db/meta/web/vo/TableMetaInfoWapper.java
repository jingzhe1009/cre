package com.bonc.frame.module.db.meta.web.vo;

import com.bonc.frame.module.db.meta.core.model.StructInfo;
import com.bonc.frame.module.db.meta.core.model.TableInfo;

import java.util.ArrayList;
import java.util.List;


public class TableMetaInfoWapper{

	private boolean isUpdateTableInfo;
	private TableInfo tableInfo;
	private List<StructInfo> needInsertStructs;
	private List<StructInfo> needUpdateStructs;
	private List<StructInfo> needDeleteStructs;
	
	
	public TableMetaInfoWapper(TableInfo tableInfo){
		this.tableInfo = tableInfo;
		this.needInsertStructs = new ArrayList<StructInfo>();
		this.needUpdateStructs = new ArrayList<StructInfo>();
		this.needDeleteStructs = new ArrayList<StructInfo>();
	}

	public List<StructInfo> getNeedDeleteStructs() {
		return needDeleteStructs;
	}

	public void setNeedDeleteStructs(List<StructInfo> needDeleteStructs) {
		this.needDeleteStructs = needDeleteStructs;
	}

	public void addUpdateStructs(StructInfo structInfo){
		this.needUpdateStructs.add(structInfo);
	}
	
	public void addInsertStructs(StructInfo structInfo){
		this.needInsertStructs.add(structInfo);
	}
	
	public void addDeleteStructs(StructInfo structInfo){
		this.needDeleteStructs.add(structInfo);
	}
	
	public List<StructInfo> getNeedInsertStructs() {
		return needInsertStructs;
	}

	public void setNeedInsertStructs(List<StructInfo> needInsertStructs) {
		this.needInsertStructs = needInsertStructs;
	}

	public TableInfo getTableInfo() {
		return tableInfo;
	}


	public void setTableInfo(TableInfo tableInfo) {
		this.tableInfo = tableInfo;
	}


	public List<StructInfo> getNeedUpdateStructs() {
		return needUpdateStructs;
	}

	public void setNeedUpdateStructs(List<StructInfo> needUpdateStructs) {
		this.needUpdateStructs = needUpdateStructs;
	}


	public boolean isUpdateTableInfo() {
		return isUpdateTableInfo;
	}


	public void setUpdateTableInfo(boolean isUpdateTableInfo) {
		this.isUpdateTableInfo = isUpdateTableInfo;
	}
}
