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
 * An adaptation strategy that can remap self to a different type and/or object.
 * This capability allows to use instances of this interface as adaptive keys in
 * various map-like structures and with various mapping strategies.
 * 
 * <p>
 * This interface extends the offer of adaptation methods with new methods which
 * interact with other common types like {@link Function} and {@link BiConsumer}
 * or {@link Map}. Inherited types are expected to add new overloads of these
 * methods to support interaction with additional types, that are substantial
 * with the respect to the intended use. However, all those method should follow
 * the same pattern, so that all the methods behave consistently. Following list
 * depicts the points to follow:
 * 
 * <ul>
 * <li>All methods that read some data should have a single parameter for the
 * data source. The data source is supposed to accept {@link #remap()} as the
 * input for providing the data to adapt.</li>
 * <li>All methods that store some data should have two parameters: the first as
 * the data target, the second as the value to be stored. The data target should
 * accept {@link #remap()} as the key.</li>
 * <li>A {@code get} method should use {@link #apply(Object)} to adapt the
 * result of the data source.</li>
 * <li>A {@code use} method should use {@link #recover(Object)} to adapt the
 * result of the data source.</li>
 * <li>A {@code find} method should use {@link #resolve(Object)} to adapt the
 * result of the data source.</li>
 * <li>A {@code yield} method should use {@link #adapt(Object)} to adapt the
 * result of the data source.</li>
 * <li>A {@code put} method should just store the value.</li>
 * <li>A {@code set} method should use {@link #apply(Object)} to adapt the value
 * and store the result, using the {@code put} variant.</li>
 * </ul>
 * 
 * @param <K>
 *            the type of the remapping result
 * @param <V>
 *            the type of resulting values
 */
public interface Mappable<K, V> extends AdaptationStrategy<V> {

    /**
     * Returns an instance that adapts anything to {@code null}, provides only
     * {@code null} as the fallback and returns {@code null} mapping.
     *
     * @param <K>
     *            the type of the mappable key
     * @param <V>
     *            the type of resulting values
     *
     * @return an instance that reduces anything to {@code null}
     */
    @SuppressWarnings("unchecked")
    static <K, V> Mappable<K, V> nullified() {
        return (Mappable<K, V>) DefaultMappable.NULL;
    }

    /**
     * Remap this instance to a specific key.
     *
     * <p>
     * This method must return always equal values (and it is preferred to use
     * immutable ones, or at least effectively immutable); the values therefore
     * must have properly implemented and well-defined equality to be usable as
     * keys.
     *
     * @return the key for this instance, which should never be {@code null}
     */
    K remap();

    // Generic access methods

    /**
     * Returns an adapted value from the source.
     *
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the result of the adaptation, or {@code null} if not possible
     */
    default V get(Function<? super K, ?> source) {
        return apply(source.apply(remap()));
    }

    /**
     * Returns an adapted value from the source, or the default.
     *
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the result of the adaptation, or the default
     */
    default V use(Function<? super K, ?> source) {
        return recover(source.apply(remap()));
    }

    /**
     * Returns an adapted value from the source as an {@link Optional}.
     *
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return an adapted value from the source as an {@link Optional}
     */
    default Optional<V> find(Function<? super K, ?> source) {
        return resolve(source.apply(remap()));
    }

    /**
     * Returns a representation of an adapted value from the source.
     * 
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the result of the adaptation
     */
    default AdaptationResult<V> yield(Function<? super K, ?> source) {
        return adapt(source.apply(remap()));
    }

    /**
     * Transfers the given value to the given consumer.
     *
     * @param consumer
     *            the consumer to accept the {@link #remap()} result and the
     *            given value. It must not be {@code null}.
     * @param value
     *            the value to transfer
     */
    default void put(BiConsumer<? super K, ? super V> consumer, V value) {
        consumer.accept(remap(), value);
    }

    /**
     * Transfers the adapted value to the given consumer.
     *
     * @param consumer
     *            the consumer to accept the {@link #remap()} result and the
     *            adapted value. It must not be {@code null}.
     * @param value
     *            the value to adapt and transfer
     */
    default void set(BiConsumer<? super K, ? super V> consumer, Object value) {
        put(consumer, adaptation().apply(value));
    }

    // Map-based access methods

    /**
     * Returns an adapted value from the source.
     *
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the result of the adaptation, or {@code null} if not possible
     */
    default V get(Map<?, ?> source) {
        return apply(source.get(remap()));
    }

    /**
     * Returns an adapted value from the source, or the default.
     *
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the result of the adaptation, or the default
     */
    default V use(Map<?, ?> source) {
        return recover(source.get(remap()));
    }

    /**
     * Returns an adapted value from the source as an {@link Optional}.
     *
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return an adapted value from the source as an {@link Optional}
     */
    default Optional<V> find(Map<?, ?> source) {
        return resolve(source.get(remap()));
    }

    /**
     * Returns a representation of an adapted value from the source.
     * 
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     *
     * @return the result of the adaptation
     */
    default AdaptationResult<V> yield(Map<?, ?> source) {
        return adapt(source.get(remap()));
    }

    /**
     * Puts the given value to the given map.
     *
     * @param consumer
     *            the map to accept the {@link #remap()} result and the given
     *            value. It must not be {@code null}.
     * @param value
     *            the value to put
     * 
     * @return the result of {@link Map#put(Object, Object)}
     */
    default Object put(Map<? super K, ? super V> consumer, V value) {
        return consumer.put(remap(), value);
    }

    /**
     * Puts the adapted value to the given map.
     *
     * @param consumer
     *            the map to accept the {@link #remap()} result and the adapted
     *            value. It must not be {@code null}.
     * @param value
     *            the value to adapt and transfer
     * 
     * @return the result of the {@link Map#put(Object, Object)}
     */
    default Object set(Map<? super K, ? super V> consumer, Object value) {
        return put(consumer, apply(value));
    }
}
