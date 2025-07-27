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
package com.devives.commons.io.store;

/**
 * Менеджер чанков для организации хранения байтовых данных с произвольным доступом.
 * <p>
 * Позволяет получать чанк по позиции, создавать новые чанки, удалять и перемещаться между чанками.
 * Используется для эффективного управления крупными массивами данных, разбитыми на части (чанки).
 *
 * @param <C> тип чанка
 */
public interface ChunkManager<C extends Chunk> {

    /**
     * Возвращает количество чанков в менеджере.
     *
     * @return количество.
     */
    int getChunkCount();

    /**
     * Возвращает максимальную вместимость одного чанка (в байтах).
     *
     * @return максимальный размер чанка.
     */
    int getChunkMaxCapacity();

    /**
     * Возвращает локатор чанка, содержащего байт по указанной позиции.
     *
     * @param position позиция байта в общем хранилище.
     * @return локатор чанка.
     * @throws IndexOutOfBoundsException если позиция вне диапазона.
     */
    Locator<C> getChunkByPosition(long position);

    /**
     * Ищет локатор чанка по позиции, либо возвращает null, если позиция вне диапазона.
     *
     * @param position позиция байта.
     * @return локатор чанка или null.
     * @throws IndexOutOfBoundsException если позиция больше размера хранилища.
     */
    Locator<C> findChunkByPosition(long position);

    /**
     * Возвращает локатор существующего чанка по позиции, либо создаёт новый чанк, если требуется.
     *
     * @param position позиция байта.
     * @return локатор чанка.
     * @throws IndexOutOfBoundsException если позиция вне диапазона.
     */
    Locator<C> getOrCreateChunkByPosition(long position);

    /**
     * Возвращает локатор следующего чанка относительно указанного локатора.
     *
     * @param locator текущий локатор.
     * @return локатор следующего чанка или null, если это последний чанк.
     */
    Locator<C> findNextChunk(Locator<C> locator);

    /**
     * Возвращает локатор следующего чанка, создавая его при необходимости.
     *
     * @param locator текущий локатор.
     * @return локатор следующего чанка.
     */
    Locator<C> getOrCreateNextChunk(Locator<C> locator);

    /**
     * Вставляет новый чанк перед указанным локатором с заданной вместимостью.
     *
     * @param locator          локатор, перед которым вставляется чанк.
     * @param requiredCapacity требуемая вместимость нового чанка.
     * @return локатор нового чанка.
     */
    Locator<C> insertPriorChunk(Locator<C> locator, int requiredCapacity);

    /**
     * Вставляет новый чанк после указанного локатора с заданной вместимостью.
     *
     * @param locator          локатор, после которого вставляется чанк.
     * @param requiredCapacity требуемая вместимость нового чанка.
     * @return локатор нового чанка.
     */
    Locator<C> insertNextChunk(Locator<C> locator, int requiredCapacity);

    /**
     * Разделяет чанк по указанной позиции, создавая новый чанк с оставшимися данными.
     *
     * @param locator  локатор исходного чанка.
     * @param position глобальная позиция разделения.
     * @return локатор исходного чанка после разделения.
     */
    Locator<C> splitChunk(Locator<C> locator, long position);

    /**
     * Удаляет чанк, указанный локатором.
     *
     * @param locator локатор удаляемого чанка.
     * @return локатор следующего чанка или null, если чанков не осталось.
     */
    Locator<C> removeChunk(Locator<C> locator);

    /**
     * Очищает все чанки и сбрасывает состояние менеджера.
     */
    void clear();

    /**
     * Интерфейс локатора для доступа к чанку и его методам.
     * <p>
     * Локатор содержит информацию о параметрах чанка в текущий момент времени. Локатор становится невалидным,
     * после любого изменения в хранилище данных.
     *
     * @param <C> тип чанка
     */
    interface Locator<C extends Chunk> {
        /**
         * Возвращает чанк.
         *
         * @return чанк.
         * @throws IllegalStateException если локатор невалиден.
         */
        C getChunk();

        /**
         * Возвращает индекс чанка.
         *
         * @return индекс.
         * @throws IllegalStateException если локатор невалиден.
         */
        int getIndex();

        /**
         * Возвращает смещение чанка относительно начала хранилища.
         *
         * @return смещение.
         * @throws IllegalStateException если локатор невалиден.
         */
        long getOffset();
    }
}