package com.bonc.framework.rule.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.*;
import java.util.Set;

/**
 * Redis实现规则缓存
 *
 * @author qxl
 * @version 1.0
 * @date 2018年5月9日 下午2:59:02
 */
public class RedisRuleCache<K, V> implements ICache<K, V> {

    /**
     * 最大的等待时间 当borrow一个jedis实例时，如果超过等待时间，则直接抛出JedisConnectionException；
     */
    private final int MAX_WAIT_MILLIS = 10000;

    private JedisPool pool;

    /**
     * 规则缓存前缀
     */
    private final String KEY_PREFIX = "ruleCachePrefix_";

//	private RuntimeSchema<V> schema;

    public RedisRuleCache(String ip, int port, Class<V> clazz) {
        this(ip, port, null, clazz);
    }

    public RedisRuleCache(String ip, int port, String password, Class<V> clazz) {
        this(null, ip, port, password, clazz);
    }

    public RedisRuleCache(JedisPoolConfig poolConfig, String ip, int port, String password, Class<V> clazz) {
        if (poolConfig == null) {
            poolConfig = new JedisPoolConfig();
        }
        poolConfig.setMaxTotal(20);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);

        if (ip == null) {
            throw new NullPointerException("The ip is null.");
        }
        if (port <= 0) {
            throw new IllegalArgumentException("The port is illegal.[" + port + "]");
        }

        if (password != null && !password.isEmpty()) {
            pool = new JedisPool(poolConfig, ip, port, MAX_WAIT_MILLIS, password);
        } else {
            pool = new JedisPool(poolConfig, ip, port, MAX_WAIT_MILLIS);
        }
        // protostuff的schema
//		schema = RuntimeSchema.createFrom(clazz);
    }

    /**
     * 从连接池获取Jedis连接对象
     *
     * @return
     */
    private Jedis getJedis() {
        Jedis jedis = pool.getResource();
        return jedis;
    }

    /**
     * 将使用完的jedis对象返还连接池
     *
     * @param jedis
     */
    private void returnJedis(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    /*
     * 从redis读取值 采用自定义序列化方法 protostuff
     */
    private V getValue(K key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();// 获取jedis连接对象
            V value = null;
            String newKey = KEY_PREFIX + key;
            // 从redis读取值
            byte[] valueBytes = jedis.get(newKey.getBytes());
            if (valueBytes != null) {
                value = toObject(valueBytes);
            }
            return value;
        } finally {
            returnJedis(jedis);
        }

    }

    /*
     * 将值存入redis 采用自定义序列化方法 protostuff
     */
    private V setValue(K key, V value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();// 获取jedis连接对象
            V oldValue = null;
            String newKey = KEY_PREFIX + key;
            // 从redis读取旧的值
//			byte[] oldValueBytes = jedis.get(newKey.getBytes());
//			if (oldValueBytes != null) {
//				oldValue = toObject(oldValueBytes);
//			}
            // 存入redis
            byte[] newBytes = this.toByteArray(value);
            jedis.set(newKey.getBytes(), newBytes);
            return oldValue;
        } finally {
            returnJedis(jedis);
        }
    }

    @Override
    public boolean containsKey(K key) {
        Jedis jedis = null;
        String newKey = KEY_PREFIX + key;
        try {
            jedis = getJedis();// 获取jedis连接对象
            byte[] bytes = jedis.get(newKey.getBytes());
            if (bytes != null && bytes.length > 0) {
                return true;
            }
        } finally {
            returnJedis(jedis);
        }
        return false;
    }

    @Override
    public V get(K key) {
        long t1 = System.currentTimeMillis();
        V v = this.getValue(key);
        long t2 = System.currentTimeMillis();
        System.err.println("--GetCache from redis use time:" + (t2 - t1) + "ms.");
        return v;
    }

    @Override
    public V put(K key, V value) {
        long t1 = System.currentTimeMillis();
        V v = this.setValue(key, value);
        long t2 = System.currentTimeMillis();
        System.err.println("----Put cache to redis use time:" + (t2 - t1) + "ms.");
        return v;
    }

    @Override
    public V remove(K key) {
        Jedis jedis = null;
        String newKey = KEY_PREFIX + key;
        try {
            jedis = getJedis();// 获取jedis连接对象
//			jedis.setex(newKey.getBytes(), 1, "".getBytes());
            jedis.del(newKey.getBytes());
        } finally {
            returnJedis(jedis);
        }
        return null;
    }

    @Override
    public void clear() {
        Jedis jedis = null;
        try {
            jedis = getJedis();// 获取jedis连接对象
            Set<String> keySet = jedis.keys(KEY_PREFIX + "*");
            for (String key : keySet) {
//				jedis.setex(key.getBytes(), 1, "".getBytes());
                jedis.del(key.getBytes());
            }
        } finally {
            returnJedis(jedis);
        }

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
        if (bytes == null || bytes.length == 0) {
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
