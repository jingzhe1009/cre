package com.bonc.framework.entity.format;

import com.bonc.framework.entity.exception.EntityFormatException;
import com.bonc.framework.entity.vo.EntityObject;
import com.bonc.framework.entity.vo.EntityObjectValue;

/**
 * 实体格式化接口抽象实现，实现通用逻辑，将特殊逻辑以模板方法来实现。
 * @author qxl
 * @date 2018年3月12日 下午3:56:33
 * @version 1.0
 */
public abstract class AbstractEntityFormat implements IEntityFormat {

	/*
	public EntityObjectValue parseToEntity(EntityObject entityObject, String str, String entityValueName, boolean isCache,
			boolean isCover) throws EntityFormatException {
		if(entityObject == null){
			throw new NullPointerException("The entityObject is null.");
		}
		EntityObjectValue entityObjectValue = parse(entityObject, str, null);
		if(entityObjectValue == null){
			throw new EntityFormatException("Parse to entity exception.");
		}
		entityObjectValue.setEntityValueName(entityValueName);
		if(isCache){//缓存
			if(!isCover) {//非覆盖
				EntityObjectValue cachedEntityObjectValue = EntityValueCache.getEntityValue(entityValueName);
				if(cachedEntityObjectValue != null){//已存在缓存值
					throw new EntityFormatException("There is exist cached entity object value.Cache exception.");
				}
			}
			try {
				EntityValueCache.cacheEntityObject(entityObjectValue);//缓存实体对象值
			} catch (Exception e) {
				throw new EntityFormatException(e);
			}
			return entityObjectValue;
		}
		return entityObjectValue;
	}
	*/

	@Override
	public EntityObjectValue parseToEntity(EntityObject entityObject, String str) throws EntityFormatException {
		if(entityObject == null){
			throw new NullPointerException("The entityObject is null.");
		}
		EntityObjectValue entityObjectValue = parse(entityObject, str, null);
		return entityObjectValue;
	}

	/**
	 * 具体解析实现
	 * @param entityObject	实体对象
	 * @param str			要解析转换的字符串（json/xml等）
	 * @param object		转换后的实体值别名，可通过此别名获取缓存的实体值
	 * @return
	 * @throws EntityFormatException
	 */
	public abstract EntityObjectValue parse(EntityObject entityObject, String str, String entityValueName)  throws EntityFormatException ;
	
	

}
