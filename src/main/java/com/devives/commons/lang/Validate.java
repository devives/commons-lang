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

/**
 * The class contains utility methods for checking values.
 */
public class Validate {

    /**
     * Validate that the specified primitive value is greater than specified bound; otherwise, throws an exception.
     *
     * @param value значение.
     * @param bound граница.
     * @param name  имя проверяемого значения.
     * @throws IllegalArgumentException if value is lower than or equal bound.
     */
    public static int greater(int value, int bound, String name) {
        if (value <= bound) {
            throw new IllegalArgumentException(String.format("The value of '%s' must be greater than %s. Actual value: %s", name, bound, value));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is greater than specified bound; otherwise, throws an exception.
     *
     * @param value значение.
     * @param bound граница.
     * @param name  имя проверяемого значения.
     * @throws IllegalArgumentException if value is lower than bound.
     */
    public static long greater(long value, long bound, String name) {
        if (value <= bound) {
            throw new IllegalArgumentException(String.format("The value of '%s' must be greater than %s. Actual value: %s", name, bound, value));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is greater than or equal specified bound; otherwise, throws an exception.
     *
     * @param value значение.
     * @param bound граница.
     * @param name  имя проверяемого значения.
     * @throws IllegalArgumentException if value is lower than bound.
     */
    public static int greaterOrEqual(int value, int bound, String name) {
        if (value < bound) {
            throw new IllegalArgumentException(String.format("The value of '%s' must be greater than or equal %s. Actual value: %s", name, bound, value));
        }
        return value;
    }

    /**
     * Validate that the specified primitive value is greater than or equal specified bound; otherwise, throws an exception.
     *
     * @param value значение.
     * @param bound граница.
     * @param name  имя проверяемого значения.
     * @throws IllegalArgumentException if value is lower than bound.
     */
    public static long greaterOrEqual(long value, long bound, String name) {
        if (value < bound) {
            throw new IllegalArgumentException(String.format("The value of '%s' must be greater than or equal %s. Actual value: %s", name, bound, value));
        }
        return value;
    }

}
