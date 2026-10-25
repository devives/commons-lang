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
 * Ленивая ссылка без синхронизации.
 * </p>
 * Заменяет конструкцию:
 * <pre>{@code
 * private T field_;
 * public T getField(){
 *     if (field_ == null){
 *         field_ = new T();
 *     }
 *     return field;
 * }
 * }</pre>
 * <p>
 * Не потокобезопасна. Для доступа из нескольких потоков используйте {@link SynchronizedLazyRef}.
 *
 * @param <T> - Тип объекта, на который будет храниться ссылка.
 */
public final class SimpleLazyRef<T> extends LazyRef<T> {
    private T val_;

    /**
     * Используется фабрикой {@link LazyRef#of()}, если планируется использовать метод {@link #getOrCreate(Supplier)}.
     */
    SimpleLazyRef() {
        this(null);
    }

    /**
     * Используется фабрикой {@link LazyRef#of(Supplier)}, если планируется использовать метод {@link #get()}.
     *
     * @param initiator инициализатор значения.
     */
    SimpleLazyRef(Supplier<T> initiator) {
        super(initiator);
    }

    @Override
    public T getOrCreate(Supplier<T> initiator) {
        if (val_ == null) {
            val_ = Objects.requireNonNull(initiator, "initiator").get();
        }
        return val_;
    }

    @Override
    public boolean isPresent() {
        return val_ != null;
    }

    @Override
    public void set(T value) {
        val_ = value;
    }

    @Override
    public void reset() {
        val_ = null;
    }

}
