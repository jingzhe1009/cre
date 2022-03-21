package com.bonc.frame.module.db.dbcp;

import com.bonc.frame.module.cache.handler.EldestHandler;
import com.bonc.frame.module.db.dbcp.entity.DbEntity;
import com.bonc.frame.module.db.dbcp.mapper.DbMapper;
import com.bonc.frame.module.db.dbcp.repository.HbaseHelperRepository;
import com.bonc.frame.module.db.dbcp.repository.Repository;
import com.bonc.frame.module.db.operator.hbase.HbaseConfig;
import com.bonc.frame.module.db.operator.hbase.HbaseHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.Set;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/7/7 10:03
 */
public class HbaseDbPool {
    private Log log = LogFactory.getLog(getClass());

    private DbMapper mapper = new DbMapper();

    private Repository<String, HbaseHelper> dsRepository = new HbaseHelperRepository(
            new EldestHandler<String, HbaseHelper>() {
                @Override
                public void handler(Map.Entry<String, HbaseHelper> eldest) {
                    try {
                        log.info("删除过期数据源连接池，dbId: " + eldest.getKey());
                        destroyDbPool(eldest.getKey());
                    } catch (Exception e) {
                        log.warn("关闭缓存中过期的数据源失败", e);
                    }
                }
            }
    );

