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

import com.devives.commons.lang.function.ThrowableProcedure;
import com.devives.commons.lang.function.ThrowableProcedure1;

/**
 * Класс предназначен для использования конструкции Try без возвращаемого значения
 * <p/>
 * Примечание по работе с локальными переменными: Так как изменить значения локальных переменных в лямбда выражениях нельзя,
 * приходится использовать обертку {@link com.devives.commons.lang.Ref}, у которой с помощью геттера и сеттера можно менять
 * значение переменной.
 * <p/>
 * Пример использования:
 * <blockquote><pre>
 * try {
 *      onTryFunc();
 * } catch (Throwable e){
 *      onCatchFunc();
 * } finally {
 *      onFinallyFunc();
 * }
 * </pre></blockquote>
 * <blockquote><pre>
 * new Try(() -> {
 *      onTryFunc();
 * }).onCatch((e) -> {
 *      onCatchFunc();
 * }).onFinally(() -> {
 *      onFinallyFunc();
 * }).call();
 *
 * </pre></blockquote>
 */
public class Try extends TryAbst<Void> {

    /**
     * Конструктор инициализирует блок кода onTry, который не имеет возвращаемого значения.
     *
     * @param onTry блок кода Try
     */
    public Try(ThrowableProcedure onTry) {
        super(onTry);
    }

    @Override
    public CatchTry<Void> doCatch(ThrowableProcedure1<Throwable> onCatch) {
        return super.doCatch(onCatch);
    }
}
