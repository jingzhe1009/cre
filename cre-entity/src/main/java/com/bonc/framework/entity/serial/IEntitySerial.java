package com.bonc.framework.entity.serial;

import com.bonc.framework.entity.vo.EntityObjectValue;

/**
 * 实体序列化接口，将实体序列化成json、xml等。
 * @author qxl
 * @date 2018年3月12日 上午9:34:56
 * @version 1.0
 */
public interface IEntitySerial {

	/**
	 * 将实体对象序列化成json对象
	 * @param entityObject
	 * @return
	 */
	String toJson(EntityObjectValue entityObjectValue);
	
	/**
	 * 将实体对象序列化成xml
	 * @param entityObject
	 * @return
	 */
	String toXml(EntityObjectValue entityObjectValue);
	
}
