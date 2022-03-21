package com.bonc.frame.module.task.outputTask.file;

import com.bonc.frame.module.task.outputTask.AbstractOutputTaskHandler;

import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/7/12 15:27
 */
public class CsvHandler extends AbstractOutputTaskHandler<Map<String, Object>, String> {

    @Override
    public String batchSave(List<Map<String, Object>> batches) throws Exception {
        return null;
    }

}
