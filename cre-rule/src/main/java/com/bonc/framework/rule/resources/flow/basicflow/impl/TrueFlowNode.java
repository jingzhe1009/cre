package com.bonc.framework.rule.resources.flow.basicflow.impl;

import com.bonc.framework.rule.resources.flow.annotation.FlowNodeAnno;
import com.bonc.framework.rule.resources.flow.basicflow.SimpleFlowNode;

import java.util.Map;

/**
 * @author 作者: jxw
 * @version 版本: 1.0
 * @date 创建时间: 2016年11月16日 下午3:43:17
 */
@FlowNodeAnno(type = "T")
public class TrueFlowNode extends SimpleFlowNode {
    private static final long serialVersionUID = -9119565966652962878L;
    private String bussType;

    public String getBussType() {
        return bussType;
    }


    @Override
    public void serialize(Map<String, String> data) {
        this.bussType = data.get("");
        return;
    }
}

