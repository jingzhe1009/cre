package com.bonc.framework.rule.executor.worker;

import com.bonc.framework.rule.executor.worker.impl.EndTraversalStrategy;
import com.bonc.framework.rule.executor.worker.impl.FalseTraversalStrategy;
import com.bonc.framework.rule.executor.worker.impl.TrueTraversalStrategy;
import com.bonc.framework.rule.resources.flow.FlowNodeStateEnum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 遍历策略工厂
 *
 * @author qxl
 * @version 1.0
 * @date 2018年4月11日 下午4:50:41
 */
public class TraversalStrategyFactory {

    private static Map<FlowNodeStateEnum, ITraversalStrategy> map;

    static {
        map = new ConcurrentHashMap<FlowNodeStateEnum, ITraversalStrategy>();
        map.put(FlowNodeStateEnum.TRUE, new TrueTraversalStrategy());
        map.put(FlowNodeStateEnum.FALSE, new FalseTraversalStrategy());
        map.put(FlowNodeStateEnum.END, new EndTraversalStrategy());
    }

    /**
     * 根据节点状态获取遍历策略
     *
     * @param nodeState
     * @return
     */
    public static ITraversalStrategy getTraversalStrategy(FlowNodeStateEnum nodeState) {
        if (nodeState == null) throw new NullPointerException("The nodeState is null.");
        if (map.containsKey(nodeState)) {
            return map.get(nodeState);
        }
        return null;
    }

}
