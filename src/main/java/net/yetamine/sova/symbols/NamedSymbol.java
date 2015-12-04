/*
 * Copyright 2016 Yetamine
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.yetamine.sova.symbols;

import net.yetamine.sova.core.AdaptationStrategy;
import net.yetamine.sova.core.Downcasting;

/**
 * A convenient extension of {@link TaggedSymbol} that declares a {@link String}
 * tag. The tag is intended to provide a human-friendly name of the particular
 * symbol instance for debugging purposes.
 *
 * @param <T>
 *            the type of resulting values
 */
public final class NamedSymbol<T> extends TaggedSymbol<String, T> {

    /**
     * Create a new instance.
     *
     * @param instanceTag
     *            the tag of this instance. It must not be {@code null}.
     * @param adaptation
     *            the adaptation implementation. It must not be {@code null}.
     */
    public NamedSymbol(String instanceTag, AdaptationStrategy<T> adaptation) {
        super(instanceTag, adaptation);
    }

    /**
     * Creates a new instance using {@link Downcasting#to(Class)}.
     *
     * @param instanceTag
     *            the tag of the new instance. It must not be {@code null}.
     * @param type
     *            the desired type of resulting values. It must not be
     *            {@code null}.
     */
    public NamedSymbol(String instanceTag, Class<T> type) {
        this(instanceTag, Downcasting.to(type));
    }
}
