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


import com.devives.commons.lang.LazyClosingDirectorBase.LazyCloseHandler;
import com.devives.commons.state.State;
import com.devives.commons.state.StateHolder;
import com.devives.commons.state.SynchronizedStateHolder;
import com.devives.commons.state.SynchronizedStateHolderImpl;
import com.devives.commons.util.usage.UsageCounter;

import java.util.concurrent.CompletionStage;

/**
 * Базовая реализация объекта с отложенным закрытием: {@link #closeAsync()} закрывает объект
 * после того, как будут освобождены все его использования ({@link UsageCounter}).
 * <p>
 * Закрытие выполняется в потоке, который его инициировал, см. {@link LazyClosingDirector}.
 */
public abstract class AbstractLazyCloseable extends CloseableBase implements UsageCounter {
    private static final long serialVersionUID = 1L;
    /**
     * Распорядитель отложенного закрытия: закрывает объект, когда вызван {@link #closeAsync()}
     * и счётчик использований равен нулю.
     */
    protected final LazyClosingDirector lazyClosingDirector_ = new LazyClosingDirector(
            new LazyCloseHandler() {
                @Override
                public void lazyClose() throws Exception {
                    AbstractLazyCloseable.this.lazyClose();
                }

                @Override
                public boolean isReentrantUsageAllowed() {
                    return AbstractLazyCloseable.this.isReentrantUsageAllowed();
                }
            });

    /**
     * Создаёт объект в состоянии {@link #OPENED}.
     */
    public AbstractLazyCloseable() {
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

    /**
     * Помечает объект для отложенного закрытия. Объект будет закрыт, когда счётчик использований станет равен нулю.
     *
     * @return стадия, завершающаяся по окончании закрытия.
     */
    public final CompletionStage<Void> closeAsync() {
        return lazyClosingDirector_.closeAsync();
    }

    @Override
    protected SynchronizedStateHolder<State> getStateHolder() {
        return (SynchronizedStateHolder<State>) super.getStateHolder();
    }

    /**
     * Разрешает потоку, выполняющему закрытие объекта, увеличивать счётчик использований объекта, например,
     * если код {@link #onClose()} порождает цепочку вызовов, захватывающую закрываемый объект.
     * <p>
     * Значение читается в момент захвата объекта во время закрытия. Все захваты, сделанные во время закрытия,
     * должны быть освобождены до выхода из {@link #onClose()}.
     *
     * @return {@code false} по умолчанию.
     * @see LazyCloseHandler#isReentrantUsageAllowed()
     */
    protected boolean isReentrantUsageAllowed() {
        return false;
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
