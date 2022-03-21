package com.bonc.frame.module.db.meta.core.sql;


import com.bonc.frame.module.db.meta.core.model.TableInfo;

public class MySqlBuilder extends SqlBuilder{

	public MySqlBuilder(TableInfo tableInfo) {
		super(tableInfo);
	}

	@Override
	public void buildPage() {
		this.sql += " limit 0,"+this.defaultPages;
	}

}
