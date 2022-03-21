package com.bonc.framework.api.server.redis;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisOpt implements IRedisOpt {

	private  Log log = LogFactory.getLog(getClass());
	
	private  JedisPool pool = null;
	
	private  String ip = null;
	private  int port = 0;
	private String auth = null;
	
	public RedisOpt(String ip,int port){
		this.ip = ip;
		this.port = port;
	}
	
	public RedisOpt(String ip,int port,String auth){
		this.ip = ip;
		this.port = port;
		this.auth = auth;
	}
	
	/*** <p>Description: 获取jedispool 连接池 </p>
	* @author wenquan
	* @date  2017年1月5日
	* @param
	 * @throws Exception 
	*/
	
	private JedisPool getPool() throws Exception{
		if(pool == null){
			if(ip == null||port ==0){
				throw new Exception("redis pool init failed;ip or port is null:ip"+ip+";port:"+port);
			}
			JedisPoolConfig config = new JedisPoolConfig();
			Properties prop = new Properties();
			 //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
	        //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
	        config.setMaxTotal(500);
	        //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
	        config.setMaxIdle(500);
	        //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
	        config.setMaxWaitMillis(10000);
	        //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
	        config.setTestOnBorrow(true);

	        if(auth == null || auth.isEmpty()){
	        	pool = new JedisPool(config, ip, port,100000);
	        }else{
	        	pool = new JedisPool(config, ip, port,100000,auth);
	        }
	        
		}
		return pool;
	}
	
	/*** <p>Description: 返回资源 </p>
	* @author wenquan
	* @date  2017年1月5日
	* @param
	*/
	public  void returnResource(Jedis jedis) {
	    if (jedis != null) {
	         jedis.close();
	    }
	}
	
	/*** <p>Description: 获取jedis 实例</p>
	* @author wenquan
	* @date  2017年1月5日
	* @param
	*/
	public  Jedis getJedis() throws Exception {
	    Jedis jedis = null;
	    pool = getPool();
	    jedis = pool.getResource();
	    return jedis;
	}
	
	/*** <p>Description: 得到值</p>
	* @author wenquan
	* @date  2017年1月5日
	* @param key
	*/
	public  String get(String key){
	    String value = null;
	    Jedis jedis = null;
	    try {
	        jedis= getJedis();
	        value = jedis.get(key);
	    } catch(Exception e){
	        log.error("jedis get Exception:"+jedis+";"+e);
	    } finally {
	        returnResource(jedis);
	    }
	    return value;
	}
	

	/*** <p>Description: 设置键值</p>
	* @author wenquan
	* @date  2017年1月5日
	* @param key value
	*/
	public  String set(String key,String value){
	     Jedis jedis = null;
	     String ans = null;
	     try {
	         jedis = getJedis();
	         ans = jedis.set(key,value);
	     } catch (Exception e) {
	         log.error("jedis set Exception:"+jedis+";"+e);
	     } finally {
	         //返还到连接池
	    	 returnResource(jedis);
	     }
	     return ans;
	}
	
	/*** <p>Description: 设置键值 并同时设置有效期</p>
	* @author wenquan
	* @date  2017年1月5日
	* @param key seconds秒数 value
	*/
	public  String setex(String key,int seconds,String value){
	     Jedis jedis = null;
	     String ans = null;
	     try {

	         String.valueOf(100);
	         jedis = getJedis();
	         ans = jedis.setex(key,seconds,value);
	     } catch (Exception e) {
	         log.error("jedis setex Exception:"+jedis+";"+e);
	     } finally {
	         //返还到连接池
	    	 returnResource(jedis);
	     }
	     return ans;
	}
	
	/*** <p>Description: 设置键值 并同时设置有效期 ms</p>
	* @author wenquan
	* @date  2017年1月5日
	* @param key seconds秒数 value
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
	         log.error("jedis setexms Exception:"+jedis+";"+e);
	     } finally {
	         //返还到连接池
	    	 returnResource(jedis);
	     }
	     return ans;
	}
	
	/*** <p>Description: 判断key是否存在</p>
	* @author wenquan
	* @date  2017年1月6日
	* @param
	*/
	public  Boolean exists(String key){
	     Jedis jedis = null;
	     try {
	         jedis = getJedis();
	         return jedis.exists(key);
	     } catch (Exception e) {
	         log.error("jedis exists Exception:"+jedis+";"+e);
	         return false;
	     } finally {
	        //返还到连接池
	    	 returnResource(jedis);
	     }
	}
	
	/*** <p>Description: 判断key是否存在，若不存在则存</p>
	* @author wenquan
	* @date  2017年1月6日
	* @param
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
	         log.error("jedis exists Exception:"+jedis+";"+e);
	         return false;
	     } finally {
	        //返还到连接池
	    	 returnResource(jedis);
	     }
	}

	public  void main(String[] args) {
         ip = "133.128.42.22";
         port=6380;
         while(true){
        	 set("testkey1", "1");
             System.out.println(get("testkey1"));
         }
        
    }
	
	public  String getIp() {
		return ip;
	}

	public  void setIp(String ip) {
		this.ip = ip;
	}

	public  int getPort() {
		return port;
	}

	public  void setPort(int port) {
		this.port = port;
	}
	
}
