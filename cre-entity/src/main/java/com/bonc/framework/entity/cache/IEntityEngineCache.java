package com.bonc.framework.entity.cache;

/**
 * 规则缓存接口
 * @author qxl
 * @date 2018年5月9日 上午10:55:46
 * @version 1.0
 */
public interface IEntityEngineCache <K,V> {

	/**
	 * 是否包含key
	 * @param key
	 * @return
	 */
	boolean containsKey(K key);
	
	/**
	 * 根据key获取对应的缓存
	 * @param key
	 * @return
	 */
	V get(K key);
	
	/**
	 * 将值写入缓存
	 * @param key
	 * @param value
	 * @return
	 */
	V put(K key,V value);
	
	/**
	 * 根据key从缓存移除
	 * @param key
	 * @return
	 */
	V remove(K key);
	
	/**
	 * 清空缓存
	 */
	void clear();
	
	
}
