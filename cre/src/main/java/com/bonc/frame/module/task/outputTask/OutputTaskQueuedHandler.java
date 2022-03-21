package com.bonc.frame.module.task.outputTask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author yedunyao
 * @date 2019/7/12 15:04
 */
public class OutputTaskQueuedHandler<T> {

    private BlockingQueue<T> queue = new LinkedBlockingQueue<>();

    public void save(T data) throws Exception {

    }

}
