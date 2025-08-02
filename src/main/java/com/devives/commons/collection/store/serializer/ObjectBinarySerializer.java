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

import com.devives.commons.lang.ExceptionUtils;

import java.io.*;

/**
 * @param <E> тип объектов.
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
public class ObjectBinarySerializer<E> implements BinarySerializer<E> {

    private final int size_;

    public ObjectBinarySerializer(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("Size can not be lower than '1'. Actual: '" + size + "'.");
        }
        size_ = size;
    }

    @Override
    public void serialize(DataOutput output, E element) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(128);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(element);
        objectOutputStream.flush();
        byte[] bytes = outputStream.toByteArray();
        output.writeInt(bytes.length);
        output.write(bytes);
        if (bytes.length < getElementSize()) {
            byte[] emptyBytes = new byte[getElementSize() - bytes.length - 4];
            output.write(emptyBytes);
        }
    }

    @Override
    public E deserialize(DataInput input) throws IOException {
        int length = input.readInt();
        byte[] bytes = new byte[length];
        input.readFully(bytes);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        Object element = ExceptionUtils.passChecked(() -> objectInputStream.readObject());
        return (E) element;
    }

    @Override
    public int getElementSize() {
        return size_;
    }
}
