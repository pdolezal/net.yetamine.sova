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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * An adaptation strategy that allows using self as a generic mapping strategy.
 *
 * @param <K>
 *            the type of the mappable key
 * @param <V>
 *            the type of resulting values
 */
public interface Mappable<K, V> extends AdaptationStrategy<V> {

    /**
     * Returns the mapping key for this instance.
     *
     * <p>
     * This method must return always equal values (and it is preferred to use
     * immutable ones, or at least effectively immutable); the values therefore
     * must have properly implemented and well-defined equality to be usable as
     * keys.
     *
     * @return the mapping key for this instance, which should never be
     *         {@code null}
     */
    K mapping();

    // Generic access methods

    /**
     * Adapts the object provided by a {@link Function} with {@link #mapping()}
     * as its input.
     *
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the result of the adaptation, or {@code null} if not possible
     */
    default V get(Function<? super K, ?> source) {
        return adaptation().apply(source.apply(mapping()));
    }

    /**
     * Adapts the value provided by a {@link Function} with {@link #mapping()}
     * as its input; if the returned value is {@code null}, {@link #fallback()}
     * result is returned instead.
     *
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the result of the adaptation, or the fallback if the adaptation
     *         input is missing
     */
    default V gain(Function<? super K, ?> source) {
        final Object object = source.apply(mapping());
        return (object != null) ? adaptation().apply(object) : fallback();
    }

    /**
     * Adapts the object provided by a {@link Function} with {@link #mapping()}
     * as its input, or returns the {@link #fallback()} if the adaptation
     * returns {@code null} for whatever reason.
     *
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the result of the adaptation or the fallback (which still may
     *         return {@code null})
     */
    default V seek(Function<? super K, ?> source) {
        return find(source).orElseGet(fallbackSupplier());
    }

    /**
     * Returns the adaptation of the object provided by a {@link Function} with
     * {@link #mapping()} as its input or the {@link #fallback()} result if the
     * adaptation returns {@code null} for whatever reason.
     *
     * @param <X>
     *            the type of the exception to throw if the method fails to
     *            return a non-{@code null} result
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     * @param exception
     *            the function that gets the key of the failing entry and shall
     *            return the exception to throw. It must not be {@code null}.
     *
     * @return the result of the adaptation or the fallback
     *
     * @throws X
     *             if both the adaptation and fallback returns {@code null}
     */
    default <X extends Throwable> V require(Function<? super K, ?> source, Function<? super K, ? extends X> exception) throws X {
        final K key = mapping();
        final Object object = source.apply(key);
        final V result = (object != null) ? adaptation().apply(object) : fallback();

        if (result == null) {
            throw exception.apply(key);
        }

        return result;
    }

    /**
     * Returns the adaptation of the object provided by a {@link Function} with
     * {@link #mapping()} as its input or the {@link #fallback()} result if the
     * adaptation returns {@code null} for whatever reason.
     *
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the result of the adaptation or the fallback
     *
     * @throws NoSuchElementException
     *             if both the adaptation and fallback returns {@code null}
     */
    default V require(Function<? super K, ?> source) {
        return require(source, o -> new NoSuchElementException(String.format("Missing item: %s", o)));
    }

    /**
     * Adapts the object provided by a {@link Function} with {@link #mapping()}
     * as its input.
     *
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the {@link Optional} with the result of the adaptation
     */
    default Optional<V> find(Function<? super K, ?> source) {
        return adaptation().attempt(source.apply(mapping()));
    }

    /**
     * Passes {@link #mapping()} and the given value to the given consumer.
     *
     * @param consumer
     *            the consumer to accept the mappable value and the given value.
     *            It must not be {@code null}.
     * @param value
     *            the value to pass
     */
    default void put(BiConsumer<? super K, ? super V> consumer, V value) {
        consumer.accept(mapping(), value);
    }

