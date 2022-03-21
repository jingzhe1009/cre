package com.bonc.frame.dao.variableentity;

import com.bonc.frame.entity.variableentity.VariableEntity;

public interface VariableEntityMapper {
    int insert(VariableEntity record);

    int insertSelective(VariableEntity record);
}