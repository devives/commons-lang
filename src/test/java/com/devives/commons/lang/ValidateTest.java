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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValidateTest {

    @Test
    public void greater_intValueAboveBound_returnsValue() {
        Assertions.assertEquals(2, Validate.greater(2, 1));
    }

    @Test
    public void greater_intValueEqualsBound_throwsIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.greater(1, 1)
        );

        Assertions.assertEquals("The  value must be greater than 1. Actual value: 1", exception.getMessage());
    }

    @Test
    public void greater_intValueBelowBoundWithCustomMessage_throwsFormattedIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.greater(1, 2, "Expected %s > %s", "value", 2)
        );

        Assertions.assertEquals("Expected value > 2", exception.getMessage());
    }

    @Test
    public void greater_longValueAboveBound_returnsValue() {
        Assertions.assertEquals(3L, Validate.greater(3L, 2L, "Expected positive delta"));
    }

    @Test
    public void greater_longValueEqualsBoundWithCustomMessage_throwsUnformattedIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.greater(2L, 2L, "%Failed%")
        );

        Assertions.assertEquals("%Failed%", exception.getMessage());
    }

    @Test
    public void greater_floatValueAboveBound_returnsValue() {
        Assertions.assertEquals(2.5F, Validate.greater(2.5F, 2.0F));
    }

    @Test
    public void greater_floatNanValue_throwsIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.greater(Float.NaN, 1.0F)
        );

        Assertions.assertEquals("The  value must be greater than 1.0. Actual value: NaN", exception.getMessage());
    }

    @Test
    public void greater_floatValueBelowBoundWithCustomMessage_throwsFormattedIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.greater(1.0F, 2.0F, "%s must be > %s", "value", 2.0F)
        );

        Assertions.assertEquals("value must be > 2.0", exception.getMessage());
    }

    @Test
    public void greater_doubleValueAboveBound_returnsValue() {
        Assertions.assertEquals(2.5D, Validate.greater(2.5D, 2.0D));
    }

    @Test
    public void greater_doubleValueEqualsBound_throwsIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.greater(2.0D, 2.0D)
        );

        Assertions.assertEquals("The  value must be greater than 2.0. Actual value: 2.0", exception.getMessage());
    }

    @Test
    public void greater_doubleNanBoundWithCustomMessage_throwsFormattedIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.greater(2.0D, Double.NaN, "Invalid %s", "bound")
        );

        Assertions.assertEquals("Invalid bound", exception.getMessage());
    }

    @Test
    public void greaterOrEqual_intValueAboveBound_returnsValue() {
        Assertions.assertEquals(2, Validate.greaterOrEqual(2, 1));
    }

    @Test
    public void greaterOrEqual_intValueEqualsBound_returnsValue() {
        Assertions.assertEquals(1, Validate.greaterOrEqual(1, 1));
    }

    @Test
    public void greaterOrEqual_intValueBelowBound_throwsIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.greaterOrEqual(0, 1)
        );

        Assertions.assertEquals("The  value must be greater than or equal 1. Actual value: 0", exception.getMessage());
    }

    @Test
    public void greaterOrEqual_intValueBelowBoundWithCustomMessage_throwsFormattedIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.greaterOrEqual(0, 1, "%s must be >= %d", "value", 1)
        );

        Assertions.assertEquals("value must be >= 1", exception.getMessage());
    }

    @Test
    public void greaterOrEqual_longValueEqualsBound_returnsValue() {
        Assertions.assertEquals(5L, Validate.greaterOrEqual(5L, 5L));
    }

    @Test
    public void greaterOrEqual_longValueBelowBound_throwsIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.greaterOrEqual(4L, 5L)
        );

        Assertions.assertEquals("The  value must be greater than or equal 5. Actual value: 4", exception.getMessage());
    }

    @Test
    public void greaterOrEqual_longValueBelowBoundWithCustomMessage_throwsFormattedIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.greaterOrEqual(4L, 5L, "%s=%d is too small", "value", 4)
        );

        Assertions.assertEquals("value=4 is too small", exception.getMessage());
    }

    @Test
    public void greaterOrEqual_floatValueEqualsBound_returnsValue() {
        Assertions.assertEquals(1.5F, Validate.greaterOrEqual(1.5F, 1.5F));
    }

    @Test
    public void greaterOrEqual_floatValueBelowBound_throwsIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.greaterOrEqual(1.0F, 1.5F)
        );

        Assertions.assertEquals("The  value must be greater than or equal 1.5. Actual value: 1.0", exception.getMessage());
    }

    @Test
    public void greaterOrEqual_floatNanValueWithCustomMessage_throwsFormattedIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.greaterOrEqual(Float.NaN, 1.0F, "%s must be finite", "value")
        );

        Assertions.assertEquals("value must be finite", exception.getMessage());
    }

    @Test
    public void greaterOrEqual_doubleValueEqualsBound_returnsValue() {
        Assertions.assertEquals(1.5D, Validate.greaterOrEqual(1.5D, 1.5D));
    }

    @Test
    public void greaterOrEqual_doubleNanBound_throwsIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.greaterOrEqual(1.5D, Double.NaN)
        );

        Assertions.assertEquals("The  value must be greater than or equal NaN. Actual value: 1.5", exception.getMessage());
    }

    @Test
    public void greaterOrEqual_doubleValueBelowBoundWithCustomMessage_throwsFormattedIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.greaterOrEqual(1.0D, 1.5D, "%s must be >= %s", "value", 1.5D)
        );

        Assertions.assertEquals("value must be >= 1.5", exception.getMessage());
    }

    @Test
    public void lower_intValueBelowBound_returnsValue() {
        Assertions.assertEquals(1, Validate.lower(1, 2));
    }

    @Test
    public void lower_intValueEqualsBound_throwsIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.lower(2, 2)
        );

        Assertions.assertEquals("The  value must be lower than 2. Actual value: 2", exception.getMessage());
    }

    @Test
    public void lower_intValueAboveBoundWithCustomMessage_throwsFormattedIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.lower(3, 2, "%s must be < %s", "value", 2)
        );

        Assertions.assertEquals("value must be < 2", exception.getMessage());
    }

    @Test
    public void lower_longValueBelowBound_returnsValue() {
        Assertions.assertEquals(1L, Validate.lower(1L, 2L));
    }

    @Test
    public void lower_longValueEqualsBoundWithCustomMessage_throwsFormattedIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.lower(2L, 2L, "%s must be lower", "value")
        );

        Assertions.assertEquals("value must be lower", exception.getMessage());
    }

    @Test
    public void lower_floatValueBelowBound_returnsValue() {
        Assertions.assertEquals(1.5F, Validate.lower(1.5F, 2.0F));
    }

    @Test
    public void lower_floatNanValue_throwsIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.lower(Float.NaN, 2.0F)
        );

        Assertions.assertEquals("The  value must be lower than 2.0. Actual value: NaN", exception.getMessage());
    }

    @Test
    public void lower_floatValueEqualsBoundWithCustomMessage_throwsFormattedIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.lower(2.0F, 2.0F, "%s must be < %s", "value", 2.0F)
        );

        Assertions.assertEquals("value must be < 2.0", exception.getMessage());
    }

    @Test
    public void lower_doubleValueBelowBound_returnsValue() {
        Assertions.assertEquals(1.5D, Validate.lower(1.5D, 2.0D));
    }

    @Test
    public void lower_doubleNanBound_throwsIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.lower(1.5D, Double.NaN)
        );

        Assertions.assertEquals("The  value must be lower than NaN. Actual value: 1.5", exception.getMessage());
    }

    @Test
    public void lower_doubleValueAboveBoundWithCustomMessage_throwsFormattedIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.lower(3.0D, 2.0D, "%s must be < %s", "value", 2.0D)
        );

        Assertions.assertEquals("value must be < 2.0", exception.getMessage());
    }

    @Test
    public void lowerOrEqual_intValueBelowBound_returnsValue() {
        Assertions.assertEquals(1, Validate.lowerOrEqual(1, 2));
    }

    @Test
    public void lowerOrEqual_intValueEqualsBound_returnsValue() {
        Assertions.assertEquals(2, Validate.lowerOrEqual(2, 2));
    }

    @Test
    public void lowerOrEqual_intValueAboveBound_throwsIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.lowerOrEqual(3, 2)
        );

        Assertions.assertEquals("The  value must be lower than or equal 2. Actual value: 3", exception.getMessage());
    }

    @Test
    public void lowerOrEqual_intValueAboveBoundWithCustomMessage_throwsFormattedIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.lowerOrEqual(3, 2, "%s must be <= %s", "value", 2)
        );

        Assertions.assertEquals("value must be <= 2", exception.getMessage());
    }

    @Test
    public void lowerOrEqual_longValueEqualsBound_returnsValue() {
        Assertions.assertEquals(2L, Validate.lowerOrEqual(2L, 2L));
    }

    @Test
    public void lowerOrEqual_longValueAboveBoundWithCustomMessage_throwsFormattedIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.lowerOrEqual(3L, 2L, "%s=%s is too high", "value", 3L)
        );

        Assertions.assertEquals("value=3 is too high", exception.getMessage());
    }

    @Test
    public void lowerOrEqual_floatValueEqualsBound_returnsValue() {
        Assertions.assertEquals(2.0F, Validate.lowerOrEqual(2.0F, 2.0F));
    }

    @Test
    public void lowerOrEqual_floatNanValue_throwsIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.lowerOrEqual(Float.NaN, 2.0F)
        );

        Assertions.assertEquals("The  value must be lower than or equal 2.0. Actual value: NaN", exception.getMessage());
    }

    @Test
    public void lowerOrEqual_floatValueAboveBoundWithCustomMessage_throwsFormattedIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.lowerOrEqual(3.0F, 2.0F, "%s must be <= %s", "value", 2.0F)
        );

        Assertions.assertEquals("value must be <= 2.0", exception.getMessage());
    }

    @Test
    public void lowerOrEqual_doubleValueEqualsBound_returnsValue() {
        Assertions.assertEquals(2.0D, Validate.lowerOrEqual(2.0D, 2.0D));
    }

    @Test
    public void lowerOrEqual_doubleNanBound_throwsIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.lowerOrEqual(2.0D, Double.NaN)
        );

        Assertions.assertEquals("The  value must be lower than or equal NaN. Actual value: 2.0", exception.getMessage());
    }

    @Test
    public void lowerOrEqual_doubleValueAboveBoundWithCustomMessage_throwsFormattedIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.lowerOrEqual(3.0D, 2.0D, "%s must be <= %s", "value", 2.0D)
        );

        Assertions.assertEquals("value must be <= 2.0", exception.getMessage());
    }

    @Test
    public void notEmpty_nonEmptyArray_returnsSameArray() {
        String[] values = new String[]{"alpha"};

        Assertions.assertSame(values, Validate.notEmpty(values));
    }

    @Test
    public void notEmpty_emptyArray_throwsIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.notEmpty(new String[0])
        );

        Assertions.assertEquals("The validated array is empty", exception.getMessage());
    }

    @Test
    public void notEmpty_nullArray_throwsNullPointerException() {
        NullPointerException exception = Assertions.assertThrows(
                NullPointerException.class,
                () -> Validate.notEmpty(null)
        );

        Assertions.assertEquals("The validated array is empty", exception.getMessage());
    }

    @Test
    public void notEmpty_emptyArrayWithCustomMessage_throwsFormattedIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Validate.notEmpty(new Integer[0], "Array '%s' must not be empty", "ids")
        );

        Assertions.assertEquals("Array 'ids' must not be empty", exception.getMessage());
    }

    @Test
    public void notNull_nonNullObject_returnsSameObject() {
        Object value = new Object();

        Assertions.assertSame(value, Validate.notNull(value, "value"));
    }

    @Test
    public void notNull_nullObject_throwsFormattedNullPointerException() {
        NullPointerException exception = Assertions.assertThrows(
                NullPointerException.class,
                () -> Validate.notNull(null, "%s must not be null", "value")
        );

        Assertions.assertEquals("value must not be null", exception.getMessage());
    }
}
