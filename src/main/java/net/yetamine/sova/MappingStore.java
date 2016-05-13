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

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A collection of associations defined using {@link Mappable} references.
 *
 * <h1>Overview</h1>
 *
 * <p>
 * The references provide both the keys (using {@link Mappable#remap()}) for the
 * values and adaptation strategies to adapt the values to the desired form. The
 * interface can be understood as an extension of {@link Mapping} with mutating
 * operations, while retaining its functional character.
 *
 * <p>
 * This interface intentionally lacks enumeration methods or means for getting
 * the whole set of data, which allows implementations to materialize mappings
 * on demand, storing only changes.
 *
 * <p>
 * Despite of the declaration of mutating operations, changing the content may
 * be prohibited anyway in the similar way in which regular maps may prevent
 * their modifications. The protection may be even partial (e.g., applied on
 * selected entries).
 *
 * <h1>Exceptions</h1>
 *
 * <p>
 * Almost each operation may throw {@link UnsupportedOperationException} if the
 * operation could not be peformed, because it is indeed unsupported, or it may
 * not performed due to a restriction.
 *
 * <p>
 * Almost each operation may throw {@link AdaptationException} if the value to
 * be inserted in the container fails to pass the adaptation test required for
 * storing a value.
 *
 * @param <K>
 *            the type of the keys
 * @param <V>
 *            the type of the values
 */
public interface MappingStore<K, V> extends Mapping {

    // Insertion methods

    /**
     * Associates the specified value with the specified reference.
     *
     * @param <R>
     *            the type of the result
     * @param ref
     *            the reference with which the specified value is to be
     *            associated. It must not be {@code null}.
     * @param value
     *            the value to be associated with the specified reference. It
     *            must not be {@code null}.
     *
     * @return the previously associated value, or {@code null} if no such value
     *         existed before or the value could not be adapted to the desired
     *         form
     */
    <R extends V> R put(Mappable<? extends K, R> ref, R value);

    /**
     * Associates the specified value with the specified reference.
     *
     * <p>
     * This method is a {@code null}-tolerant {@link #put(Mappable, Object)}: if
     * the value is {@code null}, it rather removes an existing association than
     * fails.
     *
     * @param <R>
     *            the type of the result
     * @param ref
     *            the reference with which the specified value is to be
     *            associated. It must not be {@code null}.
     * @param value
     *            the value to be associated with the specified reference. If
     *            {@code null}, the current association is removed.
     *
     * @return the previously associated value, or {@code null} if no such value
     *         existed before or the value could not be adapted to the desired
     *         form
     */
    default <R extends V> V set(Mappable<? extends K, R> ref, R value) {
        return (value == null) ? remove(ref) : put(ref, value);
    }

    /**
     * Associates the specified value with the specified reference if the value
     * can be adapted to a valid object.
     *
     * @param <R>
     *            the type of the result
     * @param ref
     *            the reference with which the specified value is to be
     *            associated. It must not be {@code null}.
     * @param value
     *            the value to adapt and transfer
     *
     * @return the result of the adaptation
     */
    <R extends V> Optional<R> let(Mappable<? extends K, R> ref, Object value);

    /**
     * Associates the specified value with the specified reference if the
     * reference is not associated yet.
     *
     * @param <R>
     *            the type of the result
     * @param ref
     *            the reference with which the specified value is to be
     *            associated. It must not be {@code null}.
     * @param value
     *            the value to be associated with the specified reference. It
     *            must not be {@code null}.
     *
     * @return the previously associated value, or {@code null} if no such value
     *         existed before or the value could not be adapted to the desired
     *         form
     */
    <R extends V> R putIfAbsent(Mappable<? extends K, R> ref, R value);

    // Replacement methods

    /**
     * Replaces the entry for the specified reference only if currently
     * associated with the specified value.
     *
     * @param <R>
     *            the type of the mapping result
     * @param ref
     *            the reference with which the specified value is to be
     *            associated. It must not be {@code null}.
     * @param oldValue
     *            the expected currently associated value. It should not be
     *            {@code null}; if the underlying storage does not support
     *            {@code null} values, this method may fail in such a case.
     * @param newValue
     *            the value to be associated with the specified reference. It
     *            must not be {@code null}.
     *
     * @return {@code true} if the value was replaced, {@code false} if the
     *         expected value didn't match the actual current value
     */
    <R extends V> boolean replace(Mappable<? extends K, R> ref, V oldValue, R newValue);

    /**
     * Replaces the entry for the specified reference only if it is currently
     * associated with some value.
     *
     * @param <R>
     *            the type of the result
     * @param ref
     *            the reference with which the specified value is to be
     *            associated. It must not be {@code null}.
     * @param value
     *            the value to be associated with the specified reference. It
     *            must not be {@code null}.
     *
     * @return the replaced value, or {@code null} if no such value was
     *         associated with the reference or the result could not be adapted
     */
    <R extends V> V replace(Mappable<? extends K, R> ref, R value);

    // Removal methods

    /**
     * Removes an entry for the specified reference.
     *
     * @param <R>
     *            the type of the result
     * @param ref
     *            the reference identifying the value to be removed. It must not
     *            be {@code null}.
     *
     * @return the removed value, or {@code null} if no such value exists or the
     *         existing value could not be adapted to the desired form
     */
    <R extends V> R remove(Mappable<?, R> ref);

    /**
     * Removes an entry for the specified reference if the reference maps to the
     * given value.
     *
     * @param ref
     *            the reference identifying the value to be removed. It must not
     *            be {@code null}.
     * @param value
     *            the value to be removed. It should not be {@code null}; if the
     *            underlying storage does not support {@code null} values, this
     *            method may fail in such a case.
     *
     * @return {@code true} if the value was removed (no matter if has been
     *         adaptable or not), {@code false} if the value hadn't existed
     */
    boolean remove(Mappable<?, ?> ref, Object value);

    // Composite operations

    /**
     * Associates the specified reference, if the reference is not already
     * associated with a value, with the given value; otherwise, replaces the
     * associated value with the results of the given remapping function, or
     * removes if the result is {@code null}.
     *
     * @param <R>
     *            the type of the result
     * @param ref
     *            reference with which the specified value is to be associated.
     *            It must not be {@code null}.
     * @param value
     *            the value to associate the reference with
     * @param remapping
     *            the function to compute a value. It must not be {@code null}.
     *
     * @return the new value associated with the specified reference, or
     *         {@code null} if no value is associated with the reference
     */
    <R extends V> R merge(Mappable<? extends K, R> ref, R value, BiFunction<? super R, ? super R, ? extends R> remapping);

    /**
     * Attempts to compute a mapping for the specified reference and its current
     * mapped value (or {@code null} if there is no current mapping).
     *
     * @param <U>
     *            the type of the mapping key
     * @param <R>
     *            the type of the result
     * @param ref
     *            reference with which the specified value is to be associated.
     *            It must not be {@code null}.
     * @param remapping
     *            the function to compute a value. It must not be {@code null}.
     *
     * @return the new value associated with the specified reference, or
     *         {@code null} if none
     */
    <U extends K, R extends V> R compute(Mappable<U, R> ref, BiFunction<? super U, ? super R, ? extends R> remapping);

    /**
     * Attempts to compute a mapping for the specified reference only if the
     * reference is not associated yet.
     *
     * @param <U>
     *            the type of the mapping key
     * @param <R>
     *            the type of the result
     * @param ref
     *            reference with which the specified value is to be associated.
     *            It must not be {@code null}.
     * @param mapping
     *            the function to compute a value. It must not be {@code null}.
     *
     * @return the current (existing or computed) value associated with the
     *         specified reference, or {@code null} if none
     */
    default <U extends K, R extends V> R computeIfAbsent(Mappable<U, R> ref, Function<? super U, ? extends R> mapping) {
        return compute(ref, (k, v) -> (ref.nullable(v) == null) ? ref.adapt(mapping.apply(k)).request() : v);
    }

    /**
     * Attempts to compute a mapping for the specified reference only if the
     * reference is associated already to some value.
     *
     * @param <U>
     *            the type of the mapping key
     * @param <R>
     *            the type of the result
     * @param ref
     *            reference with which the specified value is to be associated.
     *            It must not be {@code null}.
     * @param remapping
     *            the function to compute a value. It must not be {@code null}.
     *
     * @return the new value associated with the specified reference, or
     *         {@code null} if none
     */
    default <U extends K, R extends V> R computeIfPresent(Mappable<U, R> ref, BiFunction<? super U, ? super R, ? extends R> remapping) {
        return compute(ref, (k, v) -> (ref.nullable(v) != null) ? ref.adapt(remapping.apply(k, v)).request() : null);
    }

    /**
     * Attempts to compute a mapping for the specified reference only if the
     * reference is not associated yet.
     *
     * @param <R>
     *            the type of the result
     * @param <S>
     *            the type of the supplied value
     * @param ref
     *            reference with which the specified value is to be associated.
     *            It must not be {@code null}.
     * @param valueSupplier
     *            the supplier of the value. It must not be {@code null}.
     *
     * @return the computed value associated with the specified reference
     */
    default <R extends V, S extends R> R supplyIfAbsent(Mappable<? extends K, R> ref, Supplier<S> valueSupplier) {
        return compute(ref, (k, v) -> (ref.nullable(v) == null) ? ref.adapt(valueSupplier.get()).request() : v);
    }

    /**
     * Attempts to compute a mapping for the specified reference only if the
     * reference is associated already to some value.
     *
     * @param <R>
     *            the type of the result
     * @param <S>
     *            the type of the supplied value
     * @param ref
     *            reference with which the specified value is to be associated.
     *            It must not be {@code null}.
     * @param valueSupplier
     *            the supplier of the value. It must not be {@code null}.
     *
     * @return the computed value associated with the specified reference
     */
    default <R extends V, S extends R> R supplyIfPresent(Mappable<? extends K, R> ref, Supplier<S> valueSupplier) {
        return compute(ref, (k, v) -> (ref.nullable(v) != null) ? ref.adapt(valueSupplier.get()).request() : null);
    }
}
