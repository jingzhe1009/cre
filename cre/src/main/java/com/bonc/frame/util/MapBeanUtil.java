package com.bonc.frame.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author qxl
 * @version 1.0
 * @date 2018年4月2日 下午2:05:21
 */
public class MapBeanUtil {

    /**
     * 将一个 JavaBean 对象转化为一个  Map
     *
     * @param bean 要转化的JavaBean 对象
     * @return 转化出来的  Map 对象
     * @throws IntrospectionException    如果分析类属性失败
     * @throws IllegalAccessException    如果实例化 JavaBean 失败
     * @throws InvocationTargetException 如果调用属性的 setter 方法失败
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map convertBean2Map(Object bean)
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        Class type = bean.getClass();
        Map returnMap = new HashMap();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {
                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke(bean, new Object[0]);
                if (result != null) {
                    returnMap.put(propertyName, result);
                } else {
                    returnMap.put(propertyName, "");
                }
            }
        }
        return returnMap;
    }


    /**
     * 将一个 Map 对象转化为一个 JavaBean
     *
     * @param type 要转化的类型
     * @param map  包含属性值的 map
     * @return 转化出来的 JavaBean 对象
     * @throws IntrospectionException    如果分析类属性失败
     * @throws IllegalAccessException    如果实例化 JavaBean 失败
     * @throws InstantiationException    如果实例化 JavaBean 失败
     * @throws InvocationTargetException 如果调用属性的 setter 方法失败
     */
    @SuppressWarnings("rawtypes")
    public static <T> T convertMap2Bean(Class<T> type, Map map) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
            T obj = type.newInstance(); // 创建 JavaBean 对象

            // 给 JavaBean 对象的属性赋值
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();

