package com.bonc.frame.controller.datasource;

import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.module.db.dbcp.DbPoolConstant;
import com.bonc.frame.module.db.dbcp.DbPoolFactory;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissionsRequires;
import com.bonc.frame.service.datasource.DataSourceService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.JsonUtils;
import com.bonc.frame.util.ResponseResult;
import com.bonc.framework.util.IdUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/5/15 9:29
 */
@Controller
@RequestMapping("/datasource")
public class DatasourceController {

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private VariableService variableService;

//    @PermissionsRequires(value = "/datasource", resourceType = ResourceType.MENU)
    @RequestMapping("/view")
    public String view(String idx, String childOpen, Model model, HttpServletRequest request) {
        model.addAttribute("idx", idx);//菜单状态标识
        model.addAttribute("childOpen", childOpen);
        List<Map<String, Object>> baseVariableType = variableService.getVariableType();
        model.addAttribute("baseVariableType",
                JsonUtils.beanToJson(baseVariableType).toString());
        return "/pages/datasource/datasource";
    }

    /**
     * 分页查询数据源配置信息
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> findDataSourceByPage(HttpServletRequest request) {
        String dbAlias = ControllerUtil.getParam(request, "dbAlias");
        String tenantId = ControllerUtil.getLoginUserId(request);
        String start = request.getParameter("start");
        String size = request.getParameter("length");
        return dataSourceService.findDataSourceByPage(dbAlias, tenantId, start, size);
    }

    /**
     * 查询所有数据源配置信息
     */
    @RequestMapping("/listAll")
    @ResponseBody
    public ResponseResult findDataSource(HttpServletRequest request) {
        String dbAlias = ControllerUtil.getParam(request, "dbAlias");
        String tenantId = ControllerUtil.getLoginUserId(request);
        List<DataSource> dataSource = dataSourceService.findDataSource(dbAlias, tenantId);
        return ResponseResult.createSuccessInfo("", dataSource);
    }

    /**
     * 新建和修改数据源时测试数据库链接
     *
     * @param dataSource
     * @return
     * @throws Exception
     */
    @RequestMapping("/dbConnetionTest")
    @ResponseBody
    public ResponseResult dbConnetionTest(DataSource dataSource) throws Exception {
        ResponseResult result = dataSourceService.dbConnetionTest(dataSource);
        return result;
    }

    /**
     * 新建数据源信息
     *
     * @param dataSource
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/insert")
    @ResponseBody
    public ResponseResult insertDataSource(DataSource dataSource, HttpServletRequest request) throws Exception {
        String userId = ControllerUtil.getLoginUserId(request);
        dataSource.setDbId(IdUtil.createId());
        dataSource.setCreatePerson(ControllerUtil.getLoginUserId(request));
        dataSource.setCreateDate(new Date());
        dataSource.setUserId(ControllerUtil.getLoginUserId(request));
        ResponseResult result = dataSourceService.insertDataSource(dataSource, userId);
        return result;
    }

    /**
     * 删除数据源信息
     */
    @PermissionsRequires(value = "/datasource/delete?dbId", resourceType = ResourceType.DATA_DATASOURCE)
    @RequestMapping("/delete")
    @ResponseBody
    public ResponseResult deleteByPrimaryKey(String dbId) throws Exception {
        ResponseResult result = dataSourceService.deleteByPrimaryKey(dbId);
        return result;
    }

    /**
     * 按主键查询数据源信息
     *
     * @param dbId
     * @return
     */
    @PermissionsRequires(value = "/datasource/view?dbId", resourceType = ResourceType.DATA_DATASOURCE)
    @RequestMapping("/selectByPrimaryKey")
    @ResponseBody
    public DataSource selectByPrimaryKey(String dbId) throws Exception {
        DataSource dataSource = dataSourceService.selectByPrimaryKey(dbId);
        return dataSource;
    }

    /**
     * 修改数据源信息的方法
     *
     * @param dataSource
     * @param request
     * @return
     */
    @PermissionsRequires(value = "/datasource/update?dataSource.dbId", resourceType = ResourceType.DATA_DATASOURCE)
    @RequestMapping("/update")
    @ResponseBody
    public ResponseResult updateByPrimaryKeySelective(DataSource dataSource, HttpServletRequest request) throws Exception {
        dataSource.setUpdatePerson(ControllerUtil.getLoginUserId(request));
        dataSource.setUpdateDate(new Date());
        ResponseResult result = dataSourceService.updateByPrimaryKeySelective(dataSource);
        return result;
    }

