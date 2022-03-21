package com.bonc.frame.module.task.outputTask.db;

import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.module.task.outputTask.AbstractOutputTaskHandler;

import java.util.List;

/**
 * @author yedunyao
 * @date 2019/7/12 15:14
 */
public class OracleHander<T, R> extends AbstractOutputTaskHandler<T, R> {

    private DataSource dbInfo;


    // List<Map>
    @Override
    public void batchSaveSilence(List<T> batches) throws Exception {
        // 转化sql 插入
    }

}
