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
package com.devives.commons.util.usage;

import com.devives.commons.lang.function.FailableConsumer;
import com.devives.commons.lang.function.FailableProcedure;

/**
 * Utility class with a reference to a captured instance.
 * <p>
 * Used in constructs like:
 * <pre>{@code
 * try (Usage<Item> itemUsage = manager.acquire()){
 *     itemUsage.get().doWork();
 * }
 * }</pre>
 *
 * @param <T> The type of the instance to which a reference is obtained.
 */
public interface Usage<T> extends AutoCloseable {
    /**
     * Returns a reference to the captured instance.
     *
     * @return the instance.
     */
    T get();

    /**
     * Instantiate an new instance of {@link Usage}.
     *
     * @param instance an instance of used object
     * @param releaseCallback release usage of object
     * @return new instance of {@link Usage}
     * @param <T> The type of the instance to which a reference is obtained.
     */
    static <T> Usage<T> of(T instance, FailableProcedure releaseCallback) {
        return new GenericUsage<T>(instance, releaseCallback);
    }

    /**
     * Instantiate an new instance of {@link Usage}.
     *
     * @param instance an instance of used object
     * @param releaseCallback release usage of object
     * @return new instance of {@link Usage}
     * @param <T> The type of the instance to which a reference is obtained.
     */
    static <T> Usage<T> of(T instance, FailableConsumer<T> releaseCallback) {
        return new ManagedUsage<>(instance, releaseCallback);
    }
}