package com.bonc.framework.entity.format;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bonc.framework.entity.exception.EntityFormatException;
import com.bonc.framework.entity.vo.EntityObject;
import com.bonc.framework.entity.vo.EntityObjectValue;
import com.bonc.framework.entity.vo.EntityProperty;
import com.bonc.framework.util.JsonUtils;

/**
 * 将json格式的数据转换成实体数据
 * @author qxl
 * @date 2018年3月12日 上午9:47:29
 * @version 1.0
 */
public class JsonFormat extends AbstractEntityFormat {

	@Override
	public EntityObjectValue parse(EntityObject entityObject, String str, String entityValueName)
			throws EntityFormatException {
		EntityObjectValue entityObjectValue = new EntityObjectValue();
		entityObjectValue.setEntityObject(entityObject);
		entityObjectValue.setEntityValueName(entityValueName);
		
		Map<String,Object> valueMap = new HashMap<String,Object>();//此Map中值没有层级关系
		Map<String,Object> jsonValueMap = new HashMap<String,Object>();//此map中值有层次关系
		parseValue(entityObject,jsonValueMap,valueMap,str);
		
		entityObjectValue.setJsonValue(JsonUtils.collectToString(jsonValueMap));
		entityObjectValue.setValueMap(valueMap);
		return entityObjectValue;
	}

	@SuppressWarnings("unchecked")
	private void parseValue(EntityObject entityObject, Map<String, Object> jsonValueMap,Map<String,Object> valueMap, String str) {
		List<EntityProperty> entityProperties = entityObject.getProperties();
		if(entityProperties == null) {
			return;
		}
		Map<String,Object> jsonMap = null;
		try{
			jsonMap = JsonUtils.stringToMap(str);
		} catch(Exception e) {
			return;
		}
		for(EntityProperty entityProperty : entityProperties) {
			if(entityProperty == null){
				continue;
			}
			String propertyName = entityProperty.getPropertyName();
			String type = entityProperty.getEntityType().getType();
			Object value = jsonMap.get(propertyName);
			if(EntityObject.ENTITY_TYPE_USER.equals(type)) {
				Map<String,Object> map = new HashMap<String,Object>();
				jsonValueMap.put(propertyName, map);
				if(value != null){
					parseValue(entityProperty.getEntityType(),map,valueMap,value.toString());
				}
			}
			jsonValueMap.put(propertyName, value);
			
			if(EntityObject.ENTITY_TYPE_INNER.equals(type)){
				String propertyAlias = entityProperty.getPropertyAlias();
				if(propertyAlias == null || propertyAlias.isEmpty()){
					valueMap.put(propertyName, value);
				} else {
					valueMap.put(propertyAlias, value);
				}
			}
			
		}
		
	}

	

}
