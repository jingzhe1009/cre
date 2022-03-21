package com.bonc.framework.entity.cache;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bonc.framework.entity.vo.EntityObject;

/**
 * 实体对象缓存
 * @author qxl
 * @date 2018年3月12日 上午10:23:28
 * @version 1.0
 */
public class EntityCache implements Serializable {
	private static final long serialVersionUID = 8397390831905121710L;
	private Map<String,EntityObject> entityMap = new ConcurrentHashMap<String,EntityObject>();
	
	/**
	 * 根据实体名，获取实体对象
	 * @param entityName
	 * @return
	 */
	public EntityObject getEntityObject(String entityName) {
		if(entityMap.containsKey(entityName)) {
			return entityMap.get(entityName);
		}
		return null;
	}
	
	/**
	 * 缓存实体对象
	 * @param entityObject
	 * @return
	 * @throws Exception 
	 */
	public void cacheEntityObject(EntityObject entityObject) throws Exception {
		if(entityObject == null){
			throw new Exception("The entityObject is null.");
		}
		String entityName = entityObject.getEntityName();
		if(entityName == null){
			throw new Exception("The entityObject name is null.");
		}
		if(entityMap.containsKey(entityName)) {
			throw new Exception("The entityObject is exist.");
		}
		entityMap.put(entityName, entityObject);
	}
	
	/**
	 * 根据实体名移除实体
	 * @param entityName
	 */
	public void removeEntity(String entityName) {
		if(entityMap.containsKey(entityName)) {
			entityMap.remove(entityName);
		}
	}
	
	/**
	 * 移除全部实体
	 * @param entityName
	 */
	public void removeAll() {
		entityMap.clear();
	}

}
