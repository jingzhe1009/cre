/*
package com.bonc.frame.module.kafka;

import com.bonc.frame.util.BeanUtils;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

*/
/**
 * @author yedunyao
 * @date 2019/6/12 17:23
 *//*

public class DecodeingKafka implements Deserializer<Object> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        try {
            return BeanUtils.byte2Obj(data);
        } catch (Exception e) {
            throw new SerializationException("反序列化失败");
        }
    }

    @Override
    public void close() {

    }
}
*/
