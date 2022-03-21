package com.bonc.frame.module.aBTest.manager;

import com.bonc.frame.entity.aBTest.ABTest;

/**
 * @author yedunyao
 * @since 2020/9/23 18:34
 */
public interface ABTestManager {

    void init() throws Exception;

    boolean start(ABTest abTest) throws Exception;

    boolean stop(ABTest abTest) throws Exception;
}
