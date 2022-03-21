package com.bonc.frame.module.cache.heap;

import com.bonc.frame.module.cache.UnBoundedCache;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yedunyao
 * @date 2019/6/14 15:40
 */
public class ConcurentMapCache<K, V> implements UnBoundedCache<K, V> {

    private final ConcurrentHashMap<K, V> cache;

    public ConcurentMapCache() {
        this.cache = new ConcurrentHashMap<>(32);
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
