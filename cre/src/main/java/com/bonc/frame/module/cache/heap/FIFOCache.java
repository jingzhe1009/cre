package com.bonc.frame.module.cache.heap;

import com.bonc.frame.module.cache.BoundedCache;
import com.bonc.frame.module.cache.handler.DefaultEldestHandler;
import com.bonc.frame.module.cache.handler.EldestHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author yedunyao
 * @date 2019/6/14 12:05
 */
public class FIFOCache<K, V> implements BoundedCache<K, V> {

    private final LinkedHashMap<K, V> cache;

    public FIFOCache(final int maxSize) {
        this(maxSize, new DefaultEldestHandler());
    }


    public FIFOCache(final int maxSize, final EldestHandler eldestHandler) {
        cache = new LinkedHashMap<K, V>(16, .75f, false) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                eldestHandler.handler(eldest);
                return size() > maxSize;
            }
        };
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public boolean remove(K key) {
        return cache.remove(key) != null;
    }

    @Override
    public long size() {
        return cache.size();
    }

    @Override
    public Set<K> getAllKeys() {
        return cache.keySet();
    }

}
