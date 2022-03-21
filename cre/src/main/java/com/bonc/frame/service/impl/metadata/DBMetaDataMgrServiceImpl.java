package com.bonc.frame.service.impl.metadata;

import com.bonc.frame.applicationrunner.StaticDataLoader;
import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.dbresource.DbResourceMetaRef;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.kpi.KpiFetchLimiters;
import com.bonc.frame.entity.metadata.MetaDataColumn;
import com.bonc.frame.entity.metadata.MetaDataScanTask;
import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.module.db.meta.core.IScanService;
import com.bonc.frame.module.db.meta.core.exception.StartTaskFailException;
import com.bonc.frame.module.db.meta.util.Constants;
import com.bonc.frame.module.db.meta.web.bo.imp.*;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.datasource.DataSourceService;
import com.bonc.frame.service.metadata.DBMetaDataMgrService;
import com.bonc.frame.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

/**
 * 数据源下的元数据管理
 *
 * @author qxl
 * @version 1.0.0
 * @date 2016年12月7日 上午10:22:11
 */
@Service("dbMetaDataMgrService")
public class DBMetaDataMgrServiceImpl implements DBMetaDataMgrService {

    private Log log = LogFactory.getLog(DBMetaDataMgrServiceImpl.class);

    @Autowired
    private DaoHelper daoHelper;

//    @Autowired
//    private MetaDataTableMapper metaDataTableMapper;

    private final String _METADATA_DB_TABLE_PREFIX = "com.bonc.frame.dao.metadata.DBMetaDataTableMapper.";
    private final String _METADATA_COLUMN_PREFIX = "com.bonc.frame.mapper.metadata.MetaDataColumnMapper.";
    private final String _METADATA_SCAN_TASK_PREFIX = "com.bonc.frame.mapper.metadata.MetaDataScanTaskMapper.";
    /**
     * 关联关系表
     */
    private final String _DB_RESOURCE_META_REF_PREFIX = "com.bonc.frame.mapper.dbresource.DbResourceMetaRefMapper.";

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private StaticDataLoader staticDataLoader;

    @Autowired
    private AuthorityService authorityService;


