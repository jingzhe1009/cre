package com.bonc.framework.api.builder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bonc.framework.api.core.IRuleApi;
import com.bonc.framework.api.exception.ApiClassException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractApiBuilder implements IApiBuilder{

	private static final Log LOG = LogFactory.getLog(AbstractApiBuilder.class);
	private final static String apiSource = "api_source.properties";
	
	protected List<Map<String, Object>> contextList;
	
	protected Map<String, Object> context;
	protected static Map<String, String> ruleApiMapper = new ConcurrentHashMap<String, String>();
	
	@Override
	public IApiBuilder buildAllRuleApiMapper() throws ApiClassException{
		Properties prop = resourceType2ClassMap();
		Set<Object> keys = prop.keySet();//返回属性key的集合
	    for (Object key : keys) {
	    	String classPath = prop.get(key).toString();
			ruleApiMapper.put((String)key, classPath);
			if (LOG.isDebugEnabled()) {
				LOG.debug(key.toString() + "=" + prop.get(key));
			}
	    }
	    return this;
	}
	
	@Override
	public abstract IApiBuilder setContexts(List<Map<String, Object>> params);

	@Override
	public abstract IRuleApi buildRuleApi() throws ApiClassException;
	
	private Properties resourceType2ClassMap(){
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(apiSource);
		Properties prop = new Properties();
		try {
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	    return prop;
	}
	
}
