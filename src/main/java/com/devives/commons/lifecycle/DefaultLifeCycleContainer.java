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

import com.devives.commons.lang.ExceptionUtils;
import com.devives.commons.lang.ref.Ref;
import com.devives.commons.lang.call.Try;

import java.util.*;

/**
 * Не синхронизированная реализация контейнера элементов с жизненным циклом.
 *
 * @param <T> тип элементов контейнера.
 */
public final class DefaultLifeCycleContainer<T extends LifeCycle> extends AbstractLifeCycle implements LifeCycleContainer<T> {
    private static final long serialVersionUID = 1L;
    /**
     * Содержит список управляемых элементов.
     */
    private final LinkedHashSet<T> itemSet = new LinkedHashSet<T>();
    /**
     * Содержит множество запущенных элементов.
     */
    private final LinkedHashSet<T> startedItemSet = new LinkedHashSet<T>();

    /**
     * Создаёт пустой контейнер в состоянии {@link LifeCycleStates#STOPPED}.
     */
    public DefaultLifeCycleContainer() {
    }

    public void addItem(T lifeCycle) {
        Objects.requireNonNull(lifeCycle, "Item cannot be null");
        if (!itemSet.add(lifeCycle)) {
            throw new IllegalArgumentException("A duplicate element in a container.");
        }
        if (isStarted()) {
            Try.runnable(() -> {
                internalStartItem(lifeCycle);
            }).onCatch((th) -> {
                itemSet.remove(lifeCycle);
                throw th;
            }).run();
        }
    }

    public void removeItem(T lifeCycle) {
        Objects.requireNonNull(lifeCycle, "Item cannot be null");
        try {
            internalStopItem(lifeCycle);
        } finally {
            itemSet.remove(lifeCycle);
        }
    }

    @Override
    public Collection<T> getItems() {
        return Collections.unmodifiableCollection(itemSet);
    }

    private void internalStartItem(T lifeCycle) {
        if (startedItemSet.add(lifeCycle)) {
            try {
                lifeCycle.start();
            } catch (Throwable th) {
                startedItemSet.remove(lifeCycle);
                throw ExceptionUtils.asUnchecked(th);
            }
        }
    }

    private void internalStopItem(T lifeCycle) {
        if (startedItemSet.remove(lifeCycle)) {
            try {
                lifeCycle.stop();
            } catch (Exception e) {
                throw ExceptionUtils.asUnchecked(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void internalStopItems() {
        final List<Throwable> collector = new ArrayList<>();
        final LifeCycle[] array = itemSet.toArray(new LifeCycle[0]);
        for (int i = array.length - 1; i >= 0; i--) {
            try {
                internalStopItem((T) array[i]);
            } catch (Throwable e) {
                collector.add(e);
            }
        }
        ExceptionUtils.throwCollected(collector);
    }

    private void internalStartItems() {
        // При исключении на старте любого элемента, исключение будет передано в {@link #onFailure).
        itemSet.forEach(this::internalStartItem);
    }

    @Override
    protected void onStart() throws Exception {
        internalStartItems();
    }

    @Override
    protected void onStop() throws Exception {
        internalStopItems();
    }

    @Override
    protected FailureAction onFailure(Ref<Throwable> thRef) {
        try {
            internalStopItems();
        } catch (Throwable th1) {
            thRef.get().addSuppressed(th1);
        }
        return super.onFailure(thRef);
    }

    @Override
    public void addListener(Listener listener) {
        super.addListener(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        super.removeListener(listener);
    }

}
