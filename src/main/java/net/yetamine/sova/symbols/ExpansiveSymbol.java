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
import java.util.StringJoiner;

import net.yetamine.lang.introspective.Introspection;
import net.yetamine.sova.Symbol;

/**
 * An abstract base class for implementing the {@link Symbol} interface.
 *
 * <p>
 * This class extends {@link AbstractSymbol} with introspection feature and
 * provides an improved {@link #toString} implementation, using the
 * introspection data to render the result.
 *
 * @param <T>
 *            the type of resulting values
 */
public abstract class ExpansiveSymbol<T> extends AbstractSymbol<T> implements Introspection {

    /** Cached {@link #toString()} result. */
    private String toString;

    /**
     * Prepares a new instance.
     */
    protected ExpansiveSymbol() {
        // Default constructor
    }

    /**
     * The implementation uses {@link #render()} to render the actual result and
     * stores the result, so that it does not have to be rendered again. When no
     * result is provided, {@link Object#toString()} is used instead.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        final String current = toString;
        if (current != null) { // There is something cached
            return current;
        }

        String update = render();
        if (update == null) { // Use the fallback when the custom rendering fails
            update = super.toString();
        }

        toString = update;
        return update;
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
     * Adds implementation-specific features to the map of results provided by
     * the {@link #introspect()} method.
     *
     * <p>
     * Implementations are supposed to invoke the inherited implementations and
     * just add their own data. But since the whole map is available, inherited
     * classes may even remove elements that are not correct for them.
     *
     * @param result
     *            the result to present. It must not be {@code null}.
     */
    protected void introspect(Map<Object, Object> result) {
        // Do nothing
    }

    /**
     * Renders the result for {@link #toString()}.
     *
     * <p>
     * The default implementation uses {@link #introspect()} to retrieve the
     * attributes that are rendered as the part of the result. Inherited classes
     * may override this method to provide different implementation, however, it
     * is recommended to mimic the result of the default implementation that has
     * the form of <tt>symbol[<i>details</i>]</tt> where <i>details</i> provide
     * the important information about the instance.
     *
     * @return the string representation which {@link #toString()} should
     *         return, or {@code null} if {@link Object#toString()} shall be
     *         used to render the result instead
     */
    protected String render() {
        final StringJoiner result = new StringJoiner(", ", "symbol[", "]").setEmptyValue(super.toString());
        introspect().forEach((name, value) -> result.add(new StringBuilder(name.toString()).append('=').append(value)));
        return result.toString();
    }
}
