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


import com.devives.commons.lang.function.ThrowableFunction;
import com.devives.commons.lang.function.ThrowableFunction1;

/**
 * Класс предназначен для использования конструкции Try с возвращаемым значением.
 * <p>
 * Пример использования:
 * <blockquote><pre>{@code
 * Boolean result = null;
 * try {
 *      onTryFunc();
 *      result = true;
 * } catch (Throwable e){
 *      onCatchFunc();
 *      result = false;
 * } finally {
 *      onFinallyFunc();
 * }
 * }</pre></blockquote>
 * <blockquote><pre>{@code
 * Boolean result = new TryWithResult<>(() -> {
 *      onTryWithResult();
 *      return true;
 * }).doCatch((e) -> {
 *      onCatchWithResult();
 *      return false;
 * }).doFinally(() -> {
 *      onFinallyWithResult();
 * }).call();
 * }</pre></blockquote>
 *
 * @param <R> тип возвращаемого значения конструкции.
 */
public class TryWithResult<R> extends TryAbst<R> {

    /**
     * Конструктор инициализирует блок кода onTry с возвращаемым значением
     *
     * @param onTryWithResult блок кода Try, возвращающий значение типа R
     */
    public TryWithResult(ThrowableFunction<R> onTryWithResult) {
        super(onTryWithResult);
    }

    @Override
    public CatchTry<R> doCatch(ThrowableFunction1<Throwable, R> onCatchWithResult) {
        return super.doCatch(onCatchWithResult);
    }
}