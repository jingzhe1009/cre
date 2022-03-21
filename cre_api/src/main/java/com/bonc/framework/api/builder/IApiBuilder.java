package com.bonc.framework.api.builder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.bonc.framework.api.core.IRuleApi;
import com.bonc.framework.api.exception.ApiClassException;

public interface IApiBuilder extends Serializable{
	
	public IApiBuilder setContexts(List<Map<String, Object>> params);
	
	public IApiBuilder buildAllRuleApiMapper() throws ApiClassException;
	
	public IRuleApi buildRuleApi() throws ApiClassException;

}
