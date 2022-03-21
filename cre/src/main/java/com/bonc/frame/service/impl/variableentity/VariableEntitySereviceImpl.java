package com.bonc.frame.service.impl.variableentity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.variableentity.VariableEntity;
import com.bonc.frame.service.variableentity.VariableEntityService;
import com.bonc.frame.util.ResponseResult;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VariableEntitySereviceImpl implements VariableEntityService{

	private final String _MYBITSID_PREFIX = "com.bonc.frame.dao.variableentity.VariableEntityMapper.";
	@Autowired
	private DaoHelper daoHelper;
	
	@Override
	@Transactional
	public ResponseResult insertVariableEntity(VariableEntity variableEntity) {
		this.daoHelper.insert(_MYBITSID_PREFIX+"insertSelective", variableEntity);
		return ResponseResult.createSuccessInfo();
	}

	@Override
	@Transactional
	public ResponseResult deleteVariableEntity(String entityId) {
		this.daoHelper.delete(_MYBITSID_PREFIX+"deleteByEntityId", entityId);
		return ResponseResult.createSuccessInfo();
	}

}
