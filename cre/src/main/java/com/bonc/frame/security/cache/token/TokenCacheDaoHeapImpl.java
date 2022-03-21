package com.bonc.frame.security.cache.token;

import com.bonc.frame.config.Config;
import com.bonc.frame.module.cache.ICache;
import com.bonc.frame.module.cache.heap.LRUCache;
import com.bonc.frame.module.cache.heap.SynchronizedCache;

/**
 * @author yedunyao
 * @since 2020/10/9 15:38
 */
public class TokenCacheDaoHeapImpl implements TokenCacheDao {

    private ICache<String, String> cache;

    public TokenCacheDaoHeapImpl() {
        this.cache = new SynchronizedCache(new LRUCache(Config.CRE_TOKEN_CACHE_MAXSIZE));
    }

    public String get(String token) {
        return cache.get(token);
    }

    public void set(String token, String value) {
        cache.put(token, value);
    }

    public void del(String token) {
        cache.remove(token);
    }

}
