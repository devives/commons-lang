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
import com.devives.commons.util.usage.UsageCounter;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;

/**
 * Базовый класс для директоров отложенного закрытия объекта.
 * <p>
 * Принимает в качестве аргумента конструктора обработчик закрытия {@link LazyCloseHandler}, который выполняет
 * закрытие объекта, делегирующего управление своим закрытием, и задаёт параметры закрытия.
 */
public abstract class LazyClosingDirectorBase implements UsageCounter, Serializable {
    private static final long serialVersionUID = 1L;
    protected static final long OPENED = 0;
    /**
     * Фьючерс закрытия объекта.
     */
    protected transient final CompletableFuture<Void> lazyCloseFuture_ = new CompletableFuture<>();
    /**
     * Обработчик будет вызван при выполнении условия: счётчик использований равен "0" и вызван метод {@link #closeAsync()}.
     */
    protected final LazyCloseHandler closeHandler_;
    /**
     * Флаг указывает что объект находится в состоянии отложенного закрытия. Как только будут закрыты все ссылки
     * на объект, он будет закрыт.
     */
    protected long closeTimeStamp_ = OPENED;
    /**
     * Счётчик использований.
     */
    protected int usageCounter_ = 0;
    /**
     * Поток, выполняющий {@link LazyCloseHandler#lazyClose()}, или {@code null}, если закрытие не выполняется.
     */
    protected transient Thread closingThread_;
    /**
     * Флаг указывает что закрытие объекта завершено (успешно или с ошибкой).
     */
    protected boolean closeCompleted_ = false;

    /**
     * @param closeHandler Обработчик закрытия объекта, делегирующего управление закрытием.
     */
    protected LazyClosingDirectorBase(LazyCloseHandler closeHandler) {
        closeHandler_ = Objects.requireNonNull(closeHandler, "closeHandler");
    }

    /**
     * Возвращает фьючерс, указывающий на факт закрытия объекта.
     *
     * @return Фьючерс
     */
    public Future<Void> getLazyCloseFuture() {
        return lazyCloseFuture_;
    }

    /**
     * Возвращает время начала отложенного закрытия.
     *
     * @return {@code -1}, если объект не помечен к закрытию, иначе число миллисекунд.
     */
    public long getLazyCloseTimeMills() {
        return closeTimeStamp_;
    }

    /**
     * Возвращает значение флага, указывающего на необходимость закрытия объекта после уменьшения числа
     * использований до "0".
     *
     * @return true, если объект предназначен к закрытию, иначе false.
     */
    public boolean isLazyClose() {
        return closeTimeStamp_ != OPENED;
    }

    /**
     * Возвращает число использований.
     *
     * @return число использований
     */
    public int getUsageCount() {
        return usageCounter_;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public int incUsageCount() {
        if (canAcquire()) {
            return ++usageCounter_;
        } else {
            throw new RuntimeException("Can't acquire closed object.");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public int decUsageCount() {
        if (usageCounter_ == 0) {
            throw new RuntimeException("Usage counter becomes below zero.");
        }
        final int usages = --usageCounter_;
        if (isCloseRequired()) {
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
        if (closeTimeStamp_ == OPENED) {
            closeTimeStamp_ = System.currentTimeMillis();
            needClose = isCloseRequired();
        }
        if (needClose) {
            this.doLazyClose();
        }
        return lazyCloseFuture_;
    }

    /**
     * Проверяет, может ли текущий поток увеличить счётчик использований.
     * <p>
     * Захват разрешён, если объект не помечен к закрытию, либо если объект ещё используется. Во время выполнения
     * {@link LazyCloseHandler#lazyClose()} захват разрешён только выполняющему закрытие потоку и только если
     * {@link LazyCloseHandler#isReentrantUsageAllowed()} возвращает {@code true}.
     * <p>
     * Наследники, синхронизирующие доступ к полям, должны вызывать метод под своей блокировкой.
     *
     * @return true, если захват разрешён, иначе false.
     */
    protected boolean canAcquire() {
        // Allow increment usages if `closeAsync()` was called but `usageCounter_ > 0`.
        return closeTimeStamp_ == OPENED
                || (usageCounter_ > 0 && !closeCompleted_)
                || (closingThread_ == Thread.currentThread() && closeHandler_.isReentrantUsageAllowed());
    }

    /**
     * Проверяет, нужно ли запустить закрытие объекта.
     * <p>
     * Наследники, синхронизирующие доступ к полям, должны вызывать метод под своей блокировкой.
     *
     * @return true, если объект помечен к закрытию, не используется, и закрытие ещё не выполняется и не выполнено.
     */
    protected boolean isCloseRequired() {
        return usageCounter_ == 0 && closeTimeStamp_ != OPENED && closingThread_ == null && !closeCompleted_;
    }

    /**
     * Отмечает начало закрытия объекта текущим потоком.
     */
    protected void beginLazyClose() {
        closingThread_ = Thread.currentThread();
    }

    /**
     * Отмечает окончание закрытия объекта.
     *
     * @return число использований, оставшихся после закрытия.
     */
    protected int endLazyClose() {
        closingThread_ = null;
        closeCompleted_ = true;
        return usageCounter_;
    }

    /**
     * Выполняет закрытие объекта в текущем потоке и завершает фьючерс закрытия.
     */
    protected void doLazyClose() {
        beginLazyClose();
        Throwable error = null;
        try {
            closeHandler_.lazyClose();
        } catch (Throwable e) {
            error = e;
        }
        final int usages = endLazyClose();
        if (error != null) {
            lazyCloseFuture_.completeExceptionally(error);
        } else if (usages != 0) {
            lazyCloseFuture_.completeExceptionally(
                    new IllegalStateException("Usages acquired during closing were not released: " + usages));
        } else {
            lazyCloseFuture_.complete(null);
        }
    }

    /**
     * Обработчик отложенного закрытия объекта, управляемого {@link LazyClosingDirectorBase}.
     * <p>
     * Настройки обработчика читаются директором в момент захвата и закрытия объекта, а не в конструкторе директора.
     */
    public interface LazyCloseHandler {

        /**
         * Выполняет закрытие объекта.
         *
         * @throws Exception если закрытие завершилось ошибкой.
         */
        void lazyClose() throws Exception;

        /**
         * Разрешает потоку, выполняющему {@link #lazyClose()}, увеличивать счётчик использований закрываемого объекта.
         * <p>
         * Другие потоки по-прежнему не могут захватить закрываемый объект. Все захваты, сделанные во время закрытия,
         * должны быть освобождены до выхода из {@link #lazyClose()}, иначе фьючерс закрытия завершится
         * {@link IllegalStateException}.
         *
         * @return {@code false} по умолчанию.
         */
        default boolean isReentrantUsageAllowed() {
            return false;
        }

        /**
         * Создаёт обработчик с настройками по умолчанию.
         *
         * @param closeDelegate Ссылка на метод объекта, делегирующего управление закрытием.
         * @return обработчик.
         */
        static LazyCloseHandler of(FailableProcedure closeDelegate) {
            Objects.requireNonNull(closeDelegate, "closeDelegate");
            return closeDelegate::accept;
        }
    }
}
