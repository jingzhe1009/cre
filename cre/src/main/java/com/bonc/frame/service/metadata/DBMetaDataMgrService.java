package com.bonc.frame.service.metadata;

import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.metadata.MetaDataColumn;
import com.bonc.frame.entity.metadata.MetaDataScanTask;
import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.util.ResponseResult;

import java.util.List;
import java.util.Map;

/**
 * @author qxl
 * @version 1.0.0
 * @date 2016年12月7日 上午10:21:34
 */
public interface DBMetaDataMgrService {

    /**
     * 查询元数据表信息列表
     *
     * @param dbId
     * @return
     */
    List<Map<String, Object>> findMetaTableByDbId(String dbId);

    /**
     * 分页查询元数据表信息列表
     *
     * @param dbId
     * @return
     */
    Map<String, Object> pagedFindMetaTableByDbId(String dbId, String start, String size);

    DataSource selectDbByTableId(String tableId);

    /**
     * 删除元数据表信息方法
     *
     * @return
     */
    ResponseResult deleteByPrimaryKey(String tableId);

    /**
     * 按主键查询元数据表信息方法
     *
     * @return
     */
    MetaDataTable selectTableByPrimaryKey(String tableId);

    List<MetaDataTable> selectTableByTableProperty(String tableId, String tableName, String tableCode);

    List<MetaDataTable> selectTablesByTableIdList(List<String> tableId);

    List<MetaDataColumn> selectAllColumnsByTableId(String tableId);

    /**
     * 修改元数据表信息方法
     *
     * @param metaDataTable
     * @return
     */
    ResponseResult updateByPrimaryKeySelective(MetaDataTable metaDataTable);

    /**
     * 获取扫描任务的状态
     *
     * @return
     */
    String getScanStatus(String scanId);

    /**
     * 扫描
     *
     * @param metaDataScanTask
     * @return
     */
    ResponseResult scan(MetaDataScanTask metaDataScanTask);

    List<Map<String, String>> getAllColumnByScanIdFromTemp(String scanId);

    ResponseResult cancel(String scanId);

    /**
     * 根据表的Id查询所有的列信息
     *
     * @return
     */
    Map<String, Object> findColumnByTableId(String tableId, String columnName,
                                            String start, String size);

    /**
     * 根据列Id查询列详细信息
     *
     * @param columnId
     * @return
     */
    MetaDataColumn selectColumnInfo(String columnId);

    List<MetaDataColumn> selectColumnInfoByProperty(String columnId, String columnName, String columnCode);

    List<MetaDataColumn> selectColumnInfoBatch(List<String> columnIdList);

    /**
     * 删除列
     *
     * @param columnId
     * @return
     */
    ResponseResult deleteColumn(String columnId);

    /**
     * 新增列
     *
     * @param column
     * @return
     */
    ResponseResult insertColumn(MetaDataColumn column);

    /**
     * 修改列
     *
     * @param column
     * @return
     */
    ResponseResult updateColumn(MetaDataColumn column);

    /**
     * 新增表
     *
     * @param table
     * @param relTable
     * @return
     */
    ResponseResult insertTable(MetaDataTable table, String relTable);

    void insertTablePersistence(MetaDataTable table);

    /**
     * 修改表
     *
     * @param table
     * @param relTable
     * @return
     */
    ResponseResult updateTable(MetaDataTable table, String relTable);

    /**
     * 导入列
     *
     * @param tableId
     * @param typeId
     * @param columns
     * @return
     */
    ResponseResult importColumn(String tableId, String typeId, String columns);

    /**
     * 根据columnId批量删除column
     *
     * @param list
     * @return
     */
    ResponseResult deleteColumns(List<String> list);

    /**
     * 保存扫描后的结果
     *
     * @param columnIds
     * @return
     */
    ResponseResult save(String scanId, String columnIds);

}
