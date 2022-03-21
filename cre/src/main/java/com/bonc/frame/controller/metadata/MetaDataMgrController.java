package com.bonc.frame.controller.metadata;

import com.bonc.frame.entity.metadata.MetaDataScanTask;
import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.service.metadata.MetaDataMgrService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.JsonUtils;
import com.bonc.frame.util.ResponseResult;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author qxl
 * @date 2016年12月7日 上午10:13:24
 * @version 1.0.0
 */
@Controller
@RequestMapping("/metadata")
public class MetaDataMgrController {

	@Resource
	private MetaDataMgrService metaDataMgrService;

	@Autowired
	private VariableService variableService;

	// 元数据管理主页
	@RequestMapping("/view")
    public String mgrView(String packageId, String childOpen, Model model, HttpServletRequest request) throws Exception {
        if (packageId == null) {
			throw new Exception("The folder id is null.");
		}
        model.addAttribute("folderId", packageId);
        model.addAttribute("idx", packageId);
		model.addAttribute("childOpen", childOpen);
		List<Map<String, Object>> baseVariableType = variableService.getVariableType();
		model.addAttribute("baseVariableType", JsonUtils.beanToJson(baseVariableType).toString());
        List<Map<String, Object>> entityType = variableService.getEntityType(packageId);
		model.addAttribute("entityType", JsonUtils.beanToJson(entityType).toString());
        List<Map<String, Object>> baseVariable = variableService.getBaseVariable(packageId);
		model.addAttribute("baseVariable", JsonUtils.beanToJson(baseVariable).toString());
		return "/pages/metadata/metadataMgr";
	}

	/**
	 * 分页查询元数据表配置信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Map<String, Object> findMetaTableByPage(HttpServletRequest request, String packageId) {

		String start = ControllerUtil.getParam(request, "start");
		String size = ControllerUtil.getParam(request, "length");
		String folderId = ControllerUtil.getParam(request, "folderId");
		final Map<String, Object> metaTableByPage = metaDataMgrService.findMetaTableByPage(folderId, start, size);
//		metaTableByPage.put("draw", Integer.parseInt(draw == null ? "1" : draw));// DT返回值必须有draw
		return metaTableByPage;
	}

	/**
	 * 删除元数据表信息
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public ResponseResult deleteByPrimaryKey(String tableId) throws Exception {
		ResponseResult result = metaDataMgrService.deleteByPrimaryKey(tableId);
		return result;
	}

	/**
	 * 按主键查询元数据表信息
	 * 
	 * @return
	 */
	@RequestMapping("/selectByPrimaryKey")
	@ResponseBody
	public MetaDataTable selectByPrimaryKey(String tableId) throws Exception {
        MetaDataTable metaDataTabel = metaDataMgrService.selectTableByPrimaryKey(tableId);
		return metaDataTabel;
	}

	/**
	 * 修改元数据表信息的方法
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/update")
	@ResponseBody
	public ResponseResult updateByPrimaryKeySelective(MetaDataTable metaDataTable, HttpServletRequest request)
			throws Exception {
		metaDataTable.setUpdatePerson(ControllerUtil.getLoginUserId(request));
		metaDataTable.setUpdateDate(new Date());
		ResponseResult result = metaDataMgrService.updateByPrimaryKeySelective(metaDataTable);
		return result;
	}

    // 扫描
    @RequestMapping("/scan")
    @ResponseBody
    public ResponseResult scan(MetaDataScanTask metaDataScanTask, HttpServletRequest request) {
        metaDataScanTask.setScanPerson(ControllerUtil.getLoginUserId(request));
        ResponseResult responseResult = metaDataMgrService.scan(metaDataScanTask);
        if (responseResult.getStatus() == ResponseResult.SUCCESS_STAUS) {
			// getColumnsByScanId
            final List<Map<String, String>> allColumnByScanKey = metaDataMgrService.getAllColumnByScanKeyFromTemp(
                    metaDataScanTask.getPackageId(),
                    metaDataScanTask.getScanKey());
            final Map<String, Object> result = ImmutableMap.of(
                    "scanId", metaDataScanTask.getScanId(),
                    "columns", allColumnByScanKey
            );
            responseResult = ResponseResult.createSuccessInfo("success", result);
        }
        return responseResult;
    }

	// 获取扫描任务的状态
	@RequestMapping("/getScanStatus")
	@ResponseBody
	public String getScanStatus(String scanId) {
		String status = metaDataMgrService.getScanStatus(scanId);
		return status;
	}
	
	//查看样例数据
	/*@RequestMapping("/viewData")
	@ResponseBody
	public Map<String, Object> viewData(String tableId) throws SqlExecuteException, SQLException{
		Map<String, Object> result = metaDataMgrService.viewData(tableId);
		return result;
	}*/
	
	//根据扫描关键字(表编码)，查询该表 所有的列信息
	@RequestMapping("/getAllColumn")
	@ResponseBody
    public List<Map<String, String>> getAllColumnByScanKey(String packageId, String scanKey) {
		List<Map<String, String>> result = metaDataMgrService.getAllColumnByScanKey(packageId,scanKey);
		return result;
	}
	
	//根据columnId批量删除column
	@RequestMapping("/deleteColumns")
	@ResponseBody
	public ResponseResult deleteColumns(String columnIds){
		if(columnIds==null || columnIds.isEmpty()){
			return ResponseResult.createSuccessInfo();
		}
		List<String> list = JsonUtils.toList(columnIds, String.class);
		ResponseResult result = metaDataMgrService.deleteColumns(list);
		return result;
	}
	
	//保存扫描后的数据
	@RequestMapping("/save")
	@ResponseBody
    public ResponseResult save(String scanId, String columnIds) {
        ResponseResult result = metaDataMgrService.save(scanId, columnIds);
		return result;
	}

	//保存扫描后的数据
	@RequestMapping("/scan/cancel")
	@ResponseBody
	public ResponseResult cancel(String scanId) {
		ResponseResult result = metaDataMgrService.cancel(scanId);
		return result;
	}
}
