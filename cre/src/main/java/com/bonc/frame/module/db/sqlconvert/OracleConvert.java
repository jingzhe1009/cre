package com.bonc.frame.module.db.sqlconvert;

import com.bonc.frame.entity.metadata.MetaDataColumn;
import com.bonc.frame.entity.metadata.RelationTable;
import com.bonc.frame.entity.task.VariableMapping;
import com.bonc.frame.module.vo.TableRelationVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * oracle语法的sql转换器
 *
 * @author qxl
 * @version 1.0
 * @date 2017年9月7日 上午10:41:15
 */
public class OracleConvert extends AbstractSqlConvert {

    Log log = LogFactory.getLog(getClass());


    public OracleConvert() {
    }


    @Override
    public String isExistTable(String tableName, String userName) {
        String exitTableSql = null;
        if (StringUtils.isAnyEmpty(tableName, userName)) {
            throw new IllegalArgumentException("tableName or userName is empty");
        }
        exitTableSql = "SELECT COUNT(1) NUM FROM ALL_TABLES WHERE TABLE_NAME = '" + tableName + "' AND OWNER='" + userName + "'";
        return exitTableSql.toUpperCase();
    }

    @Override
    public String createTable(String tableName, List<MetaDataColumn> columns) {
        if (columns == null || columns.isEmpty()) return null;

        Set<MetaDataColumn> metaDataColumnSet = new LinkedHashSet<>();
        metaDataColumnSet.addAll(columns);

        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE \"").append(tableName).append("\" (");
        for (MetaDataColumn column : metaDataColumnSet) {
            String columnCode = column.getColumnCode();
            String columnType = getColumnType(column.getColumnType());
            String columnSize = String.valueOf(column.getColumnSize());
            String isPK = "1".equals(column.getIsPk()) ? " primary key " : "";
            String isNull = ("0").equals(column.getIsNull()) ? " not null " : " null ";
            sb.append("\"").append(columnCode).append("\" ").append(columnType).append(" ");
            if (columnSize != null && !columnSize.isEmpty()) {
                sb.append("(").append(columnSize).append(") ").append(" ");
            }
            sb.append(isNull).append(" ").append(isPK).append(",");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return sb.toString().toUpperCase();
    }

    @Override
    public String getColumnType(String type) {
        // 和数据库中的码表对应
        switch (type) {
            case "1":
                return "VARCHAR2";
            case "2":
                return "INTEGER";
            case "4":
            case "6":
                return "DECIMAL";
            case "7":
                return "NUMBER";
            case "8":
                return "DATE";
            case "10":
                return "CLOB";
            default:
                return "VARCHAR2";
        }
    }

    // [A].[id] = 1 and [B].[name] = 'neal' -> "A"."id" = 1 and "B"."name" = 'neal'
    @Override
    public String selectWhere(String select, String where) {
        if (StringUtils.isBlank(where) || where.contains(";")) {    // 防止注入sql
            return select;
        }
        // 给表名和字段加双引号，避免大小写问题，表名字段名的匹配规则：非空白字符非点符号非双引号的一个或多个字符
//        where = " " + where + " ";
//        where = where.replaceAll("\\s+([^\\s\"\\.]+)\\.([^\\s\"\\.]+)\\s+", " \"$1\"\\.\"$2\" ");
        where = where.replaceAll("\\[|\\]", "\"");
        return select + " where " + where;
    }

    // select a.column1 as "alias1", a.column2  as "alias2", [b.column1, ...]
    // from table1 a [left join table2 b on a.column1 = b.column1]
    @Override
    public String select(List<VariableMapping> mappings, String mainTable) {
        Set<VariableMapping> variableMappingSet = new LinkedHashSet<>();
        variableMappingSet.addAll(mappings);

        StringBuilder sb = new StringBuilder("select ");
        sb.append(selectItems(variableMappingSet)).append(" from \"")
                .append(mainTable).append("\"");

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
        StringBuffer nullRelTable = new StringBuffer();
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
                    || codeOnStr.isEmpty()) {
                sb.append(" left outer join \"").append(tableCode).append("\" on 1=1 ");
                continue;
            }

