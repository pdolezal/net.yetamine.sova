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

import java.util.Objects;

import net.yetamine.sova.core.AdaptationStrategy;
import net.yetamine.sova.core.DelegatingSymbol;
import net.yetamine.sova.core.Downcasting;
import net.yetamine.sova.core.PublicSymbol;

/**
 * A symbol implementation which is coupled with a published identifier that is
 * unique in the given domain (or preferably globally unique, like GUID) and
 * that can be recognized according to the identifier.
 *
 * <p>
 * Relying on the identifier link is reflected in the equality of two instances
 * of this class: they are equal if and only if they have equal identifiers. No
 * other class information is relevant, hence it depends very much on assigning
 * the identifiers and managing them.
 *
 * <p>
 * The identifiers must be of an (effectively) immutable type. Good choices of
 * an identifier are, e.g., UID, QName or URI, a good idea is to use a suitable
 * dedicated type rather than a {@link String}, but even string identifiers are
 * acceptable as long as their uniqueness in the scope of use can be guaranteed.
 *
 * <p>
 * There is usually no need to inherit from this class, but this option is left
 * open intentionally when an implementation wishes to provide some additional
 * features or attributes, or just alter the debugging support.
 *
 * @param <I>
 *            the type of the identifier
 * @param <V>
 *            the type of resulting values
 */
public class ExternalSymbol<I, V> extends DelegatingSymbol<V> implements PublicSymbol<I, V> {

    /** Identifier of this instance. */
    private final I identifier;

    /**
     * Creates a new instance.
     *
     * @param instanceIdentifier
     *            the identifier of this instance. It must not be {@code null}.
     * @param adaptation
     *            the adaptation implementation. It must not be {@code null}.
     */
    public ExternalSymbol(I instanceIdentifier, AdaptationStrategy<V> adaptation) {
        super(adaptation);
        identifier = Objects.requireNonNull(instanceIdentifier);
    }

    /**
     * Creates a new instance using {@link Downcasting#to(Class)}.
     *
     * @param instanceIdentifier
     *            the identifier of the new instance. It must not be
     *            {@code null}.
     * @param type
     *            the desired type of resulting values. It must not be
     *            {@code null}.
     */
    public ExternalSymbol(I instanceIdentifier, Class<V> type) {
        this(instanceIdentifier, Downcasting.to(type));
    }

    /**
     * The default implementation returns the information consisting of the
     * {@link #identifier()} as a {@link String} in the way which is consistent
     * with the representation of other symbol implementations in this package.
     * This method can be overridden by inherited classes arbitrarily, but it
     * should be always considered as an informative source.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("symbol[id=%s]", identifier);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(Object obj) {
        return PublicSymbol.equals(this, obj);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        return PublicSymbol.hashCode(this);
    }

    /**
     * @return the identifier of this instance
     */
    public final I identifier() {
        return identifier;
    }
}