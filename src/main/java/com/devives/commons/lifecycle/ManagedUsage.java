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

import com.devives.commons.lang.function.FailableConsumer;

import java.util.Objects;

/**
 * Basic implementation of {@link Usage}.
 *
 * @param <T> The type of the instance to which a reference is obtained.
 */
final class ManagedUsage<T> implements Usage<T> {

    private final T instance_;
    private final FailableConsumer<T> releaseCallback_;

    /**
     * The constructor.
     *
     * @param instance        the instance being captured.
     * @param releaseCallback the callback to decrease the use counter.
     */
    ManagedUsage(T instance, FailableConsumer<T> releaseCallback) {
        instance_ = Objects.requireNonNull(instance);
        releaseCallback_ = Objects.requireNonNull(releaseCallback);
    }

    /**
     * Returns a reference to the captured instance.
     *
     * @return the instance.
     */
    public T get() {
        return instance_;
    }

    @Override
    public void close() throws Exception {
        releaseCallback_.accept(instance_);
    }

}