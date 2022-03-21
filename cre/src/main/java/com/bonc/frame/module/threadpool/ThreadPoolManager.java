package com.bonc.frame.module.threadpool;


import com.google.common.util.concurrent.MoreExecutors;

/**
 * 线程池管理类
 *
 * @author yedunyao
 * @since 2019/12/6 19:30
 */
public class ThreadPoolManager {

    // TODO: 实现线程池统一管理、线程监控

    public void DirectThreadPool() {
        // 不创建线程直接运行
        MoreExecutors.directExecutor();
    }

}