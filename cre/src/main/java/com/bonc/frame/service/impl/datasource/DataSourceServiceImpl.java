package com.bonc.frame.service.impl.datasource;

import com.bonc.frame.applicationrunner.StaticDataLoader;
import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.auth.resource.DataSourceResource;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.staticdata.StaticDataVo;
import com.bonc.frame.module.db.DbInfoUtil;
import com.bonc.frame.module.db.DbOperatorFactory;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.datasource.DataSourceService;
import com.bonc.frame.util.CollectionUtil;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.ResponseResult;
import com.bonc.framework.util.FrameLogUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 数据源配置service
 *
 * @author lt
 * @version 1.0.0
 * @date 2016年10月24日 10:03:37
 */
@Service("dataSourceService")
public class DataSourceServiceImpl implements DataSourceService {

    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private StaticDataLoader staticDataLoader;

    @Autowired
    private DbInfoUtil dbInfoUtil;

    @Autowired
    private AuthorityService authorityService;

    private final String _MYBITSID_PREFIX = "com.bonc.frame.dao.datasource.DataSourceMapper.";

    @Override
    public Map<String, Object> pagedDataSource(String dbAlias,
                                               String dbIp,
                                               String dbType,
                                               String start, String size) {
        Map<String, Object> param = new HashMap<>(3);
        param.put("dbAlias", dbAlias);
        param.put("dbIp", dbIp);
        param.put("dbType", dbType);
        Map<String, Object> result = daoHelper.queryForPageList(_MYBITSID_PREFIX + "pagedDataSource",
                param, start, size);
        for (DataSourceResource dataSource : (List<DataSourceResource>) result.get("data")) {
            String dbTypeName = staticDataLoader.getDatasourceCodeValue(dataSource.getDbType());
            if (StringUtils.isBlank(dbTypeName)) {
                dataSource.setDbTypeName("--");
            }
            dataSource.setDbTypeName(dbTypeName);
        }
        return result;
    }

