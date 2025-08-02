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

import com.devives.commons.collection.store.serializer.BinarySerializer;
import com.devives.commons.io.store.AlignedByteStore;
import com.devives.commons.io.store.ArrayByteStore;
import com.devives.commons.lang.ExceptionUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Objects;

/**
 * The class implements a storage of &lt;E&gt; elements in serialized form.
 *
 * @param <E> Type of elements.
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
public final class SerializedStore<E> extends AbstractStore<E> implements Serialized {

    private final AlignedByteStore alignedByteStore_;
    private final ElementMarshaller elementMarshaller_;
    private int writeBufferSize_;

    /**
     * Creates an instance of the object store in serialized form based on the element converter and the byte store.
     *
     * @param binarySerializer the element to binary and vice versa converter.
     * @param alignedByteStore the aligned byte store.
     */
    public SerializedStore(BinarySerializer<E> binarySerializer, AlignedByteStore alignedByteStore) {
        alignedByteStore_ = Objects.requireNonNull(alignedByteStore);
        elementMarshaller_ = new ElementMarshaller(binarySerializer);
        writeBufferSize_ = Math.max(1, 16 * 1024 / getElementSize());
    }

    /**
     * Return the write buffer size, in elements.
     *
     * @return size, in elements.
     */
    public int getWriteBufferSize() {
        return writeBufferSize_;
    }

    /**
     * Set the property {@link #getWriteBufferSize()}.
     *
     * @param value new value, in elements.
     */
    public void setWriteBufferSize(int value) {
        if (value < 1) {
            throw new IllegalArgumentException("The buffer size must be greater than '0'.");
        }
        writeBufferSize_ = value;
    }

    @Override
    public void insert(int index, E element) {
        alignedByteStore_.insert(index, elementMarshaller_.serialize(element));
    }


    @Override
    public E get(int index) {
        return elementMarshaller_.deserialize(index);
    }

    @Override
    public void addRange(int fromIndex, Collection<E> elements) {
        final ByteBuffer elementsByteBuffer = ByteBuffer.allocate(elementMarshaller_.getElementSize() * writeBufferSize_);
        int index = fromIndex;
        int count = 0;
        for (E element : elements) {
            elementsByteBuffer.put(elementMarshaller_.serialize(element));
            count++;
            if (count == writeBufferSize_) {
                elementsByteBuffer.position(0);
                alignedByteStore_.insert(index, elementsByteBuffer);
                elementsByteBuffer.position(0);
                index += count;
                count = 0;
            }
        }
        if (count > 0) {
            elementsByteBuffer.flip();
            alignedByteStore_.insert(index, elementsByteBuffer);
        }
    }

    @Override
    public void replaceRange(int fromIndex, int toIndex, Collection<E> elements) {
        final ByteBuffer elementsByteBuffer = ByteBuffer.allocate(getElementSize() * writeBufferSize_);
        int index = fromIndex;
        int count = 0;

        int removingCount = toIndex - fromIndex;
        int writingCount = elements.size();
        int diffCount = writingCount - removingCount;
        if (diffCount <= 0) {
            for (E element : elements) {
                elementsByteBuffer.put(elementMarshaller_.serialize(element));
                count++;
                if (count == writeBufferSize_) {
                    elementsByteBuffer.position(0);
                    alignedByteStore_.replaceRange(index, index + count, elementsByteBuffer);
                    elementsByteBuffer.position(0);
                    index += count;
                    count = 0;
                }
            }
            if (count > 0) {
                elementsByteBuffer.flip();
                alignedByteStore_.replaceRange(index, toIndex, elementsByteBuffer);
            } else if (index < toIndex) {
                alignedByteStore_.removeRange(index, toIndex);
            }
        } else {
            // This is not optimal. Firstly shift data by `diffCount*getElementSize()` in `alignedByteStore_` and
            // replacing regions - is better.
            for (E element : elements) {
                elementsByteBuffer.put(elementMarshaller_.serialize(element));
                count++;
                if (count == writeBufferSize_) {
                    elementsByteBuffer.position(0);
                    if (index < toIndex) {
                        alignedByteStore_.replaceRange(index, Math.min(toIndex, index + count), elementsByteBuffer);
                    } else {
                        alignedByteStore_.insert(index, elementsByteBuffer);
                    }
                    elementsByteBuffer.position(0);
                    index += count;
                    count = 0;
                }
            }
            if (count > 0) {
                elementsByteBuffer.flip();
                if (index < toIndex) {
                    alignedByteStore_.replaceRange(index, Math.min(toIndex, index + count), elementsByteBuffer);
                } else {
                    alignedByteStore_.insert(index, elementsByteBuffer);
                }
            }
        }
    }

    @Override
    public void removeRange(int fromIndex, int toIndex) {
        alignedByteStore_.removeRange(fromIndex, toIndex);
    }

    @Override
    public int size() {
        return Math.toIntExact(alignedByteStore_.size());
    }

    public int getElementSize() {
        return elementMarshaller_.getElementSize();
    }

    /**
     * Return background byte store in which stored serialized elements.
     * <p>
     * Method is package protected for fast data replace.
     * </p>
     *
     * @return aligned byte store.
     */
    AlignedByteStore getAlignedByteStore() {
        return alignedByteStore_;
    }

    public byte[] getByteArray() {
        if (alignedByteStore_.getByteStore() instanceof ArrayByteStore) {
            return ((ArrayByteStore) alignedByteStore_.getByteStore()).getByteArray();
        }
        throw new UnsupportedOperationException("Can't return bytes. The background byte store is not an instance of ArrayByteStore.");
    }

    /**
     * The element marshaller.
     */
    private final class ElementMarshaller {
        private final BinarySerializer<E> binarySerializer_;
        // A reusable byte buffer and accessors used during serialization and deserialization.
        private final byte[] elementBytes_;
        private final ByteBuffer elementByteBuffer_;
        private final ReusableDataOutputStream elementDataOutput_;
        private final DataInputStream elementDataInput_;

        public ElementMarshaller(BinarySerializer<E> binarySerializer) {
            binarySerializer_ = Objects.requireNonNull(binarySerializer);
            if (binarySerializer_.getElementSize() < 1) {
                throw new IllegalArgumentException("The element size: " + binarySerializer_.getElementSize() + " < 1");
            }
            elementBytes_ = new byte[binarySerializer_.getElementSize()];
            elementByteBuffer_ = ByteBuffer.wrap(elementBytes_);
            elementDataInput_ = new DataInputStream(new ReusableByteArrayInputStream(elementBytes_));
            elementDataOutput_ = new ReusableDataOutputStream(new ReusableByteArrayOutputStream(elementBytes_));
        }

        public ByteBuffer serialize(E element) {
            try {
                elementDataOutput_.reset();
                binarySerializer_.serialize(elementDataOutput_, element);
                if (elementDataOutput_.size() != binarySerializer_.getElementSize()) {
                    throw new IOException("Invalid size " + elementDataOutput_.size() + " of written data. Require '" + binarySerializer_.getElementSize() + "' bytes.");
                }
                elementByteBuffer_.position(0);
                return elementByteBuffer_;
            } catch (IOException e) {
                throw ExceptionUtils.asUnchecked(e);
            }
        }

        public E deserialize(int index) {
            try {
                elementByteBuffer_.clear();
                int n = alignedByteStore_.read(index, elementByteBuffer_);
                if (n != 1) {
                    throw new IOException("Element '" + index + "' has not read.");
                }
                // It will reset an elementInputStream_.
                elementDataInput_.reset();
                E result = binarySerializer_.deserialize(elementDataInput_);
                return result;
            } catch (IOException e) {
                throw ExceptionUtils.asUnchecked(e);
            }
        }

        public int getElementSize() {
            return binarySerializer_.getElementSize();
        }
    }

}
