package com.bonc.framework.rule.resources.flow;

/**
 * 节点状态枚举
 * <p>
 * 根据节点的状态制定遍历策略 {@link com.bonc.framework.rule.executor.worker.ITraversalStrategy}
 * {@link com.bonc.framework.rule.executor.worker.TraversalStrategyFactory}
 *
 * @author qxl
 * @version 1.0
 * @date 2018年4月11日 下午4:41:19
 */
public enum FlowNodeStateEnum {

    TRUE("true"),
    FALSE("false"),
    END("end");

    private String state;

    private FlowNodeStateEnum(String state) {
        this.state = state;
    }

    public String toString() {
        return this.state;
    }

}
