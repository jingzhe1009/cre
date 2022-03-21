/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.bonc.frame.util.retry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * <p>Mechanism to perform an operation on Zookeeper that is safe against disconnections and "recoverable" errors.</p>
 * <p>
 * <p> If an exception occurs during the operation, the RetryLoop will process it, check with the current retry policy
 * and either attempt to reconnect or re-throw the exception </p>
 * <p>
 * Canonical usage:<br>
 * <pre>
 * RetryLoop retryLoop = client.newRetryLoop();
 * while ( retryLoop.shouldContinue() )
 * {
 *     try
 *     {
 *         // do your work
 *
 *         retryLoop.markComplete();
 *     }
 *     catch ( Exception e )
 *     {
 *         retryLoop.takeException(e);
 *     }
 * }
 * </pre>
 */
public abstract class AbstractRetryLoop implements IRetryLoop {
    protected boolean isDone = false;
    protected int retryCount = 0;

    private final Logger log = LogManager.getLogger(getClass());
    private final long startTimeMs = System.currentTimeMillis();
    private final RetryPolicy retryPolicy;

    private static final RetrySleeper sleeper = new RetrySleeper() {
        @Override
        public void sleepFor(long time, TimeUnit unit) throws InterruptedException {
            unit.sleep(time);
        }
    };

//    private final AtomicInteger tracer = new AtomicInteger(0);
    private int tracer = 0;

    /**
     * Returns the default retry sleeper
     *
     * @return sleeper
     */
    public static RetrySleeper getDefaultRetrySleeper() {
        return sleeper;
    }


    protected AbstractRetryLoop(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    /**
     * If true is returned, make an attempt at the operation
     *
     * @return true/false
     */
    @Override
    public boolean shouldContinue() {
        return !isDone;
    }

    /**
     * Call this when your operation has successfully completed
     */
    @Override
    public void markComplete() {
        isDone = true;
    }

    /**
     * Utility - return true if the given Zookeeper result code is retry-able
     *
     * @return true/false
     */
    public abstract boolean shouldRetry(Throwable t);

    /**
     * Utility - return true if the given exception is retry-able
     *
     * @param exception exception to check
     * @return true/false
     */
    public abstract boolean isRetryException(Throwable exception);

    /**
     * Pass any caught exceptions here
     *
     * @param exception the exception
     * @throws Exception if not retry-able or the retry policy returned negative
     */
    public void takeException(Exception exception) throws Exception {
        boolean rethrow = true;
        if (isRetryException(exception)) {
            if (log.isDebugEnabled()) {
                log.debug("重试操作，异常信息", exception);
            }

            if (retryPolicy.allowRetry(retryCount++, System.currentTimeMillis() - startTimeMs, sleeper)) {
                if (log.isDebugEnabled()) {
                    log.debug("重试操作，重试次数：" + tracer++);
                }
                rethrow = false;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("停止重试，重试次数：" + tracer++);
                }
            }
        }

        if (rethrow) {
            throw exception;
        }
    }
}
