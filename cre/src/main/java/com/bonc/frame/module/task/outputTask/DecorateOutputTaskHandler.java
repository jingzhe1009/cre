package com.bonc.frame.module.task.outputTask;

import java.util.List;

/**
 * @author yedunyao
 * @date 2019/8/8 20:09
 */
public class DecorateOutputTaskHandler<T, R> extends AbstractOutputTaskHandler<T, R> {

    protected AbstractOutputTaskHandler<T, R> decorator;

    public DecorateOutputTaskHandler(AbstractOutputTaskHandler decorator) {
        this.decorator = decorator;
    }

    @Override
    public void saveSilence(T t) throws Exception {
        decorator.saveSilence(t);
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
