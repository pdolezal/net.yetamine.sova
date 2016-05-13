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
import java.util.function.Function;

/**
 * Represents an adaptation strategy that provides the adaptation function and a
 * fallback which can provide default values instead of some missing input. This
 * interface can supply even the run-time type information when it is available.
 *
 * <p>
 * Subsequent invocations of all methods must provide equal results. Recommended
 * practice is making an implementation immutable and returning constant values,
 * or when the values might be mutable, return copies.
 *
 * <p>
 * This interface intentionally does not inherit from {@link Function} to allow
 * inherited types to decide themselves if, how and for which purpose would the
 * prominent function-like capability provide.
 *
 * @param <T>
 *            the type of resulting values
 */
public interface AdaptationStrategy<T> extends AdaptationProvider<T> {

    /**
     * Returns a representation of the adaptation result, which offers flexible
     * means to consider the fallback, if any, and apply other post-conditions.
     *
     * <p>
     * The default implementation evaluates the adaptation of the argument right
     * away and returns an object that captures the result and its context. This
     * is the recommended implementation, which is more efficient in the typical
     * use case where the result of this method is immediately processed, on the
     * other hand, an implementation may supply an object that evaluates the
     * adaptation on demand.
     *
     * @param o
     *            the argument for the adaptation
     *
     * @return an object for retrieving the result of the adaptation
     */
    default AdaptationResult<T> adapt(Object o) {
        return AdaptationResult.of(o, nullable(o), this);
    }

    // Convenient application methods

    /**
     * Returns the adaptation of the given argument; this method is a shortcut
     * for {@code adaptation().apply(o)} and therefore returns the most direct
     * result of the adaptation which may be {@code null} if the argument is
     * {@code null} or could not be adapted.
     *
     * @param o
     *            the argument to adapt
     *
     * @return the result of the adaptation of the given argument, possibly
     *         {@code null} if the argument is {@code null} or could not be
     *         adapted
     */
    default T nullable(Object o) {
        return adaptation().apply(o);
    }

    /**
     * Returns the result of {@link #nullable(Object)} as an {@link Optional}.
     *
     * @param o
     *            the argument to adapt
     *
     * @return the result of {@link #nullable(Object)} as an {@link Optional}
     */
    default Optional<T> optional(Object o) {
        return Optional.ofNullable(nullable(o));
    }

    /**
     * Returns the adaptation of the given argument, or the fallback if the
     * argument is {@code null} or could not be adapted, which still may be
     * {@code null} if no better fallback exists.
     *
     * @param o
     *            the argument to adapt
     *
     * @return the result of the adaptation of the given argument, or the
     *         fallback if the argument is {@code null} or could not be adapted;
     *         {@code null} may be returned if the fallback strategy returns it
     */
    default T surrogate(Object o) {
        final T result = nullable(o);
        if (result != null) {
            return result;
        }

        // Fallback should be consistent!
        final T fallback = fallback().get();
        assert ((fallback == null) || (nullable(fallback) != null));
        return fallback;
    }

    // Interoperability support

    /**
     * Returns a {@link Function} instance invoking the {@link #adapt(Object)}
     * method of this instance.
     *
     * <p>
     * This interface does not intentionally inherit from the {@link Function},
     * although it is close enough, in order to prevent overloading with other
     * methods, like {@link Function#andThen(Function)}, which do not fit very
     * well into the purpose of this interface. Nevertheless, to support easy
     * interoperability, this method provides a bridge that allows chaining
     * like: {@code instance.function().andThen(CustomHandler::handle)}.
     *
     * @return a function invoking {@link #adapt(Object)} of this instance
     */
    default Function<Object, AdaptationResult<T>> function() {
        return this::adapt;
    }
}
