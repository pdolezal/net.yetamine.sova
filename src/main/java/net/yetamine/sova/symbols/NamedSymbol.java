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

import net.yetamine.sova.AdaptationProvider;
import net.yetamine.sova.Downcasting;

/**
 * An implementation of {@link AnnotatedSymbol} that uses a {@link String}. The
 * annotation is intended to provide a human-friendly name of a symbol instance
 * for debugging purposes.
 *
 * @param <T>
 *            the type of resulting values
 */
public final class NamedSymbol<T> extends InternalSymbol<T> implements AnnotatedSymbol<String, T> {

    /** Annotation of this instance. */
    private final String annotation;

    /**
     * Create a new instance.
     *
     * @param name
     *            the name of this instance that shall be available as the
     *            annotation. It must not be {@code null}.
     * @param provider
     *            the adaptation provider. It must not be {@code null}.
     */
    public NamedSymbol(String name, AdaptationProvider<T> provider) {
        super(provider);
        annotation = name;
    }

    /**
     * Creates a new instance using {@link Downcasting#to(Class)}.
     *
     * @param name
     *            the name of this instance that shall be available as the
     *            annotation. It must not be {@code null}.
     * @param type
     *            the desired type of resulting values. It must not be
     *            {@code null}.
     */
    public NamedSymbol(String name, Class<T> type) {
        this(name, Downcasting.to(type));
    }

    /**
     * @see net.yetamine.sova.symbols.AnnotatedSymbol#annotation()
     */
    public String annotation() {
        return annotation;
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
     * @see net.yetamine.sova.symbols.ExpansiveSymbol#introspect(java.util.Map)
     */
    @Override
    protected void introspect(Map<Object, Object> result) {
        super.introspect(result);
        result.put("annotation", annotation);
    }
}
