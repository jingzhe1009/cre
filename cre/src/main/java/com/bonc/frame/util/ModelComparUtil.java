package com.bonc.frame.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;

public class ModelComparUtil {
    /**
     * 将List<Map<String,Object>> 转化为 Map<String,Map<String,Object>>
     * results 的 key为 List里的Map中的 keyProperty 字段的值
     *
     * @param actionsJSONArrayString List<Map<String,Object>> 的JSON格式
     */
    public static JSONObject paseMapByKey(String actionsJSONArrayString, String keyProperty) {
        JSONArray actions = JSONArray.parseArray(actionsJSONArrayString);
        if (actions == null || actions.isEmpty()) {
            return null;
        }
        JSONObject result = new JSONObject();
        for (Object o : actions) {
            JSONObject action = JSONObject.parseObject(paseObjectToString(o));
            if (action != null && !action.isEmpty()) {
                result.put(action.getString(keyProperty), action);
            }
        }
        return result;
    }

    /**
     * 比较两个JSONOBject中的key的value---value只能是字符串或基本类型
     * 如果不一样,放到different中
     */
    public static void comparJsonValueDifferent(JSONObject oldJson, JSONObject newJson, String comparKey, JSONObject oldDifferent) {
        String oldValueString = null;
        Object oldValue = null;
        if (oldJson != null) {
            oldValueString = oldJson.getString(comparKey);
            oldValue = oldJson.get(comparKey);
        }
        String newValueString = null;
        Object newValue = null;
        if (newJson != null) {
            newValueString = newJson.getString(comparKey);
            newValue = newJson.get(comparKey);
        }
        if (!isEqualsString(oldValueString, newValueString)) {
            oldDifferent.put(comparKey, oldValue);
            oldDifferent.put("new_" + comparKey, newValue);
        }
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }


    /**
     * 判断Object是不是空
     * 如果是string和基本类型,判断值是不是空
     * 如果是对象类型,判断它的toString是不是空
     */
    public static boolean isBlank(Object object) {
        if (object != null && !StringUtils.isBlank(object + "")) {
            return false;
        }
        return true;
    }

    /**
     * 将子节点放到父节点中,
     * 如果子节点有一个为空,则不放
     *
     * @param key 放入父节点中的key
     */
    public static void setChildToParentJSON(JSONObject oldParent, JSONObject oldChild, String key) {
        if (isBlank(key)) {
            return;
        }
        if (!isBlank(oldChild)) {
            oldParent.put(key, oldChild);
        }
    }

    /**
     * 判断两个字符串是否相等,
     * 如果都为null 返回true
     */
    public static boolean isEqualsString(Object str1, Object str2) {
        if (str1 != null) {
            return str1.equals(str2);
        }
        return str2 == null; //这里 str1一定等于null , 所以只用判断 str2是不是等于null就行 ; 如果都为null,返回true
    }

    /**
     * 获取 Map<String,Object>的value 并转化为String
     *
     * @param jsonObject
     * @param key
     * @return
     */
    public static String getMapValue(Map<String, Object> jsonObject, String key) {
        if (jsonObject == null) {
            return null;
        }
        if (isBlank(jsonObject.get(key))) {
            return null;
        }
        return jsonObject.get(key) + "";
    }

    /**
     * 获取 JSONObject的value 并转化为String
     *
     * @param jsonObject
     * @param key
     * @return
     */
    public static String getJSONValue(JSONObject jsonObject, String key) {
        if (jsonObject == null) {
            return null;
        }
        return jsonObject.getString(key);
    }

    /**
     * 获取jsonString中的value,并转化为String
     *
     * @param jsonString
     * @param key
     * @return
     */
    public static String getJsonStringValueString(String jsonString, String key) {
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        if (jsonObject == null) {
            return null;
        }
        if (jsonObject.get(key) == null) {
            return null;
        }
        return jsonObject.get(key) + "";
    }

    /**
     * 获取jsonString中的value,并转化为String
     *
     * @param jsonString
     * @param key
     * @return
     */
    public static Object getJsonStringValue(String jsonString, String key) {
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        if (jsonObject == null) {
            return null;
        }
        return jsonObject.get(key);
    }

    public static String paseObjectToString(Object o) {
        if (o == null) {
            return null;
        }
        return o + "";
    }

    /**
     * 取交集
     */
    public static Set<String> intersectionSet(Set<String> set1, Set<String> set2) {
        Set<String> result = new HashSet<String>();
        if (set1 != null && !set1.isEmpty() && set2 != null && !set2.isEmpty()) {
            result.addAll(set1);
            result.retainAll(set2);
        }
        return result;
    }

