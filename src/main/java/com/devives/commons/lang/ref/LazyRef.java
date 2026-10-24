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
 * <p><strong>Потокобезопасность.</strong></p>
 * Зависит от реализации:
 * <ul>
 *     <li>{@link UnsynchronizedLazyRef} ({@link #of()}, {@link #of(Supplier)}) &mdash; не потокобезопасна;
 *     <li>{@link SynchronizedLazyRef} ({@link #ofSynchronized()}, {@link #ofSynchronized(Supplier)}) &mdash;
 *     потокобезопасна, инициализатор вызывается не более одного раза до сброса.
 * </ul>
 *
 * @param <T> - Тип объекта, на который будет храниться ссылка.
 */
public interface LazyRef<T> {

    /**
     * Создаёт ленивую ссылку без синхронизации и без инициализатора.
     * Значение создаётся методом {@link #getOrCreate(Supplier)}.
     *
     * @param <T> тип значения.
     * @return новая ссылка {@link UnsynchronizedLazyRef}.
     */
    static <T> LazyRef<T> of() {
        return new UnsynchronizedLazyRef<>();
    }

    /**
     * Создаёт ленивую ссылку без синхронизации.
     *
     * @param initiator инициализатор значения, будет вызван при первом обращении к методу {@link #get()}.
     * @param <T>       тип значения.
     * @return новая ссылка {@link UnsynchronizedLazyRef}.
     */
    static <T> LazyRef<T> of(Supplier<T> initiator) {
        return new UnsynchronizedLazyRef<>(initiator);
    }

    /**
     * Создаёт потокобезопасную ленивую ссылку без инициализатора.
     * Значение создаётся методом {@link #getOrCreate(Supplier)}.
     *
     * @param <T> тип значения.
     * @return новая ссылка {@link SynchronizedLazyRef}.
     */
    static <T> LazyRef<T> ofSynchronized() {
        return new SynchronizedLazyRef<>();
    }

    /**
     * Создаёт потокобезопасную ленивую ссылку.
     *
     * @param initiator инициализатор значения, будет вызван при первом обращении к методу {@link #get()}.
     * @param <T>       тип значения.
     * @return новая ссылка {@link SynchronizedLazyRef}.
     */
    static <T> LazyRef<T> ofSynchronized(Supplier<T> initiator) {
        return new SynchronizedLazyRef<>(initiator);
    }

    /**
     * Вернёт ссылку на объект типа T, создав его при первом обращении к ленивой ссылке.
     *
     * @param initiator Будет вызван при первом обращении к ссылке.
     * @return Ссылка на объект, созданный ранее или новый.
     * @throws NullPointerException если значения нет и {@code initiator} равен {@code null}.
     */
    T getOrCreate(Supplier<T> initiator);

    /**
     * Возвращает ссылку на объект типа T, создав его при первом обращении к ленивой ссылке с помощью переданной
     * при создании функции инициализатора.
     *
     * @return Значение
     * @throws NullPointerException если значения нет и ссылка создана без инициализатора.
     */
    T get();

    /**
     * Метод проверяет наличие значения.
     *
     * @return {@code true}, если значение не равно {@code null}, иначе {@code false}.
     */
    boolean isPresent();

    /**
     * Устанавливает значение. Инициализатор при этом не вызывается.
     * Установка {@code null} равносильна {@link #reset()}.
     *
     * @param value новое значение.
     */
    void set(T value);

    /**
     * Сбрасывает значение. Следующее обращение к {@link #get()} или {@link #getOrCreate(Supplier)}
     * снова вызовет инициализатор.
     */
    void reset();

}
