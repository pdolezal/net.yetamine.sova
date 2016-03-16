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
import java.util.function.Supplier;

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
        return AdaptationResult.of(o, derive(o), this);
    }

    // Convenient application methods

    /**
     * Returns the result of the adaptation of the given argument; this method
     * is a shortcut for {@code adaptation().apply(o)}.
     *
     * @param o
     *            the argument to adapt
     *
     * @return the result of the adaptation, or {@code null} if the argument is
     *         {@code null} or could not be adapted
     */
    default T derive(Object o) {
        return adaptation().apply(o);
    }

    /**
     * Returns the default value if the argument is {@code null}, otherwise the
     * argument.
     *
     * @param o
     *            the object to check
     * @param f
     *            the fallback to use. It must not be {@code null}.
     *
     * @return the given object, or the default value
     */
    default T fallback(T o, Supplier<? extends T> f) {
        return (o != null) ? o : f.get();
    }

    /**
     * Returns the default value if the argument is {@code null}, otherwise the
     * argument.
     *
     * @param o
     *            the object to check
     *
     * @return the given object, or the default value
     */
    default T fallback(T o) {
        return fallback(o, fallback());
    }

    /**
     * Returns the result of the adaptation of the given argument or the default
     * value; this method is a shortcut for {@code fallback(apply(o))}.
     *
     * @param o
     *            the argument to adapt
     *
     * @return the result of the adaptation or the default value; {@code null}
     *         is returned if neither the adaptation nor the fallback provided a
     *         valid object
     */
    default T recover(Object o) {
        return fallback(derive(o));
    }

    /**
     * Returns an {@link Optional} instance representing the result of the
     * adaptation of the given argument or the fallback; this method is a
     * shortcut for {@code Optional.ofNullable(recover(o))}.
     *
     * @param o
     *            the argument to adapt
     *
     * @return the result of the adaptation or the fallback
     */
    default Optional<T> resolve(Object o) {
        return Optional.ofNullable(recover(o));
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