                if (map.containsKey(propertyName)) {
                    // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                    Object value = map.get(propertyName);

                    Object[] args = new Object[1];
                    args[0] = value;

                    descriptor.getWriteMethod().invoke(obj, args);
                }
            }
            return obj;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将一个 Map 对象转化为一个 JavaBean
     *
     * @param type 要转化的类型
     * @param map  包含属性值的 map
     * @return 转化出来的 JavaBean 对象
     * @throws IntrospectionException    如果分析类属性失败
     * @throws IllegalAccessException    如果实例化 JavaBean 失败
     * @throws InstantiationException    如果实例化 JavaBean 失败
     * @throws InvocationTargetException 如果调用属性的 setter 方法失败
     */
    @SuppressWarnings("rawtypes")
    public static <T> T convertMap2Object(Class<T> type, Map map) {
        try {
//            BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
            T obj = type.newInstance(); // 创建 JavaBean 对象
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                Class<?> propertyType = field.getType();
                String propertyName = field.getName();
                if (map.containsKey(propertyName)) {
                    field.setAccessible(true);
                    // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                    try {
                        Object value = map.get(propertyName);
                        Object args = convertObjectClassType(value, propertyType, field);
                        field.set(obj, args);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T convertObjectClassType(Object o, Class<T> c, Field field) {
        if (o == null) {
            return null;
        }
        // TODO : List 和 Map的类型转换 : 问题:  这里有可能会有问题
        if (c.isAssignableFrom(o.getClass())) {
            return (T) o;
        }
        if (c == String.class) {
            o = o.toString();
            return (T) o;
        }

        if (c == Byte.class || c == Short.class || c == Integer.class || c == Long.class || c == Float.class || c == Double.class) {
            if (StringUtils.isBlank(o.toString())) {
                o = null;
                return (T) o;
            }
            if (!StringUtil.stringIsNumber(o.toString())) {
                throw new ClassCastException("对象转对象数字类型失败:[" + o.toString() + "],不是对象数字类型数字");
            }
            if (c == Integer.class) {
                o = Integer.parseInt(o.toString());
            } else if (c == Long.class) {
                o = Long.parseLong(o.toString());
            } else if (c == Double.class) {
                o = Double.parseDouble(o.toString());

            } else if (c == Float.class) {
                o = Float.parseFloat(o.toString());

            } else if (c == Byte.class) {
                o = Byte.parseByte(o.toString());

            } else {
                o = Short.parseShort(o.toString());
            }
        } else if (c == byte.class || c == short.class || c == int.class || c == long.class || c == float.class || c == double.class) {
            if (StringUtils.isBlank(o.toString()) || !StringUtil.stringIsNumber(o.toString())) {
                throw new ClassCastException("对象转基本数字类型失败:[" + o.toString() + "],不是基本数字类型");
            }
            if (c == int.class) {
                o = Integer.parseInt(o.toString());
            } else if (c == long.class) {
                o = Long.parseLong(o.toString());
            } else if (c == double.class) {
                o = Double.parseDouble(o.toString());

            } else if (c == float.class) {
                o = Float.parseFloat(o.toString());

            } else if (c == byte.class) {
                o = Byte.parseByte(o.toString());

            } else {
                o = Short.parseShort(o.toString());
            }
        } else if (c == Character.class || c == char.class) {
            char[] chars = o.toString().toCharArray();
            if (chars.length == 0) {
                if (c == Character.class) {
                    o = null;
                } else {
                    throw new ClassCastException("对象转char类型失败:[" + o.toString() + "],不是char类型");
                }
            } else if (chars.length > 1) {
                throw new ClassCastException("对象转Char类型失败:[" + o.toString() + "],不是Char类型");
            } else {
                o = chars[0];
            }
        } else if (Date.class == c || java.sql.Date.class == c) {
            try {
                String dateString = DateFormatUtil.format(o.toString());
                Date date = null;
                if (StringUtils.isNotBlank(dateString)) {
                    date = DateFormatUtil.parse(dateString);
                } else {
//                    String dateString = "Sat Sep 05 14:26:36 CST  2020";
                    try {
                        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
                        date = format.parse(o.toString());
                    } catch (Exception e) {
                        date = null;
                    }
                }
                if (date == null) {
                    throw new ClassCastException("对象转日期类型失败:[" + o.toString() + "],不是日期类型");
                }
                if (java.sql.Date.class == c) {
                    o = new java.sql.Date(date.getTime());
                } else {
                    o = date;
                }
            } catch (ParseException e) {
                throw new ClassCastException("对象转日期类型失败:[" + o.toString() + "],不是日期类型");
            }
        } else if (List.class.isAssignableFrom(c)) {
            // TODO : List 和 Map的类型转换 : 问题: 怎么获取List或者Map内部的数据类型?
            JSONArray listValues = null;
            try {
                listValues = JSONArray.parseArray(o.toString());
            } catch (Exception e) {
                throw new ClassCastException("对象转数组类型失败:[" + o.toString() + "],不是数组类型");
            }
            if (listValues != null && !listValues.isEmpty()) {
                Class listclass = null;
                try {
                    if (field != null) {
                        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                        listclass = (Class) genericType.getActualTypeArguments()[0];
                        System.out.println(genericType);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ClassCastException("获取数组类型中的类型失败:field:[" + field + ",o:[" + o.toString() + "]");
                }
                if (listclass == null) {
                    listclass = Object.class;
                }
                List<Object> result = new ArrayList<>();
                for (Object listValue : listValues) {
                    Object o1 = convertObjectClassType(listValue, listclass, null);
                    result.add(o1);
                }
                o = result;
                return (T) o;
            } else {
                o = null;
            }
        } else if (Map.class.isAssignableFrom(c)) {

        } else {
            // 不是上面的数据类型, 就为对象类型
            if (o instanceof Map) {
                convertMap2Object(c, JSONObject.parseObject(JSON.toJSONString(o)));

            } else {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(o.toString());
                    o = convertMap2Object(c, jsonObject);
                } catch (Exception e) {
                    throw new ClassCastException("对象转" + c.getName() + "类型失败:[" + o.toString() + "],不是" + c.getName() + "类型");
                }
            }
        }
        return (T) o;
    }

    @SuppressWarnings("rawtypes")
    public static <T, K, V> List<T> convertMap2List(Class<T> type, List<Map<K, V>> list) {
        if (list == null || list.size() == 0) {
            return new ArrayList<T>();
        }
        try {
            List<T> resultList = new ArrayList<T>();
            for (Map map : list) {
                BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
                T obj = type.newInstance(); // 创建 JavaBean 对象

                // 给 JavaBean 对象的属性赋值
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                for (int i = 0; i < propertyDescriptors.length; i++) {
                    PropertyDescriptor descriptor = propertyDescriptors[i];
                    String propertyName = descriptor.getName();

                    if (map.containsKey(propertyName)) {
                        // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                        Object value = map.get(propertyName);

                        Object[] args = new Object[1];
                        args[0] = value;

                        descriptor.getWriteMethod().invoke(obj, args);
                    }
                }
                resultList.add(obj);
            }
            return resultList;
        } catch (Exception e) {
            return null;
        }
    }

}
