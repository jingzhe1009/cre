package com.bonc.frame.service.variableentityrelation;

import com.bonc.frame.entity.variableentityrelation.VariableEntityRelation;
import com.bonc.frame.util.ResponseResult;

public interface VariableEntityRelationService {
	
	public ResponseResult insertVariableEntityRelation(VariableEntityRelation variableEntityRelation);
	public ResponseResult updateVariableEntityRelation(VariableEntityRelation variableEntityRelation);
	public ResponseResult deleteVariableEntityRelation(VariableEntityRelation variableEntityRelation);

}
