package com.bonc.frame.controller.metadata;

import com.bonc.frame.entity.metadata.MetaDataColumn;
import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissionsRequires;
import com.bonc.frame.service.metadata.MetaDataMgrService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.IdUtil;
import com.bonc.frame.util.JsonUtils;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author qxl
 * @date 2017年8月8日 下午5:14:56
 * @version 1.0
 */
@Controller
@RequestMapping("/batchdata/column")
public class ColumnMgrController {
	
	@Resource
	private MetaDataMgrService metaDataMgrService;
	
	@Autowired
	private VariableService variableService;

	@RequestMapping("/view")
    public String index(String packageId, String tableId, String tableName,
                        String scanId, String childOpen, HttpServletRequest request) throws Exception {
		if (packageId == null || packageId.isEmpty()) {
			throw new Exception("规则包Id不能为空");
		}

		MetaDataTable metaTable = metaDataMgrService.selectTableByPrimaryKey(tableId);
		request.setAttribute("packageId", packageId);
		request.setAttribute("tableId", tableId);
		request.setAttribute("tableName", metaTable.getTableName());
        request.setAttribute("scanId", scanId);
		if (StringUtils.isBlank(metaTable.getCdtConfig())) {
			metaTable.setCdtConfig("''");
		}
		request.setAttribute("metaTable", metaTable);
		List<Map<String, Object>> baseVariableType = variableService.getVariableType();
		request.setAttribute("baseVariableType", JsonUtils.beanToJson(baseVariableType).toString());
		List<Map<String, Object>> entityType = variableService.getEntityType(packageId);
		request.setAttribute("entityType", JsonUtils.beanToJson(entityType).toString());
		List<Map<String, Object>> baseVariable = variableService.getBaseVariable(packageId);
		request.setAttribute("baseVariable", JsonUtils.beanToJson(baseVariable).toString());
        request.setAttribute("childOpen", childOpen);
		return "/pages/metadata/metadataConfig";
	}
	
	
	//查看表的所有列信息
//	@PermissionsRequires(value = "/metaTable/view?tableId", resourceType = ResourceType.DATA_METADATA)
	@RequestMapping("/columnList")
	@ResponseBody
	public Map<String, Object> findColumnByTableId(String tableId, String columnName, HttpServletRequest request) {
		String start = ControllerUtil.getParam(request, "start");
		String size = ControllerUtil.getParam(request, "length");
        if (StringUtils.isBlank(columnName)) {
            columnName = "";
        }
        columnName = "%" + columnName.trim() + "%";

		tableId = tableId.trim();

		Map<String, Object> returnData = metaDataMgrService.findColumnByTableId(tableId, columnName, start, size);
//		returnData.put("draw", Integer.parseInt(draw==null?"1":draw));
		return returnData;
	}

//    @PermissionsRequires(value = "/metaTable/view?tableId", resourceType = ResourceType.DATA_METADATA)
    @RequestMapping("/columnList/all")
    @ResponseBody
    public ResponseResult findAllColumnsByTableId(String tableId, String columnName, HttpServletRequest request) {
        if (StringUtils.isBlank(columnName)) {
            columnName = "";
        }
        columnName = "%" + columnName.trim() + "%";

        tableId = tableId.trim();

        List<Map<String, Object>> columnByTableId = metaDataMgrService.findColumnByTableId(tableId, columnName);
//		returnData.put("draw", Integer.parseInt(draw==null?"1":draw));
        return ResponseResult.createSuccessInfo("", columnByTableId);
    }

	//查看列详细信息
	@RequestMapping("/columnInfo")
	@ResponseBody
	public MetaDataColumn columnInfo(String columnId){
		MetaDataColumn column = metaDataMgrService.selectColumnInfo(columnId);
		return column;
	}
	
	//删除列
	@RequestMapping("/deleteColumn")
	@ResponseBody
	public ResponseResult deleteColumn(String columnId){
		ResponseResult result = metaDataMgrService.deleteColumn(columnId);
		return result;
	}

	//删除列
//	@PermissionsRequires(value = "/metaTable/delete?tableId", resourceType = ResourceType.DATA_METADATA)
	@RequestMapping("/deleteColumn2")
	@ResponseBody
	public ResponseResult deleteColumn(String columnId, String tableId) {
		ResponseResult result = metaDataMgrService.deleteColumn(columnId);
		return result;
	}
	
