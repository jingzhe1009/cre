package com.bonc.frame.util;



import com.alibaba.fastjson.JSONObject;


import java.util.List;


public class HAUtils {
    public static List<String> castList(Object object){
        List<String> map = JSONObject.parseArray(JSONObject.toJSONString(object), String.class);
        return map;
    }
}
