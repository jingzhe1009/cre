package com.bonc.framework.rule.executor.builder;

import com.bonc.framework.rule.executor.IExecutor;
import com.bonc.framework.rule.executor.resolver.IResolver;
import com.bonc.framework.rule.executor.worker.ITraversal;
import com.bonc.framework.rule.kpi.IKpiService;
import com.bonc.framework.rule.log.IRuleLog;
import com.bonc.framework.rule.resources.flow.FlowNode;

/**
 * 规则执行器的构造器接口
 * 使用方法：
 * IExecutorBuilder builder = new DefaultExecutorBuilder();
 * builder.addResolverMapper(x).setTraversal(x).builExecutor();
 *
 * @author 作者: jxw
 * @version 版本: 1.0
 * @date 创建时间: 2018年1月8日 下午2:23:21
 */
public interface IExecutorBuilder {

    /**
     * 构造执行器接口
     *
     * @return
     * @throws Exception
     */
    public IExecutor buildExecutor() throws Exception;

    /**
     * 构造执行器接口
     *
     * @return
     * @throws Exception
     */
    public IExecutor buildExecutor(String clazz) throws Exception;


    /**
     * 添加不同类型的执行者
     *
     * @param clazz
     * @param resolver
     * @return
     */
    public IExecutorBuilder addResolverMapper(Class<? extends FlowNode> clazz, IResolver resolver);

    /**
     * 设置遍历器
     *
     * @param traversal
     * @return
     */
    public IExecutorBuilder setTraversal(ITraversal traversal);

    /**
     * 设置上下文内容,规则执行参数等
     * @param context
     * @return
     */
//	public IExecutorBuilder setContext(IQLExpressContext<String,Object> context);

    /**
     * 设置日志记录实现
     *
     * @param traversal
     * @return
     */
    public IExecutorBuilder setRuleLog(IRuleLog ruleLog);


    public IExecutorBuilder setKpiService(IKpiService kpiService);


}

