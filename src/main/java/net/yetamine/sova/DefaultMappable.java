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

package net.yetamine.sova;

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

    /**
     * Implementation of {@link Mappable#nullified()} result.
     *
     * <p>
     * The instance is stateless and accepts any instance as well as producing
     * only {@code null} results which are compatible with any type, therefore
     * the instance can be used for any generic types.
     */
    static final Mappable<Object, Object> NULL // @formatter:break
    = new DefaultMappable<>(Downcasting.withFilter(Object.class, o -> false), () -> null);

    /** Implementation of the adaptation part. */
    private final AdaptationProvider<V> provider;
    /** Supplier of the remapping values. */
    private final Supplier<? extends K> remapping;

    /**
     * Creates a new instance.
     *
     * @param implementation
     *            the adaptation implementation. It must not be {@code null}.
     * @param mapping
     *            the mappable supplier. It must not be {@code null}.
     */
    public DefaultMappable(AdaptationProvider<V> implementation, Supplier<? extends K> mapping) {
        provider = Objects.requireNonNull(implementation);
        remapping = Objects.requireNonNull(mapping);
    }

    /**
     * @see net.yetamine.sova.Mappable#remap()
     */
    public K remap() {
        return remapping.get();
    }

    /**
     * @see net.yetamine.sova.AdaptationStrategy#adaptation()
     */
    public Adaptation<V> adaptation() {
        return provider.adaptation();
    }

    /**
     * @see net.yetamine.sova.AdaptationProvider#fallback()
     */
    public Supplier<? extends V> fallback() {
        return provider.fallback();
    }

    /**
     * @see net.yetamine.sova.AdaptationStrategy#rtti()
     */
    public Class<V> rtti() {
        return provider.rtti();
    }
}
