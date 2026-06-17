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
package com.devives.commons.lang;

import com.devives.commons.lang.function.FailableProcedure;

import java.util.concurrent.CompletionStage;

/**
 * Класс реализует функциональность подсчёта использований и отложенного закрытия объекта.
 * <p>
 * Принимает в качестве аргумента конструктора, ссылку на метод объекта, делегирующего управление своим закрытием.
 */
public final class SynchronizedLazyClosingDirector extends LazyClosingDirectorBase {
    private static final long serialVersionUID = 1L;
    /**
     * Синхронизирует доступ к полям {@link #closeTimeStamp_} и {@link #usageCounter_}.
     */
    private final Object lock_ = new Object();

    /**
     * @param closeDelegate Ссылка на метод объекта, делегирующего управление закрытием.
     */
    public SynchronizedLazyClosingDirector(FailableProcedure closeDelegate) {
        super(closeDelegate);
    }

    /**
     * Возвращает время начала отложенного закрытия.
     *
     * @return {@code -1}, если объект не помечен к закрытию, иначе число миллисекунд.
     */
    public long getLazyCloseTimeMills() {
        synchronized (lock_) {
            return closeTimeStamp_;
        }
    }

    /**
     * Возвращает значение флага, указывающего на необходимость закрытия объекта после уменьшения числа
     * использований до "0".
     *
     * @return true, если объект предназначен к закрытию, иначе false.
     */
    public boolean isLazyClose() {
        synchronized (lock_) {
            return closeTimeStamp_ != OPENED;
        }
    }

    /**
     * Возвращает число использований.
     *
     * @return число использований
     */
    public int getUsageCount() {
        synchronized (lock_) {
            return usageCounter_;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public int incUsageCount() {
        final int usages;
        synchronized (lock_) {
            // Allow increment usages if `closeAsync()` was called but `usageCounter_ > 0`.
            if (closeTimeStamp_ == OPENED || usageCounter_ > 0) {
                usages = ++usageCounter_;
            } else {
                throw new RuntimeException("Can't acquire closed object.");
            }
        }
        return usages;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public int decUsageCount() {
        final boolean needClose;
        final int usages;
        synchronized (lock_) {
            if (usageCounter_ == 0) {
                throw new RuntimeException("Usage counter becomes below zero.");
            }
            usages = --usageCounter_;
            needClose = usageCounter_ == 0 && closeTimeStamp_ != OPENED;
        }
        if (needClose) {
            this.doLazyClose();
        }
        return usages;
    }

    /**
     * Marks the object for lazy closing and returns a stage that completes when closing finishes.
     *
     * @return completion stage for the lazy close operation.
     */
    public synchronized CompletionStage<Void> closeAsync() {
        boolean needClose = false;
        synchronized (lock_) {
            if (closeTimeStamp_ == OPENED) {
                closeTimeStamp_ = System.currentTimeMillis();
                needClose = usageCounter_ == 0;
            }
        }
        if (needClose) {
            this.doLazyClose();
        }
        return lazyCloseFuture_;
    }

}
