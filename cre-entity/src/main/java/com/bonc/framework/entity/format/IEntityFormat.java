package com.bonc.framework.entity.format;

import com.bonc.framework.entity.exception.EntityFormatException;
import com.bonc.framework.entity.vo.EntityObject;
import com.bonc.framework.entity.vo.EntityObjectValue;

/**
 * 实体格式化接口，将字符串(json/xml等)转换成实体对象.
 * @author qxl
 * @date 2018年3月12日 上午9:37:09
 * @version 1.0
 */
public interface IEntityFormat {
	
	/**
	 * 将字符串(json/xml等)转换成实体对象
	 * @param entityName		实体对象名
	 * @param str				要解析转换的字符串（json/xml等）
	 * @param entityValueName	转换后的实体值别名，可通过此别名获取缓存的实体值
	 * @param isCache			是否缓存此实体值
	 * @param isCover			是否覆盖已缓存的实体值
	 * @return
	 */
//	EntityObjectValue parseToEntity(EntityObject entityObject,String str,String entityValueName,
//			boolean isCache,boolean isCover) throws EntityFormatException;

	/**
	 * 将字符串(json/xml等)转换成实体对象
	 * @param entityName		实体对象名
	 * @param str				要解析转换的字符串（json/xml等）
	 * @return
	 */
	EntityObjectValue parseToEntity(EntityObject entityObject,String str) throws EntityFormatException;

}
