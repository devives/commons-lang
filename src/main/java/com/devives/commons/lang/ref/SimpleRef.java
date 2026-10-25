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

/**
 * A reference backed by a plain field.
 * <p>
 * Instances are created by {@link Ref#of(Object)}, {@link Ref#ofNullable(Object)} and {@link Ref#ofNullable()}.
 *
 * <p><strong>Thread safety.</strong></p>
 * Not thread-safe. Concurrent access requires external synchronization.
 *
 * @param <T> the type of the value
 */
public class SimpleRef<T> extends Ref<T> {

    private T value_;

    SimpleRef(T value) {
        value_ = validate(value);
    }

    T validate(T value) {
        return value;
    }

    @Override
    public final T get() {
        return value_;
    }

    @Override
    public final void set(T value) {
        value_ = validate(value);
    }

    @Override
    public boolean isNullable() {
        return true;
    }

}
