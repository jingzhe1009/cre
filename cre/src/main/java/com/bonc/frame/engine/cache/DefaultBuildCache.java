package com.bonc.frame.engine.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bonc.framework.rule.cache.ICache;

/**
 * build缓存接口默认实现
 * @author qxl
 * @date 2018年5月9日 上午10:59:54
 * @version 1.0
 */
public class DefaultBuildCache<K, V> implements ICache<K, V> {
	
	private Map<K,V> cache = new ConcurrentHashMap<K,V>();

	@Override
	public boolean containsKey(K key) {
		return cache.containsKey(key);
	}

	@Override
	public V get(K key) {
		return cache.get(key);
	}

	@Override
	public V put(K key, V value) {
		return cache.put(key, value);
	}

	@Override
	public V remove(K key) {
		return cache.remove(key);
	}

	@Override
	public void clear() {
		cache.clear();
	}

}
