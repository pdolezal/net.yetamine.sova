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
 * Represents the core of an adaptation strategy which provides the adaptation
 * function and a fallback supplier which can supply default values instead of
 * missing input. This interface can provide even the run-time type information
 * when it is available.
 *
 * <p>
 * Subsequent invocations of all methods must provide equal results. Recommended
 * practice is making an implementation immutable and returning constant values,
 * or when the values might be mutable, return copies.
 *
 * @param <T>
 *            the type of resulting values
 */
public interface AdaptationProvider<T> {

    /**
     * Provides the actual adaptation.
     *
     * @return the adaptation, never {@code null}
     */
    Adaptation<T> adaptation();

    /**
     * Provides the supplier of the default values.
     *
     * <p>
     * There are often cases when a default value would be useful as an
     * alternative to a missing input. However, returning a valid instance as a
     * result for a {@code null} argument would cause difficulties in different
     * cases. To avoid the problems, adaptations are defined intentionally as
     * {@code null}-neutral, using {@code null} as the placeholder for absent
     * input data, which usually plays well with collections. To support the
     * cases where a default value might be useful, the default value can be
     * provided by this interface, making the adaptation strategy complete.
     *
     * <p>
     * This method must never return {@code null}, but the provided supplier may
     * return {@code null} if this instance does not define any better default.
     *
     * @return the supplier of the default value, never {@code null}
     */
    Supplier<? extends T> fallback();

    /**
     * Provides the return type of the adaptation.
     *
     * @return the return type of the provided adaptation, or {@code null} if
     *         the type is unknown or can't be provided
     */
    Class<T> rtti();
}
