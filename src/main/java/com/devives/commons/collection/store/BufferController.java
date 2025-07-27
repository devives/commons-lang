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
package com.devives.commons.collection.store;

/**
 * Interface of the buffer controller.
 * <p>
 * The buffer optimizes the operations of reading/writing elements from/to the element store.
 */
public interface BufferController {

    /**
     * Returns the size of the buffer for reading elements from store.
     * <p>
     * This is the maximum number of elements that will be read from the storage in a single read operation.
     *
     * @return size, in pieces.
     */
    int getBufferSize();

    /**
     * Sets the {@link #getBufferSize()} property.
     * <p>
     * When setting a new value:
     * <ol>
     *     <li>{@link #flushBuffer()} and buffer clearing are performed to change the size.
     *     <li>{link {@link #getBufferMaxSize()}} is set at {@code size * 2}.
     * </ol>
     *
     * @param size size, in pieces. Minimum is: {@code 1}.
     */
    void setBufferSize(int size);

    /**
     * Returns the maximum size of the buffer while adding elements.
     * <p>
     * When an element adding in to the buffered list, it's firstly placed in to buffer. After adding, the number
     * of elements in the buffer may exceed {@link #getBufferSize()}. The {@link #getBufferMaxSize()} defines the
     * number of elements that, when exceeded, will cause flush the buffer.
     *
     * @return size, in pieces.
     */
    int getBufferMaxSize();

    /**
     * Sets the {@link #getBufferMaxSize()} property.
     * <p>
     * When setting a new value, {@link #flushBuffer()} and buffer clearing are performed to change the size.
     *
     * @param size size, in pieces. Minimum is: {@code getBufferSize() + 1}.
     */
    void setBufferMaxSize(int size);

    /**
     * Writes changes contained in the buffer to the storage and clears the buffer.
     */
    void flushBuffer();
}
