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


import com.devives.commons.lang.function.ExceptionFunction;
import com.devives.commons.lang.function.ExceptionProcedure;

public class ClassLoaderUtils {

    /**
     * Устанавливает переданный загрузчик классов в качестве текущего для текущего потока и выполняет анонимную функцию.
     *
     * @param classLoader загрузчик классов
     * @param func        анонимная ф-ция
     * @param <T>         Тип результата
     * @return Результат выполнения
     */
    public static <T> T callFuncInContextClassLoader(ClassLoader classLoader, final ExceptionFunction<T> func) {
        Thread thread = Thread.currentThread();
        ClassLoader c = thread.getContextClassLoader();
        boolean setRequired = (c != classLoader);
        if (setRequired) thread.setContextClassLoader(classLoader);
        try {
            return func.apply();
        } catch (Exception e) {
            throw ExceptionUtils.asUnchecked(e);
        } finally {
            if (setRequired) thread.setContextClassLoader(c);
        }
    }

    /**
     * Устанавливает переданный загрузчик классов в качестве текущего для текущего потока и выполняет анонимную процедуру.
     *
     * @param classLoader загрузчик классов
     * @param proc        анонимная ф-ция
     */
    public static void callProcInContextClassLoader(ClassLoader classLoader, final ExceptionProcedure proc) {
        Thread thread = Thread.currentThread();
        ClassLoader c = thread.getContextClassLoader();
        boolean setRequired = (c != classLoader);
        if (setRequired) thread.setContextClassLoader(classLoader);
        try {
            proc.accept();
        } catch (Exception e) {
            throw ExceptionUtils.asUnchecked(e);
        } finally {
            if (setRequired) thread.setContextClassLoader(c);
        }
    }


}
