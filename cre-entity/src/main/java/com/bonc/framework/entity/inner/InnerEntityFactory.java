package com.bonc.framework.entity.inner;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bonc.framework.entity.vo.EntityObject;

/**
 * 
 * @author qxl
 * @date 2018年3月12日 下午6:04:55
 * @version 1.0
 */
public class InnerEntityFactory {
	
	private static Map<String,EntityObject> innerEntityMap;
	
	static {
		innerEntityMap = new ConcurrentHashMap<String,EntityObject>();
		
		StringEntity stringEntity = new StringEntity();
		stringEntity.setType(EntityObject.ENTITY_TYPE_INNER);
		innerEntityMap.put(InnerEntityNameEnum.name_string.toString(), stringEntity);
		
		IntEntity intEntity = new IntEntity();
		intEntity.setType(EntityObject.ENTITY_TYPE_INNER);
		innerEntityMap.put(InnerEntityNameEnum.name_int.toString(), intEntity);
		
		IntegerEntity integerEntity = new IntegerEntity();
		integerEntity.setType(EntityObject.ENTITY_TYPE_INNER);
		innerEntityMap.put(InnerEntityNameEnum.name_integer.toString(), integerEntity);
		
		DoubleEntity doubleEntity = new DoubleEntity();
		doubleEntity.setType(EntityObject.ENTITY_TYPE_INNER);
		innerEntityMap.put(InnerEntityNameEnum.name_double.toString(), doubleEntity);
		
		ArrayEntity arrayEntity = new ArrayEntity();
		arrayEntity.setType(EntityObject.ENTITY_TYPE_INNER);
		innerEntityMap.put(InnerEntityNameEnum.name_array.toString(), arrayEntity);
	}
	
	/**
	 * 根据内置实体名获取内置实体
	 * @param name
	 * @return
	 */
	public static EntityObject getInnerEntityByName(InnerEntityNameEnum name){
		if(name != null && innerEntityMap.containsKey(name.toString())) {
			return innerEntityMap.get(name.toString());
		}
		return null;
	}

}
