package com.bonc.framework.entity.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 规则缓存接口默认实现
 * @author qxl
 * @date 2018年5月9日 上午10:59:54
 * @version 1.0
 */
public class DefaultEntityEngineCache<K, V> implements IEntityEngineCache<K, V> {
	
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
