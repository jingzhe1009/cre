package com.bonc.frame.service.variableentity;

import com.bonc.frame.entity.variableentity.VariableEntity;
import com.bonc.frame.util.ResponseResult;

public interface VariableEntityService {
	
	public ResponseResult insertVariableEntity(VariableEntity variableEntity);
	public ResponseResult deleteVariableEntity(String entityId);

}
