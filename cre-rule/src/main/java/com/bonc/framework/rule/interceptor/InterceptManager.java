package com.bonc.framework.rule.interceptor;

import com.bonc.framework.rule.RuleType;
import com.bonc.framework.rule.exception.RuleInterceptorException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 规则拦截器管理者，拦截器所有的操作均通过此类来完成
 *
 * @author qxl
 * @version 1.0
 * @date 2018年3月2日 下午3:42:37
 */
public class InterceptManager {
    private Log log = LogFactory.getLog(getClass());

    //单例模式
    private static InterceptManager manager = new InterceptManager();

    private InterceptManager() {
    }

    public static InterceptManager getManager() {
        if (manager == null) {
            manager = new InterceptManager();
        }
        return manager;
    }

    /**
     * 规则拦截器,key为规则类型
     */
    private Map<String, List<IRuleInterceptor>> interceptMap = new ConcurrentHashMap<String, List<IRuleInterceptor>>();


    /**
     * 规则是否正在执行
     */
    private Map<String, Boolean> isRunMap = new ConcurrentHashMap<String, Boolean>();

    /**
     * 添加规则拦截器
     *
     * @param interceptor
     * @throws RuleInterceptorException
     */
    public void addRuleInterceptor(RuleType ruleType, IRuleInterceptor interceptor) throws RuleInterceptorException {
        String ruleTypeStr = ruleType.toString();
        Boolean isRunning = isRunMap.get(ruleTypeStr);
        if (isRunning != null && isRunning == true) {
            throw new RuleInterceptorException("The rule of type [" + ruleTypeStr + "] is running,con't add RuleInterceptor.");
        }
        List<IRuleInterceptor> list = interceptMap.get(ruleTypeStr);
        if (list == null) {
            list = new ArrayList<IRuleInterceptor>();
        }
        list.add(interceptor);
        this.interceptMap.put(ruleTypeStr, list);
    }

    /**
     * 移除规则拦截器
     *
     * @param interceptor
     * @throws RuleInterceptorException
     */
    public void removeInterceptor(RuleType ruleType, IRuleInterceptor interceptor) throws RuleInterceptorException {
        String ruleTypeStr = ruleType.toString();
        Boolean isRunning = isRunMap.get(ruleTypeStr);
        if (isRunning != null && isRunning == true) {
            throw new RuleInterceptorException("The rule of type [" + ruleTypeStr + "] is running,con't remove RuleInterceptor.");
        }
        if (interceptMap.containsKey(ruleTypeStr)) {
            List<IRuleInterceptor> list = interceptMap.get(ruleTypeStr);
            list.remove(interceptor);
        }
    }

    /**
     * 根据规则类型清除该类型所有的规则拦截器
     *
     * @param ruleType 规则类型
     * @throws RuleInterceptorException
     */
    public void clearInterceptor(RuleType ruleType) throws RuleInterceptorException {
        String ruleTypeStr = ruleType.toString();
        Boolean isRunning = isRunMap.get(ruleTypeStr);
        if (isRunning != null && isRunning == true) {
            throw new RuleInterceptorException("The rule of type [" + ruleTypeStr + "] is running,con't clear RuleInterceptor.");
        }
        List<IRuleInterceptor> list = interceptMap.get(ruleTypeStr);
        if (list != null) {
            list.clear();
        }
    }

    /**
     * 清除所有的规则拦截器
     *
     * @throws RuleInterceptorException
     */
    public void clearInterceptor() throws RuleInterceptorException {
        for (String key : isRunMap.keySet()) {
            Boolean isRunning = isRunMap.get(key);
            if (isRunning != null && isRunning == true) {
                throw new RuleInterceptorException("The rule of type [" + key + "] is running,con't clear RuleInterceptor.");
            }
        }
        interceptMap.clear();
    }

    /**
     * 规则执行前-执行拦截器-preHandle
     *
     * @param ruleType
     * @param param
     * @param rule
     * @return
     * @throws Exception
     */
    public boolean preHandle(RuleType ruleType, Map<String, Object> param, Object rule, boolean isSkipValidation) throws Exception {
        String ruleTypeStr = ruleType.toString();
        List<IRuleInterceptor> interceptList = interceptMap.get(ruleTypeStr);
        if (interceptList == null) {//未配置拦截器
            return true;
        }
        isRunMap.put(ruleTypeStr, true);//设置拦截器正在执行
        boolean preHandleFlag = true;
        for (IRuleInterceptor intercept : interceptList) {
            if (isSkipValidation) {
                if (intercept instanceof ValidationInterceptor) {
                    continue;
                }
            }

            boolean result = intercept.preHandle(param, rule);
            if (!result) {//被拦截
                log.info("This execute is be intercepted.The interceptor is [" + intercept.getClass() + "]");
                preHandleFlag = false;
                break;
            }
        }
        return preHandleFlag;
    }

    /**
     * 规则执行后-执行拦截器-afterCompletion
     *
     * @param param
     * @param rule
     * @throws Exception
     */
    public void afterCompletion(RuleType ruleType, Map<String, Object> param, Object rule) throws Exception {
        String ruleTypeStr = ruleType.toString();
        List<IRuleInterceptor> interceptList = interceptMap.get(ruleTypeStr);
        if (interceptList == null) {//未配置拦截器
            return;
        }
        for (int i = interceptList.size() - 1; i >= 0; i--) {
            interceptList.get(i).afterCompletion(param, rule);
        }
        isRunMap.put(ruleTypeStr, false);//设置拦截器执行结束
    }

}
