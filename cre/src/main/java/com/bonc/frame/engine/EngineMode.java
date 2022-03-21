package com.bonc.frame.engine;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.engine.builder.AbstractEngineBuilder;
import com.bonc.frame.engine.builder.ApiEngineBuilder;
import com.bonc.frame.engine.builder.EntityEngineBuilder;
import com.bonc.frame.engine.builder.RuleEngineBuilder;
import com.bonc.frame.engine.cache.RedisCache;
import com.bonc.frame.engine.kpi.IKpiServiceImp;
import com.bonc.frame.engine.log.ApiLog;
import com.bonc.frame.engine.log.CreRuleLog;
import com.bonc.framework.api.RuleApiFacade;
import com.bonc.framework.api.cache.IApiCache;
import com.bonc.framework.api.core.IRuleApi;
import com.bonc.framework.entity.EntityEngine;
import com.bonc.framework.entity.EntityEngineFactory;
import com.bonc.framework.rule.RuleEngineFactory;
import com.bonc.framework.rule.cache.ICache;
import com.bonc.framework.rule.resources.RuleResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * 引擎模式管理类
 * @author qxl
 * @date 2018年5月11日 上午10:09:25
 * @version 1.0
 */
public class EngineMode {
	private static Log log = LogFactory.getLog(EngineManager.class);
	
	private static final String redisPropertiesName = "application.properties";
	
	private static boolean isLoadRedisProp = false;
	
	private static Properties redisProp;
	
	private static EngineModeEume mode = EngineModeEume.MEMERY;
	
	static {
		loadRedisProperties();
		if(redisProp != null) {
			String type = redisProp.getProperty("spring.session.store-type");
			if("redis".equals(type)) {
				EngineMode.setMode(EngineModeEume.REDIS);
				log.info("The EngineMode is changed to "+EngineModeEume.REDIS);
			}
		}else {
			log.error("load file ["+redisPropertiesName+"] error.");
		}
	}
	
	enum EngineModeEume{
		MEMERY,REDIS;
	}

	/**
	 * 初始化引擎
	 * @param mode
	 * @param daoHelper
	 * @param engineBuilderList
	 */
	public static void initEngine(EngineModeEume mode,DaoHelper daoHelper,List<AbstractEngineBuilder> engineBuilderList){
		switch (mode) {
			case REDIS:
				initRedisMode(daoHelper,engineBuilderList);
				break;
			default:
				initMemeryMode(daoHelper, engineBuilderList);
				break;
		}
	}
	/**
	 * 使用默认模式初始化引擎
	 */
	public static void initEngine(DaoHelper daoHelper,List<AbstractEngineBuilder> engineBuilderList){
		EngineMode.initEngine(EngineMode.getMode(), daoHelper, engineBuilderList);
	}
	
	private synchronized static void loadRedisProperties(){
		if(isLoadRedisProp == false) {
			redisProp = new Properties();
			InputStream in = null;
			try {
				in = EngineManager.class.getClassLoader().getResourceAsStream(redisPropertiesName);
				redisProp.load(in);
			} catch (IOException e) {
				log.error("Load redis-config.properties error.");
			} finally {
				try {
					if(in != null){
						in.close();
					}
				} catch (IOException e) {
					log.error(e);
				}
			}
			isLoadRedisProp = true;
		}
		
	}
	
	private static void initRedisMode(DaoHelper daoHelper,List<AbstractEngineBuilder> engineBuilderList){
		if(isLoadRedisProp == false) {
			loadRedisProperties();
		}
		//redis连接信息
		String ip = redisProp.getProperty("spring.redis.host");
		String portStr = redisProp.getProperty("spring.redis.port");
		portStr=portStr==null?"-1":portStr.trim();
		int port = -1;
		try {
			port = Integer.parseInt(portStr);
		} catch(Exception e) {
			log.error(e);
		}
		
		//规则引擎
		ICache<String, String> buildCache3 = new RedisCache<String, String>("rule_");
		AbstractEngineBuilder reb = new RuleEngineBuilder();
		reb.setIsBuildCache(buildCache3);
		engineBuilderList.add(reb);
		//实体引擎
		ICache<String, String> buildCache1 = new RedisCache<String, String>("entity_");
		AbstractEngineBuilder eeb = new EntityEngineBuilder();
		eeb.setIsBuildCache(buildCache1);
		engineBuilderList.add(eeb);
		//接口引擎
		ICache<String, String> buildCache2 = new RedisCache<String, String>("api_");
		AbstractEngineBuilder aeb = new ApiEngineBuilder();
		aeb.setIsBuildCache(buildCache2);
		engineBuilderList.add(aeb);
		
		//设置api日志实现
		RuleApiFacade.getInstance().setApiLog(new ApiLog(daoHelper));
		//设置规则日志实现
		RuleEngineFactory.getRuleEngine().setRuleLog(new CreRuleLog(daoHelper));
		//设置指标实现
		RuleEngineFactory.getRuleEngine().setKpiService(new IKpiServiceImp());
		//设置规则缓存为redis--默认缓存到内存中
		ICache<String, RuleResource> ruleCache = new RedisCache<String, RuleResource>("ruleCache_");
		RuleEngineFactory.getRuleEngine().setRuleCache(ruleCache);
		//设置实体引擎缓存器
		ICache<String, EntityEngine> entityEngineCache = new RedisCache<>("entithCache_");
		EntityEngineFactory.getInstance().setEntityEngineCache(entityEngineCache);
		//设置api接口引擎缓存器
		IApiCache<String, IRuleApi> apiCache = new RedisCache<String,IRuleApi>("apiCache_");
		RuleApiFacade.getInstance().setApiCache(apiCache);
	}
	
	private static void initMemeryMode(DaoHelper daoHelper,List<AbstractEngineBuilder> engineBuilderList) {
		//规则引擎
		AbstractEngineBuilder reb = new RuleEngineBuilder();
		engineBuilderList.add(reb);
		//实体引擎
		AbstractEngineBuilder eeb = new EntityEngineBuilder();
		engineBuilderList.add(eeb);
		//接口引擎
		AbstractEngineBuilder aeb = new ApiEngineBuilder();
		engineBuilderList.add(aeb);
		
		//设置api日志实现
		RuleApiFacade.getInstance().setApiLog(new ApiLog(daoHelper));
		//设置规则日志实现
		RuleEngineFactory.getRuleEngine().setRuleLog(new CreRuleLog(daoHelper));
		// 设置指标的实现
		RuleEngineFactory.getRuleEngine().setKpiService(new IKpiServiceImp());


	}
	
	/**
	 * 缓存方式：redis、memery
	 * @return
	 */
	public static EngineModeEume getMode() {
		return mode;
	}

	public static void setMode(EngineModeEume mode) {
		EngineMode.mode = mode;
	}

	public static void main(String[] args) {
		System.out.println(EngineMode.EngineModeEume.MEMERY);
	}
	
}
