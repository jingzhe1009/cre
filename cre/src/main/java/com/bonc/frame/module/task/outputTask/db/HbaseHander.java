package com.bonc.frame.module.task.outputTask.db;

import com.bonc.frame.module.task.outputTask.DecorateOutputTaskHandler;
import com.bonc.frame.module.task.outputTask.file.CsvHandler;

import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/7/12 15:14
 */
public class HbaseHander<T, R> extends DecorateOutputTaskHandler<T, R> {

    public HbaseHander() {
        super(new CsvHandler());
    }

    @Override
    public void batchSaveSilence(List<T> batches) throws Exception {
        final CsvHandler csvHandler = (CsvHandler) super.decorator;

        // 存储临时文件
        final String path = csvHandler.batchSave((List<Map<String, Object>>) batches);

        // insert table(f1, f2...) 'CLIENT:E:\\user.csv' SEPARATOR '	' QUOTED

        // checkAuthDelete file
    }

}
