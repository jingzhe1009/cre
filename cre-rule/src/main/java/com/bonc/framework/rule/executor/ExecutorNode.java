package com.bonc.framework.rule.executor;

import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.exception.ExecuteException;
import com.bonc.framework.rule.executor.builder.AbstractExecutorBuilder;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.executor.context.impl.ExecutorResponse;
import com.bonc.framework.rule.log.IRuleLog;
import com.bonc.framework.rule.log.entity.TraceModelLogDetail;

import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/4/19 18:13
 */
public interface ExecutorNode {

    // 返回本次执行结果的命中数
    @Deprecated
    int executor(Map<String, Object> param, boolean isLog, AbstractExecutorBuilder builder,
                 String logId, IRuleLog ruleLog, ConsumerInfo... consumerInfo) throws Exception;

    @Deprecated
    int executor(Map<String, Object> param, boolean isLog,
                 AbstractExecutorBuilder builder,
                 String logId, IRuleLog ruleLog,
                 boolean isTraceDetail, TraceModelLogDetail traceModelLogDetail,
                 ConsumerInfo... consumerInfo) throws Exception;

    int executor(AbstractExecutorBuilder builder, ExecutorRequest executorRequest, ExecutorResponse executorResponse) throws Exception;
}
