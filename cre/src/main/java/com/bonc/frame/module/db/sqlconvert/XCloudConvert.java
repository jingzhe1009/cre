package com.bonc.frame.module.db.sqlconvert;


import com.bonc.frame.entity.metadata.MetaDataColumn;
import com.bonc.frame.entity.metadata.RelationTable;
import com.bonc.frame.entity.task.VariableMapping;
import com.bonc.frame.module.vo.TableRelationVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * xcloud语法的sql转换器
 * <p>
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
 * <p>
 * 组成sql
 * @date 2017年9月7日 上午10:41:15
 */
public class XCloudConvert extends AbstractSqlConvert {

    private Log log = LogFactory.getLog(getClass());

    public XCloudConvert() {
        super();
    }

    @Override
    public String getColumnType(String type) {
        // 和数据库中的码表对应
        switch (type) {
            case "1":
            case "10":
                return "STRING";
            case "2":
                return "INT";
            case "4":
                return "DOUBLE";
            case "6":
                return "DECIMAL";
            case "7":
                return "LONG";
            case "8":
                return "DATE";
            case "9":
                return "STRING";
            default:
                return "STRING";
        }
    }

    @Override
    public String createTable(String tableName, List<MetaDataColumn> columns) {
        throw new UnsupportedOperationException("xcloud暂不支持建表功能");
    }


    // select a.column1 alias1, a.column2 alias2, [b.column1, ...]
    // from table1 a [left join table2 b on a.column1 = b.column1]
    @Override
    public String select(List<VariableMapping> mappings, String mainTable) {
        Set<VariableMapping> variableMappingSet = new LinkedHashSet<>();
        variableMappingSet.addAll(mappings);

        StringBuilder sb = new StringBuilder("select ");
        sb.append(selectItems(variableMappingSet)).append(" from ")
                .append(mainTable);

        return sb.toString();
    }

    @Override
    public String selectWithJoin(List<VariableMapping> mappings, List<RelationTable> relationTables) {
        Set<VariableMapping> variableMappingSet = new LinkedHashSet<>();
        variableMappingSet.addAll(mappings);

        StringBuilder select = new StringBuilder("select ");
        select.append(selectItems(variableMappingSet)).append(" from ");

        // 拼接join
        StringBuilder sb = new StringBuilder();
        String mainTable = null;//主表
        //存储没有配置关联关系的表名，格式(tableA,tableB)
        //配置关联关系的存储格式：tableA left/right join tableB on xxx
        final String mainTableFlag = "1";//主表标志
        for (RelationTable table : relationTables) {
            String tableCode = table.getTableCode();
            String isMain = table.getIsMain();
            if (isMain != null && mainTableFlag.equals(isMain)) {
                mainTable = tableCode;
                continue;
            }
            TableRelationVo tableRelationVo = TableRelationVo.parseToTableRelationVo(table.getTableRelation());
            //没有配置关联关系,直接拼接join table on 1=1
            String codeOnStr = tableRelationVo.getCodeOnStr();
            if (tableRelationVo == null
                    || tableRelationVo.getJoinStr().isEmpty()
                    || codeOnStr.trim().isEmpty()) {
                sb.append(" left outer join ").append(tableCode).append(" on 1=1 ");
                continue;
            }

            codeOnStr = resolveOn(codeOnStr);

            //拼接关联关系及关联表
            sb.append(" ").append(tableRelationVo.getJoinStr()).append(" ").append(tableCode)
                    .append(" on ").append(codeOnStr);
        }
        mainTable = "" + mainTable + "";
        sb.insert(0, mainTable).append(" ");
        return select.append(sb).toString();
    }

    // [test_loan.userId] = [test_age.id] -> test_loan.userId = test_age.id
    private static String resolveOn(String codeOnStr) {
        codeOnStr = codeOnStr.trim();
        codeOnStr = codeOnStr.replaceAll("\\[|\\]", "");
        return codeOnStr;
    }

    // a.column1 as "alias1"
    @Override
    public StringBuilder selectItem(VariableMapping mapping) {
        final String tableCode = mapping.getTableCode();
        StringBuilder sb = new StringBuilder(tableCode);
        sb.append(".").append(mapping.getColumnCode())
                .append(" as \"")
                .append(mapping.getVariableCode())
                .append("\" ");
        return sb;
    }

    // a.column1 as "alias1", a.column2 as "alias2", [b.column1, ...]
    @Override
    public StringBuilder selectItems(Set<VariableMapping> mappings) {
        StringBuilder sb = new StringBuilder();
        for (VariableMapping mapping : mappings) {
            final StringBuilder builder = selectItem(mapping);
            sb.append(builder).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb;
    }

    // INSERT
    @Override
    public String insert(String tableName, List<MetaDataColumn> columns,
                         Map<String, Object> data, List<VariableMapping> outputVariableMappings) {
        throw new UnsupportedOperationException("不支持hive类型数据源的insert操作");
    }

    @Override
    public String insert(String tableName, List<MetaDataColumn> columns) {
        throw new UnsupportedOperationException("不支持hive类型数据源的insert操作");
    }
}
