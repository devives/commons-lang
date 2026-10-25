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
package com.devives.commons.lang.ref;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * <p>
 * Ленивая потокобезопасная ссылка.
 * </p>
 * Заменяет конструкцию:
 * <pre>{@code
 * private volatile T field_;
 * public T getField(){
 *     T val = field_;
 *     if (val == null) {
 *          synchronized (lock_) {
 *              val = field_;
 *              if (val == null) {
 *                  val = Objects.requireNonNull(initiator, "initiator").get();
 *              }
 *              field_ = val;
 *          }
 *     }
 *     return val;
 * }}</pre>
 *
 * @param <T> - Тип объекта, на который будет храниться ссылка.
 */
public final class SynchronizedLazyRef<T> extends LazyRef<T> {
    private final Object mutex_ = new Object();
    private volatile T val_;

    /**
     * Используется фабрикой {@link LazyRef#ofSynchronized()}, если планируется использовать метод
     * {@link #getOrCreate(Supplier)}.
     */
    SynchronizedLazyRef() {
        this(null);
    }

    /**
     * Используется фабрикой {@link LazyRef#ofSynchronized(Supplier)}, если планируется использовать метод
     * {@link #get()}.
     *
     * @param initiator Будет вызван при первом обращении к методу {@link #get()}.
     */
    SynchronizedLazyRef(Supplier<T> initiator) {
        super(initiator);
    }

    @Override
    public T getOrCreate(Supplier<T> initiator) {
        T val = val_;
        if (val == null) {
            synchronized (mutex_) {
                val = val_;
                if (val == null) {
                    val = Objects.requireNonNull(initiator, "initiator").get();
                }
                val_ = val;
            }
        }
        return val;
    }

    @Override
    public boolean isPresent() {
        return val_ != null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Выполняется под той же блокировкой, что и инициализация, поэтому не пересекается
     * с выполняющимся в другом потоке инициализатором: значение будет установлено до или после него.
     */
    @Override
    public void set(T value) {
        synchronized (mutex_) {
            val_ = value;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Выполняется под той же блокировкой, что и инициализация, поэтому не пересекается
     * с выполняющимся в другом потоке инициализатором: сброс произойдёт до или после него.
     */
    @Override
    public void reset() {
        synchronized (mutex_) {
            val_ = null;
        }
    }

}
