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
package com.devives.commons.lang.concurrent;

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
public class ConcurrentLazyRef<T> {
    private final Object lock_ = new Object();
    private final Supplier<T> initiator_;
    private volatile T val_;

    /**
     * Используйте этот конструктор, если планируете использовать метод {@link #getOrCreate(Supplier)}.
     */
    public ConcurrentLazyRef() {
        this(null);
    }

    /**
     * Используйте этот конструктор, если планируете использовать метод get().
     *
     * @param initiator Будет вызван при первом обращении к методу {@link #get()}.
     */
    public ConcurrentLazyRef(Supplier<T> initiator) {
        initiator_ = initiator;
    }

    /**
     * Вернёт ссылку на объект типа T, создав его при первом обращении к ленивой ссылке.
     *
     * @param initiator Будет вызван при первом обращении к ссылке.
     * @return Ссылки на объект, созданный ранее или новый.
     */
    public T getOrCreate(Supplier<T> initiator) {
        T val = val_;
        if (val == null) {
            synchronized (lock_) {
                val = val_;
                if (val == null) {
                    val = Objects.requireNonNull(initiator, "initiator").get();
                }
                val_ = val;
            }
        }
        return val;
    }

    /**
     * Возвращает ссылку на объект типа T, создав его при первом обращении к ленивой ссылке с помощью переданной
     * в конструктор функции инициализатора.
     *
     * @return Значение
     */
    public T get() {
        return getOrCreate(initiator_);
    }

    /**
     * @return {@code true}, если значение равно {@code null}.
     */
    public boolean isPresent() {
        return val_ != null;
    }

    /**
     * Устанавливает значение ссылки.
     *
     * @param val Значение
     */
    public void set(T val) {
        val_ = val;
    }
}
