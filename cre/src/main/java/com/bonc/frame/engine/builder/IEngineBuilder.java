package com.bonc.frame.engine.builder;

/**
 * 引擎（实体引擎、接口引擎、规则引擎等）初始化接口
 * 用于初始化/构造需要的引擎
 * @author qxl
 * @date 2018年4月2日 上午10:17:47
 * @version 1.0
 */
public interface IEngineBuilder {
	
	/**
	 * 构造引擎
	 * @param folderId		文件夹ID
	 * @param ruleId		规则ID
	 * @throws Exception
	 */
	void builder(String folderId,String ruleId) throws Exception;
	
	/**
	 * 构造引擎
	 * @param folderId		文件夹ID
	 * @param ruleId		规则ID
	 * @param isTest		是否是测试模式
	 * @throws Exception
	 */
	void builder(String folderId,String ruleId,boolean isTest) throws Exception;
	
	/**
	 * 是否已经build
	 * @param folderId
	 * @param ruleId
	 * @return
	 */
	boolean isBuild(String folderId,String ruleId);
	
	
	/**
	 * 清除已经build
	 * @param folderId
	 * @param ruleId
	 * @return
	 */
	void clean(String folderId,String ruleId);

	/**
	 * 编译完成
	 * @param folderId
	 * @param ruleId
	 */
	void buildOver(String folderId,String ruleId);
}
