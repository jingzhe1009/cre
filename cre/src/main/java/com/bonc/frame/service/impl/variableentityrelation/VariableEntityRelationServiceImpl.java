package com.bonc.frame.service.impl.variableentityrelation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.variable.Variable;
import com.bonc.frame.entity.variableentityrelation.VariableEntityRelation;
import com.bonc.frame.service.variableentityrelation.VariableEntityRelationService;
import com.bonc.frame.util.ResponseResult;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VariableEntityRelationServiceImpl implements VariableEntityRelationService{

	private final String _MYBITSID_PREFIX = "com.bonc.frame.dao.variableentityrelation.VariableEntityRelationMapper.";
	
	@Autowired
	private DaoHelper daoHelper;

	@Override
	@Transactional
	public ResponseResult insertVariableEntityRelation(VariableEntityRelation variableEntityRelation) {
		this.daoHelper.insert(_MYBITSID_PREFIX+"insertSelective", variableEntityRelation);
		return ResponseResult.createSuccessInfo();
	}

	@Override
	@Transactional
	public ResponseResult updateVariableEntityRelation(VariableEntityRelation variableEntityRelation) {
		daoHelper.update(_MYBITSID_PREFIX + "updateByPrimaryKeySelective", variableEntityRelation);
		return ResponseResult.createSuccessInfo();
	}

	@Override
	@Transactional
	public ResponseResult deleteVariableEntityRelation(VariableEntityRelation variableEntityRelation) {
		daoHelper.delete(_MYBITSID_PREFIX + "deleteByPrimaryKey", variableEntityRelation);
		return ResponseResult.createSuccessInfo();
	}

}
