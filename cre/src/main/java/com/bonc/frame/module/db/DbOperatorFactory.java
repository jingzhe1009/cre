package com.bonc.frame.module.db;

import com.bonc.frame.applicationrunner.StaticDataLoader;
import com.bonc.frame.config.Config;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.module.cache.ICache;
import com.bonc.frame.module.cache.heap.FIFOCache;
import com.bonc.frame.module.cache.heap.SynchronizedCache;
import com.bonc.frame.module.db.operator.HiveOperator;
import com.bonc.frame.module.db.operator.OracleOperator;
import com.bonc.frame.module.db.operator.XCloudOperator;
import com.bonc.frame.module.db.operator.hbase.HbaseOperator;
import com.bonc.frame.util.ResponseResult;
import com.bonc.frame.util.SpringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author qxl
 * @date 2017年8月21日 下午4:35:10
 * @version 1.0
 */
public class DbOperatorFactory {
	private static Log log = LogFactory.getLog(DbOperatorFactory.class);

	private static ICache<String, Set<String>> createTableCache;

	private static StaticDataLoader staticDataLoader;

	static {
		staticDataLoader = (StaticDataLoader) SpringUtils.getBean("staticDataLoader");
		createTableCache = new SynchronizedCache<>(new FIFOCache<String, Set<String>>(Config.DATASOURCE_TABLE_CREATE_CACHE_MAX_SIZE));
	}

    public static ResponseResult execute(String sql, DataSource dbInfo) throws Exception {
        final IDbOperator dbOperator = getIDbOperator(dbInfo);
        return dbOperator.execute(sql);
    }

    public static ResponseResult pagedQuery(String sql, DataSource dbInfo, long offset, long size) throws Exception {
        log.info(String.format("开始分页扫描元数据表，[sql: %s, dbInfo: %s, offset: %d, size: %d]\n", sql,
                dbInfo.toString(), offset, size));
        final IDbOperator dbOperator = getIDbOperator(dbInfo);
        return dbOperator.pagedQuery(sql, offset, size);
    }

    /**
     * 根据数据源Id获取此数据源下所有表信息
     *
     * @param dbId
     * @param dbType
     * @return
     * @throws Exception
     */
    public static List<Map<String, String>> getAllTables(String dbId, String dbType) throws Exception {
        if (dbId == null) {
            throw new Exception("The dbInfo is null.");
        }
        if (dbType == null) {
            throw new Exception("The dbType is null.");
        }

        final IDbOperator dbOperator = getIDbOperator(dbId, dbType);
        return dbOperator.getAllTables(dbId);
    }

    public static ResponseResult saveAndCreateTable(String existTableSql, String createTableSql,
                                                    String insertSql, DataSource dbInfo) throws Exception {
        if (StringUtils.isBlank(insertSql)) {
            throw new IllegalArgumentException("insertSql 不能为null");
        }

        final IDbOperator dbOperator = getIDbOperator(dbInfo);
        if (createTableSql != null) {
            Set createSqlSet = createTableCache.get(dbInfo.getDbId());
            if (createSqlSet == null) {
                createSqlSet = new LinkedHashSet();
            }
            if (!createSqlSet.contains(createTableSql)) {
                // 创建表
                dbOperator.createTable(existTableSql, createTableSql);
                createSqlSet.add(createTableSql);
                createTableCache.put(dbInfo.getDbId(), createSqlSet);
            }
        }
        return dbOperator.execute(insertSql);
    }

    /**
     * 根据数据源Id删除表
     *
     * @param dbId
     * @param dbType
     * @param tableName
     * @return
     * @throws Exception
     */
    public static void dropTable(String dbId, String dbType, String tableName) throws Exception {
        if (dbId == null) {
            throw new Exception("The dbInfo is null.");
        }
        if (dbType == null) {
            throw new Exception("The dbType is null.");
        }
        final IDbOperator dbOperator = getIDbOperator(dbId, dbType);
        dbOperator.deleteTable(dbId, tableName);
    }

    private static IDbOperator getIDbOperator(String dbId, String dbTypeId) {
        if (dbId == null) {
            throw new IllegalArgumentException("The dbInfo is null.");
        }
        if (dbTypeId == null) {
            throw new IllegalArgumentException("The dbTypeId is null.");
        }

        String dbType = staticDataLoader.getDatasourceCodeValue(dbTypeId);
        IDbOperator dbOperator = null;
        switch (dbType) {
            case "HIVE":
            case "HIVE2":
                dbOperator = new HiveOperator(dbId);
                break;
            case "ORACLE":
                dbOperator = new OracleOperator(dbId);
                break;
            case "HBASE":
                dbOperator = new HbaseOperator(dbId);
                break;
            case "XCLOUD":
                dbOperator = new XCloudOperator(dbId);
                break;
            default:
                dbOperator = null;
        }
        if (dbOperator == null) {
            throw new UnsupportedOperationException("暂不支持的数据源类型：[" + dbType + "].");
        }
        return dbOperator;
    }

    private static IDbOperator getIDbOperator(DataSource dataSource) {
        if (dataSource == null) {
            log.info("The dbInfo is null.");
            throw new IllegalArgumentException("The dbInfo is null.");
        }
        return getIDbOperator(dataSource.getDbId(), dataSource.getDbType());
    }
}
