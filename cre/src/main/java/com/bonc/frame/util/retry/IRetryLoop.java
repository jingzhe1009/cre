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

/**
 * <p>Mechanism to perform an operation on Zookeeper that is safe against
 * disconnections and "recoverable" errors.</p>
 *
 * <p>
 * If an exception occurs during the operation, the RetryLoop will process it,
 * check with the current retry policy and either attempt to reconnect or re-throw
 * the exception
 * </p>
 *
 * Canonical usage:<br>
 * <pre>
 * RetryLoop retryLoop = new RetryLoop();
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
public interface IRetryLoop {


    /**
     * If true is returned, make an attempt at the operation
     *
     * @return true/false
     */
    public boolean shouldContinue();

    /**
     * Call this when your operation has successfully completed
     */
    public void markComplete();

    /**
     * Utility - return true if the given Zookeeper result code is retry-able
     *
     * @return true/false
     */
    public boolean shouldRetry(Throwable exception) ;

    /**
     * Utility - return true if the given exception is retry-able
     *
     * @param exception exception to check
     * @return true/false
     */
    public  boolean isRetryException(Throwable exception) ;

    /**
     * Pass any caught exceptions here
     *
     * @param exception the exception
     * @throws Exception if not retry-able or the retry policy returned negative
     */
    public void takeException(Exception exception) throws Exception ;
}
