package com.bonc.frame.service.staticdata;

import com.bonc.frame.entity.staticdata.StaticDataVo;

import java.util.List;

/**
 * @author yedunyao
 * @date 2019/5/15 11:00
 */
public interface StaticDataService {

    // 获取数据源码表
    List<StaticDataVo> getDataSourceCode();

}
