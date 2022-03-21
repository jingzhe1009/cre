package com.bonc.framework.rule.executor;

import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.exception.ExecuteModelException;
import com.bonc.framework.rule.executor.builder.AbstractExecutorBuilder;
import com.bonc.framework.rule.executor.context.IQLExpressContext;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.executor.context.impl.ExecutorResponse;
import com.bonc.framework.rule.executor.context.impl.QLExpressContext;
import com.bonc.framework.rule.executor.resolver.rule.RuleFactory;
import com.bonc.framework.rule.resources.RuleResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * 默认规则执行器
 *
 * @author qxl
 * @version 1.0
 * @date 2018年1月8日 下午3:32:20
 */
public class DefaultExecutor extends AbstractExecutor {
    private Log log = LogFactory.getLog(getClass());

    public DefaultExecutor(AbstractExecutorBuilder builder) {
        super(builder);
    }

    @Override
    public void executor(RuleResource ruleResource, Map<String, Object> param, ConsumerInfo... consumerInfo) throws ExecuteModelException {
        if (ruleResource == null) {
            throw new ExecuteModelException("The ruleResource is null.");
        }
        String express = ruleResource.getRuleContent();
        IQLExpressContext<String, Object> context = new QLExpressContext(param);
        try {
            Object r = RuleFactory.getRule().execute(express, context);
            if (log.isDebugEnabled()) {
                log.debug("表达式执行结果：" + r);
            }
//			System.out.println(r);
        } catch (Exception e) {
            log.error(e);
            throw new ExecuteModelException(e);
        }
    }

    @Override
    public ExecutorResponse executor(ExecutorRequest request) throws ExecuteModelException {
        Map<String, Object> param = request.getParam();
        executor(request.getExecutable(), param, request.getConsumerInfos());
        ExecutorResponse executorResponse = new ExecutorResponse();
        executorResponse.setSuccessed(true);
        executorResponse.setResult(param);
        return executorResponse;
    }

}
