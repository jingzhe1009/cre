package com.bonc.framework.api.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuleApiCache {
	
	private static Map<String, Object> ruleApiCache = new ConcurrentHashMap<String, Object>();
	
	public static void put(String key,Object value){
		ruleApiCache.put(key, value);
	}
	
	public static Object get(String key){
		return ruleApiCache.get(key);
	}
	
	public static boolean containsKey(String key){
		return ruleApiCache.containsKey(key);
	}
	
	public static void clear(){
		ruleApiCache.clear();
	}

}
