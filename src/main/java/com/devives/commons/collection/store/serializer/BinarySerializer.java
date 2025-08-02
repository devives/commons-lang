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
package com.devives.commons.collection.store.serializer;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Interface of element transformer to a binary stream and back.
 *
 * @param <E> Type of element.
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
public interface BinarySerializer<E> {

    /**
     * The method writes the values of the fields of the serializable object to the stream.
     *
     * @param output  binary stream.
     * @param element list element.
     * @throws IOException on errors writing to the stream.
     */
    void serialize(DataOutput output, E element) throws IOException;

    /**
     * The method reads the values of the fields of the deserializable object from the stream.
     *
     * @param input binary stream positioned at the beginning of the object.
     * @return list element.
     * @throws IOException on errors reading from the stream.
     */
    E deserialize(DataInput input) throws IOException;

    /**
     * Returns the number of bytes that the serialized object data occupies.
     *
     * @return size, in bytes.
     */
    int getElementSize();

}

