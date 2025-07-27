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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Buffered object storage.
 *
 * @param <E> Element type.
 */
public final class BufferedObjectStore<E> extends AbstractBufferedStore<E> {
    /**
     * Main element store.
     */
    private final Store<E> mainStore_;

    /**
     * Constructor.
     *
     * @param mainStore   main element store.
     */
    public BufferedObjectStore(Store<E> mainStore) {
        this(mainStore, new ObjectStore<>());
    }

    /**
     * Constructor.
     *
     * @param mainStore   main element store.
     * @param bufferStore element buffer store.
     */
    public BufferedObjectStore(Store<E> mainStore, Store<E> bufferStore) {
        super(new Buffer(bufferStore, mainStore));
        mainStore_ = Objects.requireNonNull(mainStore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return mainStore_.size() + (buffer_.size() - buffer_.getLoadedCount());
    }

    private final static class Buffer<E> extends AbstractBuffer<E> {

        private final Store<E> bufferStore_;
        private final Store<E> mainStore_;

        public Buffer(Store<E> bufferStore, Store<E> mainStore) {
            super(bufferStore);
            bufferStore_ = Objects.requireNonNull(bufferStore, "bufferStore");
            mainStore_ = Objects.requireNonNull(mainStore, "mainStore");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void load(int fromIndex, int toIndex) {
            mainOffset_ = fromIndex;
            mainCount_ = 0;
            bufferStore_.clear();
            mainStore_.getRange(fromIndex, toIndex, bufferStore_);
            mainCount_ = bufferStore_.size();
            modified_ = false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void commit() {
            if (modified_) {
                Collection<E> collection;
                if (bufferStore_ instanceof Collection) {
                    collection = (Collection<E>) bufferStore_;
                } else if (bufferStore_ instanceof Iterable) {
                    collection = StreamSupport.stream(((Iterable<E>) bufferStore_).spliterator(), false).collect(Collectors.toList());
                } else {
                    ArrayList<E> elements = new ArrayList<>(bufferStore_.size());
                    for (int i = 0; i < bufferStore_.size(); i++) {
                        elements.add(bufferStore_.get(i));
                    }
                    collection = elements;
                }
                mainStore_.replaceRange(mainOffset_, mainOffset_ + mainCount_, collection);
                mainCount_ = bufferStore_.size();
                modified_ = false;
            }
        }

    }

}
