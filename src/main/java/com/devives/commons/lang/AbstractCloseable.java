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
import com.devives.commons.state.StateHolderImpl;

/**
 * An abstract implementation of a closable resource with lifecycle state management.
 * <p>
 * Subclasses implement {@link #onClose()} to release resources. The framework guarantees
 * that {@code onClose()} is called exactly once when {@link #close()} is invoked.
 * </p>
 * <p>
 * This class is NOT thread-safe. Concurrent calls to {@link #close()} are guarded by state
 * checks (only one transition from {@code OPENED} to {@code CLOSING} is allowed), but
 * subclasses must ensure that their {@code onClose()} implementation does not access shared
 * mutable state from multiple threads.
 * </p>
 *
 * @since 0.3.0
 */
public abstract class AbstractCloseable extends CloseableBase {
    private static final long serialVersionUID = 1L;

    public AbstractCloseable() {
        this(OPENED);
    }

    public AbstractCloseable(State initialState) {
        super(new StateHolderImpl<State>(initialState));
    }

    /**
     * Release object's resources.
     * <p>
     * Closing of object can be cancelled by results of calling {@link #canBeClosed()} method.
     *
     * @throws Exception when resource closing failed.
     */
    public final void close() throws Exception {
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
