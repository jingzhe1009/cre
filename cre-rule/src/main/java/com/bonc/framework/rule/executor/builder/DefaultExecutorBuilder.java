package com.bonc.framework.rule.executor.builder;

import com.bonc.framework.rule.constant.RuleEngineConstant;
import com.bonc.framework.rule.exception.ExecuteException;
import com.bonc.framework.rule.executor.IExecutor;
import com.bonc.framework.rule.executor.worker.ITraversal;
import com.bonc.framework.rule.resources.RuleResource;
import com.bonc.framework.rule.util.RuleEnginePropertiesUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Constructor;

/**
 * @author 作者: jxw
 * @version 版本: 1.0
 * @date 创建时间: 2018年1月8日 下午2:47:21
 */
public class DefaultExecutorBuilder extends AbstractExecutorBuilder {

    private Log log = LogFactory.getLog(getClass());

    @Override
    public IExecutor createExecutor(IExecutorBuilder builder, String clazz) throws Exception {
        try {
            Class<?> executorClass = Class.forName(clazz);
            Constructor<?> c = executorClass.getDeclaredConstructor(new Class[]{AbstractExecutorBuilder.class});
            c.setAccessible(true);
            IExecutor executor = (IExecutor) c.newInstance(new Object[]{builder});
            return executor;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public ITraversal getTraversal(RuleResource ruleResource) throws Exception {
        // 根据配置文件中的配置创建遍历器
        String taversalClazz = RuleEnginePropertiesUtil.getProperty(
                RuleEngineConstant.PREFIX_RULETRAVERSALCLASS + (ruleResource.getRuleType().toString()));

        log.info("Used traversal: " + taversalClazz);

        try {
            Class<?> executorClass = Class.forName(taversalClazz);
            Constructor<?> c = executorClass.getDeclaredConstructor();
            c.setAccessible(true);
            ITraversal traversal = (ITraversal) c.newInstance();
            return traversal;
        } catch (Exception e) {
            throw e;
        }

//		return new DefaultTraversal();
//		return new BFSTopoTraversal();
    }


}

