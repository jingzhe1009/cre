/* Copyright 2013-2015 www.snakerflow.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bonc.frame.module.db.dialect;


import com.bonc.frame.module.db.Page;

/**
 * Oracle数据库方言实现
 *
 * @author yuqs
 * @since 1.0
 */
public class XCloudDialect implements Dialect {

    public String getPageSql(String sql, Page<?> page) {
        StringBuilder pageSql = new StringBuilder(sql);
        pageSql.append(" limit(");
        long start = page.getPageNo();
        pageSql.append(start);
        pageSql.append(", ");
        pageSql.append(start + page.getPageSize() - 1);
        pageSql.append(" ) ");
        return pageSql.toString();
    }
}
