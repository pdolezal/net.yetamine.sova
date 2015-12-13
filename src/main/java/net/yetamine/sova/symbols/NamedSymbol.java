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

import java.util.Map;

import net.yetamine.sova.adaptation.AdaptationProvider;
import net.yetamine.sova.adaptation.Downcasting;

/**
 * An implementation of {@link TaggedSymbol} that declares a {@link String} tag.
 * The tag is intended to provide a human-friendly name of the particular symbol
 * instance for debugging purposes.
 *
 * @param <T>
 *            the type of resulting values
 */
public final class NamedSymbol<T> extends DelegatingSymbol<T> implements TaggedSymbol<String, T> {

    /** Tag of this instance. */
    private final String tag;

    /**
     * Create a new instance.
     *
     * @param instanceTag
     *            the tag of this instance. It must not be {@code null}.
     * @param provider
     *            the adaptation provider. It must not be {@code null}.
     */
    public NamedSymbol(String instanceTag, AdaptationProvider<T> provider) {
        super(provider);
        tag = instanceTag;
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
    public NamedSymbol(String instanceTag, Class<T> type) {
        this(instanceTag, Downcasting.to(type));
    }

    /**
     * @see net.yetamine.sova.symbols.TaggedSymbol#tag()
     */
    public String tag() {
        return tag;
    }

    /**
     * Makes a qualified name according to a common pattern.
     *
     * @param qualifier
     *            the qualifying space for the local part of the name. It must
     *            not be {@code null} and it should consist only of characters
     *            that may occur in internet domain names.
     * @param identifier
     *            the local part of the name. It must not be {@code null}.
     *
     * @return a qualified name consisting of both parts
     */
    public static String name(String qualifier, String identifier) {
        final StringBuilder result = new StringBuilder(qualifier.length() + identifier.length() + 2);
        result.append('{').append(qualifier).append('}').append(identifier);
        return result.toString();
    }

    /**
     * Makes a qualified name according to a common pattern.
     *
     * @param qualifier
     *            the qualifying space for the local part of the name. It must
     *            not be {@code null}.
     * @param identifier
     *            the local part of the name. It must not be {@code null}.
     *
     * @return a qualified name consisting of both parts
     */
    public static String name(Class<?> qualifier, String identifier) {
        return name(qualifier.getTypeName(), identifier);
    }

    /**
     * @see net.yetamine.sova.symbols.AbstractSymbol#introspect(java.util.Map)
     */
    @Override
    protected void introspect(Map<String, Object> result) {
        super.introspect(result);
        result.put("id", String.format("hash:%08x", System.identityHashCode(this)));
        result.put("tag", tag);
    }
}
