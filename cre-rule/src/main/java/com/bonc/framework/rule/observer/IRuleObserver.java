package com.bonc.framework.rule.observer;

/**
 * 观察者接口
 *
 * @author qxl
 * @version 1.0
 * @date 2018年3月1日 上午10:56:38
 */
public interface IRuleObserver {

    /**
     * 观察者更新观察到的内容
     *
     * @param msg
     * @param e
     */
    void update(String msg, Exception e);

    /**
     * 设置被观察者，方便移除此观察者
     *
     * @param ruleObserverable
     */
    void setRuleObserverable(IRuleObserverable ruleObserverable);


}