    public HbaseDbPool() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shutdownThreadPool();
            }
        });
    }

    public HbaseHelper getHbaseHelper(String dbId) throws Exception {
        if (dbId == null || dbId.isEmpty()) {
            throw new Exception("Get connection from pool fail,the dbId is null.");
        }

        HbaseHelper ds = this.dsRepository.get(dbId);
        if (ds == null) {
            createDbPool(dbId);
            ds = this.dsRepository.get(dbId);
        }
        if (ds == null) {
            throw new Exception("Get connection fail,conn't create db pool.dbId is [" + dbId + "].");
        }
        log.info("the getted HbaseHelper is :" + ds);
        return ds;

    }

    public HbaseHelper getHbaseHelper(HbaseConfig hbaseConfig) throws Exception {
        if (hbaseConfig == null) {
            throw new Exception("Get connection from pool fail,the hbaseConfig is null.");
        }
        String dbId = hbaseConfig.getDbId();
        if (StringUtils.isBlank(dbId)) {
            throw new Exception("Get connection from pool fail,the dbId is null.");
        }
        HbaseHelper ds = this.dsRepository.get(dbId);
        if (ds == null) {
            createDbPool(dbId);
            ds = this.dsRepository.get(dbId);
        }
        if (ds == null) {
            throw new Exception("Get connection fail,conn't create db pool.dbId is [" + dbId + "].");
        }
        log.info("the getted HbaseHelper is :" + ds);
        return ds;

    }

    //创建数据库连接池
    // 加锁，防止重复创建
    public synchronized void createDbPool(String dbId) throws Exception {
        log.info("开始创建HBASE数据源连接池，dbId: " + dbId);
        if (dbId == null || dbId.isEmpty()) {
//			log.error("the dbId is null.");
            throw new Exception("the dbId is null.");
        }
        createPool(dbId);
        //更新数据库IS_POOL状态
        mapper.updateDbPoolState(dbId, DbPoolConstant.IS_USE);
        log.info("创建HBASE数据源连接池成功.");
        return;
    }

    private HbaseHelper createPool(String dbId) throws Exception {
        try {
            //判断连接池是否已经存在
            if (isExistPool(dbId)) {
                log.info("从缓存中获取到HBASE数据源，dbId: [" + dbId + "].");
                return dsRepository.get(dbId);
            }

            //从数据库查询连接参数
            DbEntity dbConfig = mapper.getDbConfigByDbId(dbId);
            if (dbConfig == null) {
                throw new Exception("查询HBASE数据源信息失败，dbId: [" + dbId + "].");
            }
            log.info("创建HBASE数据源连接池配置信息，dbConfig: " + dbConfig);
            return createPool(HbaseConfig.builder()
                    .dbId(dbId)
                    .host(dbConfig.getDbIp())
                    .port(String.valueOf(dbConfig.getDbPort()))
                    .namespace(dbConfig.getDbServiceName())
                    .poolSize(dbConfig.getMaxConnect())
                    .build());
        } catch (Exception e) {
            throw new Exception("创建HBASE数据源失败，dbId: [" + dbId + "].", e);
        }
    }

    public synchronized void createDbPool(HbaseConfig hbaseConfig) throws Exception {
        log.info("开始创建HBASE数据源连接池，hbaseConfig: " + hbaseConfig);
        if (hbaseConfig == null) {
            throw new Exception("Get connection from pool fail,the hbaseConfig is null.");
        }
        String dbId = hbaseConfig.getDbId();
        if (dbId == null || dbId.isEmpty()) {
//			log.error("the dbId is null.");
            throw new Exception("the dbId is null.");
        }
        createPool(hbaseConfig);
        //更新数据库IS_POOL状态
        mapper.updateDbPoolState(dbId, DbPoolConstant.IS_USE);
        log.info("创建HBASE数据源连接池成功.");
        return;
    }

    private HbaseHelper createPool(HbaseConfig hbaseConfig) throws Exception {
        try {
            if (hbaseConfig == null) {
                throw new Exception("Get connection from pool fail,the hbaseConfig is null.");
            }
            String dbId = hbaseConfig.getDbId();
            if (dbId == null || dbId.isEmpty()) {
                throw new Exception("the dbId is null.");
            }
            //判断连接池是否已经存在
            if (isExistPool(dbId)) {
                log.info("从缓存中获取到HBASE数据源，dbId: [" + dbId + "].");
                return dsRepository.get(dbId);
            }

            log.info("创建HBASE数据源连接池配置信息，hbaseConfig: " + hbaseConfig);

            HbaseHelper ds = new HbaseHelper(hbaseConfig);
            dsRepository.put(dbId, ds);
            return ds;
        } catch (Exception e) {
            throw new Exception("创建HBASE数据源失败，hbaseConfig: [" + hbaseConfig + "].", e);
        }
    }


    public synchronized void destroyDbPool(String dbId) throws Exception {
//		log.info("start destroy dbpool...dbId["+dbId+"].the history dbpool is:"+dsRepository.getAllKeys());
        if (dbId == null || dbId.isEmpty()) {
            log.error("the dbId is null.");
            return;
        }
        HbaseHelper ds = this.dsRepository.get(dbId);
        dsRepository.remove(dbId);
        destroyDbPool(ds);
        //更新数据库IS_POOL状态
        mapper.updateDbPoolState(dbId, DbPoolConstant.IS_NOT_USE);
//		log.info("destroy dbpool success.the dbpool is:"+dsRepository.getAllKeys());
    }

    public synchronized void removeDbPool(String dbId) throws Exception {
//		log.info("start destroy dbpool...dbId["+dbId+"].the history dbpool is:"+dsRepository.getAllKeys());
        if (dbId == null || dbId.isEmpty()) {
            log.error("the dbId is null.");
            return;
        }
        dsRepository.remove(dbId);
        //更新数据库IS_POOL状态
        mapper.updateDbPoolState(dbId, DbPoolConstant.IS_NOT_USE);
//		log.info("destroy dbpool success.the dbpool is:"+dsRepository.getAllKeys());
    }

    private void destroyDbPool(HbaseHelper ds) throws Exception {
        if (ds != null) {
            ds.close();
            log.trace("连接池被关闭");
        }
    }

    public int getDbPoolSize() {
        return (int) dsRepository.size();
    }

    public boolean isExistPool(String dbId) throws Exception {
        if (dbId == null || dbId.isEmpty()) {
            log.error("the dbId is null.");
            return false;
        }
        HbaseHelper ds = this.dsRepository.get(dbId);
        if (ds != null) {
            return true;
        }
        return false;
    }

    public void shutdownThreadPool() {
        log.info("Start to shutdown HbaseDbPool");

        if (dsRepository != null && dsRepository.size() > 0) {
            Set<String> allKeys = dsRepository.getAllKeys();
            for (String dbId : allKeys) {
                try {
                    destroyDbPool(dbId);
                } catch (Exception e) {
                    log.error("关闭连接池失败,dbId:[" + dbId + "]");
                    throw new RuntimeException("关闭连接池失败,dbId:[" + dbId + "].", e);
                }
            }
        }
    }
}