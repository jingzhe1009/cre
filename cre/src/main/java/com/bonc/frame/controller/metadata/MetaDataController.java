package com.bonc.frame.controller.metadata;

import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.auth.RoleService;
import com.bonc.frame.service.dbresource.DbResourceService;
import com.bonc.frame.service.metadata.MetaDataMgrService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.IdUtil;
import com.bonc.frame.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 数据跑批规则包之元数据管理controller
 *
 * @author qxl
 * @date 2017年8月7日 上午9:33:56
 * @version 1.0
 */
@Controller
@RequestMapping("/batchdata/metadata")
public class MetaDataController {
	@Autowired
	private RoleService roleService;
	@Resource
	private MetaDataMgrService metaDataMgrService;

	@Resource
	private DbResourceService dbResourceService;

	@Autowired
	private AuthorityService authorityService;

//	@Resource
//	private RulePackageMgrService rulePackageMgrService;

	//主页面
	@RequestMapping("/index")
	public String index(String packageId, HttpServletRequest request) throws Exception{
		if (packageId == null || packageId.isEmpty()) {
			throw new Exception("规则包Id不能为空");
		}
		request.setAttribute("packageId", packageId);
		/*RulePackage rulePackage = rulePackageMgrService.selectRulePackageById(packageId);
		request.setAttribute("rulePackage", rulePackage);*/
		return "batchdata/metaDataMgr";
	}

	//列出所有的元数据表信息
	@RequestMapping("/tableList")
	@ResponseBody
    public List<Map<String, Object>> findMetaTableList(String packageId) {
        List<Map<String, Object>> result = metaDataMgrService.findMetaTable(packageId);
		return result;
	}

//	public ResponseResult deleteMetaTable(){
//
//	}

	//新增表
	@RequestMapping("/insertTable")
	@ResponseBody
	public ResponseResult insertTable(MetaDataTable table, String relTable, HttpServletRequest request){
		String tableId = IdUtil.createId();
		table.setTableId(tableId);
		table.setCreateDate(new Date());
		final String currentUser = ControllerUtil.getLoginUserId(request);
//		List<currentUser> rolesByUser = roleService.getRoleByUser(currentUser.toString());
		table.setCreatePerson(currentUser);
		ResponseResult result = metaDataMgrService.insertTable(table,relTable);

		// 自动授权：插入用户对数据的权限
		/*final Authority authority = new Authority();
		authority.setResourceId(tableId);
		authority.setResourceExpression("*");
		authorityService.grantToUser(authority, ResourceType.DATA_METADATA.getType(), currentUser);*/
		authorityService.autoGrantAuthToCurrentUser(tableId, ResourceType.DATA_METADATA);

		result.setData(tableId);
		return result;
	}

	//修改表
	@RequestMapping("/updateTable")
	@ResponseBody
	public ResponseResult updateTable(MetaDataTable table,String relTable, HttpServletRequest request){
		table.setUpdateDate(new Date());
		table.setUpdatePerson(ControllerUtil.getLoginUserId(request));
		ResponseResult result = metaDataMgrService.updateTable(table,relTable);
		result.setData(table.getTableId());
		return result;
	}

	//根据关联表id查询其关联关系
	@RequestMapping("/tableRelate")
	@ResponseBody
	public List<Map<String,String>> findRelateByTableId(String tableId){
		List<Map<String,String>> result = dbResourceService.findRelateByTableId(tableId);
		return result;
	}

	//导入元数据信息
	@RequestMapping("/importMetaData")
	@ResponseBody
	public ResponseResult importMetaData(String packageId,String selectedPackageId,String isOverwrite) throws Exception{
		ResponseResult result = metaDataMgrService.importMetaData(packageId,selectedPackageId,isOverwrite);
		return result;
	}

}
