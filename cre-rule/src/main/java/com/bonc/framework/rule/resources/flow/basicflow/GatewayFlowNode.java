package com.bonc.framework.rule.resources.flow.basicflow;

/**
 * @author qxl
 * @version 1.0.0
 * @date 2016年11月14日 上午10:10:09
 */
public abstract class GatewayFlowNode extends AbstractFlowNode {
    private static final long serialVersionUID = 7145486696275646511L;
    public static final String OR = "OR";
    public static final String XOR = "XOR";
    public static final String AND = "AND";

//	private final String optTypeKey = "type";

    private String optType;//OR,XOR,AND

    public String getOptType() {
        return optType;
    }

    public void setOptType(String optType) {
        this.optType = optType;
    }

}
