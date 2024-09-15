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


import java.util.Objects;
import java.util.function.Supplier;

/**
 * <p>
 * Ленивая ссылка.
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
 *
 * @param <T> - Тип объекта, на который будет храниться ссылка.
 */
public class LazyRef<T> {
    private T val_;
    private final Supplier<T> initiator_;

    /**
     * Используйте этот конструктор, если планируете использовать метод {@link #getOrCreate(Supplier)}.
     */
    public LazyRef() {
        this(null);
    }

    /**
     * Используйте этот конструктор, если планируете использовать метод get().
     *
     * @param initiator инициализатор значения.
     */
    public LazyRef(Supplier<T> initiator) {
        initiator_ = initiator;
    }

    /**
     * Вернёт ссылку на объект типа T, создав его при первом обращении к ленивой ссылке.
     *
     * @param initiator Будет вызван при первом обращении к ссылке.
     * @return Ссылки на объект типа <code>T</code>
     */
    public T getOrCreate(Supplier<T> initiator) {
        if (val_ == null) {
            val_ = Objects.requireNonNull(initiator, "initiator").get();
        }
        return val_;
    }

    public T get() {
        return getOrCreate(initiator_);
    }

    /**
     * Метод проверяет, наличие значения.
     *
     * @return {@code true}, если значение равно {@code null}, иначе {@code false}.
     */
    public boolean isPresent() {
        return val_ != null;
    }

    public void set(T value) {
        val_ = value;
    }

}
