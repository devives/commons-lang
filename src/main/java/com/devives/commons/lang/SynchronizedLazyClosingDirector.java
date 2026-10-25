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

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

/**
 * Класс реализует функциональность подсчёта использований и отложенного закрытия объекта.
 * <p>
 * Принимает в качестве аргумента конструктора обработчик закрытия {@link SynchronizedLazyCloseHandler}.
 * Если обработчик задаёт исполнитель {@link SynchronizedLazyCloseHandler#getLazyCloseExecutor()}, закрытие
 * выполняется в нём: {@link #closeAsync()} возвращает ещё не завершённый stage. Пока задача закрытия ждёт
 * в очереди исполнителя, захват объекта запрещён; во время выполнения задачи повторный захват разрешён только потоку
 * исполнителя, выполняющему закрытие, и только при {@link LazyCloseHandler#isReentrantUsageAllowed()}.
 */
public final class SynchronizedLazyClosingDirector extends LazyClosingDirectorBase {
    private static final long serialVersionUID = 1L;
    /**
     * Синхронизирует доступ к полям {@link #closeTimeStamp_}, {@link #usageCounter_}, {@link #closingThread_}
     * и {@link #closeCompleted_}.
     */
    private final Mutex lock_ = new Mutex();

    /**
     * Создаёт распорядителя отложенного закрытия.
     *
     * @param closeHandler Обработчик закрытия объекта, делегирующего управление закрытием.
     */
    public SynchronizedLazyClosingDirector(SynchronizedLazyCloseHandler closeHandler) {
        super(closeHandler);
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
            if (canAcquire()) {
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
            needClose = isCloseRequired();
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
    public CompletionStage<Void> closeAsync() {
        boolean needClose = false;
        synchronized (lock_) {
            if (closeTimeStamp_ == OPENED) {
                closeTimeStamp_ = System.currentTimeMillis();
                needClose = isCloseRequired();
            }
        }
        if (needClose) {
            this.doLazyClose();
        }
        return lazyCloseFuture_;
    }

    @Override
    protected void beginLazyClose() {
        synchronized (lock_) {
            super.beginLazyClose();
        }
    }

    @Override
    protected int endLazyClose() {
        synchronized (lock_) {
            return super.endLazyClose();
        }
    }

    /**
     * Выполняет закрытие объекта в исполнителе обработчика, если он задан, иначе в текущем потоке.
     */
    @Override
    protected void doLazyClose() {
        final Optional<Executor> executor = ((SynchronizedLazyCloseHandler) closeHandler_).getLazyCloseExecutor();
        if (!executor.isPresent()) {
            super.doLazyClose();
        } else {
            try {
                executor.get().execute(super::doLazyClose);
            } catch (Throwable e) {
                endLazyClose();
                lazyCloseFuture_.completeExceptionally(e);
            }
        }
    }

    /**
     * Обработчик отложенного закрытия объекта, управляемого {@link SynchronizedLazyClosingDirector}.
     * <p>
     * Дополнительно к {@link LazyCloseHandler} позволяет выполнять закрытие в другом потоке.
     */
    public interface SynchronizedLazyCloseHandler extends LazyCloseHandler {

        /**
         * Исполнитель, в котором выполняется {@link #lazyClose()}.
         * <p>
         * Если исполнитель задан, {@link SynchronizedLazyClosingDirector#closeAsync()} и
         * {@link SynchronizedLazyClosingDirector#decUsageCount()} только ставят закрытие в очередь исполнителя
         * и возвращают управление, не дожидаясь его окончания.
         *
         * @return {@link Optional#empty()} по умолчанию: закрытие выполняется в потоке, который его инициировал.
         */
        default Optional<Executor> getLazyCloseExecutor() {
            return Optional.empty();
        }

        /**
         * Создаёт обработчик с настройками по умолчанию.
         *
         * @param closeDelegate Ссылка на метод объекта, делегирующего управление закрытием.
         * @return обработчик.
         */
        static SynchronizedLazyCloseHandler of(FailableProcedure closeDelegate) {
            Objects.requireNonNull(closeDelegate, "closeDelegate");
            return closeDelegate::accept;
        }
    }
}
