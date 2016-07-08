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
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents a conditional type-safe adaptation that adapts an input object to
 * the desired format.
 *
 * <p>
 * The resulting format of an adaptation has the specified type and may require
 * additional conditions on the representation that are not imposed by the type
 * itself (e.g., an adaptation may return a non-empty list of numbers). Usually,
 * an adaptation does not change the type of its arguments, rather verifies the
 * type and the preconditions, which differs from a conversion. On the other
 * hand, an implementation may support multiple types of the argument, while
 * specifying one of them as the canonical format, which shall be the result of
 * adapting the other variants.
 *
 * <p>
 * This interface intentionally does not inherit from {@link Function}, which is
 * more suitable as the base for a general conversion interface and which offers
 * additional operations like {@link Function#compose(Function)} that are not so
 * compatible with this interface.
 *
 * <p>
 * Implementations must satisfy following conditions:
 *
 * <ul>
 * <li>{@code apply(null)} must return {@code null}.</li>
 * <li>{@code apply(x)} must be equal to {@code apply(x)}.</li>
 * <li>{@code apply(x)} must be equal to {@code apply(apply(x))}.</li>
 * <li>All other methods must be consistent with {@link #apply(Object)}.</li>
 * </ul>
 *
 * The sense of the conditions is that the adaptation must provide stable result
 * always and when a result of an adaptation is stored, loading the result later
 * using the adaptation must provide an equal value.
 *
 * <p>
 * Implementations should be thread-safe, preferably immutable.
 *
 * @param <T>
 *            the type of resulting values
 */
@FunctionalInterface
public interface Adaptation<T> {

    /**
     * Adapts the given argument.
     *
     * <p>
     * This is the functional method of this interface, therefore when talking
     * about an adaptation or a result of an adaptation in a broader sense, it
     * is meant as the actual adaptation, resp. its result is meant as the
     * result of an adaptation.
     *
     * <p>
     * This method itself should throw no exceptions. However, it should relay
     * exceptions thrown as the result of delegation to other code, even if it
     * is assumed not to throw exceptions either.
     *
     * @param o
     *            the argument to adapt
     *
     * @return the result of the adaptation, or {@code null} if the argument is
     *         {@code null} or could not be adapted
     */
    T apply(Object o);

    /**
     * Attempts to adapt the given argument.
     *
     * <p>
     * This method is a variant of {@link #apply(Object)} which returns an
     * {@link Optional} instead of the value or {@code null} directly. The
     * method otherwise behaves same.
     *
     * @param o
     *            the object to adapt
     *
     * @return an {@link Optional} containing the result of the adaptation
     */
    default Optional<T> attempt(Object o) {
        return Optional.ofNullable(apply(o));
    }

    /**
     * Returns a conditional adaptation testing non-{@code null} results of this
     * adaptation with the given predicate and yields a result iff the predicate
     * returns {@code true}, otherwise {@code null}.
     *
     * @param predicate
     *            the predicate that tests the adaptation result. It must not be
     *            {@code null} and it must be consistent with the implementation
     *            of this instance, so that the returned adaptation complies to
     *            the contracts of the inherited methods.
     *
     * @return the conditional adaptation
     */
    default Adaptation<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);

        return o -> {
            final T result = apply(o);
            // More efficient than using Optional
            return ((result != null) && predicate.test(result)) ? result : null;
        };
    }

    /**
     * Returns a composed function that adapts its argument using this instance
     * and maps the adaptation result (possibly {@code null}) with the provided
     * mapping function.
     *
     * @param <V>
     *            the return type of the resulting composed function
     * @param mapping
     *            the mapping function. It must not be {@code null}.
     *
     * @return the composed function
     */
    default <V> Function<Object, V> map(Function<? super T, V> mapping) {
        Objects.requireNonNull(mapping);
        return o -> mapping.apply(apply(o));
    }
}
