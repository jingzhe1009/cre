package com.bonc.frame.service.modelCompare;

import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.entity.modelCompare.entity.ModelCompareType;
import com.bonc.frame.util.ModelComparUtil;
import org.springframework.stereotype.Service;

@Service
public class InterfaceModelCompareImp extends AbstractModelCompare {

    @Override
    public ModelCompareType getSupport() {
        return ModelCompareType.INTERFACE;
    }

    @Override
    public void comparNodePropsOthers(JSONObject oldNodeProps, JSONObject newNodeProps, JSONObject rectNodePropsDifferentJson) {
        String oldText = oldNodeProps.getString("ointerface");
        String newText = newNodeProps.getString("ointerface");

        if (!ModelComparUtil.isEqualsString(oldText, newText)) {
            rectNodePropsDifferentJson.put("ointerface", JSONObject.parseObject(oldText));
            rectNodePropsDifferentJson.put("new_ointerface", JSONObject.parseObject(newText));
        }
    }
}