            // [test_loan.userId] = [test_age.id]
            codeOnStr = resolveOn(codeOnStr);

            //拼接关联关系及关联表
            sb.append(" ").append(tableRelationVo.getJoinStr()).append(" \"").append(tableCode)
                    .append("\" on ").append(codeOnStr);
        }
        mainTable = "\"" + mainTable + "\"";
        sb.insert(0, mainTable).append(" ");
        return select.append(sb).toString();
    }

    public String genWhereClauses(List<VariableMapping> whereVariableMappings){
        if(whereVariableMappings == null || whereVariableMappings.isEmpty()){
            return null;
        }
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1 AND");
        for(VariableMapping variableMapping : whereVariableMappings){
//            whereClause.append(tableCode).append(".\"").append(kpiFetchLimiters.getColumnCode())
//                    .append("\" = \"${").append(kpiFetchLimiters.getVariableCode()).append("}\" ")
//                    .append("AND");
            whereClause.append(genWhereClause(variableMapping.getTableCode(),variableMapping.getColumnCode(),variableMapping.getVariableCode()))
                    .append("AND");
        }
        String whereClauseString = whereClause.toString();
        return whereClauseString.substring(0,whereClauseString.length()-3);//去掉最后末尾的 AND
    }

    public String genWhereClause( String tableCode ,String columnCode,String variableCode){
        StringBuilder whereClause = new StringBuilder();
        if(StringUtils.isBlank(columnCode)){
            return null;
        }
        if (!StringUtils.isBlank(tableCode)) {
            whereClause.append(" \"").append(tableCode).append("\".\"");
        }
        whereClause.append(columnCode);
        if(!StringUtils.isBlank(variableCode)){
            whereClause.append("\" = '${").append(variableCode).append("}' ");
        }else {
            whereClause.append("\" = '' ");
        }
        return whereClause.toString();
    }


    // a.column1 as "alias1"
    public StringBuilder selectItem(VariableMapping mapping) {
        String tableCode = mapping.getTableCode();
        tableCode = "\"" + tableCode + "\"";
        StringBuilder sb = new StringBuilder(tableCode);
        sb.append(".\"").append(mapping.getColumnCode())
                .append("\" as \"")
                .append(mapping.getVariableCode())
                .append("\"");
        return sb;
    }

    // a.column1 as "alias1", a.column2  as "alias2", [b.column1, ...]
    public StringBuilder selectItems(Set<VariableMapping> mappings) {
        StringBuilder sb = new StringBuilder();
        for (VariableMapping mapping : mappings) {
            final StringBuilder builder = selectItem(mapping);
            sb.append(builder).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb;
    }

    // [test_loan.userId] = [test_age.id] -> "test_loan"."userId" = "test_age"."id"
    private static String resolveOn(String codeOnStr) {
        codeOnStr = codeOnStr.trim();
        codeOnStr = codeOnStr.replaceAll("\\[|\\]", "\"");
        return codeOnStr.replaceAll("\\.", "\".\"");
    }

    @Override
    public String convertObject(Object val) {
        String finalVal = null;
        if (val == null) {
            finalVal = "";
        } else if (val instanceof String) {
            finalVal = splitClob(val.toString());
        } else if (val instanceof Date) {
            finalVal = convertDate(val);
        } else {
            finalVal = String.valueOf(val);
        }
        return finalVal;
    }

    private static String splitClob(String text) {
        return splitClob(text, 4000);
    }

    // to_clob('aaa...')||to_clob('bbb...')
    private static String splitClob(String text, final int MAX_LEN) {
        if (text == null) {
            return "''";
        }

        final int length = text.length();
        if (length <= MAX_LEN) {
            return "'" + text + "'";
        }

        StringBuilder builder = new StringBuilder(text);
        StringBuilder result = new StringBuilder(text.length() + 110);
        int start = 0;
        int end = start + MAX_LEN;
        while (end <= length) {
            final String sequence = (String) builder.subSequence(start, end);
            result.append("to_clob('" + sequence + "')").append("||");
            start = end;
            end = start + MAX_LEN;
        }

        final String sequence = (String) builder.subSequence(start, length);
        result.append("to_clob('" + sequence + "')");

        return result.toString();
    }

}
