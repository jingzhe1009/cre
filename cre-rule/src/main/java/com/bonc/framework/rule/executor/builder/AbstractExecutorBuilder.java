package com.bonc.framework.rule.executor.builder;

import com.bonc.framework.rule.executor.DefaultExecutor;
import com.bonc.framework.rule.executor.IExecutor;
import com.bonc.framework.rule.executor.resolver.DefaultResolver;
import com.bonc.framework.rule.executor.resolver.IResolver;
import com.bonc.framework.rule.executor.worker.ITraversal;
import com.bonc.framework.rule.kpi.IKpiService;
import com.bonc.framework.rule.log.IRuleLog;
import com.bonc.framework.rule.resources.RuleResource;
import com.bonc.framework.rule.resources.flow.FlowNode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 作者: jxw
 * @version 版本: 1.0
 * @date 创建时间: 2018年1月8日 下午2:27:03
 */
public abstract class AbstractExecutorBuilder implements IExecutorBuilder {

    private Map<Class<? extends FlowNode>, IResolver> resolverMapper = new HashMap<>();

    protected ITraversal traversal;

    protected IRuleLog ruleLog;

    protected IKpiService kpiService;

    private IResolver defaultResolver = new DefaultResolver();

//	protected IQLExpressContext<String,Object> context;

    @Override
    public IExecutor buildExecutor(String clazz) throws Exception {
        if (clazz == null || clazz.isEmpty()) {
            return this.buildExecutor();
        }
//		if(this.traversal==null){
//			throw new Exception("build executor fail . traversal is null.");
//		}
//		if(this.context == null){
//			throw new Exception("build executor fail . context is null.");
//		}
        return this.createExecutor(this, clazz);
    }

    @Override
    public IExecutor buildExecutor() throws Exception {
        if (this.traversal == null) {
            throw new Exception("build executor fail . traversal is null.");
        }
//		if(this.context == null){
//			throw new Exception("build executor fail . context is null.");
//		}
        return this.createExecutor(this, DefaultExecutor.class.getName());
    }

    @Override
    public IExecutorBuilder addResolverMapper(Class<? extends FlowNode> clazz, IResolver resolver) {
        this.resolverMapper.put(clazz, resolver);
        return this;
    }

    @Override
    public IExecutorBuilder setTraversal(ITraversal traversal) {
        this.traversal = traversal;
        return this;
    }

    public ITraversal getTraversal() {
        return traversal;
    }

    public abstract ITraversal getTraversal(RuleResource ruleResource) throws Exception;

//	@Override
//	public IExecutorBuilder setContext(IQLExpressContext<String,Object> context) {
//		this.context = context;
//		return this;
//	}
//	
//	public IQLExpressContext<String, Object> getContext() {
//		return context;
//	}

    @Override
    public IExecutorBuilder setRuleLog(IRuleLog ruleLog) {
        this.ruleLog = ruleLog;
        return this;
    }

    @Override
    public IExecutorBuilder setKpiService(IKpiService kpiService) {
        this.kpiService = kpiService;
        return this;
    }

    public IRuleLog getRuleLog() {
        return ruleLog;
    }

    public IKpiService getKpiSetvice() {
        return kpiService;
    }

    public abstract IExecutor createExecutor(IExecutorBuilder builder, String clazz) throws Exception;

    /**
     * 根据节点类型获取对应执行器
     *
     * @param clazz
     * @return
     * @throws Exception
     */
    public IResolver getResolverByFlowNode(Class<? extends FlowNode> clazz) throws Exception {
        if (this.resolverMapper.containsKey(clazz)) {
            return this.resolverMapper.get(clazz);
        }
        return defaultResolver;
    }

}

