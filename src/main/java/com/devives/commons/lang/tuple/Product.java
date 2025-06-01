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
package com.devives.commons.lang.tuple;

/**
 * Интерфейс служит общим супер-типом для всех классов, экземпляры которых можно рассматривать как "произведение" своих полей.
 * <p>
 * Служит для унификации доступа к структуре данных, состоящих из нескольких полей.
 */
public interface Product {

    /**
     * Возвращает значение поля с указанным индексом.
     *
     * @param n индекс поля, начиная с "0".
     * @return
     */
    Object productElement(int n);

    /**
     * Возвращает размер продукта, равный числу его полей.
     * @return размер.
     */
    int productArity();

}
