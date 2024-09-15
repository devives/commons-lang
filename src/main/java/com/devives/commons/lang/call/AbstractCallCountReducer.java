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
package com.devives.commons.lang.call;

import com.devives.commons.lang.function.ExceptionProcedure1;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

/**
 * Уменьшитель числа вызовов целевого метода.
 * <p>
 * Класс сокращает число вызовов целевого метода {@link #proc_} за интервал времени {@link #interval_},
 * тем самым предотвращает многократный вызов (DDos) целевого метода {@link #proc_}
 * за заданный промежуток времени {@link #interval_}.
 * <p>
 * Методы класса потокобезопасны. Целевой метод всегда вызывается в потоке переданного исполнителя {@link #executor_}.
 * <pre>{@code
 *  private Executor executor = Executors.newSingleThreadExecutor();
 *  // Целевой метод
 *  private void targetProcedure(BaseEvent events){
 *      // do some stuff
 *  }
 *
 *  private CallCountReducer1<BaseEvent> callCountReducedProc = CallCountReducer.of(
 *    this::targetProcedure,
 *    500,
 *    executor,
 *    new ErrorSinkImpl<>(ErrorSink.SinkHandler.fromEventSourceLogger()
 *  );
 *
 *  private EventListener<BaseEvent> someEventListener = new EventListenerAbst<BaseEvent>() {
 *         public void handleEvent(BaseEvent event) {
 *            callCountReducedProc.invoke(event);
 *         }
 *  }
 * }</pre>
 * В приведённом примере, за заданный интервал времени (500 мс), вызов {@link CallCountReducer1#invoke(Object)},
 * может быть произведён множество раз. Целевой метод, будет вызван только 2 раза: для первых и последних значений переданных аргументов.
 * Не гарантируется, что первый вызов целевого метода произойдёт именно для первых переданных аргументов, т.к. до
 * начала работы целевого метода может быть произведено несколько вызовов {@link CallCountReducer1#invoke(Object)}.
 */
abstract class AbstractCallCountReducer {

    private final Object lock_ = new Object();
    private final Executor executor_;
    private final ExceptionProcedure1<Object[]> proc_;
    private final long interval_;
    private final BiConsumer<Throwable, Object> errorSink_;
    /**
     * Обращение к полям в synchronized методах, по-этому не volatile.
     */
    private long lastInvokeMills_ = 0;
    private Timer invokeTimer_;
    private long counter_ = 0;
    private Object[] args_;

    /**
     * @param executor Исполнитель целевого метода.
     * @param proc     Целевой метод.
     * @param interval Интервал между последовательными вызовами запланированного метода, в миллисекундах.
     */
    public AbstractCallCountReducer(ExceptionProcedure1<Object[]> proc, long interval, Executor executor, BiConsumer<Throwable, Object> errorSink) {
        proc_ = Objects.requireNonNull(proc);
        interval_ = interval;
        executor_ = Objects.requireNonNull(executor);
        errorSink_ = Objects.requireNonNull(errorSink);
    }

    /**
     * Планирует вызов целевого метода.
     * @param args аргументы
     */
    protected void invoke(Object... args) {
        synchronized (lock_) {
            args_ = args;
            counter_++;
            //Если таймер существует, значит вызов запланирован или выполняется.
            if (invokeTimer_ == null) {
                invokeTimer_ = scheduleTask();
            }
        }
    }

    /**
     * Метод вызывается из блоков, синхронизированных через {@link AbstractCallCountReducer#lock_}/
     */
    private Timer scheduleTask() {
        // Считаем интервал между временем последнего вызова таска и текущим моментом.
        final long passedMills = System.currentTimeMillis() - lastInvokeMills_;
        // Если предыдущий вызов выполнялся более interval_ миллисекунд назад, интервал отложенного вызова == 0.
        final long delay = Math.max(0, interval_ - passedMills);
        final Timer invokeTimer = new Timer();
        invokeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    final Object[] args;
                    final long counter;
                    synchronized (lock_) {
                        args = args_;
                        counter = counter_;
                        lastInvokeMills_ = System.currentTimeMillis();
                    }
                    executor_.execute(() -> {
                        try {
                            try {
                                proc_.accept(args);
                            } finally {
                                synchronized (lock_) {
                                    // Если за время выполнения декорируемого метода произошёл ещё один вызов invoke(args),
                                    // планируем следующее выполнение.
                                    if (counter != counter_) {
                                        invokeTimer_ = scheduleTask();
                                    } else {
                                        invokeTimer_ = null;
                                    }
                                }
                            }
                        } catch (Throwable throwable) {
                            errorSink_.accept(throwable, AbstractCallCountReducer.this);
                        }
                    });
                } catch (Throwable throwable) {
                    errorSink_.accept(throwable, AbstractCallCountReducer.this);
                }
            }
        }, delay);
        return invokeTimer;
    }

    /**
     * Выполняет отмену запланированного ранее выполнения целевого метода.
     * Если целевой метод уже выполняется, он не будет отменён или прерван.
     */
    public void cancel() {
        synchronized (lock_) {
            if (invokeTimer_ != null) {
                try {
                    invokeTimer_.cancel();
                } finally {
                    invokeTimer_ = null;
                }
            }
        }
    }

}
