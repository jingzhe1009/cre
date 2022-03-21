package com.bonc.framework.entity.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 实体对象类
 * @author qxl
 * @date 2018年3月12日 上午9:30:15
 * @version 1.0
 */
public class EntityObject implements Serializable {
	
	private static final long serialVersionUID = -8368542023237086956L;

	/** 实体类型-内置 */
	public static final String ENTITY_TYPE_INNER = "0";
	
	/** 实体类型-自定义 */
	public static final String ENTITY_TYPE_USER = "1";
	
	
	
	private String entityName;//实体名
	
	/**
	 * 0-内置，1-自定义
	 * 内置指：String、int、double等
	 * 自定义指：用户自定义，person、user等
	 */
	private String type;//实体类型
	
	private List<EntityProperty> properties;//实体属性
	
	public EntityObject(){
		this.type = ENTITY_TYPE_USER;
		this.properties = new ArrayList<EntityProperty>();
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<EntityProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<EntityProperty> properties) {
		this.properties = properties;
	}
	
	

}