    /**
     * 启用 /关闭连接池
     *
     * @param dbId
     * @param status
     * @return
     * @throws Exception
     */
    @RequestMapping("/updateIsPool")
    @ResponseBody
    public ResponseResult updateIsPool(String dbId, String status) throws Exception {
        final DataSource dataSource = selectByPrimaryKey(dbId);
        if ("8".equals(dataSource.getDbType())) {   // hbase 不创建连接池
            if (DbPoolConstant.IS_USE.equals(status)) {
                DbPoolFactory.getHbaseDbPool().createDbPool(dbId);
            } else if (DbPoolConstant.IS_NOT_USE.equals(status)) {
                DbPoolFactory.getHbaseDbPool().destroyDbPool(dbId);
            } else {
                throw new Exception("the status is illegal.status[" + status + "].");
            }
//            return ResponseResult.createFailInfo("hbase 暂不支持创建连接池");
        } else {
            if (DbPoolConstant.IS_USE.equals(status)) {
                DbPoolFactory.getDbPool().createDbPool(dbId);
            } else if (DbPoolConstant.IS_NOT_USE.equals(status)) {
                DbPoolFactory.getDbPool().destroyDbPool(dbId);
            } else {
                throw new Exception("the status is illegal.status[" + status + "].");
            }
        }
        return ResponseResult.createSuccessInfo();
    }

    /**
     * 获取所有的数据库类型
     *
     * @return
     */
    @RequestMapping("/getDbType")
    @ResponseBody
    public List<Map<String, String>> getDbType() {
        List<Map<String, String>> result = dataSourceService.getDbType();
        return result;
    }

    @RequestMapping("/dataSourceList")
    @ResponseBody
    //获取数据源列表
    public List<Map<String, String>> getDataSourceList() {
        List<Map<String, String>> list = dataSourceService.getDataSourceList();
        return list;
    }

    @RequestMapping("/getDbTables")
    @ResponseBody
    //根据数据源id获取数据源中所有表信息
    public List<Map<String, String>> getDbTables(String dbId) throws Exception {
        List<Map<String, String>> list = dataSourceService.getDbTables(dbId);
        return list;
    }

    @RequestMapping("/getColumns")
    @ResponseBody
    //根据数据源id和表名获取数据源中表的所有列信息
    public List<Map<String, String>> getColumns(String dbId, String tableName) throws Exception {
        List<Map<String, String>> list = dataSourceService.getColumns(dbId, tableName);
        return list;
    }

    @RequestMapping("/getCodeTable")
    @ResponseBody
    //根据数据源id、表名、key、value来查询相应列来导入数据库码表
    public List<Map<String, String>> getCodeTable(String dbId, String tableName, String key, String value, String condition) throws Exception {
        List<Map<String, String>> list = dataSourceService.getCodeTable(dbId, tableName, key, value, condition);
        for (Map<String, String> map : list) {
            if (map.get("key").length() > 32) {
                throw new Exception("数据不符合要求,请重新选择key值列");
            } else if (map.get("value").length() > 32) {
                throw new Exception("数据不符合要求,请重新选择value值列");
            }
        }
        return list;
    }

    // ------------------------------- 权限校验 -------------------------------

    @PermissionsRequires(value = "/datasource/update?dbId", resourceType = ResourceType.DATA_DATASOURCE)
    @RequestMapping(value = "/update/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult update(String dbId) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/datasource/delete?dbId", resourceType = ResourceType.DATA_DATASOURCE)
    @RequestMapping(value = "/delete/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult delete(String dbId) {
        return ResponseResult.createSuccessInfo();
    }

    @PermissionsRequires(value = "/datasource/metadataMgr?dbId", resourceType = ResourceType.DATA_DATASOURCE)
    @RequestMapping(value = "/metadataMgr/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult metadataMgr(String dbId) {
        return ResponseResult.createSuccessInfo();
    }

}
