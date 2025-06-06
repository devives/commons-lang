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
package com.devives.commons.lang.listener;

import com.devives.commons.lang.exception.AggregateException;
import com.devives.commons.lang.publisher.Publisher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PublisherTest {

    @Test
    public void build_nonNull() throws Exception {
        Publisher<TestListener> publisher = Publisher.<TestListener>builder().build();
        Assertions.assertNotNull(publisher);
    }

    @Test
    public void build_setListenerPresenceCheck_nonNull() throws Exception {
        Publisher<TestListener> publisher = Publisher.<TestListener>builder().setListenerPresenceCheck().build();
        Assertions.assertNotNull(publisher);
    }

    @Test
    public void build_SetIndependentDelivery_nonNull() throws Exception {
        Publisher<TestListener> publisher = Publisher.<TestListener>builder().setIndependentDelivery().build();
        Assertions.assertNotNull(publisher);
    }

    @Test
    public void build_SetDistinctListeners_nonNull() throws Exception {
        Publisher<TestListener> publisher = Publisher.<TestListener>builder().listeners(builder -> builder.setDistinct()).build();
        Assertions.assertNotNull(publisher);
    }

    @Test
    public void publish_NonListenerPresenceCheck_Listener2Fired() throws Exception {
        Publisher<TestListener> publisher = Publisher.<TestListener>builder().build();
        TestListenerImpl listener2 = new TestListenerImpl();
        publisher.getListeners().add(new TestListenerImpl() {
            @Override
            public void someHandler1(String arg1) {
                publisher.getListeners().remove(listener2);
            }
        });
        publisher.getListeners().add(listener2);
        publisher.publish(listener -> listener.someHandler1("1"));
        Assertions.assertTrue(listener2.isFired1());
    }

    @Test
    public void publish_ListenerPresenceCheck_Listener2NotFired() throws Exception {
        Publisher<TestListener> publisher = Publisher.<TestListener>builder().setListenerPresenceCheck().build();
        TestListenerImpl listener2 = new TestListenerImpl();
        publisher.getListeners().add(new TestListenerImpl() {
            @Override
            public void someHandler1(String arg1) {
                publisher.getListeners().remove(listener2);
            }
        });
        publisher.getListeners().add(listener2);
        publisher.publish(listener -> listener.someHandler1("1"));
        Assertions.assertFalse(listener2.isFired1());
    }

    @Test
    public void publish_DependentPublish_Listener2NotFired() throws Exception {
        Publisher<TestListener> publisher = Publisher.<TestListener>builder().build();
        TestListenerImpl listener2 = new FailureTestListenerImpl();
        publisher.getListeners().add(new FailureTestListenerImpl());
        publisher.getListeners().add(listener2);
        Assertions.assertThrows(RuntimeException.class, () -> publisher.publish(listener -> listener.someHandler1("1")));
        Assertions.assertFalse(listener2.isFired1());
    }

    @Test
    public void publish_IndependentPublish_Listener2Fired() throws Exception {
        Publisher<TestListener> publisher = Publisher.<TestListener>builder().setIndependentDelivery().build();
        TestListenerImpl listener2 = new FailureTestListenerImpl();
        publisher.getListeners().add(new FailureTestListenerImpl());
        publisher.getListeners().add(listener2);
        Assertions.assertThrows(AggregateException.class, () -> publisher.publish(listener -> listener.someHandler1("1")));
        Assertions.assertTrue(listener2.isFired1());
    }

    @Test
    public void publish_MutableDependentPublish_Listener2NotFired() throws Exception {
        Publisher<TestListener> publisher = Publisher.<TestListener>builder().setListenerPresenceCheck().build();
        TestListenerImpl listener2 = new FailureTestListenerImpl();
        publisher.getListeners().add(new FailureTestListenerImpl());
        publisher.getListeners().add(listener2);
        Assertions.assertThrows(RuntimeException.class, () -> publisher.publish(listener -> listener.someHandler1("1")));
        Assertions.assertFalse(listener2.isFired1());
    }

    @Test
    public void publish_MutableIndependentPublish_Listener2Fired() throws Exception {
        Publisher<TestListener> publisher = Publisher.<TestListener>builder().setListenerPresenceCheck().setIndependentDelivery().build();
        TestListenerImpl listener2 = new FailureTestListenerImpl();
        publisher.getListeners().add(new FailureTestListenerImpl());
        publisher.getListeners().add(listener2);
        Assertions.assertThrows(AggregateException.class, () -> publisher.publish(listener -> listener.someHandler1("1")));
        Assertions.assertTrue(listener2.isFired1());
    }

    private interface TestListener {
        void someHandler1(String arg1);

        void someHandler2(String arg1, Integer arg2);
    }

    private static class TestListenerImpl implements TestListener {
        private volatile boolean fired1_ = false;
        private volatile boolean fired2_ = false;

        @Override
        public void someHandler1(String arg1) {
            fired1_ = true;
        }

        @Override
        public void someHandler2(String arg1, Integer arg2) {
            fired2_ = false;
        }

        public boolean isFired1() {
            return fired1_;
        }

        public boolean isFired2() {
            return fired2_;
        }
    }

    private static class FailureTestListenerImpl extends TestListenerImpl {

        @Override
        public void someHandler1(String arg1) {
            super.someHandler1(arg1);
            throw new RuntimeException("Test exception");
        }

        @Override
        public void someHandler2(String arg1, Integer arg2) {
            super.someHandler2(arg1, arg2);
            throw new RuntimeException("Test exception");
        }

    }
}