    @Override
    public Map<String, Object> findDataSourceByPage(String dbAlias, String userId, String start, String size) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("dbAlias", dbAlias);
        Map<String, Object> result = daoHelper.queryForPageList(_MYBITSID_PREFIX + "findDataSourceByPage",
                param, start, size);
        for (DataSource dataSource : (List<DataSource>) result.get("data")) {
            String dbTypeName = staticDataLoader.getDatasourceCodeValue(dataSource.getDbType());
            if (StringUtils.isBlank(dbTypeName)) {
                dataSource.setDbTypeName("--");
            }
            dataSource.setDbTypeName(dbTypeName);
        }
        return result;
    }

    @Override
    public List<DataSource> findDataSource(String dbAlias, String userId) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("dbAlias", dbAlias);
        List<DataSource> result = daoHelper.queryForList(_MYBITSID_PREFIX + "findDataSourceByPage",
                param);
        for (DataSource dataSource : (List<DataSource>) result) {
            String dbTypeName = staticDataLoader.getDatasourceCodeValue(dataSource.getDbType());
            if (StringUtils.isBlank(dbTypeName)) {
                dataSource.setDbTypeName("--");
            }
            dataSource.setDbTypeName(dbTypeName);
        }
        return result;
    }

    public boolean isExists(String dbAlias) {
        int count = (int) daoHelper.queryOne(_MYBITSID_PREFIX + "isExistsByDBAlias", dbAlias);
        return count == 0 ? false : true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult insertDataSource(DataSource dataSource, String userId) {
        if (isExists(dataSource.getDbAlias())) {
            return ResponseResult.createFailInfo("连接别名已存在");
        }
//        daoHelper.insert(_MYBITSID_PREFIX + "insertSelective", dataSource);
//         自动授权：插入用户对数据的权限
//        authorityService.autoGrantAuthToCurrentUser(dataSource.getDbId(), ResourceType.DATA_DATASOURCE);

        insertDBDataPersistence(dataSource);

      /*  final Authority authority = new Authority();
        authority.setResourceId(dataSource.getDbId());
        authority.setResourceExpression("*");
        authorityService.grantToUser(authority, ResourceType.DATA_DATASOURCE.getType(), userId);*/
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertDBDataPersistence(DataSource dataSource) {
        if (dataSource != null) {
            String currentUser = ControllerUtil.getCurrentUser();
            dataSource.setCreateDate(new Date());
            dataSource.setCreatePerson(currentUser);
            daoHelper.insert(_MYBITSID_PREFIX + "insertSelective", dataSource);
            // 自动授权：插入用户对数据的权限
            authorityService.autoGrantAuthToCurrentUser(dataSource.getDbId(), ResourceType.DATA_DATASOURCE);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult deleteByPrimaryKey(String dbId) {
        // TODO: 删除数据源时是否级联删除表、字段
        daoHelper.delete(_MYBITSID_PREFIX + "deleteByPrimaryKey", dbId);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    public DataSource selectByPrimaryKey(String dbId) {
        final DataSource dataSource = (DataSource) daoHelper.queryOne(_MYBITSID_PREFIX + "selectByPrimaryKey", dbId);
        return dataSource;
    }

    @Override
    public List<DataSource> selectDBByProperty(String dbId, String dbAlias, String dbIp) {
        Map<String, Object> param = new HashMap<>();
        param.put("dbId", dbId);
        param.put("dbAlias", dbAlias);
        param.put("dbIp", dbIp);
        return daoHelper.queryForList(_MYBITSID_PREFIX + "selectDBByProperty", param);
    }

    @Override
    public List<DataSource> selectDataSourceByDBIdList(List<String> dbIdList) {
        if (CollectionUtil.isEmpty(dbIdList)) {
            return Collections.emptyList();
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("dbIdList", dbIdList);
        List<DataSource> result = daoHelper.queryForList(_MYBITSID_PREFIX + "selectDataSourceByDBIdList",
                param);
        return result;
    }

    @Override
    @Transactional
    public ResponseResult updateByPrimaryKeySelective(DataSource dataSource) {
        daoHelper.update(_MYBITSID_PREFIX + "updateByPrimaryKeySelective", dataSource);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    //获取数据源列表
    public List<Map<String, String>> getDataSourceList() {
        return daoHelper.queryForList(_MYBITSID_PREFIX + "getDataSourceList");
    }

    @Override
    //根据数据源id获取数据源中所有表信息
    public List<Map<String, String>> getDbTables(String dbId) throws Exception {
        DataSource dbInfo = selectByPrimaryKey(dbId);
        if (dbInfo == null) {
            return null;
        }

        List<Map<String, String>> tables = null;
        try {
            //tables = DbInfoUtil.getAllTables(dbInfo);
            tables = DbOperatorFactory.getAllTables(dbId, dbInfo.getDbType());
        } catch (Exception e) {
            FrameLogUtil.error(getClass(), e.getMessage(), e);
            throw e;
        }
        return tables;
    }

    @Override
    //获取表中所有列信息
    public List<Map<String, String>> getColumns(String dbId, String tableName) {
        DataSource dbInfo = selectByPrimaryKey(dbId);
        List<Map<String, String>> result = null;
        try {
            result = dbInfoUtil.getTableColumnsInfo(dbInfo, tableName);
        } catch (Exception e) {
            FrameLogUtil.error(getClass(), e.getMessage(), e);
        }
        return result;
    }

    @Override
    //根据数据源id、表名、key、value来查询相应列来导入数据库码表
    public List<Map<String, String>> getCodeTable(String dbId, String tableName, String key, String value,
                                                  String condition) {
        String sql = "SELECT " + key + " AS \"key\"," + value + " AS \"value\" FROM " + tableName + " WHERE 1=1";
//		System.out.println(sql);
        if (condition != null && !condition.isEmpty()) {
            sql += " AND " + condition;
        }
        DataSource dbInfo = selectByPrimaryKey(dbId);
        List<Map<String, String>> result = null;
        try {
            result = dbInfoUtil.executeSQL(dbInfo, sql);
        } catch (Exception e) {
            FrameLogUtil.error(getClass(), e.getMessage(), e);
        }
        return result;
    }

    @Override
    @Transactional
    public ResponseResult dbConnetionTest(DataSource dataSourceCon) {
        boolean flag = false;
        ResponseResult result = null;
        try {
            flag = dbInfoUtil.testConnection(dataSourceCon);
        } catch (Exception e) {
            return ResponseResult.createFailInfo("连接测试异常！" + (e == null ? "" : e.getMessage()));
        }
        if (true == flag) {
            result = ResponseResult.createSuccessInfo();
        } else {
            result = ResponseResult.createFailInfo("连接失败！");
        }
        return result;
    }

    @Override
    @Transactional
    public ResponseResult saveExecuteRuleSQL(String tableName1, String tableName2, boolean createFlag,
                                             String dbId, String ruleId, String packageId) throws Exception {
        ResponseResult result = null;
       /* DataSource dataSource = selectByPrimaryKey(dbId);
        if (dataSource == null) {
            result = ResponseResult.createFailInfo("该数据源已经不存在");
        } else {
            String driver = StaticDataUtil.getStaticDataVoByKey(StaticDataUtil.DB_TYPE,
                    dataSource.getDbType().toString()).getRemarks1();//根据数据库类型获取驱动
            String url = DbInfoUtil.createUrl(dataSource);//获取url
            boolean isTable1Exist = true;//源表
            boolean isTable2Exist = true;//新表
            try {
                isTable1Exist = DbInfoUtil.isExistTable(driver, url, dataSource.getDbUsername(),
                        dataSource.getDbPassword(), tableName1);
                isTable2Exist = DbInfoUtil.isExistTable(driver, url, dataSource.getDbUsername(),
                        dataSource.getDbPassword(), tableName2);
            } catch (Exception e) {
                e.printStackTrace();
                result = ResponseResult.createFailInfo("连接数据库失败");
            }
            //源表不存在的情况
            if (!isTable1Exist) {
                result = ResponseResult.createFailInfo("源表名不存在");
            } else
                //不创建表并且该表不存在的情况
                if (createFlag == false && !isTable2Exist) {
                    result = ResponseResult.createFailInfo("该目标表不存在");
                } else
                    //创建表并且该表已经存在的情况
                    if (createFlag == true && isTable2Exist) {
                        result = ResponseResult.createFailInfo("该目标表已经存在");
                    } else {
                        Connection conn = null;
                        try {
                            //获取驱动链接
                            conn = DbInfoUtil.getConnections(driver, url, dataSource.getDbUsername(),
                                    dataSource.getDbPassword());
                        } catch (Exception e) {
                            e.printStackTrace();
                            result = ResponseResult.createFailInfo("连接数据库失败");
                        }
                        try {
                            //执行创建表
                            String createSql = "create table " + tableName2 + " as select * from " + tableName1;
                            PreparedStatement stat = conn.prepareStatement(createSql);
                            stat.execute();
                            stat.close();//执行
                            result = ResponseResult.createSuccessInfo();
                        } catch (Exception e) {
                            e.printStackTrace();
                            result = ResponseResult.createFailInfo("创建目标表失败");
                        }
                        try {
                            //查询sql条件语句，再拼成完整sql语句返回到前台
                            String sqls = RuleEngine.getInstance().compile().queryRuleResourceById(packageId, ruleId).getString();
                            String ruleSQL = TreeRuleUtil.handleSql(sqls, tableName1);
                            //执行插入
                            String insertSql = "insert into " + tableName2 + " ( select * from (" + ruleSQL + ")as temp )";
                            PreparedStatement stat = conn.prepareStatement(insertSql);
                            stat.execute();//执行
                            stat.close();
                            result = ResponseResult.createSuccessInfo();
                        } catch (Exception e) {
                            e.printStackTrace();
                            //模拟回滚
                            String dropSql = "drop table " + tableName2;
                            PreparedStatement stat = conn.prepareStatement(dropSql);
                            stat.execute();
                            stat.close();//执行
                            result = ResponseResult.createFailInfo("执行SQL失败");
                        }
                        conn.close();
                    }
        }*/
        return result;
    }

    @Override
    //获取所有的数据库类型
    public List<Map<String, String>> getDbType() {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        List<StaticDataVo> list = staticDataLoader.getDatasourceCodeVos();
        for (StaticDataVo vo : list) {
            Map<String, String> map = new HashMap<String, String>();
            String dbType = vo.getKey();
            String dbName = vo.getValue();
            map.put("dbType", dbType);
            map.put("dbName", dbName);
            result.add(map);
        }
        return result;
    }

}
