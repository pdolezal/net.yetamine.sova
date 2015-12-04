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

package net.yetamine.sova.collections;

import java.util.Map;
import java.util.Optional;

import net.yetamine.sova.core.Symbol;

/**
 * An unmodifiable symbol-based view of a {@link Map}.
 *
 * <p>
 * The interface provides a methods for looking up values using symbols as the
 * keys that mirror similar methods in the {@link Map} interface. It offers a
 * {@link Map} view on the content as well to improve the interoperability with
 * the Java Collections Framework.
 *
 * <p>
 * This structure does not support {@code null} values, although implementations
 * may tolerate them and allow storing mappings to {@code null} values, which is
 * sometimes useful for the interoperability via the {@link #map()} view.
 *
 * <p>
 * The interface is designed as read-only; however, changing the content might
 * be possible anyway: an implementation may offer yet a mutable interface for
 * the content, or an implementation may allow removing entries via the view.
 */
public interface SymbolMapping {

    /**
     * Returns a map-like view on the container. The view should be considered
     * unmodifiable, nevertheless an implementation may allow removing entries.
     *
     * @return a map-like view on the container
     */
    Map<?, ?> map();

    /**
     * Compares the specified object with this instance for equality and returns
     * {@code true} iff the object provides equal {@link SymbolMapping#map()} view too.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    boolean equals(Object obj);

    /**
     * Returns the hash code of the {@link #map()} view.
     *
     * @see java.lang.Object#hashCode()
     */
    int hashCode();

    /**
     * Returns the size of the {@link #map()} view.
     *
     * @return the size of the {@link #map()} view
     */
    default int size() {
        return map().size();
    }

    /**
     * Returns {@code true} iff {@link #size()} is zero.
     *
     * @return {@code true} iff {@link #size()} is zero
     */
    default boolean isEmpty() {
        return map().isEmpty();
    }

    /**
     * Returns the value associated with the given symbol, or {@code null} if no
     * such value exists.
     *
     * @param <T>
     *            the type of the result
     * @param symbol
     *            the symbol whose associated value is to be returned
     *
     * @return the value associated with the given symbol, or {@code null} if no
     *         such value exists
     */
    default <T> T get(Symbol<T> symbol) {
        return symbol.get(map());
    }

    /**
     * Returns the value associated with the given symbol, or the fallback of
     * the symbol if no such value exists.
     *
     * @param <T>
     *            the type of the result
     * @param symbol
     *            the symbol whose associated value is to be returned
     *
     * @return the value associated with the given symbol, or {@code null} if no
     *         such value exists
     */
    default <T> T gain(Symbol<T> symbol) {
        return symbol.gain(map());
    }

    /**
     * Returns the value associated with the given symbol, or the fallback of
     * the symbol if the value could be adapted.
     *
     * @param <T>
     *            the type of the result
     * @param symbol
     *            the symbol whose associated value is to be returned
     *
     * @return the value associated with the given symbol, or {@code null} if
     *         the value could not be adapted
     */
    default <T> T seek(Symbol<T> symbol) {
        return symbol.seek(map());
    }

    /**
     * Returns an {@link Optional} containing the value associated with the
     * given symbol, or an empty container if no such value exists.
     *
     * @param <T>
     *            the type of the result
     * @param symbol
     *            the symbol whose associated value is to be returned
     *
     * @return an {@link Optional} containing the value associated with the
     *         given symbol, or an empty container if no such value exists
     */
    default <T> Optional<T> find(Symbol<T> symbol) {
        return symbol.find(map());
    }
}
