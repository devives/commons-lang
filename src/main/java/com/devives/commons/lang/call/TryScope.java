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

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * Общий код реализаций конструкции {@code try-catch-finally}.
 *
 * @see TryCallableScope
 * @see TryRunnableScope
 */
class TryScope {

    /**
     * Проверяет, достижимо ли исключение {@code target} из {@code throwable} по цепочкам cause и suppressed.
     * Сравнение выполняется по ссылке; циклические ссылки в графе исключений допускаются.
     *
     * @param throwable исключение, с которого начинается обход.
     * @param target    искомое исключение.
     * @return {@code true}, если {@code target} совпадает с {@code throwable} или достижимо из него.
     */
    protected static boolean hasReference(Throwable throwable, Throwable target) {
        if (throwable == target) {
            return true;
        }
        if (throwable.getCause() == target) {
            return true;
        }
        Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        Deque<Throwable> stack = new ArrayDeque<>();
        stack.push(throwable);
        while (!stack.isEmpty()) {
            Throwable t = stack.pop();
            if (t == target) {
                return true;
            }
            if (!visited.add(t)) {
                continue;
            }
            if (t.getCause() != null) {
                stack.push(t.getCause());
            }
            for (Throwable s : t.getSuppressed()) {
                stack.push(s);
            }
        }
        return false;
    }

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
    protected static boolean isLinked(Throwable a, Throwable b) {
        return hasReference(a, b) || hasReference(b, a);
    }
}
