package com.bonc.frame.module.db.meta.core.sql;


import com.bonc.frame.module.db.meta.core.model.TableInfo;

public class OracleSqlBuilder extends SqlBuilder{

	public OracleSqlBuilder(TableInfo tableInfo) {
		super(tableInfo);
	}

	@Override
	public void buildPage() {
		String header = "select * from (select tmp_page.*, rownum row_id_meta123456 from (";
		String end    = ") tmp_page where rownum <= "+this.defaultPages+") where row_id_meta123456 > 0";
		this.sql = header + sql + end;
	}
}
