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
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.yetamine.lang.formatting.ToString;
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
    @Override
    public final Map<?, ?> introspect() {
        final Map<Object, Object> result = new LinkedHashMap<>();
        introspect(result);
        return result;
    }

    /**
     * @see net.yetamine.sova.AdaptationStrategy#nullable(java.lang.Object)
     */
    @Override
    public final T nullable(Object o) {
        return Symbol.super.nullable(o);
    }

    /**
     * @see net.yetamine.sova.AdaptationStrategy#optional(java.lang.Object)
     */
    @Override
    public final Optional<T> optional(Object o) {
        return Symbol.super.optional(o);
    }

    /**
     * @see net.yetamine.sova.AdaptationStrategy#surrogate(java.lang.Object)
     */
    @Override
    public final T surrogate(Object o) {
        return Symbol.super.surrogate(o);
    }

    /**
     * @see net.yetamine.sova.AdaptationStrategy#function()
     */
    @Override
    public final Function<Object, AdaptationResult<T>> function() {
        return Symbol.super.function();
    }

    // Generic access methods

    /**
     * @see net.yetamine.sova.Mappable#pull(java.util.function.Function)
     */
    @Override
    public <R> R pull(Function<? super Symbol<T>, ? extends R> source) {
        return Symbol.super.pull(source);
    }

    /**
     * @see net.yetamine.sova.symbols.AbstractSymbol#push(java.util.function.BiConsumer,
     *      java.lang.Object)
     */
    @Override
    public final void push(BiConsumer<? super Symbol<T>, ? super T> consumer, T value) {
        Symbol.super.push(consumer, value);
    }

    /**
     * @see net.yetamine.sova.Mappable#get(java.util.function.Function)
     */
    @Override
    public final T get(Function<? super Symbol<T>, ?> source) {
        return Symbol.super.get(source);
    }

    /**
     * @see net.yetamine.sova.Mappable#use(java.util.function.Function)
     */
    @Override
    public final T use(Function<? super Symbol<T>, ?> source) {
        return Symbol.super.use(source);
    }

    /**
     * @see net.yetamine.sova.Mappable#find(java.util.function.Function)
     */
    @Override
    public final Optional<T> find(Function<? super Symbol<T>, ?> source) {
        return Symbol.super.find(source);
    }

    /**
     * @see net.yetamine.sova.Mappable#yield(java.util.function.Function)
     */
    @Override
    public final AdaptationResult<T> yield(Function<? super Symbol<T>, ?> source) {
        return Symbol.super.yield(source);
    }

    /**
     * @see net.yetamine.sova.Mappable#put(java.util.function.BiConsumer,
     *      java.lang.Object)
     */
    @Override
    public final void put(BiConsumer<? super Symbol<T>, ? super T> consumer, Object value) {
        Symbol.super.put(consumer, value);
    }

    // Map-based access methods

    /**
     * @see net.yetamine.sova.Mappable#pull(java.util.Map)
     */
    @Override
    public <R> R pull(Map<?, ? extends R> source) {
        return Symbol.super.pull(source);
    }

    /**
     * @see net.yetamine.sova.Mappable#push(java.util.Map, java.lang.Object)
     */
    @Override
    public final Object push(Map<? super Symbol<T>, ? super T> consumer, T value) {
        return Symbol.super.push(consumer, value);
    }

    /**
     * @see net.yetamine.sova.Mappable#get(java.util.Map)
     */
    @Override
    public final T get(Map<?, ?> source) {
        return Symbol.super.get(source);
    }

    /**
     * @see net.yetamine.sova.Mappable#use(java.util.Map)
     */
    @Override
    public final T use(Map<?, ?> source) {
        return Symbol.super.use(source);
    }

    /**
     * @see net.yetamine.sova.Mappable#find(java.util.Map)
     */
    @Override
    public final Optional<T> find(Map<?, ?> source) {
        return Symbol.super.find(source);
    }

    /**
     * @see net.yetamine.sova.Mappable#yield(java.util.Map)
     */
    @Override
    public final AdaptationResult<T> yield(Map<?, ?> source) {
        return Symbol.super.yield(source);
    }

    /**
     * @see net.yetamine.sova.Mappable#put(java.util.Map, java.lang.Object)
     */
    @Override
    public final T put(Map<? super Symbol<T>, ? super T> consumer, Object value) {
        return Symbol.super.put(consumer, value);
    }

    /**
     * @see net.yetamine.sova.Mappable#supply(java.util.Map,
     *      java.util.function.Supplier)
     */
    @Override
    public T supply(Map<? super Symbol<T>, ? super T> map, Supplier<?> surrogate) {
        return Symbol.super.supply(map, surrogate);
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
}
