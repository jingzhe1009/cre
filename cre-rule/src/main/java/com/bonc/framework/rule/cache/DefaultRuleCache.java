package com.bonc.framework.rule.cache;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 规则缓存接口默认实现
 *
 * @author qxl
 * @version 1.0
 * @date 2018年5月9日 上午10:59:54
 */
public class DefaultRuleCache<K, V> implements ICache<K, V> {

    private Map<K, byte[]> ruleCache = new ConcurrentHashMap<>();

    @Override
    public boolean containsKey(K key) {
        return ruleCache.containsKey(key);
    }

    @Override
    public V get(K key) {
        byte[] bytes = ruleCache.get(key);
        return toObject(bytes);
    }

    @Override
    public V put(K key, V value) {
        byte[] bytes = toByteArray(value);
        ruleCache.put(key, bytes);
        return null;
    }

    @Override
    public V remove(K key) {
        ruleCache.remove(key);
        return null;
    }

    @Override
    public void clear() {
        ruleCache.clear();
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
