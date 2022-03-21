package com.bonc.framework.rule;

import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.cache.DefaultRuleCache;
import com.bonc.framework.rule.cache.ICache;
import com.bonc.framework.rule.compile.DefaultRuleCompile;
import com.bonc.framework.rule.compile.IRuleCompile;
import com.bonc.framework.rule.constant.RuleEngineConstant;
import com.bonc.framework.rule.exception.BuildExecutorException;
import com.bonc.framework.rule.exception.NoCompiledRuleException;
import com.bonc.framework.rule.exception.ParseRuleException;
import com.bonc.framework.rule.exception.RuleInterceptorException;
import com.bonc.framework.rule.executor.IConContext;
import com.bonc.framework.rule.executor.IExecutor;
import com.bonc.framework.rule.executor.builder.DefaultExecutorBuilder;
import com.bonc.framework.rule.executor.builder.IExecutorBuilder;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.executor.context.impl.ExecutorResponse;
import com.bonc.framework.rule.executor.impl.DefaultConContext;
import com.bonc.framework.rule.executor.resolver.*;
import com.bonc.framework.rule.interceptor.InterceptManager;
import com.bonc.framework.rule.interceptor.NumericParamValidationInterceptor;
import com.bonc.framework.rule.kpi.IKpiService;
import com.bonc.framework.rule.log.DefaultRuleLog;
import com.bonc.framework.rule.log.IRuleLog;
import com.bonc.framework.rule.observer.IRuleObserver;
import com.bonc.framework.rule.observer.IRuleObserverable;
import com.bonc.framework.rule.observer.RuleLogObsreverable;
import com.bonc.framework.rule.resources.RuleResource;
import com.bonc.framework.rule.resources.flow.basicflow.PathFlowNode;
import com.bonc.framework.rule.resources.flow.basicflow.impl.*;
import com.bonc.framework.rule.util.RuleEnginePropertiesUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * 规则引擎门面，由此类统一对外提供服务
 *
 * @author qxl
 * @version 1.0
 * @date 2018年3月1日 下午3:15:43
 */
public class RuleEngineFacade {
    private Log log = LogFactory.getLog(getClass());

    /**
     * 单例模式
     */
//	private volatile static RuleEngineFacade ruleEngineFacade;

    private IRuleObserverable ruleObserverable;//被观察主题

    private IRuleCompile ruleCompile;//规则编译接口

    private IRuleLog ruleLog;//规则日志记录接口

    private IKpiService kpiService;//指标接口

    private IExecutorBuilder builder;//规则执行器的构造器

    private IConContext conContext;

    //编译之后的规则缓存
    private ICache<String, RuleResource> ruleCache;

    public RuleEngineFacade() {
        ruleObserverable = new RuleLogObsreverable();
        ruleCompile = new DefaultRuleCompile();
        ruleLog = new DefaultRuleLog();
        ruleCache = new DefaultRuleCache<String, RuleResource>();
        conContext = new DefaultConContext();
        initInterceptor();  // 初始化拦截器
        initBuilder();//初始化builder
    }

    /**
     * 设置规则编译器
     *
     * @param ruleCompile
     */
    public void setRuleCompile(IRuleCompile ruleCompile) {
//        if (ruleCompile != null) {
//            this.ruleCompile = ruleCompile;
//        }
        this.ruleCompile = ruleCompile;
    }

    /**
     * 设置规则日志记录接口实现
     *
     * @param ruleLog
     */
    public void setRuleLog(IRuleLog ruleLog) {
//        if (ruleLog != null) {
//            this.ruleLog = ruleLog;
//        }
        this.ruleLog = ruleLog;
        if (this.builder != null) {
            this.builder.setRuleLog(ruleLog);
        }
    }

    /**
     * 设置规则日志记录接口实现
     *
     * @param kpiService
     */
    public void setKpiService(IKpiService kpiService) {
//        if (ruleLog != null) {
//            this.ruleLog = ruleLog;
//        }
        this.kpiService = kpiService;
        if (this.builder != null) {
            this.builder.setKpiService(kpiService);
        }
    }

    public IRuleLog getRuleLog() {
        return ruleLog;
    }

    public IKpiService getKpiService() {
        return kpiService;
    }

    /**
     * 设置规则缓存器
     *
     * @param ruleCache
     */
    public void setRuleCache(ICache<String, RuleResource> ruleCache) {
//        if (ruleCache != null)
        this.ruleCache = ruleCache;
    }

    public ICache<String, RuleResource> getRuleCache() {
        return this.ruleCache;
    }