    /**
     * Passes {@link #mapping()} and the adaptation of the given value to the
     * given consumer.
     *
     * @param consumer
     *            the consumer to accept the mappable value and the given value.
     *            It must not be {@code null}.
     * @param value
     *            the value to adapt and pass
     */
    default void set(BiConsumer<? super K, ? super V> consumer, Object value) {
        put(consumer, adaptation().apply(value));
    }

    // Map-based access methods

    /**
     * Adapts the object taken from a {@link Map} with {@link #mapping()} as the
     * key.
     *
     * @param source
     *            the map providing the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the result of the adaptation, or {@code null} if not possible
     */
    default V get(Map<?, ?> source) {
        return adaptation().apply(source.get(mapping()));
    }

    /**
     * Adapts the value taken from a {@link Map} with {@link #mapping()} as the
     * key; if the returned value is {@code null}, {@link #fallback()} result is
     * returned instead.
     *
     * @param source
     *            the map providing the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the result of the adaptation, or the fallback if the adaptation
     *         input is missing
     */
    default V gain(Map<?, ?> source) {
        final Object object = source.get(mapping());
        return (object != null) ? adaptation().apply(object) : fallback();
    }

    /**
     * Adapts the object taken from a {@link Map} with {@link #mapping()} as the
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
    default V seek(Map<?, ?> source) {
        return find(source).orElseGet(fallbackSupplier());
    }

    /**
     * Returns the adaptation of the object taken from a {@link Map} with
     * {@link #mapping()} as the key or the {@link #fallback()} result if the
     * adaptation returns {@code null} for whatever reason.
     *
     * @param <X>
     *            the type of the exception to throw if the method fails to
     *            return a non-{@code null} result
     * @param source
     *            the map providing the argument to adapt. It must not be
     *            {@code null}.
     * @param exception
     *            the function that gets the key of the failing entry and shall
     *            return the exception to throw. It must not be {@code null}.
     *
     * @return the result of the adaptation or the fallback
     *
     * @throws X
     *             if both the adaptation and fallback returns {@code null}
     */
    default <X extends Throwable> V require(Map<?, ?> source, Function<? super K, ? extends X> exception) throws X {
        final K key = mapping();
        final Object object = source.get(key);
        final V result = (object != null) ? adaptation().apply(object) : fallback();

        if (result == null) {
            throw exception.apply(key);
        }

        return result;
    }

    /**
     * Returns the adaptation of the object taken from a {@link Map} with
     * {@link #mapping()} as the key the {@link #fallback()} result if the
     * adaptation returns {@code null} for whatever reason.
     *
     * @param source
     *            the map providing the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the result of the adaptation or the fallback
     *
     * @throws NoSuchElementException
     *             if both the adaptation and fallback returns {@code null}
     */
    default V require(Map<?, ?> source) {
        return require(source, o -> new NoSuchElementException(String.format("Missing item: %s", o)));
    }

    /**
     * Adapts the object taken from a {@link Map} with {@link #mapping()} as the
     * key.
     *
     * @param source
     *            the map providing the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the {@link Optional} with the result of the adaptation
     */
    default Optional<V> find(Map<?, ?> source) {
        return adaptation().attempt(source.get(mapping()));
    }

    /**
     * Puts the given value into the given {@link Map}, with {@link #mapping()}
     * as the key for the value.
     *
     * @param consumer
     *            the map accepting the value. It must not be {@code null}.
     * @param value
     *            the value to put
     *
     * @return the result of the {@link Map#put(Object, Object)} invocation
     */
    default Object put(Map<? super K, ? super V> consumer, V value) {
        return consumer.put(mapping(), value);
    }

    /**
     * Puts the adaptation of the given value to the given {@link Map}, with
     * {@link #mapping()} as the key for the value.
     *
     * @param consumer
     *            the consumer to accept the mappable value and the given value.
     *            It must not be {@code null}.
     * @param value
     *            the value to adapt and pass
     *
     * @return the result of the {@link Map#put(Object, Object)} invocation
     */
    default Object set(Map<? super K, ? super V> consumer, Object value) {
        return put(consumer, adaptation().apply(value));
    }
}
