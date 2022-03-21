package com.bonc.frame.module.cache;

import java.util.Set;

/**
 * @author yedunyao
 * @date 2019/6/14 10:47
 */
public interface ICache<K, V> {

    V get(K key);

    boolean containsKey(K key);

    void put(K key, V value);

    boolean remove(K key);

    long size();

    Set<K> getAllKeys();

}