	//新增列
//	@PermissionsRequires(value = "/metaTable/add?column.tableId", resourceType = ResourceType.DATA_METADATA)
    @RequestMapping("/insertColumn")
    @ResponseBody
    public ResponseResult insertColumn(MetaDataColumn column){
		column.setColumnId(IdUtil.createId());
		ResponseResult result = metaDataMgrService.insertColumn(column);
		return result;
	}
	
	//修改列
//	@PermissionsRequires(value = "/metaTable/update?column.tableId", resourceType = ResourceType.DATA_METADATA)
    @RequestMapping("/updateColumn")
    @ResponseBody
    public ResponseResult updateColumn(MetaDataColumn column){
		ResponseResult result = metaDataMgrService.updateColumn(column);
		return result;
	}
	
	//获取表的列 树
	@RequestMapping("/getTableTree")
	@ResponseBody
	public List<Map<String, String>> getTableTree(String packageId){
		List<Map<String, String>> result = metaDataMgrService.getSysVarTree(packageId);
		return result;
	}
	
	//导入列
	@RequestMapping("/importColumn")
	@ResponseBody
	public ResponseResult importColumn(String tableId,String typeId,String columns){
		ResponseResult result = metaDataMgrService.importColumn(tableId,typeId,columns);
		return result;
	}

	//保存配置的条件
	@RequestMapping("/saveCdtConfig")
	@ResponseBody
	public ResponseResult saveCdtConfig(MetaDataTable metaTable){
		ResponseResult result = metaDataMgrService.saveCdtConfig(metaTable);
		return result;
	}

//	---------------------------------------------------------------------------------------------
	
	@RequestMapping("/cdtConfFrame")
	//加载条件配置弹框页面
	public String quotaHandFrame(String packageId, String tableId, String text, Model model, HttpServletRequest request) throws Exception{
		if(packageId==null || packageId.isEmpty()){
			throw new Exception("规则包Id不能为空！");
		}
		model.addAttribute("title","条件配置");//frame的标题
		//配置的条件
		model.addAttribute("text",text);
		
		//输入表tree
//		List<Map<String,Object>> varResult1 = this.dbResourceService.findQuotaTreeByTableId(packageId, tableId);
//		model.addAttribute("ruleVarData", JsonUtils.beanToJson(varResult1));
		
		//函数树
		/*List<Map<String,Object>> ruleFunResult = ruleFunctionService.getFunTree(RuleMgrController.type_batchdata_function,ControllerUtil.getTenant(request));
		model.addAttribute("ruleFunData", JsonUtils.beanToJson(ruleFunResult));*/
		model.addAttribute("packageId",packageId);
		model.addAttribute("tableId",tableId);
		return "/rule/quotaHandFrame";
	}

	// ------------------------------- 权限校验 -------------------------------

	@PermissionsRequires(value = "/metaTable/view?tableId", resourceType = ResourceType.DATA_METADATA)
	@RequestMapping(value = "/view/checkAuth", method = RequestMethod.GET)
	@ResponseBody
	public ResponseResult view(String tableId) {
		return ResponseResult.createSuccessInfo();
	}

	@PermissionsRequires(value = "/metaTable/add?tableId", resourceType = ResourceType.DATA_METADATA)
	@RequestMapping(value = "/save/checkAuth", method = RequestMethod.GET)
	@ResponseBody
    public ResponseResult save(String tableId) {
		return ResponseResult.createSuccessInfo();
	}

	@PermissionsRequires(value = "/metaTable/update?tableId", resourceType = ResourceType.DATA_METADATA)
	@RequestMapping(value = "/update/checkAuth", method = RequestMethod.GET)
	@ResponseBody
    public ResponseResult update(String tableId) {
		return ResponseResult.createSuccessInfo();
	}

	@PermissionsRequires(value = "/metaTable/delete?tableId", resourceType = ResourceType.DATA_METADATA)
	@RequestMapping(value = "/delete/checkAuth", method = RequestMethod.GET)
	@ResponseBody
    public ResponseResult delete(String tableId) {
		return ResponseResult.createSuccessInfo();
	}

}
