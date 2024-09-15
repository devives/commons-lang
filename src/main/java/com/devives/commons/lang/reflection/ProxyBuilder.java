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

import java.lang.reflect.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Фабрика Proxy-объектов.
 * <p>
 * Создаёт экземпляр прокси-объекта, реализующего заглушки-методов требуемого ИНТЕРФЕЙСА.
 */
public class ProxyBuilder {

    /**
     * Создаёт экземпляр объекта, реализующего методы переданного интерфейса.
     *
     * @param classOfProxy класс создаваемого объекта
     * @param <T>          тип создаваемого объекта.
     * @return экземпляр прокси класса.
     */
    public static <T> T build(final Class<T> classOfProxy) {
        return build(classOfProxy, new Object());
    }

    /**
     * Создаёт экземпляр объекта, реализующего методы переданного интерфейса.
     *
     * @param classOfProxy класс создаваемого объекта
     * @param classOfStub  класс объекта-делегата, реализующего методы создаваемого класса.
     * @param <T>          тип создаваемого объекта.
     * @param <S>          тит объекта-делегата, реализующего методы создаваемого класса.
     * @return новый экземпляр прокси класса.
     */
    public static <T, S> T build(final Class<T> classOfProxy, final Class<S> classOfStub) {
        try {
            Object stub = classOfStub.getConstructors()[0].newInstance();
            return build(classOfProxy, stub);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Proxy object initialisation error", e);
        }
    }

    /**
     * Создаёт экземпляр объекта, реализующего методы переданного интерфейса.
     *
     * @param classOfProxy Класс интерфейса, для которого будет создаваться прокси-объект
     * @param stub         Экземпляр объекта, обладающего методами, сигнатуры которых совпадают с сигнатурами методов
     *                     интерфейса, требующих специфической реализации.
     * @param <T>          тип создаваемого объекта.
     * @return Прокси-объект реализующий требуемый интерфейс
     */
    @SuppressWarnings("unchecked")
    public static <T> T build(final Class<T> classOfProxy, final Object stub) {
        return build(classOfProxy, stub, Class::getMethod);
    }

