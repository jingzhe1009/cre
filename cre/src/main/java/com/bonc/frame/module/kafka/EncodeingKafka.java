/*
package com.bonc.frame.module.kafka;

import com.bonc.frame.util.BeanUtils;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

*/
/**
 * @author yedunyao
 * @date 2019/6/12 17:22
 *//*

public class EncodeingKafka implements Serializer<Object> {

    @Override
    public void configure(Map configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, Object data) {
        try {
            return BeanUtils.bean2Byte(data);
        } catch (Exception e) {
            throw new SerializationException("序列化失败");
        }
    }

    */
/*
 * producer调用close()方法是调用
 *//*

    @Override
    public void close() {

    }

}
*/
