package com.bonc.frame.service.impl.dbresource;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.dbresource.DbResourceMetaRef;
import com.bonc.frame.entity.metadata.RelationTable;
import com.bonc.frame.module.antlr.util.SqlAntlrUtil;
import com.bonc.frame.module.db.operator.AbstractDbOperator;
import com.bonc.frame.module.vo.TableRelationVo;
import com.bonc.frame.service.dbresource.DbResourceService;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Clob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qxl
 * @version 1.0.0
 * @date 2016年12月13日 上午10:11:11
 */
@Service("dbResourceService")
public class DbResourceServiceImpl implements DbResourceService {

    Log log = LogFactory.getLog(getClass());

    @Autowired
    private DaoHelper daoHelper;

    private final String _DB_RESOURCE_META_REF_PREFIX = "com.bonc.frame.mapper.dbresource.DbResourceMetaRefMapper.";
    private final String _METADATA_COLUMN_PREFIX = "com.bonc.frame.mapper.metadata.MetaDataColumnMapper.";

    /**
     * 获取关系表的所有子表
     *
     * @param dbResourceId
     * @return
     */
    @Override
    public List<Map<String, String>> selectByResourceId(String dbResourceId) {
        List<Map<String, String>> tableList = daoHelper.queryForList(_DB_RESOURCE_META_REF_PREFIX + "selectByResourceId", dbResourceId);
        return tableList;
    }

    @Override
    @Transactional
    //给资源添加表
    public ResponseResult addTableToDbResource(String dbResourceId, String tableId) {
        DbResourceMetaRef dbResourceMetaRef = new DbResourceMetaRef();
        dbResourceMetaRef.setDbResourceId(dbResourceId);
        dbResourceMetaRef.setTableId(tableId);
        dbResourceMetaRef.setIsMain("0");
        int count = (int) daoHelper.queryOne(_DB_RESOURCE_META_REF_PREFIX + "selectByResIdDbId", dbResourceMetaRef);
        if (count > 0) {
            return ResponseResult.createFailInfo("添加失败，已存在资源中.");
        }
        int rows = daoHelper.insert(_DB_RESOURCE_META_REF_PREFIX + "insertSelective", dbResourceMetaRef);
        if (rows == 1) {
            return ResponseResult.createSuccessInfo();
        }
        return ResponseResult.createFailInfo("添加失败");
    }

    @Override
    @Transactional
    //删除资源中的表
    public ResponseResult deleteTableFromDbResource(String dbResourceId, String tableId) {
        DbResourceMetaRef dbResourceMetaRef = new DbResourceMetaRef();
        dbResourceMetaRef.setDbResourceId(dbResourceId);
        dbResourceMetaRef.setTableId(tableId);
        int rows = daoHelper.delete(_DB_RESOURCE_META_REF_PREFIX + "deleteTableFromDbSource", dbResourceMetaRef);
        if (rows == 1) {
            return ResponseResult.createSuccessInfo();
        }
        return ResponseResult.createFailInfo("删除失败");
    }

    @Override
    public List<RelationTable> selectRelationTable(String dbResourceId) {
        return daoHelper.queryForList(_DB_RESOURCE_META_REF_PREFIX + "selectRelationTable", dbResourceId);
    }

    //获取绑定关系主页面中表的关系
    @Override
    public List<Map<String, Object>> getTableRelation(String dbResourceId) throws Exception {
        List<Map<String, Object>> result = daoHelper.queryForList(_DB_RESOURCE_META_REF_PREFIX +
                "selectTableRelation", dbResourceId);
        boolean mainTableFlag = false;
        for (Map<String, Object> map : result) {
            if (map.containsKey("ISMAIN") && map.get("ISMAIN") != null && map.get("ISMAIN").equals("1")) {
                mainTableFlag = true;
            } else {
                map.put("ISMAIN", "0");
            }

            final String tablerelation = AbstractDbOperator.ClobToString((Clob) map.get("TABLERELATION"));

            //将关系作转换
            TableRelationVo tableRelationVo = TableRelationVo.parseToTableRelationVo(tablerelation);
            map.put("JOIN", tableRelationVo.getJoinStr());
            map.put("RELATION", tableRelationVo.getCodeOnStr());
            map.remove("TABLERELATION");
        }
        if (!mainTableFlag && result != null && result.size() > 0) {//没有主表，默认将第一个设为主表
            DbResourceMetaRef dbResourceMetaRef = new DbResourceMetaRef();
            dbResourceMetaRef.setDbResourceId(result.get(0).get("DBRESOURCEID").toString());
            dbResourceMetaRef.setTableId(result.get(0).get("TABLEID").toString());
            dbResourceMetaRef.setIsMain("1");
            daoHelper.update(_DB_RESOURCE_META_REF_PREFIX + "updateByResIdTableId", dbResourceMetaRef);
            result.get(0).put("ISMAIN", "1");
        }
        return result;
    }

