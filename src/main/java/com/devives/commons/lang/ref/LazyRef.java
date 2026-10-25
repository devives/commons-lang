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
 * <p>
 * Иерархия закрыта: наследовать класс можно только внутри пакета. Экземпляры создаются фабриками
 * {@link #of()}, {@link #of(Supplier)}, {@link #ofSynchronized()} и {@link #ofSynchronized(Supplier)}.
 *
 * <p><strong>Потокобезопасность.</strong></p>
 * Зависит от реализации:
 * <ul>
 *     <li>{@link SimpleLazyRef} ({@link #of()}, {@link #of(Supplier)}) &mdash; не потокобезопасна;
 *     <li>{@link SynchronizedLazyRef} ({@link #ofSynchronized()}, {@link #ofSynchronized(Supplier)}) &mdash;
 *     потокобезопасна, инициализатор вызывается не более одного раза до сброса.
 * </ul>
 *
 * @param <T> - Тип объекта, на который будет храниться ссылка.
 */
public abstract class LazyRef<T> {

    private final Supplier<T> initiator_;

    LazyRef(Supplier<T> initiator) {
        initiator_ = initiator;
    }

    /**
     * Создаёт ленивую ссылку без синхронизации и без инициализатора.
     * Значение создаётся методом {@link #getOrCreate(Supplier)}.
     *
     * @param <T> тип значения.
     * @return новая ссылка {@link SimpleLazyRef}.
     */
    public static <T> LazyRef<T> of() {
        return new SimpleLazyRef<>();
    }

    /**
     * Создаёт ленивую ссылку без синхронизации.
     *
     * @param initiator инициализатор значения, будет вызван при первом обращении к методу {@link #get()}.
     * @param <T>       тип значения.
     * @return новая ссылка {@link SimpleLazyRef}.
     */
    public static <T> LazyRef<T> of(Supplier<T> initiator) {
        return new SimpleLazyRef<>(initiator);
    }

    /**
     * Создаёт потокобезопасную ленивую ссылку без инициализатора.
     * Значение создаётся методом {@link #getOrCreate(Supplier)}.
     *
     * @param <T> тип значения.
     * @return новая ссылка {@link SynchronizedLazyRef}.
     */
    public static <T> LazyRef<T> ofSynchronized() {
        return new SynchronizedLazyRef<>();
    }

    /**
     * Создаёт потокобезопасную ленивую ссылку.
     *
     * @param initiator инициализатор значения, будет вызван при первом обращении к методу {@link #get()}.
     * @param <T>       тип значения.
     * @return новая ссылка {@link SynchronizedLazyRef}.
     */
    public static <T> LazyRef<T> ofSynchronized(Supplier<T> initiator) {
        return new SynchronizedLazyRef<>(initiator);
    }

    /**
     * Вернёт ссылку на объект типа T, создав его при первом обращении к ленивой ссылке.
     *
     * @param initiator Будет вызван при первом обращении к ссылке.
     * @return Ссылка на объект, созданный ранее или новый.
     * @throws NullPointerException если значения нет и {@code initiator} равен {@code null}.
     */
    public abstract T getOrCreate(Supplier<T> initiator);

    /**
     * Возвращает ссылку на объект типа T, создав его при первом обращении к ленивой ссылке с помощью переданной
     * при создании функции инициализатора.
     *
     * @return Значение
     * @throws NullPointerException если значения нет и ссылка создана без инициализатора.
     */
    public final T get() {
        return getOrCreate(initiator_);
    }

    /**
     * Метод проверяет наличие значения.
     *
     * @return {@code true}, если значение не равно {@code null}, иначе {@code false}.
     */
    public abstract boolean isPresent();

    /**
     * Устанавливает значение. Инициализатор при этом не вызывается.
     * Установка {@code null} равносильна {@link #reset()}.
     *
     * @param value новое значение.
     */
    public abstract void set(T value);

    /**
     * Сбрасывает значение. Следующее обращение к {@link #get()} или {@link #getOrCreate(Supplier)}
     * снова вызовет инициализатор.
     */
    public abstract void reset();

}
