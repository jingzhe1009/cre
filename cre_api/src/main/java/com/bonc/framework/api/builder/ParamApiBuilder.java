package com.bonc.framework.api.builder;

import java.util.List;
import java.util.Map;

import com.bonc.framework.api.RuleApiFacade;
import com.bonc.framework.api.core.IRuleApi;
import com.bonc.framework.api.exception.ApiClassException;

public class ParamApiBuilder extends AbstractApiBuilder{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1835931089142959398L;

	@Override
	public IApiBuilder setContexts(List<Map<String, Object>> params) {
		this.contextList = params;
		return this;
	}

	@Override
	public IRuleApi buildRuleApi() throws ApiClassException {
		for(Map<String, Object> context:this.contextList){
			String apiId = (String) context.get("apiId");
			String apiType = (String) context.get("apiType");
			IRuleApi ruleApi;
			try {
				ruleApi = (IRuleApi) Class.forName(ruleApiMapper.get(apiType)).newInstance();
			} catch (Exception e) {
				throw new ApiClassException("classpath:"+ruleApiMapper.get(apiType)+" to ruleApi failed", e);
			}

			if(ruleApi == null) throw new ApiClassException("apiType:"+apiType+" is not exits");
			ruleApi.setBuilder(this);
			ruleApi.setContext(context);
			RuleApiFacade.getInstance().put(apiId, ruleApi);
		}
		return null;
	}

}
