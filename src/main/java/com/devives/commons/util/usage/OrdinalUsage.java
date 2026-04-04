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

import com.devives.commons.lang.function.FailableProcedure;

/**
 * Utility object with a reference to a captured instance and the acquisition ordinal
 * observed when this usage was acquired.
 * <p>
 * The acquisition ordinal is the value of the instance usage counter immediately after
 * this acquisition. It is therefore suitable for checks such as detecting the first
 * active acquisition, but it is not a unique ever-increasing sequence number.
 *
 * @param <T> the type of referenced object.
 */
public interface OrdinalUsage<T> extends Usage<T> {

    /**
     * Returns {@code true} if this usage was the first active acquisition of the referenced instance,
     * i.e. if the acquisition ordinal observed at acquire time is equal to {@code 1}.
     *
     * @return {@code true} if this is the first active acquisition.
     */
    boolean isFirstAcquisition();

    /**
     * Returns the acquisition ordinal observed when this usage was acquired.
     * <p>
     * The returned value is the instance usage counter immediately after this acquisition,
     * so it reflects this usage's position among currently active acquisitions at that moment.
     * The same value may appear again after all usages are released and the instance is acquired again.
     *
     * @return the acquisition ordinal observed at acquire time.
     */
    int getAcquisitionOrdinal();

    /**
     * Increases the use counter of the object {@code instance} and creates a new instance of {@link OrdinalUsage}.
     *
     * @param instance the instance with a usage counter.
     * @param <T>      the type of referenced object.
     * @return a new instance of {@link OrdinalUsage}
     */
    static <T extends UsageCounter> OrdinalUsage<T> of(T instance) {
        return new GenericOrdinalUsage<T>(instance, instance.incUsageCount(), instance::decUsageCount);
    }

    /**
     * Instantiate an new instance of {@link OrdinalUsage}.
     *
     * @param instance        the instance with a usage.
     * @param usages          the acquisition ordinal observed at acquire time.
     * @param releaseCallback release usage callback.
     * @param <T>             the type of referenced object.
     * @return a new instance of {@link OrdinalUsage}
     */
    static <T> OrdinalUsage<T> of(T instance, int usages, FailableProcedure releaseCallback) {
        return new GenericOrdinalUsage<T>(instance, usages, releaseCallback);
    }

}
