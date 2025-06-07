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
package com.devives.commons.listener;

/**
 * Interface for managing a collection of listeners.
 *
 * @param <I> the type of listener.
 *
 * @since 0.2.0
 */
public interface Listeners<I> {

    /**
     * Adds a listener to the end of the collection.
     *
     * @param item the listener to add.
     */
    void add(I item);

    /**
     * Adds a listener to the beginning of the collection.
     *
     * @param item the listener to add.
     */
    void addFirst(I item);

    /**
     * Adds a listener before a specified listener in the collection.
     *
     * @param item the listener to add.
     * @param prior the listener before which to add the new listener.
     */
    void addBefore(I item, I prior);

    /**
     * Adds a listener after a specified listener in the collection.
     *
     * @param item the listener to add.
     * @param next the listener after which to add the new listener.
     */
    void addAfter(I item, I next);

    /**
     * Checks if a listener is in the collection.
     *
     * @param item the listener to check.
     * @return true if the listener is in the collection, false otherwise.
     */
    boolean contains(I item);

    /**
     * Removes a listener from the collection.
     *
     * @param item the listener to remove.
     */
    void remove(I item);

    /**
     * Removes all listeners from the collection.
     */
    void clear();

    /**
     * Returns the number of listeners in the collection.
     *
     * @return the number of listeners.
     */
    int size();

    /**
     * Returns an array containing all the listeners in the collection.
     *
     * @return new instance of array or cached instance if {@link CachedListeners} used.
     */
    Object[] toArray();

    /**
     * Return the new instance of {@link ListenersBuilder}.
     *
     * @return new instance of builder.
     */
    static ListenersBuilder builder() {
        return new ListenersBuilder();
    }
}
