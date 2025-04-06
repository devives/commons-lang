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
package com.devives.commons.lang.function;

/**
 * A procedure with three arguments that can throw an exception of the specified type.
 *
 * @param <T1> the type of first argument.
 * @param <T2> the type of second argument.
 * @param <T3> the type of third argument.
 * @param <E> The type of exception
 */
@FunctionalInterface
public interface ThrowableTriConsumer<T1, T2, T3, E extends Throwable> {

    /**
     * Performs the operation given the specified arguments.
     *
     * @param a1 the first argument.
     * @param a2 the second argument.
     * @param a3 the third argument.
     * @throws E The type of exception with can be thrown.
     */
    void accept(T1 a1, T2 a2, T3 a3) throws E;
}