    /**
     * 取set1相对于set2的差集
     * 例 set1=[1,2,3,4]  set2=[3456]
     * return [1,2]
     */
    public static Set<String> differenceSet(Set<String> set1, Set<String> set2) {
        if (set1 == null || set1.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> result = new HashSet<String>(set1);   // 这里不能改成 new HashSet<>(set1) ,否则 result 有可能为null ,会报空指针异常
        if (set2 != null && !set2.isEmpty()) {
            result.removeAll(set2);
        }
        return result;
    }

    /**
     * 取并集
     */
    public static Set<String> unionSet(Set<String> set1, Set<String> set2) {
        Set<String> result = new HashSet<String>();
        if (set1 != null && !set1.isEmpty()) {
            result.addAll(set1);   // 这里不能改成 new HashSet<>(set1) ,否则 result 有可能为null ,会报空指针异常
        }
        if (set2 != null && !set2.isEmpty()) {
            result.addAll(set2);
        }
        return result;
    }


    /**
     * 比较两个实体属性值，返回一个map以有差异的属性名为key，value为一个Map分别存oldObject,newObject此属性名的值
     *
     * @param oldObject 进行属性比较的对象1
     * @param newObject 进行属性比较的对象2
     * @return 属性差异比较结果map
     */
    public static Map<String, Object> compareFields(Object oldObject, Object newObject) {
        Map<String, Object> map = null;

        try {
            /**
             * 只有两个对象都是同一类型的才有可比性
             */
            map = new HashMap<String, Object>();
            if (oldObject == null && newObject == null) {
                return map;
            }

            Class clazz = null;
            if (newObject != null) {
                clazz = newObject.getClass();
            }
            if (oldObject != null) {
                clazz = oldObject.getClass();
            }
            if (clazz == null) {
                return map;
            }
            //获取object的所有属性
            PropertyDescriptor[] pds = Introspector.getBeanInfo(clazz, Object.class).getPropertyDescriptors();

            for (PropertyDescriptor pd : pds) {
                //遍历获取属性名
                String name = pd.getName();

                //获取属性的get方法
                Method readMethod = pd.getReadMethod();

                // 在oldObject上调用get方法等同于获得oldObject的属性值
                Object oldValue = null;
//                if (oldObject != null) {
//                    oldValue = readMethod.invoke(oldObject);
//                }
                // 在newObject上调用get方法等同于获得newObject的属性值
                Object newValue = null;
//                if (newObject != null) {
//                    newValue = readMethod.invoke(newObject);
//                }

                if (oldObject == null) {
                    newValue = readMethod.invoke(newObject);
                    map.put(name, newValue);
                } else if (newObject == null) {
                    oldValue = readMethod.invoke(oldObject);
                    map.put(name, oldValue);
                } else {
                    oldValue = readMethod.invoke(oldObject);
                    newValue = readMethod.invoke(newObject);
                    if (oldValue instanceof List) {
                        continue;
                    }
                    if (oldValue instanceof Timestamp) {
                        oldValue = new Date(((Timestamp) oldValue).getTime());
                    }

                    if (newValue instanceof List) {
                        continue;
                    }


                    if (newValue instanceof Timestamp) {
                        newValue = new Date(((Timestamp) newValue).getTime());
                    }

                    if (oldValue == null && newValue == null) {
                        continue;
                    } else if (oldValue == null) {

                        map.put(name, oldValue);
                        map.put("new_" + name, newValue);
                        continue;
                    }
                    if (!oldValue.equals(newValue)) {// 比较这两个值是否相等,不等就可以放入map了
                        map.put(name, oldValue);
                        map.put("new_" + name, newValue);
                    }
                }

//                if (oldValue instanceof List) {
//                    continue;
//                }
//                if (oldValue instanceof Timestamp) {
//                    oldValue = new Date(((Timestamp) oldValue).getTime());
//                }
//
//                if (newValue instanceof List) {
//                    continue;
//                }
//
//
//                if (newValue instanceof Timestamp) {
//                    newValue = new Date(((Timestamp) newValue).getTime());
//                }
//
//                if (oldValue == null && newValue == null) {
//                    continue;
//                } else if (oldValue == null && newValue != null) {
//
//                    map.put("old_" + name, oldValue);
//                    map.put(name, newValue);
//
//
//                    continue;
//                }
//
//                if (!oldValue.equals(newValue)) {// 比较这两个值是否相等,不等就可以放入map了
//                    map.put("old_" + name, oldValue);
//                    map.put(name, newValue);
//                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

}


