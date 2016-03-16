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

import java.util.function.Supplier;

import net.yetamine.sova.Adaptation;
import net.yetamine.sova.AdaptationProvider;

/**
 * An abstract base class that uses another {@link AdaptationProvider}.
 *
 * <p>
 * This class delegates to the operations given by an {@link AdaptationProvider}
 * in order to provide the same behavior, but rather than delegating directly to
 * the other instance, it embodies those operations to reduce the overhead.
 *
 * @param <T>
 *            the type of resulting values
 */
public abstract class DelegatingSymbol<T> extends AbstractSymbol<T> {

    /** Actual adaptation implementation. */
    private final Adaptation<T> adaptation;
    /** Actual supplier of default values. */
    private final Supplier<? extends T> fallback;
    /** Run-time type information for the values. */
    private final Class<T> rtti;

    /**
     * Prepares a new instance.
     *
     * @param provider
     *            the adaptation provider to use. It must not be {@code null}.
     */
    protected DelegatingSymbol(AdaptationProvider<T> provider) {
        adaptation = provider.adaptation();
        fallback = provider.fallback();
        rtti = provider.rtti();
        assert (fallback != null);
        assert (adaptation != null);
    }

    /**
     * @see net.yetamine.sova.AdaptationProvider#adaptation()
     */
    public final Adaptation<T> adaptation() {
        return adaptation;
    }

    /**
     * @see net.yetamine.sova.AdaptationProvider#fallback()
     */
    public final Supplier<? extends T> fallback() {
        return fallback;
    }

    /**
     * @see net.yetamine.sova.AdaptationProvider#rtti()
     */
    public final Class<T> rtti() {
        return rtti;
    }
}
