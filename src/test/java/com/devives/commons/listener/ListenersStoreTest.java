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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class ListenersStoreTest {

    @Test
    public void add_ExpectedSize() throws Exception {
        Listeners<Integer> listeners = new ListenersStore<>(new ArrayList<>());
        Integer o = new Integer(0);
        listeners.add(o);
        listeners.add(o);
        Assertions.assertEquals(2, listeners.size());
    }

    @Test
    public void addFirst_ExpectedIndex() throws Exception {
        Listeners<Integer> listeners = new ListenersStore<>(new ArrayList<>());
        listeners.add(1);
        listeners.addFirst(2);
        Assertions.assertEquals(0, Arrays.binarySearch(listeners.toArray(), 2));
    }

    @Test
    public void addBefore_ExpectedIndex() throws Exception {
        Listeners<Integer> listeners = new ListenersStore<>(new ArrayList<>());
        listeners.add(1);
        listeners.addBefore(2, 1);
        Assertions.assertEquals(0, Arrays.binarySearch(listeners.toArray(), 2));
    }

    @Test
    public void addAfter_ExpectedIndex() throws Exception {
        Listeners<Integer> listeners = new ListenersStore<>(new ArrayList<>());
        listeners.add(1);
        listeners.addAfter(2, 1);
        Assertions.assertEquals(1, Arrays.binarySearch(listeners.toArray(), 2));
    }

    @Test
    public void size_ExpectedSize() throws Exception {
        Listeners<Integer> listeners = new ListenersStore<>(new ArrayList<>());
        listeners.add(1);
        listeners.add(2);
        Assertions.assertEquals(2, listeners.size());
    }

    @Test
    public void clear_ExpectedSize() throws Exception {
        Listeners<Integer> listeners = new ListenersStore<>(new ArrayList<>());
        listeners.add(1);
        listeners.add(2);
        listeners.clear();
        Assertions.assertEquals(0, listeners.size());
    }

    @Test
    public void remove_ExpectedSize() throws Exception {
        Listeners<Integer> listeners = new ListenersStore<>(new ArrayList<>());
        listeners.add(1);
        listeners.add(2);
        listeners.add(3);
        listeners.remove(2);
        Assertions.assertEquals(2, listeners.size());
    }

    @Test
    public void contains_True() throws Exception {
        Listeners<Integer> listeners = new ListenersStore<>(new ArrayList<>());
        listeners.add(1);
        listeners.add(2);
        listeners.add(3);
        Assertions.assertTrue(listeners.contains(2));
    }

}
