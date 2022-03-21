package com.bonc.frame.module.db.sqlconvert;


import com.bonc.frame.entity.metadata.MetaDataColumn;
import com.bonc.frame.entity.metadata.RelationTable;
import com.bonc.frame.entity.task.ColumnMapping;
import com.bonc.frame.entity.task.VariableMapping;

import java.util.List;
import java.util.Map;

/**
 * sql转换器
 *
 * @author qxl
 * @version 1.0
 * @date 2017年9月7日 上午10:12:49
 */
public interface ISqlConvert {

    /**
     * 查看是否存在表
     *
     * @param
     * @return
     */
    String isExistTable(String tableName, String userName);

    /**
     * 创建表
     *
     * @param
     * @return
     */
    String createTable(String tableName, List<MetaDataColumn> columns);

    String insert(String tableName, List<MetaDataColumn> columns);

    String insert(String tableName, List<MetaDataColumn> columns,
                  Map<String, Object> data, List<VariableMapping> outputVariableMappings);

    String orderBy(ColumnMapping columnMapping);

    String selectWhere(String select, String where);

    String selectWithOrderBy(ColumnMapping columnMapping, String select);

    String select(List<VariableMapping> mappings, String mainTable);

    String selectWithJoin(List<VariableMapping> mappings, List<RelationTable> relationTables);

    String getColumnType(String type);

    String genWhereClauses(List<VariableMapping> whereVariableMappings);

}
