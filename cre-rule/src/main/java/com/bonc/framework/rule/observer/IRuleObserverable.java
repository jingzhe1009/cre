package com.bonc.framework.rule.observer;

/**
 * 被观察者（观察主题）接口，当规则执行报错时，通知所有的观察者
 *
 * @author qxl
 * @version 1.0
 * @date 2018年3月1日 上午10:55:16
 */
public interface IRuleObserverable {

    /**
     * 注册观察者
     *
     * @param observer
     * @return
     */
    void registerObserver(IRuleObserver observer);

    /**
     * 移除观察者
     *
     * @param observer
     * @return
     */
    void removeObserver(IRuleObserver observer);

    /**
     * 通知所有观察者有消息变化
     */
    void notifyObservers(String msg, Exception e);

}
