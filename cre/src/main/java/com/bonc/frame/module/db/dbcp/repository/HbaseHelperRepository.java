package com.bonc.frame.module.db.dbcp.repository;


import com.bonc.frame.module.cache.ICache;
import com.bonc.frame.module.cache.handler.EldestHandler;
import com.bonc.frame.module.cache.heap.LRUCache;
import com.bonc.frame.module.cache.heap.SynchronizedCache;
import com.bonc.frame.module.db.operator.hbase.HbaseHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Set;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/7/7 9:54
 */
public class HbaseHelperRepository implements Repository<String, HbaseHelper> {
    private Log log = LogFactory.getLog(getClass());
    private final ICache<String, HbaseHelper> dbCache;

    public HbaseHelperRepository(EldestHandler eldestHandler) {
        final int datasourceCacheMaxSize = 20;
        log.info("数据源缓存最大个数：" + datasourceCacheMaxSize);
        dbCache = new SynchronizedCache(
                new LRUCache(datasourceCacheMaxSize, eldestHandler)
        );
    }

    @Override
    public void put(String key, HbaseHelper value) {
        dbCache.put(key, value);
    }

    @Override
    public HbaseHelper get(String key) {
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

    @Override
    public Set<String> getAllKeys() {
        return dbCache.getAllKeys();
    }
}
