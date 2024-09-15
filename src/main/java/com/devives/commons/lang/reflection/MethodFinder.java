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
package com.devives.commons.lang.reflection;

import java.lang.reflect.Method;

/**
 * Декларация метода поиска подходящего для вызова метода класса.
 */
@FunctionalInterface
public interface MethodFinder {
    /**
     * @param clazz          Класс, в котором осуществляется поиск метода
     * @param name           Имя искомого метода
     * @param parameterTypes Параметры искомого метода
     * @return Найденный метод или `null`.
     * @throws NoSuchMethodException Если метод не найден
     * @throws SecurityException     Отсутствие прав на доступ
     */
    Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException;

}
