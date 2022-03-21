package com.bonc.framework.entity.vo;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * @author qxl
 * @date 2018年3月12日 下午3:26:19
 * @version 1.0
 */
public class EntityObjectValue implements Serializable{
	private static final long serialVersionUID = 1523850806498051088L;

	private EntityObject entityObject;
	
	private String entityValueName;
	
	private String jsonValue;
	
	private Map<String,Object> valueMap;

	public EntityObject getEntityObject() {
		return entityObject;
	}

	public void setEntityObject(EntityObject entityObject) {
		this.entityObject = entityObject;
	}

	public String getEntityValueName() {
		return entityValueName;
	}

	public void setEntityValueName(String entityValueName) {
		this.entityValueName = entityValueName;
	}

	public String getJsonValue() {
		return jsonValue;
	}

	public void setJsonValue(String value) {
		this.jsonValue = value;
	}

	public Map<String, Object> getValueMap() {
		return valueMap;
	}

	public void setValueMap(Map<String, Object> valueMap) {
		this.valueMap = valueMap;
	}
	
	

}
