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

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Proxy object factory.
 * <p>
 * Creates an instance of a proxy object that implements stubs-methods of the required interfaces.
 */
public final class ProxyBuilder<T> {

    private final Class<?>[] interfacesOfProxy_;
    private final ClassLoader classLoader_;
    private Object target_;
    private MethodFinder methodFinder_;
    private Supplier<Map> methodMapFactory_;

    /**
     * Creates a proxy object builder.
     *
     * @param iface      the interface of the created object.
     * @param additional additional interfaces of the created object.
     * @param <T>        the type of the created object.
     * @return the proxy object builder.
     */
    public static <T> ProxyBuilder<T> forClasses(final Class<T> iface, final Class<?>... additional) {
        Objects.requireNonNull(iface, "iface");
        Class<?>[] classesOfProxy = new Class<?>[1 + additional.length];
        classesOfProxy[0] = iface;
        if (additional.length >= 0) {
            System.arraycopy(additional, 0, classesOfProxy, 1, additional.length);
        }
        Stream.of(classesOfProxy).toArray(Class[]::new);
        return new ProxyBuilder<T>(iface.getClassLoader(), classesOfProxy);
    }

    /**
     * Constructs the builder object
     *
     * @param classLoader    the class loader of the required interfaces.
     * @param interfacesOfProxy the classes of the required interfaces.
     */
    private ProxyBuilder(ClassLoader classLoader, Class<?>[] interfacesOfProxy) {
        classLoader_ = classLoader;
        interfacesOfProxy_ = interfacesOfProxy;
    }

    /**
     * Sets a reference to an object that has methods with signatures that match the signatures of the interface methods.
     * <pre>{@code
     * public interface SomeInterface {
     *     int foo();
     * }
     *
     * SomeInterface proxy = ProxyBuilder
     *   .forClasses(SomeInterface.class)
     *   .setTarget(new Object() {
     *       public int foo() {
     *           return 100500;
     *       }
     *   })
     *   .build();
     *
     * proxy.foo();
     * }</pre>
     * Example of using {@link TransparentDecorator}.
     * <pre>{@code
     * public interface SomeInterface {
     *     int foo();
     *     int doSome();
     * }
     *
     * public class SomeTarget {
     *     public final int foo() {
     *         return 100000;
     *     }
     *     public final int doSome() {
     *         return 100000;
     *     }
     * }
     *
     * SomeInterface proxy = ProxyBuilder
     *   .forClasses(SomeInterface.class)
     *   .setTarget(new TransparentDecorator(new SomeTarget()) {
     *       public int foo() {
     *           return 100500;
     *       }
     *   })
     *   .build();
     *
     * proxy.foo(); // Will invoke TransparentDecorator#foo()
     * proxy.doSome();  // Will invoke SomeTarget#doSome()
     * }</pre>
     * @param target the object implementing the interface methods
     * @return the current proxy object builder.
     */
    public ProxyBuilder<T> setTarget(Object target) {
        target_ = target;
        return this;
    }

    /**
     * Sets the method for finding the method suitable for calling the {@code target} object instance.
     * <p>
     * By default, {@link Class#getMethod(String, Class[])} is used.
     * @param methodFinder the method finder.
     * @return the current proxy object builder.
     */
    public ProxyBuilder<T> setMethodFinder(MethodFinder methodFinder) {
        methodFinder_ = methodFinder;
        return this;
    }

    /**
     * Sets the factory of the map that will be used to cache the links to the methods of the stub object.
     * <p>
     * By default, {@link HashMap} is used. If multiple threads are expected to call the proxy object methods,
     * you should use {@link ConcurrentHashMap} or another thread-safe map implementation.
     *
     * @param factory the map factory
     * @return the current proxy object builder.
     */
    public ProxyBuilder<T> setMethodMapFactory(Supplier<Map> factory) {
        methodMapFactory_ = factory;
        return this;
    }

    /**
     * Creates a new object that implements the required interfaces.
     *
     * @return the object that implements the required interfaces.
     */
    @SuppressWarnings("unchecked")
    public T build() {
        return (T) Proxy.newProxyInstance(
                classLoader_,
                interfacesOfProxy_,
                new InvocationHandlerImpl(
                        target_ != null ? target_ : new Object(),
                        methodMapFactory_ != null ? Objects.requireNonNull(methodMapFactory_.get(), "Factory must return Map instance.") : new HashMap<>(),
                        methodFinder_ != null ? methodFinder_ : Class::getMethod)
        );
    }

    /**
     * Implementation of {@link InvocationHandler}.
     */
    private static final class InvocationHandlerImpl implements InvocationHandler {
        private final Object stub_;
        private final Class<?> stubClass_;
        private final MethodFinder methodFinder_;
        private final Map<Method, StubMethod> methodMap_;

