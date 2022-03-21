package com.bonc.framework.entity.vo;

import java.io.Serializable;

/**
 * 实体属性类
 * @author qxl
 * @date 2018年3月12日 上午9:31:10
 * @version 1.0
 */
public class EntityProperty implements Serializable{
	private static final long serialVersionUID = 2079808189184566910L;

	private String propertyName;//属性名
	
	private EntityObject entityType;//属性类型
	
	private String propertyAlias;//属性别名

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public EntityObject getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityObject entityObject) {
		this.entityType = entityObject;
	}

	public String getPropertyAlias() {
		return propertyAlias;
	}

	public void setPropertyAlias(String propertyAlias) {
		this.propertyAlias = propertyAlias;
	}
	
	

}
