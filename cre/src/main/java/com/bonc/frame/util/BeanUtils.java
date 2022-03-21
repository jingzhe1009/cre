package com.bonc.frame.util;

import com.google.common.base.Preconditions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author yedunyao
 * @date 2019/6/12 17:20
 */
public class BeanUtils {

    private BeanUtils() {
    }

    /**
     * 对象序列化为byte数组
     *
     * @param obj
     * @return
     */
    public static byte[] bean2Byte(Object obj) throws Exception {
        Preconditions.checkNotNull(obj);
        byte[] bb = null;
        try (ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
             ObjectOutputStream outputStream = new ObjectOutputStream(byteArray)) {
            outputStream.writeObject(obj);
            outputStream.flush();
            bb = byteArray.toByteArray();
            return bb;
        }
    }

    /**
     * 字节数组转为Object对象
     *
     * @param bytes
     * @return
     */
    public static Object byte2Obj(byte[] bytes) throws Exception {
        Preconditions.checkNotNull(bytes);
        Object readObject = null;
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes);
             ObjectInputStream inputStream = new ObjectInputStream(in)) {
            readObject = inputStream.readObject();
            return readObject;
        }
    }

}