    @Override
    public List<Map<String, Object>> findMetaTableByDbId(String dbId) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("dbId", dbId);
        List<Map<String, Object>> list = daoHelper.queryForList(_METADATA_DB_TABLE_PREFIX +
                "findMetaTableByDbId", param);
        return list;
    }

    @Override
    public Map<String, Object> pagedFindMetaTableByDbId(String dbId, String start, String size) {
        Map<String, String> param = new HashMap<>();
        param.put("dbId", dbId);
        final Map<String, Object> result = daoHelper.queryForPageList(_METADATA_DB_TABLE_PREFIX +
                "findMetaTableByDbId", param, start, size);
        return result;
    }

    @Override
    public DataSource selectDbByTableId(String tableId) {
        final DataSource dataSource = (DataSource) daoHelper.queryOne(_METADATA_DB_TABLE_PREFIX +
                "selectDbByTableId", tableId);
        return dataSource;
    }

    // TODO: 元数据使用的校验
    public boolean isDBMetaTableUsed(String tableId) {
        int count = (int) daoHelper.queryOne(_METADATA_DB_TABLE_PREFIX + "countDBMetaTableUsed", tableId);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult deleteByPrimaryKey(String tableId) {
        daoHelper.delete(_METADATA_DB_TABLE_PREFIX + "deleteByPrimaryKey", tableId);
        // 级联删除：表中的字段
        daoHelper.delete(_METADATA_COLUMN_PREFIX + "deleteByTableId", tableId);

//		dbResourceMetaRefMapper.updateByResId(tableId);

        // 级联删除：关联关系表
//        daoHelper.delete(_DB_RESOURCE_META_REF_PREFIX + "deleteByResourceId", tableId);
//        daoHelper.delete(_DB_RESOURCE_META_REF_PREFIX + "deleteByTableId", tableId);

        // 级联删除：数据对应的权限
//        authorityService.deleteByResourceId(tableId, ResourceType.DATA_METADATA.getType());

        return ResponseResult.createSuccessInfo();
    }

    @Override
    public MetaDataTable selectTableByPrimaryKey(String tableId) {
        final MetaDataTable metaDataTable = (MetaDataTable) daoHelper.queryOne(_METADATA_DB_TABLE_PREFIX +
                "selectByPrimaryKey", tableId);
        return metaDataTable;
    }

    @Override
    public List<MetaDataTable> selectTableByTableProperty(String tableId, String tableName, String tableCode) {
        if (StringUtils.isBlank(tableId) && StringUtils.isBlank(tableName) && StringUtils.isBlank(tableCode)) {
            return Collections.emptyList();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("tableId", tableId);
        params.put("tableName", tableName);
        params.put("tableCode", tableCode);
        final List<MetaDataTable> metaDataTable = daoHelper.queryForList(_METADATA_DB_TABLE_PREFIX +
                "selectTableByTableProperty", params);
        return metaDataTable;
    }

    @Override
    public List<MetaDataTable> selectTablesByTableIdList(List<String> tableIdList) {
        if (CollectionUtil.isEmpty(tableIdList)) {
            return Collections.emptyList();
        }
        Map<String, Object> param = new HashMap<>(1);
        param.put("tableIdList", tableIdList);
        final List<MetaDataTable> metaDataTable = daoHelper.queryForList(_METADATA_DB_TABLE_PREFIX +
                "selectTablesByTableIdList", param);
        return metaDataTable;
    }

    private boolean checkNameExists(MetaDataTable metaDataTable) {
        int count = (int) daoHelper.queryOne(_METADATA_DB_TABLE_PREFIX + "selectByName", metaDataTable);
        if (count > 0) {
            return true;
        }
        return false;
    }

    private boolean checkDbIdCodeExists(MetaDataTable metaDataTable) {
        int count = (int) daoHelper.queryOne(_METADATA_DB_TABLE_PREFIX + "selectByTableCodeAndDbId", metaDataTable);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public ResponseResult updateByPrimaryKeySelective(MetaDataTable metaDataTable) {
        if (checkNameExists(metaDataTable)) {
            return ResponseResult.createFailInfo("表别名重复");
        }
        if (checkDbIdCodeExists(metaDataTable)) {
            return ResponseResult.createFailInfo("表编码重复");
        }
        daoHelper.update(_METADATA_DB_TABLE_PREFIX + "updateByPrimaryKeySelective", metaDataTable);
        return ResponseResult.createSuccessInfo();
    }


    //新增表
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult insertTable(MetaDataTable table, String relTable) {
        if (checkNameExists(table)) {
            return ResponseResult.createFailInfo("表别名重复");
        }
        if (checkDbIdCodeExists(table)) {
            return ResponseResult.createFailInfo("表编码重复");
        }

        daoHelper.insert(_METADATA_DB_TABLE_PREFIX + "insertSelective", table);
        if (relTable != null && !relTable.isEmpty()) {
            //关联表的配置
            Object[] relTableArr = JsonUtils.toArray(relTable);
            DbResourceMetaRef metaTableRelate = new DbResourceMetaRef();
            metaTableRelate.setDbResourceId(table.getTableId());
            for (Object obj : relTableArr) {
                metaTableRelate.setTableId((String) obj);
                daoHelper.insert(_DB_RESOURCE_META_REF_PREFIX + "insertSelective", metaTableRelate);
            }
        }
        return ResponseResult.createSuccessInfo();
    }

    public void insertTablePersistence(MetaDataTable table) {
        if (table != null) {
            String currentUser = ControllerUtil.getCurrentUser();
            table.setCreateDate(new Date());
            table.setCreatePerson(currentUser);
            daoHelper.insert(_METADATA_DB_TABLE_PREFIX + "insertSelective", table);
        }
    }

    //修改表
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult updateTable(MetaDataTable table, String relTable) {
        if (checkNameExists(table)) {
            return ResponseResult.createFailInfo("表别名重复");
        }
        if (checkDbIdCodeExists(table)) {
            return ResponseResult.createFailInfo("表编码重复");
        }
        daoHelper.update(_METADATA_DB_TABLE_PREFIX + "updateByPrimaryKeySelective", table);
        if (relTable != null && !relTable.isEmpty()) {
            //关联表的配置
            Object[] relTableArr = JsonUtils.toArray(relTable);
            Map<String, Object> param = new HashMap<>();
            param.put("id", table.getTableId());
            param.put("ids", relTableArr);
            daoHelper.delete(_DB_RESOURCE_META_REF_PREFIX + "deleteOldRelate", param);
            DbResourceMetaRef metaTableRelate = new DbResourceMetaRef();
            metaTableRelate.setDbResourceId(table.getTableId());
            for (Object obj : relTableArr) {
                metaTableRelate.setTableId((String) obj);
                int row = (int) daoHelper.queryOne(_DB_RESOURCE_META_REF_PREFIX + "selectByResIdDbId", metaTableRelate);
                if (row == 0) {//库中不存在，新添加
                    daoHelper.insert(_DB_RESOURCE_META_REF_PREFIX + "insertSelective", metaTableRelate);
                }
            }
        }
        return ResponseResult.createSuccessInfo();
    }


    // --------------------------------------------------------------------------------------

    /**
     * 获取扫描任务的状态
     *
     * @param scanId
     * @return
     */
    @Override
    public String getScanStatus(String scanId) {
        MetaDataScanTask metaDataScanTask = (MetaDataScanTask) daoHelper.queryOne(
                _METADATA_SCAN_TASK_PREFIX + "selectByPrimaryKey", scanId);
        return metaDataScanTask.getScanStatus();
    }

    /**
     * 扫描
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ResponseResult scan(MetaDataScanTask metaDataScanTask) {
        metaDataScanTask.setScanId(IdUtil.createId());
        metaDataScanTask.setScanStatus(String.valueOf(Constants.Task_Status_Ready));
        metaDataScanTask.setStartDate(new Date());

        daoHelper.insert(_METADATA_SCAN_TASK_PREFIX + "insertSelective", metaDataScanTask);

        final DataSource dataSource = dataSourceService.selectByPrimaryKey(metaDataScanTask.getDbId());
        try {
            IScanService scanService = null;
            String dbTypeName = staticDataLoader.getDatasourceCodeValue(dataSource.getDbType());
            if ("HIVE".equals(dbTypeName) || "HIVE2".equals(dbTypeName)) {
                scanService = new HiveTableService(MetaConvertUtil.convertToScanTask(metaDataScanTask),
                        MetaConvertUtil.convertToDatabaseResource(dataSource), new TableMetaInfoBOImpl());
            } else if ("HBASE".equals(dbTypeName)) {
                scanService = new HbaseTableService(MetaConvertUtil.convertToScanTask(metaDataScanTask),
                        MetaConvertUtil.convertToDatabaseResource(dataSource), new TableMetaInfoBOImpl());
            } else if ("ORACLE".equals(dbTypeName)) {
                scanService = new OracleTableService(MetaConvertUtil.convertToScanTask(metaDataScanTask),
                        MetaConvertUtil.convertToDatabaseResource(dataSource), new TableMetaInfoBOImpl());
            } else if ("XCLOUD".equals(dbTypeName)) {
                scanService = new XCloudTableService(MetaConvertUtil.convertToScanTask(metaDataScanTask),
                        MetaConvertUtil.convertToDatabaseResource(dataSource), new TableMetaInfoBOImpl());
            } else {
                throw new UnsupportedOperationException("暂不支持扫描的数据源类型：" + dbTypeName);
            }
            scanService.startScan();
        } catch (SQLException e) {
            log.error(e);
            return ResponseResult.createFailInfo("扫描失败，请检查数据库连接和表名");
        } catch (StartTaskFailException e) {
            log.error(e);
            return ResponseResult.createFailInfo("正在扫描中，请稍等");
        } catch (Exception e) {
            log.error(e);
            return ResponseResult.createFailInfo("扫描失败", e.getLocalizedMessage());
        }
        return ResponseResult.createSuccessInfo("success", metaDataScanTask.getScanId());
    }

    @Override
    public List<Map<String, String>> getAllColumnByScanIdFromTemp(String scanId) {
        Map<String, String> param = new HashMap<>();
        param.put("scanId", scanId);
        List<Map<String, String>> result = daoHelper.queryForList(_METADATA_COLUMN_PREFIX +
                "getAllColumnByScanIdFromTemp", param);
        return result;
    }

    //查询列信息
    @Override
    public Map<String, Object> findColumnByTableId(String tableId, String columnName,
                                                   String start, String size) {
        Map<String, String> param = new HashMap<>();
        param.put("tableId", tableId);
        param.put("columnName", columnName);
        final Map<String, Object> map = daoHelper.queryForPageList(_METADATA_COLUMN_PREFIX + "selectColumnsInfo", param, start, size);
        return map;
    }

    @Override
    public List<MetaDataColumn> selectAllColumnsByTableId(String tableId) {
        return daoHelper.queryForList(_METADATA_COLUMN_PREFIX + "selectAllColumnsByTableId", tableId);
    }

    //根据列Id查询列详细信息
    @Override
    public MetaDataColumn selectColumnInfo(String columnId) {
        return (MetaDataColumn) daoHelper.queryOne(_METADATA_COLUMN_PREFIX + "selectByPrimaryKey", columnId);
    }

    @Override
    public List<MetaDataColumn> selectColumnInfoByProperty(String columnId, String columnName, String columnCode) {
        if (StringUtils.isBlank(columnId) && StringUtils.isBlank(columnName) && StringUtils.isBlank(columnCode)) {
            return Collections.emptyList();
        }
        Map<String, Object> param = new HashMap<>();
        param.put("columnId", columnId);
        param.put("columnName", columnName);
        param.put("columnCode", columnCode);
        List<MetaDataColumn> result = daoHelper.queryForList(_METADATA_COLUMN_PREFIX + "selectColumnInfoByProperty", param);
        return result;
    }

    //根据列Id查询列详细信息
    @Override
    public List<MetaDataColumn> selectColumnInfoBatch(List<String> columnIdList) {
        if (CollectionUtil.isEmpty(columnIdList)) {
            return Collections.emptyList();
        }
        Map<String, Object> param = new HashMap<>(1);
        param.put("columnIdList", columnIdList);

        return daoHelper.queryForList(_METADATA_COLUMN_PREFIX + "selectColumnInfoBatch", param);
    }

    //删除列
    @Override
    @Transactional
    public ResponseResult deleteColumn(String columnId) {
        int row = daoHelper.delete(_METADATA_COLUMN_PREFIX + "deleteByPrimaryKey", columnId);
        if (row == 1) {
            return ResponseResult.createSuccessInfo();
        }
        return ResponseResult.createFailInfo("删除失败");
    }

    //新增列
    @Override
    public ResponseResult insertColumn(MetaDataColumn column) {
        daoHelper.insert(_METADATA_COLUMN_PREFIX + "insertSelective", column);
        return ResponseResult.createSuccessInfo();
    }

    //修改列
    @Override
    public ResponseResult updateColumn(MetaDataColumn column) {
        daoHelper.update(_METADATA_COLUMN_PREFIX + "updateByPrimaryKeySelective", column);
        return ResponseResult.createSuccessInfo();
    }


    //导入列
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult importColumn(String tableId, String typeId, String columns) {
        List<String> columnIdList = JsonUtils.toList(columns, String.class);
        List<MetaDataColumn> columnInfos = daoHelper.queryForList(_METADATA_COLUMN_PREFIX + "selectColumnsInfos", columnIdList);
        for (MetaDataColumn metaDataColumn : columnInfos) {
            metaDataColumn.setColumnId(IdUtil.createId());
            metaDataColumn.setTableId(tableId);
            metaDataColumn.setColumnType(typeId);
            //覆盖已存在的
            daoHelper.delete(_METADATA_COLUMN_PREFIX + "deleteByCondition", metaDataColumn);
            //插入
            daoHelper.insert(_METADATA_COLUMN_PREFIX + "insertSelective", metaDataColumn);
        }
        return ResponseResult.createSuccessInfo();
    }

    //根据扫描关键字(表编码)，查询该表 所有的列信息
    /*@Override
    public List<Map<String, String>> getAllColumnByScanKey(String packageId, String scanKey) {
        Map<String, String> param = new HashMap<>();
        param.put("packageId", packageId);
        param.put("tableCode", scanKey);
        List<Map<String, String>> result = daoHelper.queryForList(_METADATA_COLUMN_PREFIX + "getAllColumnByScanKey", param);
        return result;
    }*/

    /*@Override
    public List<Map<String, String>> getAllColumnByScanKeyFromTemp(String packageId, String scanKey) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("packageId", packageId);
        param.put("tableCode", scanKey);
        List<Map<String, String>> result = daoHelper.queryForList(_METADATA_COLUMN_PREFIX + "getAllColumnByScanKeyFromTemp", param);
        return result;
    }*/

    //根据columnId批量删除column
    @Override
    public ResponseResult deleteColumns(List<String> list) {
        if (list.size() > 0) {
            daoHelper.delete(_METADATA_COLUMN_PREFIX + "deleteColumns", list);
        }
        return ResponseResult.createSuccessInfo();
    }

    /**
     * 扫描的表以dbId + tableName + folderId作为主键存在临时表中，
     * 如果扫入的表与已经保存的表id重复，则替换旧表，
     * 通过再次扫入的方式可以实现表的更新；
     * 如果扫入的表与已经保存的表id不重复，则新增表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult save(String scanId, String columnIds) {
        if (columnIds == null || columnIds.isEmpty()) {
            clearTempData(scanId);
            return ResponseResult.createSuccessInfo();
        }
        List<String> list = JsonUtils.toList(columnIds, String.class);
        daoHelper.delete(_METADATA_COLUMN_PREFIX + "deleteColumns", list);
        //删除已存在的元数据表
        daoHelper.delete(_METADATA_DB_TABLE_PREFIX + "deleteDBMetaTable");
        //导入扫描的元数据表
        daoHelper.insert(_METADATA_DB_TABLE_PREFIX + "importDBMetaTable", scanId);
        //导入选中的 列
        daoHelper.insert(_METADATA_COLUMN_PREFIX + "importMetaColumn", list);
        clearTempData(scanId);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult cancel(String scanId) {
        clearTempData(scanId);
        return ResponseResult.createSuccessInfo();
    }

    //清空临时表
    private void clearTempData(String scanId) {
        daoHelper.delete(_METADATA_COLUMN_PREFIX + "deleteMetaColumnTemp", scanId);
        daoHelper.delete(_METADATA_COLUMN_PREFIX + "deleteMetaTableTemp", scanId);
    }

}