    /**
     * 初始化规则执行器的构造器
     */
    private void initInterceptor() {
        NumericParamValidationInterceptor numericParamValidationInterceptor = new NumericParamValidationInterceptor();
        try {
            InterceptManager.getManager().addRuleInterceptor(RuleType.expRule_type, numericParamValidationInterceptor);
            InterceptManager.getManager().addRuleInterceptor(RuleType.treeRule_type, numericParamValidationInterceptor);
        } catch (RuleInterceptorException e) {
            log.warn("初始化拦截器失败", e);
        }
    }

    /**
     * 初始化规则执行器的构造器
     */
    public void initBuilder() {
        this.initBuilder(new DefaultExecutorBuilder());
    }

    /**
     * 重载方法
     * 初始化规则执行器的构造器
     */
    public void initBuilder(IExecutorBuilder builder) {
        this.builder = null;
        this.builder = builder;
        builder.addResolverMapper(ActionFlowNode.class, new ActionResolver())
                .addResolverMapper(PathFlowNode.class, new PathResolver())
                .addResolverMapper(InterfaceFlowNode.class, new InterfaceResolver())
                .addResolverMapper(FunctionFlowNode.class, new FunctionResolver())
                .addResolverMapper(EndFlowNode.class, new EndResolver())
                .addResolverMapper(StartFlowNode.class, new StartResolver())
//                .addResolverMapper(CompoundFlowNode.class, new EndResolver())
//										.setTraversal(new DefaultTraversal())
                .setRuleLog(ruleLog).setKpiService(kpiService);
    }

    /**
     * 获取规则执行器的构造器
     *
     * @return
     */
    public IExecutorBuilder getBuilder() {
        return this.builder;
    }


    /**
     * 注册观察者
     *
     * @param observer
     */
    public void registerRuleObserver(IRuleObserver observer) {
        observer.setRuleObserverable(ruleObserverable);
        ruleObserverable.registerObserver(observer);
    }

    /**
     * 根据规则Id清除规则，即重新编译
     *
     * @param ruleId
     */
    public void clean(String folderId, String ruleId) {
        ruleCache.remove(ruleId);
        this.conContext.cleanByRule(ruleId);
        this.conContext.clean(folderId);
    }

    /**
     * 清除全部规则，即重新编译全部规则
     */
    public void clean() {
        ruleCache.clear();
        this.conContext.clean();
    }

    public RuleResource compileRule(String folderId, String ruleId, RuleType ruleType, String ruleName,
                                    String ruleContent) throws Exception {
        return this.compileRule(folderId, ruleId, ruleType, ruleName, ruleContent, false);
    }

    /**
     * 编译规则
     *
     * @param ruleId
     * @param ruleType
     * @param ruleName
     * @param ruleContent
     * @param isLog
     * @throws Exception
     */
    public RuleResource compileRule(String folderId, String ruleId, RuleType ruleType, String ruleName,
                                    String ruleContent, boolean isLog) throws Exception {
        //规则编译
        if (!ruleCache.containsKey(ruleId)) {
            synchronized (ruleCache) {
                if (!ruleCache.containsKey(ruleId)) {
                    RuleResource ruleResource = ruleCompile.compileRule(folderId, ruleId, ruleType, ruleName, ruleContent, isLog);
                    try {
                        ruleCache.put(ruleId, ruleResource);
                        return ruleResource;
                    } catch (Exception e) {
                        throw new ParseRuleException("编译后的模型加入到缓存异常", e);
                    }
                }
            }
        }
        // 注意: 多线程下取出的是相同对象
        final RuleResource resource = ruleCache.get(ruleId);
        return resource;
    }


    /**
     * 执行已编译好的规则
     *
     * @param ruleId 规则ID
     * @param param  规则执行输入参数值
     * @throws Exception
     */
    @Deprecated
    public void execute(String ruleId, Map<String, Object> param,
                        boolean isSkipValidation, ConsumerInfo... consumerInfo) throws Exception {
        try {
            //获取已编译好的规则
            final RuleResource resource = ruleCache.get(ruleId);
            this.executeRule(resource, param, isSkipValidation, consumerInfo);
        } catch (Exception e) {
//			log.error(e);
            //通知观察者
            ruleObserverable.notifyObservers("###[EXCEPTION] the ruleId is " + ruleId + ".", e);
            throw e;
        }
    }

