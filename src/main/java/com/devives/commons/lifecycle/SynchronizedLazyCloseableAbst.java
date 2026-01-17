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


import java.util.concurrent.CompletionStage;

public abstract class SynchronizedLazyCloseableAbst extends CloseableBaseAbst implements UsageCounter {

    protected final SynchronizedLazyClosingDirector lazyClosingDirector_ = new SynchronizedLazyClosingDirector(this::lazyClose);

    public SynchronizedLazyCloseableAbst() {
        super(new StateHolderImpl(OPENED));
    }

    @Override
    public int incUsageCount() {
        return lazyClosingDirector_.incUsageCount();
    }

    @Override
    public int decUsageCount() {
        return lazyClosingDirector_.decUsageCount();
    }

    public CompletionStage<Void> closeAsync() {
        return lazyClosingDirector_.closeAsync();
    }

    private void lazyClose() throws Exception {
        final StateHolder stateHolder = getStateHolder();
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
