package com.bonc.framework.entity.format;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.bonc.framework.entity.exception.EntityFormatException;
import com.bonc.framework.entity.vo.EntityObject;
import com.bonc.framework.entity.vo.EntityObjectValue;
import com.bonc.framework.entity.vo.EntityProperty;
import com.bonc.framework.util.JsonUtils;

/**
 * 将xml格式的数据转换成实体数据
 * @author qxl
 * @date 2018年3月12日 上午9:47:07
 * @version 1.0
 */
public class XmlFormat extends AbstractEntityFormat {

	@Override
	public EntityObjectValue parse(EntityObject entityObject, String str, String entityValueName)
			throws EntityFormatException {
		EntityObjectValue entityObjectValue = new EntityObjectValue();
		entityObjectValue.setEntityObject(entityObject);
		entityObjectValue.setEntityValueName(entityValueName);
		
		Map<String,Object> valueMap = new HashMap<String,Object>();//此Map中值没有层级关系
		Map<String,Object> jsonValueMap = new HashMap<String,Object>();//此map中值有层次关系
		
		Document dom = null;
		Element root = null;
		try {
			dom = DocumentHelper.parseText(str);
			root = dom.getRootElement();
		} catch (DocumentException e) {
			throw new EntityFormatException("The str parse to xml error.");
		}
		
		parseValue(entityObject,jsonValueMap,valueMap,root);
		
		entityObjectValue.setJsonValue(JsonUtils.collectToString(jsonValueMap));
		entityObjectValue.setValueMap(valueMap);
		return entityObjectValue;
	}

	private void parseValue(EntityObject entityObject, Map<String, Object> jsonValueMap, Map<String, Object> valueMap,
			Element node) {
		List<EntityProperty> entityProperties = entityObject.getProperties();
		if(entityProperties == null) {
			return;
		}
		
		for(EntityProperty entityProperty : entityProperties) {
			if(entityProperty == null){
				continue;
			}
			String propertyName = entityProperty.getPropertyName();
			String type = entityProperty.getEntityType().getType();
			String value = node.element(propertyName).getTextTrim();
			if(EntityObject.ENTITY_TYPE_USER.equals(type)) {
				Map<String,Object> map = new HashMap<String,Object>();
				jsonValueMap.put(propertyName, map);
				if(value != null){
					parseValue(entityProperty.getEntityType(),map,valueMap,node.element(propertyName));
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
