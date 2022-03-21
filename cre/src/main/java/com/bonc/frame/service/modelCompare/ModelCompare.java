package com.bonc.frame.service.modelCompare;


import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.entity.modelCompare.entity.ModelCompareResult;
import com.bonc.frame.entity.modelCompare.entity.ModelCompareType;

/**
 * 模型节点比较器
 */
public interface ModelCompare {

    boolean isSupport(String type);

    ModelCompareType getSupport();

    void comparNodePropsJson(ModelCompareResult result, JSONObject oldNodePropsJson, JSONObject newNodePropsJson, String nodeKey);

    void comparNodePropsOthers(JSONObject oldNodeProps, JSONObject newNodeProps, JSONObject oldPropsDifferentJson);

}
