package com.bonc.frame.controller.function;

import com.bonc.frame.entity.function.RuleFunction;
import com.bonc.frame.service.function.FunctionService;
import com.bonc.frame.service.syslog.SysLogService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.JsonUtils;
import com.bonc.frame.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 规则函数相关操作Controller
 * @author qxl
 * @date 2018年5月3日 上午10:29:13
 * @version 1.0
 */
@Controller
@RequestMapping("/fun")
public class FunctionController {
	
	@Resource
	private FunctionService functionService;
	
	@Autowired
	private VariableService variableService;

	@Autowired
	private SysLogService sysLogService;

	//规则函数主页面
	@RequestMapping("/index")
	public String index(String folderId,String groupId,String childOpen,Model model) throws Exception {
		if(folderId == null){
			throw new Exception("The folder id is null.");
		}
		model.addAttribute("folderId", folderId);
		model.addAttribute("groupId", groupId);
		model.addAttribute("idx", folderId);
		model.addAttribute("childOpen", childOpen);
		List<Map<String, Object>> baseVariableType = variableService.getVariableType();
		model.addAttribute("baseVariableType", JsonUtils.beanToJson(baseVariableType).toString());
		List<Map<String, Object>> entityType = variableService.getEntityType(folderId);
		model.addAttribute("entityType", JsonUtils.beanToJson(entityType).toString());
		return "/pages/function/funIndex";
	}
	
	//函数列表
	@RequestMapping("/list")
	@ResponseBody
	public Map<String,Object> funList(String folderId,String groupId,
			String functionName,HttpServletRequest request) {
		String start = request.getParameter("start");
		String size = request.getParameter("length");
		Map<String,Object> result = functionService.getFunList(functionName,folderId,groupId,start,size);
		return result;
	}
	
	
	//新建函数主页面
	@RequestMapping("/createFunIndex")
    public String createIndex(String folderId,String groupId,Model model) throws Exception {
		if(folderId == null){
			throw new Exception("The folder id is null.");
		}
		model.addAttribute("folderId", folderId);
		model.addAttribute("idx", folderId);
		List<Map<String, Object>> baseVariableType = variableService.getVariableType();
		model.addAttribute("baseVariableType", JsonUtils.beanToJson(baseVariableType).toString());
		List<Map<String, Object>> entityType = variableService.getEntityType(folderId);
		model.addAttribute("entityType", JsonUtils.beanToJson(entityType).toString());
        return "/pages/function/createFun";
    }
	
	//新建函数
	@RequestMapping("/insert")
	@ResponseBody
	public ResponseResult insert(RuleFunction ruleFunction,HttpServletRequest request) {
		ruleFunction.setCreatePerson(ControllerUtil.getLoginUserId(request));
		ResponseResult result = functionService.insert(ruleFunction);
		return result;
	}
	
	//修改函数
	@RequestMapping("/update")
	@ResponseBody
	public ResponseResult update(RuleFunction ruleFunction,HttpServletRequest request) {
		ruleFunction.setUpdatePerson(ControllerUtil.getLoginUserId(request));
		ResponseResult result = functionService.update(ruleFunction);
		return result;
	}
	
	//删除函数
	@RequestMapping("/delete")
	@ResponseBody
	public ResponseResult delete(String functionId, String folderId) {
        ResponseResult result = functionService.delete(functionId, folderId);
		return result;
	}
}
