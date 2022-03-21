package com.bonc.framework.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bonc.framework.entity.builder.DefaultEntityBuilder;
import com.bonc.framework.entity.builder.IEntityBuilder;
import com.bonc.framework.entity.builder.entity.EntityRelation;
import com.bonc.framework.entity.builder.entity.Variable;
import com.bonc.framework.entity.cache.EntityCache;
import com.bonc.framework.entity.format.DataTypeEnum;
import com.bonc.framework.entity.format.EntityFormatFactory;
import com.bonc.framework.entity.format.IEntityFormat;
import com.bonc.framework.entity.serial.DefaultEntitySerial;
import com.bonc.framework.entity.serial.IEntitySerial;
import com.bonc.framework.entity.vo.EntityObject;
import com.bonc.framework.entity.vo.EntityObjectValue;

/**
 * 实体引擎
 * @author qxl
 * @date 2018年3月12日 上午10:03:39
 * @version 1.0
 */
public class EntityEngine implements Serializable {
	private static final long serialVersionUID = -2022428838808541687L;

	private Log log = LogFactory.getLog(getClass());
	
	private IEntityBuilder entityBuilder;//实体构造器
	
	private boolean isBuildedAll = false;//是否已构建
	
	private EntityCache entityCache;//实体对象缓存
	
	public EntityEngine() {
		entityBuilder = new DefaultEntityBuilder();
		entityCache = new EntityCache();
	}
	
	
	/**
	 * 设置实体构造器
	 * @param entityBuilder
	 */
	public void setEntityBuilder(IEntityBuilder entityBuilder){
		this.entityBuilder = entityBuilder;
	}
	
	
	/**
	 * 构造所有的实体对象
	 * @throws Exception
	 */
	public void buildAllEntity(List<Variable> variableList,List<EntityRelation> entityRelList) throws Exception {
		if(isBuildedAll) {
			return;
		}
		try {
			entityBuilder.buildAll(entityCache,variableList,entityRelList);
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
		isBuildedAll = true;
	}

	/**
	 * 将给定的字符串（json/xml）转换成指定的实体类型
	 * @param entityName
	 * @param str
	 * @param dataType
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> parseToMap(String entityName,String str,DataTypeEnum dataType) 
			throws Exception{
		if(!isBuildedAll) {
			throw new Exception("The entity is not build,please build entity first.");
		}
		IEntityFormat entityFormat = EntityFormatFactory.getEntityFormat(dataType);
		try {
			EntityObject entityObject = entityCache.getEntityObject(entityName);
			EntityObjectValue entityObjectValue = entityFormat.parseToEntity(entityObject, str);
			if(entityObjectValue == null) {
				return null;
			}
			return entityObjectValue.getValueMap();
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 获取数据格式化接口
	 * @param dataType
	 * @return
	 */
	public IEntityFormat getEntityFormat(DataTypeEnum dataType) {
		return EntityFormatFactory.getEntityFormat(dataType);
	}
	
	/**
	 * 获取实体序列化接口
	 * @return
	 */
	public IEntitySerial getEntitySerial() {
		return new DefaultEntitySerial();
	}
	
}
