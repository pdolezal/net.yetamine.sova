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
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An adaptation provider that is based on {@link Adaptable}.
 *
 * <p>
 * Besides this class supports the most common adaptation strategy, it serves as
 * a builder/template for creating variants which differ in some parameters, but
 * use the same initial step: {@link Adaptable#adapt(Class)}.
 *
 * @param <T>
 *            the type of resulting values
 */
public final class AdaptationSelector<T> extends AdaptationDelegate<T> {

    /**
     * Creates a new instance.
     *
     * @param type
     *            the RTTI information. It must not be {@code null}.
     * @param adapting
     *            the adaptation implementation. It must not be {@code null}.
     * @param defaulting
     *            the fallback supplier. It must not be {@code null}.
     */
    private AdaptationSelector(Class<T> type, Adaptation<T> adapting, Supplier<? extends T> defaulting) {
        super(type, adapting, defaulting);
    }

    // Factory methods

    /**
     * Creates a new instance.
     *
     * @param <T>
     *            the type of resulting values
     * @param t
     *            the class of the type that the adaptation shall adapt its
     *            arguments to. It must not be {@code null}.
     * @param p
     *            the predicate that tests the adaptation result. It must not be
     *            {@code null}.
     * @param f
     *            the fallback for the new instance. It must not be {@code null}
     *            and it must be consistent with all requirements imposed by the
     *            adaptation derived from the type.
     *
     * @return an adaptation strategy that adapts to the specified type
     */
    public static <T> AdaptationSelector<T> define(Class<T> t, Predicate<? super T> p, Supplier<? extends T> f) {
        return new AdaptationSelector<>(t, adaptation(t).filter(p), f);
    }

    /**
     * Creates a new instance.
     *
     * @param <T>
     *            the type of resulting values
     * @param t
     *            the class of the type that the adaptation shall adapts its
     *            arguments to. It must not be {@code null}.
     *
     * @return an adaptation strategy that adapts to the specified type
     */
    public static <T> AdaptationSelector<T> using(Class<T> t) {
        return new AdaptationSelector<>(t, adaptation(t), () -> null);
    }

    /**
     * Creates a new instance with the given filter.
     *
     * @param <T>
     *            the type of resulting values
     * @param t
     *            the class of the type that the adaptation shall adapt its
     *            arguments to. It must not be {@code null}.
     * @param p
     *            the predicate that tests the adaptation result. It must not be
     *            {@code null}.
     *
     * @return an adaptation strategy that adapts to the specified type
     */
    public static <T> AdaptationSelector<T> withFilter(Class<T> t, Predicate<? super T> p) {
        return new AdaptationSelector<>(t, adaptation(t).filter(p), () -> null);
    }

    /**
     * Creates a new instance with the given fallback value.
     *
     * @param <T>
     *            the type of resulting values
     * @param t
     *            the class of the type that the adaptation shall adapt its
     *            arguments to. It must not be {@code null}.
     * @param f
     *            the fallback for the new instance. It must not be {@code null}
     *            and it must be consistent with all requirements imposed by the
     *            adaptation derived from the type.
     *
     * @return the new instance
     */
    public static <T> AdaptationSelector<T> withFallback(Class<T> t, Supplier<? extends T> f) {
        return new AdaptationSelector<>(t, adaptation(t), f);
    }

    /**
     * Creates a new instance with the given fallback value.
     *
     * @param <T>
     *            the type of resulting values
     * @param t
     *            the class of the type that the adaptation shall adapt its
     *            arguments to. It must not be {@code null}.
     * @param f
     *            the fallback value for the new instance. The value must be (at
     *            least effectively) immutable, or {@code null}.
     *
     * @return the new instance
     */
    public static <T> AdaptationSelector<T> withFallbackTo(Class<T> t, T f) {
        return withFallback(t, () -> f);
    }

    // Builder-like interface

    /**
     * Creates a new instance using the specified value as the fallback result.
     *
     * @param value
     *            the fallback value for the new instance. The value must be (at
     *            least effectively) immutable, or {@code null}.
     *
     * @return the new instance
     */
    public AdaptationSelector<T> fallbackTo(T value) {
        return new AdaptationSelector<>(rtti(), adaptation(), () -> value);
    }

    /**
     * Creates a new instance using the specified fallback.
     *
     * @param value
     *            the fallback for the new instance. It must not be {@code null}
     *            and it must be consistent with all requirements imposed by the
     *            adaptation derived from the type.
     *
     * @return the new instance
     */
    public AdaptationSelector<T> fallback(Supplier<? extends T> value) {
        return new AdaptationSelector<>(rtti(), adaptation(), value);
    }

    // Supportive adaptation methods

    /**
     * Returns an adaptation that adapts to the specified type.
     *
     * @param <T>
     *            the type of resulting values
     * @param t
     *            the class of the type that the adaptation shall adapt its
     *            arguments to. It must not be {@code null}.
     *
     * @return an adaptation that adapts to the specified type
     */
    public static <T> Adaptation<T> adaptation(Class<T> t) {
        Objects.requireNonNull(t);
        return o -> Optional.ofNullable(o)              // Adaptable uses Optional, unify the handling
                .filter(a -> a instanceof Adaptable)    // The actual argument does not have to be an Adaptable
                .flatMap(a -> ((Adaptable) a).adapt(t)) // Adapt if it is
                .orElse(null);
    }

    /**
     * Adapts the given object to the specified type.
     *
     * @param <T>
     *            the type of resulting values
     * @param t
     *            the class of the type to adapt the object to. It must not be
     *            {@code null}.
     * @param o
     *            the object to adapt
     *
     * @return the object adapted to the specified type, or {@code null} if the
     *         object is not an instance of {@link Adaptable} or does not allow
     *         the required adaptation
     */
    public static <T> T apply(Class<T> t, Adaptable o) {
        return Optional.ofNullable(o).flatMap(a -> a.adapt(t)).orElse(null);
    }
}
