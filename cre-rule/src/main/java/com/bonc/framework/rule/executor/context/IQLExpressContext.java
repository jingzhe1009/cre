package com.bonc.framework.rule.executor.context;

import java.util.Map;

/**
 * 规则执行上下文（包括参数等）接口
 *
 * @author 作者: jxw
 * @version 版本: 1.0
 * @date 创建时间: 2018年1月8日 下午2:31:47
 */
public interface IQLExpressContext<K, V> {

    /**
     * 根据名称从属性列表中提取属性值
     *
     * @param key 属性名称
     * @return
     */
    public V get(Object key);

    /**
     * @param name
     * @param object
     * @return
     */
    public V put(K name, V object);

    public void putAll(Map<? extends K, ? extends V> m);

}

