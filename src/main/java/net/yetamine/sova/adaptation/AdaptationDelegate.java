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

package net.yetamine.sova.adaptation;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A default adaptation provider that just uses the given components for the
 * actual operations without any own internal processing.
 *
 * @param <T>
 *            the type of resulting values
 */
public class AdaptationDelegate<T> implements AdaptationProvider<T> {

    /** Actual adaptation implementation. */
    private final Adaptation<T> adaptation;
    /** Actual supplier of the default values. */
    private final Supplier<? extends T> fallback;
    /** Run-time type information for the values. */
    private final Class<T> rtti;

    /**
     * Creates a new instance.
     *
     * <p>
     * If assertions are enabled, {@link ClassCastException} besides the usual
     * {@link AssertionError} might raise when the parameters are suspicious.
     *
     * @param type
     *            the RTTI information. It must not be {@code null}.
     * @param adapting
     *            the adaptation implementation. It must not be {@code null}.
     * @param defaulting
     *            the fallback supplier. It must not be {@code null}.
     */
    public AdaptationDelegate(Class<T> type, Adaptation<T> adapting, Supplier<? extends T> defaulting) {
        assert checkFallback(type, adapting, defaulting);
        adaptation = Objects.requireNonNull(adapting);
        fallback = Objects.requireNonNull(defaulting);
        rtti = Objects.requireNonNull(type);
    }

    /**
     * @see net.yetamine.sova.adaptation.AdaptationStrategy#adaptation()
     */
    public final Adaptation<T> adaptation() {
        return adaptation;
    }

    /**
     * @see net.yetamine.sova.adaptation.AdaptationProvider#fallback()
     */
    public final Supplier<? extends T> fallback() {
        return fallback;
    }

    /**
     * @see net.yetamine.sova.adaptation.AdaptationStrategy#rtti()
     */
    public final Class<T> rtti() {
        return rtti;
    }

    /**
     * Checks the fallback supplier in the debugging mode.
     *
     * @param type
     *            the RTTI information. It must not be {@code null}.
     * @param adapting
     *            the adaptation implementation. It must not be {@code null}.
     * @param defaulting
     *            the fallback supplier. It must not be {@code null}.
     *
     * @return {@code true}
     *
     * @throws ClassCastException
     *             if the fallback supplier provides an instance that is not an
     *             instance of the given type
     * @throws AdaptationException
     *             if the fallback supplier provides a value that is not stable
     *             and accepted by the adaptation implementation
     */
    private static <T> boolean checkFallback(Class<T> type, Adaptation<T> adapting, Supplier<? extends T> defaulting) {
        // Following code verifies the contract at least in the special case
        // which could be easily violated by a programming error
        final T fallbackResult = type.cast(defaulting.get());
        if (!Objects.deepEquals(fallbackResult, adapting.apply(fallbackResult))) {
            throw new AssertionError(new AdaptationException(fallbackResult));
        }

        return true;
    }
}
