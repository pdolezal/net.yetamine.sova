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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An adaptation provider that unifies the operands to a canonical form.
 *
 * <p>
 * Besides this class supports the most common adaptation strategy, it serves as
 * a builder/template for creating variants which differ in some parameters, but
 * use the same initial step: conditional downcasting.
 *
 * @param <T>
 *            the type of resulting values
 */
public final class Unifying<T> extends AdaptationDelegate<T> {

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
    private Unifying(Class<T> type, Adaptation<T> adapting, Supplier<? extends T> defaulting) {
        super(type, adapting, defaulting);
    }

    // Factory methods

    /**
     * Creates a new instance.
     *
     * @param <T>
     *            the type of resulting values
     * @param t
     *            the class of the type that the adaptation shall downcast its
     *            arguments to. It must not be {@code null}.
     * @param u
     *            the unification function applied when the argument can't be
     *            cast down to the desired class. It must not be {@code null}.
     * @param p
     *            the predicate that tests the adaptation result. It must not be
     *            {@code null}.
     * @param f
     *            the fallback for the new instance. It must not be {@code null}
     *            and it must be consistent with all requirements imposed by the
     *            adaptation derived from the type.
     *
     * @return an adaptation strategy that downcasts to the specified type
     */
    public static <T> Unifying<T> define(Class<T> t, Function<Object, T> u, Predicate<? super T> p, Supplier<? extends T> f) {
        return new Unifying<>(t, adaptation(t, u).filter(p), f);
    }

    /**
     * Creates a new instance.
     *
     * @param <T>
     *            the type of resulting values
     * @param t
     *            the class of the type that the adaptation shall downcast its
     *            arguments to. It must not be {@code null}.
     * @param u
     *            the unification function applied when the argument can't be
     *            cast down to the desired class. It must not be {@code null}.
     *
     * @return an adaptation strategy that downcasts to the specified type
     */
    public static <T> Unifying<T> to(Class<T> t, Function<Object, T> u) {
        return new Unifying<>(t, adaptation(t, u), () -> null);
    }

    /**
     * Creates a new instance with the given filter.
     *
     * @param <T>
     *            the type of resulting values
     * @param t
     *            the class of the type that the adaptation shall downcast its
     *            arguments to. It must not be {@code null}.
     * @param u
     *            the unification function applied when the argument can't be
     *            cast down to the desired class. It must not be {@code null}.
     * @param p
     *            the predicate that tests the adaptation result. It must not be
     *            {@code null}.
     *
     * @return an adaptation strategy that downcasts to the specified type
     */
    public static <T> Unifying<T> withFilter(Class<T> t, Function<Object, T> u, Predicate<? super T> p) {
        return new Unifying<>(t, adaptation(t, u).filter(p), () -> null);
    }

    /**
     * Creates a new instance with the given fallback value.
     *
     * @param <T>
     *            the type of resulting values
     * @param t
     *            the class of the type that the adaptation shall downcast its
     *            arguments to. It must not be {@code null}.
     * @param u
     *            the unification function applied when the argument can't be
     *            cast down to the desired class. It must not be {@code null}.
     * @param f
     *            the fallback for the new instance. It must not be {@code null}
     *            and it must be consistent with all requirements imposed by the
     *            adaptation derived from the type.
     *
     * @return the new instance
     */
    public static <T> Unifying<T> withFallback(Class<T> t, Function<Object, T> u, Supplier<? extends T> f) {
        return new Unifying<>(t, adaptation(t, u), f);
    }

    /**
     * Creates a new instance with the given fallback value.
     *
     * @param <T>
     *            the type of resulting values
     * @param t
     *            the class of the type that the adaptation shall downcast its
     *            arguments to. It must not be {@code null}.
     * @param u
     *            the unification function applied when the argument can't be
     *            cast down to the desired class. It must not be {@code null}.
     * @param f
     *            the fallback value for the new instance. The value must be (at
     *            least effectively) immutable, or {@code null}.
     *
     * @return the new instance
     */
    public static <T> Unifying<T> withFallbackTo(Class<T> t, Function<Object, T> u, T f) {
        return withFallback(t, u, () -> f);
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
    public Unifying<T> fallbackTo(T value) {
        return new Unifying<>(rtti(), adaptation(), () -> value);
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
    public Unifying<T> fallback(Supplier<? extends T> value) {
        return new Unifying<>(rtti(), adaptation(), value);
    }

    // Supportive casting methods

    /**
     * Returns an adaptation that downcasts to the specified type.
     *
     * @param <T>
     *            the type of resulting values
     * @param t
     *            the class of the type that the adaptation shall downcast its
     *            arguments to. It must not be {@code null}.
     * @param u
     *            the unification function applied when the argument can't be
     *            cast down to the desired class. It must not be {@code null}.
     *
     * @return an adaptation that downcasts to the specified type
     */
    public static <T> Adaptation<T> adaptation(Class<T> t, Function<Object, T> u) {
        Objects.requireNonNull(t);
        Objects.requireNonNull(u);
        return o -> apply(t, u, o);
    }

    /**
     * Downcasts the given object to the specified type if possible, otherwise
     * it invokes the unification function.
     *
     * @param <T>
     *            the type of resulting values
     * @param t
     *            the class of the type to downcast the object to. It must not
     *            be {@code null}.
     * @param u
     *            the unification function applied when the argument can't be
     *            cast down to the desired class. It must not be {@code null}.
     * @param o
     *            the object to adapt
     *
     * @return the object downcast to the specified type, or {@code null} if the
     *         object is not an instance of the specified type
     */
    public static <T> T apply(Class<T> t, Function<Object, T> u, Object o) {
        if (o == null) {
            return null;
        }

        return t.isInstance(o) ? t.cast(o) : u.apply(o);
    }
}
