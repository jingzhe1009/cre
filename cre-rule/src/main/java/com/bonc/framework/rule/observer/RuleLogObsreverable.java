package com.bonc.framework.rule.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 规则执行过程中日志观察主题
 *
 * @author qxl
 * @version 1.0
 * @date 2018年3月1日 下午4:12:58
 */
public class RuleLogObsreverable implements IRuleObserverable {

    /**
     * 用来存放和记录观察者
     */
    private List<IRuleObserver> observers = new ArrayList<IRuleObserver>();

    @Override
    public synchronized void registerObserver(IRuleObserver observer) {
        observers.add(observer);
    }

    @Override
    public synchronized void removeObserver(IRuleObserver observer) {
        int i = observers.indexOf(observer);
        if (i >= 0) {
            observers.remove(i);
        }
    }

    @Override
    public synchronized void notifyObservers(String msg, Exception e) {
        for (int i = 0; i < observers.size(); i++) {
            IRuleObserver o = observers.get(i);
            o.update(msg, e);
        }
    }

}
