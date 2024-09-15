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
package com.devives.commons.lang.call;

import com.devives.commons.lang.function.ExceptionProcedure;
import com.devives.commons.lang.function.ExceptionProcedure1;

import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

/**
 * Уменьшитель числа вызовов целевого метода.
 *
 * @see AbstractCallCountReducer
 */
public final class CallCountReducer extends AbstractCallCountReducer {

    CallCountReducer(ExceptionProcedure proc, long interval, Executor executor, BiConsumer<Throwable, Object> errorSink) {
        super((args) -> proc.accept(), interval, executor, errorSink);
    }

    /**
     * Планирует вызов целевого метода.
     */
    public void invoke() {
        super.invoke();
    }

    /**
     * Создаёт новый экземпляр уменьшителя числа вызовов целевого метода.
     *
     * @param proc      Целевой метод.
     * @param interval  Интервал между последовательными вызовами запланированного метода, в миллисекундах.
     * @param executor  исполнитель.
     * @param errorSink воронка исключений.
     * @return новый экземпляр {@link CallCountReducer}.
     */
    public static CallCountReducer of(ExceptionProcedure proc,
                                      long interval,
                                      Executor executor,
                                      BiConsumer<Throwable, Object> errorSink) {
        return new CallCountReducer(proc, interval, executor, errorSink);
    }

    /**
     * Создаёт новый экземпляр уменьшителя числа вызовов целевого метода с одним аргументом.
     *
     * @param proc      Целевой метод.
     * @param interval  Интервал между последовательными вызовами запланированного метода, в миллисекундах.
     * @param executor  исполнитель.
     * @param errorSink воронка исключений.
     * @param <A1>      тип атрибута целевого метода.
     * @return новый экземпляр {@link CallCountReducer1}.
     */
    public static <A1> CallCountReducer1<A1> of(ExceptionProcedure1<A1> proc,
                                                long interval,
                                                Executor executor,
                                                BiConsumer<Throwable, Object> errorSink) {
        return new CallCountReducer1<A1>(proc, interval, executor, errorSink);
    }

}
