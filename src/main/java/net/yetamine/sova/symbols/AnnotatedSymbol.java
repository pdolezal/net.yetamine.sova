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

/**
 * An extension of the {@link Symbol} interface that can equipped with an
 * annotation that helps manipulations with the symbol for the purpose of
 * presentation, logging, debugging etc.
 *
 * <p>
 * An annotations must be of an (effectively) immutable type and must not affect
 * equality definition of the instances as it does not define any semantics of a
 * particular symbol.
 *
 * @param <A>
 *            the type of the annotation
 * @param <V>
 *            the type of resulting values
 */
public interface AnnotatedSymbol<A, V> extends Symbol<V> {

    /**
     * Returns the annotation of this instance.
     *
     * @return the annotation of this instance
     */
    A annotation();
}
