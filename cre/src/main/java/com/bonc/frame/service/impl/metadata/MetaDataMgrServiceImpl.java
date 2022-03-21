package com.bonc.frame.service.impl.metadata;

import com.bonc.frame.applicationrunner.StaticDataLoader;
import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.dao.metadata.MetaDataTableMapper;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.dbresource.DbResourceMetaRef;
import com.bonc.frame.entity.metadata.MetaDataColumn;
import com.bonc.frame.entity.metadata.MetaDataScanTask;
import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.module.db.meta.core.IScanService;
import com.bonc.frame.module.db.meta.core.exception.StartTaskFailException;
import com.bonc.frame.module.db.meta.util.Constants;
import com.bonc.frame.module.db.meta.web.bo.imp.*;
import com.bonc.frame.module.vo.TableRelationVo;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.datasource.DataSourceService;
import com.bonc.frame.service.metadata.MetaDataMgrService;
import com.bonc.frame.util.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qxl
 * @version 1.0.0
 * @date 2016年12月7日 上午10:22:11
 */
@Service("metaDataMgrService")
public class MetaDataMgrServiceImpl implements MetaDataMgrService {

    private Log log = LogFactory.getLog(MetaDataMgrServiceImpl.class);

    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private MetaDataTableMapper metaDataTableMapper;

    private final String _METADATA_TABLE_PREFIX = "com.bonc.frame.dao.metadata.MetaDataTableMapper.";
    private final String _METADATA_COLUMN_PREFIX = "com.bonc.frame.mapper.metadata.MetaDataColumnMapper.";
    private final String _METADATA_SCAN_TASK_PREFIX = "com.bonc.frame.mapper.metadata.MetaDataScanTaskMapper.";
    private final String _DB_RESOURCE_META_REF_PREFIX = "com.bonc.frame.mapper.dbresource.DbResourceMetaRefMapper.";

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private StaticDataLoader staticDataLoader;

    @Autowired
    private AuthorityService authorityService;


    @Override
    public List<Map<String, Object>> findMetaTable(String folderId) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("packageId", folderId);
        List<Map<String, Object>> list = metaDataTableMapper.findMetaTable(param);
//        List<Map<String, Object>> list = daoHelper.queryForList(_METADATA_TABLE_PREFIX + "findMetaTable", param);
        return list;
    }

    public ResponseResult deleteMetaTable(String folderId) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("packageId", folderId);
        List<Map<String, Object>> tables = metaDataTableMapper.findMetaTable(param);
        for (Map<String, Object> table : tables) {
            // 如果之后加了权限的话,需要鉴权

            String tableid = (String) table.get("TABLEID");
            ResponseResult responseResult = deleteByPrimaryKey(tableid);
        }
        return ResponseResult.createSuccessInfo();
    }


    @Override
    public Map<String, Object> findMetaTableByPage(String folderId, String start, String size) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("packageId", folderId);

        int pageSize = Integer.parseInt((size == null || "0".equals(size)) ? "5" : size);
        int currentPage = Integer.parseInt((start == null) ? "0" : start) / pageSize + 1;
        PageHelper.startPage(currentPage, pageSize);

        final List<Map<String, Object>> result = metaDataTableMapper.findMetaTableByPage(param);

        PageInfo<Map<String, Object>> page = new PageInfo<>(result);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("recordsTotal", page.getTotal());
        resultMap.put("recordsFiltered", page.getTotal());
        resultMap.put("total", page.getTotal());
        resultMap.put("data", page.getList());
