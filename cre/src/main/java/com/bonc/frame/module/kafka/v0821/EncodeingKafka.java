package com.bonc.frame.module.kafka.v0821;

import com.bonc.frame.util.BeanUtils;
import kafka.utils.VerifiableProperties;
import org.apache.kafka.common.errors.SerializationException;

/**
 * @author yedunyao
 * @date 2019/6/12 17:22
 */
public class EncodeingKafka<T> implements kafka.serializer.Encoder<T> {

    public EncodeingKafka(VerifiableProperties props) {

    }

    @Override
    public byte[] toBytes(T t) {
        try {
            return BeanUtils.bean2Byte(t);
        } catch (Exception e) {
            throw new SerializationException("序列化失败");
        }
    }

}
