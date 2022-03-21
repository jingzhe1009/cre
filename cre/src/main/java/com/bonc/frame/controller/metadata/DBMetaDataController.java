package com.bonc.frame.controller.metadata;

import com.bonc.frame.entity.metadata.MetaDataScanTask;
import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.service.metadata.DBMetaDataMgrService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.JsonUtils;
import com.bonc.frame.util.ResponseResult;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 数据源下元数据管理
 *
 * @author yedunyao
 * @date 2019/10/21 17:15
 */
@Controller
@RequestMapping("/datasource/metadata")
public class DBMetaDataController {

    @Autowired
    private DBMetaDataMgrService dbMetaDataMgrService;

    // ---------------------------- 表管理 ----------------------------

    /**
     * 按主键查询元数据表信息
     *
     * @return
     */
    @RequestMapping("/table/getOne")
    @ResponseBody
    public MetaDataTable selectByPrimaryKey(String tableId) throws Exception {
        MetaDataTable metaDataTabel = dbMetaDataMgrService.selectTableByPrimaryKey(tableId);
        return metaDataTabel;
    }

    //列出所有的元数据表信息
    @RequestMapping("/table/list")
    @ResponseBody
    public List<Map<String, Object>> findMetaTableList(String dbId) {
        List<Map<String, Object>> result = dbMetaDataMgrService.findMetaTableByDbId(dbId);
        return result;
    }

    //分页列出所有的元数据表信息
    @RequestMapping("/table/paged")
    @ResponseBody
    public Map<String, Object> pagedFindMetaTableByDbId(String dbId, String start, String length) {
        Map<String, Object> result = dbMetaDataMgrService.pagedFindMetaTableByDbId(dbId, start, length);
        return result;
    }

    /**
     * 修改元数据表信息的方法
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/table/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult updateByPrimaryKeySelective(MetaDataTable metaDataTable, HttpServletRequest request)
            throws Exception {
        metaDataTable.setUpdatePerson(ControllerUtil.getLoginUserId(request));
        metaDataTable.setUpdateDate(new Date());
        ResponseResult result = dbMetaDataMgrService.updateByPrimaryKeySelective(metaDataTable);
        return result;
    }

    /**
     * 删除元数据表信息
     */
    @RequestMapping(value = "/table/delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult deleteByPrimaryKey(String tableId) throws Exception {
        ResponseResult result = dbMetaDataMgrService.deleteByPrimaryKey(tableId);
        return result;
    }

    // 扫描
    @RequestMapping("/scan")
    @ResponseBody
    public ResponseResult scan(MetaDataScanTask metaDataScanTask, HttpServletRequest request) {
        metaDataScanTask.setScanPerson(ControllerUtil.getLoginUserId(request));
        ResponseResult responseResult = dbMetaDataMgrService.scan(metaDataScanTask);
        if (responseResult.getStatus() == ResponseResult.SUCCESS_STAUS) {
            String scanId = metaDataScanTask.getScanId();
            final List<Map<String, String>> allColumnByScanKey = dbMetaDataMgrService.getAllColumnByScanIdFromTemp(
                    scanId);
            final Map<String, Object> result = ImmutableMap.of(
                    "scanId", scanId,
                    "columns", allColumnByScanKey
            );
            responseResult = ResponseResult.createSuccessInfo("success", result);
        }
        return responseResult;
    }

    // 获取扫描任务的状态
    @RequestMapping("/scan/getScanStatus")
    @ResponseBody
    public String getScanStatus(String scanId) {
        String status = dbMetaDataMgrService.getScanStatus(scanId);
        return status;
    }

    //根据columnId批量删除column
    @RequestMapping("/deleteColumns")
    @ResponseBody
    public ResponseResult deleteColumns(String columnIds) {
        if (columnIds == null || columnIds.isEmpty()) {
            return ResponseResult.createSuccessInfo();
        }
        List<String> list = JsonUtils.toList(columnIds, String.class);
        ResponseResult result = dbMetaDataMgrService.deleteColumns(list);
        return result;
    }

    //保存扫描后的数据
    @RequestMapping("/scan/save")
    @ResponseBody
    public ResponseResult save(String scanId, String columnIds) {
        ResponseResult result = dbMetaDataMgrService.save(scanId, columnIds);
        return result;
    }

    //保存扫描后的数据
    @RequestMapping("/scan/cancel")
    @ResponseBody
    public ResponseResult cancel(String scanId) {
        ResponseResult result = dbMetaDataMgrService.cancel(scanId);
        return result;
    }

}
