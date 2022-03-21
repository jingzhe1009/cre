package com.bonc.frame.module.db.operator.hbase;

import com.bonc.frame.module.db.dbcp.DbPoolFactory;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * try (HbaseHelper helper = new HbaseHelper()) {
 * helper.list();
 * helper.desc("table");
 * // ...
 * } catch (IOException e) {
 * // handler exception
 * }
 *
 * @author yedunyao
 * @date 2019/7/9 16:57
 */
public class HbaseHelper implements Closeable {

    private Log log = LogFactory.getLog(HbaseHelper.class);

    private String dbId;

    private HConnection connection;

    private HBaseAdmin hBaseAdmin;


    public HbaseHelper(HbaseConfig config) throws Exception {
        if (config == null) {
            throw new IllegalArgumentException("创建Hbase连接失败，参数config为null");
        }
        this.dbId = config.getDbId();

        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", config.getHost());
        configuration.set("hbase.zookeeper.property.clientPort", config.getPort());
//        configuration.set("hbase.client.ipc.pool.type","Reusable"); //Reusable，RoundRobin和ThreadLocal
        configuration.set("hbase.client.ipc.pool.size", config.getPoolSize() == null || config.getPoolSize() < 1 ? "1" : config.getPoolSize().toString());
        HBaseAdmin.checkHBaseAvailable(configuration);
        this.connection = HConnectionManager.createConnection(configuration);
        this.hBaseAdmin = new HBaseAdmin(configuration);
    }


    public boolean testConnecttion() {
        return connection == null ? false : true;
    }

    public ClusterStatus status() throws IOException {
        final ClusterStatus clusterStatus = hBaseAdmin.getClusterStatus();
        return clusterStatus;
    }

    public List<String> listNamespace() throws IOException {
        final NamespaceDescriptor[] namespaceDescriptors = hBaseAdmin.listNamespaceDescriptors();
        List<String> result = new ArrayList<>(namespaceDescriptors.length);
        for (NamespaceDescriptor namespaceDescriptor : namespaceDescriptors) {
            result.add(namespaceDescriptor.getName());
        }
        return ImmutableList.copyOf(result);
    }

    public List<String> list() throws IOException {
        final TableName[] tableNames = connection.listTableNames();
        List<String> result = new ArrayList<>(tableNames.length);
        for (TableName tableName : tableNames) {
            result.add(tableName.getNameAsString());
        }
        return ImmutableList.copyOf(result);
    }

    public List<String> listByNamespace(String namespace) throws IOException {
        if (StringUtils.isBlank(namespace)) {
            return list();
        }
        final HTableDescriptor[] tableDescriptors = hBaseAdmin.listTableDescriptorsByNamespace(namespace);
        List<String> result = new ArrayList<>(tableDescriptors.length);
        for (HTableDescriptor table : tableDescriptors) {
            result.add(table.getNameAsString());
        }
        return ImmutableList.copyOf(result);
    }

    public HTableDescriptor desc(String tableName) throws IOException {
        try (HTableInterface table = connection.getTable(tableName)) {
            return table.getTableDescriptor();
        }
    }

    public HbaseResult scan(String tableName, HbaseCondition hbaseCondition) throws IOException {
        List<Result> resultList = new LinkedList<>();
        Scan scan = hbaseCondition.scan();

        byte[] lastRow = null;
        try (HTableInterface table = connection.getTable(tableName);
             ResultScanner scanner = table.getScanner(scan)) {
            int count = 0;
            for (Result result : scanner) {
                lastRow = result.getRow();
                resultList.add(result);
                count++;
            }
            log.info("hbase扫描结果个数: " + count);
        }
        return new HbaseResult(resultList, lastRow, hbaseCondition.getVariableMappings());
    }

    /**
     * 调用次方法,需要对传入的参数进行非空判断
     */
    public HbaseResult get(String tableName, String rowKey, HbaseCondition hbaseCondition) throws IOException {
        try (HTableInterface table = connection.getTable(tableName)) {

            Get get = hbaseCondition.get(rowKey);

            Result result = table.get(get); // 获取到一行的数据
            log.info("hbase的get结果 " + result);

            List<Result> resultList = new LinkedList<>();
            resultList.add(result);
            log.info("hbase扫描结果个数: " + resultList.size());

            return new HbaseResult(resultList, result.getRow(), hbaseCondition.getVariableMappings());
        }
    }

    @Override
    public void close() throws IOException {
        if (hBaseAdmin != null) {
            hBaseAdmin.close();
        }
        if (connection != null) {
            connection.close();
        }
        try {
            DbPoolFactory.getHbaseDbPool().removeDbPool(dbId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
