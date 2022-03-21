package com.bonc.frame.applicationrunner;

import com.bonc.frame.entity.staticdata.StaticDataVo;
import com.bonc.frame.service.staticdata.StaticDataService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yedunyao
 * @date 2019/5/15 10:47
 */
@Component("staticDataLoader")
@Order(2)
public class StaticDataLoader implements CommandLineRunner {

    private static Log log = LogFactory.getLog(StaticDataLoader.class);

    /**
     * 数据库类型
     */
    public static final String DATASOURCE_CODE = "datasource_code";

    @Autowired
    private StaticDataService staticDataService;

    @Autowired
    private StaticDataCache staticDataCache;

    @Override
    public void run(String... args) throws Exception {
        // 加载静态数据
        log.info("加载数据库中的静态数据");
        final List<StaticDataVo> dataSourceCode = staticDataService.getDataSourceCode();
        staticDataCache.put(DATASOURCE_CODE, dataSourceCode);
        staticDataCache.toImmutable();
    }

    public String getDatasourceCodeValue(String dataKey) {
        return staticDataCache.getDataValueByKey(DATASOURCE_CODE, dataKey);
    }

    public StaticDataVo getDatasourceCodeVo(String dataKey) {
        return staticDataCache.getStaticDataVoByKey(DATASOURCE_CODE, dataKey);
    }

    public List<StaticDataVo> getDatasourceCodeVos() {
        return staticDataCache.getStaticDataVos(DATASOURCE_CODE);
    }
}
