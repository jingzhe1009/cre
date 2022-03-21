package com.bonc.frame.module.db.meta.web.vo;

import java.util.List;

public class MetaChangeLog {

	public int inserts;
	public int updates;
	public List<TableMetaInfoWapper> insertList;
	public List<TableMetaInfoWapper> updateList;
	
	
	
	public MetaChangeLog(int inserts, int updates,
			List<TableMetaInfoWapper> insertList,
			List<TableMetaInfoWapper> updateList) {
		super();
		this.inserts = inserts;
		this.updates = updates;
		this.insertList = insertList;
		this.updateList = updateList;
	}
	
	
	public int getInserts() {
		return inserts;
	}
	public void setInserts(int inserts) {
		this.inserts = inserts;
	}
	public int getUpdates() {
		return updates;
	}
	public void setUpdates(int updates) {
		this.updates = updates;
	}
	public List<TableMetaInfoWapper> getInsertList() {
		return insertList;
	}
	public void setInsertList(List<TableMetaInfoWapper> insertList) {
		this.insertList = insertList;
	}
	public List<TableMetaInfoWapper> getUpdateList() {
		return updateList;
	}
	public void setUpdateList(List<TableMetaInfoWapper> updateList) {
		this.updateList = updateList;
	}
}
