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
 * An extension of the {@link Symbol} interface that links every instance to a
 * public identifier.
 *
 * <p>
 * The public identifier defines the semantics and behavior of the symbol. For
 * that purpose, the identifier must be globally unique, or unique at least in
 * the given domain or scope; then it may be used for deciding equality of two
 * symbol instances as well. Good choices of such an identifier is UID or GUID,
 * QName or URI.
 *
 * <p>
 * The identifiers must be of an (effectively) immutable type. A good idea is
 * using a suitable dedicated type rather than a plain {@link String}, but even
 * plain string identifiers are acceptable as long as the scope of use confines
 * them.
 *
 * @param <I>
 *            the type of the identifier
 * @param <V>
 *            the type of resulting values
 */
public interface PublicSymbol<I, V> extends Symbol<V> {

    /**
     * Compares the specified symbols for equality.
     *
     * <p>
     * An instance is considered equal to this instance if the instance is an
     * instance of {@link PublicSymbol} and has equal {@link #identifier()}. All
     * Implementations must override this method to ensure th contract; they are
     * supposed to delegate to the {@link #equals(PublicSymbol, Object)} method.
     *
     * @param o
     *            object to be compared for equality with this instance
     *
     * @return {@code true} if the object is equal to this instance
     *
     * @see net.yetamine.sova.core.Symbol#equals(java.lang.Object)
     */
    boolean equals(Object o);

    /**
     * Returns the hash code value for this symbol.
     *
     * <p>
     * The result must be just the hash code of the {@link #identifier()} in
     * order to keep consistency with the {@link #equals(Object)} method. The
     * {@link #hashCode(PublicSymbol)} method provides this functionality and
     * implementations, which must override this method always to satisfy the
     * contract, are suppposed to delegate to it.
     *
     * @return the hash code value for this symbol
     *
     * @see Object#equals(Object)
     */
    int hashCode();

    /**
     * Returns the identifier of this instance.
     *
     * @return the identifier of this instance, never {@code null}
     */
    I identifier();

    /**
     * Compares an instance to an object on equality.
     *
     * <p>
     * This method actually implements the {@link #equals(Object)} method
     * according to its contract and therefore should be used by implementations
     * of this interface for overriding their {@code equals()} method which must
     * be done anyway.
     *
     * @param that
     *            the instance acting as {@code this}. It must not be
     *            {@code null}.
     * @param obj
     *            the argument to compare with
     *
     * @return {@code true} if the argument is an instance of this interface and
     *         has an equal identifier
     */
    static boolean equals(PublicSymbol<?, ?> that, Object obj) {
        return (obj instanceof PublicSymbol<?, ?>) && that.identifier().equals(((PublicSymbol<?, ?>) obj).identifier());
    }

    /**
     * Returns the hash code of an instance of this interface according to the
     * contract of the {@link #hashCode()} method.
     *
     * @param that
     *            the instance acting as {@code this}. It must not be
     *            {@code null}.
     *
     * @return the hash code of the given instance
     *
     * @see java.lang.Object#hashCode()
     */
    static int hashCode(PublicSymbol<?, ?> that) {
        return that.identifier().hashCode();
    }
}
