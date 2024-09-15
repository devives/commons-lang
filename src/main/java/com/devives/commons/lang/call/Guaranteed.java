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

import com.devives.commons.lang.exception.ExceptionUtils;
import com.devives.commons.lang.function.ExceptionProcedure;
import com.devives.commons.lang.function.FailableProcedure;

import java.sql.SQLException;

/**
 * Класс содержит утилитарные методы обработки исключений при вызове группы методов.
 */
public final class Guaranteed {

    /**
     * Метод предназначен для гарантированного вызова последовательности методов, независимо от результата вызова
     * каждого отдельного метода последовательности.
     *
     * @param procs массив анонимных методов.
     * @throws Exception если в одном или нескольких вызовах возникли исключения.
     */
    static public void calls(ExceptionProcedure... procs) throws Exception {
        Throwable throwable = null;
        for (ExceptionProcedure proc : procs) {
            try {
                proc.accept();
            } catch (Throwable thr) {
                if (throwable != null) {
                    thr.addSuppressed(throwable);
                }
                throwable = thr;
            }
        }
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException) throwable;
        } else if (throwable != null) {
            throw ExceptionUtils.toException(throwable);
        }
    }

    /**
     * Метод предназначен для гарантированного вызова последовательности методов, независимо от результата вызова
     * каждого отдельного метода последовательности.
     * <p>
     * Название метода начинается с "sql", т.к. выбрасывает SQLException.
     *
     * @param procs массив анонимных методов.
     * @throws SQLException если в одном или нескольких вызовах возникли исключения.
     */
    @SafeVarargs
    static public void sqlCalls(FailableProcedure<SQLException>... procs) throws SQLException {
        Throwable throwable = null;
        for (FailableProcedure<SQLException> proc : procs) {
            try {
                proc.accept();
            } catch (Throwable thr) {
                if (throwable != null) {
                    thr.addSuppressed(throwable);
                }
                throwable = thr;
            }
        }
        if (throwable instanceof SQLException) {
            throw (SQLException) throwable;
        } else if (throwable instanceof RuntimeException) {
            throw (RuntimeException) throwable;
        } else if (throwable != null) {
            throw new SQLException(throwable);
        }
    }


}
