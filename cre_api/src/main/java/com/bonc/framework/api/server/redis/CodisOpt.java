package com.bonc.framework.api.server.redis;

import io.codis.jodis.JedisResourcePool;
import io.codis.jodis.RoundRobinJedisPool;
import redis.clients.jedis.Jedis;

/** 
 * Codis方式连接redis集群工具类
 * @author  qxl
 * @date    2017年7月4日 上午9:11:47 
 * @version 1.0
 */
public class CodisOpt implements IRedisOpt {

	private JedisResourcePool pool = null;
	
	private  String zkAddr = null;
	private  String zkProxyDir = null;
	
	public CodisOpt(String zkAddr,String zkProxyDir){
		this.zkAddr = zkAddr;
		this.zkProxyDir = zkProxyDir;
	}
	
	
	/**
	 * 获取jedis资源池
	 * @return
	 * @throws Exception 
	 */
	private  JedisResourcePool getPool() throws Exception{
		if(pool == null){
			if(zkAddr == null||zkProxyDir ==null){
				throw new Exception("redis pool init failed;zkAddr or zkProxyDir is null:zkAddr:"+zkAddr+";zkProxyDir:"+zkProxyDir);
			}
			pool = RoundRobinJedisPool
					.create()
					.curatorClient(zkAddr,30000)
					.connectionTimeoutMs(500000)
					.timeoutMs(500000)
					.zkProxyDir(zkProxyDir).build();
		}
		return pool;
	}
	
	/**
	 * 返回资源
	 * @param jedis
	 */
	public  void returnResource(Jedis jedis) {
	    if (jedis != null) {
	         jedis.close();
	    }
	}
	
	/**
	 * 获取jedis 实例
	 * @return
	 * @throws Exception
	 */
	public  Jedis getJedis() throws Exception {
	    Jedis jedis = null;
	    pool = getPool();
	    jedis = pool.getResource();
	    return jedis;
	}
	
	/**
	 * 根据key，从redis中取对应的值
	 * @param key
	 * @return
	 */
	public  String get(String key){
	    String value = null;
	    Jedis jedis = null;
	    try {
	        jedis= getJedis();
	        value = jedis.get(key);
	    } catch(Exception e){
	        e.printStackTrace();
	    } finally {
	        returnResource(jedis);
	    }
	    return value;
	}
	

	/**
	 * 设置键值
	 * @param key
	 * @param value
	 * @return
	 */
	public  String set(String key,String value){
	     Jedis jedis = null;
	     String ans = null;
	     try {
	         jedis = getJedis();
	         ans = jedis.set(key,value);
	     } catch (Exception e) {
	     } finally {
	         //返还到连接池
	    	 returnResource(jedis);
	     }
	     return ans;
	}
	
	/**
	 * 设置键值 并同时设置有效期
	 * @param key
	 * @param seconds
	 * @param value
	 * @return
	 */
	public  String setex(String key,int seconds,String value){
	     Jedis jedis = null;
	     String ans = null;
	     try {

	         String.valueOf(100);
	         jedis = getJedis();
	         ans = jedis.setex(key,seconds,value);
	     } catch (Exception e) {
	     } finally {
	         //返还到连接池
	    	 returnResource(jedis);
	     }
	     return ans;
	}
	
	/**
	 * 设置键值 并同时设置有效期 ms
	 * @param key
	 * @param milliseconds
	 * @param value
	 * @return
	 */
	public  Long setexms(String key,long milliseconds,String value){
	     Jedis jedis = null;
	     Long ans = null;
	     try {

	         String.valueOf(100);
	         jedis = getJedis();
	         jedis.set(key, value);
	         ans = jedis.pexpireAt(key, milliseconds);
	     } catch (Exception e) {
	     } finally {
	         //返还到连接池
	    	 returnResource(jedis);
	     }
	     return ans;
	}
	
	/**
	 * 判断key是否存在
	 * @param key
	 * @return
	 */
	public  Boolean exists(String key){
	     Jedis jedis = null;
	     try {
	         jedis = getJedis();
	         return jedis.exists(key);
	     } catch (Exception e) {
	         return false;
	     } finally {
	        //返还到连接池
	    	 returnResource(jedis);
	     }
	}
	
	/**
	 * 判断key是否存在，若不存在则存
	 * @param key
	 * @param milliseconds
	 * @param value
	 * @return
	 */
	public  Boolean exists(String key,long milliseconds,String value){
	     Jedis jedis = null;
	     try {
	         jedis = getJedis();
	         if(jedis.exists(key)){
	        	 return true;
	         }else{
	        	 jedis.set(key, value);
		         jedis.pexpireAt(key, milliseconds);
		         return false;
	         }
	     } catch (Exception e) {
	         return false;
	     } finally {
	        //返还到连接池
	    	 returnResource(jedis);
	     }
	}


	public  String getZkAddr() {
		return zkAddr;
	}

	public  void setZkAddr(String zkAddr) {
		this.zkAddr = zkAddr;
	}

	public  String getZkProxyDir() {
		return zkProxyDir;
	}

	public  void setZkProxyDir(String zkProxyDir) {
		this.zkProxyDir = zkProxyDir;
	}
	
	public  void main(String[] args) {
		zkAddr = "132.194.41.102:2181,132.194.41.103:2181,132.194.41.104:2181";
		zkProxyDir = "/zk/codis/db_codis/proxy";
		System.out.println(get("qxlKey"));
    }
}