package com.bonc.framework.rule.executor;

import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.exception.ExecuteModelException;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.executor.context.impl.ExecutorResponse;
import com.bonc.framework.rule.resources.RuleResource;

import java.util.Map;

/**
 * 规则执行器接口
 *
 * @author 作者: jxw
 * @version 版本: 1.0
 * @date 创建时间: 2018年1月8日 下午2:21:56
 */
public interface IExecutor {

    /**
     * 执行规则
     *
     * @param executable
     * @param param
     * @throws Exception
     */
    void executor(RuleResource executable, Map<String, Object> param, ConsumerInfo... consumerInfo) throws ExecuteModelException;

    ExecutorResponse executor(ExecutorRequest request) throws ExecuteModelException;

}

