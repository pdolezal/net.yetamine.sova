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
 * @param <T>
 *            the type of resulting values
 */
public interface Symbol<T> extends AdaptationStrategy<T> {

    /**
     * Compares the specified symbols for equality.
     *
     * <p>
     * Implementations are required to define this method in order to provide
     * correct and consistent behavior. In general, two symbols should be equal
     * only if they have the same semantics and provide the same results.
     *
     * <p>
     * The conditions may be bound to a well-known published identifier linked
     * to a symbol instance for a particular symbol type, but using the default
     * implementation that compares just the identity of instances could be yet
     * another well-working option.
     *
     * @param o
     *            object to be compared for equality with this instance
     *
     * @return {@code true} if the object is equal to this instance
     *
     * @see Object#equals(Object)
     */
    boolean equals(Object o);

    /**
     * Returns the hash code value for this symbol.
     *
     * <p>
     * Implementations must override this method in order to make it consistent
     * with {@link #equals(Object)} if they override {@code equals()} as well.
     *
     * @return the hash code value for this symbol
     *
     * @see Object#equals(Object)
     */
    int hashCode();

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
    default T get(Function<? super Symbol<?>, ?> source) {
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
    default T gain(Function<? super Symbol<?>, ?> source) {
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
    default T seek(Function<? super Symbol<?>, ?> source) {
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
    default Optional<T> find(Function<? super Symbol<?>, ?> source) {
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
    default void put(BiConsumer<? super Symbol<T>, ? super T> consumer, T value) {
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
    default void set(BiConsumer<? super Symbol<T>, ? super T> consumer, Object value) {
        put(consumer, adaptation().apply(value));
    }

    // Generic mapped access methods

    /**
     * Adapts the object provided by a {@link Function} using this instance as
     * the input for the given mapping function.
     *
     * @param <U>
     *            the type of the mapping result
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     * @param mapping
     *            the mapping function making the input for the source. It must
     *            not be {@code null}.
     *
     * @return the result of the adaptation, or {@code null} if not possible
     */
    default <U> T get(Function<? super U, ?> source, Function<? super Symbol<?>, U> mapping) {
        return adaptation().apply(source.apply(mapping.apply(this)));
    }

    /**
     * Adapts the value provided by a {@link Function} using this instance as
     * its input; if the returned value is {@code null}, {@link #fallback()}
     * result is returned instead.
     *
     * @param <U>
     *            the type of the mapping result
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     * @param mapping
     *            the mapping function making the input for the source. It must
     *            not be {@code null}.
     *
     * @return the result of the adaptation, or the fallback if the adaptation
     *         input is missing
     */
    default <U> T gain(Function<? super U, ?> source, Function<? super Symbol<?>, U> mapping) {
        final Object object = source.apply(mapping.apply(this));
        return (object != null) ? adaptation().apply(object) : fallback();
    }

    /**
     * Adapts the object provided by a {@link Function} using this instance as
     * its input, or returns the {@link #fallback()} if the adaptation returns
     * {@code null} for whatever reason.
     *
     * @param <U>
     *            the type of the mapping result
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     * @param mapping
     *            the mapping function making the input for the source. It must
     *            not be {@code null}.
     *
     * @return the result of the adaptation or the fallback (which still may
     *         return {@code null})
     */
    default <U> T seek(Function<? super U, ?> source, Function<? super Symbol<?>, U> mapping) {
        return find(source, mapping).orElseGet(fallbackSupplier());
    }

    /**
     * Adapts the object provided by a {@link Function} using this instance as
     * its input.
     *
     * @param <U>
     *            the type of the mapping result
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     * @param mapping
     *            the mapping function making the input for the source. It must
     *            not be {@code null}.
     *
     * @return the {@link Optional} with the result of the adaptation
     */
    default <U> Optional<T> find(Function<? super U, ?> source, Function<? super Symbol<?>, U> mapping) {
        return adaptation().attempt(source.apply(mapping.apply(this)));
    }

    /**
     * Passes this instance and the given value to the given consumer.
     *
     * @param <U>
     *            the type of the mapping result
     * @param consumer
     *            the consumer to accept this instance and the given value. It
     *            must not be {@code null}.
     * @param value
     *            the value to pass
     * @param mapping
     *            the mapping function making the input for the source. It must
     *            not be {@code null}.
     */
    default <U> void put(BiConsumer<? super U, ? super T> consumer, T value, Function<? super Symbol<?>, U> mapping) {
        consumer.accept(mapping.apply(this), value);
    }

    /**
     * Passes this instance and the adaptation of the given value to the given
     * consumer.
     *
     * @param <U>
     *            the type of the mapping result
     * @param consumer
     *            the consumer to accept this instance and the given value. It
     *            must not be {@code null}.
     * @param value
     *            the value to adapt and pass
     * @param mapping
     *            the mapping function making the input for the source. It must
     *            not be {@code null}.
     */
    default <U> void set(BiConsumer<? super U, ? super T> consumer, Object value, Function<? super Symbol<?>, U> mapping) {
        put(consumer, adaptation().apply(value), mapping);
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
    default T get(Map<?, ?> source) {
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
    default T gain(Map<?, ?> source) {
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
    default T seek(Map<?, ?> source) {
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
    default Optional<T> find(Map<?, ?> source) {
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
    default Object put(Map<? super Symbol<T>, ? super T> consumer, T value) {
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
    default Object set(Map<? super Symbol<T>, ? super T> consumer, Object value) {
        return put(consumer, adaptation().apply(value));
    }
}
