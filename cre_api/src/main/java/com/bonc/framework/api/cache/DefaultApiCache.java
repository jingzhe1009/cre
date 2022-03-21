package com.bonc.framework.api.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultApiCache<K,V> implements IApiCache<K, V>{

	private Map<K,V> apiCache = new ConcurrentHashMap<K,V>();

	@Override
	public boolean containsKey(K key) {
		return apiCache.containsKey(key);
	}

	@Override
	public V get(K key) {
		return apiCache.get(key);
	}

	@Override
	public V put(K key, V value) {
		return apiCache.put(key, value);
	}

	@Override
	public V remove(K key) {
		return apiCache.remove(key);
	}

	@Override
	public void clear() {
		apiCache.clear();
	}

}
