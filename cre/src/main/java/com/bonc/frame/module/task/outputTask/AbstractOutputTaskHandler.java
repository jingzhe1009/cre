package com.bonc.frame.module.task.outputTask;

import java.util.List;

/**
 * @author yedunyao
 * @date 2019/8/8 20:06
 */
public class AbstractOutputTaskHandler<T, R> implements OutputTaskHandler<T, R> {

    @Override
    public void saveSilence(T t) throws Exception {

    }

    @Override
    public R save(T t) throws Exception {
        return null;
    }

    @Override
    public void batchSaveSilence(List<T> batches) throws Exception {

    }

    @Override
    public R batchSave(List<T> batches) throws Exception {
        return null;
    }
}
