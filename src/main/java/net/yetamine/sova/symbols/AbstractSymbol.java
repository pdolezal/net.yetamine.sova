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

package net.yetamine.sova.symbols;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.yetamine.lang.introspective.Introspection;
import net.yetamine.sova.AdaptationResult;
import net.yetamine.sova.Symbol;

/**
 * An abstract base class for implementing the {@link Symbol} interface.
 *
 * <p>
 * This class makes most of the inherited method implementations final in order
 * to increase the robustness of the implementation, which makes it a good base
 * for other extensible classes. Still, since {@link Symbol} is an interface,
 * making mixins or cross-hierarchy extensions is possible.
 *
 * @param <T>
 *            the type of resulting values
 */
public abstract class AbstractSymbol<T> implements Introspection, Symbol<T> {

    /**
     * Prepares a new instance.
     */
    protected AbstractSymbol() {
        // Default constructor
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        final StringJoiner result = new StringJoiner(", ", "symbol[", "]").setEmptyValue(super.toString());

        introspect().forEach((name, value) -> {
            if (name instanceof ToString) {
                result.add(new StringBuilder(name.toString()).append('=').append(value));
            }
        });

        return result.toString();
    }

    /**
     * @see net.yetamine.lang.introspective.Introspection#introspect()
     */
    public final Map<?, ?> introspect() {
        final Map<Object, Object> result = new LinkedHashMap<>();
        introspect(result);
        return result;
    }

    /**
     * @see net.yetamine.sova.AdaptationStrategy#derive(java.lang.Object)
     */
    public final T derive(Object o) {
        return Symbol.super.derive(o);
    }

    /**
     * @see net.yetamine.sova.AdaptationStrategy#recover(java.lang.Object)
     */
    public final T recover(Object o) {
        return Symbol.super.recover(o);
    }

    /**
     * @see net.yetamine.sova.AdaptationStrategy#resolve(java.lang.Object)
     */
    public final Optional<T> resolve(Object o) {
        return Symbol.super.resolve(o);
    }

    /**
     * @see net.yetamine.sova.AdaptationStrategy#fallback(java.lang.Object,
     *      java.util.function.Supplier)
     */
    public final T fallback(T o, Supplier<? extends T> f) {
        return Symbol.super.fallback(o, f);
    }

    /**
     * @see net.yetamine.sova.AdaptationStrategy#fallback(java.lang.Object)
     */
    public final T fallback(T o) {
        return Symbol.super.fallback(o);
    }

    /**
     * @see net.yetamine.sova.AdaptationStrategy#function()
     */
    public final Function<Object, AdaptationResult<T>> function() {
        return Symbol.super.function();
    }

    // Generic access methods

    /**
     * @see net.yetamine.sova.Mappable#get(java.util.function.Function)
     */
    public final T get(Function<? super Symbol<T>, ?> source) {
        return Symbol.super.get(source);
    }

    /**
     * @see net.yetamine.sova.Mappable#use(java.util.function.Function)
     */
    public final T use(Function<? super Symbol<T>, ?> source) {
        return Symbol.super.use(source);
    }

    /**
     * @see net.yetamine.sova.Mappable#find(java.util.function.Function)
     */
    public final Optional<T> find(Function<? super Symbol<T>, ?> source) {
        return Symbol.super.find(source);
    }

    /**
     * @see net.yetamine.sova.Mappable#yield(java.util.function.Function)
     */
    public final AdaptationResult<T> yield(Function<? super Symbol<T>, ?> source) {
        return Symbol.super.yield(source);
    }

    /**
     * @see net.yetamine.sova.Mappable#put(java.util.function.BiConsumer,
     *      java.lang.Object)
     */
    public final void put(BiConsumer<? super Symbol<T>, ? super T> consumer, T value) {
        consumer.accept(this, value);
    }

    /**
     * @see net.yetamine.sova.Mappable#set(java.util.function.BiConsumer,
     *      java.lang.Object)
     */
    public final void set(BiConsumer<? super Symbol<T>, ? super T> consumer, Object value) {
        Symbol.super.set(consumer, value);
    }

    // Map-based access methods

    /**
     * @see net.yetamine.sova.Mappable#get(java.util.Map)
     */
    public final T get(Map<?, ?> source) {
        return Symbol.super.get(source);
    }

    /**
     * @see net.yetamine.sova.Mappable#use(java.util.Map)
     */
    public final T use(Map<?, ?> source) {
        return Symbol.super.use(source);
    }

    /**
     * @see net.yetamine.sova.Mappable#find(java.util.Map)
     */
    public final Optional<T> find(Map<?, ?> source) {
        return Symbol.super.find(source);
    }

    /**
     * @see net.yetamine.sova.Mappable#yield(java.util.Map)
     */
    public final AdaptationResult<T> yield(Map<?, ?> source) {
        return Symbol.super.yield(source);
    }

    /**
     * @see net.yetamine.sova.Mappable#put(java.util.Map,
     *      java.lang.Object)
     */
    public final Object put(Map<? super Symbol<T>, ? super T> consumer, T value) {
        return Symbol.super.put(consumer, value);
    }

    /**
     * @see net.yetamine.sova.Mappable#set(java.util.Map,
     *      java.lang.Object)
     */
    public final Object set(Map<? super Symbol<T>, ? super T> consumer, Object value) {
        return Symbol.super.set(consumer, value);
    }

    // Other stuff

    /**
     * Adds implementation-specific features to the set of results provided by
     * the {@link #introspect()} method.
     *
     * <p>
     * Implementations are supposed to invoke the inherited implementations and
     * just add their own data. But since the whole set is available, inherited
     * classes may even remove elements that are not correct for them.
     *
     * <p>
     * Items to be inserted in {@link #toString()} representation, must have the
     * their key made with {@link #toString(String)} method.
     *
     * @param result
     *            the resulting set. It must not be {@code null}.
     */
    protected void introspect(Map<Object, Object> result) {
        // Do nothing
    }

    /**
     * Makes an object that {@link #toString()} recognizes as the key that shall
     * appear in the string representation of this instance.
     *
     * @param identifier
     *            the identifier to present. It must not be {@code null}.
     *
     * @return an object for {@link #toString()} representation identifier
     */
    protected final Object toString(String identifier) {
        return new ToString(identifier);
    }

    /**
     * A helper for marking {@link Introspection} items for {@link #toString()}
     * representation.
     */
    private static final class ToString {

        /** Identifier to display. */
        private final String identifier;

        /**
         * Creates a new instance.
         *
         * @param id
         *            the identifier to present. It must not be {@code null}.
         */
        public ToString(String id) {
            identifier = Objects.requireNonNull(id);
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return identifier;
        }
    }
}
