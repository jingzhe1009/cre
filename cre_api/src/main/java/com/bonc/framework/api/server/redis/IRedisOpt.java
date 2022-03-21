package com.bonc.framework.api.server.redis;

import redis.clients.jedis.Jedis;

/** 
 * redis操作接口
 * @author  qxl
 * @date    2017年7月17日 上午10:10:09 
 * @version 1.0
 */
public interface IRedisOpt {
	
	/**
	 * 返回资源
	 * @param jedis
	 */
	public void returnResource(Jedis jedis);
	
	/**
	 * 获取jedis 实例
	 * @return
	 * @throws Exception
	 */
	public Jedis getJedis() throws Exception;
	
	/**
	 * 根据key，从redis中取对应的值
	 * @param key
	 * @return
	 */
	public String get(String key);

	
	/**
	 * 设置键值
	 * @param key
	 * @param value
	 * @return
	 */
	public String set(String key,String value);
	
	/**
	 * 设置键值 并同时设置有效期
	 * @param key
	 * @param seconds
	 * @param value
	 * @return
	 */
	public String setex(String key,int seconds,String value);
	
	/**
	 * 设置键值 并同时设置有效期 ms
	 * @param key
	 * @param milliseconds
	 * @param value
	 * @return
	 */
	public Long setexms(String key,long milliseconds,String value);
	
	/**
	 * 判断key是否存在
	 * @param key
	 * @return
	 */
	public Boolean exists(String key);
	
	/**
	 * 判断key是否存在，若不存在则存到redis
	 * @param key
	 * @param milliseconds
	 * @param value
	 * @return
	 */
	public Boolean exists(String key,long milliseconds,String value);
	
}
