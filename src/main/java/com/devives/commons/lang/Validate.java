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
    public static final String DEFAULT_LOWER_MESSAGE = "The %s value must be lower than %s. Actual value: %s";
    public static final String DEFAULT_LOWER_OR_EQUAL_MESSAGE = "The %s value must be lower than or equal %s. Actual value: %s";

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
     * Validate that the specified primitive value is greater than specified bound; otherwise, throws an exception.
     *
     * @param value value.
     * @param bound bound.
     * @return the validated value.
     * @throws IllegalArgumentException if value is lower than or equal bound.
     */
    public static float greater(final float value, final float bound) {
        if (!(value > bound)) {
            throw new IllegalArgumentException(getMessage(DEFAULT_GREATER_MESSAGE, "", bound, value));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is greater than specified bound; otherwise, throws an exception.
     *
     * @param value   value.
     * @param bound   bound.
     * @param message the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param values  the optional values for the formatted exception message, null array not recommended
     * @return the validated value.
     * @throws IllegalArgumentException if value is lower than or equal bound.
     */
    public static float greater(final float value, final float bound, final String message, final Object... values) {
        if (!(value > bound)) {
            throw new IllegalArgumentException(getMessage(message, values));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is greater than specified bound; otherwise, throws an exception.
     *
     * @param value value.
     * @param bound bound.
     * @return the validated value.
     * @throws IllegalArgumentException if value is lower than or equal bound.
     */
    public static double greater(final double value, final double bound) {
        if (!(value > bound)) {
            throw new IllegalArgumentException(getMessage(DEFAULT_GREATER_MESSAGE, "", bound, value));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is greater than specified bound; otherwise, throws an exception.
     *
     * @param value   value.
     * @param bound   bound.
     * @param message the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param values  the optional values for the formatted exception message, null array not recommended
     * @return the validated value.
     * @throws IllegalArgumentException if value is lower than or equal bound.
     */
    public static double greater(final double value, final double bound, final String message, final Object... values) {
        if (!(value > bound)) {
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
     * Validate that the specified primitive value is greater than or equal specified bound; otherwise, throws an exception.
     *
     * @param value value.
     * @param bound bound.
     * @return the validated value.
     * @throws IllegalArgumentException if value is lower than bound.
     */
    public static float greaterOrEqual(final float value, final float bound) {
        if (!(value >= bound)) {
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
    public static float greaterOrEqual(final float value, final float bound, final String message, final Object... values) {
        if (!(value >= bound)) {
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
    public static double greaterOrEqual(final double value, final double bound) {
        if (!(value >= bound)) {
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
    public static double greaterOrEqual(final double value, final double bound, final String message, final Object... values) {
        if (!(value >= bound)) {
            throw new IllegalArgumentException(getMessage(message, values));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is lower than specified bound; otherwise, throws an exception.
     *
     * @param value value.
     * @param bound bound.
     * @return the validated value.
     * @throws IllegalArgumentException if value is greater than or equal bound.
     */
    public static int lower(final int value, final int bound) {
        if (value >= bound) {
            throw new IllegalArgumentException(getMessage(DEFAULT_LOWER_MESSAGE, "", bound, value));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is lower than specified bound; otherwise, throws an exception.
     *
     * @param value   value.
     * @param bound   bound.
     * @param message the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param values  the optional values for the formatted exception message, null array not recommended
     * @return the validated value.
     * @throws IllegalArgumentException if value is greater than or equal bound.
     */
    public static int lower(final int value, final int bound, final String message, final Object... values) {
        if (value >= bound) {
            throw new IllegalArgumentException(getMessage(message, values));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is lower than specified bound; otherwise, throws an exception.
     *
     * @param value value.
     * @param bound bound.
     * @return the validated value.
     * @throws IllegalArgumentException if value is greater than or equal bound.
     */
    public static long lower(final long value, final long bound) {
        if (value >= bound) {
            throw new IllegalArgumentException(getMessage(DEFAULT_LOWER_MESSAGE, "", bound, value));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is lower than specified bound; otherwise, throws an exception.
     *
     * @param value   value.
     * @param bound   bound.
     * @param message the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param values  the optional values for the formatted exception message, null array not recommended
     * @return the validated value.
     * @throws IllegalArgumentException if value is greater than or equal bound.
     */
    public static long lower(final long value, final long bound, final String message, final Object... values) {
        if (value >= bound) {
            throw new IllegalArgumentException(getMessage(message, values));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is lower than specified bound; otherwise, throws an exception.
     *
     * @param value value.
     * @param bound bound.
     * @return the validated value.
     * @throws IllegalArgumentException if value is greater than or equal bound.
     */
    public static float lower(final float value, final float bound) {
        if (!(value < bound)) {
            throw new IllegalArgumentException(getMessage(DEFAULT_LOWER_MESSAGE, "", bound, value));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is lower than specified bound; otherwise, throws an exception.
     *
     * @param value   value.
     * @param bound   bound.
     * @param message the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param values  the optional values for the formatted exception message, null array not recommended
     * @return the validated value.
     * @throws IllegalArgumentException if value is greater than or equal bound.
     */
    public static float lower(final float value, final float bound, final String message, final Object... values) {
        if (!(value < bound)) {
            throw new IllegalArgumentException(getMessage(message, values));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is lower than specified bound; otherwise, throws an exception.
     *
     * @param value value.
     * @param bound bound.
     * @return the validated value.
     * @throws IllegalArgumentException if value is greater than or equal bound.
     */
    public static double lower(final double value, final double bound) {
        if (!(value < bound)) {
            throw new IllegalArgumentException(getMessage(DEFAULT_LOWER_MESSAGE, "", bound, value));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is lower than specified bound; otherwise, throws an exception.
     *
     * @param value   value.
     * @param bound   bound.
     * @param message the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param values  the optional values for the formatted exception message, null array not recommended
     * @return the validated value.
     * @throws IllegalArgumentException if value is greater than or equal bound.
     */
    public static double lower(final double value, final double bound, final String message, final Object... values) {
        if (!(value < bound)) {
            throw new IllegalArgumentException(getMessage(message, values));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is lower than or equal specified bound; otherwise, throws an exception.
     *
     * @param value value.
     * @param bound bound.
     * @return the validated value.
     * @throws IllegalArgumentException if value is greater than bound.
     */
    public static int lowerOrEqual(final int value, final int bound) {
        if (value > bound) {
            throw new IllegalArgumentException(getMessage(DEFAULT_LOWER_OR_EQUAL_MESSAGE, "", bound, value));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is lower than or equal specified bound; otherwise, throws an exception.
     *
     * @param value   value.
     * @param bound   bound.
     * @param message the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param values  the optional values for the formatted exception message, null array not recommended
     * @return the validated value.
     * @throws IllegalArgumentException if value is greater than bound.
     */
    public static int lowerOrEqual(final int value, final int bound, final String message, final Object... values) {
        if (value > bound) {
            throw new IllegalArgumentException(getMessage(message, values));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is lower than or equal specified bound; otherwise, throws an exception.
     *
     * @param value value.
     * @param bound bound.
     * @return the validated value.
     * @throws IllegalArgumentException if value is greater than bound.
     */
    public static long lowerOrEqual(final long value, final long bound) {
        if (value > bound) {
            throw new IllegalArgumentException(getMessage(DEFAULT_LOWER_OR_EQUAL_MESSAGE, "", bound, value));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is lower than or equal specified bound; otherwise, throws an exception.
     *
     * @param value   value.
     * @param bound   bound.
     * @param message the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param values  the optional values for the formatted exception message, null array not recommended
     * @return the validated value.
     * @throws IllegalArgumentException if value is greater than bound.
     */
    public static long lowerOrEqual(final long value, final long bound, final String message, final Object... values) {
        if (value > bound) {
            throw new IllegalArgumentException(getMessage(message, values));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is lower than or equal specified bound; otherwise, throws an exception.
     *
     * @param value value.
     * @param bound bound.
     * @return the validated value.
     * @throws IllegalArgumentException if value is greater than bound.
     */
    public static float lowerOrEqual(final float value, final float bound) {
        if (!(value <= bound)) {
            throw new IllegalArgumentException(getMessage(DEFAULT_LOWER_OR_EQUAL_MESSAGE, "", bound, value));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is lower than or equal specified bound; otherwise, throws an exception.
     *
     * @param value   value.
     * @param bound   bound.
     * @param message the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param values  the optional values for the formatted exception message, null array not recommended
     * @return the validated value.
     * @throws IllegalArgumentException if value is greater than bound.
     */
    public static float lowerOrEqual(final float value, final float bound, final String message, final Object... values) {
        if (!(value <= bound)) {
            throw new IllegalArgumentException(getMessage(message, values));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is lower than or equal specified bound; otherwise, throws an exception.
     *
     * @param value value.
     * @param bound bound.
     * @return the validated value.
     * @throws IllegalArgumentException if value is greater than bound.
     */
    public static double lowerOrEqual(final double value, final double bound) {
        if (!(value <= bound)) {
            throw new IllegalArgumentException(getMessage(DEFAULT_LOWER_OR_EQUAL_MESSAGE, "", bound, value));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is lower than or equal specified bound; otherwise, throws an exception.
     *
     * @param value   value.
     * @param bound   bound.
     * @param message the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param values  the optional values for the formatted exception message, null array not recommended
     * @return the validated value.
     * @throws IllegalArgumentException if value is greater than bound.
     */
    public static double lowerOrEqual(final double value, final double bound, final String message, final Object... values) {
        if (!(value <= bound)) {
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

    /**
     * Creates a lazy message supplier for APIs like {@link Objects#requireNonNull(Object, Supplier)}.
     * The message is formatted only when the supplier is evaluated.
     *
     * @param message the {@link String#format(String, Object...)} message template, not null
     * @param values  the optional values for the formatted message
     * @return supplier returning either the formatted or original message
     */
    private static Supplier<String> toSupplier(final String message, final Object... values) {
        return () -> getMessage(message, values);
    }


    /**
     * Returns an exception message for validation failures.
     * If formatting arguments are provided, applies {@link String#format(String, Object...)};
     * otherwise returns the original message unchanged so literals like {@code "%Failed%"} remain valid.
     *
     * @param message the message template or plain message, not null
     * @param values  the optional formatting arguments
     * @return the formatted message if {@code values} is non-empty, otherwise {@code message}
     */
    private static String getMessage(final String message, final Object... values) {
        return values.length == 0 ? message : String.format(message, values);
    }

}
