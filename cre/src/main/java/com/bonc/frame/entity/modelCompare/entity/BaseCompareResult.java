package com.bonc.frame.entity.modelCompare.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2019/12/11 17:50
 */
public  class BaseCompareResult {

    /**
     * map<event,Map<type>>
     */
    private Map<String, Map<String, List<Object>>> different = new HashMap<>(8);

    private static final String add_EVEN = "add";
    private static final String update_EVEN = "update";
    private static final String delete_EVEN = "delete";

    /**
     * 从different中获取该类,改事件的List
     * @param type
     * @param event
     * @return
     */
    public List<Object> getListFromDifferentMap(String type, String event) {

        if (different == null) {
            different = new HashMap<>();
        }
        Map<String, List<Object>> stringListMap = different.get(event);
        if (stringListMap == null) {
            stringListMap = new HashMap<>();
            different.put(event, stringListMap);
        }
        List<Object> list = stringListMap.get(type);
        if (list == null) {
            list = new ArrayList<>();
            stringListMap.put(type, list);
        }
        return list;
    }

    /**
     * 判断different是不是null
     * @return
     */
    public boolean isNotEmptyDifferent() {
        boolean fl = false;
        if (different == null || different.isEmpty()) {
            return fl;
        }
        for (String type : different.keySet()) {
            Map<String, List<Object>> typeDifferent = different.get(type);
            if (typeDifferent != null && !typeDifferent.isEmpty()) {
                for (String key : typeDifferent.keySet()) {
                    List<Object> value = typeDifferent.get(key);
                    if (value != null && !value.isEmpty()) {
                        fl = true;
                    }
                }
            }
        }
        return fl;
    }
}
