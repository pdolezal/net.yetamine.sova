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

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An adaptation strategy that is based on downcasting.
 *
 * <p>
 * Besides this class allows definition of the most common adaptation strategy
 * instances, it serves as a builder or a template for creating variants which
 * differ in some parameters, but use the same initial step: downcasting.
 *
 * @param <T>
 *            the type of resulting values
 */
public final class Downcasting<T> implements AdaptationStrategy<T> {

    /** Actual adaptation implementation. */
    private final Adaptation<T> adaptation;
    /** Actual supplier of the default values. */
    private final Supplier<? extends T> fallbackSupplier;
    /** Run-time type information for the values. */
    private final Class<T> rtti;

    /**
     * Creates a new instance.
     *
     * @param type
     *            the RTTI information. It must not be {@code null}.
     * @param adapting
     *            the adaptation implementation. It must not be {@code null}.
     * @param fallback
     *            the fallback supplier. It must not be {@code null}.
     *
     * @throws ClassCastException
     *             if the fallback supplier provides an instance that is not an
     *             instance of the given type
     * @throws AdaptationException
     *             if the fallback supplier provides a value that is not stable
     *             and accepted by the adaptation implementation
     */
    private Downcasting(Class<T> type, Adaptation<T> adapting, Supplier<? extends T> fallback) {
        // Following code verifies the contract at least in the special case
        // which could be easily violated by a programming error
        final T fallbackResult = type.cast(fallback.get());
        if (!Objects.deepEquals(fallbackResult, adapting.apply(fallbackResult))) {
            throw new AdaptationException(fallbackResult);
        }

        rtti = type;
        adaptation = adapting;
        fallbackSupplier = fallback;
    }

    /**
     * @see net.yetamine.sova.core.AdaptationStrategy#adaptation()
     */
    public Adaptation<T> adaptation() {
        return adaptation;
    }

    /**
     * @see net.yetamine.sova.core.AdaptationStrategy#rtti()
     */
    public Class<T> rtti() {
        return rtti;
    }

    /**
     * @see net.yetamine.sova.core.AdaptationStrategy#fallbackSupplier()
     */
    public Supplier<? extends T> fallbackSupplier() {
        return fallbackSupplier;
    }

    /**
     * Creates a new instance using the specified fallback value.
     *
     * @param value
     *            the fallback value for the new instance. The value must be (at
     *            least effectively) immutable, or {@code null}.
     *
     * @return the new instance
     */
    public Downcasting<T> fallback(T value) {
        return new Downcasting<>(rtti, adaptation, () -> value);
    }

    /**
     * Creates a new instance using the specified fallback supplier.
     *
     * @param value
     *            the fallback supplier for the new instance. It must not be
     *            {@code null} and it must be consistent with requirements
     *            imposed by the current {@link #adaptation()}.
     *
     * @return the new instance
     */
    public Downcasting<T> fallbackSupplier(Supplier<? extends T> value) {
        return new Downcasting<>(rtti, adaptation, value);
    }

    /**
     * Downcasts the given object to the specified type.
     *
     * @param <T>
     *            the type of resulting values
     * @param t
     *            the class of the type to downcast the object to. It must not
     *            be {@code null}.
     * @param o
     *            the object to adapt
     *
     * @return the object downcast to the specified type, or {@code null} if the
     *         object is not an instance of the specified type
     */
    public static <T> T apply(Class<T> t, Object o) {
        return t.isInstance(o) ? t.cast(o) : null;
    }

    /**
     * Returns an adaptation that downcasts to the specified type.
     *
     * @param <T>
     *            the type of resulting values
     * @param t
     *            the class of the type that the adaptation shall downcast its
     *            arguments to. It must not be {@code null}.
     *
     * @return an adaptation that downcasts to the specified type
     */
    public static <T> Adaptation<T> adaptation(Class<T> t) {
        Objects.requireNonNull(t);
        return o -> apply(t, o);
    }

