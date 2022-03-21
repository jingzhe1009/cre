package com.bonc.frame.module.threadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author yedunyao
 * @date 2019/6/14 16:48
 */
public class ThreadFactoryImpl implements ThreadFactory {

    private final String namePrefix;
    private final ThreadGroup group;
    private final AtomicLong count;
    private final boolean isDaemon;

    public ThreadFactoryImpl(final String namePrefix, final ThreadGroup group, boolean isDaemon) {
        this.namePrefix = namePrefix;
        this.group = group;
        this.isDaemon = isDaemon;
        this.count = new AtomicLong();
    }

    public ThreadFactoryImpl(final String namePrefix, boolean isDaemon) {
        this(namePrefix, null, isDaemon);
    }

    public ThreadFactoryImpl(final String namePrefix) {
        this(namePrefix, null, true);
    }

    @Override
    public Thread newThread(final Runnable target) {
        final Thread thread = new Thread(this.group, target, this.namePrefix + "-" + this.count.incrementAndGet());
        thread.setDaemon(isDaemon);
        return thread;
    }

}
