package com.bonc.framework.entity.builder;

import java.io.Serializable;
import java.util.List;

import com.bonc.framework.entity.builder.entity.EntityRelation;
import com.bonc.framework.entity.builder.entity.Variable;
import com.bonc.framework.entity.cache.EntityCache;
import com.bonc.framework.entity.exception.BuildEntityException;
import com.bonc.framework.entity.vo.EntityObject;

/**
 * 实体对象构造器接口
 * @author qxl
 * @date 2018年3月13日 下午3:02:41
 * @version 1.0
 */
public interface IEntityBuilder extends Serializable {
	
	/**
	 * 批量构造实体对象
	 * @return
	 * @throws BuildEntityException
	 */
	List<EntityObject> buildAll(EntityCache cache,List<Variable> variableList,List<EntityRelation> entityRelList) throws BuildEntityException;
	
	/**
	 * 构造单个实体对象
	 * @return
	 * @throws BuildEntityException
	 */
	EntityObject buildEntity(EntityCache cache,List<Variable> variableList,List<EntityRelation> entityRelList) throws BuildEntityException;

}
