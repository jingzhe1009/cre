package com.bonc.frame.module.db.dbcp;

import com.bonc.frame.module.cache.handler.EldestHandler;
import com.bonc.frame.module.db.dbcp.entity.DbEntity;
import com.bonc.frame.module.db.dbcp.mapper.DbMapper;
import com.bonc.frame.module.db.dbcp.repository.DataSourceRepository;
import com.bonc.frame.module.db.dbcp.repository.Repository;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 默认的数据库连接池实现类
 *
 * @author qxl
 * @version 1.0
 * @date 2017年7月18日 下午6:42:41
 */
public class DefaultDbPool implements IDbPool {
    private Log log = LogFactory.getLog(getClass());

//	private Repository<String, DataSource> dsRepository = DataSourceRepository.getInstance();

    private Repository<String, DataSource> dsRepository = new DataSourceRepository(
            new EldestHandler<String, DataSource>() {
                @Override
                public void handler(Map.Entry<String, DataSource> eldest) {
                    try {
                        log.info("删除过期数据源连接池，dbId: " + eldest.getKey());
                        destroyDbPool(eldest.getKey());
                    } catch (Exception e) {
                        log.warn("关闭缓存中过期的数据源失败", e);
                    }
                }
            }
    );

    private DbMapper mapper = new DbMapper();


    @Override
    //从数据库连接池中获取连接
    public Connection getConnection(String dbId) throws Exception {
        if (dbId == null || dbId.isEmpty()) {
            throw new Exception("Get connection from pool fail,the dbId is null.");
        }

        DataSource ds = this.dsRepository.get(dbId);
        if (ds == null) {
            createDbPool(dbId);
            ds = this.dsRepository.get(dbId);
        }
        if (ds == null) {
            throw new Exception("Get connection fail,conn't create db pool.dbId is [" + dbId + "].");
        }
        Connection conn = ds.getConnection();
        log.info("the getted connection is :" + conn);
        return conn;
    }

    @Override
    //直接创建连接
    public Connection getConnectionIm(String dbId) throws Exception {
        //从数据库查询连接参数
        DbEntity dbConfig = mapper.getDbConfigByDbId(dbId);
        if (dbConfig == null) {
            log.error("conn't select dbconfig from database by dbId[" + dbId + "].");
            throw new Exception("conn't select dbconfig from database by dbId[" + dbId + "].");
        }
        Properties prop = dbConfig.parse();
        Connection conn = DriverManager.getConnection(prop.getProperty("url"), prop);
//		System.out.println("the getConnectionIm conn is :"+conn);
        log.info("the getted connection is :" + conn);
        return conn;
    }

    @Override
    //关闭连接
    public void closeConnection(Connection conn) throws Exception {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            log.error(e);
            throw e;
        }

    }

    @Override
    //创建数据库连接池
    // 加锁，防止重复创建
    public synchronized void createDbPool(String dbId) throws Exception {
        log.info("开始创建数据源连接池，dbId: " + dbId);
        if (dbId == null || dbId.isEmpty()) {
//			log.error("the dbId is null.");
            throw new Exception("the dbId is null.");
        }
        createPool(dbId);
        //更新数据库IS_POOL状态
        mapper.updateDbPoolState(dbId, DbPoolConstant.IS_USE);
        log.info("创建数据源连接池成功.");
        return;
    }

    //私有方法，只创建连接池
    private DataSource createPool(String dbId) throws Exception {
        try {
            //判断连接池是否已经存在
            if (isExistPool(dbId)) {
                log.info("从缓存中获取到数据源，dbId: [" + dbId + "].");
                return dsRepository.get(dbId);
            }

            //从数据库查询连接参数
            DbEntity dbConfig = mapper.getDbConfigByDbId(dbId);
            if (dbConfig == null) {
                throw new Exception("查询数据源信息失败，dbId: [" + dbId + "].");
            }
            Properties prop = dbConfig.parse();
            log.info("创建数据源连接池配置信息，dbConfig: " + dbConfig);
            DataSource ds = BasicDataSourceFactory.createDataSource(prop);
            dsRepository.put(dbId, ds);
            return ds;
        } catch (Exception e) {
            throw new Exception("创建数据源失败，dbId: [" + dbId + "].", e);
        }
    }

    @Override
    //销毁数据库连接池
    public synchronized void destroyDbPool(String dbId) throws Exception {
//		log.info("start destroy dbpool...dbId["+dbId+"].the history dbpool is:"+dsRepository.getAllKeys());
        if (dbId == null || dbId.isEmpty()) {
            log.error("the dbId is null.");
            return;
        }
        DataSource ds = this.dsRepository.get(dbId);
        dsRepository.remove(dbId);
        destroyDbPool(ds);
        //更新数据库IS_POOL状态
        mapper.updateDbPoolState(dbId, DbPoolConstant.IS_NOT_USE);
//		log.info("destroy dbpool success.the dbpool is:"+dsRepository.getAllKeys());
    }

    private void destroyDbPool(DataSource ds) throws Exception {
        if (ds != null) {
            if (ds instanceof BasicDataSource) {
                BasicDataSource bds = (BasicDataSource) ds;
                bds.close();
                log.trace("连接池被关闭");
            }
        }
    }

    @Override
    //判断数据库连接池是否存在  true-存在;false-不存在
    public boolean isExistPool(String dbId) throws Exception {
        if (dbId == null || dbId.isEmpty()) {
            log.error("the dbId is null.");
            return false;
        }
        DataSource ds = this.dsRepository.get(dbId);
        if (ds != null) {
            return true;
        }
        return false;
    }

    @Override
    //获取内存中连接池的数量
    public int getDbPoolSize() {
        return (int) dsRepository.size();
    }

    @Override
    //初始化所有的连接池
    public void initAllDbPool() throws Exception {
        log.info("start init all started dbpool...");
        List<Map<String, Object>> result = mapper.getAllDbPoolId();
        log.info("the started  dbpool ids is:" + result);
        if (result == null) {
            return;
        }
        for (Map<String, Object> map : result) {
            String dbId = (String) map.get("dbId");
            if (dbId == null || dbId.isEmpty()) {
                continue;
            }
            createPool(dbId);
        }
//		log.info("init dbpool end.the memory dbpool is:" + dsRepository.getAllKeys());
    }


}
