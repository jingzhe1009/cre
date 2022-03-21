package com.bonc.framework.entity.format;

/**
 * 格式化实体接口工厂
 * @author qxl
 * @date 2018年3月13日 下午3:52:36
 * @version 1.0
 */
public class EntityFormatFactory {

	/**
	 * 根据数据类型，返回不同的数据格式化实现类
	 * @param dataType
	 * @return
	 */
	public static IEntityFormat getEntityFormat(DataTypeEnum dataType) {
		switch(dataType) {
			case json:
				return new JsonFormat();
			case xml:
				return new XmlFormat();
			default:
				break; 
		}
		return null;
	}
	
}
