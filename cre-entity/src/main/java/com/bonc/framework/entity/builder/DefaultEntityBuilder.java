package com.bonc.framework.entity.builder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bonc.framework.entity.builder.entity.EntityRelation;
import com.bonc.framework.entity.builder.entity.Variable;
import com.bonc.framework.entity.cache.EntityCache;
import com.bonc.framework.entity.exception.BuildEntityException;
import com.bonc.framework.entity.vo.EntityObject;
import com.bonc.framework.entity.vo.EntityProperty;
import com.bonc.framework.util.VariableTypeUtil;

/**
 * 
 * @author qxl
 * @date 2018年3月13日 下午3:17:52
 * @version 1.0
 */
public class DefaultEntityBuilder implements IEntityBuilder {
	private static final long serialVersionUID = -2531309991575791857L;

	@Override
	public List<EntityObject> buildAll(EntityCache cache,List<Variable> variableList,List<EntityRelation> entityRelList) throws BuildEntityException {
		build(cache,variableList,entityRelList);
		return null;
	}

	@Override
	public EntityObject buildEntity(EntityCache cache,List<Variable> variableList,List<EntityRelation> entityRelList) throws BuildEntityException {
		build(cache,variableList,entityRelList);
		return null;
	}
	
	private void build(EntityCache cache,List<Variable> variableList,List<EntityRelation> entityRelList) throws BuildEntityException {
		if(variableList == null){
			return;
		}
		//创建实体
		//已创建的实体记录，key-实体ID，value-实体对象
		Map<String,EntityObject> entityMap = new HashMap<String,EntityObject>();
		for(Variable variable : variableList){
			String entityId = variable.getEntityId();
			if(entityId == null || entityId.isEmpty()){
				continue;
			}
			String variableCode = variable.getVariableCode();
			EntityObject entityObject = new EntityObject();
			entityObject.setEntityName(variableCode);
			try {
				cache.cacheEntityObject(entityObject);
				entityMap.put(entityId, entityObject);
			} catch (Exception e) {
				throw new BuildEntityException(e);
			}
		}
		//给实体添加普通类型属性（除实体类型属性外）
		for(int i=variableList.size()-1;i>=0;i--){
			Variable variable = variableList.get(i);
			String entityId = variable.getEntityId();
			String typeId = variable.getTypeId();
			String propertyName = variable.getVariableCode();
			String aliasCode = variable.getAliasCode();
			if(entityId == null || entityId.isEmpty()){
				String belongEntityId = variable.getBelongEntityId();
				EntityObject entityObject = entityMap.get(belongEntityId);
				if(entityObject == null){
					continue;
				}
				List<EntityProperty> properties = entityObject.getProperties();
				EntityProperty prop = new EntityProperty();
				prop.setEntityType(VariableTypeUtil.vairableType2EntityObject(typeId));
				prop.setPropertyName(propertyName);
				prop.setPropertyAlias(aliasCode);
				properties.add(prop);
			}
		}
		
		//给实体添加实体类型属性
		boolean flag = true;
		Set<String> pidSet = new HashSet<String>();
		while(flag){
			pidSet.clear();
			//将所有的pid存到集合中
			for(EntityRelation entityRelation : entityRelList) {
				String entityPid = entityRelation.getEntityPid();
				if(entityPid != null && !entityPid.isEmpty()) {
					pidSet.add(entityPid);
				}
			}
			//构造所有的子实体
			for(int i=entityRelList.size()-1;i>=0;i--) {
				EntityRelation entityRelation = entityRelList.get(i);
				String entityId = entityRelation.getEntityId();
				String entityPid = entityRelation.getEntityPid();
				if(pidSet.contains(entityId)) {
					continue;
				}
				if(entityPid == null) {
					entityRelList.remove(i);
					continue;
				}
				EntityObject childEntity = entityMap.get(entityId);
				EntityObject parentEntity = entityMap.get(entityPid);
				List<EntityProperty> properties = parentEntity.getProperties();
				EntityProperty prop = new EntityProperty();
				prop.setEntityType(childEntity);
				prop.setPropertyName(childEntity.getEntityName());
				properties.add(prop);
				
				//移除已处理过的
				entityRelList.remove(i);
			}
			if(entityRelList.size() == 0){
				flag = false;
			}
		}
	}

}
