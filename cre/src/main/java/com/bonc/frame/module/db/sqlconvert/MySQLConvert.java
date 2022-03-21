package com.bonc.frame.module.db.sqlconvert;

import com.bonc.frame.entity.metadata.MetaDataColumn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * hive语法的sql转换器
 * hive语法参考http://www.cnblogs.com/HondaHsu/p/4346354.html
 * <p>
 * 建表语法:
 * CREATE [EXTERNAL] TABLE [IF NOT EXISTS] table_name
 * [(col_name data_type [COMMENT col_comment], ...)]
 * [COMMENT table_comment]
 * [PARTITIONED BY (col_name data_type [COMMENT col_comment], ...)]
 * [CLUSTERED BY (col_name, col_name, ...)
 * [SORTED BY (col_name [ASC|DESC], ...)] INTO num_buckets BUCKETS]
 * [ROW FORMAT row_format]
 * [STORED AS file_format]
 * [LOCATION hdfs_path]
 *
 * @author qxl
 * @version 1.0
 * @date 2017年9月7日 上午10:41:15
 */
public class MySQLConvert extends AbstractSqlConvert {

    Log log = LogFactory.getLog(getClass());


    public MySQLConvert() {

    }

    @Override
    public String getColumnType(String type) {
        switch (type) {
            case "1":
                return "VARCHAR";
            case "2":
                return "INT";
            case "4":
                return "DOUBLE";
            case "6":
                return "DOUBLE";
            case "7":
                return "BIGINT";
            case "8":
                return "DATETIME";
            case "10":
                return "TEXT";
            default:
                return "VARCHAR";
        }
    }

    @Override
    public String createTable(String tableName, List<MetaDataColumn> columns) {
        if (columns == null || columns.isEmpty()) return null;

        Set<MetaDataColumn> metaDataColumnSet = new LinkedHashSet<>();
        metaDataColumnSet.addAll(columns);

        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
        for (MetaDataColumn column : metaDataColumnSet) {
            String columnCode = column.getColumnCode();
            String columnType = getColumnType(column.getColumnType());
            String columnSize = String.valueOf(column.getColumnSize());
            String isPK = "1".equals(column.getIsPk()) ? " primary key " : "";
            String isNull = ("0").equals(column.getIsNull()) ? " not null " : " null ";
            sb.append(columnCode).append(" ").append(columnType).append(" ");
            if (columnSize != null && !columnSize.isEmpty()) {
                sb.append("(").append(columnSize).append(") ").append(" ");
            }
            sb.append(isNull).append(" ").append(isPK).append(",");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return sb.toString().toUpperCase();
    }


}
