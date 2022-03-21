package com.bonc.framework.entity.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bonc.framework.entity.vo.EntityObjectValue;

/**
 * 实体对象缓存
 * @author qxl
 * @date 2018年3月12日 上午10:23:28
 * @version 1.0
 */
public class EntityValueCache {
	
	private Map<String,EntityObjectValue> entityValueMap = new ConcurrentHashMap<String,EntityObjectValue>();
	
	/**
	 * 根据实体值别名，获取实体对象值
	 * @param entityValueName	实体值别名
	 * @return
	 */
	public EntityObjectValue getEntityValue(String entityValueName) {
		if(entityValueMap.containsKey(entityValueName)) {
			return entityValueMap.get(entityValueName);
		}
		return null;
	}
	
	/**
	 * 缓存实体值对象
	 * @param entityObjectValue
	 * @return
	 * @throws Exception 
	 */
	public void cacheEntityObject(EntityObjectValue entityObjectValue) throws Exception {
		if(entityObjectValue == null) {
			throw new Exception("The entityObjectValue is null.");
		}
		String entityValueAlias = entityObjectValue.getEntityValueName();
		if(entityValueAlias == null) {
			throw new Exception("The entityObjectValue name is null.");
		}
		entityValueMap.put(entityValueAlias, entityObjectValue);
	}
	
	/**
	 * 根据实体名移除缓存的实体值
	 * @param entityName
	 */
	public void removeEntity(String entityValueName) {
		if(entityValueMap.containsKey(entityValueName)) {
			entityValueMap.remove(entityValueName);
		}
	}
	
	/**
	 * 移除全部缓存的实体值
	 * @param entityName
	 */
	public void removeEntity() {
		entityValueMap.clear();
	}

}
