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
package com.devives.commons.lang.reflection;

import com.devives.commons.lang.ExceptionUtils;
import com.devives.commons.lang.function.ExceptionProcedure1;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Прокси класс для перехвата вызова метода {@code #close()}.
 * <p>
 * Применяется в пулах и менеджерах объектов, где необходимо регистрировать момент завершения использования объекта
 * для уменьшения счётчика использований и/или возврата объекта в пул.
 */
public class CloseInterceptor<T> implements InvocationHandler {

    private static final Map<Class<?>, Class<?>[]> KNOWN_INTERFACES = new ConcurrentHashMap<>();
    private final Object closeable_;
    private final ExceptionProcedure1<T> closeHandler_;

    /**
     * Создаёт экземпляр прокси объекта для {@link AutoCloseable} объекта.
     *
     * @param closeable    AutoCloseable объект
     * @param closeHandler Метод, вызываемый ВМЕСТО вызова оригинального метода {@link AutoCloseable#close()}.
     *                     В качестве аргумента метода передаётся ссылка на оригинальный {@link AutoCloseable} объект.
     *                     При необходимости, требуется явно вызывать оригинальный метод {@link AutoCloseable#close()}.
     * @param <T>          тип класса декорируемого объекта.
     * @return прокси объект
     */
    public static <T> T build(T closeable, ExceptionProcedure1<T> closeHandler) {
        try {
            Objects.requireNonNull(closeable, "closeable");
            Objects.requireNonNull(closeHandler, "closeHandler");
            closeable.getClass().getMethod("close");
            return (T) Proxy.newProxyInstance(closeable.getClass().getClassLoader(),
                    getImplementedInterfaces(closeable), new CloseInterceptor<T>(closeable, closeHandler));
        } catch (Exception e) {
            throw ExceptionUtils.asUnchecked(e);
        }
    }

    /**
     * Возвращает экземпляр оригинального, декорированного, AutoCloseable объекта.
     *
     * @param proxyInstance Прокси-объект
     * @param <T>           Класс
     * @return Оригинальный объект
     */
    public static <T> T getCloseable(T proxyInstance) {
        Objects.requireNonNull(proxyInstance, "proxyInstance");
        if (Proxy.isProxyClass(proxyInstance.getClass())) {
            throw new RuntimeException("'proxyInstance' must be an instance of `Proxy` class.");
        }
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(proxyInstance);
        if (invocationHandler instanceof CloseInterceptor) {
            return (T) ((CloseInterceptor) invocationHandler).closeable_;
        } else {
            throw new RuntimeException("'invocationHandler' must be an instance of `CloseInterceptor` class.");
        }
    }

    private CloseInterceptor(T closeable, ExceptionProcedure1<T> closeHandler) {
        closeable_ = closeable;
        closeHandler_ = closeHandler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
            case "equals":
                if (proxy == args[0]) {
                    return true;
                } else if (args[0] != null && Proxy.isProxyClass(args[0].getClass())) {
                    Object otherHandler = Proxy.getInvocationHandler(proxy);
                    return closeable_.equals(otherHandler);
                } else {
                    return false;
                }
            case "close":
                closeHandler_.accept((T) this.closeable_);
                return null;
            default:
                return method.invoke(this.closeable_, args);
        }
    }

    /**
     * Возвращает массив интерфейсов, реализованных классом переданного объекта.
     *
     * @param object Объект
     * @return Массив интерфейсов
     */
    private static Class<?>[] getImplementedInterfaces(Object object) {
        Class<?> objectClass = object.getClass();
        Class<?>[] knownInterfaces = KNOWN_INTERFACES.get(objectClass);
        if (null != knownInterfaces) {
            return knownInterfaces;
        } else {
            Set<Class<?>> interfacesSet = new HashSet<>();
            addInterfaces(interfacesSet, objectClass);
            Class<?>[] interfaces = (Class<?>[]) interfacesSet.toArray(new Class[0]);
            KNOWN_INTERFACES.put(objectClass, interfaces);
            return interfaces;
        }
    }

    /**
     * Добавляет во множество все интерфейсы, реализованные классом.
     *
     * @param interfaces Множество
     * @param type       Класс
     */
    private static void addInterfaces(Set<Class<?>> interfaces, Class<?> type) {
        if (type != null) {
            Class<?>[] proxyInterfaces = type.getInterfaces();
            for (Class<?> proxyInterface : proxyInterfaces) {
                if (Modifier.isPublic(proxyInterface.getModifiers())) {
                    interfaces.add(proxyInterface);
                }
            }
            addInterfaces(interfaces, type.getSuperclass());
        }
    }
}
