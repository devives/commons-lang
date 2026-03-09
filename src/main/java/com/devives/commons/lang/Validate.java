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

import java.util.Objects;
import java.util.function.Supplier;

/**
 * The class contains utility methods for checking values.
 */
public class Validate {

    public static final String DEFAULT_NOT_EMPTY_ARRAY_MESSAGE = "The validated array is empty";
    public static final String DEFAULT_GREATER_MESSAGE = "The %s value must be greater than %s. Actual value: %s";
    public static final String DEFAULT_GREATER_OR_EQUAL_MESSAGE = "The %s value must be greater than or equal %s. Actual value: %s";

    /**
     * Validate that the specified primitive value is greater than specified bound; otherwise, throws an exception.
     *
     * @param value   значение.
     * @param bound   граница.
     * @param message the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param values  the optional values for the formatted exception message, null array not recommended
     * @return the validated value.
     * @throws IllegalArgumentException if value is lower than or equal bound.
     */
    public static int greater(int value, int bound, String message, Object... values) {
        if (value <= bound) {
            throw new IllegalArgumentException(getMessage(message, values));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is greater than specified bound; otherwise, throws an exception.
     *
     * @param value значение.
     * @param bound граница.
     * @return the validated value.
     * @throws IllegalArgumentException if value is lower than or equal bound.
     */
    public static int greater(int value, int bound) {
        if (value <= bound) {
            throw new IllegalArgumentException(getMessage(DEFAULT_GREATER_MESSAGE, "", bound, value));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is greater than specified bound; otherwise, throws an exception.
     *
     * @param value   значение.
     * @param bound   граница.
     * @param message the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param values  the optional values for the formatted exception message, null array not recommended
     * @return the validated value.
     * @throws IllegalArgumentException if value is lower than or equal bound.
     */
    public static long greater(final long value, final long bound, final String message, final Object... values) {
        if (value <= bound) {
            throw new IllegalArgumentException(getMessage(message, values));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is greater than or equal specified bound; otherwise, throws an exception.
     *
     * @param value значение.
     * @param bound граница.
     * @return the validated value.
     * @throws IllegalArgumentException if value is lower than bound.
     */
    public static int greaterOrEqual(final int value, final int bound) {
        if (value < bound) {
            throw new IllegalArgumentException(getMessage(DEFAULT_GREATER_OR_EQUAL_MESSAGE, "", bound, value));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is greater than or equal specified bound; otherwise, throws an exception.
     *
     * @param value значение.
     * @param bound граница.
     * @param message the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param values  the optional values for the formatted exception message, null array not recommended
     * @return the validated value.
     * @throws IllegalArgumentException if value is lower than bound.
     */
    public static int greaterOrEqual(final int value, final int bound, final String message, final Object... values) {
        if (value < bound) {
            throw new IllegalArgumentException(getMessage(message, values));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is greater than or equal specified bound; otherwise, throws an exception.
     *
     * @param value value.
     * @param bound bound.
     * @return the validated value.
     * @throws IllegalArgumentException if value is lower than bound.
     */
    public static long greaterOrEqual(final long value, final long bound) {
        if (value < bound) {
            throw new IllegalArgumentException(getMessage(DEFAULT_GREATER_OR_EQUAL_MESSAGE, "", bound, value));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is greater than or equal specified bound; otherwise, throws an exception.
     *
     * @param value   value.
     * @param bound   bound.
     * @param message the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param values  the optional values for the formatted exception message, null array not recommended
     * @return the validated value.
     * @throws IllegalArgumentException if value is lower than bound.
     */
    public static long greaterOrEqual(final long value, final long bound, final String message, final Object... values) {
        if (value < bound) {
            throw new IllegalArgumentException(getMessage(message, values));
        }
        return value;
    }

    /**
     * <p>Validate that the specified argument array is neither {@code null}
     * nor a length of zero (no elements); otherwise throwing an exception.
     *
     * <pre>Validate.notEmpty(myArray);</pre>
     *
     * <p>The message in the exception is &quot;The validated array is
     * empty&quot;.
     *
     * @param <T>   the array type
     * @param array the array to check, validated not null by this method
     * @return the validated array (never {@code null} method for chaining)
     * @throws NullPointerException     if the array is {@code null}
     * @throws IllegalArgumentException if the array is empty
     * @see #notEmpty(Object[], String, Object...)
     */
    public static <T> T[] notEmpty(final T[] array) {
        return notEmpty(array, DEFAULT_NOT_EMPTY_ARRAY_MESSAGE);
    }

    /**
     * <p>Validate that the specified argument array is neither {@code null}
     * nor a length of zero (no elements); otherwise throwing an exception
     * with the specified message.
     *
     * <pre>Validate.notEmpty(myArray, "The array must not be empty");</pre>
     *
     * @param <T>     the array type
     * @param array   the array to check, validated not null by this method
     * @param message the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param values  the optional values for the formatted exception message, null array not recommended
     * @return the validated array (never {@code null} method for chaining)
     * @throws NullPointerException     if the array is {@code null}
     * @throws IllegalArgumentException if the array is empty
     * @see #notEmpty(Object[])
     */
    public static <T> T[] notEmpty(final T[] array, final String message, final Object... values) {
        Objects.requireNonNull(array, toSupplier(message, values));
        if (array.length == 0) {
            throw new IllegalArgumentException(getMessage(message, values));
        }
        return array;
    }

    /**
     * Validate that the specified argument is not {@code null};
     * otherwise throwing an exception with the specified message.
     *
     * <pre>Validate.notNull(myObject, "The object must not be null");</pre>
     *
     * @param <T>     the object type
     * @param object  the object to check
     * @param message the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param values  the optional values for the formatted exception message
     * @return the validated object (never {@code null} for method chaining)
     * @throws NullPointerException if the object is {@code null}
     * @see Objects#requireNonNull(Object)
     */
    public static <T> T notNull(final T object, final String message, final Object... values) {
        return Objects.requireNonNull(object, toSupplier(message, values));
    }

    private static Supplier<String> toSupplier(final String message, final Object... values) {
        return () -> getMessage(message, values);
    }


    /**
     * Gets the message using {@link String#format(String, Object...) String.format(message, values)}
     * if the values are not empty, otherwise return the message unformatted.
     * This method exists to allow validation methods declaring a String message and varargs parameters
     * to be used without any message parameters when the message contains special characters,
     * e.g. {@code Validate.isTrue(false, "%Failed%")}.
     *
     * @param message the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param values  the optional values for the formatted message
     * @return formatted message using {@link String#format(String, Object...) String.format(message, values)}
     * if the values are not empty, otherwise return the unformatted message.
     */
    private static String getMessage(final String message, final Object... values) {
        return values.length == 0 ? message : String.format(message, values);
    }

}
