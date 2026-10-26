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

import static com.devives.commons.lang.ExceptionUtils.isEqualOrReferenced;

/**
 * Общий код реализаций конструкции {@code try-catch-finally}.
 *
 * @see TryCallableScope
 * @see TryRunnableScope
 */
class TryScope {

    /**
     * Проверяет, связаны ли исключения {@code a} и {@code b} в любом направлении по цепочкам cause и suppressed.
     * <p>
     * Связь в обратную сторону возникает, когда обработчик {@code catch} распаковывает исходное исключение,
     * например {@code throw th.getCause()}: добавление исходного в suppressed распакованного создало бы цикл.
     *
     * @param a первое исключение.
     * @param b второе исключение.
     * @return {@code true}, если одно исключение достижимо из другого.
     */
    protected static boolean notLinked(Throwable a, Throwable b) {
        return !isEqualOrReferenced(a, b) && !isEqualOrReferenced(b, a);
    }
}
