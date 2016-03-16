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

import java.util.Map;

import net.yetamine.sova.AdaptationProvider;
import net.yetamine.sova.Downcasting;

/**
 * A symbol implementation that intentionally uses instance identity, therefore
 * each instance of this class is equal only to itself.
 *
 * <p>
 * The instance identity enforcement is useful when really a single instance of
 * a symbol should exist. Then only this single instance can be used to access
 * values and keeping the consistency of access operations, avoiding different
 * handling of multiple implementations.
 *
 * <p>
 * There is usually no need to inherit from this class, but this option is left
 * open intentionally when an implementation wishes to provide some additional
 * features or attributes, or just alter the debugging support.
 *
 * @param <T>
 *            the type of resulting values
 */
public class InternalSymbol<T> extends DelegatingSymbol<T> {

    /**
     * Creates a new instance.
     *
     * @param provider
     *            the adaptation provider. It must not be {@code null}.
     */
    public InternalSymbol(AdaptationProvider<T> provider) {
        super(provider);
    }

    /**
     * Creates a new instance using {@link Downcasting#to(Class)}.
     *
     * @param type
     *            the desired type of resulting values. It must not be
     *            {@code null}.
     */
    public InternalSymbol(Class<T> type) {
        this(Downcasting.to(type));
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    /**
     * @see net.yetamine.sova.symbols.AbstractSymbol#introspect(java.util.Map)
     */
    @Override
    protected void introspect(Map<Object, Object> result) {
        super.introspect(result);
        result.put(toString("id"), String.format("hash:%08x", System.identityHashCode(this)));
        result.put(toString("class"), getClass().getTypeName());
    }
}