    public ExecutorResponse execute(ExecutorRequest executorRequest) throws Exception {
        String ruleId = executorRequest.getRuleId();
        try {
            //获取已编译好的规则
            final RuleResource resource = ruleCache.get(ruleId);
//			RuleResource ruleResource = new RuleResource(resource);//ruleCompile.getCompiledRule(ruleId);
            return this.executeRule(resource, executorRequest);
        } catch (NoCompiledRuleException e) {
            ruleObserverable.notifyObservers("模型未编译。 the ruleId is " + ruleId + ".", e);
            return ExecutorResponse.newFailedExecutorResponse("模型未编译。");
        } catch (Exception e) {
//			log.error(e);
            //通知观察者
            ruleObserverable.notifyObservers("模型执行异常。 the ruleId is " + ruleId + ".", e);
            return ExecutorResponse.newFailedExecutorResponse("模型执行异常。" + e.getMessage());
        }
    }

    /**
     * 执行规则
     *
     * @param ruleId      规则ID
     * @param ruleType    规则类型
     * @param ruleContent 规则内容
     * @param param       规则执行输入参数值
     * @throws Exception
     */
    @Deprecated
    public void execute(String folderId, String ruleId, RuleType ruleType, String ruleName,
                        String ruleContent, Map<String, Object> param, boolean isLog,
                        boolean isSkipValidation, ConsumerInfo... consumerInfo) throws Exception {
        try {
            //规则编译
            RuleResource ruleResource = this.compileRule(folderId, ruleId, ruleType, ruleName, ruleContent, isLog);
            this.executeRule(ruleResource, param, isSkipValidation, consumerInfo);
        } catch (Exception e) {
            log.error(e);
            //通知观察者
            ruleObserverable.notifyObservers("###[EXCEPTION] the ruleId is " + ruleId + ".", e);
            throw e;
        }
    }


    //通用规则执行方法
    private ExecutorResponse executeRule(RuleResource ruleResource, ExecutorRequest executorRequest) throws Exception {
        if (ruleResource == null) {
            throw new NoCompiledRuleException("模型执行失败，未找到编译好的模型。");
        }

        executorRequest.setExecutable(ruleResource);
        Map<String, Object> param = executorRequest.getParam();
        boolean isSkipValidation = executorRequest.isSkipValidation();

        RuleType ruleType = ruleResource.getRuleType();
        try {
            IExecutor executor = createRuleExecutor(ruleResource);
            //规则执行前-执行拦截器-preHandle
            // TODO: 完善异常信息
            boolean preHandleFlag = InterceptManager.getManager().preHandle(ruleType, param, ruleResource, isSkipValidation);
            if (!preHandleFlag) {//被拦截器拦截
                // log
                return ExecutorResponse.newFailedExecutorResponse("未通过拦截器的校验");
            }
            //执行规则
            ExecutorResponse executorResponse = executor.executor(executorRequest);
            //规则执行后-执行拦截器-afterCompletion
            InterceptManager.getManager().afterCompletion(ruleType, param, ruleResource);
            return executorResponse;
        } catch (Exception e) {
            log.error("执行模型失败", e);
            return ExecutorResponse.newFailedExecutorResponse("执行模型失败。" + e.getMessage());
        }
    }

    @Deprecated
    private void executeRule(RuleResource ruleResource, Map<String, Object> param, boolean isSkipValidation, ConsumerInfo... consumerInfo) throws Exception {
        if (ruleResource == null) {
            throw new NoCompiledRuleException("模型执行失败，未找到编译好的模型。");
        }
        RuleType ruleType = ruleResource.getRuleType();
        try {
            IExecutor executor = createRuleExecutor(ruleResource);
            //规则执行前-执行拦截器-preHandle
            boolean preHandleFlag = InterceptManager.getManager().preHandle(ruleType, param, ruleResource, isSkipValidation);
            if (!preHandleFlag) {//被拦截器拦截
                // log
                return;
            }
            //执行规则
            executor.executor(ruleResource, param, consumerInfo);
            //规则执行后-执行拦截器-afterCompletion
            InterceptManager.getManager().afterCompletion(ruleType, param, ruleResource);
        } catch (Exception e) {
            throw e;
        }
    }


    //创建规则执行器
    private IExecutor createRuleExecutor(RuleResource ruleResource) throws Exception {
        // 根据配置文件中的配置创建执行器
        String executorClazz = RuleEnginePropertiesUtil.getProperty(
                RuleEngineConstant.PREFIX_RULEEXECUTORCLASS + (ruleResource.getRuleType().toString()));
        try {
            IExecutor executor = builder.buildExecutor(executorClazz);
//			ruleResource.setExecutor(executor);
            return executor;
        } catch (Exception e) {
            log.error(e);
            throw new BuildExecutorException(e);
        }
    }

    public IConContext getConContext() {
        return conContext;
    }

    public void setConContext(IConContext conContext) {
        this.conContext = conContext;
    }


}
