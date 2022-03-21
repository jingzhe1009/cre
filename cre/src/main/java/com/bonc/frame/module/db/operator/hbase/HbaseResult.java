package com.bonc.frame.module.db.operator.hbase;

import com.bonc.frame.entity.task.VariableMapping;
import com.bonc.frame.util.DateFormatUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.*;

/**
 * @author yedunyao
 * @date 2019/7/10 20:02
 */
public class HbaseResult {

    private List<Result> results;

    private byte[] lastRow;

    public HbaseResult() {

    }

    public HbaseResult(List<Result> results, byte[] lastRow, List<VariableMapping> variableMappings) {
        this.results = results;
        this.lastRow = lastRow;
        this.variableMappings = variableMappings;
    }

    private List<VariableMapping> variableMappings;

    public String getLastRow() {
        return Bytes.toString(lastRow);
    }

    public List<Map<String, Object>> getResult() throws Exception {
        if (results == null) {
            throw new IllegalStateException("获取hbase返回值失败，results为null");
        }
        if (variableMappings == null) {
            throw new IllegalStateException("获取hbase返回值失败，variableMappings为null");
        }

        // 根据映射和字段类型生成结果
        List<Map<String, Object>> list = new ArrayList<>(results.size());
        for (Result result : results) {
            final String rowKey = Bytes.toString(result.getRow());
            if (!StringUtils.isBlank(rowKey)) {
                final Map<String, Object> map = parseResult(result, variableMappings);
                map.put("RowKey", rowKey);
                list.add(map);
            }
        }
        return list;
    }

    private Map<String, Object> parseResult(Result result, List<VariableMapping> variableMappings) throws Exception {
        final List<Cell> cells = result.listCells();
        Map<String, Object> map = new HashMap<>();
        if (cells != null && cells.size() > 0) {
            for (Cell cell : cells) {
                final String family = Bytes.toString(cell.getFamilyArray(),
                        cell.getFamilyOffset(), cell.getFamilyLength());
                final String qualifier = Bytes.toString(cell.getQualifierArray(),
                        cell.getQualifierOffset(), cell.getQualifierLength());
                String key = family + HbaseCondition.COLON + qualifier;
                final String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());

                // 查找对应的变量
                for (VariableMapping variableMapping : variableMappings) {
                    if (key.equals(variableMapping.getColumnCode())) {
                        final String variableCode = variableMapping.getVariableCode();
                        final String variableTypeId = variableMapping.getVariableTypeId();
                        final Object realVal = parseHbaseValue(value, variableTypeId);
                        map.put(variableCode, realVal);
                    }
                }
            }
        }
        return map;
    }

    private Object parseHbaseValue(String val, String variableTypeId) {
        if (variableTypeId == null) {
            return val;
        }
        switch (variableTypeId) {
            case "1":
            case "10":
                return val;
            case "2":
                return Integer.valueOf(val);
            case "4":
            case "6":
                return Double.valueOf(val);
            case "7":
                return Long.valueOf(val);
            case "8":
                try {
                    Date date = DateFormatUtil.parse(val);
                    return date;
                } catch (Exception e) {
                    return val;
                }
            default:
                return val;
        }
    }

}
