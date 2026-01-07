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
