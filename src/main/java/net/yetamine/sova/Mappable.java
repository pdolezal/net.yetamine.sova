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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
 * <li>A {@code get} method should use {@link #derive(Object)} to adapt the
 * result of the data source.</li>
 * <li>A {@code use} method should use {@link #recover(Object)} to adapt the
 * result of the data source.</li>
 * <li>A {@code find} method should use {@link #resolve(Object)} to adapt the
 * result of the data source.</li>
 * <li>A {@code yield} method should use {@link #adapt(Object)} to adapt the
 * result of the data source.</li>
 * <li>A {@code put} method should just store the value.</li>
 * <li>A {@code set} method should use {@link #derive(Object)} to adapt the
 * value and store the result, using the {@code put} variant.</li>
 * <li>A {@code let} method should use a merging functionality of the consumer,
 * if applicable, to store the {@link #fallback()} for missing or non-adaptable
 * values; it acts as its {@code use} companion, just storing the fallback.</li>
 * <li>A {@code have} method should use a compute-if-absent functionality of the
 * consumer, if applicable, to store the {@link #fallback()} for missing values;
 * the result should {@link #adapt(Object)} the current value, hence providing
 * the possibility to react on adaptation or fallback failures.</li>
 * </ul>
 *
 * @param <K>
 *            the type of the remapping result
 * @param <V>
 *            the type of resulting values
 */
public interface Mappable<K, V> extends AdaptationStrategy<V> {

    /**
     * Returns an instance that uses a {@link Supplier} for {@link #remap()}
     * evaluation and the given {@link AdaptationProvider} as the adaptation
     * implementation.
     *
     * @param <K>
     *            the type of the mappable key
     * @param <V>
     *            the type of resulting values
     * @param mapping
     *            the mapping supplier. It must not be {@code null}.
     * @param provider
     *            the adaptation implementation. It must not be {@code null}.
     *
     * @return the instance
     */
    static <K, V> Mappable<K, V> bind(Supplier<? extends K> mapping, AdaptationProvider<V> provider) {
        return new SupplyingMappable<>(mapping, provider);
    }

    /**
     * Returns an instance that uses the given constant for {@link #remap()} and
     * the given {@link AdaptationProvider} as the adaptation implementation.
     *
     * @param <K>
     *            the type of the mappable key
     * @param <V>
     *            the type of resulting values
     * @param mapping
     *            the mapping result
     * @param provider
     *            the adaptation implementation. It must not be {@code null}.
     *
     * @return the instance
     */
    static <K, V> Mappable<K, V> of(K mapping, AdaptationProvider<V> provider) {
        return new ConstantMappable<>(mapping, provider);
    }

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
    static <K, V> Mappable<K, V> nil() {
        return NilMappable.getInstance();
    }

    // Interface core

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
        return derive(source.apply(remap()));
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
     * Returns an adapted value from the source, or the default.
     *
     * <p>
     * If the source does not provide an adaptable argument, the default is used
     * and the sink receives it. The sink is not invoked when the default is not
     * used.
     *
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     * @param sink
     *            the sink to store the default if the source fails. It must not
     *            be {@code null}.
     *
     * @return the result of the adaptation, or the default
     */
    default V let(Function<? super K, ?> source, BiConsumer<? super K, ? super V> sink) {
        final K mapping = remap();
        final V result = derive(source.apply(mapping));
        if (result != null) {
            return result;
        }

        final V fallback = fallback().get();
        sink.accept(mapping, fallback);
        return fallback;
    }

    /**
     * Returns an adapted value from the source, or the default.
     *
     * <p>
     * If the source provides no argument, the default is used and the sink
     * receives it. The sink is not invoked when the default is not used. This
     * method does not call the sink, unlike {@link #let(Function, BiConsumer)},
     * if the source provides an argument that can't be adapted though.
     *
     * @param source
     *            the source of the argument to adapt. It must not be
     *            {@code null}.
     * @param sink
     *            the sink to store the default if the source fails. It must not
     *            be {@code null}.
     *
     * @return the result of the adaptation, or the default; an empty container
     *         is returned if no default exists
     */
    default Optional<V> have(Function<? super K, ?> source, BiConsumer<? super K, ? super V> sink) {
        final K mapping = remap();
        final Object result = source.apply(mapping);
        if (result != null) { // Do not invoke the sink then!
            return Optional.ofNullable(derive(result));
        }

        final Optional<V> fallback = Optional.ofNullable(fallback().get());
        fallback.ifPresent(value -> sink.accept(mapping, value));
        return fallback;
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
        return derive(source.get(remap()));
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
        return put(consumer, derive(value));
    }

    /**
     * Returns an adapted value from the source, or the default; the returned
     * value should be stored in the source.
     *
     * @param map
     *            the map to provide the argument to adapt and possibly to store
     *            the default. It must not be {@code null}.
     *
     * @return the result of the adaptation, or the default
     */
    default V let(Map<K, V> map) {
        return derive(map.compute(remap(), (k, v) -> recover(v)));
    }

    /**
     * Puts the default to the given map if the mapping is absent, otherwise
     * tries to use the present mapping to get the result.
     *
     * @param map
     *            the map to provide the argument to adapt and possibly to store
     *            the default. It must not be {@code null}.
     *
     * @return the result of {@link Map#computeIfAbsent(Object, Function)} after
     *         adapting; an empty container is returned if the result could not
     *         be adapted (either the existing value can't be adapted, or there
     *         is no default)
     */
    default Optional<V> have(Map<? super K, V> map) {
        return Optional.ofNullable(map.computeIfAbsent(remap(), k -> fallback().get()));
    }
}

/**
 * Implementation of {@link Mappable#nil()} result.
 */
enum NilMappable implements Mappable<Object, Object> {

    /** Sole instance of this implementation. */
    INSTANCE;

    /**
     * Returns a properly cast instance.
     *
     * @param <K>
     *            the type of the remapping result
     * @param <V>
     *            the type of resulting values
     *
     * @return the instance
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Mappable<K, V> getInstance() {
        return (Mappable<K, V>) INSTANCE;
    }

    /**
     * @see net.yetamine.sova.AdaptationProvider#adaptation()
     */
    public Adaptation<Object> adaptation() {
        return o -> null;
    }

    /**
     * @see net.yetamine.sova.AdaptationProvider#fallback()
     */
    public Supplier<? extends Object> fallback() {
        return () -> null;
    }

    /**
     * @see net.yetamine.sova.AdaptationProvider#rtti()
     */
    public Class<Object> rtti() {
        return null;
    }

    /**
     * @see net.yetamine.sova.Mappable#remap()
     */
    public Object remap() {
        return null;
    }
}

/**
 * A implementation of the {@link Mappable} interface whose {@link #remap()}
 * implementation returns a constant.
 *
 * @param <K>
 *            the type of the mappable key
 * @param <V>
 *            the type of resulting values
 */
final class ConstantMappable<K, V> implements Mappable<K, V> {

    /** Implementation of the adaptation part. */
    private final AdaptationProvider<V> provider;
    /** Result of {@link #remap()}. */
    private final K remapping;

    /**
     * Creates a new instance.
     *
     * @param implementation
     *            the adaptation implementation. It must not be {@code null}.
     * @param mapping
     *            the mappable supplier. It must not be {@code null}.
     */
    public ConstantMappable(K mapping, AdaptationProvider<V> implementation) {
        provider = Objects.requireNonNull(implementation);
        remapping = mapping;
    }

    /**
     * @see net.yetamine.sova.Mappable#remap()
     */
    public K remap() {
        return remapping;
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

/**
 * A implementation of the {@link Mappable} interface which uses a
 * {@link Supplier} for {@link #remap()} implementation.
 *
 * @param <K>
 *            the type of the mappable key
 * @param <V>
 *            the type of resulting values
 */
final class SupplyingMappable<K, V> implements Mappable<K, V> {

    /** Implementation of the adaptation part. */
    private final AdaptationProvider<V> provider;
    /** Supplier of the remapping values. */
    private final Supplier<? extends K> remapping;

    /**
     * Creates a new instance.
     *
     * @param mapping
     *            the mappable supplier. It must not be {@code null}.
     * @param implementation
     *            the adaptation implementation. It must not be {@code null}.
     */
    public SupplyingMappable(Supplier<? extends K> mapping, AdaptationProvider<V> implementation) {
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
