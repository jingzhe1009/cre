package com.bonc.frame.module.db.sqlconvert;


import com.bonc.frame.entity.metadata.MetaDataColumn;
import com.bonc.frame.entity.metadata.RelationTable;
import com.bonc.frame.entity.task.ColumnMapping;
import com.bonc.frame.entity.task.VariableMapping;
import com.bonc.frame.module.vo.TableRelationVo;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import java.util.*;


/**
 * @author 作者: jxw
 * @version 版本: 1.0
 * @date 创建时间: 2016年11月8日 上午10:26:05
 */
public abstract class AbstractSqlConvert implements ISqlConvert {

    AbstractSqlConvert() {

    }

    @Override
    public String isExistTable(String tableName, String userName) {
        return null;
    }


    // insert into table(colname1,colname2) values(?,?)
    @Override
    public String insert(String tableName, List<MetaDataColumn> columns,
                         Map<String, Object> data, List<VariableMapping> outputVariableMappings) {
        StringBuilder sb = new StringBuilder("insert into \"");
        sb.append(tableName).append("\" (");

        List<String> columnCodeList = Lists.newLinkedList();
        List<String> valueList = Lists.newLinkedList();

        for (VariableMapping variableMapping : outputVariableMappings) {
            final String columnCode = variableMapping.getColumnCode();
            String variableCode = variableMapping.getVariableCode();
            String realColumn = null;
            for (MetaDataColumn column : columns) {
                if (column.getColumnCode().equals(columnCode)) {
                    realColumn = column.getColumnCode();
                }
            }
            final Object realVal = data.get(variableCode);
            if (StringUtils.isNotEmpty(realColumn)) {
                columnCodeList.add("\"" + realColumn + "\"");
                valueList.add(convertObject(realVal));
            }
        }

        final String columnCodes = Joiner.on(",").join(columnCodeList);
        final String values = Joiner.on(",").join(valueList);
        sb.append(columnCodes).append(") values(").append(values).append(")");
        return sb.toString();
    }

    // insert into table(colname1,colname2) values(?,?)
    @Override
    public String insert(String tableName, List<MetaDataColumn> columns) {
        StringBuilder sb = new StringBuilder("insert into ");
        sb.append(tableName).append(" (");
        List<String> columnCodeList = Lists.newLinkedList();
        List<String> valueList = Lists.newLinkedList();
        for (MetaDataColumn column : columns) {
            columnCodeList.add(column.getColumnCode());
            valueList.add("?");
        }
        final String columnCodes = Joiner.on(",").join(columnCodeList);
        final String values = Joiner.on(",").join(valueList);
        sb.append(columnCodes).append(") values(").append(values).append(")");
        return sb.toString().toUpperCase();
    }

    protected String convertObject(Object val) {
        String finalVal = null;
        if (val == null) {
            finalVal = "";
        } else if (val instanceof String) {
            StringBuilder sb = new StringBuilder("'");
            finalVal = sb.append(val.toString())
                    .append("'").toString();
        } else if (val instanceof Date) {
            finalVal = convertDate(val);
        } else {
            finalVal = String.valueOf(val);
        }
        return finalVal;
    }

    String convertDate(Object val) {
        val = DateFormatUtils.ISO_DATETIME_FORMAT.format(val);
        StringBuilder sb = new StringBuilder("'");
        return sb.append(val)
                .append("'").toString();
    }

    // order by "table"."id"
    @Override
    public String orderBy(ColumnMapping columnMapping) {
        if (columnMapping == null) {
            return "";
        }
        final String tableCode = columnMapping.getInputTableCode();
        final String columnCode = columnMapping.getInputColumnCode();
        if (org.apache.commons.lang3.StringUtils.isAnyEmpty(tableCode, columnCode)) {
            return "";
        }
        return " order by \"" + tableCode + "\".\"" + columnCode + "\" ";
    }

    // [A].[id] = 1 and [B].[name] = 'neal' -> A.id = 1 and B.name = 'neal'
    @Override
    public String selectWhere(String select, String where) {
        if (where == null || "".equals(where.trim()) || where.contains(";")) {
            return select;
        }
        where = where.replaceAll("\\[|\\]", "");
        if (where == null || "".equals(where.trim())) {
            return select;
        }
        return select + " where " + where;
    }

    // select a.column1 as "alias1", a.column2  as "alias2", [b.column1, ...]
    // from table1 a [left join table2 b on a.column1 = b.column1] [order by a.column1]
    @Override
    public String selectWithOrderBy(ColumnMapping columnMapping, String select) {
        return select + " " + orderBy(columnMapping);
    }

    // select a.column1 as "alias1", a.column2  as "alias2", [b.column1, ...]
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
            if (tableRelationVo == null
                    || tableRelationVo.getJoinStr().isEmpty()
                    || tableRelationVo.getCodeOnStr().isEmpty()) {
                sb.append(" join ").append(tableCode).append(" on 1=1 ");
                continue;
            }
            //拼接关联关系及关联表
            sb.append(" ").append(tableRelationVo.getJoinStr()).append(" ").append(tableCode)
                    .append(" on ").append(tableRelationVo.getCodeOnStr());
        }
        sb.insert(0, mainTable).append(" ");
        return select.append(sb).toString();
    }

    // a.column1 as "alias1"
    protected StringBuilder selectItem(VariableMapping mapping) {
        final String tableCode = mapping.getTableCode();
        StringBuilder sb = new StringBuilder(tableCode);
        sb.append(".").append(mapping.getColumnCode())
                .append(" as \"")
                .append(mapping.getVariableCode())
                .append("\"");
        return sb;
    }

    // a.column1 as "alias1", a.column2  as "alias2", [b.column1, ...]
    protected StringBuilder selectItems(Set<VariableMapping> mappings) {
        StringBuilder sb = new StringBuilder();
        for (VariableMapping mapping : mappings) {
            final StringBuilder builder = selectItem(mapping);
            sb.append(builder).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb;
    }

    public String genWhereClauses (List < VariableMapping > whereVariableMappings) {
        if (whereVariableMappings == null || whereVariableMappings.isEmpty()) {
            return null;
        }
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1 AND");
        for (VariableMapping variableMapping : whereVariableMappings) {
//            whereClause.append(tableCode).append(".\"").append(kpiFetchLimiters.getColumnCode())
//                    .append("\" = \"${").append(kpiFetchLimiters.getVariableCode()).append("}\" ")
//                    .append("AND");
            whereClause.append(genWhereClause(variableMapping.getTableCode(), variableMapping.getColumnCode(), variableMapping.getVariableCode()))
                    .append("AND");
        }
        String whereClauseString = whereClause.toString();
        return whereClauseString.substring(0, whereClauseString.length() - 3);//去掉最后末尾的 AND
    }

    protected String genWhereClause( String tableCode ,String columnCode,String variableCode){
        StringBuilder whereClause = new StringBuilder();
        if(org.apache.commons.lang3.StringUtils.isBlank(columnCode)){
            return null;
        }
        if (!org.apache.commons.lang3.StringUtils.isBlank(tableCode)) {
            whereClause.append(" ").append(tableCode).append(".");
        }
        whereClause.append(columnCode);
        if(!StringUtils.isBlank(variableCode)){
            whereClause.append(" = '${").append(variableCode).append("}' ");
        }else {
            whereClause.append(" = '' ");
        }
        return whereClause.toString();
    }

}

