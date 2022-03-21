package com.bonc.frame.controller.dbresource;

import com.bonc.frame.service.dbresource.DbResourceService;
import com.bonc.frame.service.metadata.MetaDataMgrService;
import com.bonc.frame.util.ResponseResult;
import com.bonc.framework.util.JsonUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 数据库资源Controller
 *
 * @author qxl
 * @version 1.0.0
 * @date 2016年12月13日 上午10:07:42
 */
@Controller
@RequestMapping("/dbresource")
public class DbResourceController {

    @Resource
    private DbResourceService dbResourceService;

    @Resource
    private MetaDataMgrService metaDataMgrService;

    @RequestMapping("/getTables")
    @ResponseBody
    public List<Map<String, String>> selectByResourceId(String dbResourceId) {
        return dbResourceService.selectByResourceId(dbResourceId);
    }

    //获取资源管理主页面 ‘数据源+表’ 树
    @RequestMapping("/getDbTableTree")
    @ResponseBody
    public List<Map<String, Object>> getDbTableTree(String packageId) {
        List<Map<String, Object>> result = metaDataMgrService.getDbTableTree(packageId);
        return result;
    }
	
    //获取绑定关系主页面中表的关系
    @RequestMapping("/getTableRelation")
    @ResponseBody
    public List<Map<String, Object>> getTableRelation(String dbResourceId) throws Exception {
        List<Map<String, Object>> result = dbResourceService.getTableRelation(dbResourceId);
        return result;
    }

    //设为主表
    @RequestMapping("/setMainTable")
    @ResponseBody
    public ResponseResult setMainTable(String dbResourceId, String tableId) {
        ResponseResult result = dbResourceService.setMainTable(dbResourceId, tableId);
        return result;
    }

    /**
     * 保存绑定的关系
     *
     * @param data         json数组
     * @param dbResourceId 资源id
     * @return
     */
    @RequestMapping("/saveTableRelation")
    @ResponseBody
    public ResponseResult saveTableRelation(String data, String dbResourceId) {
//		System.out.println(data+"-"+dbResourceId);
        List<Map> dataList = JsonUtils.toList(data, Map.class);
        ResponseResult result = dbResourceService.saveTableRelation(dataList, dbResourceId);
        return result;
    }

    //获取 ‘表名+列名’ 树
    @RequestMapping("/getColumnTree")
    @ResponseBody
    public List<Map<String, Object>> getColumnTree(String dbResourceId, String tableId) {
        List<Map<String, Object>> result = metaDataMgrService.getColumnTree(dbResourceId, tableId);
        return result;
    }

    /**
     * 校验绑定的表与表之间的关系是否合法
     *
     * @param dbResourceId 资源id
     * @param data         要校验的数据
     * @return
     */
    @RequestMapping("/checkTableRelation")
    @ResponseBody
    public ResponseResult checkTableRelation(String dbResourceId, String data) {
        ResponseResult result = dbResourceService.checkTableRelation(dbResourceId, data);
        return result;
    }


    //校验虚拟列
    @RequestMapping("/validateVirtualColumn")
    @ResponseBody
    public ResponseResult validateVirtualColumn(String dbResourceId, String content) {
        ResponseResult result = dbResourceService.validateVirtualColumn(dbResourceId, content);
        return result;
    }
}
