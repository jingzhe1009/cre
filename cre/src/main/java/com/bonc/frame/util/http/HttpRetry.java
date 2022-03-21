package com.bonc.frame.util.http;

import com.bonc.frame.util.retry.AbstractRetryLoop;
import com.bonc.frame.util.retry.RetryPolicy;

import java.io.IOException;

/**
 * @author yedunyao
 * @since 2020/9/21 12:15
 */
public class HttpRetry extends AbstractRetryLoop {

    public HttpRetry(RetryPolicy retryPolicy) {
        super(retryPolicy);
    }

    @Override
    public boolean shouldRetry(Throwable t) {
        return false;
    }

    @Override
    public boolean isRetryException(Throwable exception) {
        if (exception instanceof IOException) {
            return true;
        }
        return false;
    }
}
