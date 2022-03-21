package com.bonc.framework.entity;

import com.bonc.framework.entity.cache.DefaultEntityEngineCache;
import com.bonc.framework.entity.cache.IEntityEngineCache;

/**
 * 实体引擎工厂，通过此类 创建/获取 实体引擎
 * @author qxl
 * @date 2018年4月2日 下午6:43:08
 * @version 1.0
 */
public class EntityEngineFactory {
	
	//实体引擎Map,每个文件夹对应一个实体引擎。key-文件夹id，value-实体引擎
	private IEntityEngineCache<String, EntityEngine> entityEngineCache;
	
	private static EntityEngineFactory entityEngineFactory;
	private EntityEngineFactory(){
		entityEngineCache = new DefaultEntityEngineCache<>();
	}
	public static EntityEngineFactory getInstance() {
		if(entityEngineFactory == null){
			synchronized (EntityEngineFactory.class) {
				if(entityEngineFactory == null){//双重检测机制
					entityEngineFactory = new EntityEngineFactory();
				}
			}
		}
		return entityEngineFactory;
	}
	
	/**
	 * 替换默认缓存器
	 * @param entityEngineCache
	 */
	public void setEntityEngineCache(IEntityEngineCache<String, EntityEngine> entityEngineCache) {
		if(entityEngineCache != null) {
			this.entityEngineCache = entityEngineCache;
		}
	}
	
	/**
	 * 根据文件夹ID创建实体引擎
	 * @param folderId
	 * @return
	 */
	public EntityEngine createEntityEngine(String folderId) {
		if(entityEngineCache.containsKey(folderId)) {
			return entityEngineCache.get(folderId);
		}
		EntityEngine entityEngine = new EntityEngine();
		entityEngineCache.put(folderId, entityEngine);
		return entityEngine;
	}
	
	/**
	 * 根据文件夹ID获取实体引擎
	 * @param folderId
	 * @return
	 */
	public EntityEngine getEntityEngine(String folderId) {
		if(entityEngineCache.containsKey(folderId)) {
			return entityEngineCache.get(folderId);
		}
		return null;
	}
	
	/**
	 * 将实体引擎存入缓存
	 * @param folderId
	 * @return
	 */
	public void putEntityEngine(String folderId,EntityEngine entityEngine) {
		entityEngineCache.put(folderId, entityEngine);
	}
	
	/**
	 * 根据文件夹ID移除实体引擎
	 * @param folderId
	 * @return
	 */
	public void removeEntityEngine(String folderId) {
		if(entityEngineCache.containsKey(folderId)) {
			entityEngineCache.remove(folderId);
		}
	}

}
