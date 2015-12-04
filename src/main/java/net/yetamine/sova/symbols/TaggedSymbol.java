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
import net.yetamine.sova.core.Downcasting;

/**
 * An extension of {@link InternalSymbol} that allows attaching a tag that helps
 * manipulations with the symbol (e.g., an external identifier of the value that
 * the symbol is related to). Tags must be of an (effectively) immutable type.
 *
 * <p>
 * Note that tags does not and must not affect comparing the instances nor tell
 * anything firm about their type information. They serve solely for making the
 * manipulation easier within the intended scope of use. If more semantics is
 * needed, consider using an {@link ExternalSymbol}.
 *
 * <p>
 * There is usually no need to inherit from this class, but this option is left
 * open intentionally when an implementation wishes to provide some additional
 * features or attributes, or just alter the debugging support.
 *
 * @param <T>
 *            the type of the tag
 * @param <V>
 *            the type of resulting values
 */
public class TaggedSymbol<T, V> extends InternalSymbol<V> {

    /** Tag of this instance. */
    private final T tag;

    /**
     * Create a new instance.
     *
     * @param instanceTag
     *            the tag of this instance. It must not be {@code null}.
     * @param adaptation
     *            the adaptation implementation. It must not be {@code null}.
     */
    public TaggedSymbol(T instanceTag, AdaptationStrategy<V> adaptation) {
        super(adaptation);
        tag = Objects.requireNonNull(instanceTag);
    }

    /**
     * Creates a new instance using {@link Downcasting#to(Class)}.
     *
     * @param instanceTag
     *            the tag of the new instance. It must not be {@code null}.
     * @param type
     *            the desired type of resulting values. It must not be
     *            {@code null}.
     */
    public TaggedSymbol(T instanceTag, Class<V> type) {
        this(instanceTag, Downcasting.to(type));
    }

    /**
     * The default implementation returns the information consisting of the
     * {@link #tag()} as a {@link String} and the instance identity hash code;
     * the presence of the hash code should be helpful for debugging to ensure
     * visible difference between two distinct instances, even when they have
     * the same tag.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("symbol[id=%8x, tag=%s]", System.identityHashCode(this), tag);
    }

    /**
     * @return the tag of this instance
     */
    public final T tag() {
        return tag;
    }
}
