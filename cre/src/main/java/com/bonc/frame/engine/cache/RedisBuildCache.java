package com.bonc.frame.engine.cache;

import java.util.Set;

import com.bonc.framework.rule.cache.ICache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis实现规则缓存
 * @author qxl
 * @date 2018年5月9日 下午2:59:02
 * @version 1.0
 */
public class RedisBuildCache<K, V> implements ICache<K, V> {
	
	/**
	 * 最大的等待时间
	 * 当borrow一个jedis实例时，如果超过等待时间，则直接抛出JedisConnectionException；
	 */
	private final int MAX_WAIT_MILLIS = 10000;
	
	private JedisPool pool;
	
	/** 规则缓存前缀 */
	private final String KEY_PREFIX = "buildCachePrefix_";
	
	private String prefix;
	
	public RedisBuildCache(String ip, int port,String prefix){
		this(ip, port, null,prefix);
	}
	public RedisBuildCache(String ip, int port,String password,String prefix){
		this(null, ip, port, password,prefix);
	}
	public RedisBuildCache(JedisPoolConfig poolConfig,String ip, int port,String password,String prefix){
		if(poolConfig == null) {
			poolConfig = new JedisPoolConfig();
		}
		poolConfig.setMaxTotal(20);
		poolConfig.setTestOnBorrow(true);
		poolConfig.setTestOnReturn(true);
		
		if(ip == null ){
			throw new NullPointerException("The ip is null.");
		}
		if(port <= 0){
			throw new IllegalArgumentException("The port is illegal.["+port+"]");
		}
		this.prefix = prefix==null?"":prefix;
		if(password != null && !password.isEmpty()){
			pool = new JedisPool(poolConfig, ip, port,MAX_WAIT_MILLIS,password);
		} else {
			pool = new JedisPool(poolConfig, ip, port,MAX_WAIT_MILLIS);
		}
	}
	
	/**
	 * 从连接池获取Jedis连接对象
	 * @return
	 */
	private Jedis getJedis() {
		Jedis jedis = pool.getResource(); 
		return jedis;
	}
	
	/**
	 * 将使用完的jedis对象返还连接池
	 * @param jedis
	 */
	private void returnJedis(Jedis jedis) {
		if (jedis != null) {
	         jedis.close();
	    }
	}
	
	/*
	 * 从redis读取值
	 * 采用自定义序列化方法 protostuff
	 */
	private V getValue(K key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();//获取jedis连接对象
			V value = null;
			String newKey = KEY_PREFIX + prefix + key;
			//从redis读取值
			value = (V) jedis.get(newKey);
			return value;
		} finally {
			returnJedis(jedis);
		}
		
	}
	
	/*
	 * 将值存入redis
	 * 采用自定义序列化方法 protostuff
	 */
	private V setValue(K key,V value) {
		Jedis jedis = null;
		try {
			jedis = getJedis();//获取jedis连接对象
			V oldValue = null;
			String newKey = KEY_PREFIX + prefix + key;
			oldValue = (V) jedis.get(newKey);
			jedis.set(newKey, String.valueOf(value));
			return oldValue;
		} finally {
			returnJedis(jedis);
		}
	}
	
	
	@Override
	public boolean containsKey(K key) {
		Jedis jedis = null;
		String newKey = KEY_PREFIX + prefix + key;
		try {
			jedis = getJedis();//获取jedis连接对象
			String value = jedis.get(newKey);
			if(value != null){
				return true;
			}
		} finally {
			returnJedis(jedis);
		}
		return false;
	}

	@Override
	public V get(K key) {
		V v = this.getValue(key);
		return v;
	}

	@Override
	public V put(K key, V value) {
		V v = this.setValue(key, value);
		return v;
	}

	@Override
	public V remove(K key) {
		Jedis jedis = null;
		String newKey = KEY_PREFIX + prefix + key;
		try {
			jedis = getJedis();//获取jedis连接对象
//			jedis.setex(newKey, 1, "");
			jedis.del(newKey);
		} finally {
			returnJedis(jedis);
		}
		return null;
	}

	@Override
	public void clear() {
		Jedis jedis = null;
		try {
			jedis = getJedis();//获取jedis连接对象
			Set<String> keySet = jedis.keys(KEY_PREFIX + prefix + "*");
			for(String key : keySet) {
//				jedis.setex(key, 1, "");
				jedis.del(key);
			}
		} finally {
			returnJedis(jedis);
		}
		
	}

}