    //设为主表
    @Override
    @Transactional
    public ResponseResult setMainTable(String dbResourceId, String tableId) {
        DbResourceMetaRef dbResourceMetaRef = new DbResourceMetaRef();
        dbResourceMetaRef.setDbResourceId(dbResourceId);
        dbResourceMetaRef.setTableId(tableId);
        dbResourceMetaRef.setIsMain("1");
        //根据资源id将资源下的所有表设为非主表
        daoHelper.update(_DB_RESOURCE_META_REF_PREFIX + "updateByResId", dbResourceId);
        //将指定的表设为主表
        daoHelper.update(_DB_RESOURCE_META_REF_PREFIX + "updateByResIdTableId", dbResourceMetaRef);
        return ResponseResult.createSuccessInfo();
    }

    //保存绑定的关系
    @Override
    @Transactional
    public ResponseResult saveTableRelation(List<Map> dataList, String dbResourceId) {
        DbResourceMetaRef dbResourceMetaRef = new DbResourceMetaRef();
        dbResourceMetaRef.setDbResourceId(dbResourceId);
        for (Map map : dataList) {
            String codeOnStr = map.get("relation") == null ? null : map.get("relation").toString();
//            String codeOnStr = null;
            ResponseResult checkResult = null;
            if (codeOnStr != null && !codeOnStr.isEmpty()) {
                checkResult = check(dbResourceId, codeOnStr);
//                if (checkResult.getStatus() == ResponseResult.SUCCESS_STAUS) {
//                    codeOnStr = checkResult.getData() == null ? null : checkResult.getData().toString();
//                } else {
//                    return checkResult;
//                }
            }

            dbResourceMetaRef.setIsMain(String.valueOf(map.get("isMain")));
            dbResourceMetaRef.setTableId(String.valueOf(map.get("tableId")));
            //将关联关系和连接转换成xml
            TableRelationVo tableRelation = new TableRelationVo();
            tableRelation.setCodeOnStr(codeOnStr);
//            tableRelation.setCodeOnStr(codeOnStr);
            tableRelation.setJoinStr(String.valueOf(map.get("join")));
            String xml = TableRelationVo.parseToXml(tableRelation);
            dbResourceMetaRef.setResourceTableRef(xml);
            daoHelper.update(_DB_RESOURCE_META_REF_PREFIX + "updateByResIdTableId", dbResourceMetaRef);
        }

        return ResponseResult.createSuccessInfo();
    }

    //校验绑定的表与表之间的关系是否合法
    @Override
    @Transactional
    public ResponseResult checkTableRelation(String dbResourceId, String data) {
        if (data == null || data.isEmpty()) {
            return ResponseResult.createFailInfo("表之间的关系不能为空！");
        }
        return check(dbResourceId, data);
    }

    private ResponseResult check(String dbResourceId, String data) {
        if (log.isDebugEnabled()) {
            log.debug("#DbResourceServiceImpl###dbResourceId:" + dbResourceId + "--data:" + data);
        }
        //1.查询资源下所有的表，并封装成map
        Map<String, String> tableMap = new HashMap<String, String>();
        List<Map<String, String>> tableList = daoHelper.queryForList(_DB_RESOURCE_META_REF_PREFIX + "selectByResourceId", dbResourceId);
        for (Map<String, String> map : tableList) {
            tableMap.put(map.get("TABLECODE"), map.get("TABLECODE"));
        }
        //2.查询资源下所有的表的列信息，并封装成map
        Map<String, String> columnMap = new HashMap<String, String>();
        List<Map<String, String>> columnList = daoHelper.queryForList(_METADATA_COLUMN_PREFIX + "selectByResourceId", dbResourceId);
        for (Map<String, String> map : columnList) {
            columnMap.put(map.get("TABLECODE") + "." + map.get("COLUMNCODE"), map.get("TABLECODE") + "." + map.get("COLUMNCODE"));
        }
        //3.将表名和列名替换成code
        Pattern p = Pattern.compile("\\[(.*?)\\]");//(.*?)]
        Matcher m = p.matcher(data);
        while (m.find()) {
            String repalceStr = m.group(0);//[xxx]
            String matchStr = m.group(1);//...,[]中间的字符串
            if (matchStr == null || matchStr.isEmpty() || matchStr.trim().isEmpty()) {
                return ResponseResult.createFailInfo("ERROR:格式错误【列名不能为空】.");
            }
            int dotIndex = matchStr.lastIndexOf('.');
            if (dotIndex == -1) {
                return ResponseResult.createFailInfo(m.group(0) + "应满足不满足[表名.列名]格式");
            }
            String tableName = matchStr.substring(0, dotIndex);
            String columnName = matchStr.substring(dotIndex + 1);
            if (tableName.isEmpty() || columnName.isEmpty()) {
                return ResponseResult.createFailInfo(m.group(0) + "应满足不满足[表名.列名]格式");
            }
            if (!tableMap.containsKey(tableName)) {
                return ResponseResult.createFailInfo("表：[" + tableName + "]不存在");
            }
            if (!columnMap.containsKey(matchStr)) {
                return ResponseResult.createFailInfo("列[" + matchStr + "]不存在");
            }
//            data = data.replace(repalceStr, "[" + columnMap.get(matchStr) + "]");
        }

//		4.使用Antlr进行语法校验
        String validateMsg = SqlAntlrUtil.validateSqlCdt(data);
        if (validateMsg != null && !validateMsg.isEmpty()) {
            log.error("##[Data]" + data + ",##[ValidateMsg]" + validateMsg);
            return ResponseResult.createFailInfo(validateMsg);
        }
        return ResponseResult.createSuccessInfo("success", data);
    }

