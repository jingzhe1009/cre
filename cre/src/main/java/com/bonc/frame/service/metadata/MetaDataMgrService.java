package com.bonc.frame.service.metadata;

import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.metadata.MetaDataColumn;
import com.bonc.frame.entity.metadata.MetaDataScanTask;
import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.module.db.meta.core.exception.SqlExecuteException;
import com.bonc.frame.util.ResponseResult;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** 
 *
 * @author qxl
 * @date 2016年12月7日 上午10:21:34 
 * @version 1.0.0
 */
public interface MetaDataMgrService {
	
	/**
	 * 查询元数据表信息列表
	 * @param param
	 * @return
	 */
	List<Map<String,Object>> findMetaTable(String folderId);

	ResponseResult deleteMetaTable(String folderId);

	/**
	 * 分页查询元数据表信息列表
	 * @param param
	 * @return
	 */
	Map<String, Object> findMetaTableByPage(String folderId, String start, String size);

	//通过 tableid 连表查询获取 db
	DataSource selectDbByTableId(String tableId);

	// 通过 dbId 获取 db
	DataSource selectDbByDbId(String dbId);

    Map<String, Object> pagedMetaTablesByDsNameTableNamePackageName(String tableName, String dbAlias,
                                                                    String folderName, String start,
                                                                    String size);
	
	/**
	 * 删除元数据表信息方法
	 * @return
	 */
	ResponseResult deleteByPrimaryKey(String tableId);

	/**
	 * 按主键查询元数据表信息方法
	 * @return
	 */
	MetaDataTable selectTableByPrimaryKey(String tableId);

	List<MetaDataColumn> selectAllColumnsByTableId(String tableId);
	
	/**
	 * 修改元数据表信息方法
	 * @param metaDataTable
	 * @return
	 */
	ResponseResult updateByPrimaryKeySelective(MetaDataTable metaDataTable);
	
	/**
	 * 获取扫描任务的状态
	 * @return
	 */
	String getScanStatus(String scanId);

	/**
	 * 扫描
	 * @param metaDataScanTask
	 * @return
	 */
	ResponseResult scan(MetaDataScanTask metaDataScanTask);

	ResponseResult cancel(String scanId);

	/**
	 * 查看样例数据
	 * @param tableId
	 * @return
	 * @throws SQLException 
	 * @throws SqlExecuteException 
	 */
//	Map<String, Object> viewData(String tableId) throws SqlExecuteException, SQLException;

	/**
	 * 获取资源管理主页面 ‘数据源+表’ 树
	 * @param packageId
	 * @return
	 */
	List<Map<String, Object>> getDbTableTree(String packageId);

	/**
	 * 获取 ‘表名+列名’ 树
	 * @param dbResourceId
	 * @param tableId
	 * @return
	 */
	List<Map<String, Object>> getColumnTree(String dbResourceId, String tableId);

	/**
	 * 根据表的Id查询所有的列信息
	 * @return
	 */
	Map<String, Object> findColumnByTableId(String tableId, String columnName,
											String start, String size);

	List<Map<String, Object>> findColumnByTableId(String tableId, String columnName);

	/**
	 * 根据列Id查询列详细信息
	 * @param columnId
	 * @return
	 */
	MetaDataColumn selectColumnInfo(String columnId);

	/**
	 * 删除列
	 * @param columnId
	 * @return
	 */
	ResponseResult deleteColumn(String columnId);

	/**
	 * 新增列
	 * @param column
	 * @return
	 */
	ResponseResult insertColumn(MetaDataColumn column);

	/**
	 * 修改列
	 * @param column
	 * @return
	 */
	ResponseResult updateColumn(MetaDataColumn column);

	/**
	 * 新增表
	 * @param table
	 * @param relTable 
	 * @return
	 */
	ResponseResult insertTable(MetaDataTable table, String relTable);

	/**
	 * 修改表
	 * @param table
	 * @param relTable 
	 * @return
	 */
	ResponseResult updateTable(MetaDataTable table, String relTable);

	/**
	 * 获取表的列 树
	 * @param packageId
	 * @return
	 */
	List<Map<String, String>> getSysVarTree(String packageId);

	/**
	 * 导入列
	 * @param tableId
	 * @param typeId
	 * @param columns
	 * @return
	 */
	ResponseResult importColumn(String tableId, String typeId, String columns);

	/**
	 * 根据扫描关键字(表编码)，查询该表 所有的列信息
	 * @param packageId
	 * @param scanKey
	 * @return
	 */
	List<Map<String, String>> getAllColumnByScanKey(String packageId, String scanKey);

	List<Map<String, String>> getAllColumnByScanKeyFromTemp(String packageId, String scanKey);

	/**
	 * 根据columnId批量删除column
	 * @param list
	 * @return
	 */
	ResponseResult deleteColumns(List<String> list);

	/**
	 * 保存扫描后的结果
	 * @param columnIds
	 * @return
	 */
	ResponseResult save(String scanId, String columnIds);

	ResponseResult saveCdtConfig(MetaDataTable metaTable);

	/**
	 * 导入元数据信息
	 * @param packageId
	 * @param selectedPackageId
	 * @param isOverwrite
	 * @return
	 * @throws Exception 
	 */
	ResponseResult importMetaData(String packageId, String selectedPackageId, String isOverwrite) throws Exception;

}
