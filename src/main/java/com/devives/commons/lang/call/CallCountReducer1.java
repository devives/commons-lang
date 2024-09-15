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


import com.devives.commons.lang.function.ExceptionProcedure1;

import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

/**
 * Уменьшитель числа вызовов целевого метода c одним аргументом.
 *
 * @see AbstractCallCountReducer
 */
public final class CallCountReducer1<A1> extends AbstractCallCountReducer {

    CallCountReducer1(ExceptionProcedure1<A1> proc, long interval, Executor executor, BiConsumer<Throwable, Object> errorSink) {
        super((args) -> proc.accept((A1) args[0]), interval, executor, errorSink);
    }

    /**
     * Планирует вызов целевого метода.
     *
     * @param a1 аргумент.
     */
    public void invoke(A1 a1) {
        super.invoke(a1);
    }

}