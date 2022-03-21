package com.bonc.frame.module.db.meta.core;

public interface ISqlBuilder {

	public void setDefaultPages(int defaultPages);
	public void buildSql();
	public String getSql();
}
