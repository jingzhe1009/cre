package com.bonc.frame.service.dbresource;


import com.bonc.frame.entity.metadata.RelationTable;
import com.bonc.frame.util.ResponseResult;

import java.util.List;
import java.util.Map;

/**
 * 数据库资源Service接口
 *
 * @author qxl
 * @version 1.0.0
 * @date 2016年12月13日 上午10:10:20
 */
public interface DbResourceService {

    /**
     * 获取关系表的所有子表
     *
     * @param dbResourceId
     * @return
     */
    List<Map<String, String>> selectByResourceId(String dbResourceId);

    /**
     * 给资源添加表
     *
     * @param dbResourceId
     * @param tableId
     * @return
     */
    ResponseResult addTableToDbResource(String dbResourceId, String tableId);

    /**
     * 删除资源中的表
     *
     * @param dbResourceId
     * @param tableId
     * @return
     */
    ResponseResult deleteTableFromDbResource(String dbResourceId, String tableId);


    /**
     * 获取绑定关系主页面中表的关系
     *
     * @param dbResourceId
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> getTableRelation(String dbResourceId) throws Exception;

    /**
     * 设为主表
     *
     * @param dbResourceId
     * @param tableId
     * @return
     */
    ResponseResult setMainTable(String dbResourceId, String tableId);

    /**
     * 保存绑定的关系
     *
     * @param dataList
     * @return
     */
    ResponseResult saveTableRelation(List<Map> dataList, String dbResourceId);

    /**
     * 校验绑定的表与表之间的关系是否合法
     *
     * @param dbResourceId
     * @param data
     * @return
     */
    ResponseResult checkTableRelation(String dbResourceId, String data);

    /**
     * 校验虚拟列
     *
     * @param dbResourceId
     * @param content
     * @return
     */
    ResponseResult validateVirtualColumn(String dbResourceId, String content);

    /**
     * 根据关联表id查询其关联关系
     *
     * @param tableId
     * @return
     */
    List<Map<String, String>> findRelateByTableId(String tableId);

    List<RelationTable> selectRelationTable(String dbResourceId);

}
