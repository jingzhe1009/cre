package com.bonc.frame.module.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author qxl
 * @version 1.0
 * @date 2017年4月14日 下午4:06:37
 */
public class ResourceParamMapper implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2024498741990487972L;
    private Log log = LogFactory.getLog(this.getClass());

    /**
     * 查询资源下表之间的关系
     *
     * @param dbResourceId
     * @return
     * @throws SQLException
     */
    public List<Map<String, Object>> selectTableRelation(String dbResourceId) throws SQLException {
        String sql = " select a.DB_RESOURCE_ID as 'dbResourceId',a.TABLE_ID as 'tableId', "
                + " a.IS_MAIN as 'isMain',a.RESOURCE_TABLE_REF as 'tableRelation', "
                + " b.TABLE_NAME as 'tableName',b.TABLE_CODE as 'tableCode', "
                + " b.CDT_CONFIG as 'cdtConfig',b.TABLE_KIND as 'tableKind' "
                + " from brm_resources_meta_ref a,brm_meta_table b "
                + " where a.TABLE_ID = b.TABLE_ID AND a.DB_RESOURCE_ID = ? "
                + " order by a.IS_MAIN desc ";
        log.info("execute sql : " + sql);
        return DbHelper.query4MapList(sql, dbResourceId);
    }

    public Map<String, Object> selectTableMap(String tableId) throws SQLException {
        String sql = "select a.table_code as 'tableCode' from   brm_meta_table a where a.table_id = ?";
        return DbHelper.query4Map(sql, tableId);
    }

    /**
     * 根据sql和tableId找到table所属的用户或者库
     * sql 由实现类组成,可针对不同数据库
     */
    public Map<String, Object> selectDataSourceMap(String sql, String... tableId) throws SQLException {
        return DbHelper.query4Map(sql, tableId);
    }

    /**
     * 根据sql和条件,返回list
     * sql 由实现类组成,可针对不同数据库
     */
    public List<Map<String, Object>> selectList(String sql, Object... param) throws SQLException {
        return DbHelper.query4MapList(sql, param);
    }

}
