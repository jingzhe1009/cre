package com.bonc.frame.module.db.operator.hbase;

import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.task.VariableMapping;
import com.bonc.frame.module.db.dbcp.DbPoolFactory;
import com.bonc.frame.module.db.operator.AbstractDbOperator;
import com.bonc.frame.service.datasource.DataSourceService;
import com.bonc.frame.util.ResponseResult;
import com.bonc.frame.util.SpringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.filter.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author yedunyao
 * @date 2019/7/9 17:19
 */
public class HbaseOperator extends AbstractDbOperator {

    private Log log = LogFactory.getLog(HbaseOperator.class);

    private DataSourceService dataSourceService;

    private HbaseConfig config;

    public HbaseOperator(String dbId) {
        super(dbId);
        dataSourceService = (DataSourceService) SpringUtils.getBean("dataSourceService");
        config = buildConfig();
    }

    private HbaseConfig buildConfig() {
        // 访问数据库，获取hbase配置
        final DataSource dataSource = dataSourceService.selectByPrimaryKey(dbId);
        return HbaseConfig.builder()
                .dbId(dbId)
                .host(dataSource.getDbIp())
                .port(String.valueOf(dataSource.getDbPort()))
                .namespace(dataSource.getDbServiceName())
                .poolSize(dataSource.getMaxConnect())
                .build();
    }


    @Override
    public ResponseResult pagedQuery(String sql, long offset, long size) throws Exception {
        return null;
    }

    public ResponseResult pagedQuery(String tableName, String condition, List<VariableMapping> variableMappings,
                                     String startRow, int size) throws Exception {
        log.info("开始执行分页查询，[tableName: " + tableName + " condition: " + condition +
                " startRow: " + startRow + " size: " + size + "]");
        HbaseHelper hbaseHelper = DbPoolFactory.getHbaseDbPool().getHbaseHelper(this.config);
        HbaseCondition hbaseCondition = HbaseCondition.newCondition()
                .addColumns(variableMappings)
                .startRow(startRow);

        if (StringUtils.isNotEmpty(condition)) {
            // [table].[rowkey] =|!= RegexStringComparator|SubstringComparator(value)

            final int startIndex = condition.lastIndexOf('(');
            final int endIndex = condition.lastIndexOf(')');
            String value = condition.substring(startIndex + 1, endIndex);

            CompareFilter.CompareOp compareOp;
            if (condition.contains("=")) {
                compareOp = CompareFilter.CompareOp.EQUAL;
            } else if (condition.contains("!=")) {
                compareOp = CompareFilter.CompareOp.NOT_EQUAL;
            } else {
                throw new IllegalArgumentException("hbase过滤表达式中不包含 '=' 或 '!='");
            }

            ByteArrayComparable comparable;
            if (condition.contains("RegexStringComparator")) {
                comparable = new RegexStringComparator(value);
            } else if (condition.contains("SubstringComparator")) {
                comparable = new SubstringComparator(value);
            } else {
                throw new IllegalArgumentException("hbase过滤表达式中不包含 'RegexStringComparator' 或 'SubstringComparator'");
            }

            RowFilter rowFilter = new RowFilter(compareOp, comparable);
            hbaseCondition.addFiter(rowFilter);
        }

        hbaseCondition.addPageFilter(size);

        final HbaseResult hbaseResult = hbaseHelper.scan(tableName, hbaseCondition);
        return ResponseResult.createSuccessInfo("", hbaseResult);

    }


    public ResponseResult get(String tableName, String rowKey, List<VariableMapping> variableMappings,
                              String startRow) throws Exception {
        log.info("开始通过get查询，[tableName: " + tableName + " rowKey: " + rowKey +
                " startRow: " + startRow + "] -- variableMappings: " + variableMappings);
        HbaseHelper hbaseHelper = DbPoolFactory.getHbaseDbPool().getHbaseHelper(config);
        HbaseCondition hbaseCondition = HbaseCondition.newCondition()
                .addColumns(variableMappings)
                .startRow(startRow);

        final HbaseResult hbaseResult = hbaseHelper.get(tableName, rowKey, hbaseCondition);
        log.info("get请求得到的结果 hbaseResult:" + hbaseResult.getResult());
        return ResponseResult.createSuccessInfo("", hbaseResult.getResult());

    }

    public static boolean volidateCondition(String condition) {
        if (StringUtils.isBlank(condition)) {
            return true;
        }
        if (!condition.contains("=") && !condition.contains("!=")) {
            throw new IllegalArgumentException("hbase过滤表达式中不包含 '=' 或 '!='");
        }
        if (!condition.contains("RegexStringComparator") && !condition.contains("SubstringComparator")) {
            throw new IllegalArgumentException("hbase过滤表达式中不包含 'RegexStringComparator' 或 'SubstringComparator'");
        }
        final int startIndex = condition.lastIndexOf('(');
        final int endIndex = condition.lastIndexOf(')');
        String value = condition.substring(startIndex + 1, endIndex);
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("hbase过滤表达式中行键过滤器必须有值 'RegexStringComparator|SubstringComparator(sonme_key)'");
        }
        return true;
    }

    @Override
    public ResponseResult createTable(String existsTable, String createTable) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResponseResult execute(String sql) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Map<String, String>> getAllTables(String dbId) throws Exception {
        List<Map<String, String>> result = Collections.emptyList();
        HbaseHelper hbaseHelper = DbPoolFactory.getHbaseDbPool().getHbaseHelper(config);
        final List<String> tables = hbaseHelper.listByNamespace(this.config.getNamespace());

        if (tables == null || tables.isEmpty()) {
            return result;
        }

        result = new ArrayList<>(tables.size());
        for (String tableName : tables) {
            Map<String, String> tableInfo = new HashMap<>();
            tableInfo.put("tableName", tableName);
            tableInfo.put("tableType", "TABLE");
            tableInfo.put("tableRemarks", tableName);
            result.add(tableInfo);
        }
        return result;
    }

    @Override
    public void deleteTable(String dbId, String tableName) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object handlerCloumnValue(ResultSet rs, int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) {
        // [table].[rowkey] =|!= RegexStringComparator|SubstringComparator(value)
        String condition = "[table].[rowkey] =|!= RegexStringComparator|SubstringComparator(value)";
        final int startIndex = condition.lastIndexOf('(');
        final int endIndex = condition.lastIndexOf(')');
        String value = condition.substring(startIndex + 1, endIndex);
        System.out.println(value);
    }
}
