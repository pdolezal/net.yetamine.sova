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
import java.util.function.Consumer;
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
     * Returns the original argument of the operation, i.e., the argument to
     * adapt.
     *
     * @return the original argument of the operation
     */
    Object argument();

    // Core result-access methods

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

    // Optional-like support

    /**
     * Returns {@code true} iff {@link #get()} returns a non-{@code null} value.
     *
     * @return {@code true} iff {@link #get()} returns a non-{@code null} value
     */
    default boolean isPresent() {
        return (get() != null);
    }

    /**
     * Invokes an consumer to accept the content if {@link #isPresent()}.
     *
     * @param consumer
     *            the consumer to invoke. It must not be {@code null}.
     */
    default void ifPresent(Consumer<? super T> consumer) {
        final T value = get();
        if (value != null) {
            consumer.accept(value);
        }
    }

    // Fallback chaining support

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
    AdaptationResult<T> fallback();

    // Factory methods

    /**
     * Returns an instance that represents the specified value.
     *
     * @param <T>
     *            the type of resulting values
     * @param argument
     *            the argument of the operation
     * @param value
     *            the value to represent
     * @param operation
     *            the operation that shall supply the fallback. It must not be
     *            {@code null}.
     *
     * @return an instance that represents the specified value
     */
    static <T> AdaptationResult<T> of(Object argument, T value, AdaptationStrategy<T> operation) {
        final Supplier<? extends T> fallback = operation.fallback();
        return (fallback != null) ? of(argument, value, fallback) : of(argument, value);
    }

    /**
     * Returns an instance that represents the specified value and provides a
     * fallback using the given supplier.
     *
     * @param <T>
     *            the type of resulting values
     * @param argument
     *            the argument of the operation
     * @param value
     *            the value to represent
     * @param fallback
     *            the fallback supplier. It must not be {@code null}.
     *
     * @return an instance that represents the specified value
     */
    static <T> AdaptationResult<T> of(Object argument, T value, Supplier<? extends T> fallback) {
        return new DefaultAdaptationResult<>(argument, value, fallback);
    }

    /**
     * Returns an instance that represents the specified value without any
     * fallback.
     *
     * @param argument
     *            the argument of the operation
     * @param value
     *            the value to represent
     *
     * @param <T>
     *            the type of resulting values
     *
     * @return an instance that represents the specified value
     */
    static <T> AdaptationResult<T> of(Object argument, T value) {
        return new ImmediateAdaptationResult<>(argument, value);
    }
}

/**
 * The base class for default implementations of {@link AdaptationResult}.
 *
 * @param <T>
 *            the type of the represented value
 */
abstract class AbstractAdaptationResult<T> implements AdaptationResult<T> {

    /** Value to represent. */
    private final T value;
    /** Argument of the operation. */
    private final Object argument;

    /**
     * Creates a new instance.
     *
     * @param arg
     *            the argument of the operation
     * @param val
     *            the value to represent
     */
    protected AbstractAdaptationResult(Object arg, T val) {
        argument = arg;
        value = val;
    }

    /**
     * @see net.yetamine.sova.AdaptationResult#argument()
     */
    public final Object argument() {
        return argument;
    }

    /**
     * @see net.yetamine.sova.AdaptationResult#get()
     */
    public final T get() {
        return value;
    }
}

/**
 * The default implementation for {@link AdaptationResult} that handles the
 * fallback case.
 *
 * @param <T>
 *            the type of the represented value
 */
final class DefaultAdaptationResult<T> extends AbstractAdaptationResult<T> {

    /** Fallback supplier. */
    private final Supplier<? extends T> fallback;

    /**
     * Creates a new instance.
     *
     * @param arg
     *            the argument of the operation
     * @param val
     *            the value to represent
     * @param supplier
     *            the fallback supplier. It must not be {@code null}.
     */
    public DefaultAdaptationResult(Object arg, T val, Supplier<? extends T> supplier) {
        super(arg, val);
        fallback = Objects.requireNonNull(supplier);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("AdaptationResult[result=%s, argument=%s, fallback=present]", get(), argument());
    }

    /**
     * @see net.yetamine.sova.AdaptationResult#fallback()
     */
    public AdaptationResult<T> fallback() {
        return new FallbackAdaptationResult<>(argument(), fallback.get());
    }
}

/**
 * The default implementation for {@link AdaptationResult} that can't handle any
 * fallback.
 *
 * @param <T>
 *            the type of the represented value
 */
final class ImmediateAdaptationResult<T> extends AbstractAdaptationResult<T> {

    /**
     * Creates a new instance.
     *
     * @param arg
     *            the argument of the operation
     * @param val
     *            the value to represent
     */
    public ImmediateAdaptationResult(Object arg, T val) {
        super(arg, val);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("AdaptationResult[result=%s, argument=%s, fallback=absent]", get(), argument());
    }

    /**
     * @see net.yetamine.sova.AdaptationResult#fallback()
     */
    public AdaptationResult<T> fallback() {
        return new FallbackAdaptationResult<>(argument(), null);
    }
}

/**
 * The default implementation for {@link AdaptationResult} that handles just the
 * fallback.
 *
 * @param <T>
 *            the type of the represented value
 */
final class FallbackAdaptationResult<T> extends AbstractAdaptationResult<T> {

    /**
     * Creates a new instance.
     *
     * @param arg
     *            the argument of the operation
     * @param val
     *            the value to represent
     */
    public FallbackAdaptationResult(Object arg, T val) {
        super(arg, val);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("AdaptationResult[result=%s, argument=%s, fallback=this]", get(), argument());
    }

    /**
     * @see net.yetamine.sova.AdaptationResult#fallback()
     */
    public AdaptationResult<T> fallback() {
        return this;
    }
}
