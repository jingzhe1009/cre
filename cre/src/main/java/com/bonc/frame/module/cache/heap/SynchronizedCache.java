package com.bonc.frame.module.cache.heap;

import com.bonc.frame.module.cache.ICache;

import java.util.Set;

/**
 * @author yedunyao
 * @date 2019/6/14 10:53
 */
public class SynchronizedCache<K, V> implements ICache<K, V> {
    private final ICache<K, V> underlying;

    public SynchronizedCache(ICache<K, V> underlying) {
        this.underlying = underlying;
    }

    @Override
    public synchronized V get(K key) {
        return underlying.get(key);
    }

    @Override
    public synchronized boolean containsKey(K key) {
        return underlying.containsKey(key);
    }

    @Override
    public synchronized void put(K key, V value) {
        underlying.put(key, value);
    }

    @Override
    public synchronized boolean remove(K key) {
        return underlying.remove(key);
    }

    @Override
    public synchronized long size() {
        return underlying.size();
    }

    @Override
    public synchronized Set<K> getAllKeys() {
        return underlying.getAllKeys();
    }
}