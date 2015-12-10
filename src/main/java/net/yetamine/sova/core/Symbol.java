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

/**
 * A smart type-safe key for using with heterogenous containers and data
 * handlers.
 *
 * <p>
 * A symbol encapsulates the type constraint of the value that the symbol shall
 * refer to and provides an adaptation strategy for adapting any suitable input
 * object to the desired type.
 *
 * <p>
 * The conditional adaptation capability of a symbol is a powerful feature that
 * allows safe access to any values stored in a heterogenous container as long
 * as the values are associated with the correct symbols. Since symbols should
 * be used as keys in map-like containers, all implementations must be (at
 * least) effectively immutable.
 *
 * @param <T>
 *            the type of resulting values
 */
public interface Symbol<T> extends Mappable<Symbol<T>, T> {

    // Equality definition

    /**
     * Compares the specified symbols for equality.
     *
     * <p>
     * Implementations are required to define this method in order to provide
     * correct and consistent behavior. In general, two symbols should be equal
     * only if they have the same semantics and provide the same results.
     *
     * <p>
     * The conditions may be bound to a well-known published identifier linked
     * to a symbol instance for a particular symbol type, but using the default
     * implementation that compares just the identity of instances could be yet
     * another well-working option.
     *
     * @param o
     *            object to be compared for equality with this instance
     *
     * @return {@code true} if the object is equal to this instance
     *
     * @see Object#equals(Object)
     */
    boolean equals(Object o);

    /**
     * Returns the hash code value for this symbol.
     *
     * <p>
     * Implementations must override this method in order to make it consistent
     * with {@link #equals(Object)} if they override {@code equals()} as well.
     *
     * @return the hash code value for this symbol
     *
     * @see Object#equals(Object)
     */
    int hashCode();

    // Support for mapping

    /**
     * @see net.yetamine.sova.core.Mappable#mapping()
     */
    default Symbol<T> mapping() {
        return this;
    }
}
