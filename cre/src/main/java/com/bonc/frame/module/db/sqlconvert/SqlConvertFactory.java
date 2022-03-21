package com.bonc.frame.module.db.sqlconvert;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qxl
 * @version 1.0
 * @date 2017年9月8日 上午9:39:35
 */
public class SqlConvertFactory {

    private static Map<String, ISqlConvert> sqlConvertMap;

    static {
        sqlConvertMap = new HashMap<String, ISqlConvert>();
        sqlConvertMap.put("1", new MySQLConvert());//1-MYSQL
        sqlConvertMap.put("2", new OracleConvert());//2-ORACLE
        sqlConvertMap.put("5", new HiveConvert());//5-HIVE
        sqlConvertMap.put("6", new HiveConvert());//6-HIVE2
        sqlConvertMap.put("7", new XCloudConvert());//6-HIVE2
    }

    /**
     * 根据数据库类型ID获取sqlConvert
     *
     * @param dbTypeId 对应brm_db_connect_str表的DB_TYPE列
     * @return ISqlConvert
     */
    public static ISqlConvert getSqlConvertByDbType(String dbTypeId) {
        if (StringUtils.isBlank(dbTypeId) || !sqlConvertMap.containsKey(dbTypeId)) {
            return null;
        }
        return sqlConvertMap.get(dbTypeId);
    }

}
