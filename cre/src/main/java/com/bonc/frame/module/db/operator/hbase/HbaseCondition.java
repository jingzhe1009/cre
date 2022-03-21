package com.bonc.frame.module.db.operator.hbase;

import com.bonc.frame.entity.task.VariableMapping;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.List;

/**
 * <pre>
 *     Scan scan = HbaseCondition.newCondition()
 *          .addColumn("family0", "qualifier0")
 *          .addColumn(family, qualifier)
 *          .addRegexStringRowFilter("*")
 *          .addNESubstringRowFilter("james")
 *          .addPageFilter(10)
 *          .startRow("startRowKey")
 *          .caching(10)
 *          .scan();
 * </pre>
 *
 * @author yedunyao
 * @date 2019/7/10 17:46
 */
public class HbaseCondition {

    private static final byte[] POSTFIX = new byte[]{0x00};
    public static final String COLON = ":";

    private Scan scan;

    private FilterList filterList;

    private List<VariableMapping> variableMappings;


    HbaseCondition() {
        this.scan = new Scan();
    }

    public static HbaseCondition newCondition() {
        return new HbaseCondition();
    }

    public HbaseCondition addColumns(List<VariableMapping> variableMappings) {
        for (VariableMapping variableMapping : variableMappings) {
            addColumn(variableMapping);
        }
        this.variableMappings = variableMappings;
        return this;
    }

    public HbaseCondition addColumn(VariableMapping variableMapping) {
        String columnCode = variableMapping.getColumnCode();// family:qualifier
        if (columnCode != null &&
                columnCode.contains(":")) {
            final List<String> columnList = Splitter.on(":").splitToList(columnCode);
            final String family = columnList.get(0);
            final String qualifier = columnList.get(1);
            addColumn(family, qualifier);
        }
        return this;
    }

    public HbaseCondition addColumn(String family, String qualifier) {
        scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
        return this;
    }

    public HbaseCondition filter(Filter filter) {
        addFiter(filter);
        return this;
    }

    public HbaseCondition filterList(FilterList filterList) {
        this.filterList = filterList;
        return this;
    }

    public HbaseCondition addFiter(Filter filter) {
        if (filterList == null) {
            filterList = new FilterList();
        }
        filterList.addFilter(filter);
        return this;
    }

    public HbaseCondition addRegexStringRowFilter(String value) {
        return addRegexStringRowFilter(CompareFilter.CompareOp.EQUAL, value);
    }

    public HbaseCondition addNERegexStringRowFilter(String value) {
        return addRegexStringRowFilter(CompareFilter.CompareOp.NOT_EQUAL, value);
    }

    public HbaseCondition addRegexStringRowFilter(CompareFilter.CompareOp rowCompareOp, String value) {
        RowFilter rowFilter = new RowFilter(rowCompareOp, new RegexStringComparator(value));
        addFiter(rowFilter);
        return this;
    }

    public HbaseCondition addSubstringRowFilter(String value) {
        return addSubstringRowFilter(CompareFilter.CompareOp.EQUAL, value);
    }

    public HbaseCondition addNESubstringRowFilter(String value) {
        return addSubstringRowFilter(CompareFilter.CompareOp.NOT_EQUAL, value);
    }

    public HbaseCondition addSubstringRowFilter(CompareFilter.CompareOp rowCompareOp, String value) {
        RowFilter rowFilter = new RowFilter(rowCompareOp, new SubstringComparator(value));
        addFiter(rowFilter);
        return this;
    }

    public HbaseCondition addPageFilter(int size) {
        addFiter(new PageFilter(size));
        return this;
    }

    public HbaseCondition startRow(String startRow) {
        byte[] result = HConstants.EMPTY_START_ROW;
        if (startRow != null) {
            result = Bytes.add(Bytes.toBytes(startRow), POSTFIX);
        }
        scan.setStartRow(result);
        return this;
    }

    public HbaseCondition startRow(byte[] startRow) {
        byte[] result = HConstants.EMPTY_START_ROW;
        if (startRow != null) {
            result = Bytes.add(startRow, POSTFIX);
        }
        scan.setStartRow(result);
        return this;
    }

    public HbaseCondition caching(int caching) {
        scan.setCaching(caching);
        return this;
    }

    public HbaseCondition batch(int batch) {
        scan.setBatch(batch);
        return this;
    }

    public List<VariableMapping> getVariableMappings() {
        return variableMappings;
    }

    public Scan scan() {
        scan.setFilter(filterList);
        return scan;
    }

    /**
     * 获取一行数据中的某些字段
     * @param rowKey
     * @return
     */
    public Get get(String rowKey) {
        Get get = new Get(Bytes.toBytes(rowKey));

        if (variableMappings == null || variableMappings.isEmpty()) {
            return get;
        }
        for (VariableMapping variableMapping : variableMappings) {
            String columnCode = variableMapping.getColumnCode();
            if (StringUtils.isBlank(columnCode)) {
                return null;
            }
            List<String> list = Splitter.on(":").splitToList(columnCode);
            if (list.size() != 2) {
                return null;
            }
            get.addColumn(Bytes.toBytes(list.get(0)), Bytes.toBytes(list.get(1)));
        }
        return get;
    }

}
