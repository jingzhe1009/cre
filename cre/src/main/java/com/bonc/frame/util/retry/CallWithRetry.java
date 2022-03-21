package com.bonc.frame.util.retry;

import org.apache.curator.utils.ThreadUtils;

import java.util.concurrent.Callable;

/**
 * @author yedunyao
 * @since 2020/9/21 11:02
 */
public class CallWithRetry {

    /**
     * Convenience utility: creates a retry loop calling the given proc and retrying if needed
     *
     * @param proc   procedure to call with retry
     * @param <T>    return type
     * @return procedure result
     * @throws Exception any non-retriable errors
     */
    public static <T> T callWithRetry(Callable<T> proc, IRetryLoop retryLoop) throws Exception {
        T result = null;
        while (retryLoop.shouldContinue()) {
            try {
                result = proc.call();
                retryLoop.markComplete();
            } catch (Exception e) {
                ThreadUtils.checkInterrupted(e);
                retryLoop.takeException(e);
            }
        }
        return result;
    }


}
