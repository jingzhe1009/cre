package com.bonc.frame.service.impl.staticdata;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.staticdata.StaticDataVo;
import com.bonc.frame.service.staticdata.StaticDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author yedunyao
 * @date 2019/5/15 11:00
 */
@Service
public class StaticDataServiceImpl implements StaticDataService {

    @Autowired
    private DaoHelper daoHelper;

    private final String _MYBITSID_PREFIX = "com.bonc.frame.mapper.staticdata.StaticDataMapper.";

    // 获取数据源码表
    @Override
    @Transactional
    public List<StaticDataVo> getDataSourceCode() {
        return daoHelper.queryForList(_MYBITSID_PREFIX + "dbType");
    }

}
