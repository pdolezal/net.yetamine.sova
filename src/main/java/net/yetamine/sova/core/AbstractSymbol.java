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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * An abstract base class for implementing the {@link Symbol} interface.
 *
 * <p>
 * This class makes the inherited method implementations final in order to
 * increase the robustness of the implementation, which makes it a good base for
 * other extensible classes. Still, since {@link Symbol} is an interface, making
 * mixins or cross-hierarchy extensions is possible.
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
    public String toString() {
        final StringJoiner result = new StringJoiner(", ", "symbol[", "]").setEmptyValue(super.toString());
        introspect().forEach((name, value) -> result.add(new StringBuilder(name).append('=').append(value)));
        return result.toString();
    }

    /**
     * @see net.yetamine.sova.core.Introspection#introspect()
     */
    public final Map<String, ?> introspect() {
        final Map<String, Object> result = new LinkedHashMap<>();
        introspect(result);
        return result;
    }

    /**
     * @see net.yetamine.sova.core.AdaptationStrategy#fallback()
     */
    public final T fallback() {
        return Symbol.super.fallback();
    }

    // Generic access methods

    /**
     * @see net.yetamine.sova.core.Symbol#get(java.util.function.Function)
     */
    public final T get(Function<? super Symbol<?>, ?> source) {
        return Symbol.super.get(source);
    }

    /**
     * @see net.yetamine.sova.core.Symbol#gain(java.util.function.Function)
     */
    public final T gain(Function<? super Symbol<?>, ?> source) {
        return Symbol.super.gain(source);
    }

    /**
     * @see net.yetamine.sova.core.Symbol#seek(java.util.function.Function)
     */
    public final T seek(Function<? super Symbol<?>, ?> source) {
        return Symbol.super.seek(source);
    }

    /**
     * @see net.yetamine.sova.core.Symbol#find(java.util.function.Function)
     */
    public final Optional<T> find(Function<? super Symbol<?>, ?> source) {
        return Symbol.super.find(source);
    }

    /**
     * @see net.yetamine.sova.core.Symbol#put(java.util.function.BiConsumer,
     *      java.lang.Object)
     */
    public final void put(BiConsumer<? super Symbol<T>, ? super T> consumer, T value) {
        consumer.accept(this, value);
    }

    /**
     * @see net.yetamine.sova.core.Symbol#set(java.util.function.BiConsumer,
     *      java.lang.Object)
     */
    public final void set(BiConsumer<? super Symbol<T>, ? super T> consumer, Object value) {
        Symbol.super.set(consumer, value);
    }

    // Map-based access methods

    /**
     * @see net.yetamine.sova.core.Symbol#get(java.util.Map)
     */
    public final T get(Map<?, ?> source) {
        return Symbol.super.get(source);
    }

    /**
     * @see net.yetamine.sova.core.Symbol#gain(java.util.Map)
     */
    public final T gain(Map<?, ?> source) {
        return Symbol.super.gain(source);
    }

    /**
     * @see net.yetamine.sova.core.Symbol#seek(java.util.Map)
     */
    public final T seek(Map<?, ?> source) {
        return Symbol.super.seek(source);
    }

    /**
     * @see net.yetamine.sova.core.Symbol#find(java.util.Map)
     */
    public final Optional<T> find(Map<?, ?> source) {
        return Symbol.super.find(source);
    }

    /**
     * @see net.yetamine.sova.core.Symbol#put(java.util.Map, java.lang.Object)
     */
    public final Object put(Map<? super Symbol<T>, ? super T> consumer, T value) {
        return Symbol.super.put(consumer, value);
    }

    /**
     * @see net.yetamine.sova.core.Symbol#set(java.util.Map, java.lang.Object)
     */
    public final Object set(Map<? super Symbol<T>, ? super T> consumer, Object value) {
        return Symbol.super.set(consumer, value);
    }

    /**
     * Adds implementation-specific features to the set of results provided by
     * the {@link #introspect()} method.
     *
     * <p>
     * Implementations are supposed to invoke the inherited implementations and
     * just add their own data. But since the whole set is available, inherited
     * classes may even remove elements that are not
     *
     * @param result
     *            the resulting set. It must not be {@code null}.
     */
    protected void introspect(Map<String, Object> result) {
        // Do nothing
    }
}
