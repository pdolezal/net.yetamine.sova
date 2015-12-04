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

import java.util.function.Supplier;

/**
 * Provides an adaptation strategy.
 *
 * <p>
 * Subsequent invocations of all methods must provide equal results. Recommended
 * practice is making an implementation immutable and returning constant values,
 * or when the values might be mutable, return copies.
 *
 * @param <T>
 *            the type of resulting values
 */
public interface AdaptationStrategy<T> {

    /**
     * Provides the actual adaptation.
     *
     * @return the adaptation, never {@code null}
     */
    Adaptation<T> adaptation();

    /**
     * Provides the return type of the adaptation.
     *
     * @return the return type of the provided adaptation, or {@code null} if
     *         the type is unknown or can't be provided
     */
    Class<T> rtti();

    /**
     * Returns the default value that shall be considered, if required or
     * suitable, instead of trying to adapt a {@code null} value (when an
     * adaptation must return {@code null} too).
     *
     * <p>
     * There are often cases when a default value would be useful as an
     * alternative to a missing input. However, returning a valid instance as a
     * result for a {@code null} argument would cause difficulties in different
     * cases. To avoid the problems, adaptations are defined intentionally as
     * {@code null}-neutral, using {@code null} as the placeholder for absent
     * input data, which usually plays well with collections. To support the
     * cases where a default value might be useful, the default value can be
     * provided by this interface.
     *
     * <p>
     * This method is a convenient shortcut to {@code fallbackSupplier().get()}.
     *
     * @return the default value for the adaptation, or {@code null} if none is
     *         defined
     */
    default T fallback() {
        return fallbackSupplier().get();
    }

    /**
     * Provides the supplier of the default values.
     *
     * <p>
     * This method must never return {@code null}, but it may return a supplier
     * that returns (always) {@code null}. See the {@link #fallback()} method -
     * this method must provide a consistent base for its implementation.
     *
     * @return the supplier of the default values {@code null}
     */
    Supplier<? extends T> fallbackSupplier();
}
