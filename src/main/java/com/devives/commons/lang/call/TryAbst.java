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
import com.devives.commons.lang.function.ThrowableFunction;
import com.devives.commons.lang.function.ThrowableFunction1;
import com.devives.commons.lang.function.ThrowableProcedure;
import com.devives.commons.lang.function.ThrowableProcedure1;

/**
 * Класс содержит методы, обеспечивающие работу конструкции try-catch-finally,
 * при которой последовательно возникшие в блоках исключения не будут потеряны.
 * <p>
 * При этом главным исключением (которое будет выброшено из блока) будет последнее возникшее,
 * а остальные исключения будут вложены друг в друга в обратном порядке как Suppressed Exception
 *
 * @param <R> тип возвращаемого значения конструкции Try. Если try ничего не должен возвращать, следует передать Void
 */
public abstract class TryAbst<R> {

    private final ThrowableProcedure onTry;
    private ThrowableProcedure1<Throwable> onCatch;
    private ThrowableProcedure onFinally;
    private R result = null;

    /**
     * Интерфейс, благодаря которому после блока onCatch может быть добавлен только блок onFinally или запущен процесс выполнения.
     */
    public interface CatchTry<R> {
        /**
         * Метод инициализирует блок кода onFinally, который будет выполнен после выполения onTry и onCatch(если такой блок используется).
         * Если в блоке onFinally возникнет исключение, то исключенеия, которые могли возникнуть в блоках onTry/onCatch, будут добавлены как suppressed.
         *
         * @param onFinally блок кода finally
         */
        FinallyTry<R> doFinally(ThrowableProcedure onFinally);

        /**
         * Метод запускает выполнение конструкции try-catch-finally и возвращает значение, если это требуется.<br/>
         * Метод обеспечивает обработку цепочки исключений, возникших в каждом из блоков конструкции try-catch-finally с
         * добавлением подавленных исключений в Exception.addSuppressed()
         */
        R call();
    }

    /**
     * Интерфейс, благодаря которому после блока onFinally не могут быть добавлены другие блоки, а можно только лишь запустить процесс выполнения.
     */
    public interface FinallyTry<R> {
        /**
         * Метод запускает выполнение конструкции try-catch-finally и возвращает значение, если это требуется.<br/>
         * Метод обеспечивает обработку цепочки исключений, возникших в каждом из блоков конструкции try-catch-finally с
         * добавлением подавленных исключений в Exception.addSuppressed()
         */
        R call();
    }

    /**
     * @param onTry блок кода try
     */
    TryAbst(ThrowableProcedure onTry) {
        this.onTry = onTry;
    }

    /**
     * Конструктор инициализирует onTry без возвращаемого значения как лямбду,
     * инициализирующую поле {@link TryAbst#result} значением, возвращаемым onTryWithResult.
     *
     * @param onTryWithResult блок кода try с возвращаемым значением
     */
    TryAbst(ThrowableFunction<R> onTryWithResult) {
        this.onTry = () -> result = onTryWithResult.apply();
    }

    /**
     * Метод инициализирует блок кода onCatch, который будет выполнен только в том случае, если в блоке onTry возникнет исключение.
     * При этом если исключение возникнет в блоке onCatch, исключение, возникшее в блоке onTry, будет добавлено как suppressed.
     *
     * @param onCatch блок кода catch, в качестве аргумента принимает исключение, возникшее в блоке {@link Try#onTry}
     */
    CatchTry<R> doCatch(ThrowableProcedure1<Throwable> onCatch) {
        this.onCatch = onCatch;
        return new CatchTry<R>() {
            @Override
            public FinallyTry<R> doFinally(ThrowableProcedure onFinally) {
                return TryAbst.this.doFinally(onFinally);
            }

            @Override
            public R call() {
                return TryAbst.this.call();
            }
        };
    }

    /**
     * Метод инициализирует onCatch без возвращаемого значения как лямбду,
     * инициализирующую поле {@link TryAbst#result} значением, возвращаемым onCatchWithResult.<br/>
     * OnCatch будет выполнен только в том случае, если в блоке onTry возникнет исключение.
     * При этом если исключение возникнет в блоке onCatch, исключение, возникшее в блоке onTry, будет добавлено как suppressed.
     *
     * @param onCatchWithResult блок кода catch с возвращаемым занчением типа R.
     *                          В качестве аргумента принимает исключение, возникшее в блоке {@link Try#onTry}
     */
    CatchTry<R> doCatch(ThrowableFunction1<Throwable, R> onCatchWithResult) {
        return doCatch((ThrowableProcedure1<Throwable>) (e) -> result = onCatchWithResult.apply(e));
    }

    /**
     * Метод инициализирует блок кода onFinally, который будет выполнен после выполнения onTry и onCatch(если такой блок используется).
     * Если в блоке onFinally возникнет исключение, то исключение, которые могли возникнуть в блоках onTry/onCatch, будут добавлены как suppressed.
     *
     * @param onFinally блок кода finally
     */
    public FinallyTry<R> doFinally(ThrowableProcedure onFinally) {
        this.onFinally = onFinally;
        return new FinallyTry<R>() {
            @Override
            public R call() {
                return TryAbst.this.call();
            }
        };
    }

    /**
     * Метод запускает выполнение конструкции try-catch-finally и возвращает значение, если это требуется.<br/>
     * Метод обеспечивает обработку цепочки исключений, возникших в каждом из блоков конструкции try-catch-finally с
     * добавлением подавленных исключений в Exception.addSuppressed()
     */
    private R call() {
        Throwable unhandledThrowable = null;
        try {
            onTry.accept();
        } catch (Throwable e) {
            unhandledThrowable = e;
        }

        if (onCatch != null && unhandledThrowable != null) {
            try {
                onCatch.accept(unhandledThrowable);
                unhandledThrowable = null;
            } catch (Throwable e) {
                if (e != unhandledThrowable) {
                    e.addSuppressed(unhandledThrowable);
                    unhandledThrowable = e;
                }
            }
        }

        if (onFinally != null) {
            try {
                onFinally.accept();
            } catch (Throwable e) {
                if (unhandledThrowable != null) {
                    e.addSuppressed(unhandledThrowable);
                }
                unhandledThrowable = e;
            }
        }

        if (unhandledThrowable != null) {
            throw ExceptionUtils.asUnchecked(unhandledThrowable);
        }
        return result;
    }
}