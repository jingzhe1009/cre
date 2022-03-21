package com.bonc.frame.service.impl.function;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.function.RuleFunction;
import com.bonc.frame.service.function.FunctionService;
import com.bonc.frame.service.rule.RuleDetailService;
import com.bonc.frame.service.syslog.SysLogService;
import com.bonc.frame.util.ConstantUtil;
import com.bonc.frame.util.ResponseResult;
import com.bonc.framework.util.IdUtil;
import com.bonc.framework.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 
 * @author qxl
 * @date 2018年5月3日 上午11:14:01
 * @version 1.0
 */
@Service
public class FunctionServiceImpl implements FunctionService {
	
	@Autowired
	private DaoHelper daoHelper;

    @Autowired
    private SysLogService sysLogService;
	
	private final String _MYBITSID_PREFIX = "com.bonc.frame.mapper.function.RuleFunctionMapper.";
	
	@Autowired
	private RuleDetailService ruleDetailService;

	@Override
	@Transactional
	public Map<String, Object> getFunList(String functionName, String folderId, String groupId, String start,
			String size) {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("functionName", "%"+functionName+"%");
		param.put("folderId", folderId);
		param.put("groupId", groupId);
		Map<String, Object> result = daoHelper.queryForPageList(_MYBITSID_PREFIX + "getFunList", param, start, size);
		return result;
	}

	@Override
	@Transactional
	public ResponseResult insert(RuleFunction ruleFunction) {
		boolean isRepeatFunName = isRepeatFunName(null, ruleFunction.getFunctionName(), 
				ruleFunction.getFolderId(), ruleFunction.getGroupId());
		if(isRepeatFunName) {
			return ResponseResult.createFailInfo("函数名重复.");
		}
		ruleFunction.setFunctionId(IdUtil.createId());
		ruleFunction.setCreateDate(new Date());
		daoHelper.insert(_MYBITSID_PREFIX + "insertSelective", ruleFunction);


        sysLogService.logOperate(ConstantUtil.OPERATE_CREATE_FUNC,
                JSON.toJSONString(ruleFunction));


		ResponseResult result = ResponseResult.createSuccessInfo();
		return result;
	}

	@Override
	public boolean isRepeatFunName(String functionId, String functionName, String folderId, String groupId) {
		if(functionName==null || functionName.isEmpty()) {
			return true;
		}
		Map<String,String> param = new HashMap<String,String>();
		param.put("functionId", functionId);
		param.put("functionName", functionName);
		param.put("folderId", folderId);
		param.put("groupId", groupId);
		
		Integer obj =  (Integer) daoHelper.queryOne(_MYBITSID_PREFIX + "getFunCountByName", param);
		if(obj != null && obj > 0){
			return true;
		}
		return false;
	}

	@Override
	@Transactional
	public ResponseResult update(RuleFunction ruleFunction) {
		ResponseResult result = ruleDetailService.checkCanModify(ruleFunction.getFunctionId(), ruleFunction.getFolderId());
		if(result.getStatus() == ResponseResult.ERROR_STAUS) {
			return result;
		}
		boolean isRepeatFunName = isRepeatFunName(ruleFunction.getFunctionId(), ruleFunction.getFunctionName(), 
				ruleFunction.getFolderId(), ruleFunction.getGroupId());
		if(isRepeatFunName) {
			return ResponseResult.createFailInfo("函数名重复.");
		}
		ruleFunction.setUpdateDate(new Date());
		daoHelper.update(_MYBITSID_PREFIX + "updateByPrimaryKeySelective", ruleFunction);

        sysLogService.logOperate(ConstantUtil.OPERATE_UPDATE_FUNC,
                JSON.toJSONString(ruleFunction));

		result = ResponseResult.createSuccessInfo();
		return result;
	}

	//删除函数
	@Override
	@Transactional
	public ResponseResult delete(String functionId,String folderId) {
		ResponseResult result = ruleDetailService.checkCanModify(functionId, folderId);
		if(result.getStatus() == ResponseResult.ERROR_STAUS) {
			return result;
		}
		daoHelper.delete(_MYBITSID_PREFIX + "deleteByPrimaryKey", functionId);

        final JSONObject info = new JSONObject();
        info.put("functionId", functionId);
        info.put("folderId", folderId);
        sysLogService.logOperate(ConstantUtil.OPERATE_DELETE_FUNC,
                info.toJSONString());

		result = ResponseResult.createSuccessInfo();
		return result;
	}
	
	//获取函数树
	@Override
	@Transactional
	public List<Map<String, Object>> funcMethodTree(String folderId, String groupId){
		List<Map<String, Object>> result= new ArrayList<>();
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("folderId", folderId);
		param.put("groupId", groupId);
		param.put("functionName", "%%");
		List<RuleFunction> list = daoHelper.queryForList(_MYBITSID_PREFIX + "getFunList", param);
		for(RuleFunction ruleFunction:list){
			Map<String, Object> map = new HashMap<>();
			map.put("id", ruleFunction.getFunctionId());
			map.put("title", ruleFunction.getFunctionName());
			map.put("functionReturnType",ruleFunction.getFunctionReturnType());
			String functionParamsConf = ruleFunction.getFunctionParamsConf();
			int num = 0;
			try {
				List<Map> funcList = JsonUtils.toList(functionParamsConf, Map.class);
				map.put("funcList", funcList);
				num = funcList.size();
			} catch (Exception e) {
			}
			
			map.put("paramsNum", num);
			result.add(map);
		}
		
		return result;
	}

	
}
