package com.bonc.frame.module.db.dialect;

import com.bonc.frame.module.db.Page;

/**
 * @author yedunyao
 * @date 2019/6/10 19:06
 */
public class HiveDialect implements Dialect {

    // SELECT * FROM ( SELECT ROW_NUMBER() over(order by id) AS rnum, A.* FROM (SELECT * FROM USER) A) T WHERE rnum BETWEEN 1 AND 2;
    @Override
    public String getPageSql(String sql, Page<?> page) {
        StringBuffer pageSql = new StringBuffer(sql.length() + 100);
//        pageSql.append("SELECT * FROM ( SELECT ROW_NUMBER() over() AS rnum, A.* FROM ( ");
        pageSql.append(sql);
        pageSql.append(" WHERE rnum BETWEEN ");
        final long start = page.getPageNo();
        pageSql.append(start).append(" AND ")
                .append(start + page.getPageSize() - 1);
        return pageSql.toString();
    }

}
