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
package com.devives.commons.lifecycle;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

abstract class AbstractLifeCycleBase extends StateObjAbst implements LifeCycle {

    private final CopyOnWriteArrayList<Listener<LifeCycle>> listeners_ = new CopyOnWriteArrayList<>();

    @Override
    public void addListener(Listener<LifeCycle> listener) {
        listeners_.add(Objects.requireNonNull(listener));
    }

    @Override
    public void removeListener(Listener<LifeCycle> listener) {
        listeners_.remove(Objects.requireNonNull(listener));
    }

    @Override
    public boolean isStarted() {
        return getStateHolder().get() == States.STARTED;
    }

    @Override
    public boolean isStarting() {
        return getStateHolder().get() == States.STARTING;
    }

    @Override
    public boolean isStopping() {
        return getStateHolder().get() == States.STOPPING;
    }

    @Override
    public boolean isStopped() {
        return getStateHolder().get() == States.STOPPED;
    }

    @Override
    public boolean isFailed() {
        return getStateHolder().get() == States.FAILED;
    }

    protected abstract void onStart() throws Exception;

    protected abstract void onStop() throws Exception;

    @Override
    public void start() throws Exception {
        State state = getStateHolder().get();
        if (state == States.STARTED || state == States.STARTING)
            throw new RuntimeException("Object can not be started twice.");
        try {
            setStarting();
            onStart();
            setStarted();
        } catch (Throwable e) {
            setFailed(e);
            throw e;
        }
    }

    @Override
    public void stop() throws Exception {
        State state = getStateHolder().get();
        if (state == States.STOPPING || state == States.STOPPED)
            return;
        try {
            setStopping();
            onStop();
            setStopped();
        } catch (Throwable e) {
            setFailed(e);
            throw e;
        }
    }

    private void setStarting() {
        getStateHolder().set(States.STARTING);
        onStarting();
        for (Listener<LifeCycle> listener : listeners_) {
            listener.onStarting(this);
        }
    }

    protected void onStarting() {

    }

    private void setStarted() {
        getStateHolder().set(States.STARTED);
        onStarted();
        for (Listener<LifeCycle> listener : listeners_) {
            listener.onStarted(this);
        }
    }

    protected void onStarted() {

    }

    private void setStopping() {
        getStateHolder().set(States.STOPPING);
        for (Listener<LifeCycle> listener : listeners_) {
            listener.onStopping(this);
        }
        onStopping();
    }

    protected void onStopping() {

    }

    private void setStopped() {
        getStateHolder().set(States.STOPPED);
        for (Listener<LifeCycle> listener : listeners_) {
            listener.onStopped(this);
        }
        onStopped();
    }

    protected void onStopped() {

    }


    private void setFailed(Throwable th) {
        getStateHolder().set(States.FAILED);
        onFailed(th);
        for (Listener<LifeCycle> listener : listeners_) {
            listener.onFailure(this, th);
        }
    }

    protected void onFailed(Throwable th) {

    }

    protected static abstract class States {
        public static final State STOPPING = StateFactory.named("STOPPING");
        public static final State STOPPED = StateFactory.named("STOPPED");
        public static final State STARTED = StateFactory.named("STARTED");
        public static final State STARTING = StateFactory.named("STARTING");
        public static final State FAILED = StateFactory.named("FAILED");
    }
}