    //校验虚拟列
    @Override
    public ResponseResult validateVirtualColumn(String dbResourceId, String content) {
        if (log.isDebugEnabled()) {
            log.debug("#DbResourceServiceImpl###dbResourceId:" + dbResourceId + "--content:" + content);
        }
        if (content == null || content.isEmpty()) {//查询条件为空，默认为select *
            return ResponseResult.createFailInfo("虚拟列的内容不能为空");
        }
        //1.查询资源下所有的表，并封装成map
        Map<String, String> tableMap = new HashMap<String, String>();
        List<Map<String, String>> tableList = daoHelper.queryForList(_DB_RESOURCE_META_REF_PREFIX + "selectByResourceId", dbResourceId);
        for (Map<String, String> map : tableList) {
            tableMap.put(map.get("TABLECODE"), map.get("TABLECODE"));
        }
        //2.查询资源下所有的表的列信息，并封装成map
        Map<String, String> columnMap = new HashMap<String, String>();
        List<Map<String, String>> columnList = daoHelper.queryForList(_METADATA_COLUMN_PREFIX +
                "selectByResourceId", dbResourceId);
        for (Map<String, String> map : columnList) {
            columnMap.put(map.get("COLUMNCODE"), map.get("COLUMNCODE"));
        }

        StringBuilder sb = new StringBuilder();//存储替换成code后的字符串
        //3.校验-把name替换成code
        //以","来分割字符串，获取列名
        Pattern p = Pattern.compile("\\[(.*?)\\]");//(.*?)]
        Matcher m = p.matcher(content);
        int count = 0;
        while (m.find()) {
            count++;
            String nameStr = m.group(1);//...,[]中间的字符串
            StringBuffer tempCodeStr = new StringBuffer();
            int dotIndex = nameStr.indexOf(".");
            if (dotIndex != -1) {//为 "表名.列名" 形式
                String tableName = nameStr.substring(0, dotIndex);
                String columnName = nameStr.substring(dotIndex + 1);
                if (tableName.isEmpty() || columnName.isEmpty()) {
                    return ResponseResult.createFailInfo("[" + nameStr + "]格式错误");
                }
                if (!tableMap.containsKey(tableName)) {
                    return ResponseResult.createFailInfo("表：[" + tableName + "]不存在");
                }
                if (!columnMap.containsKey(columnName)) {
                    return ResponseResult.createFailInfo("表[" + tableName + "]的列：[" + columnName + "]不存在");
                }
                tempCodeStr.append(tableMap.get(tableName)).append(".").append(columnMap.get(columnName));
            } else {//不符合格式要求
                return ResponseResult.createFailInfo("[" + nameStr + "]不存在");
            }
            content = content.replace(nameStr, tempCodeStr);
        }
        if (!content.trim().startsWith("[")) {
            //使用Antlr进行语法校验
            String validateMsg = SqlAntlrUtil.validateSqlColumn(content);
            if (validateMsg != null && !validateMsg.isEmpty()) {
                log.error("##[content]" + content + ",##[ValidateMsg]" + validateMsg);
                return ResponseResult.createFailInfo(validateMsg);
            }
        } else if (count > 1) {
            return ResponseResult.createFailInfo("ERROR：格式错误.");
        }
        content = content.replace("[", "").replace("]", "");
        sb.append(content);
        return ResponseResult.createSuccessInfo("success", sb.toString());
    }

    @Override
    //根据关联表id查询其关联关系
    public List<Map<String, String>> findRelateByTableId(String id) {
        List<Map<String, String>> result = daoHelper.queryForList(_DB_RESOURCE_META_REF_PREFIX + "dbResourceMetaRefMapper", id);
        return result;
    }
	
    /**
     * 获取sql统计函数
     */
    public List<Map<String, Object>> getCountFunc() {
        List<Map<String, Object>> list = new ArrayList<>();

        String[] countFunc = {"count", "abs", "ceil", "floor", "max", "min", "avg", "sum", "group by", "random", "sort"};
        String[] countFuncName = {"统计数量", "绝对值", "取整(大)", "取整(小)", "最大值", "最小值", "平均值", "求和", "分组", "随机数", "排序"};
        for (int i = 0; i < countFunc.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", countFunc[i]);
            map.put("pid", "statisticFun");
            map.put("name", countFuncName[i]);
            map.put("isParent", "false");
            list.add(map);
        }
        return list;
    }

}
