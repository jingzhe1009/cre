package com.bonc.frame.engine.cache;

import com.bonc.frame.util.SpringUtils;
import com.bonc.framework.rule.cache.ICache;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.*;

public class RedisCache<K,V> implements ICache<K, V>{

	private RedisTemplate<Object, Object> redisTemplate;
	
	private final String KEY_PREFIX = "buildCachePrefix_";
	
	private String prefix = "";
	
	public RedisCache() {
		this("");
	}
	@SuppressWarnings("unchecked")
	public RedisCache(String prefix){
		if(redisTemplate == null){
			redisTemplate=(RedisTemplate<Object, Object>) SpringUtils.getBean("redisTemplate");
		}
		this.prefix = prefix;
	}
	@Override
	public boolean containsKey(K key) {
		
		String newKey = KEY_PREFIX+prefix+key;
		
		Object obj = redisTemplate.opsForValue().get(newKey.getBytes());
		if (obj != null) {
			return true;
		}
		
		return false;
	}

	@Override
	public V get(K key) {
		
		String newKey = KEY_PREFIX+prefix+key;
		byte[] valueBytes = (byte[]) redisTemplate.opsForValue().get(newKey.getBytes());
		return toObject(valueBytes);
	}

	@Override
	public V put(K key, V value) {
		
		long t1 = System.currentTimeMillis();
		String newKey = KEY_PREFIX+prefix+key;
		byte[] newBytes = this.toByteArray(value);
		redisTemplate.opsForValue().set(newKey.getBytes(), newBytes);
		long t2 = System.currentTimeMillis();
		System.err.println("----Put cache to redis use time:" + (t2 - t1) + "ms.");
		
		return null;
	}

	@Override
	public V remove(K key) {
		String newKey = KEY_PREFIX+prefix+key;
//		redisTemplate.opsForValue().set(newKey.getBytes(), "1".getBytes(),1,TimeUnit.MILLISECONDS);
        redisTemplate.delete(newKey.getBytes());
		return null;
	}

	@Override
	public void clear() {
		// FIXME: 使用lua脚本删除 KEY_PREFIX 开头的key
	}

	/**
	 * 对象转数组
	 * 
	 * @param obj
	 * @return
	 */
	public byte[] toByteArray(V obj) {
		byte[] bytes = null;
		// byte[] newBytes = ProtostuffIOUtil.toByteArray(value, schema,
		// LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
		// java序列化方法
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			bytes = bos.toByteArray();

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bytes;
	}
	
	/**
	 * 数组转对象
	 * 
	 * @param bytes
	 * @return
	 */
	public V toObject(byte[] bytes) {
		if(bytes == null || bytes.length==0){
			return null;
		}
		V obj = null;
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		try {
			// Protostuff反序列化
			// obj = schema.newMessage();//空对象
			// ProtostuffIOUtil.mergeFrom(bytes, obj, schema);//反序列化

			// java反序列化方法
			bis = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bis);
			obj = (V) ois.readObject();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
				if (bis != null) {
					bis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}
	
}