    /**
     * Создаёт экземпляр объекта, реализующего методы переданного интерфейса.
     *
     * @param classOfProxy Класс интерфейса, для которого будет создаваться прокси-объект
     * @param stub         Экземпляр объекта, обладающего методами, сигнатуры которых совпадают с сигнатурами методов
     *                     интерфейса, требующих специфической реализации.
     * @param methodFinder Метод поиска подходящего для вызова метода экземпляра объекта {@code stub}.
     * @param <T>          тип создаваемого объекта.
     * @return Прокси-объект реализующий требуемый интерфейс
     */
    @SuppressWarnings("unchecked")
    public static <T> T build(final Class<T> classOfProxy, final Object stub, final MethodFinder methodFinder) {
        return (T) Proxy.newProxyInstance(classOfProxy.getClassLoader(),
                new Class[]{classOfProxy},
                new InvocationHandler() {

                    private final Object stub_ = stub;
                    private final Class<?> stubClass_ = stub.getClass();
                    private final Map<Method, StubMethod> methodMap_ = new ConcurrentHashMap<>();

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        try {
                            // Проверка вынесена из finally для минимизации кода и вероятности ошибки внутри finally.
                            final boolean isClose = method.getDeclaringClass().equals(AutoCloseable.class);
                            try {
                                if (method.getDeclaringClass().equals(Object.class)) {
                                    if (method.getName().equals("equals")) {
                                        if (proxy == args[0]) {
                                            return true;
                                        } else if (args[0] != null && Proxy.isProxyClass(args[0].getClass())) {
                                            Object otherStub = Proxy.getInvocationHandler(proxy);
                                            return stub_.equals(otherStub);
                                        } else {
                                            return false;
                                        }
                                    } else {
                                        return method.invoke(stub_, args);
                                    }
                                } else {
                                    return methodMap_.computeIfAbsent(method, key -> {
                                                try {
                                                    Method exactMethod = methodFinder.getMethod(stubClass_, method.getName(), method.getParameterTypes());
                                                    if (exactMethod == null) {
                                                        throw new NoSuchMethodException(method.getName());
                                                    }
                                                    if (!exactMethod.isAccessible()) {
                                                        exactMethod.setAccessible(true);
                                                    }
                                                    return new StubMethod(exactMethod, method.getReturnType(), methodFinder);
                                                } catch (NoSuchMethodException e) {
                                                    throw new UndeclaredThrowableException(e);
                                                }
                                            })
                                            .invoke(stub_, args);
                                }
                            } finally {
                                if (isClose) {
                                    methodMap_.clear();
                                }
                            }
                        } catch (IllegalAccessException e) {
                            // Если не обернуть отмеченное исключение в RuntimeException,
                            // java.lang.reflect.Proxy обернёт его в UndeclaredThrowableException,
                            // что скроет исходное "юзеро-читаемое" сообщение.
                            throw new UndeclaredThrowableException(e);
                        } catch (InvocationTargetException e) {
                            if (e.getCause() instanceof RuntimeException) {
                                throw e.getCause();
                            } else if (e.getCause() instanceof Exception) {
                                throw new UndeclaredThrowableException(e.getCause());
                            } else {
                                throw e.getCause();
                            }
                        }
                    }
                }
        );
    }

    private static final class StubMethod {
        /**
         * Ссылка на метод класса Stub-объекта
         */
        public final Method method;
        /**
         * Класс ожидаемого результата
         */
        private final Class<?> returnType;
        /**
         * Метод поиска подходящего для вызова метода класса.
         */
        private final MethodFinder methodFinder_;
        /**
         * Реальный результат, возвращённый Stub-методом. Его тип может не совпадать с {@link StubMethod#returnType}.
         */
        private Object rawResult;
        /**
         * Результат предыдущего вызова {@link StubMethod#invoke(Object, Object...)}, тип которого
         * совпадает с ожидаемым {@link StubMethod#returnType}.
         */
        private Object typedResult;

        public StubMethod(Method method, Class<?> returnType, MethodFinder methodFinder) {
            this.method = method;
            this.returnType = returnType;
            this.methodFinder_ = methodFinder;
        }

        public Object invoke(Object obj, Object... args) throws InvocationTargetException, IllegalAccessException {
            return castResult(method.invoke(obj, args));
        }

        /**
         * Stub-метод может вернуть объект, не поддерживающий ожидаемый интерфейс/класс. В этом случае создаю прокси объект.
         *
         * @param value Значение, возвращённое stub-методом.
         * @return Значение ожидаемого типа
         */
        private Object castResult(Object value) {
            synchronized (this) {
                if (value != null) {
                    //Сравнение на равенство ссылок.
                    if (value == rawResult) {
                        //Если вернулся тот же объект, что при предыдущем вызове, возвращаем тот же объект.
                        return typedResult;
                    } else if (returnType.isInstance(value)) {
                        rawResult = value;
                        typedResult = value;
                    } else if (returnType.isInterface()) {
                        // Тип rawResult не совпал с ожидаемым интерфейсом, создаём прокси-объект.
                        typedResult = ProxyBuilder.build(returnType, value, methodFinder_);
                        rawResult = value;
                        value = typedResult;
                    } else {
                        // Если ожидаемым результатом является примитив, рассчитываем на получение преобразуемого в прмитив значения.
                        // Если же классы не конвертируемы, то будет ошибка приведения, тут ничего не поделать.
                        rawResult = value;
                        typedResult = value;
                    }
                } else {
                    rawResult = null;
                    typedResult = null;
                }
            }
            return value;
        }
    }

    public static Class<?>[] getArgsClasses(Object[] args) {
        if (args == null) return null;
        Class<?>[] result = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = (args[i] != null) ? args[i].getClass() : null;
        }
        return result;
    }
}
