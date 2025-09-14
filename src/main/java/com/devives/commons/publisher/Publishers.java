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
package com.devives.commons.publisher;

/**
 * Factory class for creating {@link PublisherBuilder}, which configures and builds {@link Publisher}.
 * <p>
 * Used as an entry point for creating event publisher with flexible settings.
 * <p>
 * Example usage:
 * <pre>{@code
 * Publishers<SomeListener> publisher = Publishers.<SomeListener>builder()
 *    .listeners(builder -> builder.setSynchronized())
 *    .build();
 * }</pre>
 *
 * @since 0.3.0
 */
public final class Publishers {

    /**
     * Private constructor to prevent instantiation.
     */
    private Publishers() {
    }

    /**
     * Returns a new EventSourceBuilder.
     *
     * @param <LISTENER> the type of listeners.
     * @return a new PublisherBuilder.
     */
    public static <LISTENER> PublisherBuilder<LISTENER> builder() {
        return new PublisherBuilder<>();
    }

}