//        final Map<String, Object> result = daoHelper.queryForPageList(_METADATA_TABLE_PREFIX + "findMetaTableByPage", param, start, size);
        return resultMap;
    }

    @Override
    public DataSource selectDbByTableId(String tableId) {
        final DataSource dataSource = (DataSource) daoHelper.queryOne(_METADATA_TABLE_PREFIX + "selectDbByTableId", tableId);
        return dataSource;
    }

    @Override
    public DataSource selectDbByDbId(String dbId) {
        final DataSource dataSource = (DataSource) daoHelper.queryOne(_METADATA_TABLE_PREFIX + "selectDbByDbId", dbId);
        return dataSource;
    }

    @Override
    public Map<String, Object> pagedMetaTablesByDsNameTableNamePackageName(@Nullable String tableName,
                                                                           @Nullable String dbAlias,
                                                                           @Nullable String folderName,
                                                                           String start,
                                                                           String size) {
        Map<String, String> param = new HashMap<>();
        param.put("tableName", tableName);
        param.put("dbAlias", dbAlias);
        param.put("folderName", folderName);

        Map<String, Object> result = daoHelper.queryForPageList(_METADATA_TABLE_PREFIX +
                "pagedMetaTablesByDsNameTableNamePackageName", param, start, size);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult deleteByPrimaryKey(String tableId) {
        daoHelper.delete(_METADATA_TABLE_PREFIX + "deleteByPrimaryKey", tableId);
        daoHelper.delete(_METADATA_COLUMN_PREFIX + "deleteByTableId", tableId);

//		dbResourceMetaRefMapper.updateByResId(tableId);
        //20171128-qxl 添加.当删除元数据表时，需要删除相关的关联关系
        daoHelper.delete(_DB_RESOURCE_META_REF_PREFIX + "deleteByResourceId", tableId);
        daoHelper.delete(_DB_RESOURCE_META_REF_PREFIX + "deleteByTableId", tableId);

        // 级联删除：数据对应的权限
        authorityService.deleteByResourceId(tableId, ResourceType.DATA_METADATA.getType());

        return ResponseResult.createSuccessInfo();
    }

    @Override
    public MetaDataTable selectTableByPrimaryKey(String tableId) {
        final MetaDataTable metaDataTable = (MetaDataTable) daoHelper.queryOne(_METADATA_TABLE_PREFIX + "selectByPrimaryKey", tableId);
        return metaDataTable;
    }

    @Override
    @Transactional
    public ResponseResult updateByPrimaryKeySelective(MetaDataTable metaDataTable) {
        int count = (int) daoHelper.queryOne(_METADATA_TABLE_PREFIX + "selectByName", metaDataTable);
        if (count > 0) {
            return ResponseResult.createFailInfo("表别名重复");
        }
        daoHelper.update(_METADATA_TABLE_PREFIX + "updateByPrimaryKeySelective", metaDataTable);
        return ResponseResult.createSuccessInfo();
    }

    //获取资源管理主页面 ‘数据源+表’ 树
	/*
	 * 数据格式
	 * [{
			id: "rse1",
			name: "数据源连接一",
			open: true,
			isParent: true,
			children: [
				{id: "122",name: "表一"},
				{id: "111",name: "表二"}
			]
		},
		{
			id: "rse2",
			name: "数据源连接二",
			open: true,
			isParent: true,
			children: [
			{id: "100",name: "表一"},
			{id: "101",name: "表二"}
		]}
	 * ];
	 */
    @Override
    public List<Map<String, Object>> getDbTableTree(String packageId) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<String, String> selectParam = new HashMap<String, String>();
        //1.查询所有数据源
        List<Map<String, String>> dbInfoList = daoHelper.queryForList(_METADATA_TABLE_PREFIX + "selectDbIdByPackageId", packageId);
        for (Map<String, String> dbInfo : dbInfoList) {
            Map<String, Object> temp = new HashMap<String, Object>();
            String dbId = dbInfo.get("ID");
            selectParam.put("dbId", dbId);
            selectParam.put("packageId", packageId);
            //2.查询数据源下所有的表
            List<Map<String, String>> tableInfoList = daoHelper.queryForList(_METADATA_TABLE_PREFIX + "selectTablesByDbId", selectParam);

            //将数据存到list中
            temp.putAll(dbInfo);
            temp.put("open", false);
            temp.put("children", tableInfoList);
            result.add(temp);


        }
        if (result != null && result.size() > 0) {
            result.get(0).put("open", true);
        }
        return result;
    }

    //新增表
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult insertTable(MetaDataTable table, String relTable) {
        int count = (int) daoHelper.queryOne(_METADATA_TABLE_PREFIX + "selectByName", table);
        if (count > 0) {
            return ResponseResult.createFailInfo("表别名重复");
        }
        count = (int) daoHelper.queryOne(_METADATA_TABLE_PREFIX + "selectByTableCode", table);
        if (count > 0) {
            return ResponseResult.createFailInfo("表编码重复");
        }

        daoHelper.insert(_METADATA_TABLE_PREFIX + "insertSelective", table);
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

    //修改表
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult updateTable(MetaDataTable table, String relTable) {
        int count = (int) daoHelper.queryOne(_METADATA_TABLE_PREFIX + "selectByName", table);
        if (count > 0) {
            return ResponseResult.createFailInfo("表别名重复");
        }
        count = (int) daoHelper.queryOne(_METADATA_TABLE_PREFIX + "selectByTableCode", table);
        if (count > 0) {
            return ResponseResult.createFailInfo("表编码重复");
        }
        daoHelper.update(_METADATA_TABLE_PREFIX + "updateByPrimaryKeySelective", table);
        if (relTable != null && !relTable.isEmpty()) {
            //关联表的配置
            Object[] relTableArr = JsonUtils.toArray(relTable);
            Map<String, Object> param = new HashMap<String, Object>();
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

    @Override
    public ResponseResult saveCdtConfig(MetaDataTable metaTable) {
        metaTable.setUpdateDate(new Date());
        daoHelper.update(_METADATA_TABLE_PREFIX + "updateByPrimaryKeySelective", metaTable);
        return ResponseResult.createSuccessInfo();
    }

    // --------------------------------------------------------------------------------------

    // 获取扫描任务的状态
    @Override
    public String getScanStatus(String scanId) {
        MetaDataScanTask metaDataScanTask = (MetaDataScanTask) daoHelper.queryOne(_METADATA_SCAN_TASK_PREFIX + "selectByPrimaryKey", scanId);
        return metaDataScanTask.getScanStatus();
    }

    // 扫描
    @Override
    @Transactional(rollbackFor = {SQLException.class, StartTaskFailException.class})
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
            //清空临时表
//			clearTempData(metaDataScanTask.getScanId());
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

    //查看样例数据
    /*public Map<String, Object> viewData(String tableId) throws SqlExecuteException, SQLException {
        TableMetaInfoBOImpl ti = new TableMetaInfoBOImpl();
        Map<String, Object> result = ti.sampleData(tableId);
        return result;
    }*/

    //获取 ‘表名+列名’ 树
    @Override
    public List<Map<String, Object>> getColumnTree(String dbResourceId, String tableId) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<String, String> selectParam = new HashMap<String, String>();
        //1.查询资源下所有的表
        List<Map<String, String>> tableInfoList = daoHelper.queryForList(_DB_RESOURCE_META_REF_PREFIX + "selectByResourceId", dbResourceId);
        for (Map<String, String> tableInfo : tableInfoList) {
            Map<String, Object> temp = new HashMap<String, Object>();
            //2.查询表的所有列
            String tblId = tableInfo.get("TBLID");
            List<Map<String, String>> columnInfoList = daoHelper.queryForList(_METADATA_COLUMN_PREFIX + "selectColumnsByTableId1", tblId);

            //将数据存到list中
            temp.put("id", tblId);
            temp.put("name", tableInfo.get("TBLNAME"));
            temp.put("tableCode", tableInfo.get("TABLECODE"));
            temp.put("isMain", tableInfo.get("ISMAIN"));
            temp.put("isParent", true);
            if (tableId != null && tableId.equals(tblId)) {
                temp.put("open", true);
            } else {
                temp.put("open", false);
            }
            temp.put("children", columnInfoList);
            result.add(temp);
        }
        return result;
    }

    @Override
    //查询列信息
    public Map<String, Object> findColumnByTableId(String tableId, String columnName,
                                                   String start, String size) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("tableId", tableId);
        param.put("columnName", columnName);
        final Map<String, Object> map = daoHelper.queryForPageList(_METADATA_COLUMN_PREFIX + "selectColumnsInfo", param, start, size);
        return map;
    }

    //查询列信息
    @Override
    public List<Map<String, Object>> findColumnByTableId(String tableId, String columnName) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("tableId", tableId);
        param.put("columnName", columnName);
        final List<Map<String, Object>> map = daoHelper.queryForList(_METADATA_COLUMN_PREFIX + "selectColumnsInfo", param);
        return map;
    }

    @Override
    public List<MetaDataColumn> selectAllColumnsByTableId(String tableId) {
        return daoHelper.queryForList(_METADATA_COLUMN_PREFIX + "selectAllColumnsByTableId", tableId);
    }

    @Override
    //根据列Id查询列详细信息
    public MetaDataColumn selectColumnInfo(String columnId) {
        return (MetaDataColumn) daoHelper.queryOne(_METADATA_COLUMN_PREFIX + "selectByPrimaryKey", columnId);
    }

    //删除列
    @Override
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


    //获取表的列 树
    @Override
    public List<Map<String, String>> getSysVarTree(String packageId) {
        List<Map<String, String>> result = daoHelper.queryForList(_METADATA_COLUMN_PREFIX + "getSysVarTree", packageId);
        return result;
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
    @Override
    public List<Map<String, String>> getAllColumnByScanKey(String packageId, String scanKey) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("packageId", packageId);
        param.put("tableCode", scanKey);
        List<Map<String, String>> result = daoHelper.queryForList(_METADATA_COLUMN_PREFIX + "getAllColumnByScanKey", param);
        return result;
    }

    @Override
    public List<Map<String, String>> getAllColumnByScanKeyFromTemp(String packageId, String scanKey) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("packageId", packageId);
        param.put("tableCode", scanKey);
        List<Map<String, String>> result = daoHelper.queryForList(_METADATA_COLUMN_PREFIX + "getAllColumnByScanKeyFromTemp", param);
        return result;
    }

    //根据columnId批量删除column
    @Override
    public ResponseResult deleteColumns(List<String> list) {
        if (list.size() > 0) {
            daoHelper.delete(_METADATA_COLUMN_PREFIX + "deleteColumns", list);
        }
        return ResponseResult.createSuccessInfo();
    }

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
        daoHelper.delete(_METADATA_COLUMN_PREFIX + "deleteMetaTable");
        //导入扫描的元数据表
        daoHelper.insert(_METADATA_COLUMN_PREFIX + "importMetaTable", scanId);
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
    public void clearTempData(String scanId) {
        daoHelper.delete(_METADATA_COLUMN_PREFIX + "deleteMetaColumnTemp", scanId);
        daoHelper.delete(_METADATA_COLUMN_PREFIX + "deleteMetaTableTemp", scanId);
    }

    @Override
    //导入元数据信息
    @Transactional
    public ResponseResult importMetaData(String packageId, String selectedPackageId, String isOverwrite) {
        //处理元数据表----------------------------------
        int success1 = 0, success2 = 0, fail1 = 0, fail2 = 0, delete1 = 0, delete2 = 0;
        //此规则包中已存在的元数据信息
        List<MetaDataTable> metaTablelist = daoHelper.queryForList(_METADATA_TABLE_PREFIX + "selectByPackageId", packageId);
        Map<String, String> existTableMap = new HashMap<String, String>();
        for (MetaDataTable mdt : metaTablelist) {
            existTableMap.put(mdt.getTableId(), mdt.getTableName());
        }
        //查询要导入的规则包里面的所有元数据信息
        List<MetaDataTable> selectedMetaTablelist = daoHelper.queryForList(_METADATA_TABLE_PREFIX + "selectByPackageId", selectedPackageId);
        List<String> newTableIdList = new ArrayList<String>();//新生成TableId
        List<String> oldTableIdList = new ArrayList<String>();//旧的TableId
        List<String> oldRelTableIdList = new ArrayList<String>();//旧的关联表TableId
        //key-oldTableId,value-newTableId
        Map<String, String> tableIdMap = new HashMap<String, String>();
        int number = 1;
        for (MetaDataTable metaTable : selectedMetaTablelist) {
            String oldTableId = metaTable.getTableId();
            oldTableIdList.add(oldTableId);
            if ("3".equals(metaTable.getTableKind())) {//关联表
                oldRelTableIdList.add(oldTableId);
            }
            String newTableId = MD5Util.Bit32Silence(oldTableId + packageId);
            newTableIdList.add(newTableId);
            tableIdMap.put(oldTableId, newTableId);
            //重新生成ID
            metaTable.setTableId(newTableId);
            metaTable.setPackageId(packageId);
            metaTable.setCreateDate(new Date(System.currentTimeMillis() + (number++) * 1000));
            metaTable.setUpdateDate(null);
            metaTable.setUpdatePerson(null);
        }
//		System.out.println("oldTableIdList:"+oldTableIdList);
//		System.out.println("newTableIdList:"+newTableIdList);
        if ("true".equals(isOverwrite)) {//覆盖，则将相同的移除
            //删除已存在的表
            int count = daoHelper.delete(_METADATA_TABLE_PREFIX + "deleteByTableIds", newTableIdList);
            delete1 += count;
            //删除已存在的列
            daoHelper.delete(_METADATA_COLUMN_PREFIX + "deleteByTableIds", newTableIdList);
        }
        for (MetaDataTable metaTable : selectedMetaTablelist) {
            //不覆盖，则移除已存在的，不再重复导入
            if ("false".equals(isOverwrite) && existTableMap.containsKey(metaTable.getTableId())) {
                fail1++;
                continue;
            }
            daoHelper.insert(_METADATA_TABLE_PREFIX + "insertSelective", metaTable);
            success1++;
        }
        //处理元数据列----------------------------
        //查询所有的列信息
        number = 1;
        List<MetaDataColumn> metaDataColumnList = daoHelper.queryForList(_METADATA_COLUMN_PREFIX + "selectByTableIds", oldTableIdList);
        for (MetaDataColumn metaDataColumn : metaDataColumnList) {
            String oldColumnId = metaDataColumn.getColumnId();
            String newColumnId = MD5Util.Bit32Silence(oldColumnId + packageId);
            //重新生成columnId
            metaDataColumn.setColumnId(newColumnId);
            //替换tableId
            String newTableId = tableIdMap.get(metaDataColumn.getTableId());
            metaDataColumn.setTableId(newTableId);
            metaDataColumn.setCreateDate(new Date(System.currentTimeMillis() + (number++) * 1000));
            //不覆盖，则移除已存在的，不再重复导入
            if ("false".equals(isOverwrite) && existTableMap.containsKey(newTableId)) {
                continue;
            }
            daoHelper.insert(_METADATA_COLUMN_PREFIX + "insertSelective", metaDataColumn);
        }
        //处理关联关系---------------------------
        List<String> relationTableIdList = new ArrayList<String>();//新关联表ID
        for (MetaDataTable metaTable : selectedMetaTablelist) {
            if ("3".equals(metaTable.getTableKind())) {//关联表
                relationTableIdList.add(metaTable.getTableId());
            }
        }
        if ("true".equals(isOverwrite)) {//覆盖，则将相同的从库中删除
            //删除已存在的关联关系
            int count = daoHelper.delete(_DB_RESOURCE_META_REF_PREFIX + "deleteByResourceIds", relationTableIdList);
            delete2 += count;
        }
        try {
            for (String oldRelTableId : oldRelTableIdList) {
                List<Map<String, String>> list = daoHelper.queryForList(_DB_RESOURCE_META_REF_PREFIX + "selectTableRelation", oldRelTableId);
                DbResourceMetaRef dbRef = new DbResourceMetaRef();
                for (Map<String, String> map : list) {
                    String dbResourceId = tableIdMap.get(oldRelTableId);
                    dbRef.setDbResourceId(dbResourceId);
                    dbRef.setTableId(tableIdMap.get(map.get("TABLEID")));
                    dbRef.setIsMain(map.get("ISMAIN"));
                    String tableRelation = map.get("TABLERELATION");
                    String newTableRelation = replaceTableRelation(tableRelation, tableIdMap);
                    dbRef.setResourceTableRef(newTableRelation);
                    //不覆盖，则移除已存在的，不再重复导入
                    if ("false".equals(isOverwrite) && existTableMap.containsKey(dbResourceId)) {
                        fail2++;
                        continue;
                    }
                    daoHelper.insert(_DB_RESOURCE_META_REF_PREFIX + "insertSelective", dbRef);
                    success2++;
                }
            }
        } catch (Exception e) {
            log.error(e);
            return ResponseResult.createFailInfo(e.getMessage());
        }
        return ResponseResult.createSuccessInfo("import all success[" + (success1) + "].exist [" + (fail1) + "].delete[" + delete1 + "].");
    }

    private String replaceTableRelation(String tableRelation, Map<String, String> tableIdMap) throws Exception {
        //将关系作转换
        TableRelationVo tableRelationVo = TableRelationVo.parseToTableRelationVo(tableRelation);
        String relCode = tableRelationVo.getCodeOnStr();
        //替换tableId
        Pattern p = Pattern.compile("\\[(.*?)\\]");//(.*?)]
        Matcher m = p.matcher(relCode);
        while (m.find()) {
            String repalceStr = m.group(0);//[xxx]
            String matchStr = m.group(1);//...,[]中间的字符串
            if (matchStr == null || matchStr.isEmpty() || matchStr.trim().isEmpty()) {
                throw new Exception("ERROR:格式错误[列名不能为空].");
            }
            int dotIndex = matchStr.lastIndexOf('.');
            if (dotIndex == -1) {
                throw new Exception(m.group(0) + "应满足不满足[表名.列名]格式");
            }
            String tableId = matchStr.substring(0, dotIndex);
            String columnName = matchStr.substring(dotIndex + 1);
            if (!tableIdMap.containsKey(tableId)) {
                throw new Exception("表ID[" + tableId + "]不存在");
            }
            relCode = relCode.replace(repalceStr, "[" + tableIdMap.get(tableId) + "." + columnName + "]");
        }
        tableRelationVo.setCodeOnStr(relCode);
        return tableRelationVo.parseToXml(tableRelationVo);
    }

}
