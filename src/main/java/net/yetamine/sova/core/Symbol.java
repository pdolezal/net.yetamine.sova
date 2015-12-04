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

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A smart type-safe key for using with heterogenous containers and data
 * handlers.
 *
 * <p>
 * A symbol encapsulates the type constraint of the value that the symbol shall
 * refer to and provides an adaptation strategy for adapting any suitable input
 * object to the desired type.
 *
 * <p>
 * The conditional adaptation capability of a symbol is a powerful feature that
 * allows safe access to any values stored in a heterogenous container as long
 * as the values are associated with the correct symbols. Since symbols should
 * be used as keys in map-like containers, all implementations must be (at
 * least) effectively immutable.
 *
 * <p>
 * This class does not define any specific behavior of {@link #equals(Object)},
 * besides the general rules that imply that two equal instances must provide
 * the same results when all relevant preconditions are satisfied; especially,
 * an implementation must not extend preconditions of inherited contracts (it
 * may weaken them only). Inherited classes are required to define details to
 * make instance equivalence defined properly.
 *
 * @param <T>
 *            the type of resulting values
 */
public abstract class Symbol<T> implements AdaptationStrategy<T> {

    /**
     * Prepares a new instance.
     */
    protected Symbol() {
        // Default constructor
    }

    /**
     * @see net.yetamine.sova.core.AdaptationStrategy#fallback()
     */
    public final T fallback() {
        return AdaptationStrategy.super.fallback();
    }

    // Generic access methods

    /**
     * Adapts the object provided by a {@link Function} using this instance as
     * its input.
     *
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the result of the adaptation, or {@code null} if not possible
     */
    public final T get(Function<? super Symbol<?>, ?> source) {
        return adaptation().apply(source.apply(this));
    }

    /**
     * Adapts the value provided by a {@link Function} using this instance as
     * its input; if the returned value is {@code null}, {@link #fallback()}
     * result is returned instead.
     *
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the result of the adaptation, or the fallback if the adaptation
     *         input is missing
     */
    public final T gain(Function<? super Symbol<?>, ?> source) {
        final Object object = source.apply(this);
        return (object != null) ? adaptation().apply(object) : fallback();
    }

    /**
     * Adapts the object provided by a {@link Function} using this instance as
     * its input, or returns the {@link #fallback()} if the adaptation returns
     * {@code null} for whatever reason.
     *
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the result of the adaptation or the fallback (which still may
     *         return {@code null})
     */
    public final T seek(Function<? super Symbol<?>, ?> source) {
        return find(source).orElseGet(fallbackSupplier());
    }

    /**
     * Adapts the object provided by a {@link Function} using this instance as
     * its input.
     *
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the {@link Optional} with the result of the adaptation
     */
    public final Optional<T> find(Function<? super Symbol<?>, ?> source) {
        return adaptation().attempt(source.apply(this));
    }

    /**
     * Passes this instance and the given value to the given consumer.
     *
     * @param consumer
     *            the consumer to accept this instance and the given value. It
     *            must not be {@code null}.
     * @param value
     *            the value to pass
     */
    public final void put(BiConsumer<? super Symbol<T>, ? super T> consumer, T value) {
        consumer.accept(this, value);
    }

    /**
     * Passes this instance and the adaptation of the given value to the given
     * consumer.
     *
     * @param consumer
     *            the consumer to accept this instance and the given value. It
     *            must not be {@code null}.
     * @param value
     *            the value to adapt and pass
     */
    public final void set(BiConsumer<? super Symbol<T>, ? super T> consumer, Object value) {
        put(consumer, adaptation().apply(value));
    }

    // Map-based access methods

    /**
     * Adapts the object taken from a {@link Map} using this instance as the
     * key.
     *
     * @param source
     *            the map providing the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the result of the adaptation, or {@code null} if not possible
     */
    public final T get(Map<?, ?> source) {
        return adaptation().apply(source.get(this));
    }

    /**
     * Adapts the value taken from a {@link Map} using this instance as the key;
     * if the returned value is {@code null}, {@link #fallback()} result is
     * returned instead.
     *
     * @param source
     *            the map providing the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the result of the adaptation, or the fallback if the adaptation
     *         input is missing
     */
    public final T gain(Map<?, ?> source) {
        final Object object = source.get(this);
        return (object != null) ? adaptation().apply(object) : fallback();
    }

    /**
     * Adapts the object taken from a {@link Map} using this instance as the
     * key, or returns the {@link #fallback()} if the adaptation returns
     * {@code null} for whatever reason.
     *
     * @param source
     *            the map providing the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the result of the adaptation or the fallback (which still may
     *         return {@code null})
     */
    public final T seek(Map<?, ?> source) {
        return find(source).orElseGet(fallbackSupplier());
    }

    /**
     * Adapts the object taken from a {@link Map} using this instance as the
     * key.
     *
     * @param source
     *            the map providing the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the {@link Optional} with the result of the adaptation
     */
    public final Optional<T> find(Map<?, ?> source) {
        return adaptation().attempt(source.get(this));
    }

    /**
     * Puts the given value into the given {@link Map}, using this instance as
     * the key for the value.
     *
     * @param consumer
     *            the map accepting the value. It must not be {@code null}.
     * @param value
     *            the value to put
     *
     * @return the result of the {@link Map#put(Object, Object)} invocation
     */
    public final Object put(Map<? super Symbol<T>, ? super T> consumer, T value) {
        return consumer.put(this, value);
    }

    /**
     * Puts the adaptation of the given value to the given {@link Map}, using
     * this instance as the key for the value.
     *
     * @param consumer
     *            the consumer to accept this instance and the given value. It
     *            must not be {@code null}.
     * @param value
     *            the value to adapt and pass
     *
     * @return the result of the {@link Map#put(Object, Object)} invocation
     */
    public final Object set(Map<? super Symbol<T>, ? super T> consumer, Object value) {
        return put(consumer, adaptation().apply(value));
    }
}