    /**
     * Creates a new instance.
     *
     * @param <T>
     *            the type of resulting values
     * @param t
     *            the class of the type that the adaptation shall downcast its
     *            arguments to. It must not be {@code null}.
     * @param predicate
     *            the predicate that tests the adaptation result. It must not be
     *            {@code null}.
     * @param fallbackSupplier
     *            the fallback supplier for the new instance. It must not be
     *            {@code null} and it must be consistent with requirements
     *            imposed by the current {@link #adaptation()}.
     *
     * @return an adaptation strategy that downcasts to the specified type
     */
    public static <T> Downcasting<T> define(Class<T> t, Predicate<? super T> predicate, Supplier<? extends T> fallbackSupplier) {
        return new Downcasting<>(t, adaptation(t).filter(predicate), fallbackSupplier);
    }

    /**
     * Creates a new instance.
     *
     * @param <T>
     *            the type of resulting values
     * @param t
     *            the class of the type that the adaptation shall downcast its
     *            arguments to. It must not be {@code null}.
     *
     * @return an adaptation strategy that downcasts to the specified type
     */
    public static <T> Downcasting<T> to(Class<T> t) {
        return new Downcasting<>(t, adaptation(t), () -> null);
    }

    /**
     * Creates a new instance with the given filter.
     *
     * @param <T>
     *            the type of resulting values
     * @param t
     *            the class of the type that the adaptation shall downcast its
     *            arguments to. It must not be {@code null}.
     * @param predicate
     *            the predicate that tests the adaptation result. It must not be
     *            {@code null}.
     *
     * @return an adaptation strategy that downcasts to the specified type
     */
    public static <T> Downcasting<T> withFilter(Class<T> t, Predicate<? super T> predicate) {
        return new Downcasting<>(t, adaptation(t).filter(predicate), () -> null);
    }

    /**
     * Creates a new instance with the given fallback value.
     *
     * @param <T>
     *            the type of resulting values
     * @param t
     *            the class of the type that the adaptation shall downcast its
     *            arguments to. It must not be {@code null}.
     * @param fallbackSupplier
     *            the fallback supplier for the new instance. It must not be
     *            {@code null} and it must be consistent with requirements
     *            imposed by the current {@link #adaptation()}.
     *
     * @return the new instance
     */
    public static <T> Downcasting<T> withFallbackSupplier(Class<T> t, Supplier<? extends T> fallbackSupplier) {
        return new Downcasting<>(t, adaptation(t), fallbackSupplier);
    }

    /**
     * Creates a new instance with the given fallback value.
     *
     * @param <T>
     *            the type of resulting values
     * @param t
     *            the class of the type that the adaptation shall downcast its
     *            arguments to. It must not be {@code null}.
     * @param fallback
     *            the fallback value for the new instance. The value must be (at
     *            least effectively) immutable, or {@code null}.
     *
     * @return the new instance
     */
    public static <T> Downcasting<T> withFallbackConstant(Class<T> t, T fallback) {
        return withFallbackSupplier(t, () -> fallback);
    }

    /**
     * Casts a {@link Class} instance to match its actual generic argument to
     * the desired type, which is often the result of the type inference when
     * compiling the code.
     *
     * <p>
     * The cast is unchecked and can't be checked in runtime, therefore the user
     * takes the responsibility for the correctness of such a cast. However, the
     * cast is useful for generics which are non-reifiable and such a cast can't
     * be avoided.
     *
     * <p>
     * A typical and harmless example of using this method is for declaring the
     * symbol instances for generic types when the compiler fails to infer some
     * type arguments:
     *
     * <pre>
     * public static final Symbol&lt;Set&lt;String&gt;&gt; STRINGS = new InternalSymbol&lt;&gt;(Downcasting.to(Downcasting.infer(Set.class)));
     * </pre>
     *
     * @param <T>
     *            the type of resulting values
     * @param clazz
     *            the class to cast to
     *
     * @return the given argument cast to the desired formal type
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> infer(Class<?> clazz) {
        return (Class<T>) clazz;
    }
}
