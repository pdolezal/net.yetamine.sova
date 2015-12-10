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

package net.yetamine.sova.core;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A default implementation of the {@link Mappable} interface.
 *
 * @param <K>
 *            the type of the mappable key
 * @param <V>
 *            the type of resulting values
 */
public final class DefaultMappable<K, V> implements Mappable<K, V> {

    /** Implementation of the adaptation part. */
    private final AdaptationStrategy<V> adaptation;
    /** Supplier of the mappable values. */
    private final Supplier<? extends K> mappable;

    /**
     * Creates a new instance.
     *
     * @param implementation
     *            the adaptation implementation. It must not be {@code null}.
     * @param mapping
     *            the mappable supplier. It must not be {@code null}.
     */
    public DefaultMappable(AdaptationStrategy<V> implementation, Supplier<? extends K> mapping) {
        adaptation = Objects.requireNonNull(implementation);
        mappable = Objects.requireNonNull(mapping);
    }

    /**
     * @see net.yetamine.sova.core.Mappable#mapping()
     */
    public K mapping() {
        return mappable.get();
    }

    /**
     * @see net.yetamine.sova.core.AdaptationStrategy#adaptation()
     */
    public Adaptation<V> adaptation() {
        return adaptation.adaptation();
    }

    /**
     * @see net.yetamine.sova.core.AdaptationStrategy#rtti()
     */
    public Class<V> rtti() {
        return adaptation.rtti();
    }

    /**
     * @see net.yetamine.sova.core.AdaptationStrategy#fallbackSupplier()
     */
    public Supplier<? extends V> fallbackSupplier() {
        return adaptation.fallbackSupplier();
    }
}
