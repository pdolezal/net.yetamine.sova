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

import java.util.function.Supplier;

/**
 * Represents the core of an adaptation strategy which provides the adaptation
 * function and suppliers for fallback values for handling any missing inputs.
 * This interface can provide even the run-time type information when it is
 * available.
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
     * Provides the supplier of the default value(s).
     *
     * <p>
     * There are often cases when a default value is useful as a surrogate for a
     * missing input. However, returning an instance for a {@code null} argument
     * would cause difficulties in different cases. To avoid the difficulties,
     * adaptations are defined intentionally as {@code null}-neutral, using
     * {@code null} as the placeholder for absent input data, which usually
     * plays well with collections. To support the mentioned cases, where a
     * default value might be useful, this interface can supply the default
     * value, making the adaptation strategy complete.
     *
     * <p>
     * This method must never return {@code null}, but the provided supplier may
     * return {@code null} if this instance does not define any better default.
     * If the result is mutable, the supplier must return always a new instance
     * that can't be affected by changes of other instances.
     *
     * <p>
     * The fallback supplier can be very effective when dealing with storages
     * that support computation a value on demand, e.g., when a value absents
     * and should be inserted.
     *
     * @return the supplier of the default value, never {@code null}
     */
    Supplier<? extends T> fallback();

    /**
     * Provides the run-time type information (RTTI) for the return type of the
     * adaptation.
     *
     * @return the return type of the provided adaptation, or {@code null} if
     *         the type is actually unknown or its RTTI can't be provided
     */
    Class<T> rtti();
}
