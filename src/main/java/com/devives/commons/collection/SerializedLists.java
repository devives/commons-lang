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
package com.devives.commons.collection;

import com.devives.commons.collection.store.BufferedSerializedStore;
import com.devives.commons.collection.store.BufferedStoreAsListAdapter;
import com.devives.commons.collection.store.SerializedStore;
import com.devives.commons.collection.store.StoreAsListAdapter;
import com.devives.commons.collection.store.serializer.*;
import com.devives.commons.io.store.AlignedByteStore;
import com.devives.commons.io.store.ArrayByteStore;
import com.devives.commons.io.store.ByteBufferStore;
import com.devives.commons.io.store.ByteStore;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

/**
 * Точка входа для создания списка элементов, сериализованных в чанки.
 * <p>
 * Предоставляет методы для создания списков с различными типами данных и конфигурациями.
 * <pre>{@code
 * List<Long> list = SerializedChunkedLists.ofLongs().setOffHeapChunkManager(256 * 1024).setBuffered().build();
 * }</pre>
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
public final class SerializedLists {

    private SerializedLists() {

    }

    public static Builder<Byte> ofBytes() {
        return new Builder<>(new ByteBinarySerializer());
    }

    public static Builder<Short> ofShorts() {
        return new Builder<>(new ShortBinarySerializer());
    }

    public static Builder<Integer> ofIntegers() {
        return new Builder<>(new IntegerBinarySerializer());
    }

    public static Builder<Long> ofLongs() {
        return new Builder<>(new LongBinarySerializer());
    }

    public static Builder<Float> ofFloats() {
        return new Builder<>(new FloatBinarySerializer());
    }

    public static Builder<Double> ofDoubles() {
        return new Builder<>(new DoubleBinarySerializer());
    }

    public static <E> Builder<E> of(BinarySerializer<E> binarySerializer) {
        return new Builder<E>(binarySerializer);
    }

    /**
     * Строитель списка разбитого на чанки.
     *
     * @param <E> тип элементов.
     */
    public static final class Builder<E> extends AbstractBuilder<E, Builder<E>> {

        /**
         * Конструктор строителя.
         *
         * @param binarySerializer сериализатор элементов.
         */
        private Builder(BinarySerializer<E> binarySerializer) {
            super(binarySerializer);
        }

        @Override
        public List<E> build() {
            final int elementSize = binarySerializer_.getElementSize();
            final ByteStore mainStore = byteStore_ != null ? byteStore_ : new ArrayByteStore();
            final AlignedByteStore mainAlignedStore = new AlignedByteStore(mainStore, elementSize);
            final SerializedStore<E> elementStore = new SerializedStore<E>(binarySerializer_, mainAlignedStore);
            return new StoreAsListAdapter<>(elementStore);
        }
    }

    /**
     * Строитель буферизированного списка разбитого на чанки.
     *
     * @param <E> тип элементов.
     */
    public static final class BufferedBuilder<E> extends AbstractBuilder<E, BufferedBuilder<E>> {

        protected ByteStore bufferByteStore_;

        /**
         * Конструктор строителя.
         *
         * @param binarySerializer сериализатор элементов.
         */
        private BufferedBuilder(BinarySerializer<E> binarySerializer) {
            super(binarySerializer);
        }

        @Override
        public BufferedBuilder<E> setBufferByteStore(ByteStore bufferByteStore) {
            bufferByteStore_ = Objects.requireNonNull(bufferByteStore, "bufferByteStore");
            return this;
        }

        public BufferedList<E> build() {
            final int elementSize = binarySerializer_.getElementSize();
            final ByteStore mainStore = byteStore_ != null ? byteStore_ : new ArrayByteStore();
            final AlignedByteStore mainAlignedStore = new AlignedByteStore(mainStore, elementSize);
            final SerializedStore<E> bufferStore = new SerializedStore<E>(
                    binarySerializer_,
                    new AlignedByteStore(bufferByteStore_, elementSize));
            final BufferedSerializedStore<E> elementStore = new BufferedSerializedStore<E>(mainAlignedStore, bufferStore);
            return new BufferedStoreAsListAdapter<>(elementStore);
        }
    }

    /**
     * Абстрактный строитель списка разбитого на чанки.
     *
     * @param <E>    тип элементов
     * @param <SELF> тип строителя
     */
    public static abstract class AbstractBuilder<E, SELF extends AbstractBuilder<E, SELF>> {
        protected final BinarySerializer<E> binarySerializer_;
        protected ByteStore byteStore_;

        protected AbstractBuilder(BinarySerializer<E> binarySerializer) {
            binarySerializer_ = Objects.requireNonNull(binarySerializer, "binarySerializer");
        }


        public SELF setByteStore(ByteStore ByteStore) {
            byteStore_ = Objects.requireNonNull(ByteStore, "ByteStore");
            return (SELF) this;
        }

        /**
         * @return текущий экземпляр строителя.
         */
        public SELF setArrayByteStore() {
            byteStore_ = new ArrayByteStore();
            return (SELF) this;
        }

        /**
         * @return текущий экземпляр строителя.
         */
        public SELF setHeapByteStore() {
            byteStore_ = new ByteBufferStore(ByteBuffer::allocate);
            return (SELF) this;
        }

        /**
         * @return текущий экземпляр строителя.
         */
        public SELF setHeapByteStore(int initialCapacity) {
            byteStore_ = new ByteBufferStore(ByteBuffer::allocate, initialCapacity);
            return (SELF) this;
        }

        /**
         * @return текущий экземпляр строителя.
         */
        public SELF setOffHeapByteStore() {
            byteStore_ = new ByteBufferStore(ByteBuffer::allocateDirect);
            return (SELF) this;
        }

        /**
         * @return текущий экземпляр строителя.
         */
        public SELF setOffHeapByteStore(int initialCapacity) {
            byteStore_ = new ByteBufferStore(ByteBuffer::allocateDirect, initialCapacity);
            return (SELF) this;
        }

        public BufferedBuilder<E> setBuffered() {
            return setBufferByteStore(new ArrayByteStore());
        }

        public BufferedBuilder<E> setBufferByteStore(ByteStore bufferByteStore) {
            Objects.requireNonNull(bufferByteStore, "bufferByteStore");
            BufferedBuilder<E> bufferedBuilder = new BufferedBuilder<>(binarySerializer_);
            bufferedBuilder.byteStore_ = byteStore_;
            bufferedBuilder.bufferByteStore_ = bufferByteStore;
            return bufferedBuilder;
        }

        public abstract List<E> build();

    }
}
