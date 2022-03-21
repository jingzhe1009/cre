package com.bonc.frame.module.db.dbcp.repository;

import com.bonc.frame.config.Config;
import com.bonc.frame.module.cache.ICache;
import com.bonc.frame.module.cache.handler.EldestHandler;
import com.bonc.frame.module.cache.heap.LRUCache;
import com.bonc.frame.module.cache.heap.SynchronizedCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.util.Set;


/** 
 * 数据库连接池存放仓库实现类
 * 	key为brm_db_connection表中DB_ID
 * 	value为对应的数据库连接池对象
 * 
 * @author qxl
 * @date 2017年3月27日 下午7:50:16 
 * @version 1.0.0
 */
public class DataSourceRepository implements Repository<String, DataSource> {

    private Log log = LogFactory.getLog(getClass());
    //	private Map<String,DataSource> resourceMap = new ConcurrentHashMap<String,DataSource>();
    private final ICache<String, DataSource> dbCache;

    /*private static final Repository<String, DataSource> instance = new DataSourceRepository();

	public static  Repository<String,DataSource> getInstance(){
        return instance;
    }*/

    public DataSourceRepository(EldestHandler eldestHandler) {
        final int datasourceCacheMaxSize = Config.DATASOURCE_CACHE_MAX_SIZE;
        log.info("数据源缓存最大个数：" + datasourceCacheMaxSize);
        dbCache = new SynchronizedCache(
                new LRUCache(datasourceCacheMaxSize, eldestHandler)
        );
	}

	@Override
	public void put(String key, DataSource value) {
        dbCache.put(key, value);
	}

	@Override
	public DataSource get(String key) {
        return dbCache.get(key);
	}

	@Override
	public void remove(String key) {
        dbCache.remove(key);
	}

	@Override
    public boolean containsKey(String key) {
        return dbCache.containsKey(key);
	}
	@Override
    public long size() {
        return dbCache.size();
	}

	/**
	 * 获取所有的key
	 * @return
	 */
	public Set<String> getAllKeys(){
        return dbCache.getAllKeys();
	}
}
