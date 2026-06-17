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
package com.devives.commons.lang;


import com.devives.commons.state.State;
import com.devives.commons.state.StateHolder;
import com.devives.commons.state.SynchronizedStateHolder;
import com.devives.commons.state.SynchronizedStateHolderImpl;
import com.devives.commons.util.usage.UsageCounter;

import java.util.concurrent.CompletionStage;

public abstract class AbstractSynchronizedLazyCloseable extends CloseableBase implements UsageCounter {
    private static final long serialVersionUID = 1L;
    protected final SynchronizedLazyClosingDirector lazyClosingDirector_ = new SynchronizedLazyClosingDirector(this::lazyClose);

    public AbstractSynchronizedLazyCloseable() {
        super(new SynchronizedStateHolderImpl<>(OPENED));
    }

    @Override
    public int incUsageCount() {
        return lazyClosingDirector_.incUsageCount();
    }

    @Override
    public int decUsageCount() {
        return lazyClosingDirector_.decUsageCount();
    }

    public final CompletionStage<Void> closeAsync() {
        return lazyClosingDirector_.closeAsync();
    }

    @Override
    protected SynchronizedStateHolder<State> getStateHolder() {
        return (SynchronizedStateHolder<State>) super.getStateHolder();
    }

    private void lazyClose() throws Exception {
        final StateHolder<State> stateHolder = getStateHolder();
        if (!stateHolder.isExpected(CLOSING, CLOSED) && canBeClosed()) {
            stateHolder.set(CLOSING);
            try {
                doClose();
            } finally {
                stateHolder.set(CLOSED);
            }
        }
    }

    @Override
    protected final void doClose() throws Exception {
        super.doClose();
    }
}
