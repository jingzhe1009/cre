package com.bonc.frame.util;

import java.lang.reflect.Field;
import java.util.*;

/**
 * <br>Created by liyanjun@bonc.com.cn on 2017/5/17.<br><br>
 *     map 工具类
 */
public class MapUtil {

    public static Builder newMap() {
        return new Builder();
    }

    public static class Builder {

        private Map<String, Object> map = new HashMap<>();

        public Builder put(String key, Object value) {
            map.put(key, value);
            return this;
        }

        public Map<String, Object> build() {
            return map;
        }
    }

    public static List<Map<String, Object>> convertListToMap(Object obj) {
        return convertListToMap(obj, true);
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> convertListToMap(Object obj, boolean ignoreNull) {
        if (obj == null) {
            return Collections.emptyList();
        }

        List<Object> objList = null;
        if (obj instanceof List) {
            objList = (List<Object>) obj;
        }

        if (objList == null) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> result = new LinkedList<>();
        for (Object object : objList) {
            result.add(convertObjectToMap(object, ignoreNull));
        }
        return result;
    }

    /**
     * 将对象转为 map , 忽略 null 值对应的属性名
     * @param obj 需要转为 map 的对象
     * @return map
     */
    public static Map<String, Object> convertObjectToMap(Object obj) {
        return convertObjectToMap(obj, true);
    }

    /**
     * 将对象转为 map
     * @param obj 需要转为 map 的对象
     * @param ignoreNull 是否忽略 null 值, true 忽略, 即返回值中没有值为 null 的属性名对应的 key
     * @return map
     */
    public static Map<String, Object> convertObjectToMap(Object obj, boolean ignoreNull) {
        if (obj == null) {
            return Collections.emptyMap();
        }

        Class<?> objClass = obj.getClass();
        Field[] declaredFields = objClass.getDeclaredFields();
        Builder builder = MapUtil.newMap();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            Object value = null;
            try {
                value = declaredField.get(obj);
            } catch (IllegalAccessException e) {

            }
            if (value == null && ignoreNull) {
                continue;
            }
            builder.put(declaredField.getName(), value);
        }
        return builder.build();
    }

    public static Map<String, Object> convertObjectToLinkedMap(Object obj, boolean ignoreNull) {
        if (obj == null) {
            return Collections.emptyMap();
        }

        Class<?> objClass = obj.getClass();
        Field[] declaredFields = objClass.getDeclaredFields();
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>(declaredFields.length);
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            Object value = null;
            try {
                value = declaredField.get(obj);
            } catch (IllegalAccessException e) {

            }
            if (value == null && ignoreNull) {
                continue;
            }
            linkedHashMap.put(declaredField.getName(), value);
        }
        return linkedHashMap;
    }

}
