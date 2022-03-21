package com.bonc.frame.applicationrunner;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author yedunyao
 * @since 2020/7/8 10:11
 */
@Component
public class PermissibleDataCache {

    private Map<String, Set<Method>> cache;// <class, [m0, m1, ...]>

    private PermissibleDataCache() {
        this.cache = new HashMap<>();
    }

    public void toImmutable() {
        this.cache = ImmutableMap.copyOf(this.cache);
    }

    public void put(String className, Set<Method> methodNames) {
        cache.put(className, methodNames);
    }

    public Method get(String className, String methodName) {
        Set<Method> set = cache.get(className);
        if (set == null || set.isEmpty()) {
            return null;
        }
        for(Method m : set) {
            if (m.getName().equals(methodName)) {
                return m;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PermissibleDataCache{");
        sb.append("cache=").append(cache);
        sb.append('}');
        return sb.toString();
    }
}
