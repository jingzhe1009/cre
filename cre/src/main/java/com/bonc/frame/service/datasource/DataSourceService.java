package com.bonc.frame.service.datasource;

import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.util.ResponseResult;

import java.util.List;
import java.util.Map;

/**
 * 数据源配置Controller
 *
 * @author lt
 * @version 1.0.0
 * @date 2016年10月24日 10:03:37
 */
public interface DataSourceService {

    Map<String, Object> pagedDataSource(String dbAlias,
                                        String dbIp,
                                        String dbType,
                                        String start, String size);

    /**
     * 分页查询数据源信息列表
     *
     * @return
     */
    Map<String, Object> findDataSourceByPage(String dbAlias, String tenantId, String start, String size);


    /**
     * 查询所有数据源信息
     *
     * @return
     */
    List<DataSource> findDataSource(String dbAlias, String tenantId);

    /**
     * 新建数据源信息的保存方法
     *
     * @param dataSource
     * @return
     */
    ResponseResult insertDataSource(DataSource dataSource, String userId);

    void insertDBDataPersistence(DataSource dataSource);

    /**
     * 删除数据源信息方法
     *
     * @param dbId
     * @return
     */
    ResponseResult deleteByPrimaryKey(String dbId);

    /**
     * 按主键查询数据源信息方法
     *
     * @param dbId
     * @return
     */
    DataSource selectByPrimaryKey(String dbId);

    List<DataSource> selectDBByProperty(String dbId, String dbAlias, String dbIp);

    List<DataSource> selectDataSourceByDBIdList(List<String> dbIdList);

    /**
     * 修改数据源信息方法
     *
     * @param dataSourceCon
     * @return
     */
    ResponseResult updateByPrimaryKeySelective(DataSource dataSource);


    /**
     * 获取数据源列表
     *
     * @param tenantId
     * @return List<Map   <   String   ,       String>>（数据源id，数据源名）
     */
    List<Map<String, String>> getDataSourceList();

    /**
     * 根据数据源id获取数据源中所有表信息
     *
     * @param dbId
     * @return
     */
    List<Map<String, String>> getDbTables(String dbId) throws Exception;

    /**
     * 获取表中所有列信息
     *
     * @param dbId
     * @param tableName
     * @return
     */
    List<Map<String, String>> getColumns(String dbId, String tableName);

    /**
     * 根据数据源id、表名、key、value来查询相应列来导入数据库码表
     *
     * @param dbId
     * @param tableName
     * @param key
     * @param value
     * @param condition
     * @return
     */
    List<Map<String, String>> getCodeTable(String dbId, String tableName, String key, String value, String condition);


    /**
     * 新建和修改数据源时测试数据库链接
     *
     * @param dataSourceCon
     * @return
     * @throws Exception
     */
    ResponseResult dbConnetionTest(DataSource dataSource) throws Exception;

    /**
     * 执行SQL时的执行方法，创建表、插入数据
     *
     * @param tableName1 源表
     * @param tableName2 目标表
     * @param createFlag 是否创建新表的标识
     * @param dbId       数据连接ID
     * @param ruleId     规则id
     * @param packageId  规则包id
     * @return
     * @throws Exception
     */
    ResponseResult saveExecuteRuleSQL(String tableName1, String tableName2, boolean createFlag,
                                      String dbId, String ruleId, String packageId) throws Exception;

    /**
     * 获取所有的数据库类型
     *
     * @return
     */
    List<Map<String, String>> getDbType();

}