        public InvocationHandlerImpl(final Object target, final Map<Method, StubMethod> methodMap, final MethodFinder methodFinder) {
            stub_ = target;
            stubClass_ = target.getClass();
            methodMap_ = methodMap;
            methodFinder_ = methodFinder;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                return getStubMethod(method).invoke(args);
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof RuntimeException) {
                    throw e.getCause();
                } else if (e.getCause() instanceof Exception) {
                    throw new UndeclaredThrowableException(e.getCause());
                } else {
                    throw e.getCause();
                }
            } catch (Throwable e) {
                // If we don't wrap the marked exception in RuntimeException,
                // java.lang.reflect.Proxy will wrap it in UndeclaredThrowableException,
                // which will hide the original "user-readable" message.
                throw new UndeclaredThrowableException(e);
            }
        }

        private StubMethod getStubMethod(Method method) {
            return methodMap_.computeIfAbsent(method, key -> {
                try {
                    if (method.getDeclaringClass().equals(Object.class) && method.getName().equals("equals")) {
                        return new EqualsStubMethod(stub_, method, method.getReturnType(), methodFinder_);
                    } else {
                        Object exactStub;
                        Method exactMethod;
                        try {
                            exactStub = stub_;
                            exactMethod = methodFinder_.getMethod(stubClass_, method.getName(), method.getParameterTypes());
                            if (exactMethod == null) {
                                throw new NoSuchMethodException(method.getDeclaringClass().getName() + "." + method.getName() + argumentTypesToString(method.getParameterTypes()));
                            }
                        } catch (NoSuchMethodException nsc) {
                            if (stub_ instanceof TransparentDecorator) {
                                exactStub = ((TransparentDecorator) stub_).getDelegate();
                                exactMethod = methodFinder_.getMethod(exactStub.getClass(), method.getName(), method.getParameterTypes());
                                if (exactMethod == null) {
                                    throw new NoSuchMethodException(method.getDeclaringClass().getName() + "." + method.getName() + argumentTypesToString(method.getParameterTypes()));
                                }
                            } else {
                                throw nsc;
                            }
                        }
                        if (!exactMethod.isAccessible()) {
                            exactMethod.setAccessible(true);
                        }
                        return new StubMethod(exactStub, exactMethod, method.getReturnType(), methodFinder_);
                    }
                } catch (NoSuchMethodException e) {
                    throw ExceptionUtils.asUnchecked(e);
                }
            });
        }
    }

    private static class EqualsStubMethod extends StubMethod {

        public EqualsStubMethod(Object target, Method method, Class<?> returnType, MethodFinder methodFinder) {
            super(target, method, returnType, methodFinder);
        }

        @Override
        protected Object[] prepareArgs(Object... args) {
            if (args[0] != null && Proxy.isProxyClass(args[0].getClass())) {
                final InvocationHandler otherInvocationHandler = Proxy.getInvocationHandler(args[0]);
                if (otherInvocationHandler instanceof InvocationHandlerImpl) {
                    return new Object[]{((InvocationHandlerImpl) otherInvocationHandler).stub_};
                }
            }
            return super.prepareArgs(args);
        }
    }

    private static class StubMethod {
        /**
         * Reference to the method of the Stub object class
         */
        public final Method method;
        /**
         * The expected result class
         */
        private final Class<?> returnType;
        /**
         * The method for finding the suitable method of the class.
         */
        private final MethodFinder methodFinder_;
        /**
         * The actual result returned by the Stub method. Its type may not match {@link StubMethod#returnType}.
         */
        private Object rawResult;
        /**
         * The result of the previous call to {@link StubMethod#invoke(Object...)}, whose type
         * matches the expected {@link StubMethod#returnType}.
         */
        private Object typedResult;
        /**
         * The target object for which the method will be called.
         */
        private final Object target;

        public StubMethod(Object target, Method method, Class<?> returnType, MethodFinder methodFinder) {
            this.target = target;
            this.method = method;
            this.returnType = returnType;
            this.methodFinder_ = methodFinder;
        }

        public final Object invoke(Object... args) throws InvocationTargetException, IllegalAccessException {
            return castResult(method.invoke(target, prepareArgs(args)));
        }

        protected Object[] prepareArgs(Object... args) {
            return args;
        }
        /**
         * The Stub method may return an object that does not support the expected interface/class. In this case, a proxy object is created.
         *
         * @param value The value returned by the target method.
         * @return The value of the expected type
         */
        protected Object castResult(Object value) {
            synchronized (this) {
                if (value != null) {
                    // Reference equality comparison.
                    if (value == rawResult) {
                        // If the same object was returned as in the previous call, return the same object.
                        return typedResult;
                    } else if (returnType.isInstance(value)) {
                        rawResult = value;
                        typedResult = value;
                    } else if (returnType.isInterface()) {
                        // The rawResult type did not match the expected interface, create a proxy object.
                        typedResult = ProxyBuilder.forClasses(returnType).setTarget(value).setMethodFinder(methodFinder_).build();
                        rawResult = value;
                        value = typedResult;
                    } else {
                        // If the expected result is a primitive, we expect to get a convertible primitive value.
                        // If the classes are not convertible, there will be a casting error, there is nothing we can do about it.
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

    private static String argumentTypesToString(Class<?>[] parameterTypes) {
        return "(" + Stream.of(parameterTypes).map(t -> t != null ? t.getName() : "null").collect(Collectors.joining(", ")) + ")";
    }
}
