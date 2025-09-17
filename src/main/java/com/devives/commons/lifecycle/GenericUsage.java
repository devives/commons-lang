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
package com.devives.commons.lifecycle;

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
class GenericUsage<T> extends UsageAbst<T> implements Usage<T> {
    /**
     * The constructor.
     *
     * @param instance          the instance being captured.
     * @param releaseCallback the callback to decrease the use counter.
     */
    GenericUsage(T instance, FailableProcedure releaseCallback) {
        super(instance, releaseCallback);
    }
}