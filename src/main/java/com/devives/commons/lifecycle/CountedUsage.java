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
 * Utility class with a reference to a captured instance and count of it usages.
 *
 * @param <T> the type of referenced object.
 */
public interface CountedUsage<T> extends Usage<T> {
    /**
     * Returns the count of uses of the object at the time of getting the reference.
     *
     * @return the count.
     */
    long getCount();

    /**
     * Increases the use counter of the object {@code instance} and creates a new instance of {@link CountedUsage}.
     *
     * @param instance the instance with a usage counter.
     * @param <T>      the type of referenced object.
     * @return a new instance of {@link CountedUsage}
     */
    static <T extends UsageCounter> CountedUsage<T> of(T instance) {
        return new GenericCountedUsage<T>(instance, instance.incUsageCount(), instance::decUsageCount);
    }

    /**
     * Instantiate an new instance of {@link CountedUsage}.
     *
     * @param instance        the instance with a usage.
     * @param usages          a count of usages.
     * @param releaseCallback release usage callback.
     * @param <T>             the type of referenced object.
     * @return a new instance of {@link CountedUsage}
     */
    static <T> CountedUsage<T> of(T instance, int usages, FailableProcedure releaseCallback) {
        return new GenericCountedUsage<T>(instance, usages, releaseCallback);
    }

}
