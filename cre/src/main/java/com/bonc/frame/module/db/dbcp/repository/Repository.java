package com.bonc.frame.module.db.dbcp.repository;

import java.util.Set;

/** 
 * @author 作者: jxw 
 * @date 创建时间: 2016年11月8日 下午6:46:13 
 * @version 版本: 1.0 
*/
public interface Repository<K,V>{

	public void put(K key, V value);
	
	public V get(K key);
	
	public void remove(K key);

    public boolean containsKey(K key);

    public long size();
	
	public Set<K> getAllKeys();
	
}

