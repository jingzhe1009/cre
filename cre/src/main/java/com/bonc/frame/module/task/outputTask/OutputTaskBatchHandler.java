package com.bonc.frame.module.task.outputTask;

import java.util.List;

/**
 * @author yedunyao
 * @date 2019/7/12 14:59
 */
public interface OutputTaskBatchHandler<T> {

    void save(List<T> tasks) throws Exception;

}
