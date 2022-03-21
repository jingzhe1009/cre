package com.bonc.framework.rule.executor.resolver;

import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.exception.ExecuteModelException;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.log.entity.RuleLogDetail;
import com.bonc.framework.rule.resources.flow.FlowNode;

import java.util.Map;

/**
 * @author 作者: jxw
 * @version 版本: 1.0
 * 解析接口
 * @date 创建时间: 2018年1月8日 下午2:15:51
 */
public interface IResolver {

    @Deprecated
    Object resolver(FlowNode node, Map<String, Object> param, ConsumerInfo... consumerInfo) throws ExecuteModelException;

    Object resolver(FlowNode node, ExecutorRequest executorRequest, RuleLogDetail ruleLogDetail) throws ExecuteModelException;

}

