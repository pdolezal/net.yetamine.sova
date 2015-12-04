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

import net.yetamine.sova.core.Adaptation;
import net.yetamine.sova.core.AdaptationStrategy;
import net.yetamine.sova.core.Symbol;

/**
 * An abstract symbol base that uses another {@link AdaptationStrategy}.
 *
 * <p>
 * This class delegates to the operations given by an {@link AdaptationStrategy}
 * in order to provide the same behavior, but rather than delegating directly to
 * the other instance, it embodies those operations to reduce the overhead.
 *
 * @param <T>
 *            the type of resulting values
 */
public abstract class DelegatingSymbol<T> extends Symbol<T> {

    /** Actual adaptation implementation. */
    private final Adaptation<T> adaptation;
    /** Actual supplier of the default values. */
    private final Supplier<? extends T> fallbackSupplier;
    /** Run-time type information for the values. */
    private final Class<T> rtti;

    /**
     * Prepares a new instance.
     *
     * @param strategy
     *            the adaptation strategy to use. It must not be {@code null}.
     */
    protected DelegatingSymbol(AdaptationStrategy<T> strategy) {
        fallbackSupplier = strategy.fallbackSupplier();
        adaptation = strategy.adaptation();
        rtti = strategy.rtti();
        // Imposed by the contract!
        assert (adaptation != null);
        assert (fallbackSupplier != null);
    }

    /**
     * @see net.yetamine.sova.core.AdaptationStrategy#adaptation()
     */
    public final Adaptation<T> adaptation() {
        return adaptation;
    }

    /**
     * @see net.yetamine.sova.core.AdaptationStrategy#rtti()
     */
    public final Class<T> rtti() {
        return rtti;
    }

    public final Supplier<? extends T> fallbackSupplier() {
        return fallbackSupplier;
    }
}
