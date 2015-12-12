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
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a result of an {@link AdaptationStrategy}.
 *
 * <p>
 * This interface resembles the {@link Optional} class and share its purpose in
 * a way: representing a result of an operation which may not be present or may
 * need some more processing and providing a fluent API to make the processing
 * more comfortable. The main difference is that {@code Optional} is a final
 * class which prevents any further extensions or mixing with other types.
 *
 * @param <T>
 *            the type of resulting values
 */
public interface AdaptationResult<T> extends Supplier<T> {

    /**
     * Returns the operation that produced the original result.
     *
     * @return the operation that produced the original result
     */
    AdaptationStrategy<T> operation();

    /**
     * Returns the original argument of the operation, i.e., the argument to
     * adapt.
     *
     * @return the original argument of the operation
     */
    Object argument();

    /**
     * Returns the result represented by this instance.
     *
     * @return the result represented by this instance
     *
     * @see java.util.function.Supplier#get()
     */
    T get();

    /**
     * Returns the result of {@link #get()} if not {@code null}, otherwise it
     * returns {@code null} if {@link #argument()} is {@code null}; if neither
     * case applies, it maps the (non-null) argument of the operation with the
     * given function to the exception that shall be thrown.
     *
     * @param <X>
     *            the type of the exception to throw if the adaptation fails
     * @param e
     *            the function that is invoked when the adaptation fails to
     *            provide the exception to be thrown; the function gets the
     *            argument of this method
     *
     * @return the result of {@link #get()}, which may be {@code null} if the
     *         {@link #argument()} is {@code null} as well
     *
     * @throws X
     *             if {@code null} would be returned for non-{@code null}
     *             argument
     */
    default <X extends Throwable> T request(Function<Object, ? extends X> e) throws X {
        final T result = get();
        if (result != null) {
            return result;
        }

        final Object argument = argument();
        if (argument == null) {
            return null;
        }

        throw e.apply(argument);
    }

    /**
     * Returns the result of {@link #get()} if not {@code null}, otherwise it
     * returns {@code null} if {@link #argument()} is {@code null}; if neither
     * case applies, it throws an exception.
     *
     * @return the result of {@link #get()}, which may be {@code null} if the
     *         {@link #argument()} is {@code null} as well
     *
     * @throws AdaptationException
     *             if {@code null} would be returned for non-{@code null}
     *             argument
     */
    default T request() {
        return request(AdaptationException::new);
    }

    /**
     * Returns the result of {@link #get()} if not {@code null}, otherwise maps
     * the {@link #argument()} of the operation using the given function to the
     * exception that shall be thrown.
     *
     * @param <X>
     *            the type of the exception to throw if the adaptation fails
     * @param e
     *            the function that is invoked when the adaptation fails to
     *            provide the exception to be thrown; the function gets the
     *            argument of this method
     *
     * @return the result of {@link #get()} that is not {@code null}
     *
     * @throws X
     *             if can't return a valid object
     */
    default <X extends Throwable> T require(Function<Object, ? extends X> e) throws X {
        final T result = get();
        if (result != null) {
            return result;
        }

        throw e.apply(argument());
    }

    /**
     * Returns the result of {@link #get()} if not {@code null}, otherwise
     * throws an exception.
     *
     * @return the result of {@link #get()} that is not {@code null}
     *
     * @throws AdaptationException
     *             if can't return a valid object
     */
    default T require() {
        return require(AdaptationException::new);
    }

    /**
     * Returns the result of {@link #get()} as an {@link Optional}.
     *
     * @return the result of {@link #get()} as an {@link Optional}
     */
    default Optional<T> resolve() {
        return Optional.ofNullable(get());
    }

    /**
     * Returns an instance which represents either the same result as this
     * instance, or the default value if the result is {@code null}.
     *
     * <p>
     * This method allows patterns like:
     *
     * <pre>
     * adapted = adaptationStrategy.adapt(adaptee).fallback().require();
     * </pre>
     *
     * The previous code provides the same result as:
     *
     * <pre>
     * adapted = Optional.ofNullable(adaptationStrategy.recover(adaptee)).orElseThrow(AdaptationException::new);
     * </pre>
     *
     * The former code is considerably more concise and clearer.
     *
     * @return an instance representing the result or the default
     */
    default AdaptationResult<T> fallback() {
        final AdaptationStrategy<T> operation = operation();
        return AdaptationResult.of(operation, argument(), operation.fallback(get()));
    }

    /**
     * Returns a result of a function that is applied on this instance.
     *
     * @param <V>
     *            the return type of the returned function
     * @param f
     *            the function to apply. It must not be {@code null}.
     *
     * @return the result of the function
     */
    default <V> V map(Function<? super AdaptationResult<T>, ? extends V> f) {
        return f.apply(this);
    }

    /**
     * Returns an instance that represents the specified value.
     *
     * @param <T>
     *            the type of resulting values
     * @param operation
     *            the related operation. It must not be {@code null}.
     * @param argument
     *            the argument of the operation
     * @param value
     *            the value to represent
     *
     * @return an instance that represents the specified value
     */
    static <T> AdaptationResult<T> of(AdaptationStrategy<T> operation, Object argument, T value) {
        Objects.requireNonNull(operation);

        return new AdaptationResult<T>() {

            /**
             * @see net.yetamine.sova.core.AdaptationResult#operation()
             */
            public AdaptationStrategy<T> operation() {
                return operation;
            }

            /**
             * @see net.yetamine.sova.core.AdaptationResult#argument()
             */
            public Object argument() {
                return argument;
            }

            /**
             * @see net.yetamine.sova.core.AdaptationResult#get()
             */
            public T get() {
                return value;
            }
        };
    }
}
