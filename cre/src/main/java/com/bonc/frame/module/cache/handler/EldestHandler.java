package com.bonc.frame.module.cache.handler;

import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/6/14 11:17
 */
public interface EldestHandler<K, V> {

    void handler(Map.Entry<K, V> eldest);

}
