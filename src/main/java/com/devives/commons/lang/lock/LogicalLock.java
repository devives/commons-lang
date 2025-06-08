/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.devives.commons.lang.lock;


import com.devives.commons.lang.function.FailableFunction;
import com.devives.commons.lang.function.FailableProcedure;

/**
 * The class implements the functionality of a logical lock based on a lock counter.
 * The implementation is not thread-safe and is not intended for use in a multi-threaded environment.
 */
public class LogicalLock implements AutoLock {
    private long lockCount_ = 0;

    /**
     * Increases the lock counter.
     *
     * @return {@link AutoCloseable} to use the lock in a construction:
     * <pre>{@code
     * try (AutoCloseable ignored = getLock().lock()) {
     *     // do work
     * }
     * }</pre>
     */
    public AutoCloseable lock() {
        lockCount_++;
        return this::unlock;
    }

    /**
     * Decreases the lock counter.
     */
    public void unlock() {
        if (lockCount_ == 0)
            throw new RuntimeException("Is not locked before.");
        --lockCount_;
    }

    /**
     * Checks if the lock is not in use.
     *
     * @return true if the lock is not in use, false otherwise.
     */
    public boolean notLocked() {
        return lockCount_ == 0;
    }

    /**
     * Checks if the lock is currently in use.
     *
     * @return true if the lock is in use, false otherwise.
     */
    public boolean isLocked() {
        return lockCount_ != 0;
    }

    /**
     * Method executes the passed lambda expression if the lock is not set.
     * The lock is set before execution.
     * Thus, it is possible to guarantee the one-time execution of the lambda expression.
     *
     * @param proc The lambda expression to be executed
     * @throws Exception Exception thrown from
     */
    public void ifNotLockedLockAndCall(FailableProcedure proc) throws Exception {
        if (notLocked()) {
            try (AutoCloseable ignore = this.lock()) {
                proc.accept();
            }
        }
    }

    /**
     * Increases the lock counter and executes an anonymous method.
     *
     * @param proc anonymous method.
     * @throws Exception exception thrown from anonymous method.
     */
    public void lockAndCall(FailableProcedure proc) throws Exception {
        try (AutoCloseable ignore = this.lock()) {
            proc.accept();
        }
    }

    /**
     * Increases the lock counter and executes an anonymous method.
     *
     * @param func anonymous method.
     * @param <R>  type or anonymous method result.
     * @return anonymous method result.
     * @throws Exception exception thrown from anonymous method.
     */
    public <R> R lockAndCall(FailableFunction<R> func) throws Exception {
        try (AutoCloseable ignore = this.lock()) {
            return func.apply();
        }
    }

}