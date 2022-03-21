package com.bonc.framework.rule.interceptor;

import java.util.Map;

/**
 * 规则引擎执行拦截器，执行之前调用preHandle  调用之后  执行afterCompletion
 *
 * @author qxl
 * @version 1.0
 * @date 2018年3月1日 上午10:54:08
 */
public interface IRuleInterceptor {

    /**
     * preHandle方法是进行处理器拦截用的，顾名思义，该方法将在规则执行之前进行调用Interceptor拦截器是链式的，
     * 可以同时存在多个Interceptor，会根据声明的前后顺序一个接一个的执行，而且所有的Interceptor中的preHandle方法
     * 都会在规则执行之前调用。这种Interceptor链式结构也是可以进行中断的，这种中断方式是令preHandle的返
     * 回值为false，当preHandle的返回值为false的时候整个请求就结束了。
     *
     * @param context
     * @param rule
     * @return
     * @throws Exception
     */
    boolean preHandle(Map<String, Object> param, Object rule) throws Exception;

    /**
     * 该方法也是需要当前对应的Interceptor的preHandle方法的返回值为true时才会执行。
     * 该方法将在整个规则执行完成之后。
     * 这个方法的主要作用是用于对执行结果作进一步处理，
     * 当然这个方法也只能在当前这个Interceptor的preHandle方法的返回值为true时才会执行。
     *
     * @param context
     * @param handler
     * @throws Exception
     */
    void afterCompletion(Map<String, Object> param, Object rule) throws Exception;

}
