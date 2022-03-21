package com.bonc.frame.module.task.delayed;

import kafka.utils.DelayedItem;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author yedunyao
 * @date 2019/7/12 14:04
 */
public class DelayedTask<T> implements Delayed {

    private DelayedItem<T> delayedItem;

    public DelayedTask(T t, long delay, TimeUnit unit) {
        this.delayedItem = new DelayedItem<>(t, delay, unit);
    }

    public DelayedTask(T t, long delay) {
        this.delayedItem = new DelayedItem<>(t, delay);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return delayedItem.getDelay(unit);
    }

    @Override
    public int compareTo(Delayed o) {
        return delayedItem.compareTo(((DelayedTask) o).delayedItem);
    }

    @Override
    public String toString() {
        return "DelayedTask{" +
                "delayedItem=" + delayedItem +
                '}';
    }
}
