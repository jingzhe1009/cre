package com.bonc.frame.module.lock.distributed.curator;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yedunyao
 * @since 2020/1/3 0:03
 */
@Component
public class LockInternalTransaction {

    @Transactional(rollbackFor = Exception.class)
    public Object addTransactToLockLogic(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return proceedingJoinPoint.proceed();
    }

}
