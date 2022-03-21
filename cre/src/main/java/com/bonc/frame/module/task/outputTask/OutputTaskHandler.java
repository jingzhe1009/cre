package com.bonc.frame.module.task.outputTask;

import java.util.List;

/**
 * @author yedunyao
 * @date 2019/7/12 14:59
 */
public interface OutputTaskHandler<T, R> {

    void saveSilence(T t) throws Exception;

    R save(T t) throws Exception;

    void batchSaveSilence(List<T> batches) throws Exception;

    R batchSave(List<T> batches) throws Exception;

}
