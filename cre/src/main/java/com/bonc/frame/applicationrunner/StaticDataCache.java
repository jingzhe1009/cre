package com.bonc.frame.applicationrunner;

import com.bonc.frame.entity.staticdata.StaticDataVo;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/5/15 11:12
 */
@Component
public class StaticDataCache {

    private Map<String, List<StaticDataVo>> cache;//数据缓存

    private StaticDataCache() {
        this.cache = new HashMap<>();
    }

    public void toImmutable() {
        this.cache = ImmutableMap.copyOf(this.cache);
    }

    public void put(String key, List<StaticDataVo> val) {
        cache.put(key, val);
    }

    public List<StaticDataVo> getStaticDataVos(String cacheKey) {
        return cache.get(cacheKey);
    }

    public String getDataValueByKey(String cacheKey, String dataKey) {
        List<StaticDataVo> list = cache.get(cacheKey);
       return getDataVoValue(list, dataKey);
    }

    public String getDataVoValue(List<StaticDataVo> list, String dataKey) {
        for (StaticDataVo data : list) {
            if (data.getKey().equals(dataKey)) {
                return data.getValue();
            }
        }
        return "";
    }

    public StaticDataVo getStaticDataVoByKey(String cacheKey, String dataKey) {
        List<StaticDataVo> list = cache.get(cacheKey);
        for (StaticDataVo dic : list) {
            if (dic.getKey().equals(dataKey)) {
                return dic;
            }
        }
        return null;
    }

}
