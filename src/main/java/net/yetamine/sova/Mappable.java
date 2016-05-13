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
 * <li>A {@code pull} method should just return the value from the source using
 * {@link #remap()} as the input.</li>
 * <li>A {@code push} method should just store the value using {@link #remap()}
 * for supplying the key.</li>
 * <li>A {@code get} method should use {@link #nullable(Object)} to adapt the
 * result of the data source.</li>
 * <li>A {@code use} method should use {@link #surrogate(Object)} to adapt the
 * result of the data source.</li>
 * <li>A {@code find} method should use {@link #optional(Object)} to adapt the
 * result of the data source.</li>
 * <li>A {@code yield} method should use {@link #adapt(Object)} to adapt the
 * result of the data source.</li>
 * <li>A {@code put} method should use {@link #nullable(Object)} to adapt the
 * value and store the result, using the {@code push} variant.</li>
 * <li>A {@code set} method should use {@link #nullable(Object)} to adapt the
 * value and store the result; however, if the argument is {@code null}, then
 * the method should remove the mapping from the target, otherwise attempt to
 * update it, perhaps using a fallback strategy for non-adaptable values.</li>
 * <li>A {@code let} method should use {@link #optional(Object)} to adapt the
 * value and store the result if adaptation succeeded, using the {@code push}
 * variant. The result is then returned, so that the adapted value can be used
 * or a failure can be handled in any specific way.</li>
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
        return new DynamicMappable<>(mapping, provider);
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
     * <p>
     * This method for equal instances has to return equal values (in the terms
     * of {@link Object#equals(Object)}). However, equal results of this method
     * do not imply that the instances must be equal, although within a given
     * context such an implication, in a weak form, might be practically valid,
     * e.g., when a well-known key with well-defined semantics within a library
     * or context is used, binding an instance to such a key assumes that the
     * semantics the key is honored and all instances bound to the key behave
     * basically in the same way.
     *
     * @return the key for this instance, which should never be {@code null}
     */
    K remap();

    // Generic access methods

    /**
     * Returns a value from the source using {@link #remap()} as the input.
     *
     * @param <R>
     *            the type of the result
     * @param source
     *            the source of the result. It must not be {@code null}.
     *
     * @return the value retrieved from the source
     */
    default <R> R pull(Function<? super K, ? extends R> source) {
        return source.apply(remap());
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
    default void push(BiConsumer<? super K, ? super V> consumer, V value) {
        consumer.accept(remap(), value);
    }

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
        return nullable(pull(source));
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
        return surrogate(pull(source));
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
        return optional(pull(source));
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
        return adapt(pull(source));
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
    default void put(BiConsumer<? super K, ? super V> consumer, Object value) {
        push(consumer, nullable(value));
    }

    /**
     * Transfers the adapted value to the given consumer if the value could be
     * adapted to a valid object.
     *
     * @param consumer
     *            the consumer to accept the {@link #remap()} result and the
     *            adapted value. It must not be {@code null}.
     * @param value
     *            the value to adapt and transfer
     *
     * @return the result of the adaptation
     */
    default Optional<V> let(BiConsumer<? super K, ? super V> consumer, Object value) {
        final Optional<V> result = optional(value);
        result.ifPresent(v -> push(consumer, v));
        return result;
    }

    // Map-based access methods

    /**
     * Returns an adapted value from the source using {@link #remap()} as the
     * input.
     *
     * @param <R>
     *            the type of the result
     * @param source
     *            the source of the result. It must not be {@code null}.
     *
     * @return the value retrieved from the source
     */
    default <R> R pull(Map<?, ? extends R> source) {
        return source.get(remap());
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
    default Object push(Map<? super K, ? super V> consumer, V value) {
        return consumer.put(remap(), value);
    }

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
        return nullable(pull(source));
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
        return surrogate(pull(source));
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
        return optional(pull(source));
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
        return adapt(pull(source));
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
     * @return the adapted result of the {@link Map#put(Object, Object)}
     */
    default V put(Map<? super K, ? super V> consumer, Object value) {
        return nullable(push(consumer, nullable(value)));
    }

    /**
     * Puts the specified value to the given map, or removes the existing value
     * from the map if the specified value is {@code null}.
     *
     * <p>
     * If the value is not {@code null}, but can't be adapted, the method itself
     * does not modify the map and rather invokes the given {@link Supplier} for
     * resolving the situation, e.g., by throwing a suitable exception or fixing
     * the map and returning a surrogate result.
     *
     * @param consumer
     *            the map to accept the {@link #remap()} result and the adapted
     *            value. It must not be {@code null}.
     * @param value
     *            the value to adapt and transfer
     * @param fallback
     *            the fallback supplier to invoke when the value is not
     *            {@code null}, but can't be adapted. It must not be
     *            {@code null}.
     *
     * @return the adaptation of the specified value
     */
    default V set(Map<? super K, ? super V> consumer, Object value, Supplier<? extends V> fallback) {
        Objects.requireNonNull(fallback);

        if (value == null) {
            consumer.remove(remap());
            return null;
        }

        final V result = nullable(value); // May return null only when adaptation fails

        if (result == null) {
            return fallback.get();
        }

        push(consumer, result);
        return result;
    }

    /**
     * Transfers the adapted value to the given consumer if the value could be
     * adapted to a valid object.
     *
     * @param consumer
     *            the consumer to accept the {@link #remap()} result and the
     *            adapted value. It must not be {@code null}.
     * @param value
     *            the value to adapt and transfer
     *
     * @return the result of the adaptation
     */
    default Optional<V> let(Map<? super K, ? super V> consumer, Object value) {
        final Optional<V> result = optional(value);
        result.ifPresent(v -> push(consumer, v));
        return result;
    }

    /**
     * Returns a value from the source if the source can supply a valid result,
     * otherwise fixes the source with a surrogate value and returns it instead.
     * The returned value should be then present in the source in either case.
     *
     * @param map
     *            the map to provide or accept the value. It must not be
     *            {@code null}.
     * @param surrogate
     *            the surrogate supplier. It must not be {@code null}.
     *
     * @return the original or surrogate value, which the source contains now;
     *         {@code null} may be returned if the surrogate does not pass the
     *         adaptation
     */
    default V supply(Map<? super K, ? super V> map, Supplier<?> surrogate) {
        final Object computed = map.compute(remap(), (k, v) -> {
            final V current = nullable(v);
            return (current != null) ? current : nullable(surrogate.get());
        });

        // The unchecked cast is safe as long as the compute() function adapts
        // both the current and surrogate value (depending on which returns)
        @SuppressWarnings("unchecked")
        final V result = (V) computed;
        return result;
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
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return "Mappable[?]";
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
 * A implementation of the {@link Mappable} interface which delegates the core
 * to the given implementation.
 *
 * @param <K>
 *            the type of the mappable key
 * @param <V>
 *            the type of resulting values
 */
abstract class DelegatingMappable<K, V> implements Mappable<K, V> {

    /** Actual adaptation implementation. */
    private final Adaptation<V> adaptation;
    /** Actual supplier of default values. */
    private final Supplier<? extends V> fallback;
    /** Run-time type information for the values. */
    private final Class<V> rtti;

    /**
     * Creates a new instance.
     *
     * @param implementation
     *            the adaptation implementation. It must not be {@code null}.
     */
    protected DelegatingMappable(AdaptationProvider<V> implementation) {
        adaptation = implementation.adaptation();
        fallback = implementation.fallback();
        rtti = implementation.rtti();
        assert (adaptation != null);
        assert (fallback != null);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Mappable[%s]", remap());
    }

    /**
     * @see net.yetamine.sova.AdaptationStrategy#adaptation()
     */
    public final Adaptation<V> adaptation() {
        return adaptation;
    }

    /**
     * @see net.yetamine.sova.AdaptationProvider#fallback()
     */
    public final Supplier<? extends V> fallback() {
        return fallback;
    }

    /**
     * @see net.yetamine.sova.AdaptationStrategy#rtti()
     */
    public final Class<V> rtti() {
        return rtti;
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
final class ConstantMappable<K, V> extends DelegatingMappable<K, V> {

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
        super(implementation);
        remapping = mapping;
    }

    /**
     * @see net.yetamine.sova.Mappable#remap()
     */
    public K remap() {
        return remapping;
    }
}

/**
 * A implementation of the {@link Mappable} interface whose {@link #remap()}
 * implementation delegates to a {@link Supplier}.
 *
 * @param <K>
 *            the type of the mappable key
 * @param <V>
 *            the type of resulting values
 */
final class DynamicMappable<K, V> extends DelegatingMappable<K, V> {

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
    public DynamicMappable(Supplier<? extends K> mapping, AdaptationProvider<V> implementation) {
        super(implementation);
        remapping = Objects.requireNonNull(mapping);
    }

    /**
     * @see net.yetamine.sova.Mappable#remap()
     */
    public K remap() {
        return remapping.get();
    }
}
