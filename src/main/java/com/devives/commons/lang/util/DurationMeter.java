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
package com.devives.commons.lang.util;


import com.devives.commons.lang.ExceptionUtils;
import com.devives.commons.lang.function.ExceptionProcedure;
import com.devives.commons.lang.function.FailableFunction;

import java.time.Duration;

/**
 * Счётчик длительности выполнения блока кода.
 */
public class DurationMeter {
    private long startNanos_ = 0;
    private long stopNanos_ = 0;
    private boolean measuring_ = false;
    private long duration_ = 0;

    /**
     * @return Длительность
     */
    public Duration duration() {
        return Duration.ofNanos(durationNanos());
    }

    /**
     * @return Длительность в наносекундах.
     */
    public long durationNanos() {
        return duration_;
    }

    /**
     * @return Длительность в микросекундах.
     */
    public long durationMicros() {
        return durationNanos() / 1000;
    }

    /**
     * @return Длительность в миллисекундах.
     */
    public long durationMills() {
        return durationNanos() / (1000 * 1000);
    }

    /**
     * @return Длительность в секундах.
     */
    public double durationSeconds() {
        return (double) durationNanos() / (double) (1000 * 1000 * 1000);
    }

    /**
     * Выполняется измерение длительности выполнения анонимного метода.
     *
     * @param proc анонимный метод
     */
    public void measure(ExceptionProcedure proc) {
        start();
        try {
            proc.accept();
        } catch (Exception e) {
            throw ExceptionUtils.asUnchecked(e);
        } finally {
            stop();
        }
    }

    /**
     * /**
     * Выполняется измерение длительности выполнения анонимного метода.
     *
     * @param func анонимный метод
     * @param <R>  тип результата анонимного метода
     * @return результат выполнения анонимного метода
     */
    public <R> R measure(FailableFunction<R, Exception> func) {
        start();
        try {
            return func.apply();
        } catch (Exception e) {
            throw ExceptionUtils.asUnchecked(e);
        } finally {
            stop();
        }
    }

    /**
     * Начинает изменение.
     */
    public void start() {
        if (measuring_) {
            throw new RuntimeException("Unable to start the meter which is currently measuring.");
        }
        measuring_ = true;
        startNanos_ = System.nanoTime();
    }

    /**
     * Завершает изменение.
     */
    public void stop() {
        if (measuring_) {
            stopNanos_ = System.nanoTime();
            duration_ += (stopNanos_ - startNanos_);
            measuring_ = false;
        }
    }

    /**
     * Метод сбрасывает измеренную длительность в "0";
     */
    public void reset() {
        if (measuring_) {
            throw new RuntimeException("Unable to reset the meter which is currently measuring.");
        }
        duration_ = 0;
    }

    /**
     * Флаг указывает на выполнение изменения.
     *
     * @return true, если выполняется изменение, иначе false.
     */
    public boolean isMeasuring() {
        return measuring_;
    }

}
