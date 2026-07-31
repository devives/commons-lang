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
package com.devives.commons.lang;

import com.devives.commons.lang.function.FailableProcedure;
import com.devives.commons.lang.function.Procedure;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Utility methods for working with objects that may implement {@link AutoCloseable}.
 */
public final class AutoCloseableUtils {

    private AutoCloseableUtils() {
    }

    /**
     * Closes the specified object when it implements {@link AutoCloseable}.
     *
     * <p>If the object does not implement {@code AutoCloseable}, this method does nothing.
     * Exceptions thrown by {@link AutoCloseable#close()} are rethrown using
     * {@link ExceptionUtils#passChecked}.</p>
     *
     * @param object object that may need to be closed
     */
    public static void closeIfAutoCloseable(Object object) {
        if (object instanceof AutoCloseable) {
            ExceptionUtils.passChecked(((AutoCloseable) object)::close);
        }
    }


    /**
     * Closes the specified objects when they implements {@link AutoCloseable}.
     *
     * <p>If the object does not implement {@code AutoCloseable}, this method does nothing.
     * Exceptions thrown by {@link AutoCloseable#close()} are rethrown using
     * {@link ExceptionUtils#passChecked}.</p>
     *
     * @param object object that may need to be closed
     */
    public static void closeIfAutoCloseable(Object... object) {
        ExceptionUtils.collectAndThrow(Stream.of(object)
                .filter(AutoCloseable.class::isInstance)
                .map(AutoCloseable.class::cast)
                .<FailableProcedure>map(it -> () -> ExceptionUtils.passChecked(it::close))
                .toArray(FailableProcedure[]::new)
        );
    }


}
