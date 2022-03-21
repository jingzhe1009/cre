package com.bonc.framework.util;

import com.bonc.framework.entity.inner.InnerEntityFactory;
import com.bonc.framework.entity.inner.InnerEntityNameEnum;
import com.bonc.framework.entity.vo.EntityObject;

/**
 * 变量的基本类型与实体的类型转换工具类
 * @author qxl
 * @date 2018年4月2日 下午3:06:43
 * @version 1.0
 */
public class VariableTypeUtil {

	/**
	 * 将变量类型转成内置基本实体类型
	 * @param typeId
	 * @return
	 */
	public static EntityObject vairableType2EntityObject(String typeId) {
		if(typeId == null || typeId.isEmpty()){//默认返回String类型
			return InnerEntityFactory.getInnerEntityByName(InnerEntityNameEnum.name_string);
		}
		InnerEntityNameEnum entityName = InnerEntityNameEnum.name_string;
		switch (typeId) {
			case "1":
				entityName = InnerEntityNameEnum.name_string;
				break;
			case "2":
				entityName = InnerEntityNameEnum.name_int;	
				break;
			case "3":
				
				break;
			case "4":
				entityName = InnerEntityNameEnum.name_double;
				break;
			case "5":
				entityName = InnerEntityNameEnum.name_array;
				break;
	
			default:
				break;
		}
		return InnerEntityFactory.getInnerEntityByName(entityName);
	}
	
}
