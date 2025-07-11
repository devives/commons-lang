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

public class DistinctListenersTest {

    @Test
    public void new_Duplicates_DuplicateExceptionThrown() throws Exception {
        Listeners<Integer> listeners = new ListenersBuilder().build();
        Integer o = new Integer(0);
        listeners.add(o);
        listeners.add(o);
        Assertions.assertThrows(DuplicateRegistrationException.class, () -> {
            new DistinctListeners(listeners);
        });
    }

    @Test
    public void add_Duplicates_DuplicateExceptionThrown() throws Exception {
        Listeners<Integer> listeners = new DistinctListeners(new ListenersStore<>(new ArrayList<>()));
        Integer o = new Integer(0);
        listeners.add(o);
        Assertions.assertThrows(DuplicateRegistrationException.class, () -> {
            listeners.add(o);
        });
    }

    @Test
    public void addFirst_Duplicates_DuplicateExceptionThrown() throws Exception {
        Listeners<Integer> listeners = new DistinctListeners(new ListenersStore<>(new ArrayList<>()));
        Integer o = new Integer(0);
        listeners.add(o);
        Assertions.assertThrows(DuplicateRegistrationException.class, () -> {
            listeners.addFirst(o);
        });
    }

    @Test
    public void addAfter_Duplicates_DuplicateExceptionThrown() throws Exception {
        Listeners<Integer> listeners = new DistinctListeners(new ListenersStore<>(new ArrayList<>()));
        Integer o = new Integer(0);
        listeners.add(o);
        Assertions.assertThrows(DuplicateRegistrationException.class, () -> {
            listeners.addAfter(o, o);
        });
    }

    @Test
    public void addBefore_Duplicates_DuplicateExceptionThrown() throws Exception {
        Listeners<Integer> listeners = new DistinctListeners(new ListenersStore<>(new ArrayList<>()));
        Integer o = new Integer(0);
        listeners.add(o);
        Assertions.assertThrows(DuplicateRegistrationException.class, () -> {
            listeners.addBefore(o, o);
        });
    }
}
