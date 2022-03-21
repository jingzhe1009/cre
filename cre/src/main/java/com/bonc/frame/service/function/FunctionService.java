package com.bonc.frame.service.function;

import java.util.List;
import java.util.Map;

import com.bonc.frame.entity.function.RuleFunction;
import com.bonc.frame.util.ResponseResult;

/**
 * 
 * @author qxl
 * @date 2018年5月3日 上午11:11:01
 * @version 1.0
 */
public interface FunctionService {

	/**
	 * 分页获取函数列表
	 * @param functionName
	 * @param folderId
	 * @param groupId
	 * @param start
	 * @param size
	 * @return
	 */
	Map<String, Object> getFunList(String functionName, String folderId, String groupId, String start, String size);

	/**
	 * 新建函数
	 * @param ruleFunction
	 * @return
	 */
	ResponseResult insert(RuleFunction ruleFunction);

	/**
	 * 判断是否存在相同的函数名
	 * @param functionId
	 * @param functionName
	 * @param folderId
	 * @param groupId
	 * @return true-重复，false-未重复
	 */
	boolean isRepeatFunName(String functionId,String functionName,String folderId,String groupId);

	/**
	 * 修改函数
	 * @param ruleFunction
	 * @return
	 */
	ResponseResult update(RuleFunction ruleFunction);

	/**
	 * 删除函数
	 * @param functionId
	 * @return
	 */
	ResponseResult delete(String functionId,String folderId);

	List<Map<String, Object>> funcMethodTree(String folderId, String groupId);
}
